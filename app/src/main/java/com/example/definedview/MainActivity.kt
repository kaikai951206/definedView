package com.example.definedview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.myapplication.FlowLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FlowLayout.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        flowLayout.setOnItemClickListener(this)
    }

    override fun onItemClick(view: View) {
        when (view) {
            is TextView -> flowLayout.removeView(view)
        }
    }


}