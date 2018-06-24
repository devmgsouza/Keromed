package br.com.soasd.projetoa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
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
import com.google.gson.Gson;

import java.lang.reflect.Field;

import br.com.projetoa.model.Usuario;
import br.com.sapereaude.maskedEditText.MaskedEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.PostMethod;

public class CadInstituicaoActivity extends AppCompatActivity {

    @BindView(R.id.editTextNome)
    EditText editTextNome;
    @BindView(R.id.editTextEndereco)
    EditText editTextEndereco;
    @BindView(R.id.editTextNumero)
    EditText editTextNumero;
    @BindView(R.id.editTextCidade)
    EditText editTextCidade;
    @BindView(R.id.editTextFone)
    MaskedEditText editTextTelefone;
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextSenha)
    EditText editTextSenha;
    @BindView(R.id.spinnerUF)
    Spinner spinnerUF;
    ArrayAdapter adapterUF;

    int error_control;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadinstituicao);
        ButterKnife.bind(this);


        adapterUF = ArrayAdapter.createFromResource(this, R.array.uf_brasil,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerUF.setAdapter(adapterUF);
    }


    public void sair(View view){
        finish();
    }
    public void salvarRegistro(View view){

        Usuario u = new Usuario();
try {
    u.setText_razao_social(editTextNome.getText().toString());
    u.setText_nome(editTextNome.getText().toString());
    u.setText_nm_rua(editTextEndereco.getText().toString());
    u.setText_nr_residencia(editTextNumero.getText().toString());
    u.setText_cidade(editTextCidade.getText().toString());
    u.setText_fone(editTextTelefone.getText().toString());
    u.setText_uf(spinnerUF.getSelectedItem().toString());
    u.setData_nascimento("#null");
    u.setText_email(editTextEmail.getText().toString());
    u.setText_password(editTextSenha.getText().toString());
    u.setFb_id("#null");

    u.setTipo_conta(1);

    String gson = new Gson().toJson(u);
    new PostMethod(gson, "SP_CADASTRAR_USUARIO", this, new PostMethod.Callback() {
        @Override
        public void run(String result) {
            if (result.equals("1"))
                error_control = 0;
                showtAutoDismissDialog("Cadastro realizado!");

        }
    }, "Cadastrando usuário...").execute();


} catch (NullPointerException e){
    error_control = 1;
    showtAutoDismissDialog("Nenhum campo pode ser nulo");
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
                if (error_control == 0){
                    finish();
                }

            }
        });

        handler.postDelayed(runnable, 1500);
    }



}
