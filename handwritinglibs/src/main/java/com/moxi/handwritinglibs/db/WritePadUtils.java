package com.moxi.handwritinglibs.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.moxi.handwritinglibs.utils.LLog;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.crud.DataSupport.findBySQL;

/**
 * 手写板文件数据保存操作类
 * Created by 夏君 on 2017/2/9.
 */
public class WritePadUtils {
    private static WritePadUtils instatnce = null;
    private Thread.UncaughtExceptionHandler exception;

    public static WritePadUtils getInstance() {
        if (instatnce == null) {
            synchronized (WritePadUtils.class) {
                if (instatnce == null) {
                    instatnce = new WritePadUtils();
                }
            }
        }
        return instatnce;
    }

    /**
     * 普通手写获得字符数据
     */
    private final String writeAll = "id,name,saveCode,isFolder,parentCode,_index,extend,changeTime";
    private final String writeAndImage = "id,name,saveCode,isFolder,parentCode,_index,extend,imageContent,changeTime";
    private final String writeCommon= "id,name,saveCode,imageContent,changeTime";
    private final String writeImage = "imageContent";
    private final String extend = "extend";
    public final String tableName = "WritPadModel";

    /**
     * 保存数据
     *
     * @param model 保存的数据model
     * @return 是否保存成功
     */
    public boolean saveData(WritPadModel model) {
        if (null==model||model.saveCode.isEmpty()) {
            Log.e("WritePadUtils-saveData","传入数据有误请检查数据");
            return false;
        }
        boolean isSucess;
        long id=judgeDataExist(model.saveCode,model.isFolder,model._index);
        if (id!=-1){
            //已经存在
            isSucess=updataModel(model,id);
        }else {
            //未保存数据
            isSucess= model.save();
        }
        return isSucess;
    }

    /**
     * 普通保存数据
     * @param model 保存的数据model
     * @return 是否保存成功
     */
    public boolean saveData(WriteCommonModel model) {
        if (null==model||model.saveCode.isEmpty()) {
            Log.e("WritePadUtils-saveData","传入数据有误请检查数据");
            return false;
        }
        boolean isSucess=false;
        long id=judgeDataExist(model.saveCode,1,0);
        //数据装换
        WritPadModel writPadMode=new WritPadModel(model.name,model.saveCode,model.imageContent,model.changeTime);
        if (id!=-1){
            //已经存在
            isSucess=updataModel(writPadMode,id);
        }else {
            //未保存数据
            isSucess= writPadMode.save();
        }
        return isSucess;
    }

    /**
     * 更新数据
     * @param model 更新数据model
     * @return 返回是否更新成功
     */
    public boolean updataModel(WritPadModel model,long id){
        ContentValues values=new ContentValues();
        values.put("name",model.name);
        values.put("saveCode",model.saveCode);
        values.put("isFolder",model.isFolder);
        values.put("parentCode",model.parentCode);
        values.put("_index",model._index);
        values.put("extend",model.extend);
        values.put("imageContent",model.imageContent);
        values.put("changeTime",System.currentTimeMillis());
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }
    /**
     * 更新数据
     * @return 返回是否更新成功
     */
    public boolean updataSaveCode(String parentCode,String saveCode,long id){
        ContentValues values=new ContentValues();
        values.put("saveCode",saveCode);
        values.put("parentCode",parentCode);
        values.put("changeTime",System.currentTimeMillis());
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }

    /**
     * 更改文件索引
     * @param index 索引更新
     * @param id 更改数据id
     * @return 返回是否更改成功
     */
    public boolean upDateIndex(long id,int index){
        ContentValues values=new ContentValues();
        values.put("_index",index);
        values.put("changeTime",System.currentTimeMillis());
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }
    /**
     * 更改文件名字
     * @param name 更改名字
     * @param id 更改数据id
     * @return 返回是否更改成功
     */
    public boolean upDateName(String name,long id){
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("changeTime",System.currentTimeMillis());
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }
    /**
     * 更改文件绘制内容
     * @param imageContent 更改内容字符串
     * @param id 更改数据id
     * @return 返回是否更改成功
     */
    public boolean upDateContent(String imageContent,long id){
        ContentValues values=new ContentValues();
        values.put("imageContent",imageContent);
        values.put("changeTime",System.currentTimeMillis());
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }
    /**
     * 更改绘制背景
     * @param extend 更改内容字符串
     * @param id 更改数据id
     * @return 返回是否更改成功
     */
    public boolean upDateExtend(String extend,long id){
        ContentValues values=new ContentValues();
        values.put("extend",extend);
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }

    /**
     * 替换所有扩展字段
     * @param extend
     * @param saveCode savecode一致
     * @return
     */
    public boolean updateAllExtend(String extend,String saveCode){
        ContentValues values=new ContentValues();
        values.put("extend",extend);
        String sql=" saveCode='"+saveCode+"'";
        return (DataSupport.updateAll(WritPadModel.class,values,sql)>0);
    }
    /**
     * 更改绘制背景
     * @param id 更改数据id
     * @return 返回是否更改成功
     */
    public boolean upDateTime(long id){
        if (id<0)return false;
        ContentValues values=new ContentValues();
        values.put("changeTime",System.currentTimeMillis());

        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }
    public boolean upDateTimeAndExtend(long id,String extend){
        if (id<0)return false;
        ContentValues values=new ContentValues();
        values.put("changeTime",System.currentTimeMillis());
        values.put("extend",extend);
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }
    /**
     * 更改绘制背景
     * @param id 更改数据id
     * @return 返回是否更改成功
     */
    public boolean upFolder(long id,String parentCode,String saveCode){
        ContentValues values=new ContentValues();
        values.put("parentCode",parentCode);
        values.put("saveCode",saveCode);
        return (DataSupport.update(WritPadModel.class,values,id)>0);
    }

    /**
     * 删除目录下的所有文件
     * @param saveCode 文件唯一标示
     * @return
     */
    public boolean deleteAllSaveCode(String saveCode){
        String sql=" saveCode='"+saveCode+"'";
        return (DataSupport.deleteAll(WritPadModel.class,sql)>0);
    }
    /**
     * 删除目录下的所有文件
     * @param parentCode 目录信息
     * @return
     */
    public boolean deleteAllParent(String parentCode){
        String sql=" parentCode='"+parentCode+"'";
        return (DataSupport.deleteAll(WritPadModel.class,sql)>0);
    }
    /**
     * 删除指定id文件
     * @param id 删除 的文件id
     * @return
     */
    public boolean deleteFileById(long id){
        return DataSupport.delete(WritPadModel.class,id)>0;
    }

    /**
     * 获得某个父分类下的所有文件信息
     * @param parentCode1 父分类标志
     * @return 返回查询后的数据集合，集合里面没有查询出imageContent=null，需要单独查询解析
     */
    public List<WritPadModel> getListMirks(String parentCode1){
        List<WritPadModel> list=new ArrayList<>();
        list.add(new WritPadModel(-1,"新增界面","",-1,parentCode1,0,"0",null,0l));
        Cursor cursor= findBySQL("select " + writeAll + " from " + tableName + " where parentCode='" + parentCode1 + "' and _index=0 order by changeTime desc");
        if (null==cursor)return list;
        while (cursor.moveToNext()){
             long id=cursor.getLong(0);
             String name=cursor.getString(1);
             String saveCode=cursor.getString(2);
             int isFolder=cursor.getInt(3);
             String parentCode=cursor.getString(4);
             int index=cursor.getInt(5);
             String extend=cursor.getString(6);
             Long changeTime=cursor.getLong(7);
            list.add(new WritPadModel(id,name,saveCode,isFolder,parentCode,index,extend,null,changeTime));
        }
        cursor.close();
        return list;
    }

    /**
     * 获得某个父分类下的所有文件信息，包括里面的子分类信息
     * @param parentCode1 父分类标志
     * @return 返回查询后的数据集合，集合里面没有查询出imageContent=null，需要单独查询解析
     */
    public List<WritPadModel> getMirkAllFiles(String parentCode1 ){
        List<WritPadModel> list=new ArrayList<>();
        Cursor cursor= findBySQL("select " + writeAll + " from " + tableName + " where parentCode='" + parentCode1 + "'"+" OR parentCode like '" + parentCode1 + "/%'");
        if (null==cursor)return list;
        while (cursor.moveToNext()){
             long id=cursor.getLong(0);
             String name=cursor.getString(1);
             String saveCode=cursor.getString(2);
             int isFolder=cursor.getInt(3);
             String parentCode=cursor.getString(4);
             int index=cursor.getInt(5);
            String extend=cursor.getString(6);
            Long changeTime=cursor.getLong(7);
            list.add(new WritPadModel(id,name,saveCode,isFolder,parentCode,index,extend,null,changeTime));
        }
        cursor.close();
        return list;
    }

    /**
     * 获得所有文件信息
     */
    public List<WritPadModel> getAllListMirks( ){
        List<WritPadModel> list=new ArrayList<>();
        Cursor cursor= findBySQL("select " + writeAll + " from " + tableName + " order by changeTime desc");
        if (null==cursor)return list;
        while (cursor.moveToNext()){
             long id=cursor.getLong(0);
             String name=cursor.getString(1);
             String saveCode=cursor.getString(2);
             int isFolder=cursor.getInt(3);
             String parentCode=cursor.getString(4);
             int index=cursor.getInt(5);
            String extend=cursor.getString(6);
            Long changeTime=cursor.getLong(7);
            list.add(new WritPadModel(id,name,saveCode,isFolder,parentCode,index,extend,null,changeTime));
        }
        cursor.close();
        return list;
    }

    /**
     * 获得某一张图片信息
     * @param saveCode1 唯一标识
     * @param _index 图片索引
     * @return
     */
    public WritPadModel getWritPadModel(String saveCode1,int _index){
        Cursor cursor= findBySQL("select " + writeAll + " from " + tableName + " where saveCode='" + saveCode1 + "' and isFolder=1 and _index="+_index);
        WritPadModel model=null;
        if (null==cursor)return model;
        if (cursor.moveToNext()){
            long id=cursor.getLong(0);
            String name=cursor.getString(1);
            String saveCode=cursor.getString(2);
            int isFolder=cursor.getInt(3);
            String parentCode=cursor.getString(4);
            int index=cursor.getInt(5);
            String extend=cursor.getString(6);
            Long changeTime=cursor.getLong(7);
            model=new WritPadModel(id,name,saveCode,isFolder,parentCode,index,extend,null,changeTime);
        }
        cursor.close();
            return model;
    }
    /**
     * 获得某个文件里面的信息集合
     * @param saveCode1 父分类标志
     * @return 返回查询后的数据集合，集合里面没有查询出imageContent=null，需要单独查询解析
     */
    public List<WritPadModel> getListFiles( String saveCode1){
        List<WritPadModel> list=new ArrayList<>();
        Cursor cursor= findBySQL("select " + writeAll + " from " + tableName + " where saveCode='" + saveCode1 + "' and isFolder=1 order by _index ASC");
        if (null==cursor)return list;
        while (cursor.moveToNext()){
             long id=cursor.getLong(0);
             String name=cursor.getString(1);
             String saveCode=cursor.getString(2);
             int isFolder=cursor.getInt(3);
             String parentCode=cursor.getString(4);
             int index=cursor.getInt(5);
            String extend=cursor.getString(6);
            Long changeTime=cursor.getLong(7);
            list.add(new WritPadModel(id,name,saveCode,isFolder,parentCode,index,extend,null,changeTime));
        }
        cursor.close();
        return list;
    }
    public void updateIndex(String saveCode,int index){
        String sql="update "+tableName+" set _index=_index+1 where saveCode='" + saveCode + "'and _index >="+index+" and isFolder=1";
        try {
            Connector.getDatabase().execSQL(sql);
        }catch (SQLException e){
            e.printStackTrace();
            updateIndex(saveCode,index);
        }

    }
    /**
     * 获得某个文件里面的信息集合
     * @param saveCode1 父分类标志
     * @param _index  index值之上的数据
     * @return 返回查询后的数据集合，集合里面没有查询出imageContent=null，需要单独查询解析
     */
    public List<WritPadModel> getListFiles( String saveCode1,int _index){
        List<WritPadModel> list=new ArrayList<>();
        Cursor cursor= findBySQL("select " + writeAll + " from " + tableName + " where saveCode='" + saveCode1 + "'and _index >="+_index+" and isFolder=1 order by _index ASC");
        if (null==cursor)return list;
        while (cursor.moveToNext()){
             long id=cursor.getLong(0);
             String name=cursor.getString(1);
             String saveCode=cursor.getString(2);
             int isFolder=cursor.getInt(3);
             String parentCode=cursor.getString(4);
             int index=cursor.getInt(5);
            String extend=cursor.getString(6);
            Long changeTime=cursor.getLong(7);
            list.add(new WritPadModel(id,name,saveCode,isFolder,parentCode,index,extend,null,changeTime));
        }
        cursor.close();
        return list;
    }
    /**
     * 获得某个文件里面的信息集合包括图片String
     * @param saveCode1 父分类标志
     * @return 返回查询后的数据集合，集合里面没有查询出imageContent=null，需要单独查询解析
     */
    public List<WritPadModel> getListFilesAndImage( String saveCode1){
        List<WritPadModel> list=new ArrayList<>();
        Cursor cursor= findBySQL("select " + writeAndImage + " from " + tableName + " where saveCode='" + saveCode1 + "' and isFolder=1 order by _index desc");
        if (null==cursor)return list;
        while (cursor.moveToNext()){
             long id=cursor.getLong(0);
             String name=cursor.getString(1);
             String saveCode=cursor.getString(2);
             int isFolder=cursor.getInt(3);
             String parentCode=cursor.getString(4);
             int index=cursor.getInt(5);
            String extend=cursor.getString(6);
             String imageContent=cursor.getString(7);
            Long changeTime=cursor.getLong(8);
            WritPadModel model=new WritPadModel(name,saveCode,isFolder,parentCode,index,extend,imageContent);
            list.add(model);
        }
        cursor.close();
        return list;
    }
    /**
     * 更新数据
     * @param imageContent 图片数据字符串
     * @return 返回是否更新成功
     */
    public boolean updataImageContent(String imageContent,long id){
        ContentValues values=new ContentValues();
        values.put("imageContent",imageContent);
        values.put("changeTime",System.currentTimeMillis());
        return(DataSupport.update(WritPadModel.class,values,id)>0);
    }

    /**
     * 通过唯一标示获得图片String数据
     *
     * @param saveCode 保存唯一标示
     * @return 返回图片装换的String
     */
    public String getImageContent(String saveCode,int index) {
        String imageContent = null;
        String sql="select " + writeImage + " from " + tableName + " where saveCode='" + saveCode + "' and isFolder=1 and _index="+index;
        Cursor imagecur = findBySQL(sql);
        if (imagecur == null) return imageContent;
        if (imagecur.moveToNext()) {
            imageContent = imagecur.getString(0);
        }
        imagecur.close();
        return imageContent;
    }

    /**
     * 通过唯一标示获得扩展字段信息
     *
     * @param saveCode 保存唯一标示
     * @return 返回图片装换的String
     */
    public String getExtent(String saveCode,int index) {
        String extent = "0";
        String sql="select " + extend + " from " + tableName + " where saveCode='" + saveCode + "' and isFolder=1 and _index="+index;
        LLog.e("getExtent",sql);
        Cursor imagecur = findBySQL(sql);
        if (imagecur == null) return extent;
        if (imagecur.moveToNext()) {
            extent = imagecur.getString(0);
        }
        imagecur.close();
        return extent;
    }


    /**
     * 获得本文件保存内容个数
     * @param saveCode
     * @return
     */
    public int getTemporarySize(String saveCode){
        int result = DataSupport.where("saveCode = ? and isFolder=1", saveCode).count(WritPadModel.class);
//        String sql="select count(id) from " + tableName + " where saveCode='" + saveCode + "'";
//        Cursor imagecur = DataSupport.findBySQL(sql);
//        int size=0;
//        if (imagecur == null) return size;
//
//        if (imagecur.moveToNext()) {
//            size = imagecur.getInt(0);
//        }
//        imagecur.close();
        return result;
    }

    /**
     * 判断数据是否存在
     *
     * @param saveCode 保存唯一标示
     * @return 是否存在该数据
     */
    public long judgeDataExist(String saveCode,int isFolder,int index) {
        long id = -1;
        Cursor cur = findBySQL("select id from " + tableName + " where saveCode='" + saveCode + "' and _index="+index+ " and isFolder="+isFolder);
        if (cur == null) return id;
       if (cur.moveToNext()){
           id= cur.getLong(0);
       }
        cur.close();
        return id;
    }
    /**
     * 判断数据是否存在
     *
     * @param parentCode 文件夹路径
     *                   @param name 文件名字
     * @return 是否存在该数据-1不存在
     */
    public long judgeDataExist(String parentCode,String name,int isFolder) {
        long id = -1;
        Cursor cur = findBySQL("select id from " + tableName + " where parentCode='" + parentCode + "' and name='"+name+ "' and isFolder="+isFolder);
        if (cur == null) return id;
       if (cur.moveToNext()){
           id= cur.getLong(0);
       }
        cur.close();
        return id;
    }

    /**
     * 判断文件是否存在
     * @param saveCode 保存的唯一标识值
     * @param isFloder 文件夹0，文件1
     * @return
     */
    public WritPadModel isSavedWrite(String saveCode, int isFloder){
        WritPadModel model = null;
        Cursor cursor = findBySQL("select "+writeAll+" from " + tableName + " where saveCode='" + saveCode + "' and isFolder="+isFloder);
        if (cursor.moveToNext()){
            long id=cursor.getLong(0);
            String name=cursor.getString(1);
            String saveCod=cursor.getString(2);
            int isFolder=cursor.getInt(3);
            String parentCode=cursor.getString(4);
            int index=cursor.getInt(5);
            String extend=cursor.getString(6);
            Long changeTime=cursor.getLong(7);
            model=new WritPadModel(id,name,saveCod,isFolder,parentCode,index,extend,null,changeTime);
        }
        return model;
    }

    /**
     * 判断文件名是否存在
     * @param name 文件名称
     * @param parentCode 文件父目录
     * @param isFloder 文件夹0，文件1
     * @return
     */
    public boolean isNameSavedWrite(String name,String parentCode, int isFloder){
        Cursor cur = findBySQL("select id from " + tableName + " where parentCode='" + parentCode + "' and isFolder="+isFloder+" and name='"+name+"'");
        if (null==cur)return false;
        boolean is=cur.moveToNext();
        cur.close();
        return is;
    }
}
