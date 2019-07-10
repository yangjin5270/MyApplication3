package com.td.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    private Button button_start;
    private Button button_exit;
    private ToggleButton button_state;
    private String account;
    private String password;
    private final int loginRequstCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        button_start = (Button)findViewById(R.id.start);
        button_state=(ToggleButton)findViewById(R.id.toggleButton);
        button_start.setOnClickListener(this);
        button_exit =(Button)findViewById(R.id.exit);
        button_exit.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start:
                login();
                break;
            case R.id.exit:
                exit();
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case loginRequstCode:
                Toast.makeText(this, data.getStringExtra("cookies"), Toast.LENGTH_SHORT).show();
                break;
                default:
                    break;
        }

    }

    public void login(){
        Intent loginIntent = new Intent(this,loginActivity.class);
        EditText editText = (EditText)findViewById(R.id.account_md) ;
        account = editText.getText().toString();
        loginIntent.putExtra("account",account);
        loginIntent.putExtra("password",password);
        startActivityForResult(loginIntent,loginRequstCode);

    }

    private void exit(){
        this.finish();
    }

}
