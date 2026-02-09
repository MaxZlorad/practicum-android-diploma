package ru.practicum.android.diploma.ui.filter

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.ItemIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustryViewHolder(private val binding: ItemIndustryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(industry: Industry, isSelected: Boolean) {
        binding.industryName.text = industry.name
        val drawableRes = if (isSelected) {
            R.drawable.ic_radiobutton_check_24
        } else {
            R.drawable.ic_radiobutton_24
        }
        binding.radioBtn.setImageResource(drawableRes)
    }
}
