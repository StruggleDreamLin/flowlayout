package com.dreamlin.flowlayout;

public interface FlowListener {
    /**
     *  Item被选择时调用
     * @param flowItem
     */
    void onItemSelected(FlowItem flowItem);

    /**
     *  Item被取消选择时调用
     * @param flowItem
     */
    void onItemUnSelected(FlowItem flowItem);
}