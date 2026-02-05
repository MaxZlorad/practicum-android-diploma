package ru.practicum.android.diploma.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustryFragment : Fragment() {

    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IndustryViewModel by viewModel()

    val selectedId: Int? = null
    var industries: List<Industry> = emptyList()

    val adapter by lazy {
        IndustryAdapter(industries, selectedId)
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
        binding.includeToolbar.btnBack.visibility = View.VISIBLE
        binding.includeToolbar.toolbar.titleMarginStart =
            resources.getDimensionPixelSize(R.dimen.indent_56)

        binding.includeToolbar.toolbar.title = getString(R.string.title_industry)
        binding.includeToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.loadIndustries()
        observeState()
    }

    private fun observeState() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustryState.Empty -> {
                    binding.rvIndustries.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.imgError.isVisible = false
                    binding.tvError.isVisible = false
                    binding.btnApply.isVisible = false
                }

                is IndustryState.Loading -> {
                    binding.rvIndustries.isVisible = false
                    binding.progressBar.isVisible = true
                    binding.imgError.isVisible = false
                    binding.tvError.isVisible = false
                    binding.btnApply.isVisible = false
                }

                is IndustryState.Content -> {
                    binding.rvIndustries.isVisible = true
                    binding.progressBar.isVisible = false
                    binding.imgError.isVisible = false
                    binding.tvError.isVisible = false
                    binding.btnApply.isVisible = false
                    industries = state.industries
                    adapter.updateIndustries(industries, selectedId)
                    binding.rvIndustries.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvIndustries.adapter = adapter
                }

                is IndustryState.Error -> {
                    binding.rvIndustries.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.imgError.isVisible = true
                    binding.tvError.isVisible = true
                    binding.btnApply.isVisible = false

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
