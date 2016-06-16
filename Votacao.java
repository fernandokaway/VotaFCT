package VotaFCTServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

@Path("Votacao")
public class Votacao {
	
	@GET
	@Path("/perguntas/{RA},{iv}")
	@Produces("application/xml")
    public StreamingOutput getPerguntas(@PathParam("id") String id, 
    		@PathParam("iv") String iv)
	{
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, 
			WebApplicationException{
				outputPerguntas(os);
			}
		};
	}
	
	protected void outputPerguntas(OutputStream os) {
		PrintStream writer = new PrintStream(os);
		writer.println("<Perguntas>");
		writer.println("<Pergunta>" + "</Pergunta>");
		writer.println("</Perguntas>");
	}
}