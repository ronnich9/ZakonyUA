package com.feriramara.zakony.detailed

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.feriramara.zakony.utils.CollapsibleCard
import com.feriramara.zakony.R
import com.feriramara.zakony.utils.bindView
import com.feriramara.zakony.model.VoteDetail
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VoteFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null




    private val fullText: TextView by bindView(R.id.votefragment_full_text)
    private val pieChart: PieChart by bindView(R.id.pieChart_big)
    private val urlButton: Button by bindView(R.id.start_url)

    private val listZe: CollapsibleCard by bindView(R.id.list_of_ze)
    private val listOpzj: CollapsibleCard by bindView(R.id.list_of_opzj)
    private val listGolos: CollapsibleCard by bindView(R.id.list_of_golos)
    private val listDovira: CollapsibleCard by bindView(R.id.list_of_dovira)
    private val listPozafr: CollapsibleCard by bindView(R.id.list_of_pozafr)
    private val listZaMaybah: CollapsibleCard by bindView(R.id.list_of_zamaybah)
    private val listES: CollapsibleCard by bindView(R.id.list_of_es)
    private val listBatkiv: CollapsibleCard by bindView(R.id.list_of_batkiv)


    private val viewModel: VoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel.performSingleNetworkRequest(param1)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val my_view = inflater.inflate(R.layout.fragment_vote, container, false)


        val close_image: ImageView? = my_view?.findViewById(R.id.votefragment_close_image)

        close_image?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()

        }





        viewModel.uiState().observe(viewLifecycleOwner, Observer { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })

        return my_view
    }

    private fun render(uiState: VoteUiState) {
        when (uiState) {
            is VoteUiState.Loading -> {
                onLoad()
            }
            is VoteUiState.Success -> {
                onSuccess(uiState)
            }
            is VoteUiState.Error -> {
                onError(uiState)
            }
        }
    }

    private fun onLoad() {

    }

    private fun onSuccess(uiState: VoteUiState.Success) {
        fullText.text = uiState.vote.name


        urlButton.setOnClickListener {
            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uiState.vote.bills[0].url)))
        }

        if (uiState.vote.id.toInt() < 25000) {
            setPieChartVIII(uiState.vote)
            listZe.setRecyclerView(getVoters(uiState.vote.votes, "БЛОК ПЕТРА ПОРОШЕНКА"), "Блок Петра Порошенка")
            listOpzj.setRecyclerView(getVoters(uiState.vote.votes, "НАРОДНИЙ ФРОНТ"), "Народний фронт")
            listGolos.setRecyclerView(getVoters(uiState.vote.votes, "САМОПОМІЧ"), "Об'єднання «Самопоміч»")
            listDovira.setRecyclerView(getVoters(uiState.vote.votes, "Опозиційний блок"), "Опозиційний блок")
            listPozafr.setRecyclerView(getVoters(uiState.vote.votes, "Позафракційні"))
            listZaMaybah.setRecyclerView(getVoters(uiState.vote.votes, "Фракція Радикальної партії Олега Ляшка"), "Радикальна партія Олега Ляшка")
            listES.setRecyclerView(getVoters(uiState.vote.votes, "Батьківщина"), "ВО «Батьківщина»")
            listBatkiv.visibility = View.GONE
        } else {
            setPieChartIX(uiState.vote)
            listZe.setRecyclerView(getVoters(uiState.vote.votes, "фракція Слуга народу"))
            listOpzj.setRecyclerView(getVoters(uiState.vote.votes, "фракція Опозиційна платформа - За життя"))
            listGolos.setRecyclerView(getVoters(uiState.vote.votes, "фракція Голос"))
            listDovira.setRecyclerView(getVoters(uiState.vote.votes, "депутатська група ДОВІРА"))
            listPozafr.setRecyclerView(getVoters(uiState.vote.votes, "Позафракційні"))
            listZaMaybah.setRecyclerView(getVoters(uiState.vote.votes, "депутатська група За майбутнє"))
            listES.setRecyclerView(getVoters(uiState.vote.votes, "фракція Європейська солідарність"))
            listBatkiv.setRecyclerView(getVoters(uiState.vote.votes, "фракція Батьківщина"))

        }



    }

    private fun getVoters(voters: List<VoteDetail.Golos>, party: String): List<VoteDetail.Golos> {
        val listOfVoters = mutableListOf<VoteDetail.Golos>()
        for (voter in voters) {
            if (voter.member.party.contains(party))
                listOfVoters.add(voter)
        }
        listOfVoters.sortBy { it.member.last_name }
        return listOfVoters
    }

    private fun onError(uiState: VoteUiState.Error) {
        Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
    }

    private fun setPieChartIX(vote: VoteDetail) {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()

        var ze_aye = 0f
        var es_aye = 0f
        var golos_aye = 0f
        var dovira_aye = 0f
        var pozafrak_aye = 0f
        var za_maybah_aye = 0f
        var opzj_aye = 0f
        var batkiv_aye = 0f

        for (i in vote.votes) {
            if (i.member.party.contentEquals("фракція Слуга народу") && i.vote.contentEquals("aye")) {
                ze_aye++
            } else if (i.member.party.contentEquals("фракція Європейська солідарність") && i.vote.contentEquals("aye")) {
                es_aye++
            } else if (i.member.party.contentEquals("фракція Голос") && i.vote.contentEquals("aye")) {
                golos_aye++
            } else if (i.member.party.contentEquals("депутатська група ДОВІРА") && i.vote.contentEquals("aye")) {
                dovira_aye++
            } else if (i.member.party.contentEquals("Позафракційні") && i.vote.contentEquals("aye")) {
                pozafrak_aye++
            } else if (i.member.party.contentEquals("депутатська група За майбутнє") && i.vote.contentEquals("aye")) {
                za_maybah_aye++
            } else if (i.member.party.contentEquals("фракція Опозиційна платформа - За життя") && i.vote.contentEquals("aye")) {
                opzj_aye++
            } else if (i.member.party.contentEquals("фракція Батьківщина") && i.vote.contentEquals("aye")) {
                batkiv_aye++
            }
        }

        listPie.add(PieEntry(ze_aye, "СН (248)"))
        listColors.add(ColorTemplate.VORDIPLOM_COLORS[0])
        listPie.add(PieEntry(opzj_aye, "ОПЗЖ (44)"))
        listColors.add(ColorTemplate.PASTEL_COLORS[3])
        listPie.add(PieEntry(golos_aye, "Голос (20)"))
        listColors.add(ColorTemplate.VORDIPLOM_COLORS[3])
        listPie.add(PieEntry(dovira_aye, "Довіра (16)"))
        listColors.add(ColorTemplate.PASTEL_COLORS[0])
        listPie.add(PieEntry(pozafrak_aye, "Позафракційні (22)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[0])
        listPie.add(PieEntry(za_maybah_aye, "За майбутнє (22)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[1])
        listPie.add(PieEntry(es_aye, "ЄС (27)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[4])
        listPie.add(PieEntry(batkiv_aye, "Батьківщина (24)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[3])

        val pieDataSet = PieDataSet(listPie, "")
        pieDataSet.colors = listColors
        pieDataSet.sliceSpace = 5f
        pieDataSet.valueLinePart1Length = 0.7f
        pieDataSet.xValuePosition = null
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        val tf = Typeface.createFromAsset(context?.assets, "OpenSans-Regular.ttf");

        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(11f)
        pieData.setValueTextColor(Color.BLACK)
        pieData.setValueTypeface(tf)
        pieData.setValueFormatter(DefaultValueFormatter(0))
        pieChart.data = pieData

        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

        pieChart.description.isEnabled = false
       // pieChart.setExtraOffsets(10f, 10f, 10f, 10f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.rotationAngle = 30f

        pieChart.isDrawHoleEnabled = true
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setDrawCenterText(true)

        pieChart.centerText = generateCenterSpannableText(vote.aye_votes)

        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000, Easing.EaseInOutQuad)
    }

    private fun setPieChartVIII(vote: VoteDetail) {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()

        var nf_aye = 0f
        var bpp_aye = 0f
        var samop_aye = 0f
        var ob_aye = 0f
        var radikal_aye = 0f
        var pozafrak_aye = 0f
        var batkiv_aye = 0f

        for (i in vote.votes) {
            if (i.member.party.contains("НАРОДНИЙ ФРОНТ") && i.vote.contentEquals("aye")) {
                nf_aye++
            } else if (i.member.party.contains("БЛОК ПЕТРА ПОРОШЕНКА") && i.vote.contentEquals("aye")) {
                bpp_aye++
            } else if (i.member.party.contains("САМОПОМІЧ") && i.vote.contentEquals("aye")) {
                samop_aye++
            } else if (i.member.party.contains("Опозиційний блок") && i.vote.contentEquals("aye")) {
                ob_aye++
            } else if (i.member.party.contentEquals("Фракція Радикальної партії Олега Ляшка") && i.vote.contentEquals("aye")) {
                radikal_aye++
            } else if (i.member.party.contains("Батьківщина") && i.vote.contentEquals("aye")) {
                batkiv_aye++
            } else if (i.vote.contentEquals("aye")) {
                pozafrak_aye++
            }
        }

        listPie.add(PieEntry(bpp_aye, "Блок Петра Порошенка (127)"))
        listColors.add(ColorTemplate.VORDIPLOM_COLORS[0])
        listPie.add(PieEntry(nf_aye, "Народний Фронт (79)"))
        listColors.add(ColorTemplate.PASTEL_COLORS[3])
        listPie.add(PieEntry(samop_aye, "Самопоміч (25)"))
        listColors.add(ColorTemplate.VORDIPLOM_COLORS[3])
        listPie.add(PieEntry(ob_aye, "Опозиційний блок (39)"))
        listColors.add(ColorTemplate.PASTEL_COLORS[0])
        listPie.add(PieEntry(pozafrak_aye, "Позафракційні (22)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[0])
        listPie.add(PieEntry(radikal_aye, "Радикальна партія (21)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[1])
        listPie.add(PieEntry(batkiv_aye, "Батьківщина (21)"))
        listColors.add(ColorTemplate.JOYFUL_COLORS[3])


        val pieDataSet = PieDataSet(listPie, "")
        pieDataSet.colors = listColors
        pieDataSet.sliceSpace = 5f
        pieDataSet.valueLinePart1Length = 0.7f
        pieDataSet.xValuePosition = null
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        val tf = Typeface.createFromAsset(context?.assets, "OpenSans-Regular.ttf");

        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(11f)
        pieData.setValueTextColor(Color.BLACK)
        pieData.setValueTypeface(tf)
        pieData.setValueFormatter(DefaultValueFormatter(0))
        pieChart.data = pieData

        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

        pieChart.description.isEnabled = false
        // pieChart.setExtraOffsets(10f, 10f, 10f, 10f)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.rotationAngle = 30f

        pieChart.isDrawHoleEnabled = true
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setDrawCenterText(true)

        pieChart.centerText = generateCenterSpannableText(vote.aye_votes)

        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000, Easing.EaseInOutQuad)
    }

    fun generateCenterSpannableText(aye_votes: String) : SpannableString {
        val vote_icon = if (aye_votes.toInt() > 225) "Прийнято" else "Не прийнято"
        val vote_icon_color = if (aye_votes.toInt() > 225) Color.GREEN else Color.RED
        val s = SpannableString("${vote_icon}\n\n${aye_votes} / 423")
        s.setSpan(RelativeSizeSpan(1.7f), 0, vote_icon.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        s.setSpan(ForegroundColorSpan(vote_icon_color), 0, vote_icon.length, 0 )
        s.setSpan(ForegroundColorSpan(Color.GRAY), vote_icon.length, s.length, 0 )

        return s
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VoteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
