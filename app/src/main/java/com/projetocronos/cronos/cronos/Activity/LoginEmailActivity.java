package com.projetocronos.cronos.cronos.Activity;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.fragments.ImportarDadosFragment;
import com.projetocronos.cronos.cronos.fragments.MapaArvoresFragment;
import com.projetocronos.cronos.cronos.helper.Preferencias;
import com.projetocronos.cronos.cronos.helper.Sessao;

import java.io.InputStream;
import java.util.HashMap;

public class LoginEmailActivity extends AppCompatActivity {

    private TextView textoCadastrar;
    private EditText emailLogin;
    private EditText senhaLogin;
    private Button botaoLogar;
    private FirebaseAuth firebaseAuth;
    private CheckBox manterLogado;

    private DatabaseReference firebaseReferencia;
    private DatabaseReference getArvoresReferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

         firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){

            Sessao sessao = new Sessao(LoginEmailActivity.this);
            HashMap<String, String> sessaoUsuario = sessao.getLoginRecente();
            String ultimaSessao = sessaoUsuario.get("sessao");
            if (ultimaSessao.isEmpty()){
            }
            else{
                Preferencias preferencias = new Preferencias(LoginEmailActivity.this, ultimaSessao);
                HashMap<String, String> usuario = preferencias.getDadosUsuario();
                String manter = usuario.get("manter");

                String validar = "2";
                if (manter.equals(validar)){
                    firebaseAuth.signOut();
                }
                else {
                    abrirPrincipal();
                }
            }

        } else {
            Sessao sessao = new Sessao(LoginEmailActivity.this);
            HashMap<String, String> sessaoUsuario = sessao.getLoginRecente();
            String ultimaSessao = sessaoUsuario.get("sessao");
            Preferencias preferencias = new Preferencias(LoginEmailActivity.this, ultimaSessao);

       HashMap<String, String> usuario = preferencias.getDadosUsuario();
          String manter = usuario.get("manter");


            if (manter == "1") {

                String emailSalvo = usuario.get("email");
                String senhaSalva = usuario.get("semha");

                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_USUARIOS (crn_ativo VARCHAR, crn_senha VARCHAR, crn_email VARCHAR, crn_usuario VARCHAR, crn_cod_cliente VARCHAR)");
                Cursor cursor = bancoDados.rawQuery("Select crn_ativo, crn_senha, crn_email, crn_usuario from crn_usuarios where crn_email = ' " + emailSalvo + "' and crn_senha = '" + senhaSalva +"'", null);

                int indiceColunaAtivo = cursor.getColumnIndex("crn_ativo");
                int indiceColunaSenha = cursor.getColumnIndex("crn_senha");
                int indiceColunaEmail = cursor.getColumnIndex("crn_email");

                cursor.moveToFirst();
                int totalLinhas = cursor.getCount();
                if (totalLinhas == 0 ){
                    Toast.makeText(LoginEmailActivity.this, "E-mail ou Senha são inválidos!", Toast.LENGTH_LONG).show();
                }
                int contador = 0;
                while (contador < totalLinhas){
                    String campoAtivo = cursor.getString(indiceColunaAtivo);
                    String campoSenha = cursor.getString(indiceColunaSenha);
                    String campoEmail = cursor.getString(indiceColunaEmail);

                    if (campoAtivo.equals("1") && campoSenha.equals(senhaSalva) && campoEmail.equals(emailSalvo)){
                        abrirPrincipal();
                    }
                    else{
                        Toast.makeText(LoginEmailActivity.this, "E-mail ou Senha são inválidos!", Toast.LENGTH_LONG).show();
                    }
                }
                cursor.close();
            }
        }
        textoCadastrar = (TextView) findViewById(R.id.texto_cadastrar);
        textoCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginEmailActivity.this, CadastrarUsuarioActivity.class);
                    startActivity(intent);
                }
            });
        emailLogin = (EditText) findViewById(R.id.edit_email_login_cronos);
        senhaLogin = (EditText) findViewById(R.id.edit_senha_login_cronos);
        botaoLogar = (Button) findViewById(R.id.botao_logar_cronos);
        manterLogado = (CheckBox) findViewById(R.id.chk_manter_conectado);
        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (emailLogin.getText().toString().isEmpty() || senhaLogin.getText().toString().isEmpty()) {
                    Toast.makeText(LoginEmailActivity.this,"Informe E-mail e Senha!",Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (verificaConexao()) {
                            firebaseAuth.signInWithEmailAndPassword(emailLogin.getText().toString(), senhaLogin.getText().toString()).addOnCompleteListener(LoginEmailActivity.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (manterLogado.isChecked()) {
                                            Preferencias preferencias = new Preferencias(LoginEmailActivity.this, emailLogin.getText().toString());
                                            preferencias.salvarUsuarioPreferencias(emailLogin.getText().toString(), Preferencias.getMD5(senhaLogin.getText().toString()), "1");

                                            Sessao sessao = new Sessao(LoginEmailActivity.this);
                                            sessao.salvarLoginSessao(emailLogin.getText().toString());
                                        } else {
                                            Preferencias preferencias = new Preferencias(LoginEmailActivity.this, emailLogin.getText().toString());
                                            preferencias.salvarUsuarioPreferencias(emailLogin.getText().toString(), Preferencias.getMD5(senhaLogin.getText().toString()), "2");

                                            Sessao sessao = new Sessao(LoginEmailActivity.this);
                                            sessao.salvarLoginSessao(emailLogin.getText().toString());
                                        }
                                        abrirPrincipal();
                                    } else {
                                        Toast.makeText(LoginEmailActivity.this, "Usuário ou Senha Inválidos!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {

                            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
                            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_USUARIOS (crn_ativo VARCHAR, crn_senha VARCHAR, crn_email VARCHAR, crn_usuario VARCHAR, crn_cod_cliente VARCHAR)");
                            Cursor cursor = bancoDados.rawQuery("Select crn_ativo, crn_senha, crn_email, crn_usuario from crn_usuarios where crn_email = ' " + emailLogin.getText().toString() + "' and crn_senha = '" + Preferencias.getMD5(senhaLogin.getText().toString()) + "'", null);


                            int indiceColunaAtivo = cursor.getColumnIndex("crn_ativo");
                            int indiceColunaSenha = cursor.getColumnIndex("crn_senha");
                            int indiceColunaEmail = cursor.getColumnIndex("crn_email");

                            cursor.moveToFirst();
                            int totalLinhas = cursor.getCount();
                            if (totalLinhas == 0) {
                                Toast.makeText(LoginEmailActivity.this, "E-mail ou Senha são inválidos!", Toast.LENGTH_LONG).show();
                            }
                            int contador = 0;
                            while (contador < totalLinhas) {
                                String campoAtivo = cursor.getString(indiceColunaAtivo);
                                String campoSenha = cursor.getString(indiceColunaSenha);
                                String campoEmail = cursor.getString(indiceColunaEmail);

                                if (campoAtivo == "1" && campoSenha.equals(Preferencias.getMD5(senhaLogin.getText().toString())) && campoEmail.equals(emailLogin.getText().toString())) {
                                    if (manterLogado.isChecked()) {
                                        Preferencias preferencias = new Preferencias(LoginEmailActivity.this, emailLogin.getText().toString());
                                        preferencias.salvarUsuarioPreferencias(emailLogin.getText().toString(), Preferencias.getMD5(senhaLogin.getText().toString()), "1");

                                        Sessao sessao = new Sessao(LoginEmailActivity.this);
                                        sessao.salvarLoginSessao(emailLogin.getText().toString());
                                    } else {

                                        Preferencias preferencias = new Preferencias(LoginEmailActivity.this, emailLogin.getText().toString());
                                        preferencias.salvarUsuarioPreferencias(emailLogin.getText().toString(), Preferencias.getMD5(senhaLogin.getText().toString()), "2");

                                        Sessao sessao = new Sessao(LoginEmailActivity.this);
                                        sessao.salvarLoginSessao(emailLogin.getText().toString());
                                    }
                                    abrirPrincipal();
                                } else {
                                    Toast.makeText(LoginEmailActivity.this, "E-mail ou Senha são inválidos!", Toast.LENGTH_LONG).show();
                                }
                                contador = contador + 1;
                            }
                            cursor.close();
                        }
                    }
            }
        });

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

    public void abrirPrincipal(){
        firebaseReferencia = FirebaseDatabase.getInstance().getReference();
        getArvoresReferencia = firebaseReferencia.child("crn_arvores").child("0001");
     /*   getArvoresFirebase();*/
        Intent intent = new Intent(LoginEmailActivity.this, ActPrincipal.class);
        startActivity(intent);
    }
    public void getArvoresFirebase(){

        getArvoresReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARVORES (crn_arv_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_latitude VARCHAR, crn_longitude VARCHAR, crn_nome VARCHAR, crn_especie_id VARCHAR, crn_fiacao_id VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_ARVORES");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()) {

                    String latitudeFirebase = objSnapshot.child("crn_latitude").getValue().toString();
                    String longitudeFirebase = objSnapshot.child("crn_longitude").getValue().toString();
                    String nomeFirebase = objSnapshot.child("crn_nome").getValue().toString();
                    String idFirebase = objSnapshot.child("crn_arv_id").getValue().toString();
                    String especieFirebase = objSnapshot.child("crn_especie_id").getValue().toString();
                    String fiacaoFirebase = objSnapshot.child("crn_fiacao_id").getValue().toString();

                    Cursor cursor = bancoDados.rawQuery("SELECT crn_arv_id FROM CRN_ARVORES WHERE crn_arv_id = '" + idFirebase + "'", null);

                    if (cursor != null) {

                        int indiceColunaID = cursor.getColumnIndex("crn_arv_id");
                        cursor.moveToFirst();

                        int totalLinhas = cursor.getCount();
                        if (totalLinhas == 0) {
                            bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id, crn_latitude, crn_longitude, crn_nome, crn_especie_id, crn_fiacao_id) VALUES ('"
                                    + idFirebase + "','" + latitudeFirebase + "','" + longitudeFirebase + "','" + nomeFirebase + "' , '" + especieFirebase+ "' , '" + fiacaoFirebase + "')");
                        } else {
                            int codigoLite = cursor.getInt(indiceColunaID);
                            bancoDados.execSQL("UPDATE CRN_ARVORES SET crn_nome = '" + nomeFirebase + "', crn_especie_id = '" + especieFirebase + "', crn_fiacao_id = '" + fiacaoFirebase + "' WHERE crn_arv_id = '" + codigoLite + "'");
                        }
                        cursor.close();
                    }
                    else{
                        bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id, crn_latitude, crn_longitude, crn_nome, crn_especie_id, crn_fiacao_id) VALUES ('"
                                + idFirebase + "','" + latitudeFirebase + "','" + longitudeFirebase + "','" + nomeFirebase + "' , '" + especieFirebase+ "' , '" + fiacaoFirebase + "')");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
