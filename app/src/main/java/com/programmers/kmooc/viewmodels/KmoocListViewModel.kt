package com.programmers.kmooc.viewmodels

import androidx.lifecycle.*
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository
import com.programmers.kmooc.vo.Resource
import kotlinx.coroutines.launch


class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {

    private val _lectureList: MutableLiveData<Resource<LectureList>> = MutableLiveData()

    val lectureList: LiveData<Resource<LectureList>>
        get() = _lectureList

    private val _addLectureList: MutableLiveData<Resource<LectureList>> = MutableLiveData()

    val addLectureList: LiveData<Resource<LectureList>>
        get() = _addLectureList

    fun list() {
        repository.list { lectureList ->
            if (lectureList == null) {
                viewModelScope.launch {
                    _lectureList.value = Resource.error("error", null)
                }
            } else {
                viewModelScope.launch {
                    _lectureList.value = Resource.success(lectureList)
                }
            }
        }
    }

    fun next() {
        lectureList.value?.data.let { current ->
            current?.let {
                repository.next(it) { lectureList ->
                    if(lectureList == null) {
                        viewModelScope.launch {
                            _addLectureList.value = Resource.error("error", null)
                        }
                    } else {
                        viewModelScope.launch {
                            _addLectureList.value = Resource.success(lectureList)
                        }
                    }
                }
            }
        }
    }
}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}