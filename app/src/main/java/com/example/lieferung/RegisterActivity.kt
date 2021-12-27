package com.example.lieferung

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.example.lieferung.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinnerAdapter: SpinnerAdapter
    private val listOfPhone = ArrayList<SpinnerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinnerPhone()
        setupSpinnerHandler()
    }

    private fun setupSpinnerPhone() {
        val phones = resources.getStringArray(R.array.phone_num)

        for (i in phones.indices) {
            val phone = SpinnerModel(phones[i])
            listOfPhone.add(phone)
        }

        spinnerAdapter = SpinnerAdapter(this, R.layout.item_spinner, listOfPhone)
        binding.spinnerPhone.adapter = spinnerAdapter
    }

    private fun setupSpinnerHandler() {
        binding.spinnerPhone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val phone = binding.spinnerPhone.getItemAtPosition(position) as SpinnerModel

                if (!phone.text.equals("010")) {
                    Log.d("로그","Selected: ${phone.text}")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}