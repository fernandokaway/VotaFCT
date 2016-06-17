package services;

import android.os.Build;
import android.os.StrictMode;

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

    public static ArrayList<Pergunta> getPerguntas(String id, SecretKey sk, byte[] iv){
        String cipher = null;
        String xml = null;
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(ipAddress.getAddress()+"perguntas/"+id+","+iv);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            cipher = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Pergunta> perguntas = parsePerguntas(Crypto.decryptString(cipher, sk, iv));

        return perguntas;
    }

}
