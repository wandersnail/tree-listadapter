package com.snail.treeadapter

import android.view.View
import android.widget.AdapterView

/**
 * 时间: 2017/12/5 11:11
 * 作者: zengfansheng
 * 功能:
 */

interface OnInnerItemClickListener<T : Node<T>> {
    fun onClick(node: T, parent: AdapterView<*>, view: View, position: Int)
}
