package com.feriramara.zakony.utils

import android.content.Context
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.R
import com.feriramara.zakony.detailed.DeputatAdapter
import com.feriramara.zakony.model.VoteDetail

class CollapsibleCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var expanded = false
    private val cardTitleView: TextView
    private val cardDescriptionView: TextView
    private val expandIcon: ImageView
    private val titleContainer: View
    private val toggle: Transition
    private val root: View
    private val cardTitle: String?
    private val partyImage: ImageView

    private val adapter: DeputatAdapter = DeputatAdapter(context)

    private val cardRecycler: RecyclerView

    init {
        val arr = context.obtainStyledAttributes(attrs,
            R.styleable.CollapsibleCard, 0, 0)
        cardTitle = arr.getString(R.styleable.CollapsibleCard_cardTitle)
        val icon = arr.getDrawable(R.styleable.CollapsibleCard_cardIcon)
        arr.recycle()
        root = LayoutInflater.from(context)
            .inflate(R.layout.collapsible_card_content, this, true)

        titleContainer = root.findViewById(R.id.title_container)
        cardTitleView = root.findViewById<TextView>(R.id.card_title).apply {
            text = cardTitle
        }
        cardDescriptionView = root.findViewById<TextView>(R.id.card_second_title)



        cardRecycler = root.findViewById(R.id.collapsible_card_recycler)
        cardRecycler.adapter = adapter


        partyImage = findViewById<ImageView>(R.id.card_image).apply {
            setImageDrawable(icon)
        }

        expandIcon = root.findViewById(R.id.expand_icon)
        toggle = TransitionInflater.from(context)
            .inflateTransition(R.transition.info_card_toggle)
        titleContainer.setOnClickListener {
            toggleExpanded()
        }
    }

    fun setRecyclerView(list: List<VoteDetail.Golos>) {
        adapter.updateAdapter(list)
        cardDescriptionView.text = "[${list.size}]"
    }

    fun setRecyclerView(list: List<VoteDetail.Golos>, partyName: String) {
        adapter.updateAdapter(list)
        cardDescriptionView.text = "[${list.size}]"
        cardTitleView.text = partyName

        if (partyName == "Народний фронт") {
            partyImage.setImageResource(R.drawable.u_nf)
        } else if (partyName == "Блок Петра Порошенка") {
            partyImage.setImageResource(R.drawable.u_bpp)
        } else if (partyName == "Об'єднання «Самопоміч»") {
            partyImage.setImageResource(R.drawable.u_samop)
        } else if (partyName == "Опозиційний блок") {
            partyImage.setImageResource(R.drawable.u_opzj)
        } else if (partyName == "Радикальна партія Олега Ляшка") {
            partyImage.setImageResource(R.drawable.u_radikal)
        } else if (partyName == "ВО «Батьківщина»") {
            partyImage.setImageResource(R.drawable.u_batk)
        } else {
            partyImage.setImageResource(R.drawable.ic_launch)
        }

    }



    private fun toggleExpanded() {
        expanded = !expanded
        toggle.duration = if (expanded) 300L else 200L
        TransitionManager.beginDelayedTransition(root.parent as ViewGroup, toggle)
        cardRecycler.visibility = if (expanded) View.VISIBLE else View.GONE
        expandIcon.rotationX = if (expanded) 180f else 0f
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState =
            SavedState(super.onSaveInstanceState())
        savedState.expanded = expanded
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            if (expanded != state.expanded) {
                toggleExpanded()
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var expanded = false

        constructor(source: Parcel) : super(source) {
            expanded = source.readByte().toInt() != 0
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (expanded) 1 else 0).toByte())
        }

    }
}
