package service;

import Fragments.Settings;

/**
 * Created by SOA - Development on 01/02/2018.
 */

public class SettingsModel {
    private String fb_id = "";
    private int distancia_maxima;
    private String horario_inicio = "";
    private String horario_final = "";
    private String cidade = "";
    private String nome_rua = "";
    private String numero_residencia = "";
    private String text_uf = "";
    private String nr_fone = "";
    private int dom, seg, ter, qua, qui, sex, sab;


    public  SettingsModel(){
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public int getDistancia_maxima() {
        return distancia_maxima;
    }

    public void setDistancia_maxima(int distancia_maxima) {
        this.distancia_maxima = distancia_maxima;
    }

    public String getHorario_inicio() {
        return horario_inicio;
    }

    public void setHorario_inicio(String horario_inicio) {
        this.horario_inicio = horario_inicio;
    }

    public String getHorario_final() {
        return horario_final;
    }

    public void setHorario_final(String horario_final) {
        this.horario_final = horario_final;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getNome_rua() {
        return nome_rua;
    }

    public void setNome_rua(String nome_rua) {
        this.nome_rua = nome_rua;
    }

    public String getNumero_residencia() {
        return numero_residencia;
    }

    public void setNumero_residencia(String numero_residencia) {
        this.numero_residencia = numero_residencia;
    }

    public String getText_uf() {
        return text_uf;
    }

    public void setText_uf(String text_uf) {
        this.text_uf = text_uf;
    }

    public String getNr_fone() {
        return nr_fone;
    }

    public void setNr_fone(String nr_fone) {
        this.nr_fone = nr_fone;
    }

    public int getDom() {
        return dom;
    }

    public void setDom(int dom) {
        this.dom = dom;
    }

    public int getSeg() {
        return seg;
    }

    public void setSeg(int seg) {
        this.seg = seg;
    }

    public int getTer() {
        return ter;
    }

    public void setTer(int ter) {
        this.ter = ter;
    }

    public int getQua() {
        return qua;
    }

    public void setQua(int qua) {
        this.qua = qua;
    }

    public int getQui() {
        return qui;
    }

    public void setQui(int qui) {
        this.qui = qui;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSab() {
        return sab;
    }

    public void setSab(int sab) {
        this.sab = sab;
    }
}
