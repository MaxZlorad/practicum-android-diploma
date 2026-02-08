package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FilterViewModel by viewModel()

    private lateinit var salaryInputHandler: SalaryInputHandler


    private var isNoSalaryChecked: Boolean = false
        set(value) {
            field = value
            binding.checkboxSalary.setImageResource(
                if (value) R.drawable.ic_checkbox_on_24 else R.drawable.ic_checkbox_off_24
            )
            updateButtonsVisibility()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        salaryInputHandler = SalaryInputHandler(binding, requireContext(), viewModel)

        setupToolbar()
        setupIndustrySelection()
        setupSalaryInput()
        setupCheckbox()
        setupButtons()
        setupClickOutsideListener()

        setupFragmentResultListener()

        observeViewModel()
        loadSavedFilters()

        updateClearButtonVisibility()
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(IndustryFragment.REQUEST_KEY_INDUSTRY) { requestKey, bundle ->
            if (requestKey == IndustryFragment.REQUEST_KEY_INDUSTRY) {
                val industryId = bundle.getInt(IndustryFragment.KEY_INDUSTRY_ID, -1)
                val industryName = bundle.getString(IndustryFragment.KEY_INDUSTRY_NAME)

                if (industryId != -1 && !industryName.isNullOrEmpty()) {
                    viewModel.updateIndustry(industryId, industryName)
                } else {
                    viewModel.updateIndustry(null, null)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.filterState.observe(viewLifecycleOwner) { filterState ->
            filterState.salaryFrom?.let { salary ->
                binding.enterAmount.setText(salary.toString())
            }

            isNoSalaryChecked = filterState.onlyWithSalary

            updateButtonsVisibility()
        }

        viewModel.industryName.observe(viewLifecycleOwner) { industryName ->
            updateIndustryUI(industryName)
        }
    }

    private fun loadSavedFilters() {
        val currentState = viewModel.filterState.value ?: return

        currentState.salaryFrom?.let { salary ->
            binding.enterAmount.setText(salary.toString())
        }

        isNoSalaryChecked = currentState.onlyWithSalary

        currentState.industryId?.let { industryId ->
            viewModel.industryName.value?.let { name ->
                binding.industrySelected.text = name
                binding.industrySelected.visibility = View.VISIBLE
                binding.industryHintUnselected.visibility = View.GONE
                binding.industryHintSelected.visibility = View.VISIBLE
            }
        }
    }

    private fun updateIndustryUI(industryName: String?) {
        if (industryName != null) {
            binding.industrySelected.text = industryName
            binding.industrySelected.visibility = View.VISIBLE
            binding.industryHintUnselected.visibility = View.GONE
            binding.industryHintSelected.visibility = View.VISIBLE
        } else {
            binding.industrySelected.visibility = View.GONE
            binding.industryHintUnselected.visibility = View.VISIBLE
            binding.industryHintSelected.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        with(binding.includeToolbar) {
            btnBack.visibility = View.VISIBLE
            toolbar.titleMarginStart = resources.getDimensionPixelSize(R.dimen.indent_56)
            toolbar.title = getString(R.string.filter_settings)
            btnBack.setOnClickListener { findNavController().popBackStack() }
        }

        binding.buttonApply.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupIndustrySelection() {
        binding.btnSelectIndustry.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_industryFragment)
        }
    }

    private fun setupSalaryInput() {
        salaryInputHandler.setupSalaryInput()
    }

    private fun setupClickOutsideListener() {
        binding.root.setOnClickListener {
            requireActivity().currentFocus?.let { focusedView ->
                if (focusedView.id == R.id.enter_amount) {
                    salaryInputHandler.updateSalaryLabelColor(hasFocus = false)
                    handleSalaryInput()
                }
                focusedView.clearFocus()
                hideKeyboard(focusedView)
            }
        }
    }

    private fun updateClearButtonVisibility() {
        val isTextNotEmpty = binding.enterAmount.text?.toString()?.isNotEmpty() == true
        binding.buttonSalaryClear.visibility = if (isTextNotEmpty) View.VISIBLE else View.GONE
    }

    private fun setupCheckbox() {
        viewModel.filterState.value?.onlyWithSalary?.let { savedValue ->
            isNoSalaryChecked = savedValue
        }

        binding.checkboxSalary.setOnClickListener {
            isNoSalaryChecked = !isNoSalaryChecked
            viewModel.updateOnlyWithSalary(isNoSalaryChecked)
        }

        binding.doNotShowWithoutSalary.setOnClickListener {
            binding.checkboxSalary.performClick()
        }
    }

    private fun setupButtons() {
        binding.buttonApply.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("filters_applied", true)

            findNavController().popBackStack()
        }

        binding.buttonCancel.setOnClickListener {
            resetAllFilters()
        }
    }

    private fun resetAllFilters() {
        viewModel.resetFilters()
        binding.enterAmount.text?.clear()
        isNoSalaryChecked = false
        salaryInputHandler.updateSalaryLabelColor(hasFocus = false)

        updateIndustryUI(null)

        val result = Bundle().apply {
            putInt(IndustryFragment.KEY_INDUSTRY_ID, -1)
            putString(IndustryFragment.KEY_INDUSTRY_NAME, null)
        }
        parentFragmentManager.setFragmentResult(IndustryFragment.REQUEST_KEY_INDUSTRY, result)
    }

    private fun updateButtonsVisibility() {
        val currentState = viewModel.filterState.value ?: FilterState()
        val hasFilters = currentState.salaryFrom != null ||
            currentState.onlyWithSalary ||
            currentState.industryId != null
        binding.buttonApply.visibility = if (hasFilters) View.VISIBLE else View.GONE
        binding.buttonCancel.visibility = if (hasFilters) View.VISIBLE else View.GONE
    }

    private fun handleSalaryInput() {
        val salaryText = binding.enterAmount.text?.toString()?.trim()
        val salary = if (!salaryText.isNullOrEmpty()) {
            salaryText.toIntOrNull()
        } else {
            null
        }
        viewModel.updateSalary(salary)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
