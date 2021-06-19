package com.programmers.kmooc.activities.list

import androidx.recyclerview.widget.DiffUtil
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList

object LectureComparator : DiffUtil.ItemCallback<Lecture>() {
    override fun areItemsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
        return oldItem == newItem
    }
}