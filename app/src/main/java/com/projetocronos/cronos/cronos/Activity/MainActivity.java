package com.projetocronos.cronos.cronos.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.config.ConfiguracaoFirebase;
import com.projetocronos.cronos.cronos.fragments.MapaArvoresFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth usuarioAtenticacao;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        usuarioAtenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Cronos");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_carregar_dados:
                abrirSincronizar();
                return true;
            case R.id.item_abrir_mapa:
                abrirMapa();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deslogarUsuario(){
        usuarioAtenticacao.signOut();
        Intent intent = new Intent(MainActivity.this, LoginEmailActivity.class);
        startActivity(intent);
        finish();

    }

    public void abrirSincronizar(){
        Intent intent = new Intent(MainActivity.this, CarregarDadosActivity.class);
        startActivity(intent);
    }

    public void abrirMapa(){
        Intent intent = new Intent(MainActivity.this, MapaArvoresFragment.class);
        startActivity(intent);
    }
}
