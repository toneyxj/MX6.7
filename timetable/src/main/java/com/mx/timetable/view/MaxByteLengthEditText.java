package com.mx.timetable.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;

/**
 * Created by Archer on 2016/11/25.
 */
public class MaxByteLengthEditText extends EditText {
    private int maxByteLength = 8;

    private String encoding = "GBK";

    public MaxByteLengthEditText(Context context) {
        super(context);
        init();
    }

    public MaxByteLengthEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{inputFilter});
    }

    public int getMaxByteLength() {
        return maxByteLength;
    }

    public void setMaxByteLength(int maxByteLength) {
        this.maxByteLength = maxByteLength;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * input输入过滤
     */
    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            try {
                int len = 0;
                boolean more = false;
                do {
                    SpannableStringBuilder builder =
                            new SpannableStringBuilder(dest).replace(dstart, dend, source.subSequence(start, end));
                    len = builder.toString().getBytes(encoding).length;
                    more = len > maxByteLength;
                    if (more) {
                        end--;
                        source = source.subSequence(start, end);
                    }
                } while (more);
                return source;
            } catch (UnsupportedEncodingException e) {
                return "Exception";
            }
        }
    };
}
