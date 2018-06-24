package Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import adapter.ListaDoacoesAdapter;
import br.com.projetoa.model.AtualizarStatusDoacao;
import br.com.projetoa.model.BuscarDoacoes;
import br.com.soasd.projetoa.DialogDoacao;
import br.com.soasd.projetoa.MainActivity;
import br.com.soasd.projetoa.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.GetMethod;
import httpservices.PostMethod;
import service.LocalDatabase;


public class Doacoes extends BaseFragment {
    private List<BuscarDoacoes> listaDoacoes;

    protected RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshDoacoes)
    SwipeRefreshLayout swipeRefreshDoacoes;
    @BindView(R.id.imageViewDoacao)
    ImageView imageViewDoacao;
    private AlertDialog alertDialogFiltro;
    private static final int REQUEST_PERMISSIONS_CODE = 128;

    private static int TIPO_ACESSO;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_doacoes, container, false);
        ButterKnife.bind(this, view);
        recyclerView = view.findViewById(R.id.recyclerViewDoacoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        TIPO_ACESSO = getArguments().getInt("TIPO_ACESSO");

        taskBuscarDoacoes(new LocalDatabase(getContext()).buscarIDUsuario(), 4, 2);
        ( (MainActivity)getActivity()).updateToolbarTitle("Doações");






        swipeRefreshDoacoes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                taskBuscarDoacoes(new LocalDatabase(getContext()).buscarIDUsuario(), 4, 2);
                swipeRefreshDoacoes.setRefreshing(false);
            }
        });


        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater menuInflater = getActivity().getMenuInflater();

        menuInflater.inflate(br.com.soasd.projetoa.R.menu.menu_textview, menu);
        final MenuItem filtroItem = menu.findItem(br.com.soasd.projetoa.R.id.textViewButton);
        AppCompatTextView buttonFiltro = null;
        if (filtroItem != null) {
            buttonFiltro = (AppCompatTextView) filtroItem.getActionView();
            buttonFiltro.setText("Filtrar");
            buttonFiltro.setTextSize(18);
            buttonFiltro.setTextColor(Color.WHITE);
            buttonFiltro.setBackgroundColor(Color.TRANSPARENT);
            buttonFiltro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    criaAlertDialogFiltro();

                }
            });
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
                if (resultCode == 100){
                    taskBuscarDoacoes(new LocalDatabase(getContext()).buscarIDUsuario(), 4, 2);
                }
        }

        if (requestCode == 102){
                if (requestCode == 100) {
                    taskBuscarDoacoes(new LocalDatabase(getContext()).buscarIDUsuario(), 4, 2);
                }
        }

    }

    private void taskBuscarDoacoes(String id_fb, final int parametro, final int tipo){
        String metodo = "SP_BUSCAR_DOACOES?" + "id=" + id_fb;
        metodo = metodo + "&parametro=" + parametro;
        metodo = metodo + "&tipo=" + tipo;

        new GetMethod(metodo, getContext(), new GetMethod.Callback() {
            @Override
            public void run(String result) {
                Type listType = new TypeToken<ArrayList<BuscarDoacoes>>(){}.getType();
                listaDoacoes = new Gson().fromJson(result, listType);

                if (listaDoacoes.size() > 0) {
                    recyclerView.setAdapter(new ListaDoacoesAdapter(getContext(), listaDoacoes, onClickDoacao(),
                            parametro, tipo));
                    recyclerView.setVisibility(View.VISIBLE);
                    imageViewDoacao.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    imageViewDoacao.setVisibility(View.VISIBLE);
                }

            }
        }).execute();
    }

    private ListaDoacoesAdapter.DoacoesOnClickListener onClickDoacao() {
        return new ListaDoacoesAdapter.DoacoesOnClickListener() {

            @Override
            public void onClickDoacao(View view, int index) {
                BuscarDoacoes doacoes = listaDoacoes.get(index);

                Intent i = new Intent(getContext(), DialogDoacao.class);
                Bundle bundle = new Bundle();
                String classe = new Gson().toJson(doacoes);
                bundle.putString("medicamento", classe);
                i.putExtras(bundle);
                startActivityForResult(i, 101);


            }

            @Override
            public void onClickButton(View view, int index) {
                final BuscarDoacoes doacoes = listaDoacoes.get(index);

                switch(doacoes.getTipo()){
                    case 0: //Alterar
                        Intent i = new Intent(getContext(), DialogDoacao.class);
                        Bundle bundle = new Bundle();
                        String classe = new Gson().toJson(doacoes);
                        bundle.putString("medicamento", classe);
                        i.putExtras(bundle);
                        getActivity().startActivityForResult(i, 102);
                        break;

                    case 1: //Solicitação
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle(getContext().getResources().getString(R.string.deseja_cancelar));
                        alert.setPositiveButton(getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AtualizarStatusDoacao statusDoacao = new AtualizarStatusDoacao();
                                statusDoacao.setCodigo_validador(doacoes.getCodigo_validador());
                                statusDoacao.setQtd_doado(doacoes.getQtd_solicitado());
                                statusDoacao.setStatus_troca(3); //Cancelado
                                String metodo = "SP_ATUALIZAR_STATUS_DOACAO";
                                String gson = new Gson().toJson(statusDoacao);

                                PostMethod postMethod = new PostMethod(gson, metodo, getContext(), new PostMethod.Callback() {
                                    @Override
                                    public void run(String result) {
                                        showtAutoDismissDialog(result);

                                    }
                                }, getContext().getResources().getString(R.string.atualizando));
                                postMethod.execute();

                            }
                        }).setNegativeButton(getContext().getResources().getString(R.string.no), null).show();



                        break;

                }

            }
        };
    }

    private void criaAlertDialogFiltro(){
        CharSequence[] items = {"Sem Filtro","Minhas solicitações","Minhas doações",
                "Solicitações canceladas", "Solicitações pendentes", "Doações canceladas",
                "Doações pendentes", "Solicitações disponibilizadas", "Doações disponibilizadas"};
        final String fb_id = new LocalDatabase(getContext()).buscarIDUsuario();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Escolha um filtro:");

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0: //SEM FILTRO

                        taskBuscarDoacoes(fb_id, 4, 2);

                        break;
                    case 1: //Apenas Solicitacoes

                        taskBuscarDoacoes(fb_id, 4, 1);

                        break;
                    case 2: //Apenas Doacoes

                        taskBuscarDoacoes(fb_id, 4, 0);
                        break;
                    case 3: //Apenas Solicitacoes Canceladas

                        taskBuscarDoacoes(fb_id, 3, 1);
                        break;
                    case 4: //Apenas Solicitacoes Pendentes

                        taskBuscarDoacoes(fb_id, 0, 1);
                        break;
                    case 5: //Apenas Doacoes Canceladas

                        taskBuscarDoacoes(fb_id, 3, 0);
                        break;
                    case 6: //Apenas Doacoes pendentes

                        taskBuscarDoacoes(fb_id, 0, 0);
                        break;
                    case 7: //Solicitações disponibilizadas

                        taskBuscarDoacoes(fb_id, 1, 1);
                        break;
                    case 8: //Doações disponibilizadas

                        taskBuscarDoacoes(fb_id, 1, 0);
                        break;
                }
                alertDialogFiltro.dismiss();
            }
        });
        alertDialogFiltro = builder.create();
        alertDialogFiltro.show();

    }

    private void showtAutoDismissDialog(String mensagem) {
        TextView msg = new TextView(getContext());
        msg.setText("\n" + mensagem + "\n");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
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
                taskBuscarDoacoes(new LocalDatabase(getContext()).buscarIDUsuario(), 4, 2);
            }
        });

        handler.postDelayed(runnable, 1500);
    }

}
