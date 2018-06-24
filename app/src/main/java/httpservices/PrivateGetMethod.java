package httpservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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

public class PrivateGetMethod extends AsyncTask<String, Void, String> {
    private String parametro;
    private Context context;

    public PrivateGetMethod(String parametro, Context context){
        this.parametro = parametro;
        this.context = context;
    }


    @Override
    protected String doInBackground(String... params) {

        //String url = "http://ec2-18-231-108-89.sa-east-1.compute.amazonaws.com:8080/WebservicePjA/rest/resources/" + parametro;
        String url = "http://31.220.55.46:8080/webservicepja/rest/resources/" + parametro;
        //String url = context.getResources().getString(R.string.server) + parametro;
        String json = "";

        try {
            // Cria um objeto HttpURLConnection:
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();

            try {
                request.setRequestMethod("GET");
                request.setConnectTimeout(5000);
                request.setReadTimeout(5000);
                request.connect();
                InputStream is = request.getInputStream();

                json = getStringFromInputStream(is);
                is.close();

            } finally {
                request.disconnect();
            }

        } catch (IOException ex) {


            json = "ERRO";
        }


        return json;
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







}
