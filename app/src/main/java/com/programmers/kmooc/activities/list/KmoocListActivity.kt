package com.programmers.kmooc.activities.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.activities.detail.KmoocDetailActivity
import com.programmers.kmooc.databinding.ActivityKmookListBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.viewmodels.KmoocListViewModel
import com.programmers.kmooc.viewmodels.KmoocListViewModelFactory
import com.programmers.kmooc.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class KmoocListActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityKmookListBinding

    private lateinit var viewModel: KmoocListViewModel

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocListViewModelFactory(kmoocRepository)).get(
            KmoocListViewModel::class.java
        )

        binding = ActivityKmookListBinding.inflate(layoutInflater)



        setContentView(binding.root)

        val adapter = LecturesAdapter()
            .apply {
                onClick = this@KmoocListActivity::startDetailActivity
            }

        binding.lectureList.adapter = adapter

        viewModel.lectureList.observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    if (result.data == null) {
                        Log.d("TEST", "연결 실패 처리")
                    }

                    adapter.updateLectures(result.data!!.lectures)
                    binding.pullToRefresh.isRefreshing = false
                    binding.progressBar.visibility = View.GONE

                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE

                }
            }

        })

        viewModel.addLectureList.observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    if (result.data == null) {
                        Log.d("TEST", "연결 실패 처리")
                    }

                    adapter.addLectures(result.data!!.lectures)
                    binding.progressBar.visibility = View.GONE
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

        launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.list()
        }

        setSwipeRefreshLayout()

        binding.lectureList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    launch(Dispatchers.Main) {
                        binding.progressBar.visibility = View.VISIBLE
                        viewModel.next()
                    }
                }
            }
        })
        /*val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(10)
            .setPrefetchDistance(5)
            .setEnablePlaceholders(true)
            .build()

        val pagedList = PagedList.Builder(, config)*/
    }

    private fun setSwipeRefreshLayout() {
        binding.pullToRefresh.setOnRefreshListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.list()
        }
    }

    private fun startDetailActivity(lecture: Lecture) {
        startActivity(
            Intent(this, KmoocDetailActivity::class.java)
                .apply { putExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID, lecture.id) }
        )
    }
}
