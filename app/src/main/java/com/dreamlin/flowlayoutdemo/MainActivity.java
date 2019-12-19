package com.dreamlin.flowlayoutdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.dreamlin.flowlayout.FlowItem;
import com.dreamlin.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FlowLayout flowMix;
    FlowLayout flowSingle;
    FlowLayout flowMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        flowMix = findViewById(R.id.flow_mix);
        flowSingle = findViewById(R.id.flow_single);
        flowMulti = findViewById(R.id.flow_multi);

        flowMix.addChild("混合");
        flowMix.addChild("会怎么样呢");

        flowSingle.addChild("谁说爱上一个不回家的人");
        flowSingle.addChild("唯一结局就是无止境的等");
        flowSingle.addChild("Oh ...");
        flowSingle.addChild("不可能");
        flowSingle.addChild("难道真没有别的可能");
        flowSingle.addChild("这怎么成");
        flowSingle.addChild("我不要");
        flowSingle.addChild("安稳");
        flowSingle.addChild("我不要");
        flowSingle.addChild("牺牲");

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
        flowItems.add(new FlowItem().setTitle("不必痴迷 传说的甘露"));
        flowMulti.addChilds(flowItems);
    }

    static int dp2px(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}
