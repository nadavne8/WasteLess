package com.example.wasteless;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.adapters.WasteDataAdapter;
import com.example.wasteless.fragments.AddItemDialogFragment;
import com.example.wasteless.models.UserWasteCollection;
import com.example.wasteless.models.WasteDataModel;
import com.example.wasteless.utils.GenericUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button btnLogout, btnAddItem, btnMap;
    private RecyclerView recyclerView;
    private TextView welcomeTextView, totalWeightTextView, dateTextView;
    private List<WasteDataModel> wasteDataModelList;
    private WasteDataAdapter adapter;

    private UserWasteCollection wasteCollectionReference;

    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {


            wasteCollectionReference = UserWasteCollection.getInstance(currentUser.getUid());

            bindAllElements();

            setDate();

            setWelcomeMessage(currentUser);

            setLogoutOnclickFunctionality();

            setAddItemOnclickFunctionality();

            setMapActivityOnclick();

            // Init recyclerview
            initRecyclerView();

            // Fetch data for the current week and update RecyclerView
            wasteCollectionReference.getWasteDataForCurrentWeek(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    wasteDataModelList.clear(); // Clear the list before adding new data
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        WasteDataModel wasteData = snapshot.getValue(WasteDataModel.class);
                        if (wasteData != null) {
                            wasteDataModelList.add(wasteData);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    updateTotalWeightTextView();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        } else {
            goToLoginPage();
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void bindAllElements() {
        btnAddItem = findViewById(R.id.addItemButton);
        btnLogout = findViewById(R.id.logoutButton);
        welcomeTextView = findViewById(R.id.welcomeTv);
        btnMap = findViewById(R.id.googleMapsButton);
        totalWeightTextView = findViewById(R.id.sumWeightTv);
        dateTextView = findViewById(R.id.dateTv);
    }

    private void setMapActivityOnclick() {
    }

    private void setDate() {
        dateTextView.setText("Week of " + GenericUtils.getHumanReadableDate(GenericUtils.getCurrentWeekSundayDateKey()));
        dateTextView.setVisibility(View.VISIBLE);
    }

    private void updateTotalWeightTextView() {
        Float sumOfWeight = 0.0f;
        for (WasteDataModel item : wasteDataModelList) {
            sumOfWeight += item.getWeight();
        }
        totalWeightTextView.setText("Total weight recycled: " + sumOfWeight + " KG");
    }

    private void setAddItemOnclickFunctionality() {
        btnAddItem.setOnClickListener(view -> {
            AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
            addItemDialogFragment.show(getSupportFragmentManager(), "AddItemDialog");
        });
    }

    private void setLogoutOnclickFunctionality() {
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            goToLoginPage();
        });
    }

    private void setWelcomeMessage(FirebaseUser currentUser) {
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            this.welcomeTextView.setText(String.format("Welcome, %s!", currentUser.getDisplayName()));
        }
    }

    private void goToLoginPage() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize adapter outside onStart
        wasteDataModelList = new ArrayList<>();
        adapter = new WasteDataAdapter(wasteDataModelList);
    }

    public void addItem(WasteDataModel wasteData) {
        wasteCollectionReference.addWasteData(wasteData);
    }
}
