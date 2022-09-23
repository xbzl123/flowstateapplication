package com.raysharp.flowstateapplication.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.raysharp.flowstateapplication.R
import com.raysharp.flowstateapplication.databinding.MainFragmentBinding
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

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
            viewModel.nameLiveData.postValue("456")
        }
//        diffScopeCommunication()

        //终止本页面所有的协程的流处理
        binding.cancelButton.setOnClickListener {
            Toast.makeText(requireContext(),"cancell sucess!",Toast.LENGTH_SHORT).show()
            //全部取消
//            viewModelStore.clear()
//            viewModel.viewModelScope.coroutineContext.cancel()
            //单个取消
//            viewModel.suspendCoroutines()
        }

        viewModel.nameLiveData.observe(viewLifecycleOwner){
            Log.e("nameLiveData","$it")

        }

        lifecycleScope.launchWhenStarted {
            viewModel.nameStateFlow.collect{
                Log.e("nameStateFlow","$it")
            }
        }
    }

    fun diffScopeCommunication(){
        lifecycleScope.launch {
            val channel = Channel<Int>()
            //发射
            launch {
                for (i in 1..10){
                    delay(500)
                    channel.send(i)
                }
                channel.close()
            }
            //接收
            launch {
                for (i in channel){
                    Log.e("MainFragment","recevice =$i")
                }
            }
        }
    }
}