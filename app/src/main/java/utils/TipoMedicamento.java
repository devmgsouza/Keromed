package utils;

/**
 * Created by SOA - Development on 11/04/2018.
 */

public enum TipoMedicamento {

    FRASCO(1), COMPRIMIDO(2), POMADA(3), CAIXA(4), FLACONETE(5), CARTELA(6);


     private int valor;

     TipoMedicamento(int valor){
        this.valor = valor;
    }

    public int getValor(){
        return this.valor;
    }

}
