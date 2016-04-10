package concur.dbconctest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Created by coder on 08.04.16.
 */
public class Connector {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/";
    private static final String DB_DATABASE = "dbconctest";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "12345";
    private static final String CHARACTER_ENCODING = "?characterEncoding=utf-8&useUnicode=true";
    private static Connection connection;

    private Connector(){
        try{
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL /*+ DB_DATABASE*/ +
                    CHARACTER_ENCODING,DB_USER,DB_PASSWORD);
            this.prepareDB();
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    public static Connection connFabric(){
        if (connection == null){
            new Connector();
            return connection;
        }else{return connection;}
    }

    private void prepareDB() throws SQLException{
        try(Statement st = connection.createStatement()){
            st.execute("CREATE DATABASE IF NOT EXISTS "+DB_DATABASE);
            st.executeQuery("SET NAMES utf8 COLLATE utf8_general_ci");
            st.executeQuery("SET CHARACTER SET utf8");}
        connection.setCatalog(DB_DATABASE);
        System.out.println("Current DB is "+connection.getCatalog());
    }

    public static void connClose(){
        if(connection != null){
            try{connection.close();}catch(SQLException e){e.printStackTrace();}
        }
    }
}
