package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import org.bouncycastle.util.encoders.Base64;

import domain.Alternativa;
import domain.Pergunta;

public class PerguntasDAO {

	Conexao connection;
	Statement stmt;
	
	public PerguntasDAO(){
		connection = new Conexao();
	}
	
	public ArrayList<Pergunta> recuperaPerguntas(){
		String query = "select * from perguntas inner join alternativas on perguntas.idPerguntas = alternativas.Perguntas_idPerguntas where perguntas.ativa = '1' order by idPerguntas, alternativas.idAlternativas";
        ResultSet rs;
        ArrayList<Pergunta> perguntas = new ArrayList<Pergunta>();
        ArrayList<Alternativa> alternativas = new ArrayList<>();
        Pergunta pergunta;
        stmt = connection.connect();
        try {
        	rs = stmt.executeQuery(query);
        	rs.last();
        	int qtdeLinhas = rs.getRow();
        	rs.first();
        	int id = rs.getInt("idPerguntas");
        	pergunta = new Pergunta(id, rs.getString("pergunta"));
        	for(int i = 0 ; i < qtdeLinhas; i++){
        		if(id != rs.getInt("idPerguntas")){
        			id = rs.getInt("idPerguntas");
        			pergunta.setAlternativas(alternativas);
        			perguntas.add(pergunta);
        			pergunta = new Pergunta(id, rs.getString("pergunta"));
        			alternativas = new ArrayList<>();
        		}
        		alternativas.add(new Alternativa(rs.getInt("idAlternativas"),
        				rs.getString("alternativa")));
        		rs.next();
        	}
        	pergunta.setAlternativas(alternativas);
			perguntas.add(pergunta);
        }catch (SQLException e) {
				e.printStackTrace();
				connection.disconnect();
				return null;
        }
        connection.disconnect();
		return perguntas;
	}
	
	public boolean updateVoto(String idAlternativa){
		String query = "update alternativas set qtdeRespostas = "
				+ "(select sum(qtdeRespostas)+1 "
				+ "where idAlternativas = '"+idAlternativa+"') "
						+ "where idAlternativas = '"+idAlternativa+"'";
        stmt = connection.connect();
        try {
        	stmt.executeUpdate(query);
        }catch (SQLException e) {
				e.printStackTrace();
				connection.disconnect();
				return false;
        }
        connection.disconnect();
		return true;
	}
	
}