package com.snail.treeadapter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 时间: 2017/10/19 15:50
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 数据bean
 */

public class Node<T extends Node> implements Comparable<Node> {
    /** 自己的id */
    public int id;
    /** 上一层id */
    public int pId;
    /** 层级 */
    public int level;
    /** 是否展开 */
    public boolean isExpand;
    /** 子节点 */
    public List<T> childNodes;

    public Node() {
    }

    public Node(int id, int pId, int level, boolean isExpand) {
        this.id = id;
        this.pId = pId;
        this.level = level;
        this.isExpand = isExpand;
    }

    @Override
    public int compareTo(@NonNull Node o) {
        return id - o.id;
    }
    
    public boolean hasChild() {
        return childNodes != null && !childNodes.isEmpty();
    }
    
    public void addChild(T node) {
        if (childNodes == null) {
            childNodes = new ArrayList<>();
        }
        childNodes.add(node);
    }
}
