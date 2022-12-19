package com.example.finalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MainScene extends Activity implements View.OnClickListener {
    TextView userName;
    Button exit;
    Button deleteUser;
    Button changePassword;
    Button accept;
    EditText newPassword;
    TextView errorMessage;
    AlertDialog dialog;
    Button acceptBtn, cancelBtn;

    Intent intent;

    DatabaseHandler db = new DatabaseHandler(this);
    User user = new User();

    Context a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_scene);
        userName = findViewById(R.id.userName);

        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            user = (User) arguments.getSerializable("user");
        }
        userName.setText(user.getLogin());

        exit = findViewById(R.id.exitButton);
        exit.setOnClickListener(this);
        deleteUser = findViewById(R.id.deleteUserButton);
        deleteUser.setOnClickListener(this);
        changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(this);
        accept = findViewById(R.id.acceptNewPassword);
        accept.setOnClickListener(this);

        newPassword = findViewById(R.id.newPassword);
        errorMessage = findViewById(R.id.errorNewPassword);

        intent = new Intent(this, InitScene.class);
        a = MainScene.this.getApplicationContext();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


    public void customExitDialog() {
        // creating custom dialog
        final Dialog dialog = new Dialog(MainScene.this);

        // setting content view to dialog
        dialog.setContentView(R.layout.change_pwd);

        // getting reference of TextView
        acceptBtn = findViewById(R.id.acceptButton);
        cancelBtn = findViewById(R.id.cancelButton);

        // click listener for No
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss the dialog
                dialog.dismiss();

            }
        });

        // click listener for Yes
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                // and exit the exit
                dialog.dismiss();
                finish();

            }
        });

        // show the exit dialog
        dialog.show();
    }

    /*public void dialogHandler(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setView(R.layout.change_pwd);
                LayoutInflater inflater = this.getLayoutInflater();
                //View dialogView = inflater.inflate(R.layout.change_pwd,null);
                View layout = inflater.inflate(R.layout.change_pwd, null);
                builder.setView(layout);
                *//*builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });*//*

                dialog = builder.create();


                acceptBtn = findViewById(R.id.acceptButton);
                //acceptBtn.setOnClickListener(this);

                acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                cancelBtn = findViewById(R.id.cancelButton);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });

        dialog.show();
                //cancelBtn.setOnClickListener(this);
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exitButton:
                startActivity(intent);
                break;

            case R.id.deleteUserButton:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.deleteUserFromDB(user);
                        db.close();
                    }
                }).start();
                startActivity(intent);
                break;

            case R.id.changePassword:


                CustomDialog cdd = new CustomDialog(this, user);
                cdd.show();

                //customExitDialog();
                //dialogHandler();

                //newPassword.setVisibility(View.VISIBLE);
                //accept.setVisibility(View.VISIBLE);
                break;

            case R.id.acceptNewPassword:

                String newPwd = newPassword.getText().toString();

                if(newPwd.equals("")){
                    errorMessage.setText("Введите новый пароль!");
                    errorMessage.setVisibility(View.VISIBLE);
                }else if(newPwd.equals(user.getPass())){
                    errorMessage.setText("Пароли совпадают!");
                    errorMessage.setVisibility(View.VISIBLE);
                }else {
                    errorMessage.setText("Пароль успешно изменен!");
                    errorMessage.setVisibility(View.VISIBLE);
                    newPassword.setVisibility(View.INVISIBLE);
                    accept.setVisibility(View.INVISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.updateUserPassword(user, newPwd);
                            db.close();
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }
}
