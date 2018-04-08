package com.projetocronos.cronos.cronos.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projetocronos.cronos.cronos.Activity.CarregarDadosActivity;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.config.ConfiguracaoFirebase;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportarDadosFragment extends Fragment {
    private FirebaseAuth usuarioAtenticacao;
    private EditText chaveDigitada;
    private EditText clienteDigitado;
    private Button botaoImportar;
    private ProgressBar loadDados;

    private DatabaseReference firebaseReferencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference getUsuarioReferencia = firebaseReferencia.child("crn_usuarios");
    private DatabaseReference getEspecieReferencia = firebaseReferencia.child("crn_especie");
    private DatabaseReference getPlantioReferencia = firebaseReferencia.child("crn_plantio_pavimentacao");
    private DatabaseReference getCalcadaReferencia = firebaseReferencia.child("crn_largura_calcada");
    private DatabaseReference getAreaReferencia = firebaseReferencia.child("crn_area_livre");
    private DatabaseReference getFiacaoReferencia = firebaseReferencia.child("crn_fiacao");
    private DatabaseReference getPodaReferencia = firebaseReferencia.child("crn_tipo_poda");
    private DatabaseReference getRadicularReferencia = firebaseReferencia.child("crn_sistema_radicular");
    private DatabaseReference getQualidadeReferencia = firebaseReferencia.child("crn_tipo_qualidade");
    private DatabaseReference getIntervencaoReferencia = firebaseReferencia.child("crn_tipo_intervencao");
    private DatabaseReference getInclinacaoReferencia = firebaseReferencia.child("crn_inclinacao_tronco");
    private DatabaseReference getClienteReferencia = firebaseReferencia.child("clientes");
    private DatabaseReference getArvoresReferencia = firebaseReferencia.child("crn_arvores");


    public ImportarDadosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_carregar_dados, container, false);
        usuarioAtenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();



        chaveDigitada = (EditText) view.findViewById(R.id.edit_chave_carregar);
        clienteDigitado =(EditText) view.findViewById(R.id.edit_cliente_dados);
        botaoImportar = (Button) view.findViewById(R.id.botao_carregar);
        loadDados = (ProgressBar) view.findViewById(R.id.progressBar);

        botaoImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chaveTexto = chaveDigitada.getText().toString();
                String clienteTexto= clienteDigitado.getText().toString();
                if (chaveTexto.isEmpty() || clienteTexto.isEmpty()){
                    Toast.makeText(getContext(), "Informe o Cliente e a Chave",Toast.LENGTH_LONG).show();
                }
                else{
                    if(verificaConexao()){
                        loadDados.setVisibility(View.VISIBLE);
                        getUsuariosFirebase(clienteTexto);
                        getClientesFirebase();

                        getEspecieFirebase();
                        getFiacaoFirebase();
                        getInclinacaoFirebase();
                        getCalcadaFirebase();
                        getPlantioFirebase();
                        getRadicularFirebase();
                        getIntervencaoFirebase();
                        getPodaFirebase();
                        getQualidadeFirebase();
                        getAreaFirebase();
                        getArvoresFirebase(clienteTexto);

                        Toast.makeText(getContext(), "Dados importados com sucesso!", Toast.LENGTH_LONG).show();
                        loadDados.setVisibility(View.INVISIBLE);
                       /* chaveDigitada.setText("");
                        clienteDigitado.setText("");
                    */}
                    else {
                        Toast.makeText(getContext(), "Não foi possível sincronizar!",Toast.LENGTH_LONG).show();
                    }
                }
            }});

        return view;
    }

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
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

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
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

    public void getClientesFirebase(){

        getClienteReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_CLIENTES (crn_cli_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_ativo VARCHAR, crn_cod_cliente VARCHAR,crn_chave VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_CLIENTES;");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    if (objSnapshot.getKey() == "0001"){
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String chaveFirebase = objSnapshot.child("crn_chave").getValue().toString();
                    String clienteFirebase = objSnapshot.child("crn_cod_cliente").getValue().toString();
                    bancoDados.execSQL("INSERT INTO CRN_CLIENTES (crn_ativo, crn_chave, crn_cod_cliente) VALUES ('"
                            + ativoFirebase + "','" + chaveFirebase + "','"+ clienteFirebase+"')");
                    }
                    else
                    {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getEspecieFirebase(){

        getEspecieReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("DROP TABLE IF EXISTS CRN_ESPECIE");
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ESPECIE (crn_especie_id INTEGER PRIMARY KEY, crn_nome_pop VARCHAR, crn_nome_real VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_ESPECIE");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int especieIDFirebase = Integer.parseInt(objSnapshot.child("crn_especie_id").getValue().toString());
                    String nomePopFirebase = objSnapshot.child("crn_nome_pop").getValue().toString();
                    String nomeRealFirebase = objSnapshot.child("crn_nome_real").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL ="INSERT INTO CRN_ESPECIE (crn_especie_id, crn_nome_pop, crn_nome_real, crn_ativo) VALUES ("
                            + especieIDFirebase + ", '" + nomePopFirebase + "', '"+ nomeRealFirebase+"', '"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getArvoresFirebase(String cliente){

        getArvoresReferencia.child(cliente).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);

                    bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARVORES (crn_arv_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_latitude VARCHAR, crn_longitude VARCHAR, " +
                            "crn_nome VARCHAR, crn_especie_id VARCHAR, crn_plantio_id VARCHAR, crn_largura_id VARCHAR, crn_inclinacao_id VARCHAR, crn_fiacao_id VARCHAR, crn_sistema_id VARCHAR," +
                            "crn_intervencao_id VARCHAR, crn_poda_id VARCHAR, crn_qualidade_id VARCHAR, crn_area_id VARCHAR, crn_idade_arvore VARCHAR, crn_diametro_arvore VARCHAR, " +
                            "crn_altura_arvore VARCHAR, crn_altura_ramo VARCHAR, crn_altura_fiacao VARCHAR, crn_altura_copa VARCHAR, crn_doenca_praga VARCHAR, crn_obs_arvore VARCHAR, " +
                            "crn_ativo VARCHAR, crn_ult_atualizacao VARCHAR, crn_motivo_inativo VARCHAR, crn_usuario_id VARCHAR)");

                    bancoDados.execSQL("DELETE FROM CRN_ARVORES");


                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()) {

                        String latitudeFirebase = objSnapshot.child("crn_latitude").getValue().toString();
                        String longitudeFirebase = objSnapshot.child("crn_longitude").getValue().toString();
                        String nomeFirebase = objSnapshot.child("crn_nome").getValue().toString();
                        String idFirebase = objSnapshot.child("crn_arv_id").getValue().toString();
                        String especieFirebase = objSnapshot.child("crn_especie_id").getValue().toString();
                        String fiacaoFirebase = objSnapshot.child("crn_fiacao_id").getValue().toString();
                        String alturaArvoreFirebase = objSnapshot.child("crn_altura_arvore").getValue().toString();
                        String alturaCopaFirebase = objSnapshot.child("crn_altura_copa").getValue().toString();
                        String alturaFiacaoFirebase = objSnapshot.child("crn_altura_fiacao").getValue().toString();
                        String alturaRamoFirebase = objSnapshot.child("crn_altura_ramo").getValue().toString();
                        String areaFirebase = objSnapshot.child("crn_area_id").getValue().toString();
                        String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                        String diametroFirebase = objSnapshot.child("crn_diametro_arvore").getValue().toString();
                        String doencaFirebase = objSnapshot.child("crn_doenca_praga").getValue().toString();
                        String idadeFirebase = objSnapshot.child("crn_idade_arvore").getValue().toString();
                        String inclinacaoFirebase = objSnapshot.child("crn_inclinacao_id").getValue().toString();
                        String intervencaoFirebase = objSnapshot.child("crn_intervencao_id").getValue().toString();
                        String larguraFirebase = objSnapshot.child("crn_largura_id").getValue().toString();
                        String motivoInativoFirebase = objSnapshot.child("crn_motivo_inativo").getValue().toString();
                        String obsArvoreFirebase = objSnapshot.child("crn_obs_arvore").getValue().toString();
                        String plantioFirebase = objSnapshot.child("crn_plantio_id").getValue().toString();
                        String podaFirebase = objSnapshot.child("crn_poda_id").getValue().toString();
                        String qualidadeFirebase = objSnapshot.child("crn_qualidade_id").getValue().toString();
                        String sistemaFirebase = objSnapshot.child("crn_sistema_id").getValue().toString();
                        String ultAtualizacaoFirebase = objSnapshot.child("crn_ult_atualizacao").getValue().toString();
                        String usuarioFirebase = objSnapshot.child("crn_usuario_id").getValue().toString();

                        double arvoreLng = Double.parseDouble(longitudeFirebase);
                        double arvoreLat = Double.parseDouble(latitudeFirebase);

                        Cursor cursor = bancoDados.rawQuery("SELECT crn_arv_id FROM CRN_ARVORES WHERE crn_latitude = '" + tratarDecimais(arvoreLat) + "' AND crn_longitude = '" + tratarDecimais(arvoreLng) + "'", null);

                        if (cursor != null) {

                            int indiceColunaID = cursor.getColumnIndex("crn_arv_id");
                            cursor.moveToFirst();

                            int totalLinhas = cursor.getCount();
                            if (totalLinhas == 0) {
                                bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id , crn_latitude, crn_longitude,crn_nome, crn_especie_id, crn_plantio_id," +
                                        "crn_largura_id, crn_inclinacao_id, crn_fiacao_id, crn_sistema_id,crn_intervencao_id,crn_poda_id," +
                                        "crn_qualidade_id, crn_area_id,crn_idade_arvore,crn_diametro_arvore,crn_altura_arvore,crn_altura_ramo," +
                                        "crn_altura_fiacao,crn_altura_copa,crn_doenca_praga,crn_obs_arvore, crn_ativo, crn_ult_atualizacao, crn_usuario_id, crn_motivo_inativo) " +
                                        "VALUES ('" + idFirebase + "','" + latitudeFirebase + "','" + longitudeFirebase + "','" + nomeFirebase + "','"
                                        + especieFirebase + "','" + plantioFirebase + "','" + larguraFirebase + "','" + inclinacaoFirebase + "','"
                                        + fiacaoFirebase + "','" + sistemaFirebase + "','" + intervencaoFirebase + "','" + podaFirebase + "','"
                                        + qualidadeFirebase + "','" + areaFirebase + "','" + idadeFirebase + "','" + diametroFirebase + "','"
                                        + alturaArvoreFirebase + "','" + alturaRamoFirebase + "','" + alturaFiacaoFirebase + "','" + alturaCopaFirebase + "','"
                                        + doencaFirebase + "','" + obsArvoreFirebase + "',' " + ativoFirebase + "','" + ultAtualizacaoFirebase + "','"
                                        + usuarioFirebase + "','" + motivoInativoFirebase + "')");
                            } else {
                                int codigoLite = cursor.getInt(indiceColunaID);
                                bancoDados.execSQL("UPDATE CRN_ARVORES SET crn_nome = '" + nomeFirebase + "', crn_especie_id = '" + especieFirebase
                                        + "', crn_fiacao_id = '" + fiacaoFirebase
                                        + "', crn_altura_arvore = '" + alturaArvoreFirebase + "', crn_altura_copa = '" + alturaCopaFirebase
                                        + "', crn_altura_fiacao = '" + alturaFiacaoFirebase + "', crn_altura_ramo = '" + alturaRamoFirebase
                                        + "', crn_area_id = '" + areaFirebase + "', crn_diametro_arvore = '" + diametroFirebase
                                        + "', crn_doenca_praga = '" + doencaFirebase + "', crn_idade_arvore = '" + idadeFirebase
                                        + "', crn_inclinacao_id = '" + inclinacaoFirebase + "', crn_intervencao_id = '" + intervencaoFirebase
                                        + "', crn_largura_id = '" + larguraFirebase + "', crn_obs_arvore = '" + obsArvoreFirebase
                                        + "', crn_plantio_id = '" + plantioFirebase + "', crn_poda_id = '" + podaFirebase
                                        + "', crn_qualidade_id = '" + qualidadeFirebase + "', crn_sistema_id ='" + sistemaFirebase
                                        + "', crn_ult_atualizacao = '" + ultAtualizacaoFirebase + "', crn_ativo = '" + ativoFirebase
                                        + "', crn_usuario_id = '" + usuarioFirebase + "', crn_motivo_inativo = '" + motivoInativoFirebase
                                        + "' WHERE crn_arv_id = '" + codigoLite + "'");
                            }
                            cursor.close();
                        } else {
                            bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id , crn_latitude, crn_longitude,crn_nome, crn_especie_id, crn_plantio_id," +
                                    "crn_largura_id, crn_inclinacao_id, crn_fiacao_id, crn_sistema_id,crn_intervencao_id,crn_poda_id," +
                                    "crn_qualidade_id, crn_area_id,crn_idade_arvore,crn_diametro_arvore,crn_altura_arvore,crn_altura_ramo," +
                                    "crn_altura_fiacao,crn_altura_copa,crn_doenca_praga,crn_obs_arvore, crn_ativo, crn_ult_atualizacao, crn_usuario_id, crn_motivo_inativo) " +
                                    "VALUES ('" + idFirebase + "','" + latitudeFirebase + "','" + longitudeFirebase + "','" + nomeFirebase + "','"
                                    + especieFirebase + "','" + plantioFirebase + "','" + larguraFirebase + "','" + inclinacaoFirebase + "','"
                                    + fiacaoFirebase + "','" + sistemaFirebase + "','" + intervencaoFirebase + "','" + podaFirebase + "','"
                                    + qualidadeFirebase + "','" + areaFirebase + "','" + idadeFirebase + "','" + diametroFirebase + "','"
                                    + alturaArvoreFirebase + "','" + alturaRamoFirebase + "','" + alturaFiacaoFirebase + "','" + alturaCopaFirebase + "','"
                                    + doencaFirebase + "','" + obsArvoreFirebase + "',' " + ativoFirebase + "','" + ultAtualizacaoFirebase + "','"
                                    + usuarioFirebase + "','" + motivoInativoFirebase + "')");
                        }

                        Log.e("quebra", "2");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFiacaoFirebase(){

        getFiacaoReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_FIACAO (crn_fiacao_id INTEGER PRIMARY KEY, crn_tipo VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_FIACAO");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int fiacaoIDFirebase = Integer.parseInt(objSnapshot.child("crn_fiacao_id").getValue().toString());
                    String tipoFirebase = objSnapshot.child("crn_tipo").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_FIACAO (crn_fiacao_id, crn_tipo, crn_ativo) VALUES ("
                            + fiacaoIDFirebase + ",'" + tipoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getInclinacaoFirebase(){

        getInclinacaoReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_INCLINACAO_TRONCO (crn_inclinacao_id INTEGER PRIMARY KEY, crn_inclinacao VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_INCLINACAO_TRONCO");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int inclinacaoIDFirebase = Integer.parseInt(objSnapshot.child("crn_inclinacao_id").getValue().toString());
                    String inclinacaoFirebase = objSnapshot.child("crn_inclinacao").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_INCLINACAO_TRONCO (crn_inclinacao_id, crn_inclinacao, crn_ativo) VALUES ("
                            + inclinacaoIDFirebase + ",'" + inclinacaoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getCalcadaFirebase(){

        getCalcadaReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_LARGURA_CALCADA (crn_largura_id INTEGER PRIMARY KEY, crn_largura VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_LARGURA_CALCADA");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int larguraIDFirebase = Integer.parseInt(objSnapshot.child("crn_largura_id").getValue().toString());
                    String larguraFirebase = objSnapshot.child("largura").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_LARGURA_CALCADA (crn_largura_id, crn_largura, crn_ativo) VALUES ("
                            + larguraIDFirebase + ",'" + larguraFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getPlantioFirebase(){

        getPlantioReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_PLANTIO_PAVIMENTACAO (crn_plantio_id INTEGER PRIMARY KEY, crn_pavimento VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_PLANTIO_PAVIMENTACAO");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int plantioIDFirebase = Integer.parseInt(objSnapshot.child("crn_plantio_id").getValue().toString());
                    String pavimentoFirebase = objSnapshot.child("crn_pavimento").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_PLANTIO_PAVIMENTACAO (crn_plantio_id, crn_pavimento, crn_ativo) VALUES ("
                            + plantioIDFirebase + ",'" + pavimentoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getRadicularFirebase(){

        getRadicularReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_SISTEMA_RADICULAR (crn_sistema_id INTEGER PRIMARY KEY, crn_tipo VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_SISTEMA_RADICULAR");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int radicularIDFirebase = Integer.parseInt(objSnapshot.child("crn_sistema_id").getValue().toString());
                    String tipoFirebase = objSnapshot.child("crn_tipo").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_SISTEMA_RADICULAR (crn_sistema_id, crn_tipo, crn_ativo) VALUES ("
                            + radicularIDFirebase + ",'" + tipoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getIntervencaoFirebase(){

        getIntervencaoReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_TIPO_INTERVENCAO (crn_intervencao_id INTEGER PRIMARY KEY, crn_tipo VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_TIPO_INTERVENCAO");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int intervencaoIDFirebase = Integer.parseInt(objSnapshot.child("crn_intervencao_id").getValue().toString());
                    String tipoFirebase = objSnapshot.child("crn_tipo").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_TIPO_INTERVENCAO (crn_intervencao_id, crn_tipo, crn_ativo) VALUES ("
                            + intervencaoIDFirebase + ",'" + tipoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getPodaFirebase(){

        getPodaReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_TIPO_PODA (crn_poda_id INTEGER PRIMARY KEY, crn_tipo_poda VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_TIPO_PODA");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int podaIDFirebase = Integer.parseInt(objSnapshot.child("crn_poda_id").getValue().toString());
                    String tipoFirebase = objSnapshot.child("crn_tipo_poda").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_TIPO_PODA (crn_poda_id, crn_tipo_poda, crn_ativo) VALUES ("
                            + podaIDFirebase + ",'" + tipoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getQualidadeFirebase(){

        getQualidadeReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_TIPO_QUALIDADE (crn_qualidade_id INTEGER PRIMARY KEY, crn_tipo_qualidade VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_TIPO_QUALIDADE");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int qualidadeIDFirebase = Integer.parseInt(objSnapshot.child("crn_qualidade_id").getValue().toString());
                    String tipoFirebase = objSnapshot.child("crn_tipo_qualidade").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_TIPO_QUALIDADE (crn_qualidade_id, crn_tipo_qualidade, crn_ativo) VALUES ("
                            + qualidadeIDFirebase + ",'" + tipoFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getAreaFirebase(){

        getAreaReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_AREA_LIVRE (crn_area_id INTEGER PRIMARY KEY, crn_area VARCHAR, crn_ativo VARCHAR)");
                bancoDados.execSQL("DELETE FROM CRN_AREA_LIVRE");
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    int areaIDFirebase = Integer.parseInt(objSnapshot.child("crn_area_id").getValue().toString());
                    String areaFirebase = objSnapshot.child("crn_area").getValue().toString();
                    String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                    String comandoSQL = "INSERT INTO CRN_AREA_LIVRE (crn_area_id, crn_area, crn_ativo) VALUES ("
                            + areaIDFirebase + ",'" + areaFirebase + "','"+ ativoFirebase + "')";
                    bancoDados.execSQL(comandoSQL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String tratarDecimais(double numero) {
        String numeroTratado = String.valueOf(numero);
        String finalNumero;
        String inicioNumero;
        String retornoNumero;
        int indicePonto = numeroTratado.indexOf(".");
        finalNumero = numeroTratado.substring(indicePonto, indicePonto + 7);
        inicioNumero = numeroTratado.substring(0, indicePonto);
        retornoNumero = inicioNumero + finalNumero;
        return retornoNumero;
    }
}