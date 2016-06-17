package database;

import java.sql.*;

public class Conexao {
	
	static Connection connection;
    static Statement stmt;
    String host="localhost";
    String dabatase="votaFCT";
    String login="";
    String password = "";
    
    public Conexao()
    {
        
    }

    //Connects DB
    public Statement connect()
    {
        try
        {
            String url = "jdbc:mysql://" + host + "/" + dabatase + "?user=" + login + "&password=" + password;
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url);
            return connection.createStatement();
        }
        catch (ClassNotFoundException ex1)
        {
            System.out.println ("Class does not exist: "+ex1.getMessage().toString()+"\n");
        }
	    catch (SQLException ex)
        {
              System.out.println ("SQL Error: "+ex.getMessage().toString()+"\n");
        }
        catch (Exception e)
        {
            System.out.println ("Error: "+e.getMessage().toString()+"\n");
        }
        System.out.println ("Conectado");
        return null;
    }
    
    public void disconnect(){
    	try{
    		connection.close();
    	}catch(SQLException ex){
    		System.out.println ("SQL Error: "+ex.getMessage().toString()+"\n");
    	}
    }
}
