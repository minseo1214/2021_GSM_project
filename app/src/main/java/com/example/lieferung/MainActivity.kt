package com.example.lieferung

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lieferung.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG_HOME = "home_fragment"
private const val TAG_RESERVATION = "reservation_fragment"
private const val TAG_USER = "user_fragment"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val MY_PERMISSION_ACCESS_ALL = 100

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val REQUEST_ENABLE_BT: Int = 1 //블루투스 활성화


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            var permission = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permission, MY_PERMISSION_ACCESS_ALL)
        }



        //맨 처음 보이는 프래그먼트 설정
        setFragment(TAG_HOME, FragmentHome())
        binding.naviBar.selectedItemId = R.id.icon_home

        //navi_menu 항목 클릭 시 프래그먼트 변경 함수 호출
        binding.naviBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.icon_home -> setFragment(TAG_HOME, FragmentHome())
                R.id.icon_reservation -> setFragment(TAG_RESERVATION, FragmentReservation())
                R.id.icon_user -> setFragment(TAG_USER, FragmentUser())
            }
            true
        }
        //fragment 설정
/*        val resultFragmentId = intent.getIntExtra("selectFragmentId", 1)
        binding.naviBar.selectedItemId = resultFragmentId   */
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()

        //트랜잭션에 tag(전달)된 fragment가 없을 경우 추가(add)
        if (manager.findFragmentByTag(tag) == null) {
            ft.add(R.id.main, fragment, tag)
        }

        //작업 원활을 위해 manager에 추가(add)된 fragment들을 변수로 할당
        val home = manager.findFragmentByTag(TAG_HOME)
        val reservation = manager.findFragmentByTag(TAG_RESERVATION)
        val user = manager.findFragmentByTag(TAG_USER)

        //모든 fragment 숨기기(hide)
        if (home != null) {
            ft.hide(home)
        }
        if (reservation != null) {
            ft.hide(reservation)
        }
        if (user != null) {
            ft.hide(user)
        }

        //선택 항목(navi_menu)에 따라 그에 맞는 fragment만 보여주기(show)
        if (tag == TAG_HOME) {
            if (home != null) {
                ft.show(home)
            }
        } else if (tag == TAG_RESERVATION) {
            if (reservation != null) {
                ft.show(reservation)
            }
        } else if (tag == TAG_USER) {
            if (user != null) {
                ft.show(user)
            }
        }

        //마무리
        ft.commitAllowingStateLoss()
        //ft.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //사용자가 권한을 거부하면 앱 종료
        if (requestCode === MY_PERMISSION_ACCESS_ALL) {
            if (grantResults.size > 0) {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        System.exit(0)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //해당 기기 BLE 지원 여부
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "이 기기는 BLE를 지원하지 않습니다.", Toast.LENGTH_LONG).show()
            Log.d("로그_BLE_지원여부","지원X")
        } else{
            Log.d("로그_BLE_지원여부", "지원O")
            //블루투스 활성화 - false를 반환하는 경우 블루투스 비활성화된 상태
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }



}