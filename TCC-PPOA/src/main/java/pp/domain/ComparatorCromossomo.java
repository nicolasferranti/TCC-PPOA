package pp.domain;
/*
 *  UNIVERSIDADE FEDERAL DE JUIZ DE FORA
 *  CRIADO: 22/02/2011
 *  MODIFICADO: 10/06/2011
 *  AUTOR: Bruno Augusto Clemente de Assis
 *  CLASSE: ComparatorCromossomo
 *  FUN��O: Realiza a compara��o de valores entre um m�todo de 2 cromossomos. (ORDENA)
 * 
 */

import java.math.BigDecimal;
import java.util.Comparator;

/************* Classe que efetua a ordena��o dos cromossomos **************/
public class ComparatorCromossomo implements Comparator<Cromossomo> {

    private boolean crescente = true;
    public static int tipoComparacao;

    public ComparatorCromossomo(boolean cres) {
        //cres = false;
        this.crescente = cres;
    }

    public static double getDiferenca(Cromossomo c1) {
        return tipoComparacao == 0 ? c1.getDiferenca().doubleValue() : c1.getDiferencaEquacoes().doubleValue();
    }

    public int compare(Cromossomo c1, Cromossomo c2) {

        if (tipoComparacao == 0) {
            if (!crescente)
                return c1.getDiferenca().compareTo(c2.getDiferenca());
            else
                return c2.getDiferenca().compareTo(c1.getDiferenca());
            
        } else {
            if (!crescente) {
                return -(c1.compareTo(c2));
            } else {
                return (c1.compareTo(c2));
            }
        }


    }
}
