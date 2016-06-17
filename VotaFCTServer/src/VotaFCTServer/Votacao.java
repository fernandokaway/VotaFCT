package VotaFCTServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

@Path("Votacao")
public class Votacao {
	
	@GET
	@Path("/perguntas/{RA},{iv}")
	@Produces("application/xml")
    public StreamingOutput getPerguntas(@PathParam("RA") String id, 
    		@PathParam("iv") String iv)
	{
		PerguntasDAO pdao = new PerguntasDAO();
		final ArrayList<Pergunta> perguntas = pdao.recuperaPerguntas();
		
		UserDAO udao = new UserDAO();
		udao.updateIV(id, iv);
		final SecretKey sk = udao.retrSK(id);
		final byte[] ivByte = Tools.hexToBytes(iv);
				
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, 
			WebApplicationException{
				outputPerguntas(os, perguntas, sk, ivByte);
			}
		};
	}
	
	protected void outputPerguntas(OutputStream os, ArrayList<Pergunta> p, SecretKey sk, byte[] iv) {
		
		PrintStream writer = new PrintStream(os);
		writer.println("<Perguntas>");
		for(int i = 0; i < p.size(); i++){
			writer.println("	<Pergunta>");
			writer.println("		<idtitulo>" + Crypto.encryptString(p.get(i).getId()+","+p.get(i).getTitulo(), sk, iv) + "</idtitulo>");
			for(int j = 0; j < p.get(i).getAlternativas().size(); j++){
				writer.println("		<alternativa>" + Crypto.encryptString(p.get(i).getId()+","+p.get(i).getAlternativas().get(j).getId()+","+
						p.get(i).getAlternativas().get(j).getTitulo(), sk, iv)+ "</alternativa>");
			}
			writer.println("	</Pergunta>");
		}
		writer.println("</Perguntas>");
	}
	
	@GET
	@Path("/voto/{RA},{voto}")
	@Produces("application/xml")
    public StreamingOutput putVoto(@PathParam("RA") String id,
    		@PathParam("votoCifrado") String votosCifrados)
	{
		boolean resposta = false;
		UserDAO udao = new UserDAO();
		SecretKey sk = udao.retrSK(id);
		byte[] ivByte = udao.retrIV(id);
		
		String[] votos = Crypto.decryptString(votosCifrados, sk, ivByte).split(",");
		String RA = votos[0];
		if(RA.equals(id)){
			PerguntasDAO pdao = new PerguntasDAO();
				for(int i = 1; i < votos.length; i++){
					resposta = pdao.updateVoto(id,votos[i]);
					if(!resposta) break;
			}
		}
		final boolean saida = resposta;
		
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, 
			WebApplicationException{
				outputVoto(os, saida);
			}
		};
	}
	
	protected void outputVoto(OutputStream os, boolean resposta) {
		PrintStream writer = new PrintStream(os);
		writer.println("<Voto>");
		writer.println("	<resposta>" + resposta + "</resposta>");
		writer.println("</Voto>");
	}
}