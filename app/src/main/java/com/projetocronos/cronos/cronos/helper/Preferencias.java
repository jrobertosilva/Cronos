package com.projetocronos.cronos.cronos.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by jose- on 13/08/2017.
 */

public class Preferencias {

    private Context contexto;
    private SharedPreferences  preferences;
    private final String NOME_ARQUIVO = "CRONOS.PREFERENCIAS";
    private int MODE = 0;
    private SharedPreferences.Editor editor;
    private String CHAVE_EMAIL = "email";
    private String CHAVE_SENHA = "senha";
    private String CHAVE_MANTER = "manter";

    public Preferencias(Context contextoParametro, String emailUsuario){
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO + "." + emailUsuario, MODE);
        editor = preferences.edit();}

        public void salvarUsuarioPreferencias (String email, String senha, String manter){
        editor.putString(CHAVE_EMAIL, email);
        editor.putString(CHAVE_SENHA, senha);
        editor.putString(CHAVE_MANTER, manter);
            editor.commit();
    }

    public HashMap<String, String> getDadosUsuario() {
        HashMap <String , String> dadosUsuario = new HashMap<>();

        dadosUsuario.put(CHAVE_EMAIL, preferences.getString(CHAVE_EMAIL,null));
        dadosUsuario.put(CHAVE_SENHA, preferences.getString(CHAVE_SENHA,null));
        dadosUsuario.put(CHAVE_MANTER, preferences.getString(CHAVE_MANTER,null));

        return dadosUsuario;
    }
    public static String getMD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
