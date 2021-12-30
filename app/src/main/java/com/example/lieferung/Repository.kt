package com.example.lieferung

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lieferung.util.Event
import com.example.lieferung.util.SPP_UUID
import com.example.lieferung.util.Util
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class Repository {
    var connected: MutableLiveData<Boolean?> = MutableLiveData(null)
    val putTxt: MutableLiveData<String> = MutableLiveData("")

    val connectError = MutableLiveData<Event<Boolean>>()

    var mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var mBluetoothStateReceiver: BroadcastReceiver? = null
    var targetDevice: BluetoothDevice? = null
    var socket: BluetoothSocket? = null
    var mOutputStream: OutputStream? = null
    var mIntputStream: InputStream? = null

    var foundDevice: Boolean = false

    private lateinit var sendByte: ByteArray
    var discovery_error = false

    //해당 기기 블루투스 지원 여부
    fun isBluetoothSupport(): Boolean {
        return if (mBluetoothAdapter == null) {
            Util.showNotification("블루투스 지원을 하지 않는 기기")
            false
        } else {
            Util.showNotification("블루투스 지원을 하는 기기")
            true
        }
    }

    //블루투스 상태
    fun isBluetoothEnable(): Boolean {
        return if (!mBluetoothAdapter!!.isEnabled) {
            //블루투스를 지원하지만 비활성화 상태인 경우
            //블루투스 활성 상태로 바꾸기 -> 사용자 동의 요청
            Util.showNotification("블루투스가 비활성화 상태입니다.\n블루투스를 활성화해주세요.")
            false
        } else {
            Util.showNotification("블루투스가 활성화 상태입니다.")
            true
        }
    }

    //디바이스 스캔
    fun scanDevice() {
        Util.showNotification("device 스캔 중...")

        //블루투스 리버시 등록
        registerBluetoothReceiver()

        //블루투스 기기 검색 시작
        val bluetoothAdapter = mBluetoothAdapter
        foundDevice = false
        bluetoothAdapter?.startDiscovery()    //블루투스 기기 검색 시작
    }

    //블루투스 리시버를 등록해
    //디바이스를 찾았을 때/디바이스가 연결/연결 해제 되었을 때 -> action을 받아 이벤트 처리를 할 수 있다.
    //디바이스를 찾았을 때, 특정 기기의 이름인지 확인하여 원하는 기기의 이름일 경우 연결을 시작
    //리시버는 생명주기의 onStop() 같은 부분에서, unregister
    fun registerBluetoothReceiver() {
        //intentfilter(인텐트필터)
        val stateFilter = IntentFilter()

        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)  //블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)  //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)  //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND)  //기기 검색
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)  //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)  //기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)

        mBluetoothStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action  //입력된 action

                if (action != null) {
                    Log.d("로그_블루투스_action: ", action)
                }

                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                var name: String? = null

                if (device != null) {
                    name = device.name  //Broadcast를 보낸 기기의 이름 가져오기
                }

                when (action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR
                        )
                        when (state) {
                            BluetoothAdapter.STATE_OFF -> {
                            }
                            BluetoothAdapter.STATE_TURNING_OFF -> {
                            }
                            BluetoothAdapter.STATE_ON -> {
                            }
                            BluetoothAdapter.STATE_TURNING_ON -> {
                            }
                        }
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    }
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        //디바이스가 연결 해제될 경우
                        connected.postValue(false)
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        if (!foundDevice) {
                            val device_name = device!!.name
                            val device_Address = device.address

                            //블루투스 기기 이름의 앞글자가 ""으로 시작하는 기기만 검색
                            if (device_name != null && device_name.length > 4) {
                                if (device_name.substring(0, 3) == "HC-") {
                                    //targetDevice 필터링 및 connectToTargetedDevice() 사용
                                    targetDevice = device
                                    foundDevice = true
                                    //찾은 디바이스에 연결
                                    connectToTargetedDevice(targetDevice)
                                }
                            }
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        if (!foundDevice) {
                            Util.showNotification("디바이스를 찾을 수 없습니다.\n다시 시도해주세요.")
                        }
                    }
                }
            }
        }
        //리시버 등록
        MyApplication.applicationContext().registerReceiver(
            mBluetoothStateReceiver,
            stateFilter
        )
    }

    @ExperimentalUnsignedTypes
    private fun connectToTargetedDevice(targetedDevice: BluetoothDevice?) {
        Util.showNotification("${targetedDevice?.name}에 연결중..")

        val thread = Thread {
            //선택된 기기의 이름을 갖는 블루투스 device의 object
            val uuid = UUID.fromString(SPP_UUID)
            try {
                //소켓 생성
                socket = targetedDevice?.createRfcommSocketToServiceRecord(uuid)
                socket?.connect()   //소켓 연결

                //장치 연결 후
                connected.postValue(true)  //연결 상태
                //output, input stream을 열어 송/수신
                mOutputStream = socket?.outputStream
                mIntputStream = socket?.inputStream
                //데이터 수신 시작
                beginListenForData()
            } catch (e: Exception) {
                //블루투스 연결 중 오류 발생
                e.printStackTrace()
                connectError.postValue(Event(true))

                try {
                    socket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //연결 thread 수행
        thread.start()
    }

    //연결 끊기
    fun disconnect() {
        try {
            socket?.close()
            connected.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun unregisterReceiver() {
        if (mBluetoothStateReceiver != null) {
            MyApplication.applicationContext().unregisterReceiver(mBluetoothStateReceiver)
            mBluetoothStateReceiver = null
        }
    }

    //블루투스 데이터 송신
    //String sendTxt를 byte array로 바꿔 전송
    fun sendByteData(data1: ByteArray, data2: ByteArray) {
        Thread {
            try {
                mOutputStream?.write(data1)  //프로토콜 전송
                mOutputStream?.write(data2)
            } catch (e: Exception) {
                //문자열 전송 도중 오류가 발생한 경우
                e.printStackTrace()
            }
        }.run()
    }

    //변환
    //ByteToUint: byte[] -> uint
    //byteArrayToHex: byte[] -> hex string
    private val m_ByteBuffer: ByteBuffer = ByteBuffer.allocateDirect(8)
    // byte -> uint
    fun ByteToUint(data: ByteArray?, offset: Int, endian: ByteOrder): Long {
        synchronized(m_ByteBuffer) {
            m_ByteBuffer.clear()
            m_ByteBuffer.order(endian)
            m_ByteBuffer.limit(8)

            if (endian === ByteOrder.LITTLE_ENDIAN) {
                m_ByteBuffer.put(data, offset, 4)
                m_ByteBuffer.putInt(0)
            } else {
                m_ByteBuffer.putInt(0)
                m_ByteBuffer.put(data, offset, 4)
            }
            m_ByteBuffer.position(0)
            return m_ByteBuffer.long
        }
    }

    // byte -> hex
    fun byteArrayToHex(a: ByteArray): String? {
        val sb = StringBuilder()

        for (b in a) {
            sb.append(String.format("%02x ", b))
        }
        return sb.toString()
    }

    //블루투스 데이터 수신 Listener
    @ExperimentalUnsignedTypes
    fun beginListenForData() {
        val mWorkerThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val bytesAvailable = mIntputStream?.available()

                    if (bytesAvailable != null) {
                        if (bytesAvailable > 0) {   //데이터가 수신된 경우
                            val packetBytes = ByteArray(bytesAvailable)

                            mIntputStream?.read(packetBytes)

                            //한 버퍼 처리
                            //Byte -> String
                            val s = String(packetBytes, Charsets.UTF_8)
                            //수신 String 출력
                            putTxt.postValue(s)

                            //한 바이트씩 처리
                            for (i in 0 until bytesAvailable) {
                                val b = packetBytes[i]

                                Log.d("로그_inputData: ", String.format("%02x", b))
                            }
                        }
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //데이터 수신 thread 시작
        mWorkerThread.start()
    }
}