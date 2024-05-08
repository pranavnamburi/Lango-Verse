package com.example.langoverse;


import android.util.Log;

import com.mysql.jdbc.Connection;

import java.sql.DriverManager;
import java.util.Objects;

public class ConnectionClass {
protected final String db="YOUR-DB-NAME";
protected final String un="DB_USERNAME";
protected final String password="DB-PASSWORD";
protected final String ip="IP/WEB-LINK OF DB";
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
