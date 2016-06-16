package edu.fernandokaway.votafct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.spongycastle.util.encoders.Base64;

import javax.crypto.SecretKey;

import services.Auth;

public class MainActivity extends AppCompatActivity {

    private TextView infoTV;
    private EditText raET;
    private EditText senhaET;
    private Button okBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTV = (TextView)findViewById(R.id.infoTextView);
        raET = (EditText)findViewById(R.id.RAEditText);
        senhaET = (EditText)findViewById(R.id.senhaEditText);
        okBT = (Button)findViewById(R.id.okButton);

        infoTV.setText("Imposte RA e senha");

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String RA = raET.getText().toString();
                        SecretKey sk = Auth.JPAKEAuth(RA,senhaET.getText().toString());
                        Log.i("Secret Key", Base64.toBase64String(sk.getEncoded()));
                        Intent i = new Intent(getApplicationContext(), VotacaoActivity.class);
                        i.putExtra("RA", RA);
                        i.putExtra("SK", sk);
                        startActivity(i);
                    }
                }).start();
            }

        });
    }
}
