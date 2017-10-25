/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import static java.lang.Math.pow;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import pp.domain.ArrayPesosGranulares;
import pp.domain.Cromossomo;
import pp.domain.Gene;

/**
 *
 * @author nicolasferranti
 */
public class PresaPredador {

    private List<Cromossomo> pop;
    private Gerador gerador;
    private ArrayPesosGranulares pesos;
    private double granularidade;
    private int tamPop;
    private double N = 1.01;
    
    // constantes do cáculo do passo
    private double LambdaMAX = 10;
    private double Eps;
    private double Beta;
    private double w;

    public PresaPredador(Gerador g, int tamanhoPopulacao, double Granularidade) {
        this.gerador = g;
        this.tamPop = tamanhoPopulacao;
        this.granularidade = Granularidade;
        this.pesos = new ArrayPesosGranulares(Granularidade);
        this.pop = this.gerador.criaPopulacao(tamanhoPopulacao, pesos);
        for (Cromossomo c : pop) {
            avaliaCromossomoDiferenca(c, gerador);
        }
    }

    /*
        Preenche os valores de soma e diferença do cromossomo e retorna a diferença
     */
    private static BigDecimal avaliaCromossomoDiferenca(Cromossomo c, Gerador gerador) {

        c.limpaCadaResultado();
        c.setSoma(Operadores.avaliaCromossomo(c, gerador.getEquacoes(), gerador.getResultadoEquacoes()));
        c.setDiferenca(gerador.getSomatorioEquacoes().subtract(c.getSoma()).abs());
        return c.getDiferenca();
    }

    public void printDiferenca() {
        for (int i = 0; i < tamPop; i++) {
            System.out.println("individuo " + i + " | Difference:" + pop.get(i).getDiferenca() + " | SV:" + getSurvivorValue(i));
        }
        System.out.println();
    }

    // ordena os indivíduos por ordem crescente de diferença
    public void ordenaPorFitness() {
        pop.sort(new Comparator<Cromossomo>() {
            @Override
            public int compare(Cromossomo o1, Cromossomo o2) {
                return avaliaCromossomoDiferenca(o1, gerador).compareTo(avaliaCromossomoDiferenca(o2, gerador));
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    public BigDecimal getSurvivorValue(int indice) {
        if (indice > -1 && indice < this.pop.size()) {
            return BigDecimal.ONE.divide(pop.get(indice).getDiferenca(), 8, RoundingMode.HALF_UP);
        }
        return null;
    }

    /*
        Calcula nova direção considerando população ordenada em ordem decrescente
        de SurvivorValue
     */
    public void calculaDirecao(int indice) {

        // cria nova direção zerada
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        for (int i = 0; i < direcao.length; i++) {
            direcao[i] = new Gene(0);
        }

        // indice > 0 porque a melhor presa não caminha
        if (indice > 0 && indice < this.pop.size()) {
            // para cada presa cuja sobrevivencia é melhor que a minha
            for (int j = 0; j < indice; j++) {
                /** TODO SUBSTITUIR CONSTANTE PARA O CASO 0. */ 
                double NexpDivDist = pow(N, this.getSurvivorValue(j).doubleValue()) / distanciaEuclidiana(this.pop.get(indice), this.pop.get(j));
                // para cada gene compartilhado
                for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
                    direcao[geneIterator].addValor(NexpDivDist * (this.pop.get(j).genes.get(geneIterator).getValor() - this.pop.get(indice).genes.get(geneIterator).getValor()));
                }
            }
        }
        System.out.println("Nova Direção:");
        for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
            System.out.print(direcao[geneIterator].getValor() + " ");
        }
    }

    private double distanciaEuclidiana(Cromossomo i, Cromossomo j) {
        if (i == null || j == null) {
            return 0;
        }
        double difSquare = 0;
        for (int k = 0; k < i.getNumeroDeGenes(); k++) {
            difSquare += pow(j.genes.get(k).getValor() - i.genes.get(k).getValor(), 2);
        }
        return Math.sqrt(difSquare);
    }
    
    private double getPassos(int cromossomoI){
        ///TODO
        return (this.LambdaMAX*this.Eps);
    }

}
