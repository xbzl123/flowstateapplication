package com.raysharp.flowstateapplication.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.*
import com.raysharp.flowstateapplication.R
import com.raysharp.flowstateapplication.databinding.MainFragmentBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    @InternalCoroutinesApi
    private lateinit var viewModel: MainViewModel

    private lateinit var binding:MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.main_fragment, container, false)
        binding = MainFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    @InternalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        // TODO: Use the ViewModel
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.messageStateflow.setOnClickListener {
//            viewModel.nameStateFlow.value = "456"
            viewModel.nameStateFlow.tryEmit("456")
        }
        binding.messageLivedata.setOnClickListener {
//            viewModel.nameLiveData.value = "456"
//            viewModel.nameLiveData.postValue("456")
        }
//        diffScopeCommunication()

        //终止本页面所有的协程的流处理
        binding.cancelButton.setOnClickListener {
            Toast.makeText(requireContext(),"cancell sucess!",Toast.LENGTH_SHORT).show()
            //全部取消
            //方法1
            viewModelStore.clear()
            //方法2
            viewModel.viewModelScope.coroutineContext.cancel()
            //单个取消
            viewModel.suspendCoroutines()
        }

        viewModel.nameLiveData.observe(viewLifecycleOwner){
            Log.e("nameLiveData","$it")
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
        lifecycleScope.launch(coroutineExceptionHandler) {
            lifecycle.whenStarted {

            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){

            }
        }

        lifecycleScope.launchWhenStarted {
            Log.e("MainFragment","launchWhenStarted")
            viewModel.nameStateFlow.collect{
                Log.e("nameStateFlow","$it")
            }
            //不生效
            viewModel.name1StateFlow.collect{
                Log.e("name1StateFlow","$it")
            }
        }

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.nameStateFlow.collect{
//                    Log.e("nameStateFlow","$it")
//                }
//            }
//        }
//        lifecycleScope.launchWhenStarted {
//            viewModel.name1StateFlow.collect{
//                Log.e("name1StateFlow","$it")
//            }
//        }

        //第一个协防接收数据
        lifecycleScope.launch {
            val sb = StringBuffer()
            viewModel.sharedFlow.collect {
                sb.append("<<${it}")
                binding.messageSharedflow.setText(sb)
            }
        }
        binding.sharedFlowButton.setOnClickListener {
            // 发送新的数据
            viewModel.doAsClick()
            // 发送新的数据以后，启动第二个协程
            lifecycleScope.launch {
                val sb = StringBuffer()
                viewModel.sharedFlow.collect {
                    sb.append("<<${it}")
                    binding.messageSharedflow2.text = sb.toString()
                }
            }
        }
    }

    override fun onStart() {
        Log.e("MainFragment","onStart")
        super.onStart()
    }
    fun diffScopeCommunication(){
        lifecycleScope.launch {
            val ch = Channel<Int> {  }
            //发射
            val channel = produce {
                withContext(Dispatchers.IO){
                    for (i in 1..10){
                        delay(500)
                        send(i)
                    }
                    close()
                }
            }
            //接收
            launch {
                for (i in channel){
                    Log.e("diffScopeCommunication","recevice =$i")
                }
            }
        }
    }
}