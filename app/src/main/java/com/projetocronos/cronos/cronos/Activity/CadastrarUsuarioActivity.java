package com.projetocronos.cronos.cronos.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.annotation.NonNull;
import android.widget.Toast;
import android.database.Cursor;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
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

public class CadastrarUsuarioActivity extends AppCompatActivity {

    private EditText cliente;
    private EditText chave;
    private EditText email;
    private EditText senha;
    private EditText confirmarSenha;
    private Button botaoCadastrar;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference firebaseReferencia;
    private DatabaseReference getClienteReferencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        firebaseAuth = FirebaseAuth.getInstance();

        cliente = (EditText) findViewById(R.id.edit_add_usuario_cod_cliente);
        chave = (EditText) findViewById(R.id.edit_chave_cliente);
        email = (EditText) findViewById(R.id.edit_email_add_usuario);
        senha = (EditText) findViewById(R.id.edit_senha_add_usuario);
        confirmarSenha = (EditText) findViewById(R.id.edit_confirmar_senha);
        botaoCadastrar = (Button) findViewById(R.id.botao_novo_usuario);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        final String senhaTexto = senha.getText().toString();
        final String confirmarSenhatexto = confirmarSenha.getText().toString();
        final String clienteInformado = cliente.getText().toString();
        final String chaveInformada = chave.getText().toString();
        final String emailInformado = email.getText().toString();

        if(senhaTexto.isEmpty() || confirmarSenhatexto.isEmpty() || clienteInformado.isEmpty() || chaveInformada.isEmpty() || emailInformado.isEmpty()){
            Toast.makeText(CadastrarUsuarioActivity.this, "Todos os campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
        }
        else {

        if (senhaTexto.equals(confirmarSenhatexto)){
                    firebaseAuth.createUserWithEmailAndPassword(emailInformado, senhaTexto)
                            .addOnCompleteListener(CadastrarUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        DatabaseReference firebaseReferencia = FirebaseDatabase.getInstance().getReference();
                                        DatabaseReference usuarioReferencia = firebaseReferencia.child("crn_usuarios");

                                        getClienteReferencia = firebaseReferencia.child("clientes");

                                        getClientesFirebase(clienteInformado);

                                        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

                                        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_CLIENTES (crn_cli_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_ativo VARCHAR, crn_cod_cliente VARCHAR,crn_chave VARCHAR)");
                                        Cursor cursor = bancoDados.rawQuery("SELECT crn_ativo, crn_cod_cliente, crn_chave FROM CRN_CLIENTES WHERE crn_cod_cliente = '" + clienteInformado + "' and crn_chave = '" + chaveInformada +"'", null);
                                        int indiceColunaAtivo = cursor.getColumnIndex("crn_ativo");
                                        int indiceColunaCliente = cursor.getColumnIndex("crn_cod_cliente");
                                        int indiceColunaChave = cursor.getColumnIndex("crn_chave");

                                        cursor.moveToFirst();

                                        int totalLinhas = cursor.getCount();

                                        if (totalLinhas == 0 ){
                                            Toast.makeText(CadastrarUsuarioActivity.this, "Cliente ou Chave são inválidos!", Toast.LENGTH_LONG).show();
                                        }
                                        int contador = 0;
                                        while (contador < totalLinhas) {

                                            String campoAtivo = cursor.getString(indiceColunaAtivo);
                                            String campoCliente = cursor.getString(indiceColunaCliente);
                                            String campoChave = cursor.getString(indiceColunaChave);

                                            if(campoCliente.equals(clienteInformado) && campoChave.equals(chaveInformada) && campoAtivo.equals("1") ){
                                                usuarioReferencia = usuarioReferencia.child(clienteInformado);
                                                usuarioReferencia = usuarioReferencia.child(ConfiguracaoFirebase.getUserId());

                                                String senhaMD5 = Preferencias.getMD5(senhaTexto);

                                                usuarioReferencia.child("crn_ativo").setValue("1");
                                                usuarioReferencia.child("crn_atualizador").setValue("0");
                                                usuarioReferencia.child("crn_cod_cliente").setValue(clienteInformado);
                                                usuarioReferencia.child("crn_email").setValue(emailInformado);
                                                usuarioReferencia.child("crn_senha").setValue(senhaMD5);

                                                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_USUARIOS (crn_id INTEGER PRIMARY KEY AUTOINCREMENT,crn_ativo VARCHAR, crn_senha VARCHAR, crn_email VARCHAR, crn_usuario VARCHAR, crn_cod_cliente VARCHAR)");
                                                bancoDados.execSQL("INSERT INTO CRN_USUARIOS (crn_ativo, crn_senha, crn_email, crn_usuario, crn_cod_cliente) VALUES ('1','"
                                                        + senhaMD5 + "','"+ emailInformado + "','" + emailInformado + "','" + clienteInformado + "');");
                                                Toast.makeText(CadastrarUsuarioActivity.this, "Usuário cadastrado com sucesso", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(CadastrarUsuarioActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                ConfiguracaoFirebase.deleteUser();
                                                Toast.makeText(CadastrarUsuarioActivity.this, "Cliente ou Chave são inválidos!", Toast.LENGTH_LONG).show();
                                            }
                                            cursor.moveToNext();
                                            contador = contador + 1;
                                        }

                                        cursor.close();
                                    }
                                    else {
                                        Toast.makeText(CadastrarUsuarioActivity.this, "Não foi possível cadastrar", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    }
                else{
                     Toast.makeText(CadastrarUsuarioActivity.this, "Senhas não conferem!", Toast.LENGTH_LONG).show();
                }
        }}
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
