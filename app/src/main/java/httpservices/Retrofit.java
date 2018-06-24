package httpservices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.soasd.projetoa.R;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Retrofit {

    public static Retrofit instance = null;

    public Retrofit() {
    }

    public static RetrofitConfig createRetrofit(){


        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitConfig.BASE_URL_TESTE)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        RetrofitConfig service = retrofit.create(RetrofitConfig.class);

        return service;
    }


    public static Retrofit getInstance(){
        if (instance == null) {
            instance = new Retrofit();
        }
        return instance;
    }


}
