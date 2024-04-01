package com.example.wasteless;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.adapters.WasteDataAdapter;
import com.example.wasteless.fragments.AddItemDialogFragment;
import com.example.wasteless.fragments.TipDialogFragment;
import com.example.wasteless.models.UserWasteCollection;
import com.example.wasteless.models.WasteDataModel;
import com.example.wasteless.utils.GenericUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //הגדרת משתנים
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private FirebaseAuth auth;
    private Button btnLogout, btnAddItem;
    private RecyclerView recyclerView;
    private TextView welcomeTextView, totalWeightTextView, dateTextView;
    private List<WasteDataModel> wasteDataModelList;
    private WasteDataAdapter adapter;
    private FirebaseFirestore firestore;
    private UserWasteCollection wasteCollectionReference;
    private DocumentReference tipCollection;
    private float lastWeekRecyclingSum = 0f;


    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();


        requestPermissions();

        if (currentUser != null) {

            // הדאטהבייס של המשתמש - שומר את המידע לגבי המחזור שלו
            wasteCollectionReference = UserWasteCollection.getInstance(currentUser.getUid());

            firestore = FirebaseFirestore.getInstance();

            startCountdownTimer();

            bindAllElements();

            setDate();

            setWelcomeMessage(currentUser);

            setLogoutOnclickFunctionality();//מנתקת את החשבון הנוכחי מחשבון הfirebase שלו ועוברת לדף הlogin

            setAddItemOnclickFunctionality();//מגדיר את התנהגות המערכת באמצעות listener כאשר המשתמש לוחץ על הכפתור addItem

            // Init recyclerview
            initRecyclerView();

            // פה מביאים את הנתונים של השבוע הנוכחי,
            // רושמים את הListener ומאזינים לשינויים בדאטהבייס
            // Fetch data for the current week and update RecyclerView
            wasteCollectionReference.getWasteDataForCurrentWeek(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                /**
                 * This is called when we get the data form the database
                 */
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    wasteDataModelList.clear(); // Clear the list before adding new data
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        WasteDataModel wasteData = snapshot.getValue(WasteDataModel.class);//עוברת על כל הנתונים של הsnapshot ומגדירה כל אחד כפריט בודד
                        if (wasteData != null) {
                            wasteDataModelList.add(wasteData);//בודק אם האובייקט שנוצר מהנתוניים בsnapshot לא ריק ואם לא ריק, מוסיפה אותו לרשימה recycleView
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    updateTotalWeightTextView();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

            getLastWeekWeightSum();

        } else {
            goToLoginPage();
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCountdownTimer() {

        TextView TextTimer = findViewById(R.id.timerTv);

        // Get current time in milliseconds
        long currentTime = System.currentTimeMillis();

        long totalMillisecondsInAWeek = GenericUtils.getNextSundayDateInMilliseconds() - currentTime;
        new CountDownTimer(totalMillisecondsInAWeek, 1000) {
            public void onTick(long millisUntilFinished) {
                // Convert milliseconds to days, hours, minutes, and seconds
                long days = millisUntilFinished / (24 * 60 * 60 * 1000);
                long hours = (millisUntilFinished % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                long minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;

                // Set the text to display the remaining time
                TextTimer.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));
            }

            public void onFinish() {
            }
        }.start();
    }

    private void getLastWeekWeightSum() {
        wasteCollectionReference.getWasteDataForLastWeek(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WasteDataModel wasteData = dataSnapshot.getValue(WasteDataModel.class);
                    if (wasteData != null) {
                        lastWeekRecyclingSum += wasteData.getWeight();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {//מטפלת במקרה בו יש בעיה במהלך קיראה לdatabase

            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);//קביעת הפנייה
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    // פה מקשרים בין הכפתור ב-layout לactivity
    public void bindAllElements() {
        btnAddItem = findViewById(R.id.addItemButton);
        btnLogout = findViewById(R.id.logoutButton);
        welcomeTextView = findViewById(R.id.welcomeTv);
        totalWeightTextView = findViewById(R.id.sumWeightTv);
        dateTextView = findViewById(R.id.dateTv);
    }


    private void setDate() {//מעדכן את היום הראשון בשבוע
        dateTextView.setText("Week of " + GenericUtils.getHumanReadableDate(GenericUtils.getCurrentWeekSundayDateKey()));
        dateTextView.setVisibility(View.VISIBLE);
    }

    private void updateTotalWeightTextView() {
        Float sumOfWeight = 0.0f;
        for (WasteDataModel item : wasteDataModelList) {
            sumOfWeight += item.getWeight();
        }
        totalWeightTextView.setText("Total weight recycled: " + sumOfWeight + " KG");

        checkIfNeedToShowTip(sumOfWeight);
    }

    private void checkIfNeedToShowTip(Float sumOfWeight) {
        if (sumOfWeight > lastWeekRecyclingSum) {
            // TODO: Show window with difference and tip
            float percentageOfGrowth;
            String info;
            if (lastWeekRecyclingSum == 0) {
                info = String.format("Good first week! This week you recycled %s%% ", sumOfWeight);
            } else {
                percentageOfGrowth = ((sumOfWeight / lastWeekRecyclingSum)*100)-100;
                info = String.format("This week you recycled %s%% more than last week. ", percentageOfGrowth);
            }
            giveTip(info);
        } else {
            float percentageSmaller = 100 - ((sumOfWeight / lastWeekRecyclingSum)*100);
            congratulateUser(percentageSmaller);
        }
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

    private void congratulateUser(float percentageSmaller) {
        showTipFragment("",String.format("Congratulations! this week you recycled %s%% less than last week.",percentageSmaller) );
    }

    private void showTipFragment(String tipString, String titleString) {
        TipDialogFragment tipDialogFragment = new TipDialogFragment(titleString, tipString);
        tipDialogFragment.show(getSupportFragmentManager(), "AddItemDialog");
    }

    private void giveTip(String howMuchRecycledString) {
        tipCollection = firestore.collection("tips").document("1");
        tipCollection.get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Random random = new Random();

                    Map<String, Object> data = snapshot.getData();


                    if (data != null && !data.isEmpty()) {
                        int randomIndex = random.nextInt(data.size());
                        String randomField = String.valueOf(randomIndex);
                        Object randomValue = data.get(randomField);

                        if (randomValue != null) {
                            showTipFragment((String) randomValue, howMuchRecycledString);
                        }

                    }
                }
            } else {
                String error = task.getException().getMessage();
                GenericUtils.toast(error,this);
            }
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


    // זאת הפונקציה שמתחילה את התהליך של הכנסת הנתונים לdatabase
    public void addItem(WasteDataModel wasteData) {
        wasteCollectionReference.addWasteData(wasteData);
    }
}
