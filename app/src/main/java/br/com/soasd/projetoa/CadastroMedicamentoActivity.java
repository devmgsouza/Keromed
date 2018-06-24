package br.com.soasd.projetoa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.projetoa.model.Medicamento;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.PostMethod;
import httpservices.RetrofitConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import utils.TipoMedicamento;

public class CadastroMedicamentoActivity extends AppCompatActivity {
    @BindView(R.id.toolbarAdd)
            Toolbar toolbar;
    @BindView(R.id.editTextNomeComercial)
            EditText editTextNomeComercial;
    @BindView(R.id.editTextPrincipioAtivo)
            EditText editTextPrincipioAtivo;
    @BindView(R.id.editTextLaboratorio)
            EditText editTextLaboratorio;
    @BindView(R.id.editTextApresentacao)
            EditText editTextApresentacao;
    @BindView(R.id.editTextCodBarras)
            EditText editTextCodeBarras;
    @BindView(R.id.editTextTarja)
            Spinner editTextTarja;
        ArrayAdapter adapter;
    @BindView(R.id.editTextClasse)
            EditText editTextClasse;
    @BindView(R.id.textButtonSalvar)
            TextView textButtonSalvar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medicamento);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastrar medicamento");

        adapter = ArrayAdapter.createFromResource(this, R.array.tarja_medicamento,
                android.R.layout.simple_spinner_dropdown_item);





        editTextTarja.setAdapter(adapter);

        textButtonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("TAG", "onClick()");
                    salvarDados();
                } catch (NullPointerException e){
                    showtAutoDismissDialog("Somente o campo Código de barras tem permissão para ser nulo");
                }
            }
        });

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






    private void showtAutoDismissDialog(String mensagem) {
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
            }
        });

        handler.postDelayed(runnable, 1500);
    }

    private void salvarDados(){
        Medicamento m  = new Medicamento();
        m.setText_nome_comercial(editTextNomeComercial.getText().toString());
        m.setText_principio_ativo(editTextPrincipioAtivo.getText().toString());
        m.setText_apresentacao(editTextApresentacao.getText().toString());
        m.setText_laboratorio(editTextLaboratorio.getText().toString());
        m.setText_classe(editTextClasse.getText().toString());
        m.setText_tarja(editTextTarja.getSelectedItem().toString());
        m.setNr_ean(editTextCodeBarras.getText().toString());

        String gson = new Gson().toJson(m);
        String metodo = "SP_CADASTRAR_MEDICAMENTO";

        new PostMethod(gson, metodo, this, new PostMethod.Callback() {
            @Override
            public void run(String result) {
                if (result.equals("CADASTRO REALIZADO")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CadastroMedicamentoActivity.this)
                            .setTitle(result)
                            .setMessage("Deseja realizar outro cadastro?")
                            .setPositiveButton("SIM", null)
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    dialog.show();
                } else {
                    showtAutoDismissDialog("Não foi possível realizar o cadastro. Verifique os campos obrigatórios");
                }

            }
        }, "Cadastrando...").execute();
    }

}
