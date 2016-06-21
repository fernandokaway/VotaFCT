package edu.fernandokaway.votafct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import domain.Alternativa;
import domain.Pergunta;
import services.Perguntas;
import services.Votacao;

public class VotacaoActivity extends AppCompatActivity {

    private TextView perguntaTV;
    private Button retornarBT;
    private Button votarBT;
    private ArrayList<Pergunta> perguntas;
    private SecretKey sk;
    private String RA;
    private byte[] iv;
    private int contPerguntas;
    private RadioGroup rg;
    private int[] alternativas;
    private int alternativa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votacao);

        votarBT = (Button)findViewById(R.id.votarButton);
        retornarBT = (Button)findViewById(R.id.retornarButton);
        perguntaTV = (TextView)findViewById(R.id.perguntaTextView);
        rg = (RadioGroup)findViewById(R.id.respostaRadioGroup);

        RA = (String) getIntent().getSerializableExtra("RA");
        sk = (SecretKey) getIntent().getSerializableExtra("SK");
        contPerguntas = 0;

        iv = new SecureRandom().generateSeed(16);
        perguntas = Perguntas.getPerguntas(RA,sk,iv);
        perguntaTV.setText(perguntas.get(0).getTitulo());
        addRadioButtons(perguntas.get(0).getAlternativas());

        alternativas = new int[perguntas.size()];

        votarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: arrumar indice do radio group
                if(alternativa!=-1) {
                    if (contPerguntas + 1 < perguntas.size()) {
                        alternativas[contPerguntas] = alternativa;
                        contPerguntas += 1;
                        perguntaTV.setText(perguntas.get(contPerguntas).getTitulo());
                        addRadioButtons(perguntas.get(contPerguntas).getAlternativas());
                        alternativa = -1;
                    } else {
                        alternativas[contPerguntas] = alternativa;
                        for(int k = 0; k < alternativas.length; k++) Log.i("Alt "+k,""+alternativas[k]);
                        Votacao.getConfirmacaoVoto(RA, sk, iv, alternativas);
                        //TODO: fazer dialog
                    }
                }
                else{
                    Toast.makeText(VotacaoActivity.this, "Escolha um voto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retornarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(contPerguntas > 0){
                    contPerguntas -= 1;
                    perguntaTV.setText(perguntas.get(contPerguntas).getTitulo());
                    addRadioButtons(perguntas.get(contPerguntas).getAlternativas());
                }
            }
        });
    }

    public void addRadioButtons(final ArrayList<Alternativa> alternativas ){

            RadioGroup ll = new RadioGroup(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < alternativas.size(); i++) {
                RadioButton rdbtn = new RadioButton(this);
                Log.i("alternativa " + i, ""+alternativas.get(i).getId());
                rdbtn.setId(alternativas.get(i).getId());
                rdbtn.setText(alternativas.get(i).getTitulo());
                final int finalI = i;
                rdbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alternativa = alternativas.get(finalI).getId();
                    }
                });
                ll.addView(rdbtn);
            }
            rg.clearCheck();
            rg.removeAllViews();
            rg.addView(ll);

    }
}