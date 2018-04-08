package com.projetocronos.cronos.cronos.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.helper.Preferencias;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigoToken;
    private Button btValidar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoToken = (EditText) findViewById(R.id.edit_validador);
        btValidar = (Button) findViewById(R.id.bt_validar);

        SimpleMaskFormatter smfToken = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher mtwToken = new MaskTextWatcher(codigoToken, smfToken);
        codigoToken.addTextChangedListener(mtwToken);

        btValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                Preferencias preferencias = new Preferencias(ValidadorActivity.this);
                HashMap<String, String> usuario = preferencias.getDadosUsuario();

                String tokenGerado = usuario.get("token");
                String tokenDigitado = codigoToken.getText().toString();

                if(tokenGerado.equals(tokenDigitado)){
                    Toast.makeText(ValidadorActivity.this, "Token VALIDADO!",Toast.LENGTH_SHORT ).show();

                }else{
                    Toast.makeText(ValidadorActivity.this, "Token INVÁLIDO!",Toast.LENGTH_SHORT ).show();
                }
*/
            }
        });
    }
}
