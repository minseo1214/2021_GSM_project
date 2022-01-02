package com.example.lieferung

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.lieferung.databinding.ActivityRegisterBinding
import com.example.lieferung.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var localDB: LocalDB
    val DATABASE_VERSION = 1
    val DATABASE_NAME = "LocalDB.db"
    val TAG: String = "로그_회원가입"
    var isBlank = false
    var isPwSame = false

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        localDB = LocalDB(this, DATABASE_NAME, null, DATABASE_VERSION)  //SQLite 모듈 생성

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 클릭")

            val name = edit_name.text.toString()
            val id = edit_id.text.toString()
            val pw = edit_pw.text.toString()
            val pw_re = edit_pw_re.text.toString()

            //유저가 항목을 다 채우지 않을 경우
            if (name.isEmpty() || id.isEmpty() || pw.isEmpty()) {
                isBlank = true
            } else{ //항목을 다 채웠을 경우
                if (pw == pw_re) { //비밀번호가 같다면
                    isPwSame = true
                }
            }

            if (!isBlank && isPwSame) { //빈칸이 비어있지 않고, 비밀번호가 같다면
                //id, pw 저장
                createAccount(name, id, pw) //SQL 저장
                createUser(id, pw)  //Firebase 저장

                //회원가입 성공
                Log.d(TAG, "회원가입 성공/ 이름: ${name}, id: ${name}, pw: ${pw}")

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                //상태에 따른 다이얼로그 띄우기
                if (isBlank) {  //빈칸이 있을 경우
                    dialog("blank")
                    Log.d(TAG, "빈칸 존재")
                } else if (!isPwSame) { //입력한 비밀번호가 다를 경우
                    dialog("not same")
                    Log.d(TAG, "비밀번호가 같지 않음")
                }
            }
        }
    }

    //회원가입 실패시 띄워지는 다이얼로그
    fun dialog(type: String) {
        val dialog = AlertDialog.Builder(this)

        //빈칸이 있을 경우
        if (type.equals("blank")) {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("빈칸이 있습니다.\n" + "입력란을 모두 작성해주세요!")
        }
        //비밀번호가 다를 경우
        else if (type.equals("not same")) {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("비밀번호가 같지 않습니다.\n" + "다시 확인해주세요!")
        }

        val dialog_listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        Log.d(TAG, "다이얼로그")
                }
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }

    private fun createAccount(name: String, id: String, pw: String) {
        if (name.isNotEmpty() && id.isNotEmpty() && pw.isNotEmpty()) {
        //항목을 다 채웠을 경우
        // ID 중복확인
            if (localDB.checkIdExists(id)) {  //ID 중복O
                Util.showNotification("아이디가 이미 존재합니다.")
            } else {  //ID 중복X
                localDB.registerUser(name, id, pw)
            }
        }
    }

    private fun createUser(id: String, pw: String) {
        auth.createUserWithEmailAndPassword(id, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Util.showNotification("회원가입 성공!")
                } else {
                    Util.showNotification("회원가입 실패")
                }
            }
            .addOnFailureListener {
                Util.showNotification("회원가입 실패")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}