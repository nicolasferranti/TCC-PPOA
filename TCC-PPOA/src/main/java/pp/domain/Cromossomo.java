package pp.domain;

/*
 *  UNIVERSIDADE FEDERAL DE JUIZ DE FORA
 *  CRIADO: 22/02/2011
 *  MODIFICADO: 22/02/2011
 *  AUTOR: Bruno Augusto Clemente de Assis
 *  CLASSE: Cromossomo.
 *  FUNÇÃO: Realiza operações com cromossomos.
 * 
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cromossomo implements Serializable, Comparable<Cromossomo> {

    private static int cont;
    private int id;
    private BigDecimal diferenca = new BigDecimal(0);
    private BigDecimal diferencaEquacoes = new BigDecimal(0);
    private BigDecimal soma = new BigDecimal(0);
    private boolean foiMutado = false;
    private int numeroDeGenes = 0;
    private int geracao = 0;
    private boolean calculado = false;
    private ArrayList<BigDecimal> resultadoCadaEquacao = new ArrayList<BigDecimal>();
    public ArrayList<Gene> genes = new ArrayList<Gene>();

    public Cromossomo() {
        this.id = Cromossomo.cont++;
    }

    /**
     * Cria um cromossomo com fitness aleatório
     * @param numeroGenes
     */
    public Cromossomo(int numeroGenes, ArrayPesosGranulares pesos, List<String> niveis) {
        this();

        setNumeroDeGenes(numeroGenes);

        // Primeiro sortear um gene e, ap�s, sortear um valor para o gene
        HashMap<Integer, BigDecimal> aGenes = new HashMap<Integer, BigDecimal>();
        ArrayList<Integer> indicesGenes = new ArrayList<Integer>();
        for (int j = 0; j < numeroGenes; j++) indicesGenes.add(j);

        // Guarda a soma dos genes irm�os (no mesmo n�vel da �rvore)
        HashMap<String, BigDecimal> somaGenes = new HashMap<String, BigDecimal>();
        for(String nivel : niveis) 
            somaGenes.put(nivel.substring(0, nivel.length()-1), new BigDecimal(0));        

        //TODO se esse c�digo est� correto, significa que n�o pode ter cromossomo com fitness >1. 
        //Por�m isso acontece com vencedores das primeiras gera��es... pq?
        while (indicesGenes.size() > 0) {
            int pGene = indicesGenes.get((int)Math.round(Math.random()*(indicesGenes.size()-1))); // sorteia um indice de gene
            String nGene = niveis.get(pGene).substring(0, niveis.get(pGene).length()-1);
            BigDecimal vGene = pesos.getValor(pesos.getRandomIndice(somaGenes.get(nGene).floatValue()));  // sorteia um valor para esse gene
            aGenes.put(pGene, vGene);
            somaGenes.put(nGene, somaGenes.get(nGene).add(vGene));
            indicesGenes.remove(indicesGenes.indexOf(pGene));
        }

        for (int j = 0; j < numeroGenes; j++) {
            genes.add(new Gene(aGenes.get(j).doubleValue()));
            aGenes.remove(j);
        }

    }

    
    @Override
    public Cromossomo clone() {
        Cromossomo c = new Cromossomo();
        c.numeroDeGenes = this.numeroDeGenes;

        c.genes = new ArrayList<Gene>();
        for (int i = 0; i < this.numeroDeGenes; i++) 
            c.genes.add(new Gene(this.genes.get(i).getValor()));
                
        return c;
    }

    public void addGene(Gene g) {
        genes.add(new Gene(g.getValor()));
    }

    public Gene getGene(int indice) {
        return genes.get(indice);
    }

    public void setNumeroDeGenes(int ndg) {
        this.numeroDeGenes = ndg;
    }

    public void setDiferencaEquacoes(BigDecimal dif) {
        this.diferencaEquacoes = this.diferencaEquacoes.add(dif);
    }

    public BigDecimal getDiferencaEquacoes() {
        return this.diferencaEquacoes;
    }

    public int getNumeroDeGenes() {
        return this.numeroDeGenes;
    }

    public void setDiferenca(BigDecimal dif) {
        this.diferenca = dif;
    }

    public BigDecimal getDiferenca() {
        return this.diferenca;
    }

    public void setSoma(BigDecimal sum) {
        this.soma = sum;
    }

    public BigDecimal getSoma() {
        return this.soma;
    }

    public boolean getFoiMutado() {
        return foiMutado;
    }

    public void setFoiMutado(boolean result) {
        foiMutado = result;
    }

    public void addResultado(BigDecimal K) {
        resultadoCadaEquacao.add(K);
    }

    public int getId() {
        return this.id;
    }

    public BigDecimal getResultado(int PosEquacao) {
        return resultadoCadaEquacao.get(PosEquacao);
    }

    public List<BigDecimal> getResultadoCadaEquacao() {
        return this.resultadoCadaEquacao;
    }

    // Necessário limpar os resultados pois eles podem mudar se um cromossomo sofrer mutação.
    public void limpaCadaResultado() {
        resultadoCadaEquacao.clear();
        setDiferenca(new BigDecimal(0));
        setDiferencaEquacoes(new BigDecimal(0));
    }

    public void atualizaGeracao() {
        this.geracao = this.geracao + 1;
    }

    public int getGeracao() {
        return this.geracao;
    }

    public boolean isCalculado() {
        return calculado;
    }

    public void setCalculado(boolean calculado) {
        this.calculado = calculado;
    }

    public int compareTo(Cromossomo t) {
        return compareToDiferencaEquacoes(t);
    }

    public int compareToDiferencaEquacoes(Cromossomo t) {
        return t.getDiferencaEquacoes().compareTo(this.diferencaEquacoes);
    }

    public int compareToDiferenca(Cromossomo t) {
        BigDecimal thisBig = this.diferenca.abs();
        BigDecimal paramtBig = t.getDiferenca().abs();

        return paramtBig.compareTo(thisBig);
    }
}
