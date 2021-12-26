package com.example.mathtestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mathtestapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnLogin,btnRegister;
    ConstraintLayout root;
    private FirebaseAuth auth;
    DatabaseReference usersRef;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance();
        usersRef =db.getReference("Users");

        btnRegister=findViewById(R.id.RegistrationButton);
        btnLogin=findViewById(R.id.LoginButton);
        root=findViewById(R.id.root);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterWindow();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginWindow();
            }
        });

        getSupportActionBar().hide();
    }

    private void showRegisterWindow() {
        //Объект для получение шаблона окна
        LayoutInflater inflater = LayoutInflater.from(this);
        //Получаем шаблон окна register_window в переменную registerWindow
        View registerWindow =  inflater.inflate(R.layout.register_window,null);

        //Внутри этого окна будет отображаться всплывающее окно
        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(registerWindow)
            .setTitle("Регистрация")
            .setPositiveButton("Подтвердить",null)
            .setNegativeButton("Отмена",null)
            .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final EditText firstNameET = registerWindow.findViewById(R.id.nameET);
                final EditText secondNameET = registerWindow.findViewById(R.id.surnameET);
                final EditText emailET = registerWindow.findViewById(R.id.emailET);
                final EditText passwordET = registerWindow.findViewById(R.id.passwordET);
                final CheckBox isTeacherCB = registerWindow.findViewById(R.id.isTeacherCB);

                Button posButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                posButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String firstName = firstNameET.getText().toString().trim();
                        final String secondName = secondNameET.getText().toString().trim();
                        final String email = emailET.getText().toString().trim();
                        final String password = passwordET.getText().toString().trim();
                        final Boolean isTeacher =isTeacherCB.isChecked();

                        if(firstName.isEmpty()){
                            firstNameET.setError("Введите Имя");
                            firstNameET.requestFocus();
                            return;
                        }
                        if(secondName.isEmpty()){
                            secondNameET.setError("Введите Фамилию");
                            secondNameET.requestFocus();
                            return;
                        }
                        if(email.isEmpty()){
                            emailET.setError("Введите почту");
                            emailET.requestFocus();
                            return;
                        }
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            emailET.setError("Почта введена некорректно");
                            emailET.requestFocus();
                            return;
                        }

                        if(password.isEmpty()){
                            passwordET.setError("Введите пароль");
                            passwordET.requestFocus();
                            return;
                        }
                        if(password.length() < 6){
                            passwordET.setError("Пароль должен содержать 6 или более символов");
                            passwordET.requestFocus();
                            return;
                        }

                        auth.createUserWithEmailAndPassword(email,password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        User user =  new User(firstName, secondName, email, password, isTeacher);
                                        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast toast = Toast.makeText(root.getContext(), "Регистрация успешно завершена",Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mathtestapp", Context.MODE_PRIVATE);
                                                        sharedPreferences.edit().putString("email",email).apply();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast toast = Toast.makeText(root.getContext(), "Регистрация не удалась."+e.getMessage(),Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast toast = Toast.makeText(root.getContext(), "Регистрация не удалась."+e.getMessage(),Toast.LENGTH_SHORT);
                                        toast.show();
                                        dialog.dismiss();
                                    }
                                });
                    }
                });
                negButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void showLoginWindow() {
        //Объект для получение шаблона окна
        LayoutInflater inflater = LayoutInflater.from(this);
        //Получаем шаблон окна register_window в переменную registerWindow
        View loginWindow =  inflater.inflate(R.layout.login_window,null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(loginWindow)
                .setTitle("Вход в аккаунт")
                .setPositiveButton("Вход",null)
                .setNegativeButton("Отмена",null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final EditText emailET = loginWindow.findViewById(R.id.loginET);
                final EditText passwordET = loginWindow.findViewById(R.id.passwordET);

                //получаем и заполняем email из sharedPreference если есть
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.mathtestapp", Context.MODE_PRIVATE);
                final String buf_email = sharedPreferences.getString("email", null);
                if (buf_email != null){
                    emailET.setText(buf_email);
                    passwordET.requestFocus();
                }

                Button posButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                posButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String email = emailET.getText().toString();
                        final String password = passwordET.getText().toString();

                        if(email.isEmpty()){
                            emailET.setError("Введите почту");
                            emailET.requestFocus();
                            return;
                        }
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            emailET.setError("Почта введена некорректно");
                            emailET.requestFocus();
                            return;
                        }
                        if(password.isEmpty()){
                            passwordET.setError("Введите пароль");
                            passwordET.requestFocus();
                            return;
                        }
                        if(password.length() < 6){
                            passwordET.setError("Пароль должен содержать 6 или более символов");
                            passwordET.requestFocus();
                            return;
                        }

                        auth.signInWithEmailAndPassword(email,password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast toast = Toast.makeText(root.getContext(), "Авторизация удалась",Toast.LENGTH_SHORT);
                                        toast.show();
                                        sharedPreferences.edit().putString("email",email).apply();
                                        startActivity(new Intent(MainActivity.this, LevelSelectionActivity.class));
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String message = "";
                                        switch (e.getMessage()){
                                            case("There is no user record corresponding to this identifier. The user may have been deleted."):
                                                message = "Такого пользователя не существует.";
                                                break;
                                            case("The password is invalid or the user does not have a password."):
                                                message = "Введен неверный пароль.";
                                                break;
                                        }
                                        Toast toast = Toast.makeText(root.getContext(), "Авторизация не удалась. "+message,Toast.LENGTH_SHORT);
                                        toast.show();
                                        dialog.dismiss();
                                    }
                                });

                    }
                });

                negButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

}