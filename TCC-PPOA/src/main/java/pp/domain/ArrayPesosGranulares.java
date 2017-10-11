/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp.domain;

import java.math.BigDecimal;

/**
 *
 * @author jairo
 */
public class ArrayPesosGranulares {

    private double granularidade;

    public ArrayPesosGranulares(double granularidade) {
        this.granularidade = granularidade;
    }

    public int getTamanho() {
        return (int) (1 + (1/granularidade));
    }

    public BigDecimal getValor(int i) {
        return BigDecimal.valueOf(i).multiply(BigDecimal.valueOf(granularidade));
    }

    public int getRandomIndice() {
        return getRandomIndice(0);
    }

    public int getRandomIndice(float v) {
        return (int) Math.round(Math.random()*(this.getTamanho()-1-(v/this.granularidade)));
    }

}
