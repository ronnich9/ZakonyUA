package com.feriramara.zakony.filters

import android.animation.ValueAnimator
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.*
import com.feriramara.zakony.utils.blendColors
import com.feriramara.zakony.model.Policy
import com.feriramara.zakony.utils.bindColor

class FiltersViewHandler(
    private val policiesList: List<Policy>, private val filterRecyclerView: RecyclerView,
    private val bottomBarCardView: CardView
) {

    private val context = filterRecyclerView.context

    private val bottomBarColor: Int by bindColor(context, R.color.bottom_bar_color)
    private val bottomBarPinkColor: Int by bindColor(context, R.color.colorAccent)

    private val toggleAnimDuration =
        context.resources.getInteger(R.integer.toggleAnimDuration).toLong()


    var hasActiveFilters = false
        private set

    var selectedItem: Int = 0

    fun setAdapters(set: Boolean) {
        if (set) {
            val adapter = FiltersPagerAdapter(context!!, ::onFilterSelected)
            adapter.updatePoliciesList(policiesList)
            filterRecyclerView.adapter = adapter
        } else {
            filterRecyclerView.adapter = null
            hasActiveFilters = false
        }
    }

    private fun onFilterSelected(hasActiveFilters: Boolean, selectedItem: Int) {

        val bottomBarAnimator =
            if (hasActiveFilters) ValueAnimator.ofFloat(0f, 1f)
            else if (!hasActiveFilters && this.hasActiveFilters) ValueAnimator.ofFloat(1f, 0f)
            else null



        bottomBarAnimator?.let {

            this.selectedItem = selectedItem
            this.hasActiveFilters = !this.hasActiveFilters

            it.addUpdateListener { animation ->
                val color = blendColors(
                    bottomBarColor,
                    bottomBarPinkColor,
                    animation.animatedValue as Float
                )
                bottomBarCardView.setCardBackgroundColor(color)
            }
            it.duration = toggleAnimDuration
            it.start()
        }

    }
}

