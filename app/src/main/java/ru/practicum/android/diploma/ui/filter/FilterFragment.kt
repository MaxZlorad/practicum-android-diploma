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
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private var salaryText: String = ""
    private var isNoSalaryChecked: Boolean = false

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
        updateButtonsVisibility()
        setupClickOutsideListener()

        val isTextNotEmpty = binding.enterAmount.text?.toString()?.isNotEmpty() == true
        val initialColorRes = if (isTextNotEmpty) R.color.black else R.color.gray
        binding.salaryExpected.setTextColor(ContextCompat.getColor(requireContext(), initialColorRes))
    }

    private fun setupToolbar() {
        binding.includeToolbar.btnBack.visibility = View.VISIBLE
        binding.includeToolbar.toolbar.titleMarginStart =
            resources.getDimensionPixelSize(R.dimen.indent_56)

        binding.includeToolbar.toolbar.title = getString(R.string.filter_settings)

        binding.buttonApply.setOnClickListener {
            findNavController().navigate(R.id.action_filter_to_search)
        }

        binding.includeToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupIndustrySelection() {
        binding.btnSelectIndustry.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_industryFragment)
        }
    }

    private fun setupSalaryInput() {
        binding.enterAmount.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        fun updateSalaryLabelColor() {
            val isTextNotEmpty = binding.enterAmount.text?.toString()?.isNotEmpty() == true
            val hasFocus = binding.enterAmount.hasFocus()
            val colorRes = when {
                hasFocus -> R.color.blue
                isTextNotEmpty -> R.color.black
                else -> R.color.gray
            }

            binding.salaryExpected.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
        }

        binding.enterAmount.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.enterAmount.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.enterAmount.windowToken, 0)
                updateSalaryLabelColor()
                true
            } else {
                false
            }
        }

        binding.enterAmount.setOnFocusChangeListener { _, _ ->
            updateSalaryLabelColor()
        }

        binding.enterAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                salaryText = s?.toString() ?: ""

                updateSalaryLabelColor()
                updateClearButtonVisibility()
                updateButtonsVisibility()
            }

            override fun afterTextChanged(s: Editable?) {
                val filtered = s?.toString()?.filter { it.isDigit() }
                if (filtered != s?.toString()) {
                    binding.enterAmount.setText(filtered)
                    binding.enterAmount.setSelection(filtered?.length ?: 0)
                }
            }
        })

        binding.buttonSalaryClear.setOnClickListener {
            binding.enterAmount.text?.clear()
            salaryText = ""

            updateSalaryLabelColor()
            updateClearButtonVisibility()
            updateButtonsVisibility()
        }
    }

    private fun setupClickOutsideListener() {
        binding.root.setOnClickListener {
            val focusedView = requireActivity().currentFocus

            if (focusedView != null) {
                if (focusedView.id == R.id.enter_amount) {
                    val isTextNotEmpty = binding.enterAmount.text?.toString()?.isNotEmpty() == true
                    val colorRes = if (isTextNotEmpty) R.color.black else R.color.gray
                    binding.salaryExpected.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
                }

                focusedView.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }
    }

    private fun updateClearButtonVisibility() {
        binding.buttonSalaryClear.visibility =
            if (salaryText.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupCheckbox() {
        binding.checkboxSalary.setImageResource(R.drawable.ic_checkbox_off_24)

        binding.checkboxSalary.setOnClickListener {
            isNoSalaryChecked = !isNoSalaryChecked
            val drawableRes = if (isNoSalaryChecked) {
                R.drawable.ic_checkbox_on_24
            } else {
                R.drawable.ic_checkbox_off_24
            }
            binding.checkboxSalary.setImageResource(drawableRes)
            updateButtonsVisibility()
        }

        binding.doNotShowWithoutSalary.setOnClickListener {
            binding.checkboxSalary.performClick()
        }
    }

    private fun setupButtons() {
        binding.buttonApply.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonCancel.setOnClickListener {
            resetAllFilters()
        }
    }

    private fun resetAllFilters() {
        binding.enterAmount.text?.clear()
        salaryText = ""

        binding.salaryExpected.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))

        isNoSalaryChecked = false
        binding.checkboxSalary.setImageResource(R.drawable.ic_checkbox_off_24)

        updateClearButtonVisibility()
        updateButtonsVisibility()
    }

    private fun updateButtonsVisibility() {
        val hasFilters = salaryText.isNotEmpty() || isNoSalaryChecked

        if (hasFilters) {
            binding.buttonApply.visibility = View.VISIBLE
            binding.buttonCancel.visibility = View.VISIBLE
        } else {
            binding.buttonApply.visibility = View.GONE
            binding.buttonCancel.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
