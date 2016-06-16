package VotaFCTServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;

import com.sun.jersey.spi.resource.Singleton;

import database.UserDAO;

@Path("auth")
@Singleton
public class Auth {
	
	private JPAKEParticipant jpakeParticipant;
	
	@GET
	@Path("/round1/{id},{Gx1},{Gx2},{KPx1_0},{KPx1_1},{KPx2_0},{KPx2_1}")
	@Produces("text/plain")
    public StreamingOutput JPAKEAuthRound1(@PathParam("id") String id,
    		@PathParam("Gx1") String Gx1, @PathParam("Gx2") String Gx2, 
    		@PathParam("KPx1_0") String KPx1_0, @PathParam("KPx1_1") String KPx1_1,
    		@PathParam("KPx2_0") String KPx2_0, @PathParam("KPx2_1") String KPx2_1)
    {
		
		JPAKEPrimeOrderGroup group = JPAKEPrimeOrderGroups.NIST_3072;
        Digest digest = new SHA256Digest();
        SecureRandom random = new SecureRandom();

        UserDAO udao = new UserDAO();
        String password = udao.retrPassword(id);
        
        jpakeParticipant = new JPAKEParticipant("server",
        		password.toCharArray(),group,digest,random);
        
        //1st round of JPAKE
        final JPAKERound1Payload jpakeRound1Payload = 
        		jpakeParticipant.createRound1PayloadToSend();
        
        BigInteger PKx1[] = new BigInteger[2];
        BigInteger PKx2[] = new BigInteger[2];
        PKx1[0] = new BigInteger(KPx1_0);
    	PKx1[1] = new BigInteger(KPx1_1);
    	PKx2[0] = new BigInteger(KPx2_0);
    	PKx2[1] = new BigInteger(KPx2_1);
    	JPAKERound1Payload clientRound1Payload = new JPAKERound1Payload(id, new BigInteger(Gx1), new BigInteger(Gx2), PKx1, PKx2);
        
        
        try {
			jpakeParticipant.validateRound1PayloadReceived(clientRound1Payload);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
      
        
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, WebApplicationException{
				outputRound1Payload(os, jpakeRound1Payload);
			}
		};
	}
	
	protected void outputRound1Payload(OutputStream os, JPAKERound1Payload jpakeRound1Payload){
		PrintStream writer = new PrintStream(os);
		writer.println(jpakeRound1Payload.getGx1().toString()+","+jpakeRound1Payload.getGx2().toString()+","+
				jpakeRound1Payload.getKnowledgeProofForX1()[0]+","+jpakeRound1Payload.getKnowledgeProofForX1()[1]+","+
						jpakeRound1Payload.getKnowledgeProofForX2()[0]+","+jpakeRound1Payload.getKnowledgeProofForX2()[1]);
	}
	
	@GET
	@Path("/round2/{id},{A},{KPX2s_0},{KPX2s_1}")
	@Produces("text/plain")
    public StreamingOutput JPAKEAuthRound2(@PathParam("id") String id,
    		@PathParam("A") String A, @PathParam("KPX2s_0") String KPX2s_0 , @PathParam("KPX2s_1") String KPX2s_1)
    {
		
		///2nd round payload
        final JPAKERound2Payload jpakeRound2Payload = jpakeParticipant.createRound2PayloadToSend();
        
        BigInteger KPX2s[] = new BigInteger[2];
        KPX2s[0] = new BigInteger(KPX2s_0);
        KPX2s[1] = new BigInteger(KPX2s_1);
        JPAKERound2Payload clientRound2Payload = new JPAKERound2Payload(id, new BigInteger(A), KPX2s);
        try {
			jpakeParticipant.validateRound2PayloadReceived(clientRound2Payload);
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        UserDAO udao = new UserDAO();
        SecretKey sk = deriveSessionKey(
        		jpakeParticipant.calculateKeyingMaterial());
        udao.updateSK(id, sk);
        
        System.out.println("Session key = "+sk.getEncoded().toString());
        
		return new StreamingOutput(){
			public void write(OutputStream os) throws IOException, WebApplicationException{
				outputRound2Payload(os, jpakeRound2Payload);
			}
		};
	}
	
	protected void outputRound2Payload(OutputStream os, JPAKERound2Payload jpakeRound2Payload){
		PrintStream writer = new PrintStream(os);
		writer.println(jpakeRound2Payload.getA()+","+jpakeRound2Payload.getKnowledgeProofForX2s()[0]
				+","+jpakeRound2Payload.getKnowledgeProofForX2s()[1]);
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
