package service;





import android.util.Log;

import com.facebook.AccessToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import br.com.projetoa.model.Usuario;
import br.com.projetoa.model.exception.UsuarioException;
import httpservices.PostMethod;


/**
 * Created by Marcio on 29/11/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
      AtualizarToken(FirebaseInstanceId.getInstance().getToken());

    }


    private void AtualizarToken(String token){

        if (isLoggedIn()) {
            Usuario usuario = new Usuario();
            usuario.setFb_id(AccessToken.getCurrentAccessToken().getUserId());
            usuario.setToken_gcm(token);
            try {
                new ExternalDatabase(this).SP_ATUALIZAR_TOKEN_FCM(usuario);
            } catch (UsuarioException e) {
                e.printStackTrace();
            }
        } else {
           // Log.i("TAG", "USERID = " + new LocalDatabase(getApplicationContext()).buscarIDUsuario());
        }

    }


    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


}
