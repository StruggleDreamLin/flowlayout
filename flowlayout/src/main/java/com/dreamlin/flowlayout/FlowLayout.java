package com.dreamlin.flowlayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
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
    private int lastSelectedIndex = -1;
    private int currentSelectedIndex = -1;

    private List<FlowItem> flowItems = new ArrayList<>();
    private List<Rect> childRects = new ArrayList<>();
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayoutLearning);
        mFontColor = typedArray.getColor(R.styleable.FlowLayoutLearning_fontColor, Color.BLACK);
        mFontSize = typedArray.getDimensionPixelSize(R.styleable.FlowLayoutLearning_fontSize, dp2px(13));
        multiSelected = typedArray.getBoolean(R.styleable.FlowLayoutLearning_multiSelected, false);
        enableSelected = typedArray.getBoolean(R.styleable.FlowLayoutLearning_enableSelected, true);
        mLineSpacing = (int) typedArray.getDimension(R.styleable.FlowLayoutLearning_lineSpacing, dp2px(10));
        mColumnSpacing = (int) typedArray.getDimension(R.styleable.FlowLayoutLearning_columnSpacing, dp2px(10));
        mChildPaddingLeft = (int) typedArray.getDimension(R.styleable.FlowLayoutLearning_childPaddingLeft, dp2px(10));
        mChildPaddingTop = (int) typedArray.getDimension(R.styleable.FlowLayoutLearning_childPaddingTop, dp2px(3));
        mChildPaddingRight = (int) typedArray.getDimension(R.styleable.FlowLayoutLearning_childPaddingRight, dp2px(10));
        mChildPaddingBottom = (int) typedArray.getDimension(R.styleable.FlowLayoutLearning_childPaddingBottom, dp2px(3));
        mDefDrawable = typedArray.getResourceId(R.styleable.FlowLayoutLearning_defDrawable, R.drawable.item_selector);
        typedArray.recycle();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int childCount = getChildCount();
        int selfWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int selfWidthSize = MeasureSpec.getSize(widthMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        /*
         * 遍历确定所有子View的MeasureSpec
         */
        int widthUsed = paddingLeft + paddingRight;
        int heightUsed = paddingTop + paddingBottom;

        //记录上一行子View的最大高度
        int lineMaxHeight = 0;
        int lineMaxWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            //这里需要注意，widthUsed的限制，只需要在padding之内即可
            measureChildWithMargins(childAt, widthMeasureSpec, paddingLeft + paddingRight,
                    heightMeasureSpec, heightUsed);
            int childLeft, childTop, childRight, childBottom;
            if (selfWidthMode != MeasureSpec.UNSPECIFIED &&
                    widthUsed + childAt.getMeasuredWidth() > selfWidthSize) {
                heightUsed += lineMaxHeight + mLineSpacing;
                widthUsed = paddingLeft + paddingRight;
                lineMaxHeight = 0;
                childLeft = paddingLeft;
                measureChildWithMargins(childAt, widthMeasureSpec, paddingLeft + paddingRight,
                        heightMeasureSpec, heightUsed);
            } else {
                childLeft = widthUsed - paddingRight;
            }
            //fix paddingBottom error
            childTop = heightUsed - paddingBottom;
            childRight = childLeft + childAt.getMeasuredWidth();
            childBottom = childTop + childAt.getMeasuredHeight();
            Rect childRect;
            if (childRects.size() <= i) {
                childRect = new Rect();
                childRects.add(childRect);
            } else {
                childRect = childRects.get(i);
            }
            childRect.set(childLeft, childTop, childRight, childBottom);
            //最后加上列间距
            widthUsed += mColumnSpacing + childAt.getMeasuredWidth();
            if (widthUsed > lineMaxWidth) {
                lineMaxWidth = widthUsed;
            }
            //更新行高
            if (childAt.getMeasuredHeight() > lineMaxHeight) {
                lineMaxHeight = childAt.getMeasuredHeight();
            }
        }
        //加上最后一行的高度
        heightUsed += lineMaxHeight;
        setMeasuredDimension(lineMaxWidth, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.layout(childRects.get(i).left, childRects.get(i).top,
                    childRects.get(i).right, childRects.get(i).bottom);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 添加单个子Item
     *
     * @param title 子Item标题
     */
    public void addItem(String title) {
        addItemAt(flowItems.size(), new FlowItem().setTitle(title), true);
    }

    public void addItemAt(int index, String title) {
        if (index < 0 || index > flowItems.size())
            throw new IndexOutOfBoundsException(String.format("非法的插入位置{%d}", index));
        addItemAt(index, new FlowItem().setTitle(title));
    }

    /**
     * 添加Item
     *
     * @param flowItem
     */
    public void addItem(FlowItem flowItem) {
        addItemAt(flowItems.size(), flowItem, true);
    }

    /**
     * 添加Items
     *
     * @param titles
     */
    public void addItems(String... titles) {
        for (int i = 0; i < titles.length; i++) {
            addItem(titles[i]);
        }
    }

    public void addItemsAt(int index, String... titles) {
        if (index < 0 || index > flowItems.size())
            throw new IndexOutOfBoundsException(String.format("非法的插入位置{%d}", index));
        for (int i = 0; i < titles.length; i++) {
            addItemAt(index + i, titles[i]);
        }
    }

    /**
     * 添加多个子Item
     *
     * @param items
     */
    public void addItems(List<FlowItem> items) {
        for (int i = 0; i < items.size(); i++) {
            addItemAt(this.flowItems.size(), items.get(i), true);
        }
    }

    public void addItemsAt(int index, List<FlowItem> items) {
        if (index < 0 || index > flowItems.size())
            throw new IndexOutOfBoundsException(String.format("非法的插入位置{%d}", index));
        for (int i = 0; i < items.size(); i++) {
            addItemAt(index + i, items.get(i));
        }
    }

    public void addItemAt(int index, FlowItem flowItem) {
        addItemAt(index, flowItem, false);
    }

    private void addItemAt(int index, FlowItem flowItem, boolean isAppend) {
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
                if (isAppend)
                    index = flowItems.size();
            }

        }
        TextView textView = new TextView(getContext());
        LayoutParams layoutParams = new MarginLayoutParams(flowItem.getWidth(), flowItem.getHeight());
        textView.setLayoutParams(layoutParams);
        if (flowItem.getDrawable() != R.drawable.item_selector) {
            textView.setBackgroundResource(flowItem.getDrawable());
        } else {
            textView.setBackgroundResource(mDefDrawable);
        }
        textView.setEnabled(flowItem.isEnable());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, flowItem.getFontSize() > 0 ? flowItem.getFontSize() : mFontSize);
        textView.setTextColor(flowItem.getFontColor() != 0 ? flowItem.getFontColor() : mFontColor);
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
        if (index < 0 || index > flowItems.size())
            throw new IndexOutOfBoundsException(String.format("非法的插入位置{%d}", index));
        addView(textView, index);
        flowItems.add(index, flowItem);
        //View默认的select属性是false
        if (flowItem.isSelect()) {
            //这里修改为false，触发click后会置true
            flowItem.setSelect(false);
            textView.callOnClick();
        }
    }

    /**
     * 修改Item标题
     *
     * @param title
     * @param newTitle
     */
    public void updateItemTitle(String title, String newTitle) {
        for (int i = 0; i < flowItems.size(); i++) {
            if (title.equals(flowItems.get(i).getTitle())) {
                flowItems.get(i).setTitle(newTitle);
                updateItemTitle(i, newTitle);
            }
        }
    }

    public void updateItemTitle(int index, String newTitle) {
        if (index > 0 && index < flowItems.size()) {
            flowItems.get(index).setTitle(newTitle);
            View childAt = getChildAt(index);
            if (childAt instanceof TextView) {
                ((TextView) childAt).setText(newTitle);
            }
        }
    }

    /**
     * 根据title更新Item信息
     *
     * @param title
     * @param newItem
     */
    public void updateItem(String title, FlowItem newItem) {
        for (int i = 0; i < flowItems.size(); i++) {
            if (title.equals(flowItems.get(i).getTitle())) {
                updateItemAt(i, newItem);
            }
        }
    }

    /**
     * 更新Item信息
     *
     * @param flowItem
     */
    public void updateItem(FlowItem flowItem) {
        for (int i = 0; i < flowItems.size(); i++) {
            if (flowItem.getTitle().equals(flowItems.get(i).getTitle())) {
                updateItemAt(i, flowItem);
            }
        }
    }

    /**
     * 更新指定位置的Item信息
     *
     * @param index    item所在index
     * @param flowItem 要替换的item
     */
    public void updateItemAt(int index, FlowItem flowItem) {
        if (index > 0 && index < flowItems.size()) {
            flowItems.set(index, flowItem);
            View childAt = getChildAt(index);
            if (childAt instanceof TextView) {
                TextView textView = (TextView) childAt;
                if (flowItem.getDrawable() != R.drawable.item_selector) {
                    textView.setBackgroundResource(flowItem.getDrawable());
                } else {
                    textView.setBackgroundResource(mDefDrawable);
                }
                textView.setEnabled(flowItem.isEnable());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, flowItem.getFontSize() > 0 ? sp2px(flowItem.getFontSize()) : mFontSize);
                textView.setTextColor(flowItem.getFontColor() != 0 ? flowItem.getFontColor() : mFontColor);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(flowItem.getPaddingLeft() >= 0 ? flowItem.getPaddingLeft() : mChildPaddingLeft,
                        flowItem.getPaddingTop() >= 0 ? flowItem.getPaddingTop() : mChildPaddingTop,
                        flowItem.getPaddingRight() >= 0 ? flowItem.getPaddingRight() : mChildPaddingRight,
                        flowItem.getPaddingBottom() >= 0 ? flowItem.getPaddingBottom() : mChildPaddingBottom);
                textView.setText(flowItem.getTitle());
                if (flowItem.isSelect()) {
                    //这里修改为false，触发click后会置true
                    flowItem.setSelect(false);
                    textView.callOnClick();
                }
            }
        }
    }

    /**
     * 移除Item
     *
     * @param title
     */
    public void removeItem(String title) {
        for (int i = 0; i < flowItems.size(); i++) {
            if (title.equals(flowItems.get(i).getTitle()))
                removeItemAt(i);
        }
    }

    /**
     * 移除Item
     *
     * @param flowItem
     */
    public void removeItem(FlowItem flowItem) {
        for (int i = 0; i < flowItems.size(); i++) {
            if (flowItem.getTitle().equals(flowItems.get(i).getTitle()))
                removeItemAt(i);
        }
    }

    /**
     * 移除指定位置的Item
     *
     * @param index
     */
    public void removeItemAt(int index) {
        if (index < 0 || index > flowItems.size())
            throw new IndexOutOfBoundsException(String.format("非法的移除位置{%d}", index));
        flowItems.remove(index);
        super.removeViewAt(index);
    }

    /**
     * 移除所有Child
     */
    public void clear() {
        removeAllViews();
        flowItems.clear();
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
    public void setFontSize(int fontSize) {
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
     * 获取所有子Item数据
     *
     * @return
     */
    public List<FlowItem> getFlowItems() {
        return flowItems;
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

    /**
     * @return 返回当前item数量
     */
    public int getItemCount() {
        return flowItems.size();
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
