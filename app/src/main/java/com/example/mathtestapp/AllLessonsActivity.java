package com.example.mathtestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.mathtestapp.models.Lesson;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AllLessonsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference users, lessons;
    FirebaseUser currentUser;

    ListView lessonList;
    Button addButton;
    EditText lessonNameET;
    LinearLayout addLessonLL;

    String lessonLevel;
    boolean isTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mathtestapp", Context.MODE_PRIVATE);
        isTeacher = sharedPreferences.getBoolean("isTeacher",false);

        lessonLevel = getIntent().getStringExtra("LessonLevel");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser =firebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
/*        users = mDatabase.getReference("Users");*/
        lessons = mDatabase.getReference("Lessons");

        addButton = findViewById(R.id.addLessonButton);
        lessonNameET = findViewById(R.id.lessonName);
        addLessonLL = findViewById(R.id.addLesson);
        lessonList = findViewById(R.id.LessonList);

        lessons.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int i = 0;
                for (DataSnapshot snap : snapshot.getChildren()){
                    if (lessonLevel.equals(snap.child("level").getValue(String.class))) {
                        i++;
                    }
                }
                String names[] = new String[(int)i];
                int j = 0;
                for (DataSnapshot snap : snapshot.getChildren()){
                    if (lessonLevel.equals(snap.child("level").getValue(String.class))) {
                        names[j] = snap.child("name").getValue(String.class);
                        j++;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter <> (AllLessonsActivity.this, android.R.layout.simple_list_item_1, names);
                lessonList.setAdapter(adapter);

                lessonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(AllLessonsActivity.this, ViewLessonActivity.class);
                        intent.putExtra("SelectedLesson",names[i]);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (isTeacher){
            addLessonLL.setVisibility(View.VISIBLE);
        }

    }

    public void addLesson(View view){
        final String lessonName = lessonNameET.getText().toString().trim();
        if(lessonName.isEmpty()){
            lessonNameET.setError("Название урока не может быть пустое");
            lessonNameET.requestFocus();
            return;
        }

        lessons.push().setValue(new Lesson(lessonName,"",lessonLevel));
        lessonNameET.setText("");
    }
}