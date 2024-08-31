package com.example.schooltasks;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;

public class HelperClass {

    public static void showSnackbar(View view, Context context, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer));
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.md_theme_primaryContainer));
        snackbar.show();
    }

}
