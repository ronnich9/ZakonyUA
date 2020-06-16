package com.feriramara.zakony.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.*
import com.feriramara.zakony.detailed.VoteFragment
import com.feriramara.zakony.model.Vote
import com.feriramara.zakony.utils.blendColors
import com.feriramara.zakony.utils.dp
import com.feriramara.zakony.utils.getValueAnimator
import com.feriramara.zakony.utils.screenWidth
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.LocalDate
import java.time.Period
import kotlin.collections.ArrayList


class VoteListAdapter(val context: Context) :
    RecyclerView.Adapter<VoteListAdapter.VoteViewHolder>() {


    var voteList: List<Vote> = emptyList()


    private val originalBg: Int = androidx.core.content.ContextCompat.getColor(
        context,
        R.color.list_item_bg_collapsed
    )
    private val expandedBg: Int = androidx.core.content.ContextCompat.getColor(
        context,
        R.color.list_item_bg_expanded
    )

    private val listItemHorizontalPadding: Float =
        context.resources.getDimension(R.dimen.list_item_horizontal_padding)
    private val listItemVerticalPadding: Float =
        context.resources.getDimension(R.dimen.list_item_vertical_padding)


    private val originalWidth = context.screenWidth - 48.dp
    private val expandedWidth = context.screenWidth - 24.dp
    private var originalHeight = -1 // will be calculated dynamically
    private var expandedHeight = -1 // will be calculated dynamically

    private val listItemExpandDuration: Long = 375

    private lateinit var recyclerView: RecyclerView
    private var expandedModel: Vote? = null
    private var isScaledDown = false


    fun updateMatchList(voteList: List<Vote>) {
        this.voteList = voteList
        notifyDataSetChanged()
    }

    fun clearRecycler() {
        this.voteList = emptyList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return voteList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteViewHolder {
        return VoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_vote, parent, false)
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun changeText(text: String): String {

        val p = "(Поіменне голосування\\s+)|(про проект постанови\\s+)|(про проект Закону\\s+)".toRegex()

        val s = text.replace(p, "")

        return s.capitalize()
    }

    override fun onBindViewHolder(holder: VoteViewHolder, position: Int) {
        val model = voteList[position]


        holder.title.text = changeText(model.name)
        holder.subtitle_vote.text = if (model.aye_votes.toInt() > 225) "✔" else "❌"
        if (holder.subtitle_vote.text == "❌") {
            holder.subtitle_vote.textSize = 12f
        }
        holder.subtitle_vote.setTextColor(if (model.aye_votes.toInt() > 225) ColorTemplate.JOYFUL_COLORS[3] else ColorTemplate.VORDIPLOM_COLORS[4])


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.subtitle.text = changeDateFormat(model)
        } else {
            holder.subtitle.text = model.date
        }

        expandItem(holder, model == expandedModel, animate = false)
        scaleDownItem(holder, position, isScaledDown)


        holder.cardContainer.setOnClickListener {
            if (expandedModel == null) {
                // expand clicked view


                val anim = AlphaAnimation(1.0f, 0.0f)
                anim.duration = 200
                anim.repeatCount = 1
                anim.repeatMode = Animation.REVERSE

                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {
                        holder.title.maxLines = 5
                        holder.title.setPadding(0, 0, 0, 0)
                        holder.subtitle.text = ""
                        holder.subtitle_vote.text = ""

                    }
                })
                holder.subtitle.startAnimation(anim)
                holder.title.startAnimation(anim)
                holder.subtitle_vote.startAnimation(anim)

                setPieChart(holder, model)


                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            } else if (expandedModel == model) {

                // collapse clicked view
                val anim = AlphaAnimation(1.0f, 0.0f)
                anim.duration = 200
                anim.repeatCount = 1
                anim.repeatMode = Animation.REVERSE

                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {
                        holder.title.maxLines = 2
                        holder.title.setPadding(0, 0, 16, 0)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            holder.subtitle.text = changeDateFormat(model)
                        } else {
                            holder.subtitle.text = model.date
                        }
                        holder.subtitle_vote.text = if (model.aye_votes.toInt() > 225) "✔" else "❌"
                    }
                })
                holder.subtitle.startAnimation(anim)
                holder.title.startAnimation(anim)
                holder.subtitle_vote.startAnimation(anim)



                expandItem(holder, expand = false, animate = true)
                expandedModel = null
            } else {

                // collapse previously expanded view
                val expandedModelPosition = voteList.indexOf(expandedModel!!)
                val oldViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? VoteViewHolder
                if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeDateFormat(model: Vote): String {
        val model_date = LocalDate.parse(model.date)
        val current_date = LocalDate.now()
        val period = Period.between(model_date, current_date)
        val years = period.years
        val months = period.months
        val days = period.days

        val years_string = when (years) {
            0 -> ""
            1 -> "1 рік "
            2, 3, 4 -> "$years роки "
            else -> "$years років "
        }

        val months_string = when (months) {
            0 -> ""
            1 -> "1 місяць і "
            2, 3, 4 -> "$months місяці і "
            else -> "$months місяців і "
        }

        val days_string = when (days) {
            0 -> ""
            1, 21, 31 -> "$days день тому"
            2, 3, 4, 22, 23, 24 -> "$days дні тому"
            else -> "$days днів тому"
        }

        return years_string + months_string + days_string

    }

    private fun setPieChart(holder: VoteViewHolder, model: Vote) {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()
        listPie.add(PieEntry(model.aye_votes.toFloat(), "За"))
        listColors.add(Color.GREEN)
        listPie.add(PieEntry(model.no_votes.toFloat(), "Проти"))
        listColors.add(Color.RED)
        listPie.add(
            PieEntry(
                model.possible_turnout.toFloat() - model.aye_votes.toFloat() - model.no_votes.toFloat(),
                "Не голосували"
            )
        )
        listColors.add(Color.GRAY)

        val pieDataSet = PieDataSet(listPie, "")
        pieDataSet.colors = listColors
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val pieData = PieData(pieDataSet)
        holder.pieChart.data = pieData


        //holder.pieChart.setUsePercentValues(true)
        holder.pieChart.legend.isEnabled = false
        holder.pieChart.description.isEnabled = false
        holder.pieChart.setDrawCenterText(false)
        holder.pieChart.setEntryLabelColor(ColorTemplate.getHoloBlue())
        holder.pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun expandItem(holder: VoteViewHolder, expand: Boolean, animate: Boolean) {
        if (animate) {
            Log.e("Im here: expand = ", expand.toString())
            val animator = getValueAnimator(
                expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
            ) { progress -> setExpandProgress(holder, progress) }

            if (expand) animator.doOnStart { holder.expandView.isVisible = true }
            else animator.doOnEnd { holder.expandView.isVisible = false }

            animator.start()
        } else {

            // show expandView only if we have expandedHeight (onViewAttached)
            holder.expandView.isVisible = expand && expandedHeight >= 0
            setExpandProgress(holder, if (expand) 1f else 0f)
        }
    }

    override fun onViewAttachedToWindow(holder: VoteViewHolder) {
        super.onViewAttachedToWindow(holder)

        // get originalHeight & expandedHeight if not gotten before
        if (expandedHeight < 0) {
            expandedHeight = 0 // so that this block is only called once

            holder.cardContainer.doOnLayout { view ->
                originalHeight = view.height

                // show expandView and record expandedHeight in next layout pass
                // (doOnPreDraw) and hide it immediately. We use onPreDraw because
                // it's called after layout is done. doOnNextLayout is called during
                // layout phase which causes issues with hiding expandView.
                holder.expandView.isVisible = true
                view.doOnPreDraw {
                    expandedHeight = view.height
                    holder.expandView.isVisible = false
                }
            }
        }
    }

    private fun setExpandProgress(holder: VoteViewHolder, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            holder.cardContainer.layoutParams.height =
                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        holder.cardContainer.layoutParams.width =
            (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        holder.cardContainer.setBackgroundColor(
            blendColors(
                originalBg,
                expandedBg,
                progress
            )
        )
        holder.cardContainer.requestLayout()

        holder.chevron.rotation = 90 * progress
    }

    private inline val LinearLayoutManager.visibleItemsRange: IntRange
        get() = findFirstVisibleItemPosition()..findLastVisibleItemPosition()

    fun getScaleDownAnimator(isScaledDown: Boolean): ValueAnimator {
        val lm = recyclerView.layoutManager as LinearLayoutManager

        val animator = getValueAnimator(
            isScaledDown,
            duration = 300L, interpolator = AccelerateDecelerateInterpolator()
        )
        { progress ->

            // Get viewHolder for all visible items and animate attributes
            for (i in lm.visibleItemsRange) {
                try {
                    val holder = recyclerView.findViewHolderForLayoutPosition(i) as VoteViewHolder
                    setScaleDownProgress(holder, i, progress)
                } catch (e: Exception) {
                    Log.e("Recycler doesn't exist!", e.toString())
                }

            }
        }

        // Set adapter variable when animation starts so that newly binded views in
        // onBindViewHolder will respect the new size when they come into the screen
        animator.doOnStart { this.isScaledDown = isScaledDown }

        // For all the non visible items in the layout manager, notify them to adjust the
        // view to the new size
        animator.doOnEnd {
            repeat(lm.itemCount)
            { if (it !in lm.visibleItemsRange) notifyItemChanged(it) }
        }
        return animator
    }

    private fun setScaleDownProgress(holder: VoteViewHolder, position: Int, progress: Float) {
        val itemExpanded = position >= 0 && voteList[position] == expandedModel
        holder.cardContainer.layoutParams.apply {
            width =
                ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
            height =
                ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
//            log("width=$width, height=$height [${"%.2f".format(progress)}]")
        }
        holder.cardContainer.requestLayout()



        holder.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.listItemFg.alpha = progress

    }

    /** Convenience method for calling from onBindViewHolder */
    private fun scaleDownItem(holder: VoteViewHolder, position: Int, isScaleDown: Boolean) {
        setScaleDownProgress(holder, position, if (isScaleDown) 1f else 0f)
    }


    inner class VoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val expandView: View = itemView.findViewById(R.id.expand_view)
        val chevron: View = itemView.findViewById(R.id.chevron)
        val cardContainer: View = itemView.findViewById(R.id.card_container)
        val scaleContainer: View = itemView.findViewById(R.id.scale_container)
        val listItemFg: View = itemView.findViewById(R.id.list_item_fg)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle1)
        val title: TextView = itemView.findViewById(R.id.title)
        val pieChart: PieChart = itemView.findViewById(R.id.pieChart)
        val button: Button = itemView.findViewById(R.id.button)
        val subtitle_vote: TextView = itemView.findViewById(R.id.subtitle_vote)


        init {
            button.setOnClickListener {

                val voteFragment = VoteFragment.newInstance(voteList[adapterPosition].id, "hello")

                val transaction = (context as MainActivity).supportFragmentManager.beginTransaction()

                transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right, R.animator.slide_in_left, R.animator.slide_in_right)

                transaction.replace(R.id.vote_fragment_container, voteFragment).addToBackStack(null)
                    .commit()


            }
        }


    }


}