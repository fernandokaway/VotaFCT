package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKey;
import org.bouncycastle.util.encoders.Base64;

public class UserDAO {
	Conexao connection;
	Statement stmt;
	
	public UserDAO() {
		connection = new Conexao();
	}
	
	//Retrieve Enrollment password from DB
	public String retrPassword(String id){
		String password;
		String query = "select password from user where idUser = '"+id+"'";
        ResultSet rs;
        stmt = connection.connect();
        try {
        	rs = stmt.executeQuery(query);
        	rs.next();
        	password = rs.getString("password");
        }catch (SQLException e) {
				e.printStackTrace();
				connection.disconnect();
				return null;
        }
        connection.disconnect();
		return password;
	}
	
	
	//Save Session Key in DB
	public boolean updateSK(String id, SecretKey sk){
		String encodedKey = Base64.toBase64String(sk.getEncoded());
		String query = "update user set sk = '"+ encodedKey +"' where iduser = "+id;
		encodedKey = null;
		sk = null;
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
