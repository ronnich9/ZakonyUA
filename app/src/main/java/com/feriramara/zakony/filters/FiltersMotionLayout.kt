package com.feriramara.zakony.filters

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.R
import com.feriramara.zakony.utils.bindView
import com.feriramara.zakony.main.MainActivity
import com.feriramara.zakony.model.Policy
import kotlinx.coroutines.launch
import java.lang.Exception

class FiltersMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MultiListenerMotionLayout(context, attrs, defStyleAttr) {



    private val closeIcon: ImageView by bindView(R.id.close_icon)
    private val filterIcon: ImageView by bindView(R.id.filter_icon)
    private val filtersRecyclerView: RecyclerView by bindView(R.id.recycler_view_filtered)
    private val bottomBarCardView: CardView by bindView(R.id.bottom_bar_card_view)
    private var policiesList: List<Policy> = emptyList()

    private lateinit var filtersHandler: FiltersViewHandler

    fun setFilters() {
        filtersHandler = FiltersViewHandler(policiesList, filtersRecyclerView, bottomBarCardView)
    }



    init {
        inflate(context, R.layout.layout_filter_motion, this)
        setFilters()
        enableClicks()
        Log.e("init: ",  policiesList.size.toString())

    }

    /**
     * Opens the filter sheet. Set adapters before starting.
     *
     * Order of animations:
     * set1_base -> set2_path -> set3_reveal -> set4_settle
     */
    private fun openSheet(): Unit = performAnimation {

        setFilters()
        // Set adapters before opening filter sheet
        filtersHandler.setAdapters(true)

        // Set the start transition. This is necessary because the
        // un-filtering animation ends with set10 and we need to
        // reset it here when opening the sheet the next time
        setTransition(
            R.id.set1_base,
            R.id.set2_path
        )

        // 1) set1_base -> set2_path
        transitionToState(R.id.set2_path)

        try {
            startScaleDownAnimator(true)
        } catch (e: Exception) {

        }

        awaitTransitionComplete(R.id.set2_path)

        // 2) set2_path -> set3_reveal
        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)

        // 3) set3_reveal -> set4_settle
        transitionToState(R.id.set4_settle)
        awaitTransitionComplete(R.id.set4_settle)

    }



    /**
     * Closes the filter sheet. Remove adapters after it's complete, useless to
     * keep them around unless opened again.
     * Instead of creating more transitions, we reverse the transitions by setting
     * the required transition at progress=1f (end state) and using [transitionToStart].
     *
     * Order of animations:
     * set4_settle -> set3_reveal -> set2_path -> set1_base
     */
    private fun closeSheet(): Unit = performAnimation {

        // 1) set4_settle -> set3_reveal
        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)

        // 2) set3_reveal -> set2_path
        setTransition(
            R.id.set2_path,
            R.id.set3_reveal
        )
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)

        // 3) set2_path -> set1_base
        setTransition(
            R.id.set1_base,
            R.id.set2_path
        )
        progress = 1f
        transitionToStart()
        try {
            startScaleDownAnimator(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        awaitTransitionComplete(R.id.set1_base)

        // Remove adapters after closing filter sheet
        filtersHandler.setAdapters(false)
    }

    /**
     * Performs the filter animation. We don't use [transitionToState] or
     * [transitionToStart] here for multiple transitions because we use
     * autoTransition=animateToEnd in the MotionScene which automatically
     * transitions from state to state.
     *
     * Order of animations:
     * set4_settle -> set5_filterCollapse -> set6_filterLoading -> set7_filterBase
     */
    private fun onFilterApplied(): Unit = performAnimation {
        if (!filtersHandler.hasActiveFilters) return@performAnimation

        // 1) set4_settle -> set5_filterCollapse
        transitionToState(R.id.set5_filterCollapse)
        awaitTransitionComplete(R.id.set5_filterCollapse)

        // 2) set5_filterCollapse -> set6_filterLoading
        awaitTransitionComplete(R.id.set6_filterLoading)

        // 3) set6_filterLoading -> set7_filterBase
        // (Start scale 'up' animation simultaneously)
        startScaleDownAnimator(false)
        (context as MainActivity).updateAdapter(filtersHandler.selectedItem, getPolicyName(filtersHandler.selectedItem))
        awaitTransitionComplete(R.id.set7_filterBase)

        // Remove adapters
        filtersHandler.setAdapters(false)
    }

    /**
     * Removes filters in adapter while animating.
     *
     * Order of animations:
     * set7_filterBase -> set8_unfilterInset -> set9_unfilterLoading -> set10_unfilterOutset
     */
    private fun unFilterAdapterItems(): Unit = performAnimation {

        // 1) set7_filterBase -> set8_unfilterInset
        transitionToState(R.id.set8_unfilterInset)
        awaitTransitionComplete(R.id.set8_unfilterInset)

        // 2) set8_unfilterInset -> set9_unfilterLoading
        awaitTransitionComplete(R.id.set9_unfilterLoading)

        // 3) set9_unfilterLoading -> set10_unfilterOutset
        // (Start scale 'up' animation simultaneously)
        startScaleDownAnimator(false)
        (context as MainActivity).updateAdapter(-1, "Всі голосування")
        awaitTransitionComplete(R.id.set10_unfilterOutset)
    }

    fun getPolicyName(policyId: Int) : String {
        var str: String = ""
        for (i in policiesList) {
            if (i.id.toInt() == policyId) {
                str = i.name
            }
        }
        Log.e("Policy name: ", str)
        return str
    }

    /**
     * Based on the currentState (ConstraintSet), we set the appropriate click listeners.
     * Do not call this method during an animation.
     */
    private fun enableClicks() = when (currentState) {
        R.id.set1_base, R.id.set10_unfilterOutset -> {
            filterIcon.setOnClickListener { openSheet() }
            closeIcon.setOnClickListener(null)
        }
        R.id.set4_settle -> {
            filterIcon.setOnClickListener { onFilterApplied() }
            closeIcon.setOnClickListener { closeSheet() }
        }
        R.id.set7_filterBase -> {
            closeIcon.setOnClickListener { unFilterAdapterItems() }
            filterIcon.setOnClickListener(null)
        }
        else -> throw IllegalStateException("Can be called only for the permitted 3 currentStates")
    }

    /**
     * Called when an animation is started so that double clicking or
     * clicking during animation will not trigger anything
     */
    private fun disableClicks() {
        filterIcon.setOnClickListener(null)
        closeIcon.setOnClickListener(null)
    }

    /**
     * Convenience method to launch a coroutine in MainActivity's lifecycleScope
     * (to start animating transitions in MotionLayout) and to handle clicks appropriately.
     *
     * Note: [block] must contain only animation related code. Clicks are
     * disabled at start and enabled at the end.
     *
     * Warning: [awaitTransitionComplete] must be called for the final state at the end of
     * [block], otherwise [enableClicks] will be called at the wrong time for the wrong state.
     */
    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
            disableClicks()
            block()
            enableClicks()
        }
    }


    fun loadData(policies: List<Policy>) {
        policiesList = policies
    }


    /**
     * Convenience method to start ScaleDown animation in [MainListAdapter].
     * The duration of the scale down animation will match that of the current transition.
     */
    private fun startScaleDownAnimator(isScaledDown: Boolean): Unit =
        (context as MainActivity)
            .getAdapterScaleDownAnimator(isScaledDown)
            .apply { duration = transitionTimeMs }
            .start()
}