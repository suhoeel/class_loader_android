package com.programmers.kmooc.activities.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.R
import com.programmers.kmooc.databinding.ViewKmookListItemBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import org.w3c.dom.Text

class LecturesAdapter : RecyclerView.Adapter<LectureViewHolder>() {

    private val lectures = mutableListOf<Lecture>()
    var onClick: (Lecture) -> Unit = {}
    fun updateLectures(lectures: List<Lecture>) {
        this.lectures.clear()
        this.lectures.addAll(lectures)
        notifyDataSetChanged()
    }

    fun addLectures(lectures: List<Lecture>) {
        this.lectures.addAll(lectures)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_kmook_list_item, parent, false)
        val binding = ViewKmookListItemBinding.bind(view)

        return LectureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val lecture = lectures[position]

        ImageLoader.loadImage(lecture.courseImage) { img ->
            if (img == null) {

            } else {
                holder.itemView.findViewById<ImageView>(R.id.lectureImage).setImageBitmap(img)
            }
        }
        holder.itemView.findViewById<TextView>(R.id.lectureTitle).text = lecture.name
        holder.itemView.findViewById<TextView>(R.id.lectureFrom).text = lecture.orgName
        holder.itemView.findViewById<TextView>(R.id.lectureDuration).text = "${DateUtil.formatDate(lecture.start)} ~ ${DateUtil.formatDate(lecture.end)}"
        holder.itemView.setOnClickListener { onClick(lecture) }
    }
}

class LectureViewHolder(binding: ViewKmookListItemBinding) : RecyclerView.ViewHolder(binding.root) {
}