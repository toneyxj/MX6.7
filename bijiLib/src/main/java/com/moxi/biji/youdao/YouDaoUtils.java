package com.moxi.biji.youdao;

import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.moxi.biji.intf.BackImp;
import com.moxi.biji.intf.NoteUtilsImp;
import com.moxi.biji.intf.SucessImp;
import com.moxi.biji.mdoel.BiJiModel;
import com.moxi.biji.youdao.config.URLUtils;
import com.moxi.biji.youdao.config.YouDaoInfo;
import com.moxi.biji.youdao.inter.RequestBackInter;
import com.moxi.biji.youdao.utils.YNoteHttpUtils;

import java.io.File;
import java.util.List;

/**
 * Created by xiajun on 2019/4/25.
 */

public class YouDaoUtils implements NoteUtilsImp {

    @Override
    public boolean isLogin(final FragmentActivity activity) {
        if (YouDaoInfo.getInstance().isNullCode()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OauthActivity.start(activity, URLUtils.getCodeUrl());
                }
            });

            return false;
        } else if (YouDaoInfo.getInstance().isNullAccessToken()) {
            //获取AccessToken
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OauthActivity.start(activity, URLUtils.getAccessTokenUrl());
                }
            });
            return false;
        }
        return true;
    }

    private YouDaoBook notebook;
    private BackImp imp;
    private BiJiModel model;
    private String notePath;

    private synchronized YouDaoBook getNoteBook() {
        return notebook;
    }

    private synchronized void setNotebook(YouDaoBook notebook) {
        this.notebook = notebook;
    }

    @Override
    public void sendNote(BiJiModel model, BackImp imp) {
        //先是获取笔记本信息
        this.model = model;
        this.imp = imp;
        if (notebook == null) {
            YNoteHttpUtils.allBook(inter);
        } else {
            createNote();
        }
    }

    /**
     * 创建笔记
     */
    private void createNote() {
        //获得所有笔记
//        YNoteHttpUtils.allBooknotes(inter, notebook.path);
        addNote();
    }

    private void addNote() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (model.getShareType() == 1) {//上传文字
                    YNoteHttpUtils.crateBooknotes(inter, model, notebook.path, notePath);
                } else if (model.getShareType() == 2) {
                    File file = new File(model.getContent());
                    if (!file.exists() || !file.isDirectory()) {
                        imp.error(new Exception("未读取到相应文件夹"));
                        return;
                    }
                    File[] listF = file.listFiles();
                    if (listF.length == 0) {
                        imp.error(new Exception("未读取图片资源"));
                        return;
                    }

                    File[] myF = new File[listF.length];
                    for (int i = 0; i < listF.length; i++) {
                        myF[listF.length - i - 1] = listF[i];
                    }
                    try {
                      YNoteHttpUtils.upPhoto(inter, model, notebook.path, notePath, myF);
                    } catch (Exception e) {
                        e.printStackTrace();
                        imp.error(e);
                    }
                }

            }
        }).start();
    }

    @Override
    public <T> void deleteNote(List<T> lists, SucessImp sucessImp) {

    }

    RequestBackInter inter = new RequestBackInter() {
        @Override
        public void onStart(int code) {
            imp.start();
        }

        @Override
        public void onSucess(final Object value, int code) {
            try {
                switch (code) {
                    case 3://获取所有数据信息
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<YouDaoBook> books = JSON.parseArray(value.toString(), YouDaoBook.class);
                                for (YouDaoBook book : books) {
                                    if (book.name.equals(model.getNoteBook())) {
                                        notebook = book;
                                        break;
                                    }
                                }
                                if (notebook == null) {//创建笔记本
                                    YNoteHttpUtils.createBook(inter, model.getNoteBook());
                                } else {
                                    createNote();
                                }
                            }
                        }).start();
                        break;
                    case 2://创建笔记
                        notebook = JSON.parseObject(value.toString(), YouDaoBook.class);
                        createNote();
                        break;
                    case 1://
                        break;
                    case 4://笔记本下面的所有笔记
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                addNote();
                            }
                        }).start();
                        break;
                    case 5://往笔记本里面添加笔记
                        imp.result();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                imp.error(e);
            }

        }

        @Override
        public void onFail(Exception e, int code) {
            imp.error(e);
        }
    };

}
