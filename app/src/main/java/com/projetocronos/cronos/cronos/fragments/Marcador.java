package com.projetocronos.cronos.cronos.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.helper.AdapterImg;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_WORLD_READABLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Marcador extends Fragment {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private EditText descricaoMarcador;
    private EditText latitudeMarcador;
    private EditText longitudeMarcador;
    private List<String> listaEspecie = new ArrayList<String>();
    private List<String> posicaoEspecie = new ArrayList<String>();
    private Spinner spEspecie;
    private Button gravarDados;
    private Spinner spFiacao;
    private Spinner spInclinacao;
    private Spinner spCalcada;
    private Spinner spPlantio;
    private Spinner spRadicular;
    private Spinner spIntervencao;
    private Spinner spPoda;
    private Spinner spQualidade;
    private Spinner spArea;
    private List<String> listaFiacao = new ArrayList<String>();
    private List<String> posicaoFiacao = new ArrayList<String>();
    private List<String> listaInclinacao = new ArrayList<String>();
    private List<String> posicaoInclinacao = new ArrayList<String>();
    private List<String> listaCalcada = new ArrayList<String>();
    private List<String> posicaoCalcada = new ArrayList<String>();
    private List<String> listaPlantio = new ArrayList<String>();
    private List<String> posicaoPlantio = new ArrayList<String>();
    private List<String> listaRadicular = new ArrayList<String>();
    private List<String> posicaoRadicular = new ArrayList<String>();
    private List<String> listaIntervencao = new ArrayList<String>();
    private List<String> posicaoIntervencao = new ArrayList<String>();
    private List<String> listaPoda = new ArrayList<String>();
    private List<String> posicaoPoda = new ArrayList<String>();
    private List<String> listaQualidade = new ArrayList<String>();
    private List<String> posicaoQualidade = new ArrayList<String>();
    private List<String> listaArea = new ArrayList<String>();
    private List<String> posicaoArea = new ArrayList<String>();
    private EditText diametroArvore;
    private EditText idadeArvore;
    private EditText alturaArvore;
    private EditText alturaRamo;
    private EditText alturaFiacao;
    private EditText alturaCopa;
    private EditText doencaArvore;
    private EditText obsArvore;
    private Button botaoCamera;
    private Button lastPic;
    private Bitmap bitmap;
    ImageView img;
    private double markerLat;
    private double markerLng;
    private ArrayList<byte[]> imagensArvore = new ArrayList<byte[]>();

    private int totalLinhasCursor = 0;
    private int contadorCursor = 0;


    private DatabaseReference firebaseReferencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference getArvoresReferencia = firebaseReferencia.child("crn_arvores").child("0001");

    public Marcador() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marcador, container, false);

        // Bloco para obter os componentes
        descricaoMarcador = (EditText) view.findViewById(R.id.descricaoMapa);
        latitudeMarcador = (EditText) view.findViewById(R.id.latitudeMapa);
        longitudeMarcador = (EditText) view.findViewById(R.id.longitudeMapa);
        spEspecie = (Spinner) view.findViewById(R.id.spinnerEspecie);
        gravarDados = (Button) view.findViewById(R.id.gravarDados);
        spFiacao = (Spinner) view.findViewById(R.id.spinnerFiacao);
        spInclinacao = (Spinner) view.findViewById(R.id.spinnerInclinacao);
        spCalcada = (Spinner) view.findViewById(R.id.spinnerCalcada);
        spPlantio = (Spinner) view.findViewById(R.id.spinnerPlantio);
        spRadicular = (Spinner) view.findViewById(R.id.spinnerRadicular);
        spIntervencao = (Spinner) view.findViewById(R.id.spinnerIntervencao);
        spPoda = (Spinner) view.findViewById(R.id.spinnerPoda);
        spQualidade = (Spinner) view.findViewById(R.id.spinnerQualidade);
        spArea = (Spinner) view.findViewById(R.id.spinnerArea);
        idadeArvore = (EditText) view.findViewById(R.id.idadeArvore);
        diametroArvore = (EditText) view.findViewById(R.id.diametroArvore);
        alturaArvore = (EditText) view.findViewById(R.id.alturaArvore);
        alturaRamo = (EditText) view.findViewById(R.id.alturaRamoArvore);
        alturaFiacao = (EditText) view.findViewById(R.id.alturaFiacaoArvore);
        alturaCopa = (EditText) view.findViewById(R.id.alturaCopaArvore);
        doencaArvore = (EditText) view.findViewById(R.id.doencaArvore);
        obsArvore = (EditText) view.findViewById(R.id.obsArvore);
        botaoCamera = (Button) view.findViewById(R.id.acessarCamera);
        img = (ImageView) view.findViewById(R.id.imageView);

        botaoCamera.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamera();
            }
        }));

        ViewPager vp = (ViewPager) view.findViewById(R.id.viewPager);
        imagensArvore = popularPager(imagensArvore);
        vp.setAdapter(new AdapterImg(getContext(), imagensArvore));

        gravarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descricaoInformada = descricaoMarcador.getText().toString();
                String latInformada = latitudeMarcador.getText().toString();
                String lngInformada = longitudeMarcador.getText().toString();
                String especieInformada = String.valueOf(spEspecie.getSelectedItemPosition());
                String fiacaoInformada = String.valueOf(spFiacao.getSelectedItemPosition());
                String inclinacaoInformada = String.valueOf(spInclinacao.getSelectedItemPosition());
                String calcadaInformada = String.valueOf(spCalcada.getSelectedItemPosition());
                String platioInformado = String.valueOf(spPlantio.getSelectedItemPosition());
                String radicularInformado = String.valueOf(spRadicular.getSelectedItemPosition());
                String intervencaoInformada = String.valueOf(spIntervencao.getSelectedItemPosition());
                String podaInformada = String.valueOf(spPoda.getSelectedItemPosition());
                String qualidadeInformada = String.valueOf(spQualidade.getSelectedItemPosition());
                String areaInformada = String.valueOf(spArea.getSelectedItemPosition());
                String idadeInformada = idadeArvore.getText().toString();
                String diametroInformado = diametroArvore.getText().toString();
                String alturaArvoreInformada = alturaArvore.getText().toString();
                String alturaRamoInformada = alturaRamo.getText().toString();
                String alturaFiacaoInformada = alturaFiacao.getText().toString();
                String alturaCopaInformada = alturaCopa.getText().toString();
                String doencaInformada = doencaArvore.getText().toString();
                String obsInformada = obsArvore.getText().toString();

                if (descricaoInformada.isEmpty() || latInformada.isEmpty() || lngInformada.isEmpty() || especieInformada.isEmpty()
                        || fiacaoInformada.isEmpty() || inclinacaoInformada.isEmpty() || calcadaInformada.isEmpty() || platioInformado.isEmpty()
                        || radicularInformado.isEmpty() || intervencaoInformada.isEmpty() || podaInformada.isEmpty() || qualidadeInformada.isEmpty()
                        || areaInformada.isEmpty() || idadeInformada.isEmpty() || diametroInformado.isEmpty() || alturaArvoreInformada.isEmpty()
                        || alturaRamoInformada.isEmpty() || alturaFiacaoInformada.isEmpty() || alturaCopaInformada.isEmpty()
                        ) {
                    Toast.makeText(getContext(), "Necessário informar todos os campos", Toast.LENGTH_LONG).show();
                } else {
                    String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
                    SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                    bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARVORES (crn_arv_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_latitude VARCHAR, crn_longitude VARCHAR, crn_nome VARCHAR, crn_especie_id VARCHAR, crn_fiacao_id VARCHAR)");

                    Cursor cursor = bancoDados.rawQuery("SELECT crn_arv_id, crn_fiacao_id, crn_latitude, crn_longitude, crn_nome FROM CRN_ARVORES WHERE crn_latitude = '" + latInformada + "' AND crn_longitude = '" + lngInformada + "'", null);

                    int indiceColunaID = cursor.getColumnIndex("crn_arv_id");

                    cursor.moveToFirst();
                    int totalLinhas = cursor.getCount();
                    if (totalLinhas == 0) {

                        String idFirebase = "1";
                        Cursor cursorSequence = bancoDados.rawQuery("SELECT crn_arv_id FROM CRN_ARVORES ORDER BY crn_arv_id DESC LIMIT 1", null);

                        cursorSequence.moveToFirst();
                        int indiceColunaCodigo = cursorSequence.getColumnIndex("crn_arv_id");

                        int totalLinhasSequence = cursorSequence.getCount();
                        if (totalLinhasSequence > 0) {
                            idFirebase = String.valueOf(Integer.parseInt(cursorSequence.getString(indiceColunaCodigo)) + 1);
                        }
                        bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id , crn_latitude, crn_longitude,crn_nome, crn_especie_id, crn_plantio_id," +
                                "crn_largura_id, crn_inclinacao_id,crn_fiacao_id, crn_sistema_id,crn_intervencao_id,crn_poda_id," +
                                "crn_qualidade_id,crn_area_id,crn_idade_arvore,crn_diametro_arvore,crn_altura_arvore,crn_altura_ramo," +
                                "crn_altura_fiacao,crn_altura_copa,crn_doenca_praga,crn_obs_arvore, crn_ativo, crn_ult_atualizacao, crn_usuario_id) " +
                                "VALUES ('" + idFirebase + "','" + latInformada + "','" + lngInformada + "','" + descricaoInformada + "','"
                                + posicaoEspecie.get(Integer.parseInt(especieInformada)) + "','" + posicaoPlantio.get(Integer.parseInt(platioInformado)) + "','"
                                + posicaoCalcada.get(Integer.parseInt(calcadaInformada)) + "','" + posicaoInclinacao.get(Integer.parseInt(inclinacaoInformada)) + "','"
                                + posicaoFiacao.get(Integer.parseInt(fiacaoInformada)) + "','" + posicaoRadicular.get(Integer.parseInt(radicularInformado)) + "','"
                                + posicaoIntervencao.get(Integer.parseInt(intervencaoInformada)) + "','" + posicaoPoda.get(Integer.parseInt(podaInformada)) + "','"
                                + posicaoQualidade.get(Integer.parseInt(qualidadeInformada)) + "','" + posicaoArea.get(Integer.parseInt(areaInformada)) + "','"
                                + idadeInformada + "','" + diametroInformado + "','" + alturaArvoreInformada + "','" + alturaRamoInformada + "','"
                                + alturaFiacaoInformada + "','" + alturaCopaInformada + "','" + doencaInformada + "','" + obsInformada + "','1','"
                                + currentDateTimeString + "','0" + "')");

                        DatabaseReference atualizarArvore = getArvoresReferencia.child(idFirebase);
                        atualizarArvore.child("crn_nome").setValue(descricaoInformada);
                        atualizarArvore.child("crn_latitude").setValue(latInformada);
                        atualizarArvore.child("crn_longitude").setValue(lngInformada);
                        atualizarArvore.child("crn_arv_id").setValue(idFirebase);
                        atualizarArvore.child("crn_altura_arvore").setValue(alturaArvoreInformada);
                        atualizarArvore.child("crn_altura_copa").setValue(alturaCopaInformada);
                        atualizarArvore.child("crn_altura_fiacao").setValue(alturaFiacaoInformada);
                        atualizarArvore.child("crn_altura_ramo").setValue(alturaRamoInformada);
                        atualizarArvore.child("crn_area_id").setValue(posicaoArea.get(Integer.parseInt(areaInformada)));
                        atualizarArvore.child("crn_diametro_arvore").setValue(diametroInformado);
                        atualizarArvore.child("crn_doenca_praga").setValue(doencaInformada);
                        atualizarArvore.child("crn_especie_id").setValue(posicaoEspecie.get(Integer.parseInt(especieInformada)));
                        atualizarArvore.child("crn_fiacao_id").setValue(posicaoFiacao.get(Integer.parseInt(fiacaoInformada)));
                        atualizarArvore.child("crn_idade_arvore").setValue(idadeInformada);
                        atualizarArvore.child("crn_inclinacao_id").setValue(posicaoInclinacao.get(Integer.parseInt(inclinacaoInformada)));
                        atualizarArvore.child("crn_intervencao_id").setValue(posicaoIntervencao.get(Integer.parseInt(intervencaoInformada)));
                        atualizarArvore.child("crn_largura_id").setValue(posicaoCalcada.get(Integer.parseInt(calcadaInformada)));
                        atualizarArvore.child("crn_obs_arvore").setValue(obsInformada);
                        atualizarArvore.child("crn_plantio_id").setValue(posicaoPlantio.get(Integer.parseInt(platioInformado)));
                        atualizarArvore.child("crn_poda_id").setValue(posicaoPoda.get(Integer.parseInt(podaInformada)));
                        atualizarArvore.child("crn_qualidade_id").setValue(posicaoQualidade.get(Integer.parseInt(qualidadeInformada)));
                        atualizarArvore.child("crn_sistema_id").setValue(posicaoRadicular.get(Integer.parseInt(radicularInformado)));
                        atualizarArvore.child("crn_ult_atualizacao").setValue(currentDateTimeString);
                        atualizarArvore.child("crn_ativo").setValue("1");
                        atualizarArvore.child("crn_usuario_id").setValue("0");
                        atualizarArvore.child("crn_motivo_inativo").setValue("0");

                    }
                    int contador = 0;
                    while (contador < totalLinhas) {
                        String campoID = cursor.getString(indiceColunaID);

                        DatabaseReference atualizarArvore = getArvoresReferencia.child(campoID);
                        atualizarArvore.child("crn_nome").setValue(descricaoInformada);
                        atualizarArvore.child("crn_altura_arvore").setValue(alturaArvoreInformada);
                        atualizarArvore.child("crn_altura_copa").setValue(alturaCopaInformada);
                        atualizarArvore.child("crn_altura_fiacao").setValue(alturaFiacaoInformada);
                        atualizarArvore.child("crn_altura_ramo").setValue(alturaRamoInformada);
                        atualizarArvore.child("crn_area_id").setValue(posicaoArea.get(Integer.parseInt(areaInformada)));
                        atualizarArvore.child("crn_diametro_arvore").setValue(diametroInformado);
                        atualizarArvore.child("crn_doenca_praga").setValue(doencaInformada);
                        atualizarArvore.child("crn_especie_id").setValue(posicaoEspecie.get(Integer.parseInt(especieInformada)));
                        atualizarArvore.child("crn_fiacao_id").setValue(posicaoFiacao.get(Integer.parseInt(fiacaoInformada)));
                        atualizarArvore.child("crn_idade_arvore").setValue(idadeInformada);
                        atualizarArvore.child("crn_inclinacao_id").setValue(posicaoInclinacao.get(Integer.parseInt(inclinacaoInformada)));
                        atualizarArvore.child("crn_intervencao_id").setValue(posicaoIntervencao.get(Integer.parseInt(intervencaoInformada)));
                        atualizarArvore.child("crn_largura_id").setValue(posicaoCalcada.get(Integer.parseInt(calcadaInformada)));
                        atualizarArvore.child("crn_obs_arvore").setValue(obsInformada);
                        atualizarArvore.child("crn_plantio_id").setValue(posicaoPlantio.get(Integer.parseInt(platioInformado)));
                        atualizarArvore.child("crn_poda_id").setValue(posicaoPoda.get(Integer.parseInt(podaInformada)));
                        atualizarArvore.child("crn_qualidade_id").setValue(posicaoQualidade.get(Integer.parseInt(qualidadeInformada)));
                        atualizarArvore.child("crn_sistema_id").setValue(posicaoRadicular.get(Integer.parseInt(radicularInformado)));
                        atualizarArvore.child("crn_ult_atualizacao").setValue(currentDateTimeString);

                        bancoDados.execSQL("UPDATE CRN_ARVORES SET crn_nome = '" + descricaoInformada + "', crn_especie_id = '" + posicaoEspecie.get(Integer.parseInt(especieInformada))
                                + "', crn_fiacao_id = '" + posicaoFiacao.get(Integer.parseInt(fiacaoInformada))
                                + "', crn_altura_arvore = '" + alturaArvoreInformada + "', crn_altura_copa = '" + alturaCopaInformada
                                + "', crn_altura_fiacao = '" + alturaFiacaoInformada + "', crn_altura_ramo = '" + alturaRamoInformada
                                + "', crn_area_id = '" + posicaoArea.get(Integer.parseInt(areaInformada)) + "', crn_diametro_arvore = '" + diametroInformado
                                + "', crn_doenca_praga = '" + doencaInformada + "', crn_idade_arvore = '" + idadeInformada
                                + "', crn_inclinacao_id = '" + posicaoInclinacao.get(Integer.parseInt(inclinacaoInformada)) + "', crn_intervencao_id = '" + posicaoIntervencao.get(Integer.parseInt(intervencaoInformada))
                                + "', crn_largura_id = '" + posicaoCalcada.get(Integer.parseInt(calcadaInformada)) + "', crn_obs_arvore = '" + obsInformada
                                + "', crn_plantio_id = '" + posicaoPlantio.get(Integer.parseInt(platioInformado)) + "', crn_poda_id = '" + posicaoPoda.get(Integer.parseInt(podaInformada))
                                + "', crn_qualidade_id = '" + posicaoQualidade.get(Integer.parseInt(qualidadeInformada)) + "', crn_sistema_id ='" + posicaoRadicular.get(Integer.parseInt(radicularInformado))
                                + "', crn_ult_atualizacao = '" + currentDateTimeString
                                + "' WHERE crn_arv_id = '" + campoID + "'");

                        cursor.moveToNext();
                        contador = contador + 1;
                    }
                    Toast.makeText(getContext(), "Dados Atualizados com Sucesso!", Toast.LENGTH_SHORT).show();
                    cursor.close();
                }
            }
        });

        SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ESPECIE (crn_especie_id INTEGER PRIMARY KEY, crn_nome_pop VARCHAR, crn_nome_real VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_FIACAO (crn_fiacao_id INTEGER PRIMARY KEY, crn_tipo VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_INCLINACAO_TRONCO (crn_inclinacao_id INTEGER PRIMARY KEY, crn_inclinacao VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_LARGURA_CALCADA (crn_largura_id INTEGER PRIMARY KEY, crn_largura VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_PLANTIO_PAVIMENTACAO (crn_plantio_id INTEGER PRIMARY KEY, crn_pavimento VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_SISTEMA_RADICULAR (crn_sistema_id INTEGER PRIMARY KEY, crn_tipo VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_TIPO_INTERVENCAO (crn_intervencao_id INTEGER PRIMARY KEY, crn_tipo VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_TIPO_PODA (crn_poda_id INTEGER PRIMARY KEY, crn_tipo_poda VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_TIPO_QUALIDADE (crn_qualidade_id INTEGER PRIMARY KEY, crn_tipo_qualidade VARCHAR, crn_ativo VARCHAR)");
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_AREA_LIVRE (crn_area_id INTEGER PRIMARY KEY, crn_area VARCHAR, crn_ativo VARCHAR)");

        Cursor cursor = bancoDados.rawQuery("SELECT crn_especie_id, crn_nome_pop FROM CRN_ESPECIE WHERE crn_ativo = '1'", null);
        Cursor cursorFiacao = bancoDados.rawQuery("SELECT crn_fiacao_id, crn_tipo FROM CRN_FIACAO WHERE crn_ativo = '1'", null);
        Cursor cursorInclinacao = bancoDados.rawQuery("SELECT crn_inclinacao_id, crn_inclinacao FROM CRN_INCLINACAO_TRONCO WHERE crn_ativo = '1'", null);
        Cursor cursorCalcada = bancoDados.rawQuery("SELECT crn_largura_id, crn_largura FROM CRN_LARGURA_CALCADA WHERE crn_ativo = '1'", null);
        Cursor cursorPlantio = bancoDados.rawQuery("SELECT crn_plantio_id, crn_pavimento FROM CRN_PLANTIO_PAVIMENTACAO WHERE crn_ativo = '1'", null);
        Cursor cursorRadicular = bancoDados.rawQuery("SELECT crn_sistema_id, crn_tipo FROM CRN_SISTEMA_RADICULAR WHERE crn_ativo = '1'", null);
        Cursor cursorIntervencao = bancoDados.rawQuery("SELECT crn_intervencao_id, crn_tipo FROM CRN_TIPO_INTERVENCAO WHERE crn_ativo = '1'", null);
        Cursor cursorPoda = bancoDados.rawQuery("SELECT crn_poda_id, crn_tipo_poda FROM CRN_TIPO_PODA WHERE crn_ativo = '1'", null);
        Cursor cursorQualidade = bancoDados.rawQuery("SELECT crn_qualidade_id, crn_tipo_qualidade FROM CRN_TIPO_QUALIDADE WHERE crn_ativo = '1'", null);
        Cursor cursorArea = bancoDados.rawQuery("SELECT crn_area_id, crn_area FROM CRN_AREA_LIVRE WHERE crn_ativo = '1'", null);

        int indiceColunaID = cursor.getColumnIndex("crn_especie_id");
        int indiceColunaPop = cursor.getColumnIndex("crn_nome_pop");

        cursor.moveToFirst();

        totalLinhasCursor = cursor.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursor.getString(indiceColunaID);
            String campoPop = cursor.getString(indiceColunaPop);

            listaEspecie.add(campoPop);
            posicaoEspecie.add(campoID);

            cursor.moveToNext();
            contadorCursor = contadorCursor + 1;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaEspecie);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spEspecie.setAdapter(adapter);

        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDFiacao = cursorFiacao.getColumnIndex("crn_fiacao_id");
        int indiceColunaTipoFiacao = cursorFiacao.getColumnIndex("crn_tipo");

        cursorFiacao.moveToFirst();

        totalLinhasCursor = cursorFiacao.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorFiacao.getString(indiceColunaIDFiacao);
            String campoPop = cursorFiacao.getString(indiceColunaTipoFiacao);

            listaFiacao.add(campoPop);
            posicaoFiacao.add(campoID);

            cursorFiacao.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterFiacao = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaFiacao);
        adapterFiacao.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spFiacao.setAdapter(adapterFiacao);

        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDInclinacao = cursorInclinacao.getColumnIndex("crn_inclinacao_id");
        int indiceColunaInclinacao = cursorInclinacao.getColumnIndex("crn_inclinacao");

        cursorInclinacao.moveToFirst();

        totalLinhasCursor = cursorInclinacao.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorInclinacao.getString(indiceColunaIDInclinacao);
            String campoPop = cursorInclinacao.getString(indiceColunaInclinacao);

            listaInclinacao.add(campoPop);
            posicaoInclinacao.add(campoID);

            cursorInclinacao.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterInclinacao = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaInclinacao);
        adapterInclinacao.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spInclinacao.setAdapter(adapterInclinacao);

        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDCalcada = cursorCalcada.getColumnIndex("crn_largura_id");
        int indiceColunaCalcada = cursorCalcada.getColumnIndex("crn_largura");

        cursorCalcada.moveToFirst();

        totalLinhasCursor = cursorInclinacao.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorCalcada.getString(indiceColunaIDCalcada);
            String campoPop = cursorCalcada.getString(indiceColunaCalcada);

            listaCalcada.add(campoPop);
            posicaoCalcada.add(campoID);

            cursorInclinacao.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterCalcada = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaCalcada);
        adapterCalcada.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spCalcada.setAdapter(adapterCalcada);


        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDPlantio = cursorPlantio.getColumnIndex("crn_plantio_id");
        int indiceColunaPlantio = cursorPlantio.getColumnIndex("crn_pavimento");

        cursorPlantio.moveToFirst();

        totalLinhasCursor = cursorPlantio.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorPlantio.getString(indiceColunaIDPlantio);
            String campoPop = cursorPlantio.getString(indiceColunaPlantio);

            listaPlantio.add(campoPop);
            posicaoPlantio.add(campoID);

            cursorPlantio.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterPlantio = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaPlantio);
        adapterPlantio.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spPlantio.setAdapter(adapterPlantio);


        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDRadicular = cursorRadicular.getColumnIndex("crn_sistema_id");
        int indiceColunaRadicular = cursorRadicular.getColumnIndex("crn_tipo");

        cursorRadicular.moveToFirst();

        totalLinhasCursor = cursorRadicular.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorRadicular.getString(indiceColunaIDRadicular);
            String campoPop = cursorRadicular.getString(indiceColunaRadicular);

            listaRadicular.add(campoPop);
            posicaoRadicular.add(campoID);

            cursorRadicular.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterRadicular = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaRadicular);
        adapterRadicular.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spRadicular.setAdapter(adapterRadicular);


        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDInter = cursorIntervencao.getColumnIndex("crn_intervencao_id");
        int indiceColunaIntervencao = cursorIntervencao.getColumnIndex("crn_tipo");

        cursorIntervencao.moveToFirst();

        totalLinhasCursor = cursorIntervencao.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorIntervencao.getString(indiceColunaIDInter);
            String campoPop = cursorIntervencao.getString(indiceColunaIntervencao);

            listaIntervencao.add(campoPop);
            posicaoIntervencao.add(campoID);

            cursorIntervencao.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterIntervencao = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaIntervencao);
        adapterIntervencao.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spIntervencao.setAdapter(adapterIntervencao);


        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDPoda = cursorPoda.getColumnIndex("crn_poda_id");
        int indiceColunaPoda = cursorPoda.getColumnIndex("crn_tipo_poda");

        cursorPoda.moveToFirst();

        totalLinhasCursor = cursorPoda.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorPoda.getString(indiceColunaIDPoda);
            String campoPop = cursorPoda.getString(indiceColunaPoda);

            listaPoda.add(campoPop);
            posicaoPoda.add(campoID);

            cursorPoda.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterPoda = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaPoda);
        adapterPoda.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spPoda.setAdapter(adapterPoda);

        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDQualidade = cursorQualidade.getColumnIndex("crn_qualidade_id");
        int indiceColunaQualidade = cursorQualidade.getColumnIndex("crn_tipo_qualidade");

        cursorQualidade.moveToFirst();

        totalLinhasCursor = cursorQualidade.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorQualidade.getString(indiceColunaIDQualidade);
            String campoPop = cursorQualidade.getString(indiceColunaQualidade);

            listaQualidade.add(campoPop);
            posicaoQualidade.add(campoID);

            cursorQualidade.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterQualidade = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaQualidade);
        adapterQualidade.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spQualidade.setAdapter(adapterQualidade);

        totalLinhasCursor = 0;
        contadorCursor = 0;

        int indiceColunaIDArea = cursorArea.getColumnIndex("crn_area_id");
        int indiceColunaArea = cursorArea.getColumnIndex("crn_area");

        cursorArea.moveToFirst();

        totalLinhasCursor = cursorArea.getCount();
        contadorCursor = 0;
        while (contadorCursor < totalLinhasCursor) {

            String campoID = cursorArea.getString(indiceColunaIDArea);
            String campoPop = cursorArea.getString(indiceColunaArea);

            listaArea.add(campoPop);
            posicaoArea.add(campoID);

            cursorArea.moveToNext();
            contadorCursor = contadorCursor + 1;
        }
        ArrayAdapter<String> adapterArea = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listaArea);
        adapterArea.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spArea.setAdapter(adapterArea);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String markerTitle = arguments.getString("title");
            descricaoMarcador.setText(markerTitle);

            markerLat = arguments.getDouble("latitude");
            latitudeMarcador.setText(tratarDecimais(markerLat));

            markerLng = arguments.getDouble("longitude");
            longitudeMarcador.setText(tratarDecimais(markerLng));

            String consultaDados = "SELECT crn_arv_id , crn_latitude, crn_longitude, crn_nome, crn_especie_id, crn_plantio_id,crn_largura_id, " +
                    "crn_inclinacao_id,crn_fiacao_id, crn_sistema_id,crn_intervencao_id,crn_poda_id,crn_qualidade_id,crn_area_id,crn_idade_arvore,crn_diametro_arvore," +
                    "crn_altura_arvore,crn_altura_ramo,crn_altura_fiacao,crn_altura_copa,crn_doenca_praga, crn_obs_arvore,crn_ativo,crn_ult_atualizacao,crn_motivo_inativo," +
                    "crn_usuario_id FROM CRN_ARVORES WHERE crn_latitude = '" + tratarDecimais(markerLat) + "' AND crn_longitude = '" + tratarDecimais(markerLng) + "'";


            Cursor dadosArvore = bancoDados.rawQuery(consultaDados, null);

            int dadosEspecie = dadosArvore.getColumnIndex("crn_especie_id");
            int dadosPlantio = dadosArvore.getColumnIndex("crn_plantio_id");
            int dadosLargura = dadosArvore.getColumnIndex("crn_largura_id");
            int dadosInclinacao = dadosArvore.getColumnIndex("crn_inclinacao_id");
            int dadosFiacao = dadosArvore.getColumnIndex("crn_fiacao_id");
            int dadosSistema = dadosArvore.getColumnIndex("crn_sistema_id");
            int dadosIntervencao = dadosArvore.getColumnIndex("crn_intervencao_id");
            int dadosPoda = dadosArvore.getColumnIndex("crn_poda_id");
            int dadosQualidade = dadosArvore.getColumnIndex("crn_qualidade_id");
            int dadosArea = dadosArvore.getColumnIndex("crn_area_id");
            int dadosIdade = dadosArvore.getColumnIndex("crn_idade_arvore");
            int dadosDiametro = dadosArvore.getColumnIndex("crn_diametro_arvore");
            int dadosAltura = dadosArvore.getColumnIndex("crn_altura_arvore");
            int dadosRamo = dadosArvore.getColumnIndex("crn_altura_ramo");
            int dadosAltFiacao = dadosArvore.getColumnIndex("crn_altura_fiacao");
            int dadosCopa = dadosArvore.getColumnIndex("crn_altura_copa");
            int dadosDoenca = dadosArvore.getColumnIndex("crn_doenca_praga");
            int dadosObs = dadosArvore.getColumnIndex("crn_obs_arvore");
            int dadosAtivo = dadosArvore.getColumnIndex("crn_ativo");
            int dadosAtualizacao = dadosArvore.getColumnIndex("crn_ult_atualizacao");
            int dadosMotivo = dadosArvore.getColumnIndex("crn_motivo_inativo");

            totalLinhasCursor = 0;
            contadorCursor = 0;
            dadosArvore.moveToFirst();

            totalLinhasCursor = dadosArvore.getCount();

            if (totalLinhasCursor > contadorCursor) {

                String especieSet = dadosArvore.getString(dadosEspecie);
                int especieIndex = posicaoEspecie.indexOf(especieSet);
                spEspecie.setSelection(especieIndex);

                String plantioSet = dadosArvore.getString(dadosPlantio);
                int plantioIndex = posicaoPlantio.indexOf(plantioSet);
                spPlantio.setSelection(plantioIndex);

                String larguraSet = dadosArvore.getString(dadosLargura);
                int larguraIndex = posicaoCalcada.indexOf(larguraSet);
                spCalcada.setSelection(larguraIndex);

                String inclinacaoSet = dadosArvore.getString(dadosInclinacao);
                int inclinacaoIndex = posicaoInclinacao.indexOf(inclinacaoSet);
                spInclinacao.setSelection(inclinacaoIndex);

                String fiacaoSet = dadosArvore.getString(dadosFiacao);
                int fiacaoIndex = posicaoFiacao.indexOf(fiacaoSet);
                spFiacao.setSelection(fiacaoIndex);

                String sistemaSet = dadosArvore.getString(dadosSistema);
                int sistemaIndex = posicaoRadicular.indexOf(sistemaSet);
                spRadicular.setSelection(sistemaIndex);

                String intervencaoSet = dadosArvore.getString(dadosIntervencao);
                int intervencaoIndex = posicaoIntervencao.indexOf(intervencaoSet);
                spIntervencao.setSelection(intervencaoIndex);

                String podaSet = dadosArvore.getString(dadosPoda);
                int podaIndex = posicaoPoda.indexOf(podaSet);
                spPoda.setSelection(podaIndex);

                String qualidadeSet = dadosArvore.getString(dadosQualidade);
                int qualidadeIndex = posicaoQualidade.indexOf(qualidadeSet);
                spQualidade.setSelection(qualidadeIndex);

                String areaSet = dadosArvore.getString(dadosArea);
                int areaIndex = posicaoArea.indexOf(areaSet);
                spArea.setSelection(areaIndex);

                idadeArvore.setText(dadosArvore.getString(dadosIdade));
                diametroArvore.setText(dadosArvore.getString(dadosDiametro));
                alturaArvore.setText(dadosArvore.getString(dadosAltura));
                alturaRamo.setText(dadosArvore.getString(dadosRamo));
                alturaFiacao.setText(dadosArvore.getString(dadosAltFiacao));
                alturaCopa.setText(dadosArvore.getString(dadosCopa));
                doencaArvore.setText(dadosArvore.getString(dadosDoenca));
                obsArvore.setText(dadosArvore.getString(dadosObs));

                dadosArvore.moveToNext();
                contadorCursor = contadorCursor + 1;
            } else {
            }

        }

        return view;
    }

    /*
        private int getPosicaoLista(String texto, List<String> list){
            int posicao;
            posicao = list.indexOf(texto);
            return posicao;
        }*/
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

    public void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String ambienteImagens = "desenvolvimento/";
        String nomeImagem = UUID.randomUUID() + ".png";
        String path = "imagens/" + ambienteImagens + nomeImagem;
        StorageReference storageRef = storage.getReference(path);
        InputStream stream = null;
        String latitudeInformada = latitudeMarcador.getText().toString();
        String longitudeInformada = longitudeMarcador.getText().toString();
        String idFirebase = obterIDArvore(latitudeInformada, longitudeInformada);
        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        Date hoje = new Date();
        String dataFormatada = formataData.format(hoje);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }

                Bundle bundle = data.getExtras();
                //VERIFICA SE A URI ESTA PREENCHIDA PARA A INTENT DATA
                if (data.getData() != null) {
                    stream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(stream);
                }
                //SE NAO ESTIVER PREENCHIDA UTILIZA O BUNDLE NO PROCESSO
                else {
                    bitmap = (Bitmap) bundle.get("data");
                }
                //TRATA A IMAGEM PARA REDIMENSIONAR
               /* bitmap = (resizeImage(getContext(), bitmap, 1366, 768));
                */ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                //CONVERTE A IMAGEM PARA ARRAY DE BYTES
                byte[] imagemPNG = baos.toByteArray();
               // img.setImageBitmap(BitmapFactory.decodeByteArray(imagemPNG, 0, imagemPNG.length));

                //REALIZA UPLOAD PARA O FIREBASE STORAGE
                UploadTask uploadTask = storageRef.putBytes(imagemPNG);


                try {
                    SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                    bancoDados.execSQL("DELETE FROM CRN_ARV_FOTOS");
                    bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARV_FOTOS(crn_foto_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_arv_id VARCHAR, crn_latitude VARCHAR, crn_longitude VARCHAR, crn_nome_foto VARCHAR, crn_data_foto VARCHAR, crn_ativo VARCHAR, crn_upload VARCHAR, crn_conteudo_foto BLOB, crn_caminho_local VARCHAR, crn_caminho_storage VARCHAR, crn_ult_altercao VARCHAR, crn_conteudo_foto_texto VARCHAR)");

                    if (idFirebase.isEmpty()){
                        if(latitudeInformada.isEmpty() || longitudeInformada.isEmpty()){
                            Toast.makeText(getContext(), "Latitude e Longidtude devem ser preenchidas!", Toast.LENGTH_SHORT).show();
                        }else{
                        bancoDados.execSQL("INSERT INTO CRN_ARV_FOTOS(crn_latitude, crn_longitude, crn_nome_foto, crn_data_foto, crn_ativo, crn_caminho_storage, crn_conteudo_foto_texto) VALUES ("
                                + latitudeInformada + "," + longitudeInformada + ",'" + nomeImagem + "','" + dataFormatada + "', 1,'" + path +  "','" + imagemPNG.toString() + "')");
                        Log.e("exec", "INSERT SEM ID");
                        }
                    }else{
                      /*bancoDados.execSQL("INSERT INTO CRN_ARV_FOTOS(crn_arv_id, crn_latitude, crn_longitude, crn_nome_foto, crn_data_foto, crn_ativo, crn_caminho_storage, crn_conteudo_foto_texto) VALUES ("
                               + idFirebase + "," + latitudeInformada + "," + longitudeInformada + ",'" + nomeImagem + "','" + dataFormatada + "', 1, '" + path + "','" + imagemPNG.toString() + "')");
                        */ContentValues values = new ContentValues();
                        values.put("crn_arv_id", idFirebase);
                        values.put("crn_latitude", latitudeInformada);
                        values.put("crn_longitude", longitudeInformada);
                        values.put("crn_nome_foto", nomeImagem);
                        values.put("crn_data_foto", dataFormatada);
                        values.put("crn_ativo", "1");
                        values.put("crn_caminho_storage", path);
                        values.put("crn_conteudo_foto_texto", imagemPNG.toString());
                        bancoDados.insert("CRN_ARV_FOTOS", null, values);

                        }
                }catch (Exception e){
                    e.printStackTrace();
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                Log.e("log", npe.getMessage());
            } finally {
                if (stream != null)
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }


    private static Bitmap resizeImage(Context context, Bitmap bmpOriginal, float newWidth, float newHeigth) {
        Bitmap novoBmp = null;
        int w = bmpOriginal.getWidth();
        int h = bmpOriginal.getHeight();

        float densityFactor = context.getResources().getDisplayMetrics().density;
        float novoW = newWidth * densityFactor;
        float novoH = newHeigth * densityFactor;
        /*CALCULA ESCALA EM PERCENTUAL DO TAMANHO ORIGINAL PARA O NOVO TAMANHO*/

        float scalaW = novoW / w;
        float scalaH = novoH / h;

        /*CALCULAR UMA MATRIZ PARA MANIPULACAO DA IMAGEM BITMAP*/
        Matrix matrix = new Matrix();

        /*DEFINE A PROPORCAO DA ESCALA PARA A MATRIX*/
        matrix.postScale(scalaW, scalaH);

        /*CRIANDO O NOVO BITMAP COM O NOVO TAMANHO*/
        novoBmp = Bitmap.createBitmap(bmpOriginal, 0, 0, w, h, matrix, true);

        return novoBmp;
    }

    private String obterIDArvore(String latitude, String longitude) {
        String idEncontrado = null;
        if (latitude.isEmpty() || longitude.isEmpty()) {
        } else {

            SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);

            Cursor cursor = bancoDados.rawQuery("SELECT crn_arv_id, crn_fiacao_id, crn_latitude, crn_longitude, crn_nome FROM CRN_ARVORES WHERE crn_latitude = '" + latitude + "' AND crn_longitude = '" + longitude + "'", null);

            int indiceColunaID = cursor.getColumnIndex("crn_arv_id");

            cursor.moveToFirst();
            int totalLinhas = cursor.getCount();

            if (totalLinhas == 0) {
                /*
                Cursor cursorSequence = bancoDados.rawQuery("SELECT crn_arv_id FROM CRN_ARVORES ORDER BY crn_arv_id DESC LIMIT 1", null);

                cursorSequence.moveToFirst();
                int indiceColunaCodigo = cursorSequence.getColumnIndex("crn_arv_id");

                int totalLinhasSequence = cursorSequence.getCount();
                if (totalLinhasSequence > 0) {
                    idEncontrado = String.valueOf(Integer.parseInt(cursorSequence.getString(indiceColunaCodigo)) + 1);
                }*/

                idEncontrado = null;
            }
            int contador = 0;
            while (contador < totalLinhas) {
                idEncontrado = cursor.getString(indiceColunaID);
                contador = contador + 1;
            }

        }
        return idEncontrado;
    }

    private ArrayList<byte[]> popularPager(ArrayList<byte[]> listaImagens){

        try{

            InputStream stream = null;
            SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARV_FOTOS(crn_foto_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_arv_id VARCHAR, crn_latitude VARCHAR, crn_longitude VARCHAR, crn_nome_foto VARCHAR, crn_data_foto VARCHAR, crn_ativo VARCHAR, crn_upload VARCHAR, crn_conteudo_foto BLOB, crn_caminho_local VARCHAR, crn_caminho_storage VARCHAR, crn_ult_altercao VARCHAR, crn_conteudo_foto_texto VARCHAR)");


            String latitudeInformada = tratarDecimais(markerLat);
            String longitudeInformada = tratarDecimais(markerLng);

            String ambienteImagens = "desenvolvimento/";
            String nomeImagem = UUID.randomUUID() + ".png";
            String path = "imagens/" + ambienteImagens + nomeImagem;
            StorageReference storageRef = storage.getReference(path);


            if (latitudeInformada.isEmpty() || longitudeInformada.isEmpty()){
            Toast.makeText(getContext(), "Necessário informar Lat e Lng!" + latitudeInformada + " / " + longitudeInformada, Toast.LENGTH_SHORT).show();
            }
            else{

                Cursor cursorImagem = bancoDados.rawQuery("SELECT crn_data_foto, crn_conteudo_foto_texto, crn_arv_id FROM CRN_ARV_FOTOS", null);
                int indiceColunaConteudo = cursorImagem.getColumnIndex("crn_conteudo_foto_texto");
                int indiceColunaData = cursorImagem.getColumnIndex("crn_data_foto");
                int indiceColunaID = cursorImagem.getColumnIndex("crn_arv_id");

                cursorImagem.moveToFirst();
                int totalLinhas = cursorImagem.getCount();

                if (totalLinhas == 0 ){
                    Toast.makeText(getContext(), "Sem imagem", Toast.LENGTH_SHORT).show();
                }
                else {
                    bitmap.recycle();

                    String conteudoFoto = cursorImagem.getString(indiceColunaConteudo);
                    byte[] imgByte = conteudoFoto.getBytes();

                    //img.setImageBitmap(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
                    listaImagens.add(imgByte);
                    String dataUpload = cursorImagem.getString(indiceColunaData);
                    Log.e("identificador", cursorImagem.getString(indiceColunaID));
                    UploadTask uploadTask = storageRef.putBytes(imgByte);
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return listaImagens;
    }
    private void RecuperarImagem (){
        try{

            InputStream stream = null;
            SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARV_FOTOS(crn_foto_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_arv_id VARCHAR, crn_latitude VARCHAR, crn_longitude VARCHAR, crn_nome_foto VARCHAR, crn_data_foto VARCHAR, crn_ativo VARCHAR, crn_upload VARCHAR, crn_conteudo_foto BLOB, crn_caminho_local VARCHAR, crn_caminho_storage VARCHAR, crn_ult_altercao VARCHAR, crn_conteudo_foto_texto VARCHAR)");

            String latitudeInformada = latitudeMarcador.getText().toString();
            String longitudeInformada = longitudeMarcador.getText().toString();

            String ambienteImagens = "desenvolvimento/";
            String nomeImagem = UUID.randomUUID() + ".png";
            String path = "imagens/" + ambienteImagens + nomeImagem;
            StorageReference storageRef = storage.getReference(path);


            if (latitudeInformada.isEmpty() || longitudeInformada.isEmpty()){
                Toast.makeText(getContext(), "Necessário informar Lat e Lng!", Toast.LENGTH_SHORT).show();
            }
            else{

                Cursor cursorImagem = bancoDados.rawQuery("SELECT crn_data_foto, crn_conteudo_foto_texto, crn_arv_id FROM CRN_ARV_FOTOS", null);
                int indiceColunaConteudo = cursorImagem.getColumnIndex("crn_conteudo_foto_texto");
                int indiceColunaData = cursorImagem.getColumnIndex("crn_data_foto");
                int indiceColunaID = cursorImagem.getColumnIndex("crn_arv_id");

                cursorImagem.moveToFirst();
                int totalLinhas = cursorImagem.getCount();

                if (totalLinhas == 0 ){
                    Toast.makeText(getContext(), "Sem imagem", Toast.LENGTH_SHORT).show();
                }
                else {
                    bitmap.recycle();

                    String conteudoFoto = cursorImagem.getString(indiceColunaConteudo);
                    byte[] imgByte = conteudoFoto.getBytes();

                    img.setImageBitmap(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
                    String dataUpload = cursorImagem.getString(indiceColunaData);
                    Log.e("identificador", cursorImagem.getString(indiceColunaID));
                    UploadTask uploadTask = storageRef.putBytes(imgByte);
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}