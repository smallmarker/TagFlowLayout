package com.smallmarker.tagflowlayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.internal.MaterialCheckable


/**
 * @author   zl
 * @Date     2022/7/14
 **/
@SuppressLint("RestrictedApi")
class TagView : FrameLayout, MaterialCheckable<TagView> {

    private var onCheckedChangeListenerInternal: MaterialCheckable.OnCheckedChangeListener<TagView>? = null

    private var isChecked = false

    companion object {
        private val CHECK_STATE = intArrayOf(android.R.attr.state_checked)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun getTagView(): View {
        return getChildAt(0)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }

    override fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            refreshDrawableState()
            onCheckedChangeListenerInternal?.onCheckedChanged(this, isChecked)
        }
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    override fun setInternalOnCheckedChangeListener(listener: MaterialCheckable.OnCheckedChangeListener<TagView>?) {
        this.onCheckedChangeListenerInternal = listener
    }
}