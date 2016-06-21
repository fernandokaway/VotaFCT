package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import tools.Tools;

public class UserDAO {
	Conexao connection;
	Statement stmt;
	
	public UserDAO() {
		connection = new Conexao();
	}
	
	//Retrieve password from DB
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
	
	//Retrieve Session Key SK
	public SecretKey retrSK(String userId){
		String query = "select sk from user where idUser = '"+userId+"'";
		SecretKey sk = null;
	    ResultSet rs;
	    stmt = connection.connect();
	    try {
	    	rs = stmt.executeQuery(query);
	    	rs.next();
	    	byte[] decodedKey = Base64.decode(rs.getString("sk"));
	    	sk = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	    }catch (SQLException e) {
				e.printStackTrace();
				connection.disconnect();
				return sk;
	    }
	    connection.disconnect();
		return sk;
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
	
	public boolean updateIV(String id, String iv){
		String query = "update user set iv = '"+ iv +"' where iduser = "+id;
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
	
	//Retrieve initialization vector IV
	public byte[] retrIV(String userId){
		String query = "select * from user where idUser = '"+userId+"'";
		String iv = null;
        ResultSet rs;
        stmt = connection.connect();
        try {
        	rs = stmt.executeQuery(query);
        	rs.next();
        	iv = rs.getString("iv");
        }catch (SQLException e) {
				e.printStackTrace();
				connection.disconnect();
				return null;
        }
        connection.disconnect();
		return Tools.hexToBytes(iv);
	}
}
