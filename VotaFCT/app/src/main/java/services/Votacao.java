package services;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.spongycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.spongycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.spongycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.spongycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.spongycastle.crypto.digests.SHA256Digest;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import domain.Pergunta;
import tools.Crypto;

/**
 * Created by fernandokaway on 6/17/16.
 */
public class Votacao {

    private static IPAddress ipAddress = new IPAddress();

    public static String getConfirmacaoVoto(String id, SecretKey sk, byte[] iv, int[] alternativas){
        String cipher = null;
        String saida = ""+id;
        String resposta = null;
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        for(int i = 0; i < alternativas.length; i++) saida += ","+alternativas[i];

        Log.i("Saida",saida);

        cipher = Crypto.encryptString(saida, sk, iv);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(ipAddress.getAddress()+"votacao/"+id+","+cipher);

        Log.i("Request",ipAddress.getAddress()+"votacao/"+id+","+cipher);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            resposta = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resposta;
    }

}
