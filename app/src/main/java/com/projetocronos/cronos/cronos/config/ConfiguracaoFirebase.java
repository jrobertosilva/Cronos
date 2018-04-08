package com.projetocronos.cronos.cronos.config;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jose- on 13/08/2017.
 */

public final class ConfiguracaoFirebase extends AppCompatActivity{
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;
    private static FirebaseUser usuarioLogado;
    private static DatabaseReference getArvoresReferencia;

    public static DatabaseReference getFirebase(){

        if (referenciaFirebase == null) {

            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }
    public static FirebaseAuth getFirebaseAutenticacao() {
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;

    }

    public static String getUserId(){
        usuarioLogado = getFirebaseAutenticacao().getCurrentUser();
        String userID = usuarioLogado.getUid();
        return userID;
    }

    public static void deleteUser(){
        usuarioLogado = getFirebaseAutenticacao().getCurrentUser();
        usuarioLogado.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        });
    }
}
