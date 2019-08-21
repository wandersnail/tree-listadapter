package cn.wandersnail.adapter.tree;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * 节点数据模型
 * <p>
 * date: 2019/8/21 21:55
 * author: zengfansheng
 */
public class Node<T extends Node<T>> implements Comparable<T> {
    public int id;
    public int pId;
    public int level;
    public boolean isExpand;
    public List<T> childNodes;

    /**
     * @param id    自己的id
     * @param pId   上一层id
     * @param level 层级
     */
    public Node(int id, int pId, int level) {
        this(id, pId, level, false);
    }

    /**
     * @param id       自己的id
     * @param pId      上一层id
     * @param level    层级
     * @param isExpand 是否展开
     */
    public Node(int id, int pId, int level, boolean isExpand) {
        this.id = id;
        this.pId = pId;
        this.level = level;
        this.isExpand = isExpand;
    }

    /**
     * 获取自己的id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置自己的id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取上一层id
     */
    public int getpId() {
        return pId;
    }

    /**
     * 设置上一层id
     */
    public void setpId(int pId) {
        this.pId = pId;
    }

    /**
     * 获取层级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置层级
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 是否展开
     */
    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 设置是否展开
     */
    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    /**
     * 获取子节点
     */
    public List<T> getChildNodes() {
        return childNodes;
    }

    /**
     * 设置子节点
     */
    public void setChildNodes(List<T> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public int compareTo(@NonNull T other) {
        return id - other.id;
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
