package com.dangdang.reader.moxiUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.dread.BookNoteActivity;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookNote;

import java.util.Date;

/**
 * 保存笔记
 * Created by Administrator on 2016/12/15.
 */
public class SaveNoteDialog extends Dialog implements View.OnClickListener {
    private TextView show_title;
    private TextView save_note;
    private EditText input_txt;
    private View onclick_dismiss;
private BookNote note;
    private SaveNoteBack back;
    public SaveNoteDialog(Context context, int theme,BookNote note,SaveNoteBack back) {
        super(context, theme);
        this.note=note;
        this.back=back;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_save_note);

        show_title=(TextView)findViewById(R.id.show_title);
        save_note=(TextView)findViewById(R.id.save_note);
        input_txt=(EditText) findViewById(R.id.input_txt);
        onclick_dismiss=(View)findViewById(R.id.onclick_dismiss);

        show_title.setOnClickListener(this);
        save_note.setOnClickListener(this);
        onclick_dismiss.setOnClickListener(this);

    }


    /**
     * 全屏显示的dialog
     *
     * @param context
     */
    public static SaveNoteDialog getdialog(Context context, BookNote note,SaveNoteBack back) {
        SaveNoteDialog dialog = new SaveNoteDialog(context, R.style.AlertDialogStyle,note,back);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        window.setAttributes(lp);
        dialog.show();
        return dialog;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.save_note) {
            String input=input_txt.getText().toString().trim();
            if (input.equals("")) {
                return;
            }

            long operateTime = new Date().getTime();
            note.noteText = input;
            note.noteTime = new Date().getTime();
            note.modifyTime = String.valueOf(operateTime);

            int mNoteId = (int) getMarkNoteManager().operationBookNote(note,
                    MarkNoteManager.OperateType.NEW);

            Intent intent=new Intent();
            intent.putExtra(BookNoteActivity.BOOK_NOTE_BACK_FLAG, BookNoteActivity.BOOK_NOTE_SAVE);
            intent.putExtra(BookNoteActivity.BOOK_NOTE_NEW_ID, mNoteId);
            intent.putExtra(BookNoteActivity.BOOK_NOTE_NEW_CONTENT,input);
            intent.putExtra(BookNoteActivity.BOOK_NOTE_OBJECT, note);
            intent.putExtra(BookNoteActivity.BOOK_NOTE_SHARE_CHECK, true);

            back.onSaveNote(intent);
            hideSoftKeyBoard();
            this.dismiss();
        } else if (i == R.id.onclick_dismiss||i==R.id.show_title) {
            if (i==R.id.show_title){
                if (getCurrentFocus() != null) {
                    hideSoftKeyBoard();
                    return;
                }
            }
            hideSoftKeyBoard();
            this.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        hideSoftKeyBoard();
    }

    private MarkNoteManager getMarkNoteManager() {
        return ReaderAppImpl.getApp().getMarkNoteManager();
    }
    // 隐藏输入法
    public void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public interface SaveNoteBack{
        void onSaveNote(Intent intent);
    }
}
