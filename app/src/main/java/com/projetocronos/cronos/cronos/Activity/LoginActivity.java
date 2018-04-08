package com.projetocronos.cronos.cronos.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DatabaseReference;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.config.ConfiguracaoFirebase;
import com.projetocronos.cronos.cronos.helper.Permissao;
import com.projetocronos.cronos.cronos.helper.Preferencias;

import java.util.HashMap;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText telefone;
    private EditText ddd;
    private EditText codigoPais;
    private Button btCadastrar;
    private EditText nome;
    private String [] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };
    private DatabaseReference referenciaFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1, this,permissoesNecessarias);


        referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("pontos").setValue("800");

        telefone = (EditText) findViewById(R.id.edit_telefone);
        ddd = (EditText) findViewById(R.id.edit_ddd);
        codigoPais = (EditText) findViewById(R.id.edit_cod_pais);
        btCadastrar = (Button) findViewById(R.id.bt_cadastrar);
        nome = (EditText) findViewById(R.id.edit_nome);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNNNN NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(telefone, smf);
        telefone.addTextChangedListener(mtw);

        SimpleMaskFormatter smfDDD = new SimpleMaskFormatter("NN");
        MaskTextWatcher mtwDDD = new MaskTextWatcher(ddd,smfDDD);
        ddd.addTextChangedListener(mtwDDD);

        SimpleMaskFormatter smfPais = new SimpleMaskFormatter("+NN");
        MaskTextWatcher mtwPais = new MaskTextWatcher(codigoPais,smfPais);
        codigoPais.addTextChangedListener(mtwPais);


        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto = codigoPais.getText().toString() + ddd.getText().toString() + telefone.getText().toString();
                String telefoneSemFormatacao = telefoneCompleto.replace("+","").replace("-","");

                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(numeroRandomico);

             /*   Preferencias preferencias = new Preferencias(LoginActivity.this);
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);*/

                String mensagemEnvio = "WhatsApp Código de Confirmação: " + token;

                boolean enviadoSMS = enviaSMS("+" + telefoneSemFormatacao, mensagemEnvio);

                if (enviadoSMS){
                    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {

                    Toast.makeText(LoginActivity.this, "Problema ao enviar o SMS, tente novamente!", Toast.LENGTH_SHORT).show();

                }
              /*  HashMap<String, String> usuario = preferencias.getDadosUsuario();*/
            }
        });
    }

        private boolean enviaSMS(String telefone, String mensagem){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);
            return true ;

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int resultado : grantResults){
            if (resultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }

        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o aplicativo é necessário  aceitar as permissões");
        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    }

