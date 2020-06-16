package com.feriramara.zakony.main

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feriramara.zakony.R
import com.feriramara.zakony.utils.bindView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {


    private val viewModel: VotesListViewModel by viewModels()

    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val appbar: AppBarLayout by bindView(R.id.appbar)
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    private val fab: ImageView by bindView(R.id.filter_icon)
    private val toolbar_text: TextView by bindView(R.id.toolbar_title)
    private val progressBar: ProgressBar by bindView(R.id.progressBar)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val githubCodeLink: TextView by bindView(R.id.github_code_link)
    private val rada4you_about: TextView by bindView(R.id.rada4you_code_link)
    private val playmarket_link: TextView by bindView(R.id.play_market_code_link)


    private lateinit var mainListAdapter: VoteListAdapter


    var newConnection: MutableLiveData<Boolean> = MutableLiveData()


    private val loadingDuration: Long
        get() = (resources.getInteger(R.integer.loadingAnimDuration) / 0.8).toLong()


    var policyName: String = "Всі голосування"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()

        toolbar_text.text = policyName

        viewModel.uiState().observe(this, Observer { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })

        drawerIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        githubCodeLink.setOnClickListener { openBrowser(R.string.github_link_code) }
        rada4you_about.setOnClickListener { openBrowser(R.string.rada4you_about) }
        playmarket_link.setOnClickListener { openBrowser(R.string.playmarket_link) }

    }



    private fun render(uiState: UiState) {
        Log.e("RENDER", "work")
        when (uiState) {
            is UiState.Loading -> {
                onLoad()
            }
            is UiState.Success -> {
                onSuccess(uiState)
            }
            is UiState.Error -> {
                onError(uiState)
            }
        }
    }

    private fun onLoad() {
        progressBar.visibility = View.VISIBLE
        try {
            mainListAdapter.clearRecycler()
        } catch (e: Exception) {

        }
    }

    private fun onSuccess(uiState: UiState.Success) {
        progressBar.visibility = View.GONE

        mainListAdapter =
            VoteListAdapter(this)
        recyclerView.adapter = mainListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        updateRecyclerViewAnimDuration()

        mainListAdapter.updateMatchList(uiState.votes)


    }

    private fun doStuff() {
        viewModel.performSingleNetworkRequest(-1)
        newConnection.value = true
    }

    private fun onError(uiState: UiState.Error) {
        progressBar.visibility = View.GONE

        Snackbar.make(
            findViewById(android.R.id.content),
            uiState.message,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("Повторити") { doStuff() }
            .setAnchorView(fab)
            .show()
    }

    private fun openBrowser(@StringRes resId: Int): Unit =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(resId))))


    private fun updateRecyclerViewAnimDuration() = recyclerView.itemAnimator?.run {
        removeDuration = loadingDuration * 60 / 100
        addDuration = loadingDuration
    }

    fun updateAdapter(policyId: Int, policyName: String) {
        viewModel.performSingleNetworkRequest(policyId)
        this.policyName = policyName
        toolbar_text.text = policyName
    }

    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator =
        mainListAdapter.getScaleDownAnimator(isScaledDown)


}
