package com.thaifediverse.fasai.components.search.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.thaifediverse.fasai.AccountActivity
import com.thaifediverse.fasai.BottomSheetActivity
import com.thaifediverse.fasai.R
import com.thaifediverse.fasai.ViewTagActivity
import com.thaifediverse.fasai.components.search.SearchViewModel
import com.thaifediverse.fasai.di.Injectable
import com.thaifediverse.fasai.di.ViewModelFactory
import com.thaifediverse.fasai.interfaces.LinkListener
import com.thaifediverse.fasai.util.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

abstract class SearchFragment<T> : Fragment(),
        LinkListener, Injectable, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected val viewModel: SearchViewModel by viewModels({ requireActivity() }) { viewModelFactory }

    private var snackbarErrorRetry: Snackbar? = null

    abstract fun createAdapter(): PagedListAdapter<T, *>

    abstract val networkStateRefresh: LiveData<NetworkState>
    abstract val networkState: LiveData<NetworkState>
    abstract val data: LiveData<PagedList<T>>
    protected lateinit var adapter: PagedListAdapter<T, *>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupSwipeRefreshLayout()
        subscribeObservables()
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeResources(R.color.tusky_blue)
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                ThemeUtils.getColor(swipeRefreshLayout.context, android.R.attr.colorBackground)
        )
    }

    private fun subscribeObservables() {
        data.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        networkStateRefresh.observe(viewLifecycleOwner, Observer {

            searchProgressBar.visible(it == NetworkState.LOADING)

            if (it.status == Status.FAILED) {
                showError()
            }
            checkNoData()

        })

        networkState.observe(viewLifecycleOwner, Observer {

            progressBarBottom.visible(it == NetworkState.LOADING)

            if (it.status == Status.FAILED) {
                showError()
            }
        })
    }

    private fun checkNoData() {
        showNoData(adapter.itemCount == 0)
    }

    private fun initAdapter() {
        searchRecyclerView.addItemDecoration(DividerItemDecoration(searchRecyclerView.context, DividerItemDecoration.VERTICAL))
        searchRecyclerView.layoutManager = LinearLayoutManager(searchRecyclerView.context)
        adapter = createAdapter()
        searchRecyclerView.adapter = adapter
        searchRecyclerView.setHasFixedSize(true)
        (searchRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun showNoData(isEmpty: Boolean) {
        if (isEmpty && networkStateRefresh.value == NetworkState.LOADED)
            searchNoResultsText.show()
        else
            searchNoResultsText.hide()
    }

    private fun showError() {
        if (snackbarErrorRetry?.isShown != true) {
            snackbarErrorRetry = Snackbar.make(layoutRoot, R.string.failed_search, Snackbar.LENGTH_INDEFINITE)
            snackbarErrorRetry?.setAction(R.string.action_retry) {
                snackbarErrorRetry = null
                viewModel.retryAllSearches()
            }
            snackbarErrorRetry?.show()
        }
    }

    override fun onViewAccount(id: String) = startActivity(AccountActivity.getIntent(requireContext(), id))

    override fun onViewTag(tag: String) = startActivity(ViewTagActivity.getIntent(requireContext(), tag))

    override fun onViewUrl(url: String) {
        bottomSheetActivity?.viewUrl(url)
    }

    protected val bottomSheetActivity
        get() = (activity as? BottomSheetActivity)

    override fun onRefresh() {

        // Dismissed here because the RecyclerView bottomProgressBar is shown as soon as the retry begins.
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = false
        }
        viewModel.retryAllSearches()
    }
}
