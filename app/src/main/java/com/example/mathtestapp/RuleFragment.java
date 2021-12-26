package com.example.mathtestapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RuleFragment extends Fragment {

    EditText ruleET;
    FloatingActionButton saveChanges;
    FirebaseDatabase mDatabase;
    DatabaseReference lessons;

    boolean isTeacher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;


    public RuleFragment() {
        // Required empty public constructor
    }

    public static RuleFragment newInstance(String lessonName) {
        RuleFragment fragment = new RuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, lessonName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rule, container, false);
        ruleET = v.findViewById(R.id.ruleET);
        saveChanges = v.findViewById(R.id.saveChanges);

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("com.example.mathtestapp", Context.MODE_PRIVATE);
        isTeacher = sharedPreferences.getBoolean("isTeacher",false);

        if (isTeacher){
            saveChanges.setVisibility(View.VISIBLE);
        }
        else{
            ruleET.setHint("");
            ruleET.setFocusable(false);
        }

        mDatabase = FirebaseDatabase.getInstance();
        lessons = mDatabase.getReference("Lessons");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            lessons.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if (mParam1.equals(snap.child("name").getValue(String.class))){
                            String uid = snap.getKey();
                            String lessonRule = snap.child("rule").getValue(String.class);
                            ruleET.setText(lessonRule);
                            saveChanges.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    lessons.child(uid).child("rule").setValue(ruleET.getText().toString().trim());
                                    Toast toast = Toast.makeText(getContext(), "Изменения сохранены",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return v;
    }

}