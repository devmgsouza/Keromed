package Fragments;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import br.com.projetoa.model.Usuario;
import br.com.sapereaude.maskedEditText.MaskedEditText;
import br.com.soasd.projetoa.MainActivity;
import br.com.soasd.projetoa.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import httpservices.BuscarCEP;
import httpservices.PostMethod;
import service.LocalDatabase;
import service.SettingsModel;


public class Settings extends BaseFragment {
    int retornoActivity = 100;
    private int seekMIN = 1;

    @BindView(R.id.textViewDistancia)
    TextView textViewDistancia;
    @BindView(R.id.editTextHoraInicio)
    EditText editTextHoraInicio;
    @BindView(R.id.editTextHoraFim)
    EditText editTextHoraFim;
    @BindView(R.id.editTextCidade)
    EditText editTextCidade;
    @BindView(R.id.editTextRua)
    EditText editTextRua;
    @BindView(R.id.editTextFone)
    MaskedEditText editTextFone;
    @BindView(R.id.editTextNumero)
    EditText editTextNumero;
    @BindView(R.id.buttonPesquisarCep)
    TextView buttonPesquisarCep;
    @BindView(R.id.editTextCep)
    EditText editTextCep;

    @BindView(R.id.cbDom)
    CheckBox cbDom;
    @BindView(R.id.cbSeg)
    CheckBox cbSeg;
    @BindView(R.id.cbTer)
    CheckBox cbTer;
    @BindView(R.id.cbQua)
    CheckBox cbQua;
    @BindView(R.id.cbQui)
    CheckBox cbQui;
    @BindView(R.id.cbSex)
    CheckBox cbSex;
    @BindView(R.id.cbSab)
    CheckBox cbSab;

    @BindView(R.id.spinnerUf)
    Spinner spinnerUf;
    @BindView(R.id.textButtonEditar)
    TextView textButtonEditar;
    @BindView(R.id.textButtonSalvar)
    TextView textButtonSalvar;
    @BindView(R.id.textButtonEditarAjustes)
    TextView textButtonEditarAjustes;
    @BindView(R.id.textButtonSalvarAjustes)
    TextView textButtonSalvarAjustes;
    @BindView(R.id.linearLayoutDiaSemana)
    LinearLayout linearLayoutDiaSemana;
    @BindView(R.id.seekBarDistancia)
    SeekBar seekBarDistancia;

    private int buttonEditar = 0;
    private int buttonEditarAjustes = 0;

    private Calendar myCalendar = Calendar.getInstance();
    ArrayAdapter adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
          loadSettings();
          loadUser();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).updateToolbarTitle("Configurações");
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.uf_brasil,
                android.R.layout.simple_spinner_dropdown_item);
        spinnerUf.setAdapter(adapter);
        seekBarDistancia.setMax(300);

        spinnerUf.setEnabled(false);
        loadSettings();
        loadUser();
        seekBarDistancia.setEnabled(false);
        seekBarDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                textViewDistancia.setText(i + "Km");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() < seekMIN) {
                    seekBar.setProgress(seekMIN);
                    textViewDistancia.setText(seekBar.getProgress() + "Km");
                }

            }
        });

        editTextHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarHora(1);
            }
        });

        editTextHoraFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarHora(2);
            }
        });

        buttonPesquisarCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarCep();
            }
        });

        textButtonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonEditar == 0) {
                    textButtonSalvar.setVisibility(View.VISIBLE);
                    textButtonEditar.setText(getResources().getString(R.string.cancel));
                    textButtonSalvar.setText(getResources().getString(R.string.salvar));
                    editTextCep.setEnabled(true);
                    editTextCidade.setEnabled(true);
                    editTextFone.setEnabled(true);
                    editTextNumero.setEnabled(true);
                    editTextRua.setEnabled(true);
                    spinnerUf.setEnabled(true);
                    buttonPesquisarCep.setEnabled(true);
                    buttonEditar = 1;
                } else {
                    textButtonSalvar.setVisibility(View.GONE);
                    textButtonEditar.setText(getResources().getString(R.string.editar));
                    editTextCep.setEnabled(false);
                    editTextCidade.setEnabled(false);
                    editTextFone.setEnabled(false);
                    buttonPesquisarCep.setEnabled(false);
                    editTextNumero.setEnabled(false);
                    editTextRua.setEnabled(false);
                    spinnerUf.setEnabled(false);
                    buttonEditar = 0;
                }
            }
        });

        textButtonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textButtonSalvar.setVisibility(View.GONE);
                editTextCep.setEnabled(false);
                editTextCidade.setEnabled(false);
                editTextFone.setEnabled(false);
                editTextNumero.setEnabled(false);
                editTextRua.setEnabled(false);
                spinnerUf.setEnabled(false);
                saveUserLogin();
                textButtonEditar.setText(getResources().getString(R.string.editar));
                textButtonSalvar.setVisibility(View.GONE);
            }
        });

        textButtonSalvarAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                textButtonSalvarAjustes.setVisibility(View.GONE);
                textButtonEditarAjustes.setText(getResources().getString(R.string.editar));
                seekBarDistancia.setEnabled(false);
                spinnerUf.setEnabled(false);
                editTextHoraInicio.setEnabled(false);
                editTextHoraFim.setEnabled(false);
                for (int i = 0; i < linearLayoutDiaSemana.getChildCount(); i++) {
                    View child = linearLayoutDiaSemana.getChildAt(i);
                    child.setEnabled(false);
                }
                buttonEditarAjustes = 0;

            }
        });
        textButtonEditarAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonEditarAjustes == 0) {
                    textButtonSalvarAjustes.setVisibility(View.VISIBLE);
                    textButtonEditarAjustes.setText(getResources().getString(R.string.cancel));
                    spinnerUf.setEnabled(true);
                    editTextHoraInicio.setEnabled(true);
                    editTextHoraFim.setEnabled(true);
                    seekBarDistancia.setEnabled(true);
                    buttonEditarAjustes = 1;
                    for (int i = 0; i < linearLayoutDiaSemana.getChildCount(); i++) {
                        View child = linearLayoutDiaSemana.getChildAt(i);
                        child.setEnabled(true);
                    }
                } else {
                    textButtonSalvarAjustes.setVisibility(View.GONE);
                    textButtonEditarAjustes.setText(getResources().getString(R.string.editar));
                    spinnerUf.setEnabled(false);
                    editTextHoraInicio.setEnabled(false);
                    editTextHoraFim.setEnabled(false);
                    seekBarDistancia.setEnabled(false);
                    buttonEditarAjustes = 0;
                    for (int i = 0; i < linearLayoutDiaSemana.getChildCount(); i++) {
                        View child = linearLayoutDiaSemana.getChildAt(i);
                        child.setEnabled(false);
                    }
                }

            }
        });

      return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater menuInflater = ( (MainActivity)getActivity()).getMenuInflater();

        menuInflater.inflate(R.menu.menu_textview, menu);
        final MenuItem filtroItem = menu.findItem(R.id.textViewButton);
        AppCompatTextView buttonSair = null;
        if (filtroItem != null) {
            buttonSair = (AppCompatTextView) filtroItem.getActionView();
            buttonSair.setText("Sair");
            buttonSair.setTextColor(Color.WHITE);
            buttonSair.setTextSize(18);
            buttonSair.setBackgroundColor(Color.TRANSPARENT);
            buttonSair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logOut();
                    retornoActivity = 0;
                    new LocalDatabase(getContext()).removerLogin();
                    getActivity().setResult(444);
                    getActivity().finish();

                }
            });
        }

    }

    public void selecionarHora(final int param) {
            TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    String hora = String.format("%02d:%02d", hourOfDay, minute);
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalendar.set(Calendar.MINUTE, minute);

                    if (param == 1){
                        editTextHoraInicio.setText(hora);
                    } else if (param == 2) {
                        editTextHoraFim.setText(hora);
                    }
                }
            };
            new TimePickerDialog(getContext(), t,
                    myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                    true).show();


        }

    public void buscarCep(){
        String cep = editTextCep.getText().toString();
        new BuscarCEP(cep, getContext(), new BuscarCEP.Callback() {
            @Override
            public void run(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    editTextRua.setText(jsonObject.getString("logradouro"));
                    editTextCidade.setText(jsonObject.getString("localidade"));
                    int adapterPosition = adapter.getPosition(jsonObject.getString("uf"));
                    spinnerUf.setSelection(adapterPosition);
                    //DISMISS KEYBOARD AFTER SEARCH
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextCep.getWindowToken(), 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    private void loadSettings(){
        SettingsModel settings = new LocalDatabase(getContext()).loadSettings();


        seekBarDistancia.setProgress(settings.getDistancia_maxima());
        textViewDistancia.setText(settings.getDistancia_maxima() + "Km");
        editTextHoraInicio.setText(settings.getHorario_inicio());
        editTextHoraFim.setText(settings.getHorario_final());
        editTextCidade.setText(settings.getCidade());
        editTextRua.setText(settings.getNome_rua());
        editTextNumero.setText(settings.getNumero_residencia());
        int adapterPosition = adapter.getPosition(settings.getText_uf());
        spinnerUf.setSelection(adapterPosition);
        editTextFone.setText(settings.getNr_fone());

        //DOMINGO
        if (settings.getDom() == 1) {
            cbDom.setChecked(true);
        } else {
            cbDom.setChecked(false);
        }
        //SEGUNDA
        if (settings.getSeg() == 1) {
            cbSeg.setChecked(true);
        } else {
            cbSeg.setChecked(false);
        }
        //TERCA
        if (settings.getTer() == 1) {
            cbTer.setChecked(true);
        } else {
            cbTer.setChecked(false);
        }
        //QUARTA
        if (settings.getQua() == 1) {
            cbQua.setChecked(true);
        } else {
            cbQua.setChecked(false);
        }
        //QUINTA
        if (settings.getQui() == 1) {
            cbQui.setChecked(true);
        } else {
            cbQui.setChecked(false);
        }
        //SEXTA
        if (settings.getSex() == 1) {
            cbSex.setChecked(true);
        } else {
            cbSex.setChecked(false);
        }
        //SABADO
        if (settings.getSab() == 1) {
            cbSab.setChecked(true);
        } else {
            cbSab.setChecked(false);
        }




    }

    private void saveSettings(){
        Log.i("TAG", "salvando dados");
        SettingsModel s = new SettingsModel();
        if (cbDom.isChecked()) {
            s.setDom(1);
        } else {
            s.setDom(0);
        }
        if (cbSeg.isChecked()) {
            s.setSeg(1);
        } else {
            s.setSeg(0);
        }
        if (cbTer.isChecked()) {
            s.setTer(1);
        } else {
            s.setTer(0);
        }
        if (cbQua.isChecked()) {
            s.setQua(1);
        } else {
            s.setQua(0);
        }
        if (cbQui.isChecked()) {
            s.setQui(1);
        } else {
            s.setQui(0);
        }
        if (cbSex.isChecked()) {
            s.setSex(1);
        } else {
            s.setSex(0);
        }
        if (cbSab.isChecked()) {
            s.setSab(1);
        } else {
            s.setSab(0);
        }
        s.setDistancia_maxima(seekBarDistancia.getProgress());
        s.setHorario_inicio(editTextHoraInicio.getText().toString());
        s.setHorario_final(editTextHoraFim.getText().toString());
        s.setNome_rua(editTextRua.getText().toString());
        s.setNumero_residencia(editTextNumero.getText().toString());
        s.setCidade(editTextCidade.getText().toString());
        s.setText_uf(spinnerUf.getSelectedItem().toString());
        s.setNr_fone(editTextFone.getRawText());

        new LocalDatabase(getContext()).atualizarRegistro(s);
        showtAutoDismissDialog("Configurações atualizadas!");


    }

    private void saveUserLogin(){
        Usuario usuario = new Gson().fromJson(new LocalDatabase(getContext()).buscarLogin(), Usuario.class);

        usuario.setText_cidade(editTextCidade.getText().toString());
        usuario.setText_nm_rua(editTextRua.getText().toString());
        usuario.setText_nr_residencia(editTextNumero.getText().toString());
        usuario.setText_fone(editTextFone.getRawText());
        usuario.setText_uf(spinnerUf.getSelectedItem().toString());
        final String usuarioGson = new Gson().toJson(usuario);

        new PostMethod(usuarioGson, "SP_ATUALIZAR_USUARIO", getContext(), new PostMethod.Callback() {
            @Override
            public void run(String result) {
                if (result.equals("DADOS ATUALIZADOS")) {
                    new LocalDatabase(getContext()).registarLogin(usuarioGson);
                    showtAutoDismissDialog(result);
                } else {
                    showtAutoDismissDialog("Não foi possível realizar esta operação. Tente novamente em alguns minutos.");
                }
            }
        },"Atualizando seus dados pessoais...").execute();
    }


    private void loadUser(){
        Usuario u = new Gson().fromJson(new LocalDatabase(getContext()).buscarLogin(), Usuario.class);
        editTextCidade.setText(u.getText_cidade());
        editTextFone.setText(u.getText_fone());
        editTextNumero.setText(u.getText_nr_residencia());
        editTextRua.setText(u.getText_nm_rua());
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


}
