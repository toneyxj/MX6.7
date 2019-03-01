package com.moxi.calendar.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.mx.mxbase.utils.StringUtils;

/**
 * Created by xj on 2017/10/20.
 */

public class BaseEdite extends EditText {
    private TextWatcher textWatcher;
    public BaseEdite(Context context) {
        super(context);
        init();
    }

    public BaseEdite(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseEdite(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int index = getSelectionStart() - 1;
                if (index >=0) {
                    if (isEnterCharacter(editable.charAt(index))) {
                        Editable edit = getText();
                        edit.delete(index, index + 1);
                        StringUtils.closeIMM(getContext(), BaseEdite.this.getWindowToken());
                    }
                }
            }

        };
        addTextChangedListener(textWatcher);
    }

    private boolean isEnterCharacter(char codePoint) {
        return codePoint=='\n';
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(textWatcher);
    }
}
