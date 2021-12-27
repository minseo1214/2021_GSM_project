package com.example.lieferung

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.Dimension
import androidx.annotation.LayoutRes
import com.example.lieferung.databinding.ItemSpinnerBinding
import java.lang.Exception

class SpinnerAdapter(
    context: Context,
    @LayoutRes private val resId: Int,
    private val values: MutableList<SpinnerModel>
        ) : ArrayAdapter<SpinnerModel>(context, resId, values) {

    override fun getCount() = values.size

    override fun getItem(position: Int) = values[position]

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            binding.textSpinner.text = model.text
            //binding.textSpinner.setTextSize(Dimension.SP, 11F)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            binding.textSpinner.text = model.text
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }
        }