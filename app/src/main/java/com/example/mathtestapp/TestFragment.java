package com.example.mathtestapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mathtestapp.models.Test;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class TestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    boolean isTeacher;


    Integer latestNumber;
    Integer rightCount;
    ArrayList<Test> testsFromDB;
    Integer[] answerButtonsIdArr;

    Button[] answerButtonsArr;
    Button nextQuestionButton;
    Button b1,b2,b3,b4;
    Button startTestButton;
    TextView questionTV, resultsTV;
    ListView allTestsList;
    GridLayout gridLayout;
    FloatingActionButton addButton;
    LayoutInflater layoutInflater;

    FirebaseListAdapter<Test> adapter;
    FirebaseDatabase mDatabase;
    DatabaseReference lessons, tests;

    public TestFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String lessonName) {
        TestFragment fragment = new TestFragment();
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
        layoutInflater = inflater;
        View v = inflater.inflate(R.layout.fragment_test, container, false);
        addButton = v.findViewById(R.id.addTestButton);
        startTestButton = v.findViewById(R.id.startTest);
        allTestsList = v.findViewById(R.id.allTestsList);
        gridLayout = v.findViewById(R.id.grid_view);
        mDatabase = FirebaseDatabase.getInstance();
        lessons = mDatabase.getReference("Lessons");

        testsFromDB = new ArrayList<Test>();
        latestNumber = 0;
        rightCount=0;

        allTestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DatabaseReference currentField = adapter.getRef(i);
                EditText(currentField);

            }
        });

        allTestsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final DatabaseReference currentField = adapter.getRef(i);
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
                dialog.setTitle("Удалить");
                dialog.setMessage("Вы уверены?");
                dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentField.removeValue();
                    }
                });
                dialog.show();
                return true;
            }
        });

        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionTV.setVisibility(View.VISIBLE);
                if (!testsFromDB.isEmpty())
                    gridLayout.setVisibility(View.VISIBLE);
                startTestButton.setVisibility(View.GONE);
            }
        });

        questionTV = v.findViewById(R.id.questionTV);
        resultsTV = v.findViewById(R.id.resultsTextView);
        answerButtonsIdArr = new Integer[]{R.id.v_1,R.id.v_2,R.id.v_3,R.id.v_4};
        b1 = v.findViewById(R.id.v_1);
        b2 = v.findViewById(R.id.v_2);
        b3 = v.findViewById(R.id.v_3);
        b4 = v.findViewById(R.id.v_4);
        answerButtonsArr = new Button[]{b1,b2,b3,b4};

        nextQuestionButton = v.findViewById(R.id.nextQuestionButton);

        View.OnClickListener onTestClickListener = new TestClickListener();
        b1.setOnClickListener(onTestClickListener);
        b2.setOnClickListener(onTestClickListener);
        b3.setOnClickListener(onTestClickListener);
        b4.setOnClickListener(onTestClickListener);
        nextQuestionButton.setOnClickListener(onTestClickListener);


        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("com.example.mathtestapp", Context.MODE_PRIVATE);
        isTeacher = sharedPreferences.getBoolean("isTeacher",false);

        if(isTeacher){
            allTestsList.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            startTestButton.setVisibility(View.GONE);
        }
        else{
            startTestButton.setVisibility(View.VISIBLE);
        }

        lessons.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    if (mParam1.equals(snap.child("name").getValue(String.class))){
                        tests = lessons.child(snap.getKey()).child("Tests");
                        for (DataSnapshot snap2 : snap.child("Tests").getChildren()){
                            testsFromDB.add(snap2.getValue(Test.class));
                        }
                        if (testsFromDB.isEmpty()) {
                            questionTV.setText("По данному уроку пока нет тестов..");
                            return;
                        }
                        displayTests();
                        setNextQuestion();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText(null);
            }
        });

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        return v;
    }

    public void EditText(DatabaseReference dbRef){
        //Получаем шаблон окна register_window в переменную registerWindow
        View addTestWindow =  layoutInflater.inflate(R.layout.add_test,null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(addTestWindow)
                .setTitle("Новый вопрос")
                .setPositiveButton("Добавить",null)
                .setNegativeButton("Отмена",null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                final EditText questionET = addTestWindow.findViewById(R.id.editTextQuestion);
                final EditText firstAnswerET = addTestWindow.findViewById(R.id.editTextFirstAnswer);
                final EditText secondAnswerET = addTestWindow.findViewById(R.id.editTextSecondAnswer);
                final EditText thirdAnswerET = addTestWindow.findViewById(R.id.editTextThirdAnswer);
                final EditText fourthAnswerET = addTestWindow.findViewById(R.id.editTextFourthAnswer);

                if (dbRef != null){
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Test t = snapshot.getValue(Test.class);
                            questionET.setText(t.getQuestion());
                            firstAnswerET.setText(t.getAnswer1());
                            secondAnswerET.setText(t.getAnswer2());
                            thirdAnswerET.setText(t.getAnswer3());
                            fourthAnswerET.setText(t.getAnswer4());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                final RadioGroup radioGroup = addTestWindow.findViewById(R.id.testRadioGroup);

                Button posButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                mDatabase = FirebaseDatabase.getInstance();
                lessons = mDatabase.getReference("Lessons");

                if (getArguments() != null) {
                    mParam1 = getArguments().getString(ARG_PARAM1);
                    lessons.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()){
                                if (mParam1.equals(snap.child("name").getValue(String.class)))
                                {
                                    posButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            final String question = questionET.getText().toString();

                                            final String firstAnswer = firstAnswerET.getText().toString();
                                            final String secondAnswer = secondAnswerET.getText().toString();
                                            final String thirdAnswer = thirdAnswerET.getText().toString();
                                            final String fourthAnswer = fourthAnswerET.getText().toString();


                                            final int selectedId = radioGroup.getCheckedRadioButtonId();

                                            if(question.isEmpty()){
                                                questionET.setError("Поле должно быть заполнено");
                                                questionET.requestFocus();
                                                return;
                                            }

                                            if(firstAnswer.isEmpty()){
                                                firstAnswerET.setError("Поле должно быть заполнено");
                                                firstAnswerET.requestFocus();
                                                return;
                                            }
                                            if(secondAnswer.isEmpty()){
                                                secondAnswerET.setError("Поле должно быть заполнено");
                                                secondAnswerET.requestFocus();
                                                return;
                                            }
                                            if(thirdAnswer.isEmpty()){
                                                thirdAnswerET.setError("Поле должно быть заполнено");
                                                thirdAnswerET.requestFocus();
                                                return;
                                            }
                                            if(fourthAnswer.isEmpty()){
                                                fourthAnswerET.setError("Поле должно быть заполнено");
                                                fourthAnswerET.requestFocus();
                                                return;
                                            }

                                            final String rightAnswer;
                                            switch (selectedId){
                                                case (-1):
                                                    Toast toast = Toast.makeText(getContext(), "Необходимо выбрать правильный вариант ответа",Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                case (R.id.firstAnswerRB):
                                                    rightAnswer = firstAnswer;
                                                    break;
                                                case (R.id.secondAnswerRB):
                                                    rightAnswer = secondAnswer;
                                                    break;
                                                case (R.id.thirdAnswerRB):
                                                    rightAnswer = thirdAnswer;
                                                    break;
                                                case (R.id.fourthAnswerRB):
                                                    rightAnswer = fourthAnswer;
                                                    break;
                                                default:
                                                    rightAnswer = "";
                                            }

                                            if (dbRef!=null){
                                                dbRef.setValue(new Test(question,
                                                        firstAnswer,
                                                        secondAnswer,
                                                        thirdAnswer,
                                                        fourthAnswer,
                                                        rightAnswer
                                                ));
                                                dialog.dismiss();
                                            }
                                            else {
                                                String uid = snap.getKey();
                                                tests = lessons.child(uid).child("Tests");
                                                tests.push().setValue(new Test(question,
                                                        firstAnswer,
                                                        secondAnswer,
                                                        thirdAnswer,
                                                        fourthAnswer,
                                                        rightAnswer
                                                ));
                                            }
                                            Toast toast = Toast.makeText(getContext(), "Изменения сохранены",Toast.LENGTH_SHORT);
                                            toast.show();
                                            questionET.setText("");
                                            firstAnswerET.setText("");
                                            secondAnswerET.setText("");
                                            thirdAnswerET.setText("");
                                            fourthAnswerET.setText("");
                                            radioGroup.clearCheck();
                                        }
                                    });

                                    negButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }
        });
        dialog.show();
    }

    public void setNextQuestion(){
        Test temp = testsFromDB.get(latestNumber);
        questionTV.setText(temp.getQuestion());
        b1.setText(temp.getAnswer1());
        b2.setText(temp.getAnswer2());
        b3.setText(temp.getAnswer3());
        b4.setText(temp.getAnswer4());
    }

    public void AnswerSelectedMode(boolean bool){
        for (Button button:answerButtonsArr) {
            button.setEnabled(!bool);
            if (!bool)
                button.setBackgroundResource(R.drawable.btn_norm);
        }
        if (bool) {
            if (latestNumber == testsFromDB.size() - 1)
                nextQuestionButton.setText("Завершить");
            nextQuestionButton.setVisibility(View.VISIBLE);
        }
        else
            nextQuestionButton.setVisibility(View.GONE);
    }


    private class TestClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Test currentTest = testsFromDB.get(latestNumber);
            String rightAnswer = currentTest.getRightAnswer();

            if (Arrays.asList(answerButtonsIdArr).contains(id)) {
                int tmp=-1;
                String chosenAnswer="";
                if (id == R.id.v_1) {
                    chosenAnswer = currentTest.getAnswer1();
                } else if (id == R.id.v_2) {
                    chosenAnswer = currentTest.getAnswer2();
                } else if (id == R.id.v_3) {
                    chosenAnswer = currentTest.getAnswer3();
                } else if (id == R.id.v_4) {
                    chosenAnswer = currentTest.getAnswer4();
                }

                if (chosenAnswer.equals(rightAnswer)) {
                    view.setBackgroundResource(R.drawable.btn_right);
                    rightCount++;
                }
                else {
                    view.setBackgroundResource(R.drawable.btn_wrong);
                }
                AnswerSelectedMode(true);
            }
            else if (id == R.id.nextQuestionButton){
                if (latestNumber<testsFromDB.size()-1) {
                    latestNumber++;
                    AnswerSelectedMode(false);
                    setNextQuestion();
                }
                else{
                    nextQuestionButton.setVisibility(View.GONE);
                    gridLayout.setVisibility(View.GONE);
                    questionTV.setVisibility(View.GONE);
                    resultsTV.setText("Результаты: "+rightCount+" / "+testsFromDB.size()+" правильно");
                    resultsTV.setVisibility(View.VISIBLE);
                }
            }



        }
    }



    private void displayTests(){
        adapter =new FirebaseListAdapter<Test>(getActivity(),Test.class,R.layout.test_item,tests) {
            @Override
            protected void populateView(View v, Test model, int position) {
                TextView testName;
                testName = v.findViewById(R.id.testName);

                final DatabaseReference currentField = adapter.getRef(position);

                testName.setText(model.getQuestion());

            }
        };
        allTestsList.setAdapter(adapter);
    }


}