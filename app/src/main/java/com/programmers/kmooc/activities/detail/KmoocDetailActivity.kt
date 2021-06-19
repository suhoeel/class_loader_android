package com.programmers.kmooc.activities.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory
import com.programmers.kmooc.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class KmoocDetailActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private lateinit var binding: ActivityKmookDetailBinding

    private lateinit var viewModel: KmoocDetailViewModel

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        setContentView(binding.root)
        binding.progressBar.visibility = View.VISIBLE
        intent.getStringExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID)?.let {
            viewModel.detail(it)
        }

        viewModel.currentLecture.observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    if (result.data == null) {
                        Log.d("TEST", "연결 실패 처리")
                    }
                    setData(result.data!!)
                    binding.progressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
                Status.LOADING -> {

                }
            }
        })

    }

    private fun setData(lecture: Lecture) {
        ImageLoader.loadImage(lecture.courseImageLarge) { img ->
            if (img == null) {
                Log.d("TEST", "이미지를 불러올 수 없")
            } else {
                binding.lectureImage.setImageBitmap(img)
            }
            binding.lectureNumber.setDescription("강좌번호", "${lecture.number}")
            binding.lectureType.setDescription("강좌분류", "${lecture.classfyName}")
            binding.lectureOrg.setDescription("운영기관", "${lecture.orgName}")
            binding.lectureTeachers.setDescription("교수정보", "${lecture.teachers}")
            binding.lectureDue.setDescription(
                "운영기간",
                "${DateUtil.formatDate(lecture.start)} ~ ${DateUtil.formatDate(lecture.end)}"
            )

            binding.webView.loadData(lecture.overview, "text/html; charset=utf-8", "UTF-8");
        }

    }
}