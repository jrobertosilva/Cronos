package com.projetocronos.cronos.cronos.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Created by jose- on 17/09/2017.
 */

public class DB extends _Default implements Runnable{
    private Connection conn;
    private String host = "192.168.0.17";
    private String db = "teste";
    private int port = 5432;
    private String user = "postgres";
    private String pass = "Mer233907@";
    private String url = "jdbc:postgresql://%s:%d/%s";

    public DB(){
        super();
        this.url = String.format(this.url, this.host, this.port, this.db);
        this.conectar();
        this.desconectar();
    }

    @Override
    public void run() {
    try{
        Class.forName("org.postgresql.Driver");
        this.conn = DriverManager.getConnection(this.url, this.user, this.pass);
    } catch (Exception e){
        this._mensagem = e.getMessage();
        this._status = false;
    }
    }

    private void conectar(){
        Thread thread = new Thread(this);
        thread.start();
        try {
        thread.join();
        }catch (Exception e){
            this._mensagem = e.getMessage();
            this._status = false;
        }
    }
    private void desconectar(){
        if (this.conn != null){
            try{
                this.conn.close();
            }catch (Exception e){

            }finally {
                this.conn = null;
            }
        }
    }

    public ResultSet select(String query){
        this.conectar();
        ResultSet resultSet = null;
        try {
            resultSet = new ExecuteDB(this.conn, query).execute().get();
        }catch (Exception e){
            this._status = false;
            this._mensagem = e.getMessage();
        }
        return resultSet;
    }


    public ResultSet execute(String query){
        this.conectar();
        ResultSet resultSet = null;
        try {
            resultSet = new ExecuteDB(this.conn, query).execute().get();
        }catch (Exception e){
            this._status = false;
            this._mensagem = e.getMessage();
        }
        return resultSet;
    }
}
