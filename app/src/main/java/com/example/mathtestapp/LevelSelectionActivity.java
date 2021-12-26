package com.example.mathtestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LevelSelectionActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference users;
    FirebaseUser currentUser;

    String currentUserKey;
    boolean isTeacher;

    TextView testUser;
    Button elementaryButton, intermediateButton, highButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        users = mDatabase.getReference("Users");

        currentUser =firebaseAuth.getCurrentUser();
        currentUserKey = currentUser.getUid();

        testUser = findViewById(R.id.testUser);
        elementaryButton = findViewById(R.id.elementarySchool);
        intermediateButton = findViewById(R.id.intermediateSchool);
        highButton = findViewById(R.id.highSchool);

        View.OnClickListener onClickListener = new ClickListener();
        elementaryButton.setOnClickListener(onClickListener);
        intermediateButton.setOnClickListener(onClickListener);
        highButton.setOnClickListener(onClickListener);

        users.child(currentUserKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("firstName").getValue(String.class)+
                        " "+snapshot.child("secondName").getValue(String.class);
                testUser.setText("Добрый день, "+userName);

                //добавляем в sharedPreference isTeacher для других активностей
                isTeacher = snapshot.child("isTeacher").getValue(Boolean.class);
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mathtestapp", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isTeacher",isTeacher).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            if(id==R.id.elementarySchool)
            {
                intent = new Intent(LevelSelectionActivity.this, AllLessonsActivity.class);
                intent.putExtra("LessonLevel","Elementary");
                startActivity(intent);
            }
            else if (id==R.id.intermediateSchool)
            {
                intent = new Intent(LevelSelectionActivity.this, AllLessonsActivity.class);
                intent.putExtra("LessonLevel","Intermediate");
                startActivity(intent);
            }
            else if (id==R.id.highSchool)
            {
                intent = new Intent(LevelSelectionActivity.this, AllLessonsActivity.class);
                intent.putExtra("LessonLevel","High");
                startActivity(intent);
            }

        }
    }


}