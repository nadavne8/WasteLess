package com.example.wasteless.utils;

import android.content.Context;
import android.widget.Toast;

public class GenericUtils {
    public static void toast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
}
