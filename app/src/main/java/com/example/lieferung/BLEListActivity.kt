package com.example.lieferung

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.lieferung.databinding.ActivityBlelistBinding
import java.lang.Exception

lateinit var textStatus: TextView
lateinit var btnPaired: Button
lateinit var btnSearch: Button
lateinit var listView: ListView

lateinit var pairedDevices: Set<BluetoothDevice>
lateinit var btArrayAdapter: ArrayAdapter<String>
lateinit var deviceAddressArray: ArrayList<String>

private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

class BLEListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlelistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBlelistBinding.inflate(layoutInflater)
        val view = binding.root

        textStatus = binding.BLEStatus
        btnPaired = binding.BLEBtnPaired
        btnSearch = binding.BLEBtnSearch
        listView = binding.BLEListview

        setContentView(view)

        btArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        deviceAddressArray = ArrayList()
        listView.setAdapter(btArrayAdapter)

        btnPaired.setOnClickListener {
            onClickButtonPaired(listView)
        }

        btnSearch.setOnClickListener {
            onClickButtonSearch(listView)
        }

        //listView.setOnItemClickListener(myOnItemClickListener())

        binding.backBtn.setOnClickListener {
            finish()
        }


    }

    private fun onClickButtonPaired(view: View) {
        btArrayAdapter.clear()
        if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
            deviceAddressArray.clear()
        }
        pairedDevices = btAdapter!!.bondedDevices
        if (pairedDevices.size > 0) {
            //페어링된 장치의 이름과 주소 가져오기
            for (device in pairedDevices) {
                val deviceName = device.name
                val deviceHardwareAddress = device.address  //MAC 주소
                btArrayAdapter.add(deviceName)
                deviceAddressArray.add(deviceHardwareAddress)
            }
        } else
            Toast.makeText(this, "페어링된 기기가 없습니다.\n기기를 검색해주세요.", Toast.LENGTH_SHORT).show()
    }

    private fun onClickButtonSearch(view: View) {
        //장치가 이미 검색 중인지 확인
        if (btAdapter!!.isDiscovering) {
            btAdapter.cancelDiscovery()
        } else {
            if (btAdapter.isEnabled) {
                btAdapter.startDiscovery()
                btArrayAdapter.clear()

                if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
                    deviceAddressArray.clear()
                }
                val filter: IntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(receiver, filter)
            } else {
                Toast.makeText(applicationContext, "블루투스가 켜지지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //검색에서 장치 찾기 -> 블루투스디바이스 가져오기
                    //인텐트의 객체 및 해당 정보
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device!!.address    //MAC 주소

                    btArrayAdapter.add(deviceName)
                    deviceAddressArray.add(deviceHardwareAddress)
                    btArrayAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun myOnItemClickListener() = object: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            Toast.makeText(applicationContext, btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show()

            textStatus.setText("try...")

            val name: String? = btArrayAdapter.getItem(position)    //get name
            val address: String? = deviceAddressArray.get(position)    //get address
            var flag: Boolean = true

            val device: BluetoothDevice? = btAdapter?.getRemoteDevice(address)
            var btSocket: BluetoothSocket? = null

            //socket 만들고 연결

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //ACTION_FOUND 수신기 등록 취소
        unregisterReceiver(receiver)
    }

}
