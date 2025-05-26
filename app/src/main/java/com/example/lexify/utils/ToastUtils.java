package com.example.lexify.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.lexify.R;

public class ToastUtils {
    
    public static void showCustomToast(Context context, String message, boolean isSuccess) {
        // Inflate the custom layout
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        
        // Get references to the views
        ImageView iconView = layout.findViewById(R.id.toastIcon);
        TextView textView = layout.findViewById(R.id.toastText);
        
        // Set the icon based on success/failure
        iconView.setImageResource(isSuccess ? R.drawable.ic_check_circle : R.drawable.ic_error);
        iconView.setColorFilter(ContextCompat.getColor(context, 
            isSuccess ? R.color.lex_purple_primary : R.color.lex_error));
        
        // Set the message
        textView.setText(message);
        
        // Create and show the toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
} 