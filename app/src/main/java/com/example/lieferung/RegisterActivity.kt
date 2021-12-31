package com.example.lieferung

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lieferung.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}