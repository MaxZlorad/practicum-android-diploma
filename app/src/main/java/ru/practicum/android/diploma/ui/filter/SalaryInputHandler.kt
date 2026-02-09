package ru.practicum.android.diploma.ui.filter

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding

class SalaryInputHandler(
    private val binding: FragmentFilterBinding,
    private val context: Context,
    private val viewModel: FilterViewModel
) {

    private var salaryText: String = ""
        set(value) {
            field = value
            updateClearButtonVisibility()
        }

    fun setupSalaryInput() {
        setupEditTextListeners()
        setupClearButton()
    }

    private fun setupEditTextListeners() {
        binding.enterAmount.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleSalaryInputDone()
                    true
                } else {
                    false
                }
            }

            setOnFocusChangeListener { _, hasFocus ->
                updateSalaryLabelColor(hasFocus)
                if (!hasFocus) {
                    handleSalaryInput()
                }
            }

            addTextChangedListener(createTextWatcher())
        }
    }

    private fun setupClearButton() {
        binding.buttonSalaryClear.setOnClickListener {
            binding.enterAmount.text?.clear()
            salaryText = ""
            updateSalaryLabelColor(hasFocus = false)
            viewModel.updateSalary(null)
        }
    }

    private fun createTextWatcher() = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            salaryText = s?.toString() ?: ""
            updateSalaryLabelColor(binding.enterAmount.hasFocus())
        }

        override fun afterTextChanged(s: android.text.Editable?) = Unit
    }

    private fun handleSalaryInputDone() {
        clearFocusAndHideKeyboard()
        updateSalaryLabelColor(hasFocus = false)
        handleSalaryInput()
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

    fun updateSalaryLabelColor(hasFocus: Boolean) {
        val isTextNotEmpty = binding.enterAmount.text?.toString()?.isNotEmpty() == true
        val colorRes = when {
            hasFocus -> R.color.blue
            isTextNotEmpty -> R.color.black
            else -> R.color.gray
        }
        binding.salaryExpected.setTextColor(ContextCompat.getColor(context, colorRes))
    }

    private fun updateClearButtonVisibility() {
        binding.buttonSalaryClear.visibility =
            if (salaryText.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun clearFocusAndHideKeyboard() {
        binding.enterAmount.clearFocus()
        hideKeyboard(binding.enterAmount)
    }

    private fun hideKeyboard(view: android.view.View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
