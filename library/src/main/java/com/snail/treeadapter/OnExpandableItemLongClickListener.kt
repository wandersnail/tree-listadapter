package com.snail.treeadapter

import android.view.View
import android.widget.AdapterView

/**
 * 时间: 2017/12/5 11:20
 * 作者: zengfansheng
 * 功能:
 */

interface OnExpandableItemLongClickListener<T : Node<T>> {
    fun onExpandableItemLongClick(node: T, parent: AdapterView<*>, view: View, position: Int)
}
