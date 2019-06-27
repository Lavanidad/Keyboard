package com.deepspring.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fzy on 2018/3/7 0007.
 */

public class MyKeyboard {

    private Context mContext;
    private LinearLayout layout;
    private View keyContainer;              //自定义键盘的容器View
    private MyKeyboardView keyboardView;  //View
    private Keyboard keyboardNumber;        //数字键盘

    private Drawable delDrawable;
    private int keyboardContainerResId;
    private int keyboardResId;
    private EditText mEditText;
    private boolean canClick = true;
    private boolean isFinish = false;

    public MyKeyboard(Context mContext, LinearLayout layout, EditText mEditText, int id, int keyId) {
        this.mContext = mContext;
        this.layout = layout;
        this.mEditText = mEditText;
        this.keyboardContainerResId = id;
        this.keyboardResId = keyId;
        initKeyboard();
        addListeners();
    }


    public MyKeyboard(Context mContext, LinearLayout layout, EditText mEditText, int id, int keyId,
                      Drawable del, Drawable low, Drawable up) {
        this.mContext = mContext;
        this.layout = layout;
        this.mEditText = mEditText;
        this.keyboardContainerResId = id;
        this.keyboardResId = keyId;
        this.delDrawable = del;
        initKeyboard();
        addListeners();
    }

    /**
     * @param mContext  使用键盘的上下文对象
     * @param layout    键盘摆放的位置（使用时候的布局ID）
     * @param mEditText 键盘作用的输入框
     * @param id        容器ID（键盘的背景）
     * @param keyId     控件ID
     * @param canClick  特殊按钮是否能点击
     */
    public MyKeyboard(Context mContext, LinearLayout layout, EditText mEditText, int id, int keyId,
                      boolean canClick) {
        this.mContext = mContext;
        this.layout = layout;
        this.mEditText = mEditText;
        this.keyboardContainerResId = id;
        this.keyboardResId = keyId;
        this.canClick = canClick;
        initKeyboard();
        addListeners();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initKeyboard() {
        keyContainer = LayoutInflater.from(mContext).inflate(keyboardContainerResId, layout, true);
        keyContainer.setVisibility(View.VISIBLE);
        keyboardNumber = new Keyboard(mContext, R.xml.keyboard_num);            //实例化数字键盘
        // 由于符号键盘与字母键盘共用一个KeyBoardView, 所以不需要再为符号键盘单独实例化一个KeyBoardView
        keyboardView = keyContainer.findViewById(keyboardResId);
        keyboardView.setDelDrawable(delDrawable);
        keyboardView.setKeyboard(keyboardNumber);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);

        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    // 设置键盘点击监听
    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onPress(int primaryCode) {
            keyboardView.setPreviewEnabled(false);
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            try {
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();
                if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    if (editable.length() > 0) {// 回退键,删除字符
                        if (start == end) { //光标开始和结束位置相同, 即没有选中内容
                            editable.delete(start - 1, start);
                            isFinish = false;
                        } else { //光标开始和结束位置不同, 即选中EditText中的内容
                            editable.delete(start, end);
                            isFinish = false;
                        }
                    }
                } else if (primaryCode == -1000) {//清零
                    if (editable.length() > 0) {
                        editable.clear();
                        isFinish = false;
                    }
                }
                if (editable.length() < 7) {
                    if (primaryCode == -200 && canClick) {
                        if (start == 0) {
                            editable.insert(0, "200.00");
                            isFinish = true;
                        }
                    } else if (primaryCode == -500 && canClick) {
                        if (start == 0) {
                            editable.insert(0, "500.00");
                            isFinish = true;
                        }
                    } else if (primaryCode == -800 && canClick) {
                        if (start == 0) {
                            editable.insert(0, "800.00");
                            isFinish = true;
                        }
                    } else if (!isFinish) {
                        // 输入键盘值
                        //editable.insert(start, Character.toString((char) primaryCode));
                        editable.replace(start, end, Character.toString((char) primaryCode));
                        Log.d("test", "l:" + editable.length() + "text:" + mEditText.getText());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeUp() {
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideSystemKeyBoard((EditText) v);
                }
                return false;
            }
        });
    }


    //隐藏系统键盘关键代码
    private void hideSystemKeyBoard(EditText edit) {
        this.mEditText = edit;
        InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }

        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            edit.setInputType(0);
        } else {
            try {
                Method setShowSoftInputOnFocus = EditText.class.getMethod(methodName, Boolean.TYPE);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit, Boolean.FALSE);
            } catch (NoSuchMethodException e) {
                edit.setInputType(0);
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
        keyboardView.setDelDrawable(delDrawable);
    }
}
