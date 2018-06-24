package br.com.soasd.projetoa;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import br.com.projetoa.model.AtualizarStatusDoacao;
import br.com.projetoa.model.BuscarDoacoes;

import httpservices.PostMethod;
import service.LocalDatabase;

public class DialogDoacao extends AppCompatActivity {
    ImageView imageViewQrCode;
    TextView textViewNomeComercial, textViewQtdSolicitada, textViewStatus, textViewDataSolicitacao,
            textViewDataTroca, textViewCodigo, textViewCodigoValidadorLabel, textViewOrientacao,
    textViewLocalTroca, textViewOrientacoes, textViewOrientacaoLabel;
    Button buttonCancelar, buttonAlterar, buttonAlterarQtd;
    private int buttonAction; //0 - Pendente, 1 -Disponibilizao, 2 - Concluido, 3 - Cancelado
    private String meuFbId;
    BuscarDoacoes medicamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_doacao);
        Bundle bundle = getIntent().getExtras();
        String classe = bundle.getString("medicamento");

        meuFbId = new LocalDatabase(this).buscarIDUsuario();
        carregarViews();

        medicamento = new Gson().fromJson(classe, BuscarDoacoes.class);
        buttonTitle(medicamento.getStatus_troca());



        if (medicamento.getData_troca_efetivada().equals("")) {
            medicamento.setData_troca_efetivada("Aguardando...");
        }


        textViewNomeComercial.setText(medicamento.getText_nome_comercial());
        textViewQtdSolicitada.setText(medicamento.getQtd_solicitado() + " " + medicamento.getTipo_medicamento() + "(s)");
        textViewStatus.setText(medicamento.getStatus_troca());
        textViewDataSolicitacao.setText(medicamento.getData_solicitacao());
        textViewDataTroca.setText(medicamento.getData_troca_efetivada());

    }



    private void carregarViews(){
        textViewNomeComercial = (TextView)findViewById(R.id.textViewNomeComercial);
        textViewQtdSolicitada = (TextView)findViewById(R.id.textViewQtdSolicitada);
        textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        textViewDataSolicitacao = (TextView)findViewById(R.id.textViewDataSolicitacao);
        textViewDataTroca = (TextView)findViewById(R.id.textViewDataTroca);
        textViewCodigo = (TextView)findViewById(R.id.textViewCodigo);
        textViewCodigoValidadorLabel = (TextView)findViewById(R.id.textViewCodigoValidadorLabel);
        textViewOrientacao = (TextView)findViewById(R.id.textViewOrientacao);
        textViewOrientacoes = (TextView)findViewById(R.id.textViewOrientacoes);
        textViewLocalTroca = (TextView)findViewById(R.id.textViewLocalTroca);
        textViewOrientacaoLabel = (TextView)findViewById(R.id.textViewOrientacaoLabel);

        buttonAlterar = (Button)findViewById(R.id.buttonAlterar);
        buttonCancelar = (Button)findViewById(R.id.buttonCancelar);
        buttonAlterarQtd = (Button)findViewById(R.id.buttonAlterarQtd);

        imageViewQrCode = findViewById(R.id.imageViewQrCode);



    }

    private void buttonTitle(String status){
        switch (status) {
            case "PENDENTE":
                    buttonAlterarQtd.setVisibility(View.GONE);
                    textViewCodigo.setVisibility(View.GONE);
                    textViewCodigoValidadorLabel.setVisibility(View.GONE);
                    textViewOrientacao.setVisibility(View.GONE);
                    if(meuFbId.equals(medicamento.getFb_id_doador())) {
                        buttonAlterarQtd.setVisibility(View.VISIBLE);
                        buttonAlterar.setText("Disponibilizar");
                        buttonAction = 0;
                    } else {
                        buttonAlterar.setVisibility(View.GONE);
                    }
                break;
            case "CANCELADO":
                textViewCodigo.setVisibility(View.GONE);
                textViewCodigoValidadorLabel.setVisibility(View.GONE);
                textViewOrientacao.setVisibility(View.GONE);
                buttonCancelar.setVisibility(View.GONE);
                buttonAlterar.setVisibility(View.GONE);
                textViewDataTroca.setVisibility(View.GONE);
                buttonAlterarQtd.setVisibility(View.GONE);
                break;
            case "CONCLUIDO":
                textViewCodigo.setVisibility(View.GONE);
                textViewCodigoValidadorLabel.setVisibility(View.GONE);
                textViewOrientacao.setVisibility(View.GONE);
                buttonCancelar.setVisibility(View.GONE);
                buttonAlterar.setVisibility(View.GONE);
                buttonAlterarQtd.setVisibility(View.GONE);
                textViewOrientacao.setVisibility(View.GONE);
                textViewDataTroca.setVisibility(View.VISIBLE);
                textViewOrientacaoLabel.setVisibility(View.VISIBLE);
                textViewOrientacaoLabel.setText("Data da troca:");
                break;
            case "DISPONIBILIZADO":
                buttonAlterarQtd.setVisibility(View.GONE);
                if(meuFbId.equals(medicamento.getFb_id_doador())) {
                    buttonAlterar.setText("Finalizar");
                    buttonAction = 1;
                    textViewOrientacao.setText(getResources().getString(R.string.orientacao_finalizar_doacao));
                } else {
                    textViewCodigo.setEnabled(false);
                    textViewCodigo.setText(medicamento.getCodigo_validador());
                    imageViewQrCode.setImageBitmap(getQRCode(medicamento.getCodigo_validador()));
                    //imageViewQrCode.setVisibility(View.VISIBLE);
                    buttonAlterar.setVisibility(View.GONE);
                    textViewOrientacoes.setVisibility(View.VISIBLE);
                    textViewOrientacaoLabel.setVisibility(View.VISIBLE);
                    textViewLocalTroca.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void finalizarDoacao(){
        AtualizarStatusDoacao statusDoacao = new AtualizarStatusDoacao();

        statusDoacao.setCodigo_validador(medicamento.getCodigo_validador());
        statusDoacao.setQtd_doado(medicamento.getQtd_solicitado());
        statusDoacao.setStatus_troca(2); //TROCA FINALIZADA
        String metodo = "SP_ATUALIZAR_STATUS_DOACAO";
        String gson = new Gson().toJson(statusDoacao);
        PostMethod postMethod = new PostMethod(gson, metodo, DialogDoacao.this, new PostMethod.Callback() {
            @Override
            public void run(String result) {
                Toast.makeText(DialogDoacao.this, result, Toast.LENGTH_LONG).show();
                finish();
            }
        }, getResources().getString(R.string.finalizando));
        postMethod.execute();
    }

    private void disponibilizarMedicamento(){
        AtualizarStatusDoacao statusDoacao = new AtualizarStatusDoacao();

        statusDoacao.setCodigo_validador(medicamento.getCodigo_validador());
        statusDoacao.setQtd_doado(medicamento.getQtd_solicitado());
        statusDoacao.setStatus_troca(1); //DISPONIBILIZADO
        String metodo = "SP_ATUALIZAR_STATUS_DOACAO";
        String gson = new Gson().toJson(statusDoacao);
        PostMethod postMethod = new PostMethod(gson, metodo, DialogDoacao.this, new PostMethod.Callback() {
            @Override
            public void run(String result) {
                Toast.makeText(DialogDoacao.this, result, Toast.LENGTH_LONG).show();
                setResult(100);
                finish();
            }
        }, getResources().getString(R.string.atualizando));
        postMethod.execute();
    }

    public void buttonCancelar(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getResources().getString(R.string.deseja_cancelar));
        alert.setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AtualizarStatusDoacao statusDoacao = new AtualizarStatusDoacao();

                statusDoacao.setCodigo_validador(medicamento.getCodigo_validador());
                statusDoacao.setQtd_doado(medicamento.getQtd_solicitado());
                statusDoacao.setStatus_troca(3); //Cancelado
                String metodo = "SP_ATUALIZAR_STATUS_DOACAO";
                String gson = new Gson().toJson(statusDoacao);

                PostMethod postMethod = new PostMethod(gson, metodo, DialogDoacao.this, new PostMethod.Callback() {
                    @Override
                    public void run(String result) {
                        setResult(100);
                        Toast.makeText(DialogDoacao.this, result, Toast.LENGTH_LONG).show();

                        finish();
                    }
                }, getResources().getString(R.string.atualizando));
                postMethod.execute();

            }
        }).setNegativeButton(getResources().getString(R.string.no), null).show();

    }

    public void buttonAlterar(View view) {
        if (buttonAction == 0){
            disponibilizarMedicamento();

        } else if (buttonAction == 1) {
            String codigoVerificador = textViewCodigo.getText().toString();
            if (medicamento.getCodigo_validador().equals(codigoVerificador)) {
                finalizarDoacao();

            }

        }
    }

    public void sair(View view){
        finish();
    }

    public void alterarQuantidade(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(this.getResources().getString(R.string.selecione_quantidade));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            medicamento.setQtd_solicitado(Integer.parseInt(input.getText().toString()));
                textViewQtdSolicitada.setText(medicamento.getQtd_solicitado() + " " + medicamento.getTipo_medicamento() + "(s)");
            }
        });
        alert.setNegativeButton(this.getResources().getString(R.string.voltar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    public void localTrocaMaps(View view){


        String latlon = medicamento.getLatitude() + "," + medicamento.getLongitude();
        String uriString = "geo:0,0?q=" + latlon + "O doador definiu este local para troca:";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri );
        //intent.setPackage("com.google.android.apps.maps");
        startActivity( intent );
    }


    private Bitmap getQRCode(String content) {
        Bitmap bmp = null;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }


        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bmp;

    }

}


