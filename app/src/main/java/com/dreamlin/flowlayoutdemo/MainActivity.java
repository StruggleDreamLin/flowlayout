package com.dreamlin.flowlayoutdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.dreamlin.flowlayout.FlowItem;
import com.dreamlin.flowlayout.FlowLayout;
import com.dreamlin.flowlayout.FlowLayoutLearning;
import com.dreamlin.flowlayout.FlowListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = AppCompatActivity.class.getName();

    FlowLayout flowMix;
    FlowLayout flowSingle;
    FlowLayout flowMulti;
    FlowLayout flowNotSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {

        flowMix = findViewById(R.id.flow_mix);
        flowMix.addItem("混合");
        flowMix.addItem("会怎么样呢");
        flowMix.setItemStateListener(new FlowListener() {
            @Override
            public void onItemSelected(FlowItem flowItem) {
                Log.i(TAG, String.format("onItem(%d) selected:%s", flowItem.getPosition(), flowItem.getTitle()));
            }

            @Override
            public void onItemUnSelected(FlowItem flowItem) {
                Log.i(TAG, String.format("onItem(%d) unSelected:%s", flowItem.getPosition(), flowItem.getTitle()));
            }
        });

        flowSingle = findViewById(R.id.flow_single);
        flowSingle.addItem("谁说爱上一个不回家的人");
        flowSingle.addItem("唯一结局就是无止境的等");
        flowSingle.addItem("Oh ...");
        flowSingle.addItems(new String[]{"难道真没有别的可能", "这怎么成"});
        flowSingle.addItem(new FlowItem().setTitle("不可能").setEnable(false).setFontColor(0xFF969696));
        flowSingle.addItem("我不要");
        flowSingle.addItem("安稳");
        flowSingle.addItems("我不要", "牺牲");
        flowSingle.updateItem(new FlowItem().setTitle("唯一结局就是无止境的等").setEnable(false).setFontColor(0xFF969696));

        flowSingle.setItemStateListener(new FlowListener() {
            @Override
            public void onItemSelected(FlowItem flowItem) {
                Log.i(TAG, String.format("onItem(%d) selected:%s", flowItem.getPosition(), flowItem.getTitle()));
            }

            @Override
            public void onItemUnSelected(FlowItem flowItem) {
                Log.i(TAG, String.format("onItem(%d) unSelected:%s", flowItem.getPosition(), flowItem.getTitle()));
            }
        });

        flowMulti = findViewById(R.id.flow_multi);
        List<FlowItem> flowItems = new ArrayList<>();
        flowItems.add(new FlowItem().setTitle("凝不成")
                .setPadding(dp2px(20), dp2px(8), dp2px(20), dp2px(8)));
        flowItems.add(new FlowItem().setTitle("你喜欢的眉目")
                .setSelect(true));
        flowItems.add(new FlowItem().setTitle("却有一脸幸福 ")
                .setPadding(dp2px(20), dp2px(8), dp2px(20), dp2px(8)));
        flowItems.add(new FlowItem().setTitle("任着你驱逐"));
        flowItems.add(new FlowItem().setTitle("展不成"));
        flowItems.add(new FlowItem().setTitle("你迷恋的筋骨"));
        flowItems.add(new FlowItem().setTitle("倒有一腔哽咽"));
        flowItems.add(new FlowItem().setTitle("只为你劳碌"));
        flowItems.add(new FlowItem().setTitle("侥幸"));
        flowItems.add(new FlowItem().setTitle("幻觉浮出"));
        flowItems.add(new FlowItem().setTitle("仰着感触 窝着领悟"));
        flowItems.add(new FlowItem().setTitle("我们都是 被熔铸的动物"));
        flowItems.add(new FlowItem().setTitle("注定怀抱砂土"));
        flowMulti.addItems(flowItems);

        flowNotSelect = findViewById(R.id.flow_not_select);
        flowNotSelect.addItems("闻说你时常在下午", "来这里寄信件", "逢礼拜留连艺术展",
                "逢礼拜留连艺术展", "还是未间断", "何以我来回巡逻遍", "仍然和你擦肩",
                "还仍然在各自宇宙", "错过了春天");
    }

    static int dp2px(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
