package edu.fernandokaway.votafct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import javax.crypto.SecretKey;

public class VotacaoActivity extends AppCompatActivity {

    private TextView perguntaTV;
    private Button retornarBT;
    private Button votarBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votacao);

        String RA = (String)getIntent().getSerializableExtra("RA");
        SecretKey sk = (SecretKey)getIntent().getSerializableExtra("SK");

        perguntaTV = (TextView)findViewById(R.id.perguntaTextView);
        retornarBT = (Button)findViewById(R.id.retornarButton);
        votarBT = (Button)findViewById(R.id.votarButton);

        perguntaTV.setText("Fora Dunga?");

        addRadioButtons(2);
    }

    //public void addRadioButtons(int number, ArrayList<String> titulos)
    public void addRadioButtons(int number) {

        for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            for (int i = 1; i <= number; i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId((row * 2) + i);
                //rdbtn.setText(titulos.get(i);
                rdbtn.setText("Radio " + rdbtn.getId());
                ll.addView(rdbtn);
            }
            ((ViewGroup) findViewById(R.id.respostaRadioGroup)).addView(ll);
        }
    }
}