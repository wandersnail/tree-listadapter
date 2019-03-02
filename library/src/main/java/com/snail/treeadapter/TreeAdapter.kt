package com.snail.treeadapter

import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import java.util.*

/**
 * 时间: 2017/10/19 16:12
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 树结构ListView适配器。
 */

abstract class TreeAdapter<T : Node<T>>(lv: ListView, nodes: List<T>) : BaseAdapter(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private var totalNodes: List<T> = ArrayList()
    private var showNodes: MutableList<T> = ArrayList()
    private val firstLevelNodes = ArrayList<T>()
    private val addedChildNodeIds = SparseIntArray()
    private var listener: OnInnerItemClickListener<T>? = null
    private var longListener: OnInnerItemLongClickListener<T>? = null
    private var expandableListener: OnExpandableItemClickListerner<T>? = null
    private var expandableLongClickListener: OnExpandableItemLongClickListener<T>? = null

    init {
        setNodes(nodes)
        lv.onItemClickListener = this
        lv.onItemLongClickListener = this
    }

    fun setOnInnerItemClickListener(listener: OnInnerItemClickListener<T>?) {
        this.listener = listener
    }

    fun setOnInnerItemLongClickListener(listener: OnInnerItemLongClickListener<T>?) {
        longListener = listener
    }

    fun setOnExpandableItemClickListerner(listerner: OnExpandableItemClickListerner<T>?) {
        expandableListener = listerner
    }

    fun setOnExpandableItemLongClickListener(listerner: OnExpandableItemLongClickListener<T>?) {
        expandableLongClickListener = listerner
    }

    fun setNodes(nodes: List<T>) {
        totalNodes = nodes
        //过滤出显示的节点
        init()
        super.notifyDataSetChanged()
    }

    private fun init() {
        showNodes.clear()
        initNodes()
        addedChildNodeIds.clear()
        showNodes.addAll(firstLevelNodes)
        filterShowAndSortNodes()
    }

    override fun notifyDataSetChanged() {
        init()
        super.notifyDataSetChanged()
    }

    private fun initNodes() {
        firstLevelNodes.clear()
        //先循环一次，获取最小的level
        var level: Int? = null
        for (node in totalNodes) {
            if (level == null || level > node.level) {
                level = node.level
            }
        }
        for (node in totalNodes) {
            //过滤出最外层
            if (node.level == level) {
                firstLevelNodes.add(node)
            }
            //清空之前添加的
            if (node.hasChild()) {
                node.childNodes!!.clear()
            }
            //给节点添加子节点并排序
            for (t in totalNodes) {
                if (node.id == t.id && node !== t) {
                    throw IllegalArgumentException("id cannot be duplicated")
                }
                if (node.id == t.pId && node.level != t.level) {
                    node.addChild(t)
                }
            }
            if (node.hasChild()) {
                node.childNodes!!.sort()
            }
        }
        firstLevelNodes.sort()
    }

    private fun filterShowAndSortNodes() {
        for (i in showNodes.indices) {
            val node = showNodes[i]
            val value = addedChildNodeIds.get(node.id)
            if (value == 0 && node.isExpand && node.hasChild()) {
                val list = ArrayList(showNodes)
                list.addAll(i + 1, node.childNodes!!)
                showNodes = list
                addedChildNodeIds.put(node.id, 1)
                filterShowAndSortNodes()
                break
            }
        }
    }

    override fun getCount(): Int {
        return showNodes.size
    }

    override fun getItem(position: Int): T {
        return showNodes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: Holder<T> = if (convertView == null) {
            getHolder(position)
        } else {
            convertView.tag as Holder<T>
        }
        val node = showNodes[position]
        holder.setData(node, position)
        return holder.convertView
    }

    
    abstract class Holder<T> protected constructor() {
        val convertView: View

        init {
            convertView = createConvertView()
            convertView.tag = this
        }

        /**
         * 设置数据
         * @param node 节点数据
         * @param position 条目位置
         */
        abstract fun setData(node: T, position: Int)

        /**
         * 创建界面
         * @return 返回布局
         */
        protected abstract fun createConvertView(): View
    }

    protected abstract fun getHolder(position: Int): Holder<T>

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val node = getItem(position)
        if (node.hasChild()) {
            node.isExpand = !node.isExpand
            if (!node.isExpand) {
                fold(node.childNodes!!)
            }
            showNodes.clear()
            addedChildNodeIds.clear()
            showNodes.addAll(firstLevelNodes)
            filterShowAndSortNodes()
            super@TreeAdapter.notifyDataSetChanged()
            expandableListener?.onExpandableItemClick(node, parent, view, position)
        } else if (listener != null) {
            listener!!.onClick(node, parent, view, position)
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val node = getItem(position)
        if (node.hasChild()) {
            expandableLongClickListener?.onExpandableItemLongClick(node, parent, view, position)
        } else if (longListener != null) {
            longListener!!.onLongClick(node, parent, view, position)
        }
        return true
    }

    //递归收起节点及子节点
    private fun fold(list: List<T>) {
        for (t in list) {
            t.isExpand = false
            if (t.hasChild()) {
                fold(t.childNodes!!)
            }
        }
    }
}
