package services;

import android.os.Build;
import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import domain.Alternativa;
import domain.Pergunta;
import tools.Crypto;
import tools.Tools;
import tools.XMLParser;

/**
 * Created by fernandokaway on 6/17/16.
 */
public class Perguntas {

    private static IPAddress ipAddress = new IPAddress();

    public static ArrayList<Pergunta> getPerguntas(String id, SecretKey sk, byte[] iv){
        String cipher = null;
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(ipAddress.getAddress()+"perguntas/"+id+","+ Tools.bytesToHex(iv));
        HttpResponse response = null;

        try {
            response = client.execute(request);
            cipher = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String xml = Crypto.decryptString(cipher.substring(0, cipher.length()-1), sk, iv);

        ArrayList<Pergunta> perguntas = parsePerguntas(xml);

        return perguntas;
    }

    private static ArrayList<Pergunta> parsePerguntas(String xml){
        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(xml);

        NodeList perguntasNL = doc.getElementsByTagName("Ps");
        Element e0 = (Element) perguntasNL.item(0);
        NodeList perguntaNL = e0.getElementsByTagName("p");
        String[] parts, parts2;

        ArrayList<Pergunta> perguntas = new ArrayList<>();

        for(int i=0; i< perguntaNL.getLength(); i++) {
            Element e = (Element) perguntaNL.item(i);
            parts = parser.getValue(e,"it").split(",");
            Pergunta pergunta = new Pergunta(Integer.parseInt(parts[0]),parts[1]);
            NodeList alternativasNL = e.getElementsByTagName("as");
            ArrayList<Alternativa> alternativas = new ArrayList<>();
            for (int j = 0; j < alternativasNL.getLength(); j++) {
                Element e1 = (Element) alternativasNL.item(j);
                parts2 = parser.getValue(e1, "a").split(",");
                alternativas.add(new Alternativa(Integer.parseInt(parts2[0]),parts2[1]));
            }
            pergunta.setAlternativas(alternativas);
            perguntas.add(pergunta);
        }
        return perguntas;
    }

}
