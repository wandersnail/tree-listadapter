# 使用方法
1. 因为使用了jdk8的一些特性，需要在module的build.gradle里添加如下配置：
```
//纯java的项目
android {
	compileOptions {
		sourceCompatibility JavaVersion.VERSION_1_8
		targetCompatibility JavaVersion.VERSION_1_8
	}
}

//有kotlin的项目还需要在project的build.gradle里添加
allprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        kotlinOptions {
            jvmTarget = '1.8'
            apiVersion = '1.3'
            languageVersion = '1.3'
        }
    }
}
```

2. module的build.gradle中的添加依赖，自行修改为最新版本，同步后通常就可以用了：
```
dependencies {
	...
	implementation 'cn.wandersnail:tree-listadapter:latestVersion'
}
```

3. 如果从jcenter下载失败。在project的build.gradle里的repositories添加内容，最好两个都加上，添加完再次同步即可。
```
allprojects {
	repositories {
		...
		mavenCentral()
		maven { url 'https://dl.bintray.com/wandersnail/androidx/' }
	}
}
```

## 代码托管
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.wandersnail/tree-listadapter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.wandersnail/tree-listadapter)
[![Download](https://api.bintray.com/packages/wandersnail/androidx/tree-listadapter/images/download.svg)](https://bintray.com/wandersnail/androidx/tree-listadapter/_latestVersion)

## 效果图

![image](https://github.com/wandersnail/tree-listadapter/raw/master/device-2017-10-20-152326.png)
![image](https://github.com/wandersnail/tree-listadapter/raw/master/device-2017-10-20-152327.png)

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

    private class MyAdapter extends TreeListAdapter<Item> {
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
                        public void onBind(Item node) {
                            iv.setVisibility(node.hasChild() ? View.VISIBLE : View.INVISIBLE);
                            iv.setBackgroundResource(node.isExpand ? R.mipmap.expand : R.mipmap.fold);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
                            params.leftMargin = (node.level + 1) * dip2px(20);
                            iv.setLayoutParams(params);
                            tv.setText(node.name);
                        }

                        @Override
                        public View createView() {
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
                        public void onBind(Item node) {
                            tv.setText(node.name);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                            params.leftMargin = (node.level + 3) * dip2px(20);
                            tv.setLayoutParams(params);
                        }

                        @Override
                        public View createView() {
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
    final MyAdapter adapter = new MyAdapter(lv, list);
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
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            list.add(new Item(9, 7, 2, false, "a"));
            adapter.notifyDataSetChanged();
        }
    }, 2000);
