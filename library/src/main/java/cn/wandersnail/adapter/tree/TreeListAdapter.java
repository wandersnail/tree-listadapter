package cn.wandersnail.adapter.tree;

import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * 树形结构的ListView数据适配器
 * <p>
 * date: 2019/8/21 22:11
 * author: zengfansheng
 */
public abstract class TreeListAdapter<T extends Node<T>> extends BaseAdapter {
    private static final int TAG_KEY = 1098337442;
    private List<T> totalNodes = new ArrayList<>();
    private List<T> showNodes = new ArrayList<>();
    private List<T> firstLevelNodes = new ArrayList<>();
    private SparseIntArray addedChildNodeIds = new SparseIntArray();
    private OnInnerItemClickListener<T> listener;
    private OnInnerItemLongClickListener<T> longListener;
    private OnExpandableItemClickListerner<T> expandableListener;
    private OnExpandableItemLongClickListener<T> expandableLongClickListener;

    public TreeListAdapter(@NonNull AbsListView lv, @NonNull List<T> nodes) {
        lv.setAdapter(this);
        setNodes(nodes);
        AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
            T node = getItem(position);
            if (node.hasChild()) {
                node.isExpand = !node.isExpand;
                if (!node.isExpand) {
                    fold(node.childNodes);
                }
                showNodes.clear();
                addedChildNodeIds.clear();
                showNodes.addAll(firstLevelNodes);
                filterShowAndSortNodes();
                super.notifyDataSetChanged();
                if (expandableListener != null) {
                    expandableListener.onExpandableItemClick(node, parent, view, position);
                }
            } else if (listener != null) {
                listener.onClick(node, parent, view, position);
            }
        };
        lv.setOnItemClickListener(itemClickListener);
        AdapterView.OnItemLongClickListener itemLongClickListener = (parent, view, position, id) -> {
            T node = getItem(position);
            if (node.hasChild()) {
                if (expandableLongClickListener != null) {
                    expandableLongClickListener.onExpandableItemLongClick(node, parent, view, position);
                }                
            } else if (longListener != null) {
                longListener.onLongClick(node, parent, view, position);
            }
            return true;
        };
        lv.setOnItemLongClickListener(itemLongClickListener);
    }

    public void setOnInnerItemClickListener(OnInnerItemClickListener<T> listener) {
        this.listener = listener;
    }

    public void setOnInnerItemLongClickListener(OnInnerItemLongClickListener<T> listener) {
        longListener = listener;
    }

    public void setExpandableItemClickListerner(OnExpandableItemClickListerner<T> listener) {
        expandableListener = listener;
    }

    public void setExpandableItemLongClickListener(OnExpandableItemLongClickListener<T> listener) {
        expandableLongClickListener = listener;
    }

    public void setNodes(@NonNull List<T> nodes) {
        Objects.requireNonNull(nodes, "nodes can't be null");
        totalNodes = nodes;
        reset();
        super.notifyDataSetChanged();
    }

    private void reset() {
        showNodes.clear();
        initNodes();
        addedChildNodeIds.clear();
        showNodes.addAll(firstLevelNodes);
        filterShowAndSortNodes();
    }

    @Override
    public void notifyDataSetChanged() {
        reset();
        super.notifyDataSetChanged();
    }

    private void initNodes() {
        firstLevelNodes.clear();
        //先循环一次，获取最小的level
        int level = -1;
        for (T node : totalNodes) {
            if (level == -1 || level > node.level) {
                level = node.level;
            }
        }
        for (T node : totalNodes) {
            //过滤出最外层
            if (node.level == level) {
                firstLevelNodes.add(node);
            }
            //清空之前添加的
            if (node.hasChild()) {
                node.childNodes.clear();
            }
            //给节点添加子节点并排序
            for (T t : totalNodes) {
                if (node.id == t.id && node != t) {
                    throw new IllegalArgumentException("id cannot be duplicated");
                }
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

    //递归收起节点及子节点
    private void fold(List<T> list) {
        for (T t : list) {
            t.isExpand = false;
            if (t.hasChild()) {
                fold(t.childNodes);
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

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder<T> holder;
        if (view == null) {
            holder = getHolder(position);
            view = holder.createView();
            view.setTag(TAG_KEY, holder);
        } else {
            holder = (Holder<T>) view.getTag(TAG_KEY);
        }
        holder.onBind(getItem(position), position);
        return view;
    }

    public interface Holder<H> {
        /**
         * 和Adapter绑定了，可在此设置View的数据，更新View
         *
         * @param node     节点数据
         * @param position 条目位置
         */
        void onBind(H node, int position);

        /**
         * 创建界面
         */
        View createView();
    }
    
    protected abstract Holder<T> getHolder(int position);
}
