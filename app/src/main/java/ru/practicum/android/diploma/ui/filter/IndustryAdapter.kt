package ru.practicum.android.diploma.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustryAdapter(private var industries: List<Industry>, private var selectedIndustryId: String? = null) :
    RecyclerView.Adapter<IndustryViewHolder>() {
    var onIndustriesClickListener: ((Industry) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IndustryViewHolder(binding)
    }

    override fun getItemCount(): Int = industries.size

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val industry = industries[position]
        val isSelected = industry.id == selectedIndustryId
        holder.bind(industry, isSelected)
        holder.itemView.setOnClickListener {
            val previousSelectedId = selectedIndustryId
            selectedIndustryId = industry.id
            if (previousSelectedId != null) {
                val previousPosition = industries.indexOfFirst { it.id == previousSelectedId }
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }
            }
            notifyItemChanged(position)
            onIndustriesClickListener?.invoke(industries[position])
        }
    }

    fun updateIndustries(newIndustries: List<Industry>, selectedId: String?) {
        industries = newIndustries
        selectedIndustryId = selectedId
        notifyDataSetChanged()
    }
}
