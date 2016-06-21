package VotaFCTServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

@Path("votacao")
public class Votacao {
	
	@GET
	@Path("/{RA},{votosCifrados}")
	@Produces("application/xml")
    public StreamingOutput putVoto(@PathParam("RA") String id,
    		@PathParam("votosCifrados") String votosCifrados)
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
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
		System.out.println("Voto computado");
		
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