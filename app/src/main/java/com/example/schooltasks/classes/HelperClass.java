package com.example.schooltasks.classes;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.schooltasks.R;
import com.google.android.material.snackbar.Snackbar;

public class HelperClass {

    public static void showSnackbar(View view, Context context, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer));
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.md_theme_primaryContainer));
        snackbar.show();
    }

}
