package br.com.soasd.projetoa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.projetoa.model.UsuarioIndependente;
import br.com.sapereaude.maskedEditText.MaskedEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.BuscarCEP;
import httpservices.PostMethod;
import service.LocalDatabase;

public class CadUserActivity extends AppCompatActivity {
    @BindView(R.id.toolbarAdd)
    Toolbar toolbar;
    @BindView(R.id.editTextNomeCompleto)
    EditText editTextNomeCompleto;
    @BindView(R.id.editTextNascimento)
    MaskedEditText editTextNascimento;
    @BindView(R.id.editTextEndereco)
    EditText editTextEndereco;
    @BindView(R.id.editTextCidade)
    EditText editTextCidade;
    @BindView(R.id.editTextCPF)
    MaskedEditText editTextCPF;
    @BindView(R.id.editTextRG)
    MaskedEditText editTextRG;
    @BindView(R.id.editTextCep)
    MaskedEditText editTextCep;
    @BindView(R.id.editTextTelefone)
    MaskedEditText editTextTelefone;
    @BindView(R.id.imageButtonNasc)
    ImageButton imageButtonNasc;
    @BindView(R.id.editTextIdade)
    EditText editTextIdade;
    @BindView(R.id.spinnerUF)
    Spinner spinnerUF;
    ArrayAdapter spinnerAdapter;
    private Calendar myCalendar = Calendar.getInstance();
    String myFormat = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caduser);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastrar usuário");


        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.uf_brasil,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerUF.setAdapter(spinnerAdapter);

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



    public UsuarioIndependente cadastrarUsuario(){
        UsuarioIndependente u = new UsuarioIndependente();
            u.setNomeCompleto(editTextNomeCompleto.getText().toString());
            u.setTelefone(editTextTelefone.getRawText());
            u.setCidade(editTextCidade.getText().toString());
            u.setCpf(editTextCPF.getRawText());
            u.setRg(editTextRG.getRawText());
            u.setUf(spinnerUF.getSelectedItem().toString());
            u.setEndereco(editTextEndereco.getText().toString());
            u.setDataNascimento(editTextNascimento.getText().toString());
            return u;
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

    private void dialogYesNo(String retorno){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(retorno)
                .setMessage("Deseja cadastrar outro usuário?")
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        limparCampos();
                    }
                });
        dialog.show();
    }

    private void limparCampos(){
        editTextNomeCompleto.setText("");
        editTextCidade.setText("");
        editTextEndereco.setText("");
        editTextCPF.setText("");
        editTextNascimento.setText("");
        editTextRG.setText("");
        editTextTelefone.setText("");
    }


    private boolean checkData(String s){
        boolean b = false;
        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
        df.setLenient (false);
        try {
            df.parse (s);
            b = true;
        } catch (ParseException ex) {
            showtAutoDismissDialog("Insira uma data válida");
        }
        return b;
    }

    public void alterarDataNascimento(View view) {

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

                editTextNascimento.setText(sdf.format(myCalendar.getTime()).replace("/", ""));


            }
        };

        DatePickerDialog dpd =  new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();

    }

    public void cadastrarUsuario(View view){
        UsuarioIndependente u = cadastrarUsuario();
        u.setTelefone(editTextTelefone.getRawText());
        u.setNomeCompleto(editTextNomeCompleto.getText().toString());
        u.setCidade(editTextCidade.getText().toString());
        u.setEndereco(editTextEndereco.getText().toString());
        u.setCpf(editTextCPF.getRawText());
        u.setRg(editTextRG.getRawText());
        u.setDataNascimento(editTextNascimento.getText().toString());
        u.setUf(spinnerUF.getSelectedItem().toString());
        u.setIdade(editTextIdade.getText().toString());


            if (u.getEndereco() == null
                    || u.getNomeCompleto() == null
                    || u.getTelefone() == null
                    || u.getIdade() == null) {
                showtAutoDismissDialog("Necessário preencher os seguintes campos: \n" +
                        "- Nome completo\n" +
                        "- Idade\n" +
                        "- Endereço\n" +
                        "- CPF\n" +
                        "- Telefone");

            } else {

                String gson = new Gson().toJson(u);
                String fbid = new LocalDatabase(this).buscarIDUsuario();
                String metodo = "SP_CADASTRAR_USUARIOIN?id=" + fbid;

                new PostMethod(gson, metodo, CadUserActivity.this, new PostMethod.Callback() {
                    @Override
                    public void run(String result) {
                        if (!result.equals(null)) {

                            if (result.equals("CADASTRO REALIZADO")) {
                                dialogYesNo(result);
                            } else {
                                showtAutoDismissDialog(result);
                            }
                        } else {
                            showtAutoDismissDialog("Não foi possível realizar o cadastro. Tente novamente em " +
                                    "alguns instantes");
                        }
                    }
                }, "Cadastrando...").execute();


            }

    }

    public void pesquisarCEP(View view){
        String cep = editTextCep.getText().toString();
        new BuscarCEP(cep, CadUserActivity.this, new BuscarCEP.Callback() {
            @Override
            public void run(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    editTextEndereco.setText(jsonObject.getString("logradouro"));
                    editTextCidade.setText(jsonObject.getString("localidade"));
                    int adapterPosition = spinnerAdapter.getPosition(jsonObject.getString("uf"));
                    spinnerUF.setSelection(adapterPosition);
                    //DISMISS KEYBOARD AFTER SEARCH
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextCep.getWindowToken(), 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }
}
