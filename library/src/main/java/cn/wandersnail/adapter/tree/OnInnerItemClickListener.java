package cn.wandersnail.adapter.tree;

import android.view.View;
import android.widget.AdapterView;

/**
 * 不可展开的条目点击事件监听
 * <p>
 * date: 2019/8/21 22:09
 * author: zengfansheng
 */
public interface OnInnerItemClickListener<T extends Node<T>> {
    /**
     * @param node     被点击条目的数据
     * @param parent   被点击的父容器
     * @param view     被点击的View
     * @param position 被点击条目所在整个数据集合中的索引
     */
    void onClick(T node, AdapterView<?> parent, View view, int position);
}
