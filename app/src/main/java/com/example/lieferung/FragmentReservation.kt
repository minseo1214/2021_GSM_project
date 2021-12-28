package com.example.lieferung

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Lifecycle
import com.example.lieferung.databinding.FragmentReservationBinding
import kotlinx.android.synthetic.main.ble_list_dialog.*
import kotlinx.android.synthetic.main.fragment_reservation.*
import java.util.jar.Manifest

class FragmentReservation : Fragment() {

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinnerAdapterStart: SpinnerAdapter
    private lateinit var spinnerAdapterArrival: SpinnerAdapter
    private val listOfStart = ArrayList<SpinnerModel>()
    private val listOfArrival = ArrayList<SpinnerModel>()

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothDevice = mutableListOf<BluetoothDevice>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var bluetoothDevicesName = mutableListOf<String>()

    /*private lateinit var bluetoothAdapterName: AlertDialogAdapter
    private lateinit var bluetoothAdapterAddress: AlertDialogAdapter
    private val listOfName = ArrayList<AlertDialogModel>()
    private val listOfAddress = ArrayList<AlertDialogModel>()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentReservationBinding.inflate(layoutInflater)

        setupSpinnerStart()
        setupSpinnerArrival()
        setupSpinnerHandler()

        binding.btnReservation.setOnClickListener {
            val intent = Intent(requireContext(), BLEListActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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


}