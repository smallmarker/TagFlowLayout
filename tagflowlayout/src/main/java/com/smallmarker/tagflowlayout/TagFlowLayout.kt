package com.smallmarker.tagflowlayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.Dimension
import androidx.core.content.res.use
import androidx.core.view.ViewCompat
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

    var adapter: TagFlowAdapter<*>? = null
        set(value) {
            field = value?.apply {
                setNotifyDataSetChange {
                    changeAdapter()
                }
            }
            field?.notifyDataSetChange()
        }

    @Dimension
    private var chipSpacingHorizontal = 0

    @Dimension
    private var chipSpacingVertical = 0

    private var tagClickListener: TagClickListener? = null

    private var checkedChangedListener: CheckedChangedListener? = null

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
            val chipSpacing = it.getDimensionPixelOffset(R.styleable.TagFlowLayout_chipSpacing, 0);
            setChipSpacingHorizontal(
                it.getDimensionPixelOffset(
                    R.styleable.TagFlowLayout_chipSpacingHorizontal,
                    chipSpacing
                )
            )
            setChipSpacingVertical(
                it.getDimensionPixelOffset(
                    R.styleable.TagFlowLayout_chipSpacingVertical,
                    chipSpacing
                )
            )
            isSingleLine = it.getBoolean(R.styleable.TagFlowLayout_singleLine, false)
            setSingleSelection(it.getBoolean(R.styleable.TagFlowLayout_singleSelection, false))
            setSelectionRequired(it.getBoolean(R.styleable.TagFlowLayout_selectionRequired, false))
        }
        checkableGroup.setOnCheckedStateChangeListener {
            checkedChangedListener?.invoke(
                this,
                checkableGroup.getCheckedIdsSortedByChildOrder(this)
            )
        }
        super.setOnHierarchyChangeListener(passThroughListener)
    }

    fun setOnTagClickListener(tagClickListener: TagClickListener) {
        this.tagClickListener = tagClickListener
    }

    fun setCheckedChangedListener(checkedChangedListener: CheckedChangedListener) {
        this.checkedChangedListener = checkedChangedListener
    }

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
                    addView(TagView(context).apply {
                        addView(child)
                        isChecked = it.isChecked(i, item)
                        setOnClickListener {
                            this.isChecked = !isChecked
                            tagClickListener?.invoke(this, i, this@TagFlowLayout)
                        }
                    })
                }
            }
        }
    }

    fun getCheckedChipIds(): List<Int> {
        return checkableGroup.getCheckedIdsSortedByChildOrder(this)
    }

    fun getCheckedChipId(): Int {
        return checkableGroup.singleCheckedId
    }

    fun clearCheck() {
        checkableGroup.clearCheck()
    }

    fun setSelectionRequired(selectionRequired: Boolean) {
        checkableGroup.isSelectionRequired = selectionRequired
    }

    fun setSingleSelection(singleSelection: Boolean) {
        checkableGroup.isSingleSelection = singleSelection
    }

    fun setChipSpacingHorizontal(@Dimension chipSpacingHorizontal: Int) {
        if (this.chipSpacingHorizontal != chipSpacingHorizontal) {
            this.chipSpacingHorizontal = chipSpacingHorizontal
            itemSpacing = chipSpacingHorizontal
            requestLayout()
        }
    }

    fun setChipSpacingVertical(@Dimension chipSpacingVertical: Int) {
        if (this.chipSpacingVertical != chipSpacingVertical) {
            this.chipSpacingVertical = chipSpacingVertical
            lineSpacing = chipSpacingVertical
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
                var id = child.getId()
                // generates an id if it's missing
                if (id == NO_ID) {
                    id = ViewCompat.generateViewId()
                    child.setId(id)
                }
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