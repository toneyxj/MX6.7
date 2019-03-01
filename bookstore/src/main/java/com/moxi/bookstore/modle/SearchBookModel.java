package com.moxi.bookstore.modle;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.File;

/**
 * 设置搜索图书model
 * Created by xj on 2017/7/31.
 */

public class SearchBookModel extends DataSupport{
    @Column(unique = true)
    public long id;
    /**父目录*/
    public String parentPath;
    /**添加目录的路径*/
    public String filePath;
    /**添加当前目录的类型，0代表全部，1代表当前目录*/
    public int addType;

    /**
     *
     * @param filePath 添加目录的路径
     * @param addType 添加当前目录的类型，0代表全部，1代表当前目录
     */
    public SearchBookModel init(String filePath, int addType) {
        this.filePath = filePath;
        this.addType = addType;
        return this;
    }

    /**
     *
     * @param filePath 添加目录的路径,默认添加类型为0：全部
     */
    public SearchBookModel init(String filePath) {
        this.filePath = filePath;
        addType=0;
        return this;
    }
    public String getName(){
        if (addType==0) {
            File file = new File(filePath);
            return file.getName();
        }else {
            return "当前目录";
        }
    }
    public String getParentPath(){
        if (addType==0) {
            File file = new File(filePath);
            return file.getParent();
        }else {
            return filePath;
        }
    }

    @Override
    public String toString() {
        return "SearchBookModel{" +
                "filePath='" + filePath + '\'' +
                ", addType=" + addType +
                '}';
    }
}
