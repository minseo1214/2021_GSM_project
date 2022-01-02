package com.example.lieferung.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lieferung.MyApplication
import com.example.lieferung.R
import com.example.lieferung.databinding.FragmentReservationBinding
import com.example.lieferung.util.*
import com.example.lieferung.viewmodel.ReserveViewModel
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentReservation : Fragment() {

    lateinit var binding : FragmentReservationBinding

    private val  viewModel by viewModel<ReserveViewModel>()

    var recv: String = ""

    private lateinit var spinnerAdapterStart: SpinnerAdapter
    private lateinit var spinnerAdapterArrival: SpinnerAdapter
    private val listOfStart = ArrayList<SpinnerModel>()
    private val listOfArrival = ArrayList<SpinnerModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinnerStart()
        setupSpinnerArrival()
        setupSpinnerHandler()

        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            requestPermissions(PERMISSIONS, PERMISSION_ACCESS_ALL)
        }

        initObserving()

//        binding.btnReservation.setOnClickListener {
//            //viewModel.onClickSendData(point1, point2)
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reservation, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding
    }

    private fun setupSpinnerStart() {
        val starts = resources.getStringArray(R.array.stations_start)

        for (i in starts.indices) {
            val start = SpinnerModel(starts[i])
            listOfStart.add(start)
        }

        spinnerAdapterStart = SpinnerAdapter(requireContext(), R.layout.item_spinner, listOfStart)
        binding.spinnerStartPoint.adapter = spinnerAdapterStart
    }

    private fun setupSpinnerArrival() {
        val arrivals = resources.getStringArray(R.array.stations_arrival)

        for (i in arrivals.indices) {
            val arrival = SpinnerModel(arrivals[i])
            listOfArrival.add(arrival)
        }

        spinnerAdapterArrival = SpinnerAdapter(requireContext(), R.layout.item_spinner, listOfArrival)
        binding.spinnerArrivalPoint.adapter = spinnerAdapterArrival
    }

    private fun setupSpinnerHandler() {
        binding.spinnerStartPoint.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val start = binding.spinnerStartPoint.getItemAtPosition(position) as SpinnerModel

                if (!start.text.equals("출발지를 선택하세요")) {
                    Log.d("로그_출발지", "Selected: ${start.text}")
                    viewModel.startPoint.set(start.text)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("로그", "Selected: Not Selected")
            }
        }

        binding.spinnerArrivalPoint.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val arrival = binding.spinnerArrivalPoint.getItemAtPosition(position) as SpinnerModel

                if (!arrival.text.equals("목적지를 선택하세요")) {
                    Log.d("로그_도착지", "Selected: ${arrival.text}")
                    viewModel.arrivalPoint.set(arrival.text)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            viewModel.onClickConnect()
        }
    }

    private fun initObserving() {
        //Progress
        viewModel.inProgress.observe(viewLifecycleOwner, {
            if (it.getContentIfNotHandled() == true) {
                viewModel.inProgressView.set(true)
            } else {
                viewModel.inProgressView.set(false)
            }
        })

        //Progress text
        viewModel.progressState.observe(viewLifecycleOwner, {
            viewModel.txtProgress.set(it)
        })

        //블루투스 On 요청
        viewModel.requestBleOn.observe(viewLifecycleOwner, {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startForResult.launch(enableBtIntent)
        })

        //블루투스 연결/연결 끊김 이벤트
        viewModel.connected.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it) {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(true)
                    Util.showNotification("디바이스와 연결되었습니다.")
                } else {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(false)
                    Util.showNotification("디바이스와 연결이 해제되었습니다.")
                }
            }
        })

        //블루투스 연결 에러
        viewModel.connectError.observe(viewLifecycleOwner, {
            Util.showNotification("연결 오류.\n디바이스를 다시 확인해주세요")
            viewModel.setInProgress(false)
        })

//        //데이터 받기
//        viewModel.putTxt.observe(viewLifecycleOwner, {
//            if (it != null) {
//                recv += it
//                viewModel.txtRead.set(recv)
//            }
//        })
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, permission) }
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    //권한 확인
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_ACCESS_ALL -> {
                //요청 취소 -> 결과 배열이 비어있다.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "사용 권한 부여 완료!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, PERMISSION_ACCESS_ALL)
                    Toast.makeText(requireContext(), "권한 요청에 동의해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.txtRead.set("여기서 메세지가 오는 것을 볼 수 있다.")
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver()
    }

}