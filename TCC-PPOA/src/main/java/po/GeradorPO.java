/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import pp.domain.ArrayPesosGranulares;
import pp.domain.Cromossomo;
import pp.domain.Equacao;
import pp.domain.IFuncao;

/**
 *
 * @author nicolasferranti
 */
public class GeradorPO {

    // VariÃ¡veis
    private int numeroDeGenes = 0;
    private int menorNumeroDeGenes = 0;
    private BigDecimal Ez = new BigDecimal(0); // somatÃ³rio das N funÃ§Ãµes.
    private double resultadoEquacoes[];

    // Arrays
    private ArrayList<IFuncao> equacoes = new ArrayList<IFuncao>();

    public GeradorPO(ArrayList<IFuncao> equacoes, double somatorio[]) {

        int numeroDeGenes = 0;
        Equacao aux;

        // Verifica o somatÃ³rio
        //TODO Fazer isso dentro da Estrutura de Equacoes, para fazer somente 1 vez.
        resultadoEquacoes = somatorio;
        for (int i = 0; i < somatorio.length; i++) {
            Ez = Ez.add(BigDecimal.valueOf(somatorio[i]));
        }

        // Encontra o nÃºmero de genes necessÃ¡rios nos cromossomos.
        //TODO colocar isso tambÃ©m dentro da estrutura de dados de equacoes!
        Equacao equacao1 = new Equacao();
        equacao1.setFuncao(equacoes.get(0));

        menorNumeroDeGenes = equacao1.numeroDeFuncoes();

        for (int i = 0; i < equacoes.size(); i++) {
            aux = new Equacao(equacoes.get(i));
            if (aux.numeroDeFuncoes() < menorNumeroDeGenes) {
                menorNumeroDeGenes = aux.numeroDeFuncoes();
            }
            if (aux.numeroDeFuncoes() > numeroDeGenes) {
                numeroDeGenes = aux.numeroDeFuncoes();
            }
        }

        setNumeroDeGenes(numeroDeGenes); // pega o nÂº mÃ¡ximo de genes necessarios, no caso x,y,z .... w
        setEquacoes(equacoes);
    }

    // Gera valores randomicos entre 0 e 1 para a populaÃ§Ã£o e a cria.
    public List<Cromossomo> criaPopulacao(int qtd, ArrayPesosGranulares pesos) {
        ArrayList<String> niveis = new ArrayList<String>();
        this.getNiveisFuncoes(equacoes.get(0), "", niveis);

        ArrayList<Cromossomo> popInicial = new ArrayList<Cromossomo>();
        //if (qtd < 2) {
        //	System.out.println("O valor mÃ­nimo para a funÃ§Ã£o Ã© 2");
        //	System.exit(0);
        //}

        for (int j = 0; j < qtd; j++) {
            popInicial.add(new Cromossomo(getNumeroDeGenes(), pesos, niveis));
        }
        //popInicial.add(new Presa(getNumeroDeGenes(), pesos, niveis));

        return popInicial;
    }

    /**
     * Esse método retorna o caminho para cada nó da estrutura de funções. O
     * caminho é denotado por uma sequência, onde cada algarismo significa a
     * posição do filho no nível. A abordagem é parecida com um caminhamento
     * digital na árvore.
     *
     * @param raiz
     * @param alt
     * @param niveis
     */
    private void getNiveisFuncoes(IFuncao raiz, String alt, List niveis) {

        if (raiz == null) {
            return;
        }

        List<IFuncao> filhos = raiz.getFilhos();
        for (int i = 0; filhos != null && i < filhos.size(); i++) {
            getNiveisFuncoes(filhos.get(i), alt + i, niveis);
        }

        if (!"".equals(alt)) {
            niveis.add(alt);
        }
    }


    /*
	 * 
	 * ************************************
	 * *********** GETs e SETs ************
	 * ************************************
     */
    //TODO apagar GETs e SETs que nÃ£o estÃ£o sendo usados (estÃ£o como comentÃ¡rios)
    public double[] getResultadoEquacoes() {
        return this.resultadoEquacoes;
    }

    /**
     * public ArrayList<double[]> getResultadoEquacao() { ArrayList<double[]>
     * aux = new ArrayList<double[]>(); aux.add(this.resultado); return aux; }
     * public double[] getResultado() { return this.resultado; }
     */
    public void setEquacoes(ArrayList<IFuncao> eq) {
        this.equacoes = eq;
    }

    /**
     * public void copiaPopulacaoDePara(ArrayList<Cromossomo> de,
     * ArrayList<Cromossomo> para) { for (int i = 0; i < de.size(); i++) {
     * para.add(de.get(i)); } }
     */
    public ArrayList<IFuncao> getEquacoes() {
        return this.equacoes;
    }

    public BigDecimal getSomatorioEquacoes() {
        return this.Ez;
    }

    /**
     * public double[] getRes() { return this.resultado; }
     */
    public void setNumeroDeGenes(int numeroDeGenes) {
        this.numeroDeGenes = numeroDeGenes;
    }

    public int getNumeroDeGenes() {
        return this.numeroDeGenes;
    }

    /**
     * public void setNumeroDeEquacoes(int numeroDeEquacoes) {
     * this.numeroDeEquacoes = numeroDeEquacoes; } public int
     * getNumeroDeEquacoes() { return this.numeroDeEquacoes; }
     */
    public int getMenorNumeroDeGenes() {
        return this.menorNumeroDeGenes;
    }
    /**
     * public int getQtosForamMutados() { int qtd = 0; for (int i = 0; i <
     * this.popCruzada.size(); i++) { if (this.popCruzada.get(i).getFoiMutado())
     * { qtd++; } } return qtd; } public ArrayList<Cromossomo>
     * getPopulacaoFinal() { return this.popFinal; }
   *
     */
}
