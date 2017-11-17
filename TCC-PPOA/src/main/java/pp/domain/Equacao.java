/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author rooke
 */
public class Equacao {

    private IFuncao funcao;

    public Equacao() {
    }

    public Equacao(IFuncao funcao) {
        this.funcao = funcao;
    }

    public IFuncao getFuncao() {
        return funcao;
    }

    public void setFuncao(IFuncao funcao) {
        this.funcao = funcao;
    }

    public int numeroDeFuncoes() {
        return numeroDeFuncoes(funcao) - 1;
    }

    /*
     * Faz percurso pos-ordem e vai somando para saber o numero de fun��es
     */
    private int numeroDeFuncoes(IFuncao funcaoParam) {
        int retorno = 0;
        if (funcaoParam instanceof FuncaoComposta) {
            retorno++;
            for (Iterator<IFuncao> it = ((FuncaoComposta) funcaoParam).iterator(); it.hasNext();) {
                IFuncao funcaoFilha = it.next();
                retorno = retorno + numeroDeFuncoes(funcaoFilha);
            }
            return retorno;
        } else {
            return 1;
        }
    }

    /*
     * Passa um cromossomo e ele vai preenchendo a fun��o colocando os genes no lugar dos
     * pesos num percurso pos-ordem
     */
    public void setPesos(Cromossomo cro) {
        ArrayList<Gene> genes = (ArrayList<Gene>) cro.genes.clone();

        //Como a primeira n�o tem que passar gene, passaremos apenas para os filhaos dela
        for (Iterator<IFuncao> it = ((FuncaoComposta) funcao).iterator(); it.hasNext();) {
            IFuncao funcaoFilha = it.next();
            setPesos(funcaoFilha, genes);
        }
        ((FuncaoComposta) funcao).setPeso(1);
    }

    private void setPesos(IFuncao funcaoParam, ArrayList<Gene> genes) {
        if (funcaoParam instanceof FuncaoComposta) {
            for (Iterator<IFuncao> it = ((FuncaoComposta) funcaoParam).iterator(); it.hasNext();) {
                IFuncao funcaoFilha = it.next();
                setPesos(funcaoFilha, genes);
            }
            ((FuncaoComposta) funcaoParam).setPeso(genes.get(0).getValor());
            genes.remove(0);
        } else {
            ((FuncaoSimples) funcaoParam).setPeso(genes.get(0).getValor());
            genes.remove(0);
        }
    }

    public static ArrayList<IFuncao> getEquacoesComPesos(ArrayList<IFuncao> equacoes, Cromossomo c) {
        Equacao equacaoAux;
        ArrayList<IFuncao> novasEquacoes = new ArrayList<>();
        for (int i = 0; i < equacoes.size(); i++) {
            equacaoAux = new Equacao(equacoes.get(i));
            equacaoAux.setPesos(c);
            novasEquacoes.add(equacaoAux.getFuncao());
        }
        return novasEquacoes;
        
    }

}
