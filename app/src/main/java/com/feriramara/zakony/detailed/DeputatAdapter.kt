package com.feriramara.zakony.detailed

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.R
import com.feriramara.zakony.utils.bindView
import com.feriramara.zakony.utils.getDeputatImage
import com.feriramara.zakony.model.VoteDetail
import com.github.mikephil.charting.utils.ColorTemplate
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.GrayscaleTransformation

class DeputatAdapter(context: Context?) : RecyclerView.Adapter<DeputatAdapter.DeputatViewHolder>() {

    private var deputatList: List<VoteDetail.Golos> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeputatViewHolder {
        return DeputatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_deputat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DeputatViewHolder, position: Int) {

        val deputatFullName =
            deputatList[position].member.last_name + " " + deputatList[position].member.first_name
        holder.deputatName.text = deputatFullName
        holder.deputatElectorate.text = deputatList[position].member.electorate

        Picasso.get().load(getDeputatImage(deputatList[position].member.person.id)).transform(GrayscaleTransformation())
            .into(holder.deputatImage)



        if (deputatList[position].vote.equals("aye")) {
            holder.deputatVote.text = "ТАК"
            holder.deputatVote.setBackgroundColor(ColorTemplate.VORDIPLOM_COLORS[0])
        } else if (deputatList[position].vote.equals("not voting")) {
            holder.deputatVote.text = "Ні"
            holder.deputatVote.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.deputatVote.text = "Н/Г"
            holder.deputatVote.setBackgroundColor(Color.LTGRAY)

        }



    }



    fun updateAdapter(deputats: List<VoteDetail.Golos>) {
        this.deputatList = deputats
        //notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return deputatList.size
    }


    class DeputatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val deputatImage: ImageView by bindView(R.id.deputat_image)
        val deputatName: TextView by bindView(R.id.deputat_name)
        val deputatVote: TextView by bindView(R.id.deputat_vote)
        val deputatElectorate: TextView by bindView(R.id.deputat_electorate)
    }
}
