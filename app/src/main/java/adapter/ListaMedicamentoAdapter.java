package adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.google.gson.Gson;

import br.com.projetoa.model.AtualizarStatusDoacao;
import br.com.projetoa.model.RegistrarSolicitacao;
import br.com.soasd.projetoa.DialogMedicamento;
import br.com.soasd.projetoa.R;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoa.model.MedicamentosDisponiveis;
import httpservices.PostMethod;


/**
 * Created by Marcio on 25/10/2017.
 */

public class ListaMedicamentoAdapter extends RecyclerView.Adapter<ListaMedicamentoAdapter.MedicamentoHolderView> {
    private final Context context;
    private static List<MedicamentosDisponiveis> medicamentos;
    private MedicamentosOnClickListener medicamentosOnClickListener;
    private boolean isLoadingAdded = false;
    private int value; //1 -  home > 3 - meus itens
    public static ListaMedicamentoAdapter instance;



    public ListaMedicamentoAdapter(Context context,
                                   MedicamentosOnClickListener medicamentosOnClickListener, int value) {
        this.context = context;
        this.value = value;
        this.medicamentosOnClickListener = medicamentosOnClickListener;
        medicamentos = new ArrayList<>();

    }



    @Override
    public MedicamentoHolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_home, parent, false);
        MedicamentoHolderView holder = new MedicamentoHolderView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MedicamentoHolderView holder, final int position) {
        final MedicamentosDisponiveis medicamento = medicamentos.get(position);



                holder.textNomeMedicamento.setText(medicamento.getText_nome_comercial());
                String textDescricao = medicamento.getText_apresentacao();
                textDescricao += "\nValidade: " + medicamento.getData_validade();
                holder.textDescricaoMedicamento.setText(textDescricao);
                holder.textQtdDisponivel.setText("Disponível: " + medicamento.getQtd_disponivel()
                        + " " + medicamento.getTipo_medicamento() + "(s)");
                Log.i("TAG", "VALUE = " + value);
                if(value == 1) {
                    holder.buttonSolicitar.setText(context.getResources().getString(R.string.solicitar));
                    holder.textDistancia.setText(String.valueOf(medicamento.getDistancia()) + "Km");
                } else if (value == 3){
                    holder.buttonSolicitar.setText(R.string.option);
                    holder.textDistancia.setVisibility(View.INVISIBLE);
                    holder.textDistanciaDist.setVisibility(View.INVISIBLE);
                }

                holder.textTituloMedicamentoFoto.setText(medicamento.getText_nome_comercial() + "\n" +
                medicamento.getText_apresentacao());



                if (medicamentosOnClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            medicamentosOnClickListener.onClickMedicamento(holder.itemView, position);

                        }
                    });
                    holder.buttonSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            medicamentosOnClickListener.onClickDoacao(holder.buttonSolicitar, position);
                        }
                    });
                }
            }

    @Override
    public int getItemCount() {
        return this.medicamentos != null ? this.medicamentos.size() : 0;
    }

    public interface MedicamentosOnClickListener {
         void onClickMedicamento(View view, int index);
         void onClickDoacao(View view, int index);
    }

    public static class MedicamentoHolderView extends RecyclerView.ViewHolder {

        public TextView textNomeMedicamento;

        public TextView textDescricaoMedicamento;

        public TextView textQtdDisponivel;

        public TextView textTituloMedicamentoFoto;

        public TextView textDistanciaDist;

        public ImageView imageViewMedicamento;

        public TextView textDistancia;

        public CardView cardView;

        public TextView buttonSolicitar;

        public MedicamentoHolderView(View view) {

            super(view);
            textNomeMedicamento = (TextView)view.findViewById(R.id.textViewNomeMedicamento);
            textDescricaoMedicamento = (TextView)view.findViewById(R.id.textViewDescricaoMedicamento);
            textQtdDisponivel = (TextView)view.findViewById(R.id.textViewQtdDisponivel);
            cardView = (CardView)view.findViewById(R.id.card_view);
            buttonSolicitar = (TextView)view.findViewById(R.id.buttonSolicitar);
            textTituloMedicamentoFoto = (TextView)view.findViewById(R.id.textTituloMedicamentoFoto);
            textDistancia = (TextView) view.findViewById(R.id.textViewDistancia);
            textDistanciaDist = (TextView)view.findViewById(R.id.textStatusStatus);
            imageViewMedicamento = (ImageView)view.findViewById(R.id.imageViewMedicamento);
        }
    }


    public void addAll(List<MedicamentosDisponiveis> moveResults) {
        Log.i("TAG", medicamentos.size() + " TAMANHO ATUAL");
        for (MedicamentosDisponiveis result : moveResults) {
            add(result);
        }
        Log.i("TAG", medicamentos.size() + " NOVO TAMANHO");
    }

    public void add(MedicamentosDisponiveis r) {

        medicamentos.add(r);

        notifyItemInserted(medicamentos.size() - 1);
    }


    public void removeAll(){
        final int size = medicamentos.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                medicamentos.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


    public void updateList(List<MedicamentosDisponiveis> updateList){
        medicamentos = updateList;
        notifyDataSetChanged();
    }


    public int size(){
        return medicamentos.size();
    }

}
