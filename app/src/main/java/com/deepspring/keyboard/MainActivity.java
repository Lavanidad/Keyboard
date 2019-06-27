package com.deepspring.keyboard;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private MyKeyboard kb_1, kb_2;
    private DecimalEditText et_1, et_2;
    private LinearLayout ll_1, ll_2;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = LayoutInflater.from(this).inflate(R.layout.key_containor, null);
        et_1 = findViewById(R.id.et_1);
        et_2 = findViewById(R.id.et_2);
        ll_1 = findViewById(R.id.ll_1);
        ll_2 = findViewById(R.id.ll_2);
        init();
        initListener();
    }

    @SuppressLint("CutPasteId")
    private void init() {
        kb_1 = new MyKeyboard(this, ll_1, et_1,
                R.layout.key_containor, view.findViewById(R.id.mykeyboardview).getId(), true);
        kb_1.setDelDrawable(this.getResources().getDrawable(R.drawable.icon_del));

        kb_2 = new MyKeyboard(this, ll_2, et_2,
                R.layout.key_containor, view.findViewById(R.id.mykeyboardview).getId(), false);
        kb_2.setDelDrawable(this.getResources().getDrawable(R.drawable.icon_del));

        //default
        ll_1.setVisibility(View.VISIBLE);
        ll_2.setVisibility(View.GONE);
    }

    private void initListener() {
        if (et_1.hasFocusable()) {
            ll_1.setVisibility(View.VISIBLE);
            ll_2.setVisibility(View.GONE);
        }
        if (et_2.hasFocusable()){
            ll_1.setVisibility(View.GONE);
            ll_2.setVisibility(View.VISIBLE);
        }
    }

}
