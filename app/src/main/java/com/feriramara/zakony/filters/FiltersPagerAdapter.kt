package com.feriramara.zakony.filters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.R
import com.feriramara.zakony.utils.bindColor
import com.feriramara.zakony.utils.bindView
import com.feriramara.zakony.model.Policy
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FiltersPagerAdapter(context: Context, private val listener: (hasActiveFilters: Boolean, selectedItem: Int) -> Unit) :
    RecyclerView.Adapter<FiltersPagerAdapter.FiltersViewHolder>() {

    private var policiesList: List<Policy> = emptyList()

    private val opContext = context

    private val unselectedColor: Int by bindColor(context, R.color.filter_pill_color)
    private val selectedColor: Int by bindColor(context, R.color.filter_pill_selected_color)


    private var checkedPosition = -1

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var hasActiveFilters = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        return FiltersViewHolder(inflater.inflate(R.layout.item_filter, parent, false))
    }

    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {

        val id = (policiesList[position].id).toInt()
        val name = policiesList[position].name

        val final_string = "$id. $name"
        holder.title_2.text = final_string

        if (position == checkedPosition) {
            holder.filter_card.setCardBackgroundColor(selectedColor)
        } else
            holder.filter_card.setCardBackgroundColor(unselectedColor)


        /**
         * Bind all the filter buttons (if any). Clicking the filter button toggles state
         * which is shown by a short toggle animation
         */
        holder.policy_container.setOnClickListener {

            if (checkedPosition == position) {
                checkedPosition = -1
                hasActiveFilters = false
            } else {
                checkedPosition = position
                hasActiveFilters = true
            }

            notifyDataSetChanged()

            Log.e("POsition: ", policiesList[position].id)
            listener(hasActiveFilters, policiesList[position].id.toInt())
        }

        holder.policyInfo.setOnClickListener {

            MaterialAlertDialogBuilder(opContext)
                .setMessage(policiesList[position].description)
                .setPositiveButton("Ok", null)
                .show()


        }


    }

    fun updatePoliciesList(policies: List<Policy>) {
        this.policiesList = policies
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return policiesList.size
    }

    class FiltersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val policy_container: LinearLayout by bindView(R.id.policy_container)
        val filter_card: MaterialCardView by bindView(R.id.filter_card)
        val title_2: TextView by bindView(R.id.title2)
        val policyInfo: ImageView by bindView(R.id.policy_info)
    }
}