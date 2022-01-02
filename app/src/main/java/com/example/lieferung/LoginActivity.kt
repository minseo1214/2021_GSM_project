package com.example.lieferung

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.lieferung.databinding.ActivityLoginBinding
import com.example.lieferung.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val TAG: String = "로그_로그인"

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            //editText로부터 입력된 값 받아오기
            var id = id.text.toString()
            var pw = password.text.toString()

            if (id.isNotEmpty() && pw.isNotEmpty()) {
                auth.signInWithEmailAndPassword(id, pw)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //입력한 id, pw값이 저장된 id, pw값과 같다면
                            Util.showNotification("로그인 성공!")
                            moveMainPage(auth.currentUser)
                        } else {
                            //로그인 실패 다이얼로그 보여주기
                            dialog("fail")
                        }
                    }
            }
        }
    }

    private fun dialog(type: String) {
        var dialog = AlertDialog.Builder(this)

        if (type.equals("fail")) {
            dialog.setTitle("로그인 실패")
            dialog.setMessage("아이디와 비밀번호를 확인해주세요!")
        }

        var dialog_listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        Log.d(TAG, "로그인 실패")
                }
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }

    //로그아웃하지 않을 시 자동 로그인
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }

    //유저정보 넘겨주고 MainActivity 호출
    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}