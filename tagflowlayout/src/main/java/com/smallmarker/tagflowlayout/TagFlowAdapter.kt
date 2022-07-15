package com.smallmarker.tagflowlayout

import android.view.View

private typealias ViewCallBack<T> = (parent: TagFlowLayout, position: Int, t: T) -> View

private typealias CheckedCallBack<T> = (position: Int, t: T) -> Boolean

private typealias OnCheckedChangedCallBack = (isChecked: Boolean, position: Int, view: View) -> Unit

private typealias NotifyCallBack = () -> Unit

/**
 * @author   zl
 * @Date     2022/7/14
 **/
class TagFlowAdapter<T>(private val tagList: MutableList<T>) {

    private var viewCallBack: ViewCallBack<T>? = null

    private var checkedCallBack: CheckedCallBack<T>? = null

    private var onCheckedChangedCallBack: OnCheckedChangedCallBack? = null

    private var notifyCallBack: NotifyCallBack? = null

    internal fun getView(parent: TagFlowLayout, position: Int, t: T): View? {
        return viewCallBack?.invoke(parent, position, t)
    }

    internal fun isChecked(position: Int, t: T): Boolean {
        return checkedCallBack?.invoke(position, t) ?: false
    }

    internal fun onCheckedChanged(isChecked: Boolean, position: Int, view: View) {
        onCheckedChangedCallBack?.invoke(isChecked, position, view)
    }

    /**
     * 设置TAG
     */
    fun setView(callback: ViewCallBack<T>) {
        this.viewCallBack = callback
    }

    /**
     * 设置当前状态
     */
    fun setChecked(callback: CheckedCallBack<T>) {
        this.checkedCallBack = callback
    }

    /**
     * 状态监听
     */
    fun setCheckedChanged(callBack: OnCheckedChangedCallBack) {
        this.onCheckedChangedCallBack = callBack
    }

    fun getItem(position: Int): T {
        return tagList[position]
    }

    internal fun setNotifyDataSetChange(callback: NotifyCallBack) {
        this.notifyCallBack = callback
    }

    fun notifyDataSetChange() {
        notifyCallBack?.invoke()
    }

    fun getCount(): Int {
        return tagList.size
    }

    companion object {
        fun <T> create(tagList: MutableList<T>, function: TagFlowAdapter<T>.() -> Unit) =
            TagFlowAdapter(tagList).also(function)
    }
}