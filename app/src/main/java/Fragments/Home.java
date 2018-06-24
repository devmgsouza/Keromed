package Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.projetoa.model.RegistrarSolicitacao;
import br.com.projetoa.model.Usuario;
import br.com.soasd.projetoa.DialogCadastrarDoacao;
import br.com.soasd.projetoa.DialogMedicamento;
import br.com.soasd.projetoa.MainActivity;
import br.com.soasd.projetoa.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import adapter.ListaMedicamentoAdapter;
import br.com.projetoa.model.MedicamentosDisponiveis;
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
import service.SingleShotLocationProvider;
import utils.PaginationScrollListener;


public class Home extends BaseFragment  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.recyclerViewHome)
    RecyclerView recyclerView;
    @BindView(R.id.imageViewBgHome)
    ImageView imageViewBgHome;
    @BindView(R.id.swipeRefreshHome)
    SwipeRefreshLayout swipeRefreshHome;
    @BindView(R.id.progressBarHome)
    ProgressBar progressBar;
    @BindView(R.id.progressBarCenter)
    ProgressBar progressBarCenter;


    private ListaMedicamentoAdapter adapter;
    private Usuario usuario;
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    private GoogleApiClient mGoogleApiClient;
    private static double latitude, longitude;
    private int TIPO_ACESSO;
    private List<MedicamentosDisponiveis> listaMedicamentos = new ArrayList<>();
    private static final int PAGE_START = 0;
    private int currentPage = PAGE_START;
    private boolean isLoading = false;
    private int TOTAL_PAGE = 0;
    private boolean isLastPage = false;
    private RetrofitConfig retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        retrofit = createRetrofit();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        startPlayServices();

        ButterKnife.bind(this, view);
        final LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        adapter = new ListaMedicamentoAdapter(getContext(), onClickMedicamento(), 1);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        updateLocation(getContext());


        TIPO_ACESSO = getArguments().getInt("TIPO_ACESSO");
        ((MainActivity)getActivity()).updateToolbarTitle("Home");
        usuario = new Gson().fromJson(new LocalDatabase(getContext()).buscarLogin(), Usuario.class);


        if (TIPO_ACESSO == 1) {
            taskMedicamentosFacebook(0);
        } else {
            taskMedicamentos(0);
        }


        swipeRefreshHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               adapter.removeAll();
               listaMedicamentos.clear();
                if (TIPO_ACESSO == 1) {
                    taskMedicamentosFacebook(0);
                } else {
                    taskMedicamentos(0);
                }


                swipeRefreshHome.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayout) {
            @Override
            protected void loadMoreItems() {
                Log.i("TAG", "loadMoreItems()");
                isLoading = true;
                TOTAL_PAGE++;
                if (TIPO_ACESSO == 1) {
                    taskMedicamentosFacebook(currentPage);
                } else {

                    taskMedicamentos(currentPage);
                }

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGE;
            }

            @Override
            public boolean isLastPage() {
                Log.i("TAG", "isLastPage()");
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                Log.i("TAG", "isLoading()");
                return isLoading;
            }
        });

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater menuInflater = ( (MainActivity)getActivity()).getMenuInflater();

        menuInflater.inflate(R.menu.search_view_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.searchView_button);
        final MenuItem addItem = menu.findItem(R.id.addView_button);

        AppCompatImageButton buttonView = null;
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


        if (addItem != null) {
            buttonView = (AppCompatImageButton) addItem.getActionView();
            buttonView.setImageResource(R.drawable.ic_add);
            buttonView.setAdjustViewBounds(true);
            buttonView.setBackgroundColor(Color.TRANSPARENT);
            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), DialogCadastrarDoacao.class);
                    startActivity(i);

                }
            });
        }

    }

    private void taskMedicamentosFacebook(final int lastid){
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends?limit=5000",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                            List<String> amigosFacebook = new ArrayList<>();

                            JSONObject dados = response.getJSONObject();
                            try {
                                JSONArray jsonArray = dados.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    amigosFacebook.add((String) object.get("id"));
                                }
                                progressBarCenter.setVisibility(View.VISIBLE);
                                int max = new LocalDatabase(getContext()).loadSettings().getDistancia_maxima();
                                Call<List<MedicamentosDisponiveis>> b = retrofit.SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS(amigosFacebook, max, latitude, longitude, lastid);

                                b.enqueue(new Callback<List<MedicamentosDisponiveis>>() {
                                    @Override
                                    public void onResponse(Call<List<MedicamentosDisponiveis>> call, Response<List<MedicamentosDisponiveis>> response) {
                                        if (response.code() == 200) {

                                            if (response.body().size() > 0) {
                                                if (currentPage == 0) {
                                                    listaMedicamentos.clear();
                                                    for (MedicamentosDisponiveis m : response.body()) {
                                                        listaMedicamentos.add(m);
                                                        currentPage = m.getPk_mmd();

                                                    }
                                                    adapter.addAll(listaMedicamentos);
                                                } else {
                                                    isLoading = false;

                                                    List<MedicamentosDisponiveis> nextPage = new ArrayList<>();
                                                    for (MedicamentosDisponiveis m : response.body()) {
                                                        nextPage.add(m);
                                                        currentPage = m.getPk_mmd();

                                                    }
                                                    adapter.addAll(nextPage);
                                                }


                                                progressBarCenter.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                currentPage = 0;
                                                isLastPage = true;
                                                progressBar.setVisibility(View.GONE);
                                                progressBarCenter.setVisibility(View.GONE);
                                            }

                                        } else {
                                            Log.i("TAG", response.code() + " CODIGO DE RETORNO");
                                            recyclerView.setVisibility(View.GONE);
                                            imageViewBgHome.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                            progressBarCenter.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<MedicamentosDisponiveis>> call, Throwable t) {

                                    }
                                });



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }

    private void taskMedicamentos(int lastId){
        progressBarCenter.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        int max = new LocalDatabase(getContext()).loadSettings().getDistancia_maxima();
        Call<List<MedicamentosDisponiveis>> b = retrofit.SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS_GERAL(max,
                latitude, longitude, lastId, usuario.getFb_id());


        b.enqueue(new Callback<List<MedicamentosDisponiveis>>() {
            @Override
            public void onResponse(Call<List<MedicamentosDisponiveis>> call, Response<List<MedicamentosDisponiveis>> response) {
                if (response.code() == 200) {
                    Log.i("TAG", "response = 200");
                    if (response.body().size() > 0) {
                        Log.i("TAG", "response > 0");
                        if (currentPage >= 0) {
                            Log.i("TAG", "currentPage > 0");
                            for (MedicamentosDisponiveis m : response.body()) {
                                listaMedicamentos.add(m);
                                currentPage = m.getPk_mmd();
                            }
                            adapter.addAll(listaMedicamentos);

                            isLoading = true;

                        } else {
                            isLoading = false;

                            List<MedicamentosDisponiveis> nextPage = new ArrayList<>();
                            for (MedicamentosDisponiveis m : response.body()) {
                                nextPage.add(m);
                                currentPage = m.getPk_mmd();

                            }


                            adapter.addAll(nextPage);
                            progressBarCenter.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            imageViewBgHome.setVisibility(View.GONE);
                        }


                         recyclerView.setVisibility(View.VISIBLE);
                         imageViewBgHome.setVisibility(View.GONE);
                         progressBar.setVisibility(View.GONE);
                         progressBarCenter.setVisibility(View.GONE);
                    } else {
                        isLastPage = true;
                        progressBar.setVisibility(View.GONE);
                        progressBarCenter.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        imageViewBgHome.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.i("TAG", "response != 200");
                    recyclerView.setVisibility(View.GONE);
                    imageViewBgHome.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    progressBarCenter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<MedicamentosDisponiveis>> call, Throwable t) {
                Log.i("TAG", "FAIL");
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        atualizarLocalizacao();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    private void atualizarLocalizacao(){
        if ( ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                createDialogYes(R.string.importante, R.string.permissao_necessaria, R.string.yes);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_CODE);

                atualizarLocalizacao();

            }
        } else {
            isGPSOn();

            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                    if (listaMedicamentos.size() >0) {
                        listaMedicamentos.clear();
                    }
                    /*
                if (TIPO_ACESSO == 1) {
                    taskMedicamentosFacebook(0);
                } else {
                    taskMedicamentos(0);
                }
*/

            } else {
                isGPSOn();
            }
        }

    }

    private void createDialogYes(int title, int message, int YES){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
                    }
                }).show();
    }

    private void startPlayServices(){
         mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                 .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void isGPSOn(){
        LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.importante)
                    .setMessage("Seu GPS está desativado. Ele é importante para que possamos ajudar pets perdidos. " +
                            "Gostaria de ativa-lo agora?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton(R.string.no, null);
            dialog.show();

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

    private RetrofitConfig createRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BASE_URL_TESTE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitConfig service = retrofit.create(RetrofitConfig.class);
        return service;
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
                bundle.putInt("tipo", 0);
                i.putExtras(bundle);
                startActivityForResult(i, 0);
            }

            @Override
            public void onClickDoacao(View view, final int index) {
                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(getContext());
                alert.setTitle(getContext().getResources().getString(R.string.selecione_quantidade));
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RegistrarSolicitacao solicitacao = new RegistrarSolicitacao();
                        Log.i("TAG","USUARIO: " +  new LocalDatabase(getContext()).buscarIDUsuario());
                        solicitacao.setFb_id_solicitante(new LocalDatabase(getContext()).buscarIDUsuario());
                        solicitacao.setFk_medicamento_solicitado(listaMedicamentos.get(index).getPk_mmd());
                        solicitacao.setLatitude(0);
                        solicitacao.setLongitude(0);
                        solicitacao.setNr_valor(0);
                        solicitacao.setQtd_solicitado(Integer.parseInt(input.getText().toString()));
                        String metodo = "SP_REGISTRAR_SOLICITACAO";
                        String gson = new Gson().toJson(solicitacao);

                        PostMethod postMethod = new PostMethod(gson, metodo, getContext(), new PostMethod.Callback() {
                            @Override
                            public void run(String result) {
                                if (TIPO_ACESSO != 1) {
                                    taskMedicamentos(currentPage);
                                } else {
                                    taskMedicamentosFacebook(currentPage);
                                }

                                showtAutoDismissDialog(result);

                            }
                        }, "Solicitando...");
                        postMethod.execute();

                    }
                });
                alert.setNegativeButton(getContext().getResources().getString(R.string.voltar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        };
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

    public void updateLocation(Context context) {

        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                       latitude = location.latitude;
                       longitude = location.longitude;

                    }
                });
    }


}
