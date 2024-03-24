package com.example.wasteless.utils;

import com.example.wasteless.strings.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    public static DatabaseReference getDatabaseRefForUser(String userId) {
        return FirebaseDatabase.getInstance().getReference().child(Constants.COLLECTION_PATH).child(userId);
    }
}
