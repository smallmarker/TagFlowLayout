package com.smallmarker.tagflowlayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Dimension
import androidx.core.content.res.use
import com.google.android.material.internal.CheckableGroup
import com.google.android.material.internal.FlowLayout
import com.google.android.material.internal.ThemeEnforcement

private typealias TagClickListener = (view: View, position: Int, parent: TagFlowLayout) -> Unit

private typealias CheckedChangedListener = (group: TagFlowLayout, checkedIds: MutableList<Int>) -> Unit

/**
 * @author   zl
 * @Date     2022/7/14
 **/
@SuppressLint("RestrictedApi")
class TagFlowLayout : FlowLayout {

    private val checkableGroup = CheckableGroup<TagView>()

    // Adapter
    var adapter: TagFlowAdapter<*>? = null
        set(value) {
            field = value?.apply {
                setNotifyDataSetChange {
                    changeAdapter()
                }
            }
            field?.notifyDataSetChange()
        }

    // Tag左右边距
    @Dimension
    private var tagSpacingHorizontal = 0

    // Tag上下边距
    @Dimension
    private var tagSpacingVertical = 0

    // Tag点击事件
    private var tagClickListener: TagClickListener? = null

    // Tag状态变更监听事件
    private var checkedChangedListener: CheckedChangedListener? = null

    // 最大选择
    private var selectMax: Int = 0

    private val passThroughListener = PassThroughHierarchyChangeListener()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        ThemeEnforcement.obtainStyledAttributes(
            context,
            attrs,
            R.styleable.TagFlowLayout,
            defStyleAttr,
            -1
        ).use {
            val tagSpacing = it.getDimensionPixelOffset(R.styleable.TagFlowLayout_tagSpacing, 0);
            setTagSpacingHorizontal(
                it.getDimensionPixelOffset(
                    R.styleable.TagFlowLayout_tagSpacingHorizontal,
                    tagSpacing
                )
            )
            setTagSpacingVertical(
                it.getDimensionPixelOffset(
                    R.styleable.TagFlowLayout_tagSpacingVertical,
                    tagSpacing
                )
            )
            isSingleLine = it.getBoolean(R.styleable.TagFlowLayout_singleLine, false)
            setSingleSelection(it.getBoolean(R.styleable.TagFlowLayout_singleSelection, false))
            setSelectionRequired(it.getBoolean(R.styleable.TagFlowLayout_selectionRequired, false))
            setSelectMax(it.getInt(R.styleable.TagFlowLayout_selectMax, 0))
        }
        checkableGroup.setOnCheckedStateChangeListener {
            checkedChangedListener?.invoke(
                this,
                checkableGroup.getCheckedIdsSortedByChildOrder(this)
            )
        }
        super.setOnHierarchyChangeListener(passThroughListener)
    }

    /**
     * 设置Tag点击事件
     */
    fun setOnTagClickListener(tagClickListener: TagClickListener) {
        this.tagClickListener = tagClickListener
    }

    /**
     * 设置Tag状态变更监听事件
     */
    fun setCheckedChangedListener(checkedChangedListener: CheckedChangedListener) {
        this.checkedChangedListener = checkedChangedListener
    }

    /**
     * 刷新Adapter
     */
    private fun changeAdapter() {
        (adapter as TagFlowAdapter<Any>?)?.let {
            removeAllViews()
            for (i in 0 until it.getCount()) {
                val item = it.getItem(i)
                val child = it.getView(this@TagFlowLayout, i, item)?.apply {
                    isDuplicateParentStateEnabled = true
                    isClickable = false
                }
                if (child != null) {
                    val tagView = TagView(context).apply {
                        addView(child)
                    }
                    addView(tagView)
                    setChildCheckedState(it.isChecked(i, item) && !isSelectMax(), i, tagView)
                    tagView.setOnClickListener {
                        setChildCheckedState(!tagView.isChecked && !isSelectMax(), i, tagView)
                        tagClickListener?.invoke(this, i, this@TagFlowLayout)
                    }
                }
            }
        }
    }

    /**
     * 设置TAG状态
     */
    private fun setChildCheckedState(isChecked: Boolean, position: Int, view: TagView) {
        view.isChecked = isChecked
        adapter?.onCheckedChanged(isChecked, position, view.getTagView())
    }

    /**
     * 是否达到最大的选择数量
     */
    fun isSelectMax(): Boolean {
        return if (selectMax > 0) {
            checkableGroup.checkedIds.size >= selectMax
        } else {
            false
        }
    }

    /**
     * 获取选中集合下标组
     */
    fun getCheckedTagOrders(): List<Int> {
        return checkableGroup.getCheckedIdsSortedByChildOrder(this)
    }

    /**
     * 获取选中集合下标
     */
    fun getCheckedTagOrder(): Int {
        return checkableGroup.singleCheckedId
    }

    /**
     * 清除所有选中数据
     */
    fun clearCheck() {
        checkableGroup.clearCheck()
    }

    /**
     * 设置单选是否为必选项
     */
    fun setSelectionRequired(selectionRequired: Boolean) {
        checkableGroup.isSelectionRequired = selectionRequired
    }

    /**
     * 设置是否单选
     */
    fun setSingleSelection(singleSelection: Boolean) {
        checkableGroup.isSingleSelection = singleSelection
    }

    /**
     * 设置最大选中数量
     */
    fun setSelectMax(selectMax: Int) {
        this.selectMax = selectMax
    }

    /**
     * 设置是否单选
     */
    override fun setSingleLine(singleLine: Boolean) {
        super.setSingleLine(singleLine)
    }

    /**
     * 设置TAG左右边距
     */
    fun setTagSpacingHorizontal(@Dimension tagSpacingHorizontal: Int) {
        if (this.tagSpacingHorizontal != tagSpacingHorizontal) {
            this.tagSpacingHorizontal = tagSpacingHorizontal
            itemSpacing = tagSpacingHorizontal
            requestLayout()
        }
    }

    /**
     * 设置TAG上下边距
     */
    fun setTagSpacingVertical(@Dimension tagSpacingVertical: Int) {
        if (this.tagSpacingVertical != tagSpacingVertical) {
            this.tagSpacingVertical = tagSpacingVertical
            lineSpacing = tagSpacingVertical
            requestLayout()
        }
    }

    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
        // the user listener is delegated to our pass-through listener
        passThroughListener.onHierarchyChangeListener = listener
    }

    private inner class PassThroughHierarchyChangeListener :
        OnHierarchyChangeListener {
        var onHierarchyChangeListener: OnHierarchyChangeListener? = null
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@TagFlowLayout && child is TagView) {
                child.setId(childCount - 1)
                checkableGroup.addCheckable(child)
            }
            onHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@TagFlowLayout && child is TagView) {
                checkableGroup.removeCheckable(child)
            }
            onHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }
}