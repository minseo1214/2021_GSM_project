package com.example.lieferung

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lieferung.databinding.ActivityMainBinding
import com.example.lieferung.ui.FragmentReservation
import com.example.lieferung.viewmodel.ReserveViewModel

private const val TAG_HOME = "home_fragment"
private const val TAG_RESERVATION = "reservation_fragment"
private const val TAG_USER = "user_fragment"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

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

}