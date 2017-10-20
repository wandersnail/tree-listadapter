package cn.zfs.treeadapter;

import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 时间: 2017/10/19 16:12
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 树结构ListView适配器。
 */

public abstract class TreeAdapter<T extends Node<T>> extends BaseAdapter {
    private List<T> totalNodes = new ArrayList<>();
    private List<T> showNodes = new ArrayList<>();
    private List<T> firstLevelNodes = new ArrayList<>();
    private SparseIntArray addedChildNodeIds = new SparseIntArray();
    private OnInnerItemClickListener<T> listener;

    public interface OnInnerItemClickListener<T> {
        void onClick(T node);
    }
    
    public TreeAdapter(List<T> nodes) {
        setNodes(nodes);        
    }
    
    public void setOnInnerItemClickListener(OnInnerItemClickListener<T> listener) {
        this.listener = listener;
    }
    
    public void setNodes(List<T> nodes) {
        if (nodes != null && !nodes.isEmpty()) {
            totalNodes = nodes;
            //过滤出显示的节点
            showNodes.clear();
            initNodes();
            addedChildNodeIds.clear();
            showNodes.addAll(firstLevelNodes);
            filterShowAndSortNodes();
            notifyDataSetChanged();
        }
    }
    
    private void initNodes() {
        //先循环一次，获取最小的level
        Integer level = null;
        for (T node : totalNodes) {
            if (level == null || level > node.level) {
                level = node.level;
            }
        }
        for (T node : totalNodes) {
            //过滤出最外层
            if (node.level == level) {
                firstLevelNodes.add(node);
            }
            //给节点添加子节点并排序
            for (T t : totalNodes) {
                if (node.id == t.pId && node.level != t.level) {
                    node.addChild(t);
                }
            }
            if (node.hasChild()) {
                Collections.sort(node.childNodes);
            }
        }    
        Collections.sort(firstLevelNodes);
    }
    
    private void filterShowAndSortNodes() {
        for (int i = 0; i < showNodes.size(); i++) {
            T node = showNodes.get(i);
            int value = addedChildNodeIds.get(node.id);
            if (value == 0 && node.isExpand && node.hasChild()) {
                List<T> list = new ArrayList<>(showNodes);
                list.addAll(i + 1, node.childNodes);
                showNodes = list;
                addedChildNodeIds.put(node.id, 1);
                filterShowAndSortNodes();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return showNodes.size();
    }

    @Override
    public T getItem(int position) {
        return showNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder<T> holder;
        if (convertView == null) {
            holder = getHolder();
        } else {
            holder = (Holder<T>) convertView.getTag();
        }
        holder.setData(showNodes.get(position));
        holder.position = position;
        View view = holder.getConvertView();
        view.setOnClickListener(clickListener);
        return view;
    }
    
    public abstract static class Holder<T> {
        private View convertView;
        int position;

        public Holder() {
            convertView = createConvertView();
            convertView.setTag(this);
        }

        public View getConvertView() {
            return convertView;
        }
        
        /**
         * 设置数据
         */
        protected abstract void setData(T node);

        /**
         * 创建界面
         */
        protected abstract View createConvertView();
    }

    protected abstract Holder<T> getHolder();
    
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                Holder<T> holder = (Holder<T>) v.getTag();
                T node = showNodes.get(holder.position);
                if (node.hasChild()) {
                    node.isExpand = !node.isExpand;
                    if (!node.isExpand) {
                        fold(node.childNodes);
                    }
                    showNodes.clear();
                    addedChildNodeIds.clear();
                    showNodes.addAll(firstLevelNodes);
                    filterShowAndSortNodes();
                    notifyDataSetChanged();
                } else {
                    listener.onClick(node);
                }
            }
        }
    };
    
    //递归收起节点及子节点
    private void fold(List<T> list) {
        for (T t : list) {
            t.isExpand = false;
            if (t.hasChild()) {
                fold(t.childNodes);
            }
        }
    }
}
