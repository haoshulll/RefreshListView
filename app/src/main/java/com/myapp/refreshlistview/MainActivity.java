package com.myapp.refreshlistview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myapp.refreshlistview.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private List<String> mData;
    private MyAdapter mAdapter;
    private Handler handler=new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            //访问网络，获取数据
            mData.add(0, "特价商品"+3);
            mData.add(0, "特价商品"+2);
            mData.add(0, "特价商品"+1);
            mAdapter.notifyDataSetChanged();
            refreshListView.refreshFinish();
        };
    };
    private RefreshListView refreshListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        refreshListView = (RefreshListView) findViewById(R.id.refresh_lstview);
        mData = new ArrayList<String>();
        for(int i=1;i<=30;i++){
            mData.add("特大喜讯-打折促销商品"+i);
        }

        // 设置适配器 ，展示数据
        refreshListView.setAdapter(mAdapter=new MyAdapter());
        //监听 刷新事件
        refreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

            @Override
            public void refresh() {

                //模拟网络访问耗费3秒，延迟3秒调用刷新结束的方法
                //刷新已经结束
//				handler.sendEmptyMessageDelayed(0, 3000);
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mData.add(0, "高价商品"+3);
                        mData.add(0, "高价商品"+2);
                        mData.add(0, "高价商品"+1);
                        mAdapter.notifyDataSetChanged();
                        refreshListView.refreshFinish();

                    }
                }, 3000);
            }
        });


    }



    //自定义适配器
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv=new TextView(context);
            tv.setText(mData.get(position));
            tv.setTextSize(18);
            tv.setPadding(10, 10, 0, 10);//内边距
            tv.setTextColor(Color.BLUE);
            return tv;
        }

    }

}

