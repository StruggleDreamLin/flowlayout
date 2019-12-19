package com.dreamlin.flowlayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Size;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup implements View.OnClickListener {

    private boolean enableSelected = true;
    private boolean multiSelected = false;
    private boolean isFirstAppend = true;
    private int lastSelectedIndex = -1;
    private int currentSelectedIndex = -1;

    private List<FlowItem> flowItems = new ArrayList<>();
    private int mColumnSpacing = dp2px(10);
    private int mLineSpacing = dp2px(10);
    private int mChildPaddingLeft = dp2px(10);
    private int mChildPaddingTop = dp2px(3);
    private int mChildPaddingRight = dp2px(10);
    private int mChildPaddingBottom = dp2px(3);
    private @Size
    int mFontSize = sp2px(13);//默认13sp 单位px吧,为了统一
    private @ColorInt
    int mFontColor = Color.BLACK;
    @DrawableRes
    int mDefDrawable;
    private FlowListener mListener;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources(context, attrs);
    }

    private void initResources(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mFontColor = typedArray.getColor(R.styleable.FlowLayout_fontColor, Color.BLACK);
        mFontSize = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_fontSize, dp2px(13));
        multiSelected = typedArray.getBoolean(R.styleable.FlowLayout_multiSelected, false);
        enableSelected = typedArray.getBoolean(R.styleable.FlowLayout_enableSelected, true);
        mLineSpacing = (int) typedArray.getDimension(R.styleable.FlowLayout_lineSpacing, dp2px(10));
        mColumnSpacing = (int) typedArray.getDimension(R.styleable.FlowLayout_columnSpacing, dp2px(10));
        mChildPaddingLeft = (int) typedArray.getDimension(R.styleable.FlowLayout_childPaddingLeft, dp2px(10));
        mChildPaddingTop = (int) typedArray.getDimension(R.styleable.FlowLayout_childPaddingTop, dp2px(3));
        mChildPaddingRight = (int) typedArray.getDimension(R.styleable.FlowLayout_childPaddingRight, dp2px(10));
        mChildPaddingBottom = (int) typedArray.getDimension(R.styleable.FlowLayout_childPaddingBottom, dp2px(3));
        mDefDrawable = typedArray.getResourceId(R.styleable.FlowLayout_defDrawable, R.drawable.item_selector);
        typedArray.recycle();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int selfWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int selfWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int selfHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        /**
         * 遍历确定所有子View的MeasureSpec
         */
        int measureWidth = paddingLeft + paddingRight;
        int measureHeight = paddingTop + paddingBottom;

        //记录上一行子View的最大高度
        int lastRowHeight = 0;
        int row = 0; //行
        int column = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            LayoutParams layoutParams = childAt.getLayoutParams();
            int childWidthMeasureSpec;
            int childHeightMeasureSpec;
            switch (layoutParams.width) {

                case LayoutParams.MATCH_PARENT:
                    //当子View layout是match_parent时, viewgroup的限制是固定大小或者上限
                    //则子View铺满的其实就是viewgroup的固定大小或上限
                    if (selfWidthMode == MeasureSpec.EXACTLY ||
                            selfWidthMode == MeasureSpec.AT_MOST) {
                        int availableWidth = selfWidthSize - paddingLeft - paddingRight;
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY);
                    } else /*if (selfWidthMode == MeasureSpec.UNSPECIFIED)*/ {
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    }
                    break;
                case LayoutParams.WRAP_CONTENT:
                    //而当子View为WRAP_CONTENT时，其实隐含的条件就是不超过父View的大小 即AT_MOST
                    if (selfWidthMode == MeasureSpec.EXACTLY ||
                            selfWidthMode == MeasureSpec.AT_MOST) {
                        int availableWidth = selfWidthSize - paddingLeft - paddingRight;
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST);
                    } else /*if (selfWidthMode == MeasureSpec.UNSPECIFIED)*/ {
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    }
                    break;
                default:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
                    break;
            }

            switch (layoutParams.height) {

                case LayoutParams.MATCH_PARENT:
                    //当子View layout是match_parent时, viewgroup的限制是固定大小或者上限
                    //则子View铺满的其实就是viewgroup的固定大小或上限
                    if (selfHeightMode == MeasureSpec.EXACTLY ||
                            selfHeightMode == MeasureSpec.AT_MOST) {
                        int availableHeight = selfHeightSize - paddingTop - paddingBottom;
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.EXACTLY);
                    } else/* if (selfHeightMode == MeasureSpec.UNSPECIFIED) */ {
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    }
                    break;
                case LayoutParams.WRAP_CONTENT:
                    //而当子View为WRAP_CONTENT时，其实隐含的条件就是不超过父View的大小 即AT_MOST
                    if (selfHeightMode == MeasureSpec.EXACTLY ||
                            selfHeightMode == MeasureSpec.AT_MOST) {
                        int availableHeight = selfHeightSize - paddingTop - paddingBottom;
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST);
                    } else /*if (selfHeightMode == MeasureSpec.UNSPECIFIED) */ {
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    }
                    break;
                default:
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
                    break;
            }

            childAt.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            int childAtMeasuredHeight = childAt.getMeasuredHeight();
            int childAtMeasuredWidth = childAt.getMeasuredWidth();

            //需要换行
            if (selfWidthMode != MeasureSpec.UNSPECIFIED) {
                if (measureWidth + mColumnSpacing + childAtMeasuredWidth > selfWidthSize) {
                    measureWidth = paddingLeft + paddingRight;
                    measureHeight += lastRowHeight + mLineSpacing;
                    lastRowHeight = childAtMeasuredHeight;
                    column = 0;
                    row++;
                }
                if (column == 0)
                    measureWidth += childAtMeasuredWidth;
                else if (column > 0)
                    measureWidth += mColumnSpacing + childAtMeasuredWidth;
                column++;
                if (childAtMeasuredHeight > lastRowHeight) {
                    lastRowHeight = childAtMeasuredHeight;
                }
            } else {//这个什么条件下触发，包在HorizontalScrollView里可以，横向无限制
                if (i == 0)
                    measureWidth += childAtMeasuredWidth;
                else
                    measureWidth += mColumnSpacing + childAtMeasuredWidth;
                if (childAtMeasuredHeight > lastRowHeight)
                    lastRowHeight = childAtMeasuredHeight;
            }
        }
        //最后加上当前行高度
        measureHeight += lastRowHeight;

        //如果限制是固定值,遵从开发者的限定
        if (selfWidthMode == MeasureSpec.EXACTLY) {
            measureWidth = selfWidthSize;
        }
        //如果限制是上限
        if (selfWidthMode == MeasureSpec.AT_MOST) {
            //如果存在多行，说明超出了宽度限制，取宽度限制
            if (row > 0)
                measureWidth = selfWidthSize;
        }

        //其实宽度自己都限制好了
        int resolveWidthSize = resolveSize(measureWidth, widthMeasureSpec);
        int resolveHeightSize = resolveSize(measureHeight, heightMeasureSpec);
        setMeasuredDimension(resolveWidthSize, resolveHeightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int curLeft = getPaddingLeft();
        int curTop = getPaddingTop();

        int lastRowHeight = 0;
        int childCount = getChildCount();
        int measuredWidth = getMeasuredWidth();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int childAtMeasuredWidth = childAt.getMeasuredWidth();
            int childAtMeasuredHeight = childAt.getMeasuredHeight();

            //需要换行
            if (curLeft + childAtMeasuredWidth > measuredWidth - getPaddingRight()) {
                curLeft = getPaddingLeft();
                curTop += mLineSpacing + lastRowHeight;
                lastRowHeight = childAtMeasuredHeight;
            }
            childAt.layout(curLeft, curTop, curLeft + childAtMeasuredWidth, curTop + childAtMeasuredHeight);
            curLeft += mColumnSpacing + childAtMeasuredWidth;

            if (childAtMeasuredHeight > lastRowHeight) {
                lastRowHeight = childAtMeasuredHeight;
            }
        }
    }

    /**
     * 添加单个子Item
     *
     * @param title 子Item标题
     */
    public void addChild(String title) {
        addChild(new FlowItem().setTitle(title));
    }

    public void addChild(FlowItem flowItem) {
        //正常情况下，getChildCount == flowItems.size()
        if (getChildCount() > flowItems.size()) { //处理XML添加的Child
            for (int i = 0; i < getChildCount(); i++) {
                FlowItem xmlFlowItem = new FlowItem();
                View childAt = getChildAt(i);
                childAt.setTag(i);
                if (enableSelected)
                    childAt.setOnClickListener(this);
                if (childAt.getBackground() == null) {
                    childAt.setBackgroundResource(mDefDrawable);
                }
                int paddingLeft = childAt.getPaddingLeft() > 0 ? childAt.getPaddingLeft() : mChildPaddingLeft;
                int paddingTop = childAt.getPaddingTop() > 0 ? childAt.getPaddingTop() : mChildPaddingTop;
                int paddingRight = childAt.getPaddingRight() > 0 ? childAt.getPaddingRight() : mChildPaddingRight;
                int paddingBottom = childAt.getPaddingBottom() > 0 ? childAt.getPaddingBottom() : mChildPaddingBottom;
                childAt.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                xmlFlowItem.setTitle(childAt instanceof TextView ?
                        ((TextView) childAt).getText().toString() : "");
                xmlFlowItem.setPosition(i);
                flowItems.add(xmlFlowItem);
            }
        }
        TextView textView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        if (flowItem.getDrawable() != R.drawable.item_selector) {
            textView.setBackgroundResource(flowItem.getDrawable());
        } else {
            textView.setBackgroundResource(mDefDrawable);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize);
        textView.setTextColor(mFontColor);
        textView.setText(flowItem.getTitle());
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(flowItem.getPaddingLeft() >= 0 ? flowItem.getPaddingLeft() : mChildPaddingLeft,
                flowItem.getPaddingTop() >= 0 ? flowItem.getPaddingTop() : mChildPaddingTop,
                flowItem.getPaddingRight() >= 0 ? flowItem.getPaddingRight() : mChildPaddingRight,
                flowItem.getPaddingBottom() >= 0 ? flowItem.getPaddingBottom() : mChildPaddingBottom);
        int insertPosition = getChildCount();
        textView.setTag(insertPosition);
        flowItem.setPosition(insertPosition);
        if (enableSelected)
            textView.setOnClickListener(this);
        addView(textView);
        flowItems.add(flowItem);
        //View默认的select属性是false
        if (flowItem.isSelect()) {
            //这里修改为false，触发click后会置true
            flowItem.setSelect(false);
            textView.callOnClick();
        }
    }

    /**
     * 添加多个子Item
     *
     * @param flowItems
     */
    public void addChilds(List<FlowItem> flowItems) {
        for (int i = 0; i < flowItems.size(); i++) {
            addChild(flowItems.get(i));
        }
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        flowItems.get(tag).setSelect(!flowItems.get(tag).isSelect());
        v.setSelected(flowItems.get(tag).isSelect());
        if (v.isSelected()) {
            currentSelectedIndex = tag;
            if (mListener != null)
                mListener.onItemSelected(flowItems.get(tag));
        } else {
            currentSelectedIndex = -1;
            if (mListener != null)
                mListener.onItemUnSelected(flowItems.get(tag));
        }
        //如果是单选，取消上次选择的
        if (!multiSelected && lastSelectedIndex >= 0 &&
                lastSelectedIndex < getChildCount() &&
                lastSelectedIndex != currentSelectedIndex) {
            flowItems.get(lastSelectedIndex).setSelect(false);
            getChildAt(lastSelectedIndex).setSelected(false);
            if (mListener != null)
                mListener.onItemUnSelected(flowItems.get(lastSelectedIndex));
        }
        if (v.isSelected())
            lastSelectedIndex = tag;
        else
            lastSelectedIndex = -1;
    }

    /**
     * @param enableSelected 是否开启选择，默认开启
     */
    public void setEnableSelected(boolean enableSelected) {
        this.enableSelected = enableSelected;
    }

    /**
     * @param multiSelected 是否多选，默认单选
     */
    public void setMultiSelected(boolean multiSelected) {
        this.multiSelected = multiSelected;
    }

    /**
     * 设置列间距
     *
     * @param columnSpacing 水平间距
     */
    public void setColumnSpacing(int columnSpacing) {
        this.mColumnSpacing = columnSpacing;
    }

    /**
     * 设置行间距
     *
     * @param lineSpacing 垂直间距
     */
    public void setLineSpacing(int lineSpacing) {
        this.mLineSpacing = lineSpacing;
    }

    /**
     * 设置默认字体大小
     *
     * @param fontSize 单位sp
     */
    public void setmFontSize(int fontSize) {
        this.mFontSize = fontSize;
    }

    /**
     * 设置默认字体颜色
     *
     * @param fontColor 默认字体颜色
     */
    public void setFontColor(int fontColor) {
        this.mFontColor = fontColor;
    }

    /**
     * 设置Item状态监听
     *
     * @param listener
     */
    public void setItemStateListener(FlowListener listener) {
        this.mListener = listener;
    }

    /**
     * @return 返回当前选中的Item
     */
    public FlowItem getSelect() {
        if (currentSelectedIndex >= 0 && currentSelectedIndex < getChildCount()) {
            return flowItems.get(currentSelectedIndex);
        }
        return null;
    }

    /**
     * @return 返回当前选中的所有Item
     */
    public List<FlowItem> getSelects() {
        List<FlowItem> selects = new ArrayList<>();
        for (int i = 0; i < flowItems.size(); i++) {
            if (flowItems.get(i).isSelect()) {
                selects.add(flowItems.get(i));
            }
        }
        return selects;
    }

    static int dp2px(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    static int sp2px(int sp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics);
    }

}
