package Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import adapter.ListaMedicamentoAdapter;
import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.Usuario;
import br.com.soasd.projetoa.CadUserActivity;
import br.com.soasd.projetoa.CadastroMedicamentoActivity;
import br.com.soasd.projetoa.DialogCadastrarDoacao;
import br.com.soasd.projetoa.DialogMedicamento;
import br.com.soasd.projetoa.DoarActivity;
import br.com.soasd.projetoa.MainActivity;
import br.com.soasd.projetoa.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.PostMethod;
import httpservices.RetrofitConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.LocalDatabase;


public class MeusItens extends BaseFragment {
    @BindView(R.id.imageViewBackgroundItens)
    ImageView imageViewBackground;
    @BindView(R.id.recyclerViewMeusItens)
    RecyclerView recyclerView;
    @BindView(R.id.progressBarMeusItens)
    ProgressBar progressBar;

    private List<MedicamentosDisponiveis> listaMedicamentos = new ArrayList<>();
    private static ListaMedicamentoAdapter adapter = null;
    private static int TIPO_ACESSO;
    private RetrofitConfig retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        retrofit = httpservices.Retrofit.getInstance().createRetrofit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_meus_itens, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        ((MainActivity)getActivity()).updateToolbarTitle("Meus Itens");
        TIPO_ACESSO = getArguments().getInt("TIPO_ACESSO");

        adapter = new ListaMedicamentoAdapter(getContext(), onClickMedicamento(), 3);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);


            taskMeusItens();

            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);





        return view;
    }


    private void taskMeusItens(){
        listaMedicamentos.clear();
       progressBar.setVisibility(View.VISIBLE);
        Call<List<MedicamentosDisponiveis>> b = retrofit.SP_BUSCAR_MEUS_ITENS(new LocalDatabase(getContext()).buscarIDUsuario());

        b.enqueue(new Callback<List<MedicamentosDisponiveis>>() {
            @Override
            public void onResponse(Call<List<MedicamentosDisponiveis>> call, Response<List<MedicamentosDisponiveis>> response) {
                    if ((response.code() == 200)){

                        if (response.body() != null) {
                            if (response.body().size() > 0) {
                                for (MedicamentosDisponiveis m : response.body()) {
                                    listaMedicamentos.add(m);
                                }

                                adapter.addAll(listaMedicamentos);
                                imageViewBackground.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else {
                                imageViewBackground.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        imageViewBackground.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
            }

            @Override
            public void onFailure(Call<List<MedicamentosDisponiveis>> call, Throwable t) {
                imageViewBackground.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private ListaMedicamentoAdapter.MedicamentosOnClickListener onClickMedicamento() {
        return new ListaMedicamentoAdapter.MedicamentosOnClickListener() {
            @Override
            public void onClickMedicamento(View view, int index) {
                MedicamentosDisponiveis medicamento = listaMedicamentos.get(index);
                Intent i = new Intent(getContext(), DialogMedicamento.class);
                Bundle bundle = new Bundle();
                String classe = new Gson().toJson(medicamento);
                bundle.putString("medicamento", classe);
                bundle.putInt("tipo", 1);
                i.putExtras(bundle);
                ((AppCompatActivity) getContext()).startActivityForResult(i, 0);

            }

            @Override
            public void onClickDoacao(View view, int index) {
                if (TIPO_ACESSO == 0) {

                    final MedicamentosDisponiveis medicamento = listaMedicamentos.get(index);
                    PopupMenu popup = new PopupMenu(getContext(), view);
                    popup.getMenuInflater()
                            .inflate(R.menu.meusitens_more, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_excluir:

                                    Call<Boolean> c =
                                            retrofit.SP_EXCLUIR_MEDICAMENTO( new LocalDatabase(getContext()).buscarIDUsuario(),
                                                    medicamento.getPk_mmd());

                                    c.enqueue(new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                                if (response.isSuccessful()) {
                                                        if (response.body()) {
                                                            showtAutoDismissDialog("Medicamento excluido");
                                                        } else {
                                                            showtAutoDismissDialog("Não foi possível excluir o medicamento");
                                                        }
                                                } else {
                                                    showtAutoDismissDialog("Não foi possível excluir o medicamento");
                                                }
                                        }

                                        @Override
                                        public void onFailure(Call<Boolean> call, Throwable t) {
                                            Log.i("TAG", t.getMessage());
                                            showtAutoDismissDialog("Não foi possível excluir o medicamento");
                                        }
                                    });

                                    break;
                                case R.id.menu_doar:
                                    String medicamentoGson = new Gson().toJson(medicamento);
                                    Intent i = new Intent(getContext(), DoarActivity.class);
                                    i.putExtra("MEDICAMENTO_GSON", medicamentoGson);
                                    startActivityForResult(i, 101);

                                    break;

                            }


                            return true;
                        }
                    });

                    popup.show();


                } else {

                    MedicamentosDisponiveis medicamento = listaMedicamentos.get(index);
                    Intent i = new Intent(getContext(), DialogMedicamento.class);
                    Bundle bundle = new Bundle();
                    String classe = new Gson().toJson(medicamento);
                    bundle.putString("medicamento", classe);
                    bundle.putInt("tipo", 1);
                    i.putExtras(bundle);
                    ((AppCompatActivity) getContext()).startActivityForResult(i, 0);

                }

            }
        };


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (TIPO_ACESSO != 1) {
            MenuInflater menuInflater = getActivity().getMenuInflater();

            menuInflater.inflate(R.menu.search_view_meus_itens, menu);
            final MenuItem filtroItem = menu.findItem(R.id.menu_cadastro);
            final MenuItem searchItem = menu.findItem(R.id.searchView_button);

            SearchView searchView  = null;

            if (searchItem != null) {
                searchView = (SearchView)searchItem.getActionView();
                searchView.setQueryHint("Pesquisar");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        Log.i("TAG", "Submit()");

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        filter(s);
                        return false;
                    }
                });
            }




            AppCompatTextView buttonCadastrar = null;
            if (filtroItem != null) {
                buttonCadastrar = (AppCompatTextView) filtroItem.getActionView();
                buttonCadastrar.setText("Cadastrar");
                buttonCadastrar.setTextColor(Color.WHITE);
                buttonCadastrar.setTextSize(18);
                buttonCadastrar.setBackgroundColor(Color.TRANSPARENT);
                buttonCadastrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(getContext(), view);
                        popup.getMenuInflater()
                                .inflate(R.menu.menu_cadastro, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_medicamento:
                                        Intent i = new Intent(getContext(), CadastroMedicamentoActivity.class);
                                        startActivityForResult(i, 101);
                                        break;
                                    case R.id.menu_usuario:
                                        Intent i2 = new Intent(getContext(), CadUserActivity.class);
                                        startActivity(i2);
                                        break;

                                }


                                return true;
                            }
                        });

                        popup.show();

                    }
                });
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if (resultCode == 100) {
                Log.i("TAG", "retornando a activity");
                adapter.removeAll();
                        listaMedicamentos.clear();
                taskMeusItens();
            }
        }

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
            }
        });

        handler.postDelayed(runnable, 1500);
    }


    private void filter(String text){
        List<MedicamentosDisponiveis> temp = new ArrayList<>();
        for (MedicamentosDisponiveis m: listaMedicamentos) {
            if (m.getText_nome_comercial().toLowerCase().contains(text.toLowerCase())){
                temp.add(m);
            }
        }
        adapter.updateList(temp);
        recyclerView.setAdapter(adapter);
    }
}
