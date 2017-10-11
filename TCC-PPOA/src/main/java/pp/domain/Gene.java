package pp.domain;


import java.io.Serializable;
import java.math.BigDecimal;

/*
 *  UNIVERSIDADE FEDERAL DE JUIZ DE FORA
 *  CRIADO: 22/02/2011
 *  MODIFICADO: 22/02/2011
 *  AUTOR: Bruno Augusto Clemente de Assis
 *  CLASSE: Gene
 *  FUNÇÃO: Atribui o valor a um determinado Gene do cromossomo.
 * 
 */


public class Gene implements Serializable{
	private BigDecimal valor;
	
	public Gene(double valor){
            this.valor = BigDecimal.valueOf(valor);
	}
	
	public void replaceValor(double valor) {
            this.valor = BigDecimal.valueOf(valor);
	}

        public void addValor(double valor) {
            this.valor = this.valor.add(BigDecimal.valueOf(valor));
        }

        public void subtValor(double valor) {
            this.valor = this.valor.subtract(BigDecimal.valueOf(valor));
        }

        public double getValor() {
            return this.valor.doubleValue();
	}



}
