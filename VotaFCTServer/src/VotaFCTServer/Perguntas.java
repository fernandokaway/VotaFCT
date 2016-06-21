package VotaFCTServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import database.PerguntasDAO;
import database.UserDAO;
import domain.Pergunta;
import tools.Crypto;
import tools.Tools;

@Path("perguntas")
public class Perguntas {
	

	@GET
	@Path("/{RA},{iv}")
	@Produces("text/plain")
    public StreamingOutput getPerguntas(@PathParam("RA") String id, 
    		@PathParam("iv") String iv)
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		PerguntasDAO pdao = new PerguntasDAO();
		final ArrayList<Pergunta> perguntas = pdao.recuperaPerguntas();
		
		UserDAO udao = new UserDAO();
		udao.updateIV(id, iv);
		final SecretKey sk = udao.retrSK(id);
		final byte[] ivBytes = Tools.hexToBytes(iv);
		
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, 
			WebApplicationException{
				outputPerguntas(os, Crypto.encryptString(geraXML(perguntas), sk, ivBytes)); 
				//outputPerguntas(os, geraXML(perguntas)); 
			}
		};
		
	}
	
	protected void outputPerguntas(OutputStream os, String cifra) {
		PrintStream writer2 = new PrintStream(os);
		writer2.println(cifra);
	}
	
	private String geraXML(ArrayList<Pergunta> p){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream writer = new PrintStream(baos);
		String xml = new String();
		writer.println("<Ps>");
		for(int i = 0; i < p.size(); i++){
			writer.println("<p>");
			writer.println("	<it>" + p.get(i).getId() + "," + p.get(i).getTitulo() + "</it>");
			for(int j = 0; j < p.get(i).getAlternativas().size(); j++){
				writer.println("	<as>");
				writer.println("		<a>" + p.get(i).getAlternativas().get(j).getId()+","+
						p.get(i).getAlternativas().get(j).getTitulo()+ "</a>");
				writer.println("	</as>");
			}
			
			writer.println("</p>");
		}
		writer.println("</Ps>");
		xml = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		return xml;
	}
}
