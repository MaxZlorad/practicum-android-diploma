package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.util.debounce

class IndustryFragment : Fragment() {
    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IndustryViewModel by viewModel()
    private val adapter by lazy {
        IndustryAdapter { industry ->
            viewModel.onIndustrySelected(industry.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupButtons()
        observeViewModel()

        viewModel.loadIndustries()
    }

    private fun observeViewModel() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryState.Loading -> showLoading()
                is IndustryState.Empty -> showEmpty()
                is IndustryState.Error -> showError()
                is IndustryState.Content -> showContent(state.industries)
            }
        }

        viewModel.isButtonEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.btnApply.isEnabled = enabled
            binding.btnApply.isVisible = enabled
        }
    }

    private fun setupToolbar() {
        binding.includeToolbar.btnBack.isVisible = true
        binding.includeToolbar.toolbar.title = getString(R.string.title_industry)
        binding.includeToolbar.toolbar.titleMarginStart =
            resources.getDimensionPixelSize(R.dimen.indent_56)

        binding.includeToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.rvIndustries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIndustries.adapter = adapter
    }

    private fun setupSearch() {
        setupSearchIme()
        setupSearchTextChange()
        setupSearchClear()
    }

    private fun setupSearchIme() {
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchInput.text?.toString().orEmpty()
                viewModel.search(query)
                binding.searchInput.clearFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupSearchTextChange() {
        val searchDebounce = debounce(
            delayMillis = DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { query: String ->
            viewModel.search(query)
        }

        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString().orEmpty()
            updateSearchIcon(query)
            searchDebounce(query)
        }
    }

    private fun setupSearchClear() {
        binding.drawableEnd.setOnClickListener {
            if (!binding.searchInput.text.isNullOrEmpty()) {
                clearSearch()
            }
        }
    }

    private fun updateSearchIcon(query: String) {
        binding.drawableEnd.setImageResource(
            if (query.isNotEmpty()) {
                R.drawable.ic_clear
            } else {
                R.drawable.ic_search_24
            }
        )
    }

    private fun setupButtons() {
        binding.btnApply.setOnClickListener {
            viewModel.onApplyClicked()
            val selectedIndustryName = viewModel.getSelectedIndustryName()
            val selectedIndustryId = viewModel.getSelectedIndustryId()
            val result = Bundle().apply {
                putInt(KEY_INDUSTRY_ID, selectedIndustryId ?: -1)
                putString(KEY_INDUSTRY_NAME, selectedIndustryName)
            }

            parentFragmentManager.setFragmentResult(REQUEST_KEY_INDUSTRY, result)
            findNavController().popBackStack()
        }
    }

    private fun showLoading() = with(binding) {
        progressBar.isVisible = true
        rvIndustries.isVisible = false
        imgError.isVisible = false
        tvError.isVisible = false
    }

    private fun showEmpty() = with(binding) {
        progressBar.isVisible = false
        rvIndustries.isVisible = false
        imgError.isVisible = false
        tvError.isVisible = false
    }

    private fun showError() = with(binding) {
        progressBar.isVisible = false
        rvIndustries.isVisible = false
        imgError.isVisible = true
        tvError.isVisible = true
    }

    private fun showContent(industries: List<Industry>) = with(binding) {
        progressBar.isVisible = false
        rvIndustries.isVisible = true
        imgError.isVisible = false
        tvError.isVisible = false
        adapter.submitList(
            newIndustries = industries,
            selectedId = viewModel.getSelectedIndustryId()
        )
    }

    private fun clearSearch() {
        binding.searchInput.setText("")
        hideKeyboard()
        viewModel.search("")
    }

    private fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEBOUNCE_DELAY = 1000L
        const val REQUEST_KEY_INDUSTRY = "industry_request_key"
        const val KEY_INDUSTRY_ID = "industry_id"
        const val KEY_INDUSTRY_NAME = "industry_name"
    }
}
