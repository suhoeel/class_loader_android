package com.programmers.kmooc.viewmodels

import androidx.lifecycle.*
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository
import com.programmers.kmooc.vo.Resource
import kotlinx.coroutines.launch


class KmoocDetailViewModel(private val repository: KmoocRepository) : ViewModel() {

    private val _currentLecture: MutableLiveData<Resource<Lecture>> = MutableLiveData()

    val currentLecture: LiveData<Resource<Lecture>>
        get() = _currentLecture

    fun detail(courseId: String) {
        repository.detail(courseId) { lecture ->
            if(lecture == null) {
                viewModelScope.launch {
                    _currentLecture.value = Resource.error("error", null)
                }
            } else {
                viewModelScope.launch {
                    _currentLecture.value = Resource.success(lecture)
                }
            }
        }
    }
}

class KmoocDetailViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocDetailViewModel::class.java)) {
            return KmoocDetailViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}