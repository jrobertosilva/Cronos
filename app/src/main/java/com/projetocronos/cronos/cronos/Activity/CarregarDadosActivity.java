package com.projetocronos.cronos.cronos.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.config.ConfiguracaoFirebase;
import com.projetocronos.cronos.cronos.helper.Preferencias;
import com.projetocronos.cronos.cronos.helper.Sessao;

import java.util.HashMap;
import java.util.Random;

public class CarregarDadosActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth usuarioAtenticacao;
    private EditText chaveDigitada;
    private EditText clienteDigitado;
    private Button botaoImportar;

    private DatabaseReference firebaseReferencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference getUsuarioReferencia = firebaseReferencia.child("crn_usuarios/0001");
    private DatabaseReference getClienteReferencia = firebaseReferencia.child("clientes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregar_dados);

        usuarioAtenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
      /**
       *
       * toolbar = (Toolbar) findViewById(R.id.toolbar_sinc);
       * */

        toolbar.setTitle("Cronos");
        setSupportActionBar(toolbar);

        chaveDigitada = (EditText) findViewById(R.id.edit_chave_carregar);
        clienteDigitado =(EditText) findViewById(R.id.edit_cliente_dados);
        botaoImportar = (Button) findViewById(R.id.botao_carregar);

        botaoImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chaveTexto = chaveDigitada.getText().toString();
                String clienteTexto= clienteDigitado.getText().toString();
                if (chaveTexto.isEmpty() && clienteTexto.isEmpty()){
                    Toast.makeText(CarregarDadosActivity.this, "Informe o Cliente e a Chave",Toast.LENGTH_LONG).show();
                }
                else{
                    if(verificaConexao()){
                        getUsuariosFirebase(clienteTexto);
                        getClientesFirebase(clienteTexto);
                        Toast.makeText(CarregarDadosActivity.this, "Dados importados com sucesso!", Toast.LENGTH_LONG).show();
                        chaveDigitada.setText("");
                        clienteDigitado.setText("");
                    }
                    else {
                        Toast.makeText(CarregarDadosActivity.this, "Não foi possível sincronizar!",Toast.LENGTH_LONG).show();
                    }
            }
        }});
    }
    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public void getUsuariosFirebase(String cliente){

        getUsuarioReferencia.child(cliente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_USUARIOS (crn_id INTEGER PRIMARY KEY AUTOINCREMENT,crn_ativo VARCHAR, crn_senha VARCHAR, crn_email VARCHAR, crn_usuario VARCHAR, crn_cod_cliente VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_USUARIOS;");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){

                    objSnapshot.child(ConfiguracaoFirebase.getUserId());
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String emailFirebase = objSnapshot.child("crn_email").getValue().toString();
                    String senhaFirebase = objSnapshot.child("crn_senha").getValue().toString();
                    String clienteFirebase = objSnapshot.child("crn_cod_cliente").getValue().toString();

                    bancoDados.execSQL("INSERT INTO CRN_USUARIOS (crn_usuario, crn_ativo, crn_email, crn_senha, crn_cod_cliente) VALUES ('"
                            + emailFirebase + "','" + ativoFirebase + "','"+ emailFirebase+"','"+senhaFirebase+"','"+clienteFirebase+"')");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getClientesFirebase(String cliente){

        getClienteReferencia.child(cliente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_CLIENTES (crn_cli_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_ativo VARCHAR, crn_cod_cliente VARCHAR,crn_chave VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_CLIENTES;");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){

                    objSnapshot.child(ConfiguracaoFirebase.getUserId());
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String chaveFirebase = objSnapshot.child("crn_chave").getValue().toString();
                    String clienteFirebase = objSnapshot.child("crn_cod_cliente").getValue().toString();

                    bancoDados.execSQL("INSERT INTO CRN_CLIENTES (crn_ativo, crn_chave, crn_cod_cliente) VALUES ('"
                            + ativoFirebase + "','" + chaveFirebase + "','"+ clienteFirebase+"')");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
