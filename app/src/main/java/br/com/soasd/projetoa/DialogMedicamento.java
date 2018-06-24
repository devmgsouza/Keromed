package br.com.soasd.projetoa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.RegistrarSolicitacao;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.PostMethod;
import service.LocalDatabase;

public class DialogMedicamento extends AppCompatActivity {

    private TextView textNomeComercial;
    private TextView textApresentacao;
    private TextView textDataValidade;
    private TextView textLaboratorio;
    private TextView textQuantidadeDisponivel;
    private TextView textPrincipioAtivo;
    private Button buttonSolicitar, buttonAlterarQtd, buttonAlterarValidade;
    private MedicamentosDisponiveis medicamento;
    private Calendar myCalendar = Calendar.getInstance();
    private int tipo_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.soasd.projetoa.R.layout.activity_dialog_medicamento);
        carregarViews();
        Bundle bundle = getIntent().getExtras();
        String classe = bundle.getString("medicamento");
        tipo_load = bundle.getInt("tipo");
        medicamento = new Gson().fromJson(classe, MedicamentosDisponiveis.class);
        buttonTitle();
        textNomeComercial.setText(medicamento.getText_nome_comercial());
        textApresentacao.setText(medicamento.getText_apresentacao());
        textDataValidade.setText(medicamento.getData_validade());
        textQuantidadeDisponivel.setText(medicamento.getQtd_disponivel()
                + " " + medicamento.getTipo_medicamento() + "(s)");
        textLaboratorio.setText(medicamento.getText_laboratorio());
        textPrincipioAtivo.setText(medicamento.getText_principio_ativo());

    }

    public void sair(View view){
        finish();
    }

    private void carregarViews(){
        textNomeComercial = (TextView)findViewById(R.id.textNomeComercial);
        textApresentacao = (TextView)findViewById(R.id.textApresentacao);
        textDataValidade = (TextView)findViewById(R.id.textDataValidade);
        textLaboratorio = (TextView)findViewById(R.id.textLaboratorio);
        textPrincipioAtivo = (TextView)findViewById(R.id.textPrincipioAtivo);
        textQuantidadeDisponivel = (TextView)findViewById(R.id.textQuantidadeDisponivel);
        buttonSolicitar = (Button)findViewById(R.id.buttonSolicitarDoacao);
        buttonAlterarQtd = (Button)findViewById(R.id.buttonAlterarQtdDisp);
        buttonAlterarValidade = (Button)findViewById(R.id.buttonAlterarValidade);


    }

    private void buttonTitle(){
        if (tipo_load == 0) {
            buttonSolicitar.setText(R.string.solicitar);
            buttonAlterarQtd.setVisibility(View.GONE);
            buttonAlterarValidade.setVisibility(View.GONE);

        } else if (tipo_load == 1){
            buttonSolicitar.setText(R.string.alterar);
        }
    }

    public void registrarSolicitacao(View view){

        if (tipo_load == 0) {
            solicitar();
        } else if (tipo_load == 1){
            atualizar();
        }
    }

    private void solicitar(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.selecione_quantidade));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                RegistrarSolicitacao solicitacao = new RegistrarSolicitacao();
                solicitacao.setFb_id_solicitante(new LocalDatabase(DialogMedicamento.this).buscarIDUsuario());
                solicitacao.setFk_medicamento_solicitado(medicamento.getPk_mmd());
                solicitacao.setLatitude(0);
                solicitacao.setLongitude(0);
                solicitacao.setNr_valor(0);
                solicitacao.setQtd_solicitado(Integer.parseInt(input.getText().toString()));
                String metodo = "SP_REGISTRAR_SOLICITACAO";
                String gson = new Gson().toJson(solicitacao);

                PostMethod postMethod = new PostMethod(gson, metodo, DialogMedicamento.this, new PostMethod.Callback() {
                    @Override
                    public void run(String result) {
                        Toast.makeText(DialogMedicamento.this, result, Toast.LENGTH_LONG).show();
                        finish();

                    }
                }, "Solicitando...");
                postMethod.execute();

            }
        });
        alert.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        alert.show();
    }

    private void atualizar(){
        String metodo = "SP_ATUALIZAR_MEDICAMENTO_DISPONIVEL";
        String gson = new Gson().toJson(medicamento);
        PostMethod postMethod = new PostMethod(gson, metodo, DialogMedicamento.this, new PostMethod.Callback() {
            @Override
            public void run(String result) {
                Toast.makeText(DialogMedicamento.this, result, Toast.LENGTH_LONG).show();
                finish();

            }
        }, "Solicitando...");
        postMethod.execute();
    }

    public void alterarQtd(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getResources().getString(R.string.selecione_quantidade));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                medicamento.setQtd_disponivel(Integer.parseInt(input.getText().toString()));
                textQuantidadeDisponivel.setText(medicamento.getQtd_disponivel() + " " + medicamento.getTipo_medicamento() + "(s)");
            }
        });
        alert.setNegativeButton(this.getResources().getString(R.string.voltar), null);
        alert.show();
    }

    public void alterarValidade(View view) {

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

                textDataValidade.setText(sdf.format(myCalendar.getTime()));
                medicamento.setData_validade(sdf.format(myCalendar.getTime()));

            }
        };

        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }
}
