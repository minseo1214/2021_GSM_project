package com.example.lieferung

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lieferung.databinding.FragmentUserBinding
import com.example.lieferung.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FragmentUser : Fragment() {
    private lateinit var auth: FirebaseAuth
    val TAG: String = "로그_유저페이지"


    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentUserBinding.inflate(layoutInflater)

        auth = Firebase.auth

        binding.btnAccount.setOnClickListener {
            FirebaseAuth.AuthStateListener { task ->
                val user = task.currentUser

                if (user != null) {
                    //로그인이 되었을 때
                    dialog("email")
                } else {
                    //로그아웃/로그인이 되지 않았을 때
                    dialog("login")
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            //로그인 화면으로 이동
            Log.d(TAG, "로그아웃 성공")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth.signOut()
        }

        binding.btnDropout.setOnClickListener {
            //로그인 화면으로 이동
            val intent = Intent(requireContext(), LoginActivity::class.java)

            if (auth != null) {
                auth.currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "회원탈퇴 성공")
                            Util.showNotification("회원정보가 삭제되었습니다.")
                            startActivity(intent)
                        }
                    }
            }
        }

    }

    private fun dialog(type: String) {
        var dialog = AlertDialog.Builder(requireContext())

        if (type.equals("email")) {
            dialog.setTitle("회원계정")
            dialog.setMessage(auth.currentUser.toString())
        } else if (type.equals("login")) {
            dialog.setTitle("회원계정 확인실패")
            dialog.setMessage("로그인이 되어있지 않습니다.\n로그인 해주세요.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}