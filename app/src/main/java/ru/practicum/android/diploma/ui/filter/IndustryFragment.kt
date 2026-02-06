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
    private lateinit var searchDebounce: (String) -> Unit

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
        setupAdapters()
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

    private fun setupAdapters() {
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
        binding.rvIndustries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIndustries.adapter = adapter
        searchDebounce = debounce(
            delayMillis = DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { query ->
            viewModel.search(query)
        }
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            searchDebounce(text?.toString().orEmpty())
        }
        binding.btnApply.setOnClickListener {
            viewModel.onApplyClicked()
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
        private const val DEBOUNCE_DELAY = 500L
    }
}
