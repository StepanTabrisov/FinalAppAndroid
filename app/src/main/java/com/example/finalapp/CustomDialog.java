package com.example.finalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {
    Context c;
    Button acceptBtn, cancelBtn;
    EditText newPwd, acceptPwd;
    TextView errorPwd;

    String newPassword, acceptPassword;
    User user;

    //DatabaseHandler db = new DatabaseHandler(this);
    DatabaseHandler db;

    public CustomDialog(@NonNull Activity context, User user) {
        super(context);
        c = context;
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(c);

        setContentView(R.layout.change_pwd);
        setTitle("Изменение пароля");

        acceptBtn = findViewById(R.id.acceptButton);
        cancelBtn = findViewById(R.id.cancelButton);
        newPwd = findViewById(R.id.dNewPassword);
        acceptPwd = findViewById(R.id.dAcceptNewPassword);
        errorPwd = findViewById(R.id.changePasswordError);

        acceptBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptButton:
                System.out.println("1");

                newPassword = newPwd.getText().toString().trim();
                acceptPassword = acceptPwd.getText().toString().trim();

                if(newPassword.equals("") || acceptPassword.equals("")){
                    errorPwd.setText("Заполните поля!");
                    errorPwd.setVisibility(View.VISIBLE);

                }else if(newPassword.equals(user.getPass())){
                    errorPwd.setText("Пароль совпадает со старым!");
                    errorPwd.setVisibility(View.VISIBLE);

                }else if(!newPassword.equals(acceptPassword)){
                    errorPwd.setText("Пароли не совпадают!");
                    errorPwd.setVisibility(View.VISIBLE);
                }else{

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.updateUserPassword(user, newPassword);
                            db.close();
                        }
                    }).start();

                    this.cancel();
                }


                break;
            case R.id.cancelButton:
                //cancel();
                this.cancel();
                //dismiss();
                break;
            default:
                break;
        }
        //dismiss();
    }
}