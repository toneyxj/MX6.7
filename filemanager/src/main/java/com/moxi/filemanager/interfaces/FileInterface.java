package com.moxi.filemanager.interfaces;

import com.moxi.filemanager.model.FileModel;

import java.io.File;

/**
 *fragment与主界面交互接口
 */
public interface FileInterface {
    /**
     * 页面索引
     * @param show 提示内容
     * @param sortType 排序方式
     */
    public void showIndex(String  show,int sortType);

    /**
     * 修改标题
     * @param title 标题名称
     */
    public void setTitle(String  title);

    /**
     * 点击文件
     */
    public void clickFile(FileModel model);

    /**
     * 单个文件复制
     * @param file 复制的文件
     */
    public void fileCopy(File file);
    /**
     *删除文件
     * @param file 单个文件删除
     */
    public void fileDelete(File file);
    /**
     *移动文件
     * @param file 单个文件移动
     */
    public void fileMove(File file);
    /**
     *重命名文件
     * @param file 单个文件重命名
     */
    public void fileRename(File file);

    /**
     * 判断是否需要刷新图片或者文件
     * @param file 输入判断的文件
     */
    public void judgeRefureshFileOrImage(File file);
    public void emailBack(String url);
}
