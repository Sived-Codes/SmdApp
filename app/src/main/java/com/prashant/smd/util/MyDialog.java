package com.prashant.smd.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prashant.smd.R;

import java.lang.ref.WeakReference;

public class MyDialog {

    private final WeakReference<Context> contextRef;
    private View dialogView;
    private AlertDialog alertDialog;

    public MyDialog(Context appContext, @LayoutRes int layoutResId) {
        contextRef = new WeakReference<>(appContext);
        Context context = contextRef.get();
        if (context != null) {
            dialogView = LayoutInflater.from(context).inflate(layoutResId, null);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded);
            alertDialogBuilder.setView(dialogView);
            alertDialog = alertDialogBuilder.create();
        }
    }

    public View getView() {
        return dialogView;
    }

    public void setCancelable(boolean isCancelable) {
        alertDialog.setCancelable(isCancelable);
    }

    public void show() {
        Context context = contextRef.get();
        if (context != null && !((AppCompatActivity) context).isFinishing()) {
            // Apply fade-in animation when showing the dialog
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(600);
            dialogView.startAnimation(alphaAnimation);

            alertDialog.show();
        }
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
