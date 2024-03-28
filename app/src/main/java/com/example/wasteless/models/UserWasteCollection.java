package com.example.wasteless.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wasteless.utils.FirebaseUtils;
import com.example.wasteless.utils.GenericUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserWasteCollection {
    /**
     * Object that references the current user's waste data collection
     */
    private static final String TAG = "UserWasteCollection";
    private static UserWasteCollection instance;
    private String userId;
    private DatabaseReference userCollectionRef;

    private UserWasteCollection(String userId) {
        this.userId = userId;
        this.userCollectionRef = FirebaseUtils.getDatabaseRefForUser(userId);
    }

    public static synchronized UserWasteCollection getInstance(String userId) {
        if (instance == null && userId != null) {
            instance = new UserWasteCollection(userId);
        }
        return instance;
    }

    public void getWasteDataForCurrentWeek(ValueEventListener listener) {
        String sundayDateKey = GenericUtils.getCurrentWeekSundayDateKey();
        if (sundayDateKey != null) {
            DatabaseReference weekCollectionRef = userCollectionRef.child(sundayDateKey);
            weekCollectionRef.addValueEventListener(listener);
        }
    }

    public void getWasteDataForLastWeek(ValueEventListener listener) {
        // TODO: Change this back
        String sundayDateKey = GenericUtils.getLastWeekSundayDateKey();
        DatabaseReference weekCollectionRef = userCollectionRef.child(sundayDateKey);
        weekCollectionRef.addValueEventListener(listener);
    }

    public void addWasteData(WasteDataModel wasteData) {

        // Generate a unique UUID for the waste data:
        String wasteDataId = userCollectionRef.push().getKey();

        // Add it to the waste data object
        wasteData.setUuid(wasteDataId);

        if (wasteDataId != null) {
            // Check if collection exists for the current week's Sunday date
            String sundayDateKey = GenericUtils.getCurrentWeekSundayDateKey();
            DatabaseReference collectionRef = userCollectionRef.child(sundayDateKey);

            collectionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Collection already exists, add waste data to it
                        collectionRef.child(wasteDataId).setValue(wasteData.toMap());
                    } else {
                        // Collection doesn't exist, create one and add waste data
                        Map<String, Object> collectionMap = new HashMap<>();
                        collectionMap.put(String.valueOf(System.currentTimeMillis()), wasteData.toMap());
                        collectionRef.setValue(collectionMap);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "addWasteData onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }

    public void deleteWasteData(String sundayDateKey, String wasteDataId) {
        DatabaseReference weekCollectionRef = userCollectionRef.child(sundayDateKey);
        Query query = weekCollectionRef.child(wasteDataId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // remove the value at reference
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
