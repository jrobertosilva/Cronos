package com.projetocronos.cronos.cronos.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by jose- on 21/08/2017.
 */

public class Sessao {


    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "CRONOS.PREFERENCIAS";
    private int MODE = 0;
    private SharedPreferences emailPreferences;
    private SharedPreferences.Editor sessaoPreferences;
    private String CHAVE_SESSAO = "sessao";


    public  Sessao(Context contextoParametro){
        contexto = contextoParametro;
        emailPreferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        sessaoPreferences = emailPreferences.edit();}


    public void salvarLoginSessao (String email){
        sessaoPreferences.putString(CHAVE_SESSAO, email);
        sessaoPreferences.commit();
    }


    public HashMap<String, String> getLoginRecente() {
        HashMap <String , String> dadosSessao = new HashMap<>();

        dadosSessao.put(CHAVE_SESSAO, emailPreferences.getString(CHAVE_SESSAO,null));

        return dadosSessao;


    }
}
