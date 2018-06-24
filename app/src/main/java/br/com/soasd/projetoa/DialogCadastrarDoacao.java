package br.com.soasd.projetoa;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import br.com.projetoa.model.DisponibilizarMedicamento;
import br.com.projetoa.model.Medicamento;
import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.Usuario;
import br.com.projetoa.model.exception.DisponibilizarMedicamentoException;
import br.com.projetoa.model.exception.MedicamentoException;
import br.com.sapereaude.maskedEditText.MaskedEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.GetMethod;
import httpservices.GetSingleMethod;
import httpservices.PrivateGetMethod;
import service.ExternalDatabase;
import service.LocalDatabase;
import service.SettingsModel;
import utils.TipoMedicamento;

public class DialogCadastrarDoacao extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
GoogleApiClient.ConnectionCallbacks{
    private Calendar myCalendar = Calendar.getInstance();
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    @BindView(R.id.textViewNomeComercial)
    TextView textViewNomeComercial;
    @BindView(R.id.textViewApresentacao)
    TextView textViewApresentacao;
    @BindView(R.id.textViewPrincipioAtivo)
    TextView textViewPrincipioAtivo;
    @BindView(R.id.textViewLaboratorio)
    TextView textViewLaboratorio;
    @BindView(R.id.textViewNrEan)
    TextView textViewNrEan;
    @BindView(R.id.textViewClasseTerapeutica)
    TextView textViewClasseTerapeutica;
    @BindView(R.id.editTextOrientarDoar)
    EditText editTextOrientarDoar;
    @BindView(R.id.spinnerTipos)
    Spinner spinnerTipo;
    @BindView(R.id.toolbarAdd)
    Toolbar toolbar;
    @BindView(R.id.editTextDataValidade)
    MaskedEditText editTextDataValidade;
    @BindView(R.id.editTextQuantidade)
    EditText editTextQuantidade;

    GoogleApiClient mGoogleApiClient;
    DisponibilizarMedicamento m;

    Medicamento medicamento = null;
    AutoCompleteTextView searchView  = null;
    ArrayAdapter adapter;
    private List<Medicamento> listaMedicamento = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_cadastrar_doacao);
        ButterKnife.bind(this);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, TipoMedicamento.values());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        m = new DisponibilizarMedicamento();
        m.setQtd_disponivel(0);
        getSupportActionBar().setTitle("Pesquisar");
        spinnerTipo.setAdapter(adapter);
        startPlayServices();




        editTextOrientarDoar.setText(carregarOrientacoes());

        editTextDataValidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataValidade();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){

            String contents = data.getStringExtra("SCAN_RESULT");

            buscarMedicamentoCodBarras(contents );


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = this.getMenuInflater();

        menuInflater.inflate(R.menu.autocomplete_view, menu);
        final MenuItem searchItem = menu.findItem(R.id.searchView_button);
        final MenuItem addItem = menu.findItem(R.id.addView_button);

        AppCompatImageButton buttonView = null;


        if (searchItem != null) {
            searchView = (AutoCompleteTextView)searchItem.getActionView();
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setHint("Pesquisar medicamento");

            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                    String text = s.toString();
                    text = text.split(",")[0];
                    buscarMedicamentoContextual(text);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    buscarMedicamentoCodBarras(listaMedicamento.get(position).getNr_ean());
                }
            });

            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchView.setText("");
                }
            });
        }


        if (addItem != null) {
            buttonView = (AppCompatImageButton) addItem.getActionView();
            buttonView.setImageResource(R.drawable.ic_barcode_scan);
            buttonView.setAdjustViewBounds(true);
            buttonView.setBackgroundColor(Color.TRANSPARENT);
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    solicitaPermission();


                }
            });
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void disponibilizarMedicamento(View view){
    try {
        m.setQtd_disponivel(Integer.valueOf(editTextQuantidade.getText().toString()));
    } catch (NumberFormatException e) {
        m.setQtd_disponivel(0);
    }


        if (medicamento != null){
            if (m.getQtd_disponivel() > 0) {
                if (!m.getData_validade().equals(null)) {

                    atualizarLocalizacao();
                    TipoMedicamento t = (TipoMedicamento)spinnerTipo.getSelectedItem();
                    m.setFb_id(new LocalDatabase(this).buscarIDUsuario());
                    //m.setFb_id(AccessToken.getCurrentAccessToken().getUserId());
                    m.setData_validade(editTextDataValidade.getText().toString());
                    m.setTipo_medicamento(t.getValor());
                    m.setNr_valor_unit(0);
                    m.setFk_medicamento(medicamento.getPk_medicamento());
                    m.setText_observacao(editTextOrientarDoar.getText().toString());

                    try {
                        new ExternalDatabase(this).SP_DISPONIBILIZAR_MEDICAMENTO(m);
                        limparTela();
                    } catch (DisponibilizarMedicamentoException e) {
                        e.printStackTrace();
                    }
                } else {
                    showtAutoDismissDialog("Por favor, preencha a data de validade");
                }
            } else {
                showtAutoDismissDialog("Por favor, preencha a quantidade a disponibilizar");
            }
        } else {
            showtAutoDismissDialog("Insira um medicamento antes de disponibilizar");
        }

    }

    public void sair(View view){
        finish();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            createDialogYes(R.string.erro, R.string.erro_camera, R.string.continuar);
            return false;
        }
    }

    private void solicitaPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                createDialogYes(R.string.importante, R.string.permissao_necessaria, R.string.yes);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);

                solicitaPermission();
            }


        } else {

                ativarLeitor();


        }

    }

    private void ativarLeitor(){
            if (checkCameraHardware(this)) {
                IntentIntegrator i = new IntentIntegrator(this);
                i.setPrompt("Posicione o código de barras para fazer a leitura: ");
                i.initiateScan();
            }
    }

    private void createDialogYes(int title, int message, int YES){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DialogCadastrarDoacao.this, new String[]{android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                        }
                    }).show();
        }

    private void buscarMedicamentoCodBarras(String barcode){
        String parametro = "SP_BUSCAR_MEDICAMENTO_CODBARRAS?barcode=" + barcode;

            new GetMethod(parametro, this, new GetMethod.Callback() {
                @Override
                public void run(String result) {
                    medicamento = new Gson().fromJson(result, Medicamento.class);
                    if (medicamento != null) {

                        textViewNomeComercial.setText(medicamento.getText_nome_comercial());
                        textViewApresentacao.setText(medicamento.getText_apresentacao());
                        textViewPrincipioAtivo.setText(medicamento.getText_principio_ativo());
                        textViewLaboratorio.setText(medicamento.getText_laboratorio());
                        textViewNrEan.setText(medicamento.getNr_ean());
                        textViewClasseTerapeutica.setText(medicamento.getText_classe());
                        editTextQuantidade.setEnabled(true);
                        editTextDataValidade.setEnabled(true);
                    } else {
                        showtAutoDismissDialog("Medicamento não encontrado");

                    }
                }
            }).execute();

    }

    private void convertToArrayAdapter(List<Medicamento> listaMedicamentos) {
        String[] suggestions;
        if (!ContainsAllNulls(listaMedicamentos)) {
            ArrayAdapter<String> adapter = null;
            suggestions = new String[listaMedicamentos.size()];
            ArrayList<String> listString = new ArrayList<>();
            for (int i = 0; i < listaMedicamentos.size(); i++) {
                listString.add(listaMedicamentos.get(i).getText_nome_comercial() +
                        ", " + listaMedicamentos.get(i).getText_apresentacao());

            }
            suggestions = listString.toArray(suggestions);

            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, suggestions);
            adapter.notifyDataSetChanged();
            searchView.setAdapter(adapter);

        }

    }

    private static Boolean ContainsAllNulls(List arrList) {
        if (arrList != null) {
            for (Object a : arrList)
                if (a != null) return false;
        }

        return true;
    }

    private void buscarMedicamentoContextual(String s){
        String parametro = "SP_BUSCAR_MEDICAMENTO_CONTEXTUAL?contexto=" + s;

            new GetSingleMethod(parametro, this, new GetSingleMethod.Callback() {
                @Override
                public void run(String result) {

                    Type listType = new TypeToken<ArrayList<Medicamento>>() {
                    }.getType();
                    listaMedicamento = new Gson().fromJson(result, listType);
                    convertToArrayAdapter(listaMedicamento);
                }
            }).execute();

    }

    private void dataValidade(){

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
                DateFormat df = new SimpleDateFormat(myFormat);

                editTextDataValidade.setText(sdf.format(myCalendar.getTime()).replace("/",""));

            }
        };

        DatePickerDialog dataPicckerDialog =  new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        dataPicckerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dataPicckerDialog.show();
    }

    private void startPlayServices(){
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void atualizarLocalizacao(){
    if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            createDialogYes(R.string.importante, R.string.permissao_necessaria, R.string.yes);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_CODE);

            atualizarLocalizacao();
        }
    } else {
         Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
         if (location != null) {
             m.setLatitude(location.getLatitude());
             m.setLongitude(location.getLongitude());
         }
    }

    }


    private void showtAutoDismissDialog(String mensagem){
        TextView msg = new TextView(this);
        msg.setText("\n" + mensagem + "\n");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.projetoa_ico)
                .setTitle(R.string.app_name)

                .setView(msg);

        final AlertDialog alert = dialog.create();
        alert.show();




        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1500);
    }


    private void limparTela(){
        editTextDataValidade.setText("");
        editTextQuantidade.setText("");
        textViewLaboratorio.setText("");
        textViewPrincipioAtivo.setText("");
        textViewApresentacao.setText("");
        textViewNomeComercial.setText("");
        textViewClasseTerapeutica.setText("");
        textViewNrEan.setText("");
        searchView.setText("");
    }


    private String carregarOrientacoes(){
        SettingsModel m = new LocalDatabase(this).loadSettings();
        Usuario u = new Gson().fromJson(new LocalDatabase(this).buscarLogin(), Usuario.class);
        String retorno = "Retirar no endereço: "  + u.getText_nm_rua();
        retorno+= ", " + u.getText_nr_residencia() + ", " + u.getText_cidade() + "\n";
        retorno+= " Nos dias: ";
        if (m.getDom() == 1) {
            retorno += "Dom, ";
        }
        if (m.getSeg() == 1){
            retorno += "Seg, ";
        }
        if (m.getTer() == 1) {
            retorno += "Ter, ";
        }
        if (m.getQua() == 1) {
            retorno += "Qua, ";
        }
        if (m.getQui() == 1){
            retorno += "Qui, ";
        }
        if (m.getSex() == 1){
            retorno += "Sex, ";
        }
        if (m.getSab() == 1){
            retorno += "Sab, ";
        }

        retorno += "entre os horarios: " + m.getHorario_inicio() + " e " + m.getHorario_final();

                return retorno;
    }
}
