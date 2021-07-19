package com.peerbits.base.ui.dialog;


import com.peerbits.base.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;

public class CustomProgressDialog extends AlertDialog {

    private Context context;
    private String title;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;
        super.setCancelable(false);
    }


    public void show(String text) {
        title = text;
        try {
            show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void show() {

        try {
            super.show();
            super.setCanceledOnTouchOutside(false);
            super.setCancelable(false);

            if (title != null && !TextUtils.isEmpty(title)) {
                setContentView(R.layout.custom_progress_loading);
                /*TextView tvTitle = findViewById(R.id.progress_bar_text);
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);*/
            } else {
                setContentView(R.layout.custom_progress_loading);
            }

            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void dismiss() {
        if (context != null) {
            super.dismiss();
        }
    }
}