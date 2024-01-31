package com.cherry.permissions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cherry.permissions.databinding.ActivityUseInFragmentBinding

class UseInFragmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUseInFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUseInFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //toolBar 左侧返回图标事件
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}