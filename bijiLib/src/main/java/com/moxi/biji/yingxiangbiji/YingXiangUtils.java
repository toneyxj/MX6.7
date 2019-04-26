package com.moxi.biji.yingxiangbiji;

import android.support.v4.app.FragmentActivity;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.moxi.biji.BijiUtils;
import com.moxi.biji.intf.BackImp;
import com.moxi.biji.intf.NoteUtilsImp;
import com.moxi.biji.intf.SucessImp;
import com.moxi.biji.mdoel.BiJiModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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

    private Notebook notebook;

    private synchronized Notebook getNoteBook(){
        return notebook;
    }

    private synchronized void setNotebook(Notebook notebook){
        this.notebook=notebook;
    }

    /**
     * 同步文字到印象笔记
     */
    @Override
    public void sendNote(final BiJiModel model, final BackImp imp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    imp.start();
                    EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
                   if (getNoteBook()==null) {
                       List<Notebook> notebooks = noteStoreClient.listNotebooks();
                       Notebook myNB = null;
                       for (Notebook nb : notebooks) {
                           if (nb.getName().equals(model.getNoteBook())) {
                               myNB = nb;
                               break;
                           }
                       }
                       //创建笔记本
                       if (myNB == null) {
                           Notebook notebook = new Notebook();
                           notebook.setName(model.getNoteBook());
                           myNB = noteStoreClient.createNotebook(notebook);
                       }
                       //创建后笔记本为空
                       if (myNB == null) {
                           imp.error(new NullPointerException("创建笔记本失败"));
                           return;
                       }
                       setNotebook(myNB);
                   }
                    try {
                        FindNotesTask notesTask=new FindNotesTask(getNoteBook(),model.getTitle());
                        List<NoteRef> refs=notesTask.checkedExecute();
                        List<NoteRef> deleteNote=new ArrayList<NoteRef>();
                        for (NoteRef ref:refs){
                            if (ref.getTitle().equals(model.getTitle())){
                                deleteNote.add(ref);
                            }
                        }
                        if (deleteNote.size()>0){
                            imp.removeRepeat(deleteNote,model.getTitle());
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Note note = new Note();
                    note.setNotebookGuid(getNoteBook().getGuid());
                    note.setTitle(model.getTitle());
                    if (model.getShareType()==1) {//上传文字
                        //上传同步信息
                        note.setContent(EvernoteUtil.NOTE_PREFIX + BijiUtils.readFile(model.getContent()) + EvernoteUtil.NOTE_SUFFIX);
                        //上传同步note
                        noteStoreClient.createNote(note);
                    }else if (model.getShareType()==2){//上传图片
                        File file=new File(model.getContent());
                        if (!file.exists()||!file.isDirectory()){
                            imp.error(new Exception("未读取到相应文件夹"));
                            return;
                        }
                        File[] listF=file.listFiles();
                        if (listF.length==0){
                            imp.error(new Exception("未读取图片资源"));
                            return;
                        }

                        File[] myF=new File[listF.length];
                        for (int i = 0; i < listF.length; i++) {
                            myF[listF.length-i-1]=listF[i];
                        }

                        StringBuffer buffer=new StringBuffer();
                        buffer.append(EvernoteUtil.NOTE_PREFIX);
                        for (File ff:myF){
                            try {
                                buffer.append(EvernoteUtil.createEnMediaTag(insertImage(note,ff.getAbsolutePath(),ff.getName())));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        buffer.append(EvernoteUtil.NOTE_SUFFIX);

                        note.setContent(buffer.toString());
                        noteStoreClient.createNote(note);
                    }
                    imp.result();
                } catch (Exception e) {
                    e.printStackTrace();
                    imp.error(e);
                }finally {
                    setNotebook(null);
                }
            }
        }).start();
    }

    @Override
    public <T> void deleteNote(final List<T> lists, final SucessImp sucessImp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (T ob:lists){
                        NoteRef ref= (NoteRef) ob;
                        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
                        noteStoreClient.deleteNote(ref.getGuid());
                    }
                    if (sucessImp!=null)sucessImp.onSucess();
                }catch (Exception e){
                    e.printStackTrace();
                    if (sucessImp!=null)sucessImp.onFail();
                }
            }
        }).start();
    }


    private Resource insertImage( Note note,String photoPath,String filename) throws Exception {

        InputStream in = null;
        try {
            // Hash the data in the image file. The hash is used to reference the file in the ENML note content.
            in = new BufferedInputStream(new FileInputStream(photoPath));
            FileData data = new FileData(EvernoteUtil.hash(in), new File(photoPath));

            ResourceAttributes attributes = new ResourceAttributes();
            attributes.setFileName(filename);

            // Create a new Resource
            Resource resource = new Resource();
            resource.setData(data);
            resource.setMime("image/png");
            resource.setAttributes(attributes);

            note.addToResources(resource);
            return resource;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
