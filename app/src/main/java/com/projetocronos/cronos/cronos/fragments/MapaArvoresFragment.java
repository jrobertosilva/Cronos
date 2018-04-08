package com.projetocronos.cronos.cronos.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projetocronos.cronos.cronos.R;
import com.projetocronos.cronos.cronos.helper.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class MapaArvoresFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private FragmentManager fragmentManager;
    private Button botaoLocal;
    private DatabaseReference firebaseReferencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference getArvoresReferencia = firebaseReferencia.child("crn_arvores").child("0001");
    private MapView mapView;
    private LatLngBounds bounds;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private Handler handler = new Handler();
    private Runnable runnableArvores;
    private int validador;

    public MapaArvoresFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_mapa_arvores, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
/*
        */

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        botaoLocal = (Button) view.findViewById(R.id.obterLocal);
        botaoLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPSTracker gps = new GPSTracker(getContext());

                // verifica ele
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // Add a marker in Sydney and move the camera
                    LatLng positionNow = new LatLng(latitude, longitude);

                    SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
                    Cursor cursor = bancoDados.rawQuery("SELECT crn_arv_id, crn_latitude, crn_longitude, crn_nome FROM CRN_ARVORES WHERE crn_latitude = '" + latitude + "' AND crn_longitude = '" + longitude + "'", null);

                    int indiceColunaID = cursor.getColumnIndex("crn_arv_id");

                    cursor.moveToFirst();

                    int totalLinhas = cursor.getCount();
                    if (totalLinhas == 0){
                        if(verificaConexao()){
                            try {
                                Geocoder gc = new Geocoder(getContext(), new Locale("pt", "BR"));
                                List<Address> resultados = gc.getFromLocation(positionNow.latitude, positionNow.longitude, 10);
                                String enderecoTitulo = resultados.get(0).getAddressLine(0);
                                enderecoTitulo = enderecoTitulo.replace(", Brasil", "");
                                mMap.addMarker(new MarkerOptions().position(positionNow).title(enderecoTitulo).draggable(true));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            mMap.addMarker(new MarkerOptions().position(positionNow).title("Posição Atual").draggable(true));
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(positionNow));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                    else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(positionNow));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                    }
                }
                else{
                    Bundle params = new Bundle();

                    params.putString("title", "");
                    params.putDouble("latitude", 0.0);
                    params.putDouble("longitude",0.0);

                    Fragment myFrag = new Marcador();
                    myFrag.setArguments(params);

                    showFragment(myFrag, "Dados Marcador");

                }
            }
        });

        return view;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            fragmentManager = getFragmentManager();
            //OBTEM DADOS DE MARCADORES E OS CRIA NO MAPA
            popularMapa(googleMap);
            validador = 0;
            runnableArvores = new Runnable() {
                @Override
                public void run() {
                    if (verificaConexao()){
                        getArvoresFirebase();
                        if (validador == 0){
                            validador = 1;
                        } else {
                            popularMapa(mMap);
                        }
                        handler.postDelayed(runnableArvores, 600000);
                    }
                }
            };

            handler.post(runnableArvores);


            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(verificaConexao()){
                        try {
                            Geocoder gc = new Geocoder(getContext(), new Locale("pt", "BR"));
                            List<Address> resultados = gc.getFromLocation(latLng.latitude, latLng.longitude, 10);
                            String enderecoTitulo = resultados.get(0).getAddressLine(0);
                            enderecoTitulo = enderecoTitulo.replace(", Brasil", "");
                            mMap.addMarker(new MarkerOptions().position(latLng).title(enderecoTitulo));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Novo Marcador"));
                    }
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Bundle params = new Bundle();
                    String titulo = marker.getTitle();
                    double lat = marker.getPosition().latitude;
                    double lng =  marker.getPosition().longitude;

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("crn_latitude", tratarDecimais(lat));
                    contentValues.put("crn_longitude", tratarDecimais(lng));
                    contentValues.put("crn_nome", titulo);

                    salvarDadosTemporarios(contentValues, 1, "CRN_ARVORES_TMP", null, null);


                    params.putString("title",titulo);
                    params.putDouble("latitude", lat);
                    params.putDouble("longitude", lng);

                    Fragment myFrag = new Marcador();
                    myFrag.setArguments(params);

                    showFragment(myFrag, "Dados Marcador");

                    return false;

                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Log.e("erro_mapa", e.getMessage());
        }
    }

    public void getArvoresFirebase(){
        new Thread(){
            public void run() {
                try {
                    getArvoresReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);

                            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARVORES (crn_arv_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_latitude VARCHAR, crn_longitude VARCHAR, " +
                                    "crn_nome VARCHAR, crn_especie_id VARCHAR, crn_plantio_id VARCHAR, crn_largura_id VARCHAR, crn_inclinacao_id VARCHAR, crn_fiacao_id VARCHAR, crn_sistema_id VARCHAR," +
                                    "crn_intervencao_id VARCHAR, crn_poda_id VARCHAR, crn_qualidade_id VARCHAR, crn_area_id VARCHAR, crn_idade_arvore VARCHAR, crn_diametro_arvore VARCHAR, " +
                                    "crn_altura_arvore VARCHAR, crn_altura_ramo VARCHAR, crn_altura_fiacao VARCHAR, crn_altura_copa VARCHAR, crn_doenca_praga VARCHAR, crn_obs_arvore VARCHAR, " +
                                    "crn_ativo VARCHAR, crn_ult_atualizacao VARCHAR, crn_motivo_inativo VARCHAR, crn_usuario_id VARCHAR)");
                            bancoDados.execSQL("DELETE FROM CRN_ARVORES");
                            for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                                try {
                                    if (objSnapshot != null) {
                                            String latitudeFirebase = objSnapshot.child("crn_latitude").getValue().toString();
                                            String longitudeFirebase = objSnapshot.child("crn_longitude").getValue().toString();
                                            String nomeFirebase = objSnapshot.child("crn_nome").getValue().toString();
                                            String idFirebase = objSnapshot.child("crn_arv_id").getValue().toString();
                                            String especieFirebase = objSnapshot.child("crn_especie_id").getValue().toString();
                                            String fiacaoFirebase = objSnapshot.child("crn_fiacao_id").getValue().toString();
                                            String plantioFirebase = objSnapshot.child("crn_plantio_id").getValue().toString();
                                            String larguraFirebase = objSnapshot.child("crn_largura_id").getValue().toString();
                                            String inclinacaoFirebase = objSnapshot.child("crn_inclinacao_id").getValue().toString();
                                            String radicularFirebase = objSnapshot.child("crn_sistema_id").getValue().toString();
                                            String intervencaoFirebase = objSnapshot.child("crn_intervencao_id").getValue().toString();
                                            String podaFirebase = objSnapshot.child("crn_poda_id").getValue().toString();
                                            String qualidadeFirebase = objSnapshot.child("crn_qualidade_id").getValue().toString();
                                            String areaFirebase = objSnapshot.child("crn_area_id").getValue().toString();
                                            String idadeFirebase = objSnapshot.child("crn_idade_arvore").getValue().toString();
                                            String diametroFirebase = objSnapshot.child("crn_diametro_arvore").getValue().toString();
                                            String alturaFirebase = objSnapshot.child("crn_altura_arvore").getValue().toString();
                                            String ramoFirebase = objSnapshot.child("crn_altura_ramo").getValue().toString();
                                            String alturaFiacaoFirebase = objSnapshot.child("crn_altura_fiacao").getValue().toString();
                                            String copaFirebase = objSnapshot.child("crn_altura_copa").getValue().toString();
                                            String doencaFirebase = objSnapshot.child("crn_doenca_praga").getValue().toString();
                                            String obsFirebase = objSnapshot.child("crn_obs_arvore").getValue().toString();
                                            String ativoFirebase = objSnapshot.child("crn_ativo").getValue().toString();
                                            String atualizacaoFirebase = objSnapshot.child("crn_ult_atualizacao").getValue().toString();
                                            String motivoFirebase = objSnapshot.child("crn_motivo_inativo").getValue().toString();
                                            String usuarioFirebase = objSnapshot.child("crn_usuario_id").getValue().toString();

                                            //CRIA CURSOR PARA IDENTIFICAR SE O MARCADOR JA EXISTE NA BASE
                                        Cursor cursor = null;
                                    if (latitudeFirebase.isEmpty() && longitudeFirebase.isEmpty()){
                                        }
                                    else {
                                        cursor = bancoDados.rawQuery("SELECT crn_arv_id FROM CRN_ARVORES WHERE crn_latitude = '" + latitudeFirebase + "' AND crn_longitude = '" + longitudeFirebase + "'", null);
                                        }
                                    if (cursor != null) {
                                        //OBTEM O INDEX DA COLUNA DA ARVORE NA BASE DE DADOS
                                    int indiceColunaID = cursor.getColumnIndex("crn_arv_id");
                                        //VOLTA O CURSOR PARA O PRIMEIRO REGISTRO PARA REALIZAR O LOOP COMPLETO DOS DADOS
                                        cursor.moveToFirst();
                                        //OBTEM O TOTAL DE LINHAS DO CURSOR PARA COMPARACAO COM O CONTADOR DESSA FORMA REALIZANDO O LOOP CORRETO
                                    int totalLinhas = cursor.getCount();
                                        //SENAO HOUVER REGISTROS NA BASE SERA REALIZADO INSERT
                                    if (totalLinhas == 0) {
                                        bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id , crn_latitude, crn_longitude,crn_nome, crn_especie_id, " +
                                                "crn_plantio_id,crn_largura_id, crn_inclinacao_id,crn_fiacao_id, crn_sistema_id," +
                                                "crn_intervencao_id,crn_poda_id,crn_qualidade_id,crn_area_id,crn_idade_arvore," +
                                                "crn_diametro_arvore,crn_altura_arvore,crn_altura_ramo,crn_altura_fiacao,crn_altura_copa," +
                                                "crn_doenca_praga,crn_obs_arvore, crn_ativo,crn_ult_atualizacao,crn_motivo_inativo,crn_usuario_id) VALUES ('"
                                                + idFirebase + "','" + latitudeFirebase + "','" + longitudeFirebase + "','" + nomeFirebase + "' , '" + especieFirebase+ "' , '"
                                                + plantioFirebase + "','" + larguraFirebase + "','" + inclinacaoFirebase + "','" + fiacaoFirebase + "','" + radicularFirebase + "','"
                                                + intervencaoFirebase + "','" + podaFirebase + "','" + qualidadeFirebase + "','" + areaFirebase + "','" + idadeFirebase + "','"
                                                + diametroFirebase + "','" + alturaFirebase + "','" + ramoFirebase + "','" + alturaFiacaoFirebase + "','" + copaFirebase + "','"
                                                + doencaFirebase + "','" + obsFirebase + "','" + ativoFirebase + "','" + atualizacaoFirebase + "','" + motivoFirebase + "','" + usuarioFirebase + "')");
                                    } else {
                                        int codigoLite = cursor.getInt(indiceColunaID);
                                        bancoDados.execSQL("UPDATE CRN_ARVORES SET crn_nome = '" + nomeFirebase + "', crn_especie_id = '" + especieFirebase
                                                + "', crn_fiacao_id = '" + fiacaoFirebase + "', crn_plantio_id = '" + plantioFirebase + "', crn_largura_id = '" + larguraFirebase
                                                + "', crn_inclinacao_id = '" + inclinacaoFirebase + "', crn_sistema_id ='" + radicularFirebase + "', crn_intervencao_id = '" + intervencaoFirebase
                                                + "', crn_poda_id = '" + podaFirebase + "', crn_qualidade_id= '" + qualidadeFirebase + "', crn_area_id= '" + areaFirebase
                                                + "', crn_idade_arvore= '" + idadeFirebase + "', crn_diametro_arvore ='" + diametroFirebase + "',crn_altura_arvore= '" + alturaFirebase
                                                + "', crn_altura_ramo= '" + ramoFirebase + "', crn_altura_fiacao='" + alturaFiacaoFirebase + "', crn_altura_copa='" + copaFirebase
                                                + "', crn_doenca_praga ='" + doencaFirebase + "', crn_obs_arvore='" + obsFirebase + "',crn_ativo='" + ativoFirebase
                                                + "', crn_ult_atualizacao='" + atualizacaoFirebase + "', crn_motivo_inativo='" + motivoFirebase + "', crn_usuario_id='" + usuarioFirebase
                                                + "' WHERE crn_arv_id = '" + codigoLite + "'");
                                    }
                                    cursor.close();
                                }
                                else{
                                    bancoDados.execSQL("INSERT INTO CRN_ARVORES (crn_arv_id , crn_latitude, crn_longitude,crn_nome, crn_especie_id, " +
                                            "crn_plantio_id,crn_largura_id, crn_inclinacao_id,crn_fiacao_id, crn_sistema_id," +
                                            "crn_intervencao_id,crn_poda_id,crn_qualidade_id,crn_area_id,crn_idade_arvore," +
                                            "crn_diametro_arvore,crn_altura_arvore,crn_altura_ramo,crn_altura_fiacao,crn_altura_copa," +
                                            "crn_doenca_praga,crn_obs_arvore, crn_ativo,crn_ult_atualizacao,crn_motivo_inativo,crn_usuario_id) VALUES ('"
                                            + idFirebase + "','" + latitudeFirebase + "','" + longitudeFirebase + "','" + nomeFirebase + "' , '" + especieFirebase+ "' , '"
                                            + plantioFirebase + "','" + larguraFirebase + "','" + inclinacaoFirebase + "','" + fiacaoFirebase + "','" + radicularFirebase + "','"
                                            + intervencaoFirebase + "','" + podaFirebase + "','" + qualidadeFirebase + "','" + areaFirebase + "','" + idadeFirebase + "','"
                                            + diametroFirebase + "','" + alturaFirebase + "','" + ramoFirebase + "','" + alturaFiacaoFirebase + "','" + copaFirebase + "','"
                                            + doencaFirebase + "','" + obsFirebase + "','" + ativoFirebase + "','" + atualizacaoFirebase + "','" + motivoFirebase + "','" + usuarioFirebase + "')");
                                }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("erro_get_arvore", e.getMessage());
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            }.start();
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
    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(getContext(), "Coordenadas: " + latLng.toString(), Toast.LENGTH_LONG).show();
    }


    private void showFragment(Fragment fragment, String name){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();



        fragmentTransaction.replace(R.id.frame_container, fragment, name);

        fragmentTransaction.commit();

    }

    public void salvarDadosTemporarios (ContentValues contentValues, int operacao, String tabela, String whereClause, String[] whereArgs){
        final SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", MODE_PRIVATE, null);
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARVORES_TMP (crn_arv_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_latitude VARCHAR, crn_longitude VARCHAR, " +
                "crn_nome VARCHAR, crn_especie_id VARCHAR, crn_plantio_id VARCHAR, crn_largura_id VARCHAR, crn_inclinacao_id VARCHAR, crn_fiacao_id VARCHAR, crn_sistema_id VARCHAR," +
                "crn_intervencao_id VARCHAR, crn_poda_id VARCHAR, crn_qualidade_id VARCHAR, crn_area_id VARCHAR, crn_idade_arvore VARCHAR, crn_diametro_arvore VARCHAR, " +
                "crn_altura_arvore VARCHAR, crn_altura_ramo VARCHAR, crn_altura_fiacao VARCHAR, crn_altura_copa VARCHAR, crn_doenca_praga VARCHAR, crn_obs_arvore VARCHAR, " +
                "crn_ativo VARCHAR, crn_ult_atualizacao VARCHAR, crn_motivo_inativo VARCHAR, crn_usuario_id VARCHAR)");
        if (contentValues == null || tabela.isEmpty() || operacao < 1 ) {
            Toast.makeText(getContext(), "Parâmetros não informados! ", Toast.LENGTH_SHORT).show();
        }else{
            if (operacao == 1) {
                bancoDados.insert(tabela,null,contentValues);
            } else if(operacao == 2){
                bancoDados.update(tabela,contentValues,whereClause,whereArgs);
            } else {
                Toast.makeText(getContext(), "Operação de banco de dados não reconhecida", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onStop(){
        super.onStop();
        handler.removeCallbacks(runnableArvores);
    }

    public void popularMapa(GoogleMap googleMap){
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        final SQLiteDatabase bancoDados = getContext().openOrCreateDatabase("app", getContext().MODE_PRIVATE, null);
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CRN_ARVORES (crn_arv_id INTEGER PRIMARY KEY AUTOINCREMENT, crn_latitude VARCHAR, crn_longitude VARCHAR, " +
                "crn_nome VARCHAR, crn_especie_id VARCHAR, crn_plantio_id VARCHAR, crn_largura_id VARCHAR, crn_inclinacao_id VARCHAR, crn_fiacao_id VARCHAR, crn_sistema_id VARCHAR," +
                "crn_intervencao_id VARCHAR, crn_poda_id VARCHAR, crn_qualidade_id VARCHAR, crn_area_id VARCHAR, crn_idade_arvore VARCHAR, crn_diametro_arvore VARCHAR, " +
                "crn_altura_arvore VARCHAR, crn_altura_ramo VARCHAR, crn_altura_fiacao VARCHAR, crn_altura_copa VARCHAR, crn_doenca_praga VARCHAR, crn_obs_arvore VARCHAR, " +
                "crn_ativo VARCHAR, crn_ult_atualizacao VARCHAR, crn_motivo_inativo VARCHAR, crn_usuario_id VARCHAR)");
        Cursor cursor = bancoDados.rawQuery("SELECT crn_latitude, crn_longitude, crn_nome FROM CRN_ARVORES", null);

        int indiceColunaLatitude = cursor.getColumnIndex("crn_latitude");
        int indiceColunaLongitude = cursor.getColumnIndex("crn_longitude");
        int indiceColunaNome = cursor.getColumnIndex("crn_nome");

        cursor.moveToFirst();
        int totalLinhas = cursor.getCount();
        if (totalLinhas == 0) {
        }
        int contador = 0;

        while (contador < totalLinhas) {

            String campoLatitude = cursor.getString(indiceColunaLatitude);
            String campoLongitude = cursor.getString(indiceColunaLongitude);
            String campoNome = cursor.getString(indiceColunaNome);
            double posicaoLatitude = Double.parseDouble(campoLatitude);
            double posicaoLongitude = Double.parseDouble(campoLongitude);

            LatLng newMaker = new LatLng(posicaoLatitude, posicaoLongitude);
            mMap.addMarker(new MarkerOptions().position(newMaker).title(campoNome).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                /*if (bounds == null){
                    bounds =  new LatLngBounds(newMaker, newMaker);
                }
                else {
                    bounds = bounds.including(newMaker);
                }*/
            builder.include(newMaker);
            cursor.moveToNext();
            contador = contador + 1;
        }
        bounds = builder.build();
        LatLngBounds bounds = builder.build();
        CameraUpdate camera = null;
        if (bounds == null) {
            LatLng positionNow = null;
            GPSTracker gps = new GPSTracker(getContext());
            // VERIFICA SE OBTEVE A POSICAO ATUAL
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                // OBTEM POSICAO ATUAL E CRIA VARIAVEL
                positionNow = new LatLng(latitude, longitude);
            }
            else {
                positionNow = new LatLng(-25.509235, -49.289596);
            }
            camera = CameraUpdateFactory.newLatLng(positionNow);
        }
        else {
            camera = CameraUpdateFactory.newLatLngBounds(bounds, 15);
        }

        mMap.moveCamera(camera);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        cursor.close();

    }
}
