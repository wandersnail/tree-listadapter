效果图：

![image](https://github.com/fszeng2011/treeadapter/raw/master/device-2017-10-20-152326.png)
![image](https://github.com/fszeng2011/treeadapter/raw/master/device-2017-10-20-152327.png)

支持多层级，条目内容自定义。

1.定义数据模型
    
    private class Item extends Node<Item> {
        String name;

        Item(int id, int pId, int level, boolean isExpand, String name) {
            super(id, pId, level, isExpand);
            this.name = name;
        }
    }

2.继承TreeAdapter自己实现条目内容

    private class MyAdapter extends TreeAdapter<Item> {
        MyAdapter(ListView lv, List<Item> nodes) {
            super(lv, nodes);
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        /**
         * 获取当前位置的条目类型
         */
        @Override
        public int getItemViewType(int position) {
            if (getItem(position).hasChild()) {
                return 1;
            }
            return 0;
        }
        
        @Override
        protected Holder<Item> getHolder(int position) {
            switch(getItemViewType(position)) {
                case 1:
                    return new Holder<Item>() {
                        private ImageView iv;
                        private TextView tv;

                        @Override
                        protected void setData(Item node) {
                            iv.setVisibility(node.hasChild() ? View.VISIBLE : View.INVISIBLE);
                            iv.setBackgroundResource(node.isExpand ? R.mipmap.expand : R.mipmap.fold);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
                            params.leftMargin = (node.level + 1) * dip2px(20);
                            iv.setLayoutParams(params);
                            tv.setText(node.name);
                        }

                        @Override
                        protected View createConvertView() {
                            View view = View.inflate(MainActivity.this, R.layout.item_tree_list_has_child, null);
                            iv = (ImageView) view.findViewById(R.id.ivIcon);
                            tv = (TextView) view.findViewById(R.id.tvName);
                            return view;
                        }
                    };
                default:
                    return new Holder<Item>() {
                        private TextView tv;
                        
                        @Override
                        protected void setData(Item node) {
                            tv.setText(node.name);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                            params.leftMargin = (node.level + 3) * dip2px(20);
                            tv.setLayoutParams(params);
                        }

                        @Override
                        protected View createConvertView() {
                            View view = View.inflate(MainActivity.this, R.layout.item_tree_list_no_child, null);
                            tv = (TextView) view.findViewById(R.id.tvName);
                            return view;
                        }
                    };
            }
        }
    }

3.设置适配器数据源

    List<Item> list = new ArrayList<>();
    list.add(new Item(0, 0, 0, true, "Android"));
    list.add(new Item(1, 0, 1, false, "Service"));
    list.add(new Item(2, 0, 1, false, "Activity"));
    list.add(new Item(3, 0, 1, false, "Receiver"));
    list.add(new Item(4, 0, 0, false, "Java Web"));
    list.add(new Item(5, 4, 1, false, "CSS"));
    list.add(new Item(6, 4, 1, false, "Jsp"));
    list.add(new Item(7, 4, 1, false, "Html"));
    list.add(new Item(8, 7, 2, false, "p"));
    MyAdapter adapter = new MyAdapter(lv, list);
    adapter.setOnInnerItemClickListener(new TreeAdapter.OnInnerItemClickListener<Item>() {
        @Override
        public void onClick(Item node) {
            Toast.makeText(MainActivity.this, "click: " + node.name, Toast.LENGTH_SHORT).show();
        }
    });
    adapter.setOnInnerItemLongClickListener(new TreeAdapter.OnInnerItemLongClickListener<Item>() {
        @Override
        public void onLongClick(Item node) {
            Toast.makeText(MainActivity.this, "long click: " + node.name, Toast.LENGTH_SHORT).show();
        }
    });
    lv.setAdapter(adapter);
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            list.add(new Item(9, 7, 2, false, "a"));
            adapter.notifyDataSetChanged();
        }
    }, 2000);