package br.com.soasd.projetoa;

import android.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.RegistrarSolicitacao;
import br.com.projetoa.model.UsuarioIndependente;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.GetMethod;
import httpservices.PostMethod;
import service.LocalDatabase;

public class DoarActivity extends AppCompatActivity {
    @BindView(R.id.textViewMedicamentoDoar)
    TextView textViewMedicamentoDoar;
    @BindView(R.id.spinnerUsuarioDoar)
    Spinner spinnerUsuarioDoar;
    @BindView(R.id.editTextQTD)
    EditText editTextQTD;
    List<UsuarioIndependente> lista;
    ArrayAdapter spinnerAdapter;
    MedicamentosDisponiveis medicamento;
    private static final int REQUEST_PERMISSIONS_CODE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doar);
        ButterKnife.bind(this);
        buscarUsuarios();
        String medicamento_gson = getIntent().getExtras().getString("MEDICAMENTO_GSON");
        medicamento = new Gson().fromJson(medicamento_gson, MedicamentosDisponiveis.class);
        carregarDados();
    }


    private void buscarUsuarios(){
        String id = new LocalDatabase(this).buscarIDUsuario();
        new GetMethod("SP_BUSCAR_USUARIOIN?id=" + id, DoarActivity.this, new GetMethod.Callback() {
            @Override
            public void run(String result) {
                Log.i("TAG", result);
                Type listType = new TypeToken<ArrayList<UsuarioIndependente>>(){}.getType();
                lista = new Gson().fromJson(result, listType);
                if (lista != null) {
                    if (lista.size() > 0) {
                        List<String> usuarios = new ArrayList<>();
                        for (int i = 0; i < lista.size(); i++){
                            usuarios.add(lista.get(i).getNomeCompleto());
                        }

                        spinnerAdapter = new ArrayAdapter(DoarActivity.this, android.R.layout.simple_dropdown_item_1line, usuarios);
                        spinnerUsuarioDoar.setAdapter(spinnerAdapter);
                    }

                }

            }
        }).execute();
    }

    private void carregarDados(){
        String text = medicamento.getText_nome_comercial();
        text += "\n quantidade: " + medicamento.getQtd_disponivel();
        text += "\n Tipo: " + medicamento.getTipo_medicamento();
        textViewMedicamentoDoar.setText(text);
    }

    public void sair(View view){
        finish();
    }

    public void doarMedicamento(View view) {
        if (editTextQTD.getText().toString() == null || editTextQTD.getText().toString().equals("")) {
            showtAutoDismissDialog("Insira a quantidade", 0);
        } else {
            RegistrarSolicitacao r = new RegistrarSolicitacao();
            r.setQtd_solicitado(Integer.parseInt(editTextQTD.getText().toString()));
            if (!(r.getQtd_solicitado() <= 0)) {

                r.setTokenfb_doador(new LocalDatabase(this).buscarIDUsuario());
                r.setLongitude(0); //TODO SOLICITAR LOCALIZAÇÃO
                r.setLongitude(0);
                r.setFk_medicamento_solicitado(medicamento.getPk_mmd());

                int position = spinnerUsuarioDoar.getSelectedItemPosition();
                int pk_usuarioin = lista.get(position).getPk_usuarioin();
                r.setFk_usuarioin(pk_usuarioin);
                new PostMethod(new Gson().toJson(r), "SP_REGISTRAR_DOACAO", DoarActivity.this, new PostMethod.Callback() {
                    @Override
                    public void run(String result) {
                        showtAutoDismissDialog(result, 1);
                    }
                }, "Registrando doação...").execute();
            } else {
                showtAutoDismissDialog("Insira uma quantidade válida", 0);
            }
        }

    }


    private void showtAutoDismissDialog(String mensagem, final int value) {
        TextView msg = new TextView(this);
        msg.setText("\n" + mensagem + "\n");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.projetoa_ico)
                .setTitle(R.string.app_name)

                .setView(msg);

        final AlertDialog alert = dialog.create();
        alert.show();


        final Handler handler = new Handler();
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
                if (value == 1){
                    setResult(100);
                    finish();
                }
            }
        });

        handler.postDelayed(runnable, 1500);
    }


}
