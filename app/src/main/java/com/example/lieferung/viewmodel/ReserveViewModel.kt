package com.example.lieferung.viewmodel

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.lieferung.Repository
import com.example.lieferung.ui.FragmentReservation
import com.example.lieferung.ui.SpinnerAdapter
import com.example.lieferung.ui.SpinnerModel
import com.example.lieferung.util.*
import java.nio.charset.Charset

class ReserveViewModel(private val reservation: Repository): ViewModel() {

    val connected: LiveData<Boolean?>
        get() = reservation.connected

    val connectError: LiveData<Event<Boolean>>
        get() = reservation.connectError

    val btnConnected = ObservableBoolean(false)

    private val _requestBleOn = MutableLiveData<Event<Boolean>>()
    val requestBleOn: LiveData<Event<Boolean>>
        get() = _requestBleOn

    val txtRead: ObservableField<String> = ObservableField("")
    val putTxt: LiveData<String>
        get() = reservation.putTxt



    fun onClickConnect() {
        if (connected.value == false || connected.value == null) {
            if (reservation.isBluetoothSupport()) { //블루투스 지원 체크
                if (reservation.isBluetoothEnable()) {  //블루투스 활성화 체크
                    //디바이스 스캔 시작
                    reservation.scanDevice()
                } else {
                    //블루투스를 지원하지만 비활성화 상태인 경우
                    //블루투스를 활성화 상태로 바꾸기 위해 사용자 동의 요청
                    _requestBleOn.value = Event(true)
                }
            } else {  //블루투스 지원 불가
                Util.showNotification("해당 기기에서 블루투스를 지원하지 않습니다.")
            }
        } else {
            reservation.disconnect()  //연결 끊기
        }
    }

    fun unregisterReceiver() {
        reservation.unregisterReceiver()
    }

    fun onClickSendData(sendTxt1: String, sendTxt2: String) {
        val byteArr1 = sendTxt1.toByteArray(Charset.defaultCharset())
        val byteArr2 = sendTxt2.toByteArray(Charset.defaultCharset())
        reservation.sendByteData(byteArr1, byteArr2)
        Util.showNotification("데이터를 전송했습니다.")
    }
}