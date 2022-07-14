package com.smallmarker.testtagflowlayout

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smallmarker.tagflowlayout.TagFlowAdapter
import com.smallmarker.tagflowlayout.TagFlowLayout
import com.smallmarker.testtagflowlayout.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dataList = mutableListOf<String>()
        dataList.addAll(generateList())
        binding.tagFlowLayout.adapter = TagFlowAdapter.create(dataList) {
            setView { parent, position, t ->
                TextView(parent.context).apply {
                    text = t
                    setBackgroundResource(R.drawable.bg_tag_selector)
                    setPadding(10, 10, 10, 10)
                }
            }
            setChecked { position, t ->
                position % 2 == 0
            }
        }

        binding.tagFlowLayout.setOnTagClickListener { view, position, parent ->

        }

        binding.btnNotify.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }
    }

    private fun generateList() = mutableListOf<String>().apply {
        for (i in 1 until Random.nextInt(20)) {
            add("测试" + i + 1)
        }
    }
}