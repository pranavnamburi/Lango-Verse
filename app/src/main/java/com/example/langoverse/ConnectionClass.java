package com.example.langoverse;


import android.util.Log;

import com.mysql.jdbc.Connection;

import java.sql.DriverManager;
import java.util.Objects;

public class ConnectionClass {
protected final String db="translate_1";
protected final String un="root";
protected final String password="root_123";
protected final String ip="translateapp-1.crwkm4kk2qu6.ap-south-1.rds.amazonaws.com";
protected final String port="3306";
public Connection CONN(){

    Connection conn =null;
    try{
        Class.forName("com.mysql.jdbc.Driver");
        String connectionString="jdbc:mysql://"+ip+":"+port+"/"+db;
        conn = (Connection) DriverManager.getConnection(connectionString,un,password);


    }catch(Exception e){
    Log.e("Error", Objects.requireNonNull(e.getMessage()));

    }
    return conn;

}

}
