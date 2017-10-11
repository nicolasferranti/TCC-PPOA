/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp.domain;


import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author rooke
 */
public class FuncaoSimples implements IFuncao{

    private double peso;
    private double valor;

    public FuncaoSimples(double valor) {
        this.valor = valor;
    }

    public BigDecimal calcula(){
        return BigDecimal.valueOf(peso).multiply(BigDecimal.valueOf(valor));
    }

    public double getPeso(){
        return peso;
    }

    public void setPeso(double pesoParam) {
        peso = pesoParam;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public List<IFuncao> getFilhos() {
        return null;
    }

}
