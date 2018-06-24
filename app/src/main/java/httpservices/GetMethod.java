package httpservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import br.com.soasd.projetoa.R;

/**
 * Created by Marcio on 26/07/2017.
 */

public class GetMethod extends AsyncTask<String, Void, String> {
    String parametro;
    Context context;
    Callback callback;
    ProgressDialog pdia;


    public GetMethod (String parametro, Context context, Callback callback){
        this.parametro = parametro;
        this.callback = callback;
        pdia = new ProgressDialog(context);
    }

    @Override
    protected String doInBackground(String... params) {

        //String url = "http://ec2-18-231-108-89.sa-east-1.compute.amazonaws.com:8080/WebservicePjA/rest/resources/" + parametro;
        String url = "http://31.220.55.46:8080/webservicepja/rest/resources/" + parametro;
        //String url = context.getResources().getString(R.string.server) + parametro;

        String json = "";
        Log.i("TAG", url);
        try {
            // Cria um objeto HttpURLConnection:
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();

            try {
                request.setRequestMethod("GET");
                request.setConnectTimeout(15000);
                request.setReadTimeout(15000);
                request.setRequestProperty("Content-Type", "application/json");
                request.connect();
                int responceCode = request.getResponseCode();


            if (responceCode == HttpURLConnection.HTTP_OK) {

            } else {

            }



               json = getStringFromInputStream(request.getInputStream());

            } finally {
                request.disconnect();
            }

        } catch (IOException ex) {




        }


        return json;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pdia.setMessage("Pesquisando...");
        pdia.show();

    }

    @Override
    protected void onPostExecute(String resultado){
        callback.run(resultado);
        pdia.dismiss();
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public interface Callback {
        void run(String result);
    }





}
