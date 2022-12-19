package com.example.finalapp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import androidx.annotation.Nullable;

import java.util.concurrent.Callable;

public class EnterScene extends Activity {
    TextView changeTextPrev;
    TextView errorMessage;
    Button mainBtn;
    EditText inputName;
    EditText inputPassword;
    User user;
    DatabaseHandler db = new DatabaseHandler(this);
    Thread t;
    FutureTask<Boolean> future;
    FutureTask<Void> future1;
    Callable task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enter_scene);

        final Context a = EnterScene.this.getApplicationContext();

        changeTextPrev = findViewById(R.id.enterTEXT);
        mainBtn = findViewById(R.id.regenterBtn);

        inputName = findViewById(R.id.inputName);
        inputPassword = findViewById(R.id.inputPassword);

        errorMessage = findViewById(R.id.errorMessage);

        Intent intent = new Intent(this, InitScene.class);
        Bundle changeMode = this.getIntent().getExtras();

        int flag = changeMode.getInt("mode");
        Intent intent1 = new Intent(this, MainScene.class);



        if(flag == 1){

            changeTextPrev.setText("Регистрация");
            mainBtn.setText("Регистрация");

            mainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputName.getText().toString().trim().equals("") || inputPassword.getText().toString().trim().equals("")) {
                        errorMessage.setText("Заполните поля регистрации");
                        errorMessage.setVisibility(View.VISIBLE);
                    }else {
                        user = new User(inputName.getText().toString().trim(), inputPassword.getText().toString().trim());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                boolean check = user.checkName(db);

                           /* try {
                                Thread.sleep(15000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
*/
                                if(check){
                                    errorMessage.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorMessage.setText("Пользователь с таким именем уже существует!");
                                            errorMessage.setVisibility(View.VISIBLE);
                                        }
                                    });

                                }else if(user.checkPass()){
                                    errorMessage.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorMessage.setText("Пароль менее 8 символов!");
                                            errorMessage.setVisibility(View.VISIBLE);
                                        }
                                    });

                                }else {
                                    errorMessage.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorMessage.setText("Регистрация прошла успешно!");
                                            errorMessage.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    Log.i("MESSAGE", "onClick: USER ADD");

                                    db.addUser(user);
                                    db.close();

                                    intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                    intent1.putExtra("user", user);
                                    a.startActivity(intent1);
                                }
                            }
                        }).start();
                    }
                }
            });
        }else {
            mainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(inputName.getText().toString().trim().equals("") || inputPassword.getText().toString().trim().equals("")) {
                        errorMessage.setText("Заполните поля!");
                        errorMessage.setVisibility(View.VISIBLE);
                    }else{
                        User logUser = new User(inputName.getText().toString().trim(), inputPassword.getText().toString().trim());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean check =  CheckUser(logUser);

                                if(check){
                                    db.close();
                                    intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                    intent1.putExtra("user", logUser);
                                    startActivity(intent1);
                                }else{
                                    errorMessage.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorMessage.setText("Неверный логин или пароль!");
                                            errorMessage.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }

                            }
                        }).start();

                       /* task = () -> {
                            System.out.println("new thread: Check user");
                            return CheckUser(logUser);
                        };
                        future = new FutureTask<>(task);
                        new Thread(future).start();

                        boolean check = false;
                        try {
                            check = future.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(check){
                            db.close();
                            intent1.putExtra("user", logUser);
                            startActivity(intent1);
                        }else{
                            errorMessage.setText("Неверный логин или пароль!");
                            errorMessage.setVisibility(View.VISIBLE);
                        }*/
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public boolean CheckUser(User user) {
        return db.selectUserData(user);
    }
}
