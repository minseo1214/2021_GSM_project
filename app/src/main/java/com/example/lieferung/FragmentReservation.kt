package com.example.lieferung

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Lifecycle
import com.example.lieferung.databinding.FragmentReservationBinding
import java.util.jar.Manifest

//private const val SELECT_DEVICE_REQUEST_CODE = 0

class FragmentReservation : Fragment() {

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinnerAdapterStart: SpinnerAdapter
    private lateinit var spinnerAdapterArrival: SpinnerAdapter
    private val listOfStart = ArrayList<SpinnerModel>()
    private val listOfArrival = ArrayList<SpinnerModel>()

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

//    private var scanning = false
//    private val handler = Handler()
//    //10초 후 스캔 중지
//    private val SCAN_PERIOD: Long = 10000
//    private val bluetoothLeScanner: BluetoothLeScanner get() {
//        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        val bluetoothAdapter = bluetoothManager.adapter
//
//        return bluetoothAdapter.bluetoothLeScanner
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentReservationBinding.inflate(layoutInflater)



        //장치가 발견되면 브로드캐스트에 등록
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        //registerReceiver(receiver, filter)

        scanDevice()

        setupSpinnerStart()
        setupSpinnerArrival()
        setupSpinnerHandler()

        binding.btnReservation.setOnClickListener {

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        binding.btnReservation.setOnClickListener {
//            view: View -> scanLeDevice()
//        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

        //ACTION_FOUND 수신기 등록 취소
        //unregisterReceiver(receiver)
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
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun scanDevice() {
        //페어링된 기기가 있는지 확인
        val pairedDevice: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        pairedDevice?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address      //MAC 주소

            if (device != null) {
                for (i in deviceName) {
                    Log.d("로그_BLE_List: ", "${deviceName}/${deviceHardwareAddress}")
                }

            } else {
                Log.d("로그_BLE_List: ", "페어링된 기기가 없습니다.")
            }
        }
    }



//    private fun scanLeDevice() {
//        val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        val bluetoothAdapter = bluetoothManager.adapter
//        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
//
//        if (!scanning) {
//            //사전 정의된 검색 기간이 지나면 검색 중지
//            handler.postDelayed({
//                scanning = false
//                bluetoothLeScanner.stopScan(leScanCallback)
//            }, SCAN_PERIOD)
//            scanning = true
//
//            Log.d("로그_ScanDeviceStart: ", "startScan()")
//            when (PermissionChecker.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                PackageManager.PERMISSION_GRANTED -> bluetoothLeScanner.startScan(leScanCallback)
//
//                else -> requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
//            }
//        } else {
//            scanning = false
//            bluetoothLeScanner.stopScan(leScanCallback)
//        }
//    }

    //디바이스 스캔 Callback
//    private val leScanCallback = object: ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult?) {
//            super.onScanResult(callbackType, result)
//
//            Log.d("로그_ScanDevice: ", "leScanCallback >>")
//            Log.d("로그_ScanDevice: ", "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
//        }
//
//        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
//            super.onBatchScanResults(results)
//
//            Log.d("로그_DeviceList: ", "onBatchScanResults: ${results.toString()}")
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            super.onScanFailed(errorCode)
//
//            Log.d("DeviceList: ", "onScanFailed: $errorCode")
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            1 -> when (grantResults) {
//                intArrayOf(PackageManager.PERMISSION_GRANTED) -> {
//                    Log.d("로그_ScanDevices: ", "onRequestPermissionsResult(PERMISSION_GRANTED")
//
//                    bluetoothLeScanner.startScan(leScanCallback)
//                }
//                else -> {
//                    Log.d("로그_ScanDevice: ", "onRequestPermissionsResult(not PERMISSION_GRANTED")
//                }
//            } else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//
//    }



}