效果图：

![image](https://github.com/fszeng2011/treeadapter/raw/master/device-2017-10-20-152324.png)
![image](https://github.com/fszeng2011/treeadapter/raw/master/device-2017-10-20-152325.png)

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
        MyAdapter(List<Item> nodes) {
            super(nodes);
        }

        @Override
        protected Holder<Item> getHolder() {
            return new Holder<Item>() {
                private ImageView iv;
                private TextView tv;
                private View rootView;

                @Override
                protected void setData(Item node) {
                    iv.setVisibility(node.hasChild() ? View.VISIBLE : View.INVISIBLE);
                    iv.setBackgroundResource(node.isExpand ? R.mipmap.expand : R.mipmap.fold);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
                    params.leftMargin = (node.level + 1) * dip2px(20);
                    iv.setLayoutParams(params);
                    tv.setText(node.name);
                    if (node.hasChild()) {
                        switch(node.level) {
                            case 0:
                                rootView.setBackgroundColor(0xff30C251);
                                break;
                            case 1:
                                rootView.setBackgroundColor(0xff446F91);
                                break;
                        }
                    } else {
                        rootView.setBackgroundColor(0xffE36209);
                    }                 
                }

                @Override
                protected View createConvertView() {
                    rootView = View.inflate(MainActivity.this, R.layout.item_tree_list, null);
                    iv = (ImageView) rootView.findViewById(R.id.ivIcon);
                    tv = (TextView) rootView.findViewById(R.id.tvName);
                    return rootView;
                }
            };
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
    MyAdapter adapter = new MyAdapter(list);
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