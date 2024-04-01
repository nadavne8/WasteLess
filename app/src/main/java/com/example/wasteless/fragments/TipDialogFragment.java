package com.example.wasteless.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.wasteless.R;

public class TipDialogFragment extends DialogFragment {

    private TextView tipTextView, titleTextView;
    private String titleString, tipString;
    private View dialogView;

    public TipDialogFragment(String titleString, String tipString) {
        this.titleString = titleString;
        this.tipString = tipString;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /**
         * Set the functionality for the add object dialog
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_tip, null);
        tipTextView = dialogView.findViewById(R.id.tipTv);
        titleTextView = dialogView.findViewById(R.id.titleTv);
        tipTextView.setText(tipString);
        titleTextView.setText(titleString);
        builder.setView(dialogView)
                .setNegativeButton("Okay, thank you", (dialog, which) ->{
                    dialog.dismiss();
                });
        return builder.create();
    }
}
