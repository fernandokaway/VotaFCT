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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fernandokaway on 6/14/16.
 */
public class Auth {

    private static IPAddress ipAddress = new IPAddress();

    public static SecretKey JPAKEAuth(String id, String password){
        JPAKEPrimeOrderGroup group = JPAKEPrimeOrderGroups.NIST_3072;
        Digest digest = new SHA256Digest();
        SecureRandom random = new SecureRandom();
        JPAKEParticipant jpakeParticipant = new JPAKEParticipant(
                id,password.toCharArray(),group,digest,random);
        //password = null;

        //1st round of JPAKE
        JPAKERound1Payload jpakeRound1Payload = jpakeParticipant.createRound1PayloadToSend();


        //Send payload 1 to server and receive JPAKERound1Payload from server
        String payload1 = null;
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(ipAddress.getAddress()+"auth/round1/"+id+","+jpakeRound1Payload.getGx1().toString()+","+ jpakeRound1Payload.getGx2().toString()+","+
                jpakeRound1Payload.getKnowledgeProofForX1()[0] +","+jpakeRound1Payload.getKnowledgeProofForX1()[1] +","+
                jpakeRound1Payload.getKnowledgeProofForX2()[0] +","+jpakeRound1Payload.getKnowledgeProofForX2()[1]);
        HttpResponse response = null;
        try {
            response = client.execute(request);
            payload1 = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Rebuild server payload 1
        String payload[] = payload1.split(",");
        BigInteger KPx1[] = new BigInteger[2];
        BigInteger KPx2[] = new BigInteger[2];
        KPx1[0] = new BigInteger(payload[2]);
        KPx1[1] = new BigInteger(payload[3]);
        KPx2[0] = new BigInteger(payload[4]);
        KPx2[1] = new BigInteger(payload[5].substring(0,payload[5].length()-1));
        JPAKERound1Payload serverRound1Payload = new JPAKERound1Payload("server", new BigInteger(payload[0]), new BigInteger(payload[1]), KPx1, KPx2);

        //validate server 1st round payload
        try {
            jpakeParticipant.validateRound1PayloadReceived(serverRound1Payload);
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        //create 2nd round payload
        JPAKERound2Payload jpakeRound2Payload = jpakeParticipant.createRound2PayloadToSend();

        //Send payload 2 to server and receive JPAKERound2Payload from server
        String payload2 = null;
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        request = new HttpGet(ipAddress.getAddress()+"auth/round2/"+id+","+jpakeRound2Payload.getA().toString()+","+
                jpakeRound2Payload.getKnowledgeProofForX2s()[0]+","+jpakeRound2Payload.getKnowledgeProofForX2s()[1]);
        response = null;
        try {
            response = client.execute(request);
            payload2 = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Parse payload 2
        payload = payload2.split(",");
        BigInteger KPX2s[] = new BigInteger[2];
        KPX2s[0] = new BigInteger(payload[1]);
        KPX2s[1] = new BigInteger(payload[2].substring(0, payload[2].length()-1));

        //validate server 2nd round payload
        try {
            jpakeParticipant.validateRound2PayloadReceived(new JPAKERound2Payload("server",
                    new BigInteger(payload[0]), KPX2s));
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        BigInteger keyMaterial = jpakeParticipant.calculateKeyingMaterial();

        return deriveSessionKey(keyMaterial);
    }

    private static SecretKey deriveSessionKey(BigInteger keyingMaterial) {
        SHA256Digest digest = new SHA256Digest();
        byte[] keyByteArray = keyingMaterial.toByteArray();
        byte[] output = new byte[digest.getDigestSize()];

        digest.update(keyByteArray, 0, keyByteArray.length);
        digest.doFinal(output, 0);

        return new SecretKeySpec(output, "AES");
    }
}
