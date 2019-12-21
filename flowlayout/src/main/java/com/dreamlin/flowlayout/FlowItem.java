package com.dreamlin.flowlayout;

import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

public class FlowItem {

    private String title = "";

    private boolean select = false;

    //在FlowLayout里的位置
    private int position;

    //可选
    private @DrawableRes
    int drawable = R.drawable.item_selector;
    private @ColorInt
    int fontColor;
    private int fontSize;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT; //单位 dp
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT; //单位dp
    private int paddingLeft = -1;
    private int paddingTop = -1;
    private int paddingRight = -1;
    private int paddingBottom = -1;

    public String getTitle() {
        return title;
    }

    public FlowItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isSelect() {
        return select;
    }

    public FlowItem setSelect(boolean select) {
        this.select = select;
        return this;
    }

    void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public int getDrawable() {
        return drawable;
    }

    public FlowItem setDrawable(int drawable) {
        this.drawable = drawable;
        return this;
    }

    public int getFontColor() {
        return fontColor;
    }

    public FlowItem setFontColor(int fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public FlowItem setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public FlowItem setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public FlowItem setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public FlowItem setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        return this;
    }

    public FlowItem setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public FlowItem setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public FlowItem setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    public FlowItem setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }
}
