package service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutionException;

import br.com.projetoa.bo.DAO;
import br.com.projetoa.model.AtualizarStatusDoacao;
import br.com.projetoa.model.BuscarDoacoes;
import br.com.projetoa.model.DisponibilizarMedicamento;
import br.com.projetoa.model.Medicamento;
import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.RegistrarSolicitacao;
import br.com.projetoa.model.Usuario;
import br.com.projetoa.model.UsuarioIndependente;
import br.com.projetoa.model.exception.AtualizarStatusDoacaoException;
import br.com.projetoa.model.exception.BuscarDoacoesException;
import br.com.projetoa.model.exception.DisponibilizarMedicamentoException;
import br.com.projetoa.model.exception.MedicamentoException;
import br.com.projetoa.model.exception.MedicamentosDisponiveisException;
import br.com.projetoa.model.exception.RegistrarSolicitacaoException;
import br.com.projetoa.model.exception.UsuarioException;

import br.com.projetoa.model.exception.UsuarioIndependenteException;
import br.com.soasd.projetoa.MainActivity;
import br.com.soasd.projetoa.R;

import httpservices.PostMethod;
import httpservices.PostSingleMethod;
import httpservices.PrivateGetMethod;
import httpservices.PrivatePostMethod;



/**
 * Created by SOA - Development on 18/01/2018.
 */

public class ExternalDatabase implements DAO {

    private Context context;
    public ExternalDatabase(Context context){
        this.context = context;
    }


    @Override
    public String SP_ATUALIZAR_STATUS_DOACAO(AtualizarStatusDoacao atualizarStatusDoacao) throws AtualizarStatusDoacaoException {
        return null;
    }

    @Override
    public List<BuscarDoacoes> SP_BUSCAR_DOACOES(String id_fb, int parametro, int tipo) throws BuscarDoacoesException {


        return null;
    }

    @Override
    public Medicamento SP_BUSCAR_MEDICAMENTO_CODBARRAS(String s) throws MedicamentoException {
        return null;
    }

    @Override
    public List<Medicamento> SP_BUSCAR_MEDICAMENTO_CONTEXTUAL(String s) throws MedicamentoException {

        return null;
    }

    @Override
    public List<MedicamentosDisponiveis> SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS(List<String> s, int distancia) throws MedicamentosDisponiveisException {

        return null;
    }

    @Override
    public String SP_CADASTRAR_USUARIO(Usuario usuario) throws UsuarioException {
      String metodo = "SP_CADASTRAR_USUARIO";
      String gson = new Gson().toJson(usuario);
      Log.i("TAG", gson);
      String retorno = null;

           new PostMethod(gson, metodo, context, new PostMethod.Callback() {
                @Override
                public void run(String result) {
                    if(result.equals("1")){
                        Intent i = new Intent(new Intent(context, MainActivity.class));
                        i.putExtra("novo_usuario", 0);
                        context.startActivity(i);
                    }
                }
            }, "Cadastrando...").execute();

        return retorno;
    }

    @Override
    public String SP_DISPONIBILIZAR_MEDICAMENTO(DisponibilizarMedicamento disponibilizarMedicamento) throws DisponibilizarMedicamentoException {
        String metodo = "SP_DISPONIBILIZAR_MEDICAMENTO";
        String gson = new Gson().toJson(disponibilizarMedicamento);
        String retorno = null;


        new PostMethod(gson, metodo, context, new PostMethod.Callback() {
            @Override
            public void run(String result) {

                showtAutoDismissDialog(result);
            }
        }, "Cadastrando...").execute();

        return null;
    }

    @Override
    public String SP_REGISTRAR_SOLICITACAO(RegistrarSolicitacao registrarSolicitacao) throws RegistrarSolicitacaoException {
        return null;
    }

    @Override
    public String SP_BUSCAR_USUARIO(String facebook_id) throws UsuarioException {
        String metodo = "SP_BUSCAR_USUARIO?id=" + facebook_id;
        String retorno = "0";








        try {
            retorno = new PrivateGetMethod(metodo, context).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    @Override
    public void SP_ATUALIZAR_TOKEN_FCM(Usuario usuario) throws UsuarioException {
        String metodo = "SP_ATUALIZAR_TOKEN_FCM";
        String gson = new Gson().toJson(usuario);
        Log.i("TAG", gson);
        new PrivatePostMethod(gson, metodo, context).execute();
    }

    @Override
    public String SP_ATUALIZAR_MEDICAMENTO_DISPONIVEL(MedicamentosDisponiveis medicamentosDisponiveis) throws MedicamentosDisponiveisException {

        return null;
    }

    @Override
    public String SP_ATUALIZAR_USUARIO(Usuario usuario) throws UsuarioException {
        String metodo = "SP_ATUALIZAR_USUARIO";
        String gson = new Gson().toJson(usuario);
       PostSingleMethod postMethod =  new PostSingleMethod(gson, metodo, context, new PostSingleMethod.Callback() {
            @Override
            public void run(String result) {
                //showtAutoDismissDialog(result);
            }
        });
        postMethod.execute();
       return null;
    }

    @Override
    public String SP_CADASTRA_USUARIOIN(UsuarioIndependente usuarioIndependente, String s) throws UsuarioIndependenteException {
        return null;
    }


    @Override
    public List<UsuarioIndependente> SP_BUSCAR_USUARIOIN(String s) throws UsuarioIndependenteException {
        return null;
    }

    @Override
    public String SP_EXCLUIR_USUARIOIN(String s, int i) throws UsuarioIndependenteException {
        return null;
    }

    @Override
    public String SP_ATUALIZAR_USUARIOIN(UsuarioIndependente usuarioIndependente) throws UsuarioIndependenteException {
        return null;
    }

    private void showtAutoDismissDialog(String mensagem){
        TextView msg = new TextView(context);
        msg.setText("\n" + mensagem + "\n");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context)
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



}
