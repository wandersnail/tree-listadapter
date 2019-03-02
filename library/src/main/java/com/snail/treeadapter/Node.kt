package com.snail.treeadapter

import java.util.*

/**
 * 时间: 2017/10/19 15:50
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 数据bean
 */

open class Node<T : Node<T>> : Comparable<Any> {
    /** 自己的id  */
    var id: Int = 0
    /** 上一层id  */
    var pId: Int = 0
    /** 层级  */
    var level: Int = 0
    /** 是否展开  */
    var isExpand: Boolean = false
    /** 子节点  */
    var childNodes: MutableList<T>? = null

    constructor()

    constructor(id: Int, pId: Int, level: Int, isExpand: Boolean) {
        this.id = id
        this.pId = pId
        this.level = level
        this.isExpand = isExpand
    }

    override fun compareTo(other: Any): Int {        
        return if (other !is Node<*>) return 1 else id - other.id
    }

    fun hasChild(): Boolean {
        return childNodes != null && !childNodes!!.isEmpty()
    }

    fun addChild(node: T) {
        if (childNodes == null) {
            childNodes = ArrayList()
        }
        childNodes!!.add(node)
    }
}
