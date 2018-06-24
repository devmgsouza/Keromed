package service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import br.com.projetoa.model.Usuario;

/**
 * Created by SOA - Development on 01/02/2018.
 */

public class LocalDatabase {
    private SQLiteDatabase db;
    private CreateBase nb;


    public LocalDatabase(Context context){
        nb = new CreateBase(context);
    }

    public void registarLogin(String usuarioGson){
        removerLogin();
        ContentValues values = new ContentValues();
        db = nb.getWritableDatabase();
        values.put("GSON_USER", usuarioGson);
        db.insert("USER_CACHE", null, values);
        db.close();
    }
    public void removerLogin(){
        db = nb.getWritableDatabase();
        db.delete("USER_CACHE", "PK_USER >= 0", null);
        db.close();
    }
    public String buscarLogin() {
        String usuario = "";
        db = nb.getReadableDatabase();
        Cursor cursor;
        String[] campos = {"PK_USER", "GSON_USER"};
        String where = "PK_USER >= 0";
        cursor = db.query("USER_CACHE", campos, where, null, null, null, null, null);
        if (cursor.moveToNext() == true) {
            cursor.moveToFirst();
            usuario = cursor.getString(cursor.getColumnIndexOrThrow("GSON_USER"));
        }

        return usuario;
    }

    public String buscarIDUsuario() {
        String usuario = "";
        db = nb.getReadableDatabase();
        Cursor cursor;
        String[] campos = {"PK_USER", "GSON_USER"};
        String where = "PK_USER >= 0";
        cursor = db.query("USER_CACHE", campos, where, null, null, null, null, null);
        if (cursor.moveToNext() == true) {
            cursor.moveToFirst();
            usuario = cursor.getString(cursor.getColumnIndexOrThrow("GSON_USER"));
        }
        Usuario u = new Gson().fromJson(usuario, Usuario.class);
        return u.getFb_id();
    }






    public void atualizarRegistro(SettingsModel setting
                                  ) {
        apagarTodosOsRegistrosLogin();

        ContentValues values = new ContentValues();

        db = nb.getWritableDatabase();

        values.put("distancia_maxima", setting.getDistancia_maxima());
        values.put("horario_inicio",setting.getHorario_inicio());
        values.put("horario_final", setting.getHorario_final());
        values.put("dom", setting.getDom());
        values.put("seg", setting.getSeg());
        values.put("ter", setting.getTer());
        values.put("qua", setting.getQua());
        values.put("qui", setting.getQui());
        values.put("sex", setting.getSex());
        values.put("sab", setting.getSab());
        values.put("text_nm_rua", setting.getNome_rua());
        values.put("text_nr_residencia", setting.getNumero_residencia());
        values.put("text_cidade", setting.getCidade());
        values.put("text_uf", setting.getText_uf());
        values.put("text_nr_fone", setting.getNr_fone());



        db.insert("LOGIN_CACHE", null, values);
        db.close();

    }
    public void apagarTodosOsRegistrosLogin(){
        String where = "PK_LOGIN > 0";
        db = nb.getReadableDatabase();
        db.delete("LOGIN_CACHE", where, null);
        db.close();
    }
    public SettingsModel loadSettings() {
        SettingsModel setting = new SettingsModel();
        Cursor cursor;
        //text_nm_rua text, text_nr_residencia text, text_cidade text, text_uf text, text_nr_fone text
        String[] campos = {"distancia_maxima", "horario_inicio", "horario_final", "dom",
                "seg", "ter", "qua", "qui", "sex", "sab", "text_nm_rua", "text_nr_residencia",
        "text_cidade", "text_uf", "text_nr_fone"};
        String where = "pk_login > 0";
        db = nb.getReadableDatabase();

        cursor = db.query("LOGIN_CACHE", campos, where, null, null, null, null, null);
        if (cursor.moveToNext() == true) {
            cursor.moveToFirst();

            setting.setDistancia_maxima(cursor.getInt(cursor.getColumnIndexOrThrow("distancia_maxima")));
            setting.setHorario_inicio(cursor.getString(cursor.getColumnIndexOrThrow("horario_inicio")));
            setting.setHorario_final(cursor.getString(cursor.getColumnIndexOrThrow("horario_final")));
            setting.setDom(cursor.getInt(cursor.getColumnIndexOrThrow("dom")));
            setting.setSeg(cursor.getInt(cursor.getColumnIndexOrThrow("seg")));
            setting.setTer(cursor.getInt(cursor.getColumnIndexOrThrow("ter")));
            setting.setQua(cursor.getInt(cursor.getColumnIndexOrThrow("qua")));
            setting.setQui(cursor.getInt(cursor.getColumnIndexOrThrow("qui")));
            setting.setSex(cursor.getInt(cursor.getColumnIndexOrThrow("sex")));
            setting.setSab(cursor.getInt(cursor.getColumnIndexOrThrow("sab")));
            setting.setNome_rua(cursor.getString(cursor.getColumnIndexOrThrow("text_nm_rua")));
            setting.setNumero_residencia(cursor.getString(cursor.getColumnIndexOrThrow("text_nr_residencia")));
            setting.setCidade(cursor.getString(cursor.getColumnIndexOrThrow("text_cidade")));
            setting.setText_uf(cursor.getString(cursor.getColumnIndexOrThrow("text_uf")));
            setting.setNr_fone(cursor.getString(cursor.getColumnIndexOrThrow("text_nr_fone")));


        }

        return setting;
    }

    private class CreateBase extends SQLiteOpenHelper {
        private static final String NOME_BANCO = "config.db";
        private static final int VERSAO = 8;

        public CreateBase(Context context) {
            super(context, NOME_BANCO, null, VERSAO);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL = "CREATE TABLE USER_CACHE(PK_USER integer primary key autoincrement, " +
                    " GSON_USER TEXT)";
            db.execSQL(SQL);

            SQL = "CREATE TABLE LOGIN_CACHE(PK_LOGIN integer primary key autoincrement, " +
                    "distancia_maxima integer, horario_inicio text, horario_final text, " +
                    "dom integer, seg integer, ter integer, qua integer, qui integer, sex integer, sab integer, " +
                    "text_nm_rua text, text_nr_residencia text, text_cidade text, text_uf text, text_nr_fone text)";
            db.execSQL(SQL);
            ContentValues values = new ContentValues();

            SettingsModel s = new SettingsModel();
            s.setDistancia_maxima(50);
            s.setHorario_inicio("08:00");
            s.setHorario_final("19:00");
            s.setDom(0);
            s.setSeg(1);
            s.setTer(1);
            s.setQua(1);
            s.setQui(1);
            s.setSex(1);
            s.setSab(0);


            values.put("distancia_maxima", s.getDistancia_maxima());
            values.put("horario_inicio",s.getHorario_inicio());
            values.put("horario_final", s.getHorario_final());
            values.put("dom", s.getDom());
            values.put("seg", s.getSeg());
            values.put("ter", s.getTer());
            values.put("qua", s.getQua());
            values.put("qui", s.getQui());
            values.put("sex", s.getSex());
            values.put("sab", s.getSab());
            db.insert("LOGIN_CACHE", null, values);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {


                db.execSQL("DROP TABLE IF EXISTS " + "LOGIN_CACHE");
                onCreate(db);
                db.execSQL("DROP TABLE IF EXISTS " + "USER_CACHE");
                onCreate(db);
            }

        }

    }
}
