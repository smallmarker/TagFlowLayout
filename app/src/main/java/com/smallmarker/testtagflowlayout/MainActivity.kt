package com.smallmarker.testtagflowlayout

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smallmarker.tagflowlayout.TagFlowAdapter
import com.smallmarker.testtagflowlayout.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 数据集合
    private val dataList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化监听
        initListener()
        // 初始化数据
        initData()
    }

    private fun initData() {
        // 添加数据
        dataList.addAll(generateList())
        // 设置Adapter
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
            setCheckedChanged { isChecked, position, view ->
                Log.d("TAG", "当前TAG状态：${isChecked}, ${position}")
            }
        }
    }

    private fun initListener() {
        // 点击事件
        binding.tagFlowLayout.setOnTagClickListener { view, position, parent ->
            Log.d("TAG", "当前选中TAG： ${position}")
        }

        // 状态监听
        binding.tagFlowLayout.setCheckedChangedListener { group, checkedIds ->
            binding.tvTip.text = "当前选中TAG序号：${checkedIds}"
        }
        binding.btnNotify.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }

        binding.btnMutly.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.setSingleSelection(false)
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }

        binding.btnSingle.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.setSingleSelection(true)
            binding.tagFlowLayout.setSelectionRequired(true)
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }

        binding.btnSingleNormal.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.setSingleSelection(true)
            binding.tagFlowLayout.setSelectionRequired(false)
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }

        binding.btnSingleLine.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.isSingleLine = true
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }

        binding.btnMutlyLine.setOnClickListener {
            dataList.clear()
            dataList.addAll(generateList())
            binding.tagFlowLayout.isSingleLine = false
            binding.tagFlowLayout.adapter?.notifyDataSetChange()
        }
    }

    /**
     * 随机生成数据源
     */
    private fun generateList() = mutableListOf<String>().apply {
        for (i in 0 until Random.nextInt(1, 20)) {
            add("测试$i")
        }
    }
}