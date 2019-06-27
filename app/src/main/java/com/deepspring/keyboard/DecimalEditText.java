package com.deepspring.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

/**
 * Created by fzy on 2019/4/16.
 */
public class DecimalEditText extends AppCompatEditText {
    /**
     * 保留小数点前多少位，默认4位，既到万位
     */
    private int mDecimalStarNumber = 4;

    /**
     * 保留小数点后多少位，默认4位 9999.9999
     */
    private int mDecimalEndNumber = 4;

    public DecimalEditText(Context context) {
        this(context, null);
    }

    public DecimalEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public DecimalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DecimalEditText);
        mDecimalStarNumber = typedArray.getInt(R.styleable.DecimalEditText_decimalStarNumber, mDecimalStarNumber);
        mDecimalEndNumber = typedArray.getInt(R.styleable.DecimalEditText_decimalEndNumber, mDecimalEndNumber);
        typedArray.recycle();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String lastInputContent = dest.toString();
                if (source.equals(".") && lastInputContent.length() == 0) {
                    return "0.";
                }

                if (!source.equals(".") && !source.equals("") && lastInputContent.equals("0")) {
                    return ".";
                }

                if (source.equals(".") && lastInputContent.contains(".")) {
                    return "";
                }

                if (lastInputContent.contains(".")) {
                    int index = lastInputContent.indexOf(".");
                    if (dend - index >= mDecimalEndNumber + 1) {
                        return "";
                    }
                } else {
                    if (!source.equals(".") && lastInputContent.length() >= mDecimalStarNumber) {
                        return "";
                    }
                }
                return null;
            }
        }});
    }


    public int getDecimalStarNumber() {
        return mDecimalStarNumber;
    }

    public void setDecimalStarNumber(int decimalStarNumber) {
        mDecimalStarNumber = decimalStarNumber;
    }

    public int getDecimalEndNumber() {
        return mDecimalEndNumber;
    }

    public void setDecimalEndNumber(int decimalEndNumber) {
        mDecimalEndNumber = decimalEndNumber;
    }
}
