package com.example.wasteless.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GenericUtils {
    public static void toast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();

    }
    public static boolean isValidFloat(String number) {
        boolean retVal = true;

        try {
            // Attempt to parse the string as a float
            float floatValue = Float.parseFloat(number);
        } catch (NumberFormatException e) {
            retVal = false;
        }

        return retVal;
    }
    public static String getCurrentWeekSundayDateKey() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static String getHumanReadableDate(String dateString) {
        try {
            // Create a SimpleDateFormat object for parsing the input date string
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            // Parse the input date string into a Date object
            Date date = inputFormat.parse(dateString);

            // Create a SimpleDateFormat object for formatting the output date string
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);

            // Format the date into "DD/MM" format
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            return ""; // Return empty string if parsing fails
        }
    }

    public static String uriToBase64String(Context context, Uri imageUri) {
        if (imageUri == null) {
            return null;
        }

        Bitmap bitmap = uriToBitmap(context, imageUri);
        if (bitmap != null) {
            return bitmapToBase64String(bitmap);
        } else {
            return null;
        }
    }

    private static Bitmap uriToBitmap(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    public static void base64StringToImageView(String base64String, ImageView imageView) {
        if (base64String != null && !base64String.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
