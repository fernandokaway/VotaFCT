package VotaFCTServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import database.PerguntasDAO;
import domain.Pergunta;

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
		
		
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, 
			WebApplicationException{
				outputPerguntas(os, perguntas);
			}
		};
	}
	
	protected void outputPerguntas(OutputStream os, ArrayList<Pergunta> p) {
		PrintStream writer = new PrintStream(os);
		writer.println("<Perguntas>");
		for(int i = 0; i < p.size(); i++){
			writer.println("	<Pergunta>");
			writer.println("	<id>" + p.get(i).getId() + "</id>");
			writer.println("	<titulo>" + p.get(i).getTitulo() + "</titulo>");
			for(int j = 0; j < p.get(i).getAlternativas().size(); j++){
				writer.println("		<Alternativa>");
				writer.println("		<id>" + 
					p.get(i).getAlternativas().get(j).getId() + "</id>");
				writer.println("		<titulo>" + 
					p.get(i).getAlternativas().get(j).getTitulo() + "</titulo>");
				writer.println("		</Alternativa>");
			}
			writer.println("	</Pergunta>");
		}
		writer.println("</Perguntas>");
	}
	
	@GET
	@Path("/voto/{RA},{voto}")
	@Produces("application/xml")
    public StreamingOutput putVoto(@PathParam("RA") String id,
    		@PathParam("voto") String voto)
	{
		PerguntasDAO pdao = new PerguntasDAO();
		final boolean resposta = pdao.updateVoto(voto);
		
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, 
			WebApplicationException{
				outputVoto(os, resposta);
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