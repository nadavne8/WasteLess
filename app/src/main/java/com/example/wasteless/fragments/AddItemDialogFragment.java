package com.example.wasteless.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.wasteless.MainActivity;
import com.example.wasteless.R;
import com.example.wasteless.models.WasteDataModel;
import com.example.wasteless.utils.GenericUtils;

public class AddItemDialogFragment extends DialogFragment {

    private ImageView addImageFromGalleryIV, addImageIV;
    private View dialogView;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri imageUri = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /**
         * Set the functionality for the add object dialog
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_data, null);
        builder.setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    // Get references to EditText fields
                    EditText editTextProductName = dialogView.findViewById(R.id.editTextProductName);
                    EditText editTextWeight = dialogView.findViewById(R.id.editTextWeight);
                    String productName = editTextProductName.getText().toString();
                    String weight = editTextWeight.getText().toString();

                    String isValidInput = isValidInput(productName, weight);

                    if (isValidInput.equals("valid")) {
                        // Call method in parent activity to handle adding item with image
                        WasteDataModel wasteDataModel = new WasteDataModel(Float.parseFloat(weight), productName, GenericUtils.uriToBase64String(requireActivity().getApplicationContext(), imageUri));
                        ((MainActivity) requireActivity()).addItem(wasteDataModel);
                        dialog.dismiss();
                    } else {
                        // Set the error text
                        TextView error = dialogView.findViewById(R.id.errorTextView);
                        error.setText(isValidInput);
                        error.setVisibility(View.VISIBLE);
                    }


                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Initialize views and set click listeners
        addImageFromGalleryIV = dialogView.findViewById(R.id.addImageFromGalleryIV);
        addImageIV = dialogView.findViewById(R.id.addImageIV);

        /**
         * Set the functionality for the gallery button
         */
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        addImageIV.setImageURI(uri);
                        imageUri = uri;

                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        addImageFromGalleryIV.setOnClickListener(view -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        return builder.create();
    }

    // Check if the input is okay - Meaning there's a product name, and weight is a valid float number
    // If the input isn't valid - return the error message to display to the user.
    private String isValidInput(String productName, String weight) {
        String resultString = "valid";


        if (productName.isEmpty()) {
            resultString = "Please provide the product name.";
        } else if (!GenericUtils.isValidFloat(weight)) {
            resultString = "Please provide a valid weight.";
        }

        return resultString;
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(view -> {
            // Get references to EditText fields
            EditText editTextProductName = dialogView.findViewById(R.id.editTextProductName);
            EditText editTextWeight = dialogView.findViewById(R.id.editTextWeight);
            String productName = editTextProductName.getText().toString();
            String weight = editTextWeight.getText().toString();

            String isValidInput = isValidInput(productName, weight);

            if (isValidInput.equals("valid")) {
                // Call method in parent activity to handle adding item with image
                WasteDataModel wasteDataModel = new WasteDataModel(Float.parseFloat(weight), productName, GenericUtils.uriToBase64String(requireActivity().getApplicationContext(), imageUri));
                ((MainActivity) requireActivity()).addItem(wasteDataModel);
                alertDialog.dismiss();
            } else {
                // Set the error text
                TextView error = dialogView.findViewById(R.id.errorTextView);
                error.setText(isValidInput);
                error.setVisibility(View.VISIBLE);
            }
        });
    }
}
