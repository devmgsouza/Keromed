package adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.List;

import br.com.projetoa.model.AtualizarStatusDoacao;
import br.com.projetoa.model.BuscarDoacoes;
import br.com.soasd.projetoa.DialogDoacao;
import br.com.soasd.projetoa.R;
import httpservices.PostMethod;


/**
 * Created by Marcio on 25/10/2017.
 */

public class ListaDoacoesAdapter extends RecyclerView.Adapter<ListaDoacoesAdapter.DoacaoHolderView> {
    public static String TAG = "ListaMedicamentoAdapter";
    private final Context context;
    private final List<BuscarDoacoes> doacoes;
    private DoacoesOnClickListener doacoesOnClickListener;
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    private int parametro;
    private int tipo;

    public ListaDoacoesAdapter(Context context, List<BuscarDoacoes> doacoes,
                               DoacoesOnClickListener doacoesOnClickListener, int parametro, int tipo) {
        this.context = context;
        this.doacoes = doacoes;
        this.parametro = parametro;
        this.tipo = tipo;
        this.doacoesOnClickListener = doacoesOnClickListener;


    }


    @Override
    public DoacaoHolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_doacoes, parent, false);
        DoacaoHolderView holder = new DoacaoHolderView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DoacaoHolderView holder, final int position) {
            final BuscarDoacoes medicamentoDoaco = doacoes.get(position);

        holder.textNomeMedicamento.setText(medicamentoDoaco.getText_nome_comercial());
        holder.textQtdSolicitado.setText("Solicitado: " + medicamentoDoaco.getQtd_solicitado()
                + " " + medicamentoDoaco.getTipo_medicamento() + "(s)");
        holder.textStatus.setText(medicamentoDoaco.getStatus_troca());
        holder.textTituloMedicamentoFoto.setText(medicamentoDoaco.getText_nome_comercial());
        holder.textDataSolicitacao.setText(medicamentoDoaco.getData_solicitacao());



        if (tipo == 0) {

            holder.buttonAlterar.setText(context.getResources().getString(R.string.alterar));
            if (medicamentoDoaco.getStatus_troca().equals("CANCELADO") ||
                    medicamentoDoaco.getStatus_troca().equals("CONCLUIDO")){
                holder.buttonAlterar.setVisibility(View.GONE);
                holder.buttonAlterar.setEnabled(false);
            }
/*
            holder.buttonAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    BuscarDoacoes doacoes = medicamentoDoaco;

                    Intent i = new Intent(context, DialogDoacao.class);
                    Bundle bundle = new Bundle();
                    String classe = new Gson().toJson(doacoes);
                    bundle.putString("medicamento", classe);
                    i.putExtras(bundle);
                    ((AppCompatActivity)context).startActivityForResult(i, 0);

                }
            });
  */
        } else if (tipo == 1) { //solicitações
            holder.buttonAlterar.setText(context.getResources().getString(R.string.cancelar));
            if (medicamentoDoaco.getStatus_troca().equals("CANCELADO") ||
                    medicamentoDoaco.getStatus_troca().equals("CONCLUIDO")){
                holder.buttonAlterar.setVisibility(View.GONE);
                holder.buttonAlterar.setEnabled(false);
            }
            /*
            holder.buttonAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle(context.getResources().getString(R.string.deseja_cancelar));
                    alert.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AtualizarStatusDoacao statusDoacao = new AtualizarStatusDoacao();
                            statusDoacao.setCodigo_validador(medicamentoDoaco.getCodigo_validador());
                            statusDoacao.setQtd_doado(medicamentoDoaco.getQtd_solicitado());
                            statusDoacao.setStatus_troca(3); //Cancelado
                            String metodo = "SP_ATUALIZAR_STATUS_DOACAO";
                            String gson = new Gson().toJson(statusDoacao);

                            PostMethod postMethod = new PostMethod(gson, metodo, context, new PostMethod.Callback() {
                                @Override
                                public void run(String result) {
                                    Toast.makeText(context, result, Toast.LENGTH_LONG).show();

                                }
                            }, context.getResources().getString(R.string.atualizando));
                            postMethod.execute();

                        }
                    }).setNegativeButton(context.getResources().getString(R.string.no), null).show();


                }
            });
*/
        } else {
            holder.buttonAlterar.setText(context.getResources().getString(R.string.consultar));
            holder.buttonAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BuscarDoacoes doacoes = medicamentoDoaco;

                    Intent i = new Intent(context, DialogDoacao.class);
                    Bundle bundle = new Bundle();
                    String classe = new Gson().toJson(doacoes);
                    bundle.putString("medicamento", classe);
                    i.putExtras(bundle);
                    ((AppCompatActivity)context).startActivityForResult(i, 0);

                }
            });
        }



        if (doacoesOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doacoesOnClickListener.onClickDoacao(holder.itemView, position);

                }
            });


            holder.buttonAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doacoesOnClickListener.onClickButton(holder.buttonAlterar, position);
                }
            });
        }

            }



    @Override
    public int getItemCount() {
        return this.doacoes != null ? this.doacoes.size() : 0;
    }





    public interface DoacoesOnClickListener {
         void onClickDoacao(View view, int index);
         void onClickButton(View view, int index);
    }


    public static class DoacaoHolderView extends RecyclerView.ViewHolder {

        public TextView textNomeMedicamento;

        public TextView textDataSolicitacao;

        public TextView textDescricaoMedicamento;

        public TextView textQtdSolicitado;

        public TextView textTituloMedicamentoFoto;

        public TextView textStatus;

        public CardView cardView;

        public TextView buttonAlterar;

        public DoacaoHolderView(View view) {

            super(view);
            textNomeMedicamento = (TextView)view.findViewById(R.id.textViewNomeMedicamento);
            textDescricaoMedicamento = (TextView)view.findViewById(R.id.textViewDescricaoMedicamento);
            textQtdSolicitado = (TextView)view.findViewById(R.id.textViewQtdSolicitado);
            cardView = (CardView)view.findViewById(R.id.card_view);
            buttonAlterar = (TextView)view.findViewById(R.id.buttonAlterarStatus);
            textTituloMedicamentoFoto = (TextView)view.findViewById(R.id.textTituloMedicamentoFoto);
            textStatus = (TextView) view.findViewById(R.id.textStatus);
            textDataSolicitacao = (TextView)view.findViewById(R.id.textDataSolicitacao);

        }
    }









}
