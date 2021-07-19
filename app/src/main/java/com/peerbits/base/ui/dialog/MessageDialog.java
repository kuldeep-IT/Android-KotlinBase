package com.peerbits.base.ui.dialog;

import com.peerbits.base.R;
import com.peerbits.base.databinding.CustomAlertDialogLayoutBinding;
import com.peerbits.base.utils.CommonUtils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

public class MessageDialog extends AlertDialog implements View.OnClickListener {
    private boolean cancelable = true;
    private String title, message, positiveButtonText, negativeButtonText;
    private OnClickListener onPositiveButtonClick, onNegativeButtonClick;

    public MessageDialog(Context context) {
        super(context, R.style.DialogWithAnimation);
    }

    public MessageDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public MessageDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public MessageDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public MessageDialog setPositiveButton(String text, OnClickListener listener) {
        this.positiveButtonText = text;
        this.onPositiveButtonClick = listener;
        return this;
    }

    public MessageDialog setNegativeButton(String text, OnClickListener listener) {
        this.negativeButtonText = text;
        this.onNegativeButtonClick = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomAlertDialogLayoutBinding
                mBinder = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.custom_alert_dialog_layout, null, false);
        setContentView(mBinder.getRoot());
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);

        mBinder.tvTitle.setVisibility(title != null ? View.VISIBLE : View.GONE);
        if (title != null) mBinder.tvTitle.setText(title);

        mBinder.tvMessage.setVisibility(message != null ? View.VISIBLE : View.GONE);
        if (message != null) mBinder.tvMessage.setText(message);

        if (positiveButtonText != null) mBinder.tvButtonPositive.setText(positiveButtonText);
        if (negativeButtonText != null) mBinder.tvButtonNegative.setText(negativeButtonText);

        mBinder.tvButtonPositive.setVisibility(onPositiveButtonClick != null ? View.VISIBLE : View.GONE);
        mBinder.tvButtonNegative.setVisibility(onNegativeButtonClick != null ? View.VISIBLE : View.GONE);

        mBinder.tvButtonPositive.setOnClickListener(this);
        mBinder.tvButtonNegative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        CommonUtils.preventDoubleClick(view);
        switch (view.getId()) {
            case R.id.tvButtonPositive:
                if (onPositiveButtonClick != null)
                    onPositiveButtonClick.onClick(MessageDialog.this, 0);
                break;
            case R.id.tvButtonNegative:
                if (onNegativeButtonClick != null)
                    onNegativeButtonClick.onClick(MessageDialog.this, 0);
                break;
        }
    }
}
