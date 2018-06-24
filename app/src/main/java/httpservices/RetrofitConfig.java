package httpservices;


import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoa.model.AtualizarStatusDoacao;
import br.com.projetoa.model.BuscarDoacoes;
import br.com.projetoa.model.Medicamento;
import br.com.projetoa.model.MedicamentosDisponiveis;
import br.com.projetoa.model.Usuario;
import br.com.projetoa.model.UsuarioIndependente;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by SOA - Development on 20/04/2018.
 */

public interface RetrofitConfig {
     //String BASE_URL_TESTE = "http://http://31.220.55.46:8080/webservicepja/rest/resources/";
     String BASE_URL_TESTE = "http://31.220.55.46:8080/webservicepja/rest/resources/";
    /*
            // METODOS GET //
     */

    @GET("SP_BUSCAR_DOACOES")
    Call<List<BuscarDoacoes>> SP_BUSCAR_DOACOES(@Query("id") String fb_id, @Query("parametro") int parametro);

    @GET("SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS_GERAL")
    Call<List<MedicamentosDisponiveis>> SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS_GERAL(
            @Query("distancia")  int max, @Query("latitude") double latitude, @Query("longitude") double longitude,
            @Query("page") int lastid, @Query("id") String id);


    @GET("SP_BUSCAR_DADOS_USUARIO")
    Call<Usuario> SP_BUSCAR_DADOS_USUARIO(@Query("id") String fb_id, @Query("email")String email);

    @GET("SP_BUSCAR_MEDICAMENTO_CODBARRAS")
    Call<Medicamento> SP_BUSCAR_MEDICAMENTO_CODBARRAS(@Query("barcode")String barcode);

    @GET("SP_BUSCAR_MEDICAMENTO_CONTEXTUAL")
    Call<List<Medicamento>> SP_BUSCAR_MEDICAMENTO_CONTEXTUAL(@Query("contexto") String contexto);

    @GET("SP_BUSCAR_USUARIO")
    Call<String> SP_BUSCAR_USUARIO(@Query("id") String id);

    @GET("SP_BUSCAR_USUARIOIN")
    Call<List<UsuarioIndependente>> SP_BUSCAR_USUARIOIN(@Query("id")String id);

    @GET("SP_BUSCAR_MEUS_ITENS")
    Call<List<MedicamentosDisponiveis>> SP_BUSCAR_MEUS_ITENS(@Query("id")String id);




    /*
            // METODOS POST //
     */

    @POST("SP_EFETUAR_LOGIN")
    Call<Integer> SP_EFETUAR_LOGIN(@Body Usuario u);


    @POST("SP_ATUALIZAR_STATUS_DOACAO")
    Call<String> SP_ATUALIZAR_STATUS_DOACAO(@Body AtualizarStatusDoacao a);

    @POST("SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS")
    Call<List<MedicamentosDisponiveis>> SP_BUSCAR_MEDICAMENTOS_DISPONIVEIS(
            @Body List<String> arg0, @Query("distancia") int distancia,
            @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("page") int lasid);

    @POST("SP_CADASTRAR_MEDICAMENTO")
    Call<String> SP_CADASTRAR_MEDICAMENTO(@Body Medicamento m);

    @Headers({"Content-type: text/html"})
    @DELETE("SP_EXCLUIR_MEDICAMENTO")
    Call<Boolean> SP_EXCLUIR_MEDICAMENTO(@Query("id") String id, @Query("pk") int pk);
}
