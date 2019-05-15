package com.dangdang.reader.moxiUtils;

/**
 * 设置接口
 * Created by Administrator on 2016/9/22.
 */
public interface SettingInterface {
    /**
     * 设置字体
     * @param font
     */
    public void settingFont(int font);

    /**
     * 设置显示文字大小
     * @param size
     */
    public void settingSize(int size);

    /**
     * 设置行间距
     * @param index 行间距级数 一共3级
     */
    public void settingLine(int index);
    /**
     * 设置页边距
     * @param index 页边距级数 一共3级
     */
    public void settingPage(int index);
    /**
     * 设置屏幕显示方向
     * @param index 屏幕显示方向：0竖屏，1横屏
     * */
    public void settingScreen(int index);

    /**
     * 设置阅读显示进度
     * @param index 阅读进度显示
     */
    public void settingProgress(int index);

    /**
     * 打开目录
     */
    public void startMuen();
    /**
     * 分享笔记
     */
    public void shareBiJi();
    /**
     * 结束阅读退出
     */
    public void endReader();

    /**
     * 截屏
     */
    public void onScreenShort();

    /**
     * 滑动跳转页数
     * @param page
     */
    void onScrollToPage(int page);

    /**
     * 章节跳转
     * @param lastPage 上一章传true，下一章传false
     */
    void onChapterJump(boolean lastPage);

    /**
     * 开启语音播报
     */
    void startYuyin();
}
