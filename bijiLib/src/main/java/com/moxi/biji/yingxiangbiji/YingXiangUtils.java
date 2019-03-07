package com.moxi.biji.yingxiangbiji;

import android.support.v4.app.FragmentActivity;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.moxi.biji.BijiUtils;
import com.moxi.biji.intf.BackImp;
import com.moxi.biji.intf.NoteUtilsImp;
import com.moxi.biji.mdoel.BiJiModel;

import java.util.List;

/**
 * 印象笔记基本操作工具类
 * Created by Administrator on 2019/2/27.
 */
public class YingXiangUtils implements NoteUtilsImp {
    /**
     * 判断用户是否登录，未登录的情况下，跳入登录界面
     * @param activity
     * @return
     */
    @Override
    public  boolean isLogin(FragmentActivity activity) {
        if (EvernoteSession.getInstance().isLoggedIn()) {
            return true;
        } else {
            EvernoteSession.getInstance().authenticate(activity);
            return false;
        }
    }

    /**
     * 同步文字到印象笔记
     */
    @Override
    public void sendText(final BiJiModel model, final BackImp imp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    imp.start();
                    EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
                    List<Notebook> notebooks= noteStoreClient.listNotebooks();
                    Notebook myNB=null;
                   for(Notebook nb:notebooks){
                      if (nb.getName().equals(model.getNoteBook())){
                          myNB=nb;
                          break;
                      }
                   }
                    //创建笔记本
                    if (myNB==null) {
                        Notebook notebook = new Notebook();
                        notebook.setName(model.getNoteBook());
                        myNB=noteStoreClient.createNotebook(notebook);
                    }
                    //创建后笔记本为空
                    if (myNB==null) {
                        imp.error(new NullPointerException("创建笔记本失败"));
                        return;
                    }

                    //上传同步信息
                    Note note = new Note();
                    note.setNotebookGuid(myNB.getGuid());
                    note.setTitle(model.getTitle());
                   note.setContent(EvernoteUtil.NOTE_PREFIX + BijiUtils.readFile( model.getContent()) + EvernoteUtil.NOTE_SUFFIX);
                    //上传同步note
                     noteStoreClient.createNote(note);
                    imp.result();
                } catch (Exception e) {
                    e.printStackTrace();
                    imp.error(e);
                }
            }
        }).start();
    }
}
