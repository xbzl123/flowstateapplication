package com.raysharp.flowstateapplication.ui.main

import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel : ViewModel() {
    private var job: Job
    private var job1: Job


    // TODO: Implement the ViewModel
    val nameStateFlow = MutableStateFlow("nameStateFlow")
    val nameLiveData = MutableLiveData<String>("nameLiveData")

    init {
        job = viewModelScope.launch {
            addCollectEvent(0)
        }
        job1 = viewModelScope.launch {
            addCollectEvent(1)
        }
    }

    fun suspendCoroutines(){
        job.cancel()
//        viewModelScope.coroutineContext.cancel()
    }
    suspend fun addCollectEvent(i: Int) {
        nameStateFlow.collectLatest {
            Log.e("MainViewModel","$it")
//            if(it.equals("456")){
                val sendDelayTask = sendDelayTask()

                sendDelayTask.flowOn(Dispatchers.IO).catch {
                }.collect{
//                    Log.e("addCollectEvent","is MainThread ="+(Looper.myLooper() == Looper.getMainLooper()))
                    Log.e("addCollectEvent","No. $i sendDelayTask ="+it)
                }
//            }
        }
    }

    private suspend fun sendDelayTask() =
        flow{
            for (i in 0..10){
                Log.e("sendDelayTask","is MainThread ="+(Looper.myLooper() == Looper.getMainLooper()))
                delay(500)
                emit("$i")
            }
    }

}