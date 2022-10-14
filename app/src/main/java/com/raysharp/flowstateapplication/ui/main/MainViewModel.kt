package com.raysharp.flowstateapplication.ui.main

import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.raysharp.flowstateapplication.NetworkServer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlin.system.measureTimeMillis

data class Number(val num: Int)
@InternalCoroutinesApi
class MainViewModel : ViewModel() {
    private var job: Job
    private var job1: Job


    // ViewModel
    val nameStateFlow = MutableStateFlow("nameStateFlow")

    val sharedFlow = MutableSharedFlow<Int>(
        5 // 参数一：当新的订阅者Collect时，发送几个已经发送过的数据给它
        , 3 // 参数二：MutableSharedFlow缓存区容量等于+replay，
        , BufferOverflow.DROP_OLDEST// 参数三：缓存策略，三种 丢掉最新值、丢掉最旧值和挂起
    )
    var shareFlowUI:SharedFlow<Int> = sharedFlow

    val nameLiveData = MutableLiveData<String>("nameLiveData")
    val name1StateFlow = MutableStateFlow("name1StateFlow")

    fun testLiveData(){
        nameLiveData.postValue("nameLiveData")
    }

    var startCount = 11
    // 在按钮中调用
    fun doAsClick() {
        val endCount = startCount+1
        for (i in startCount..endCount){
            sharedFlow.tryEmit(i)
        }
        startCount = endCount+1
    }

    //取消单个协防范围
    fun suspendCoroutines(){
        job.cancel()
    }

    init {
        for (i in 0..10) {
            sharedFlow.tryEmit(i)
        }

         job = viewModelScope.launch {
             addCollectEvent(0)
             addCollectEvent(1)
         }
         job1 = viewModelScope.launch {
        }
        val tmp =  flowOf(10, 200, 50, "String")
        val list = arrayListOf(5, 10, 15, 20)
        val flow = flow {
            (1..5).forEach {
                delay(100)
                emit(it)
            }
        }
        val chFlow = channelFlow{
            (1..5).forEach {
                delay(100)
                send(it)
            }
        }
        viewModelScope.launch {
            val time = measureTimeMillis {
                chFlow.collect {
                    delay(300)
                    Log.i("backpressure", "Collect value is：$it")
                }
            }
            Log.i("backpressure", "Total time: $time")
        }
//        testChannelFlow()
    }
    suspend fun addCollectEvent(i: Int) {
        val sendDelayTask = sendDelayTask()
        Log.e("sendDelayTask","is MainThread ="+(Looper.myLooper() == Looper.getMainLooper()))
        sendDelayTask.flowOn(Dispatchers.IO)
            .catch {
            Log.e("error", "throw =$this")
        }.collect{
                when(i){
                    0->{ nameStateFlow.value = it.num.toString() }
                    1->{ name1StateFlow.value = it.num.toString() }
                }
            }
    }
    private suspend fun sendDelayTask() =
        flow{
            for (i in 0..10){
                delay(500)
                emit(Number(i))
            }
    }

    private suspend fun test(){
        flow{
            emit(1)
        }.map { it.toString() }.collect(FlowCollector {
        })
    }

        fun testChannelFlow() = runBlocking {
            //创建channelFlow对象
            val flow = channelFlow {
                withContext(Dispatchers.IO){
                    for (i in 1..5){
                        delay(200)
                        send(i+i)
                    }
                }
                awaitClose ()
            }
            //创建接收端协程
            launch {
                flow.collect {
                    Log.e("testChannelFlow","result : $it")
                }
            }
        }
}