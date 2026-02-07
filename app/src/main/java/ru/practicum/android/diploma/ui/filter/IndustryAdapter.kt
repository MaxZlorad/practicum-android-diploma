package ru.practicum.android.diploma.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustryAdapter(
    private val onIndustryClick: (Industry) -> Unit
) : RecyclerView.Adapter<IndustryViewHolder>() {

    private var industries: List<Industry> = emptyList()
    private var selectedIndustryId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IndustryViewHolder(binding)
    }

    override fun getItemCount(): Int = industries.size

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = industries[position]
        val isSelected = industry.id == selectedIndustryId

        holder.bind(industry, isSelected)

        holder.itemView.setOnClickListener {
            onIndustryClick(industry)
        }
    }

    fun submitList(
        newIndustries: List<Industry>,
        selectedId: Int?
    ) {
        industries = newIndustries
        selectedIndustryId = selectedId
        notifyDataSetChanged()
    }
}
