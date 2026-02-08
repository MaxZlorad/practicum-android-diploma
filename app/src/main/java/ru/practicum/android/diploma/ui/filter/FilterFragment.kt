package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
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

    private var salaryText: String = ""
        set(value) {
            field = value
            updateClearButtonVisibility()
            updateButtonsVisibility()
        }

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

        setupToolbar()
        setupIndustrySelection()
        setupSalaryInput()
        setupCheckbox()
        setupButtons()
        setupClickOutsideListener()

        setupFragmentResultListener()

        observeViewModel()
        loadSavedFilters()

        updateSalaryLabelColor(hasFocus = false)
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
                salaryText = salary.toString()
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
            salaryText = salary.toString()
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
        binding.enterAmount.apply {
            viewModel.filterState.value?.salaryFrom?.let { salary ->
                setText(salary.toString())
                salaryText = salary.toString()
            }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocusAndHideKeyboard()
                    updateSalaryLabelColor(hasFocus = false)
                    val salaryText = text?.toString()?.trim()
                    val salary = if (!salaryText.isNullOrEmpty()) {
                        salaryText.toIntOrNull()
                    } else {
                        null
                    }
                    viewModel.updateSalary(salary)

                    true
                } else {
                    false
                }
            }

            setOnFocusChangeListener { _, hasFocus ->
                updateSalaryLabelColor(hasFocus)
                if (!hasFocus) {
                    val salaryText = text?.toString()?.trim()
                    val salary = if (!salaryText.isNullOrEmpty()) {
                        salaryText.toIntOrNull()
                    } else {
                        null
                    }
                    viewModel.updateSalary(salary)
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    salaryText = s?.toString() ?: ""
                    updateSalaryLabelColor(hasFocus())
                }

                override fun afterTextChanged(s: Editable?) = Unit
            })
        }

        binding.buttonSalaryClear.setOnClickListener {
            binding.enterAmount.text?.clear()
            salaryText = ""
            updateSalaryLabelColor(hasFocus = false)
            viewModel.updateSalary(null)
        }
    }

    private fun setupClickOutsideListener() {
        binding.root.setOnClickListener {
            requireActivity().currentFocus?.let { focusedView ->
                if (focusedView.id == R.id.enter_amount) {
                    updateSalaryLabelColor(hasFocus = false)
                    val salaryText = binding.enterAmount.text?.toString()?.trim()
                    val salary = if (!salaryText.isNullOrEmpty()) {
                        salaryText.toIntOrNull()
                    } else {
                        null
                    }
                    viewModel.updateSalary(salary)
                }
                focusedView.clearFocus()
                hideKeyboard(focusedView)
            }
        }
    }

    private fun updateSalaryLabelColor(hasFocus: Boolean) {
        val isTextNotEmpty = binding.enterAmount.text?.toString()?.isNotEmpty() == true
        val colorRes = when {
            hasFocus -> R.color.blue
            isTextNotEmpty -> R.color.black
            else -> R.color.gray
        }
        binding.salaryExpected.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun updateClearButtonVisibility() {
        binding.buttonSalaryClear.visibility =
            if (salaryText.isNotEmpty()) View.VISIBLE else View.GONE
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
        salaryText = ""
        isNoSalaryChecked = false
        updateSalaryLabelColor(hasFocus = false)

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

    private fun clearFocusAndHideKeyboard() {
        binding.enterAmount.clearFocus()
        hideKeyboard(binding.enterAmount)
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
