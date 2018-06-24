package br.com.soasd.projetoa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import br.com.projetoa.model.Usuario;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.GetSingleMethod;
import httpservices.PostMethod;
import httpservices.PrivateGetMethod;
import me.drakeet.materialdialog.MaterialDialog;
import service.LocalDatabase;


public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private Usuario usuario = new Usuario();
    private String myFormat = "dd/MM/yyyy";
    private SimpleDateFormat df = new SimpleDateFormat(myFormat);
    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextSenha)
    EditText editTextSenha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        if (isInternetConnectionOn()) {
            if (isGooglePlayServicesAvailable(this)) {
                if (checkServidor() == true) {
                    callbackManager = CallbackManager.Factory.create();

                    if (isLoggedIn()) {

                        armazenarDadosUsuario(AccessToken.getCurrentAccessToken().getUserId(), "", 1);
                    } else if (checkUserLocalLogin()){
                        String usuario = new LocalDatabase(this).buscarLogin();

                        Usuario u = new Gson().fromJson(usuario, Usuario.class);
                        armazenarDadosUsuario(u.getFb_id(), u.getText_email(), 0);

                    }

                }
            }
        }

        criaBancoDeDadosLocal();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 444){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void buttoLoginFb(View view){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile",
                "user_friends","user_birthday", "user_location", "email"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                String metodo = "SP_BUSCAR_USUARIO?id="  + loginResult.getAccessToken().getUserId();

                        new GetSingleMethod(metodo, LoginActivity.this, new GetSingleMethod.Callback() {
                            @Override
                            public void run(String result) {
                                if (result.equals("1")) {
                                    armazenarDadosUsuario(AccessToken.getCurrentAccessToken().getUserId(), "", 1);

                                } else {
                                    cadastrarUsuario();
                                }

                            }
                        }).execute();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private boolean isInternetConnectionOn() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        return accessToken != null;
    }

    private boolean checkServidor(){
        boolean retorno = false;
        try {
            String checkCode = new PrivateGetMethod("", this).execute().get();
            if (checkCode.equals("#$123$#")) {
                retorno = true;
            } else {

                MaterialDialog mMaterialDialog = new MaterialDialog(this)
                        .setTitle(getResources().getString(R.string.erro))
                        .setMessage(getResources().getString(R.string.servidor_indisponivel))
                        .setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                mMaterialDialog.show();
            }
        } catch (InterruptedException e) {
            retorno = false;
        } catch (ExecutionException e) {
            retorno = false;
        }

        return retorno;
    }



    private void cadastrarUsuario(){
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id, first_name, last_name, location, birthday, email");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();

                                    String hometown = data.getJSONObject("location").getString("name");


                                    String first_name = data.getString("first_name");
                                    String last_name = data.getString("last_name");
                                    String birthday = data.getString("birthday");
                                    String email = data.getString("email");
                                    String id = data.getString("id");


                                    usuario.setToken_gcm(FirebaseInstanceId.getInstance().getToken());
                                    usuario.setData_nascimento(df.format(new Date(birthday)));
                                    usuario.setText_nome(first_name);
                                    usuario.setText_sobrenome(last_name);
                                    usuario.setText_email(email);
                                    usuario.setText_cidade(hometown);
                                    usuario.setFb_id(id);
                                    usuario.setTipo_conta(0);
                                    String gson = new Gson().toJson(usuario);





                                new PostMethod(gson, "SP_CADASTRAR_USUARIO", LoginActivity.this, new PostMethod.Callback() {
                                    @Override
                                    public void run(String result) {

                                        if(!result.equals("ERRO")){
                                            armazenarDadosUsuario(result, usuario.getText_email(), 1);

                                        }
                                    }
                                }, "Cadastrando...").execute();





                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    private void criaBancoDeDadosLocal(){
        new LocalDatabase(this);
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public void cadastrarInstituicao(View view) {
        Intent i = new Intent(LoginActivity.this, CadInstituicaoActivity.class);
        startActivity(i);
    }

    public void efetuarLogin(View view){
            try {
                final Usuario u = new Usuario();
                u.setText_email(editTextEmail.getText().toString());
                u.setText_password(editTextSenha.getText().toString());
                if (u.getText_email().equals("") || u.getText_password().equals("")) {
                    showtAutoDismissDialog("Por favor, informe o usuário e senha");
                } else {
                    String gson = new Gson().toJson(u);
                    new PostMethod(gson, "SP_EFETUAR_LOGIN", this, new PostMethod.Callback() {
                        @Override
                        public void run(String result) {
                            if (result.equals("1")) {
                                armazenarDadosUsuario(u.getFb_id(), u.getText_email(), 0);
                            } else {
                                showtAutoDismissDialog("Usuário ou senha incorreto!");
                                editTextSenha.setText("");
                            }
                        }
                    }, "Verificando...").execute();

                }
            } catch (NullPointerException e){
                showtAutoDismissDialog("Por favor, informe o usuário e senha");
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

    private void armazenarDadosUsuario(String id, final String email, final int value){

        String metodo = "SP_BUSCAR_DADOS_USUARIO?id="+ id + "&email=" + email + "";

        new GetSingleMethod(metodo, this, new GetSingleMethod.Callback() {
            @Override
            public void run(String result) {

                if (result != null) {
                    Usuario u = new Gson().fromJson(result, Usuario.class);
                    new LocalDatabase(LoginActivity.this).registarLogin(new Gson().toJson(u));
                    Intent i = new Intent(new Intent(LoginActivity.this, MainActivity.class));
                    i.putExtra("TIPO_ACESSO", value);
                    i.putExtra("EMAIL", email);
                    startActivity(i);
                }
            }
        }).execute();
    }

    private boolean checkUserLocalLogin(){
        if (new LocalDatabase(this).buscarLogin().length() > 5) {
            return true;
        } else {
            return false;
        }
    }
}
