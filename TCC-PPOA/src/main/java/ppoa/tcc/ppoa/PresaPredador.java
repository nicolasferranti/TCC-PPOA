/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import static java.lang.Math.pow;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.xerces.impl.xpath.regex.Match;
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
    private double CHANCE_TO_FOLLOW = 0.8;

    /**
     * constantes do cálculo da direção se N é usado, então Tau nao é.
     */
    private double N = 1.01;
    private double Tau = 1.01;

    // constantes do cáculo do passo
    private double LambdaMAX = 10;
    private double Eps;
    private double Beta = 0;
    private double w = 1;

    // melhor presa 
    private Cromossomo bestIndividual = null;

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

    public void SimulaVida(int iteracoes) {
        ordenaPorFitness();
        this.bestIndividual = this.pop.get(0).clone();
        avaliaCromossomoDiferenca(bestIndividual, gerador);

        double runOrFollow;
        Cromossomo bestAfterGRASP;

        List<Cromossomo> newPopulation = new ArrayList<Cromossomo>();

        // começa gerações
        for (int iterat = 0; iterat < iteracoes; iterat++) {
            /**
             * <
             * Ideia: 1 fazer presas andarem (fugindo ou seguindo) 2 predador
             * anda separado 3 busca local na melhor presa.
             *
             */

            for (int popIterator = 1; popIterator < tamPop - 1; popIterator++) {

                /* MOVE PREY 1 to N-2. */
                runOrFollow = Math.random();
                if (runOrFollow > CHANCE_TO_FOLLOW) {
                    Cromossomo newIt = calculaDirecaoFollow(popIterator);
                    avaliaCromossomoDiferenca(newIt, gerador);
                    newPopulation.add(newIt);
                } else {
                    /* TODO : call run method. */
                    System.out.println("Choose to run");
                }

                /* MOVE PREDATOR. */
            }

            bestAfterGRASP = BuscaLocal.runGRASP2(pop, gerador, granularidade);
            avaliaCromossomoDiferenca(bestAfterGRASP, gerador);
            newPopulation.add(bestAfterGRASP);
            /**
             * se a diferença que o melhor idividuo tinha for maior que a do
             * novo depois do GRASP, atualizar o melhor individuo.
             */
            if (this.bestIndividual.getDiferenca().compareTo(bestAfterGRASP.getDiferenca()) > 0) {
                this.bestIndividual = bestAfterGRASP;
            }

            ordenaPorFitness();
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
            System.out.println("individuo " + pop.get(i).getId() + " | Difference:" + pop.get(i).getDiferenca() + " | SV:" + getSurvivorValue(i));
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

    public BigDecimal getSurvivorValue(Cromossomo c) {
        if (c != null) {
            return BigDecimal.ONE.divide(c.getDiferenca(), 8, RoundingMode.HALF_UP);
        }
        return null;
    }

    /*
        Calcula nova direção considerando população ordenada em ordem decrescente
        de SurvivorValue
     */
    public Cromossomo calculaDirecaoFollow(int indice) {

        // cria nova direção zerada
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        for (int i = 0; i < direcao.length; i++) {
            direcao[i] = new Gene(0);
        }

        // indice > 0 porque a melhor presa não caminha
        if (indice > 0 && indice < this.pop.size()) {
            // para cada presa cuja sobrevivencia é melhor que a minha
            for (int j = 0; j < indice; j++) {
                /**
                 * TODO SUBSTITUIR CONSTANTE PARA O CASO 0. < DONE
                 */
                //double NexpDivDist = pow(N, this.getSurvivorValue(j).doubleValue()) / distanciaEuclidiana(this.pop.get(indice), this.pop.get(j));
                double NexpDivDist = Math.exp(pow(this.getSurvivorValue(j).doubleValue(), this.Tau) - distanciaEuclidiana(this.pop.get(indice), this.pop.get(j)));
                // para cada gene compartilhado
                for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
                    direcao[geneIterator].addValor(NexpDivDist * (this.pop.get(j).genes.get(geneIterator).getValor() - this.pop.get(indice).genes.get(geneIterator).getValor()));
                }
            }
        }
        double passos = this.getPassos(indice);
        //        System.out.println("Diferença:");
        //        for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
        //            System.out.print(direcao[geneIterator].getValor() + " ");
        //        }
        //        System.out.println();
        //        System.out.println("Passos :" + passos);
        //        System.out.println("Diferença Vezes passos:");
        //        for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
        //            System.out.print(direcao[geneIterator].getValor() * passos + " ");
        //        }
        //        System.out.println();
        //        System.out.println("Nova direção:");
        Cromossomo it = this.pop.get(indice);
        Cromossomo newIt = it.clone();
        for (int geneIterator = 0; geneIterator < it.getNumeroDeGenes(); geneIterator++) {
            //it.genes.get(geneIterator).addValor(direcao[geneIterator].getValor() * passos);
            newIt.genes.get(geneIterator).addValor(direcao[geneIterator].getValor() * passos);
            System.out.print(it.genes.get(geneIterator).getValor() + " ");
        }
        return newIt;
        /**
         * System.out.println("---------"); System.out.println("Old SV:" +
         * this.getSurvivorValue(it)); avaliaCromossomoDiferenca(it, gerador);
         * System.out.println("New SV:" + this.getSurvivorValue(it));
         */
    }

    /**
        Calcula nova direção do predador baseado na pior presa
        <IMPORTANTE>
     */
    public Cromossomo calculaDirecaoPredator(int indice) {

        // cria nova direção zerada
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        for (int i = 0; i < direcao.length; i++) {
            direcao[i] = new Gene(0);
        }

        // indice > 0 porque a melhor presa não caminha
        if (indice > 0 && indice < this.pop.size()) {
            int j = tamPop - 2;

            double NexpDivDist = Math.exp(pow(this.getSurvivorValue(j).doubleValue(), this.Tau) - distanciaEuclidiana(this.pop.get(indice), this.pop.get(j)));
            // para cada gene compartilhado
            for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
                direcao[geneIterator].addValor(NexpDivDist * (this.pop.get(j).genes.get(geneIterator).getValor() - this.pop.get(indice).genes.get(geneIterator).getValor()));
            }

            Cromossomo newPredator = this.pop.get(indice);
            //chamar o generate random e aplciar a formula
            return newPredator;
        } else {
            return null;
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

    private double getPassos(int cromossomoI) {
        this.Eps = Math.random();
        BigDecimal diffSV = this.getSurvivorValue(cromossomoI).subtract(this.getSurvivorValue(this.pop.size() - 1), MathContext.UNLIMITED);
        double denominador = Math.exp(this.Beta * pow(Math.abs(diffSV.doubleValue()), this.w));
        return (this.LambdaMAX * this.Eps * this.granularidade) / denominador;
    }

    private Gene[] generateRandomDirectionNormalized() {
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        double sumSquare = 0;
        double gene = 0;
        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            gene = Math.random();
            direcao[i] = new Gene(gene);
            sumSquare += pow(gene, 2);
        }

        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            direcao[i].replaceValor(direcao[i].getValor() / Math.sqrt(sumSquare));
        }
        return direcao;
    }
}
