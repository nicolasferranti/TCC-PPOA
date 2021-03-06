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
import pp.domain.Equacao;
import pp.domain.Gene;
import pp.domain.IFuncao;

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
    private double CHANCE_TO_FOLLOW = 0.5;

    private double EpsonDiff = 0.00000000001;

    /**
     * constantes do cálculo da direção se N é usado, então Tau nao é.
     */
    private double N = 1.01;
    private double Tau = 0.1;

    // constantes do cáculo do passo
    private double LambdaMAX = 20;
    private double LambdaMIN = 1;
    //random entre 0 e 1 
    private double Eps;
    private double Beta = 1; // paper colocou Beta=1 e w=1
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

    public ArrayList<IFuncao> getEquacoes() {
        return Equacao.getEquacoesComPesos(gerador.getEquacoes(), this.bestIndividual);
    }

    public void SimulaVida(int iteracoes, boolean doLocalSearch) {
        ordenaPorFitness();
        printPopulationDiff();
        this.bestIndividual = this.pop.get(0).clone();
        avaliaCromossomoDiferenca(bestIndividual, gerador);

        double runOrFollow;
        Cromossomo bestAfterGRASP;

        List<Cromossomo> newPopulation;

        for (int i = 0; i < 10; i++) {
            System.out.println("Ciclo " + (i + 1));
            // começa gerações
            try {
                for (int iterat = 0; iterat < iteracoes && bestIndividual.getDiferenca().doubleValue() > EpsonDiff; iterat++) {
                    //System.out.println("BEST (" + iterat + ") :" + this.pop.get(0).getDiferenca());
                    /**
                     * <
                     * Ideia: 1 fazer presas andarem (fugindo ou seguindo)
                     * 2 predador anda separado
                     * 3 busca local na melhor presa. >
                     *
                     */

                    newPopulation = new ArrayList<Cromossomo>();
                    /// linha abaixo temporaria
                    //newPopulation.add(this.pop.get(0));
                    for (int popIterator = 1; popIterator < tamPop - 1; popIterator++) {

                        /* MOVE PREY 1 to N-2. */
                        runOrFollow = Math.random();
                        if (runOrFollow <= CHANCE_TO_FOLLOW) {
                            Cromossomo newIt = calculaDirecaoFollow(popIterator);
                            avaliaCromossomoDiferenca(newIt, gerador);
                            newPopulation.add(newIt);
                        } else {
                            //System.out.println("Choose to run");
                            Cromossomo newIt = calculaDirecaoRun(popIterator);
                            avaliaCromossomoDiferenca(newIt, gerador);
                            newPopulation.add(newIt);
                        }

                    }

                    /* MOVE PREDATOR. */
                    Cromossomo newPredator = this.calculaDirecaoPredator(tamPop - 1);
                    avaliaCromossomoDiferenca(newPredator, gerador);
                    newPopulation.add(newPredator);

                    //System.out.println("GRASP in:" + this.pop.get(0).getDiferenca());
                    bestAfterGRASP = BuscaLocal.runGRASP2(pop, gerador, granularidade);
                    avaliaCromossomoDiferenca(bestAfterGRASP, gerador);
                    newPopulation.add(bestAfterGRASP);
                    //System.out.println("GRASP out:" + bestAfterGRASP.getDiferenca());

                    /**
                     * se a diferença que o melhor idividuo tinha for maior que
                     * a do novo depois do GRASP, atualizar o melhor individuo.
                     */
                    //if (this.bestIndividual.getDiferenca().compareTo(bestAfterGRASP.getDiferenca()) > 0) {
                    if (this.bestIndividual.getDiferenca().compareTo(bestAfterGRASP.getDiferenca()) > 0) {
                        this.bestIndividual = bestAfterGRASP;
                    }
                    this.pop = newPopulation;
                    
                    ordenaPorFitness();

                    /*
                    if (iterat == 499 || iterat == 599 || iterat == 699 || iterat == 799 || iterat == 899 || iterat == 999) {
                        bestAfterGRASP = BuscaLocal.runGRASP2(pop, gerador, granularidade);
                        avaliaCromossomoDiferenca(bestAfterGRASP, gerador);
                        System.out.println("Best of " + iterat);
                        System.out.println(bestAfterGRASP.getDiferenca());
                        System.out.println("------");
                        //this.pop.add(0, bestAfterGRASP);
                    }
                     */
                }
                printPopulationDiff();
            } catch (NumberFormatException ex) {
                System.out.println("NumberFormatException. Aborting ...");
            }
            System.out.println("BEST OF " + (i + 1) + " => " + this.pop.get(0).getDiferenca());

            //localsearch
            if (doLocalSearch) {
                bestAfterGRASP = BuscaLocal.runGRASP2(pop, gerador, granularidade);
                avaliaCromossomoDiferenca(bestAfterGRASP, gerador);
                System.out.println("GRASP individual:" + bestAfterGRASP.getDiferenca().longValue());

                /**
                 * se a diferença que o melhor idividuo tinha for maior que a do
                 * novo depois do GRASP, atualizar o melhor individuo.
                 */
                if (this.bestIndividual.getDiferenca().compareTo(bestAfterGRASP.getDiferenca()) > 0) {
                    System.out.println("GRASP Better");
                    this.bestIndividual = bestAfterGRASP;
                }
            } else // atualizo o melhor individuo a cada ciclo
            {
                if (this.bestIndividual.getDiferenca().compareTo(this.pop.get(0).getDiferenca()) > 0) {
                    this.bestIndividual = this.pop.get(0).clone();
                    avaliaCromossomoDiferenca(bestIndividual, gerador);
                }
            }

            //cria novos parâmetros iniciais
            this.pop = this.gerador.criaPopulacao(this.tamPop, pesos);
            for (Cromossomo c : pop) {
                avaliaCromossomoDiferenca(c, gerador);
            }
            ordenaPorFitness();

        }

        System.out.println("BEST AT ALL :" + this.bestIndividual.getDiferenca());

        this.pop.add(0, this.bestIndividual);

        for (Gene g : this.bestIndividual.genes) {
            System.out.print(g.getValor() + " ");
        }
        System.out.println("");
    }

    private void printPopulationDiff() {
        System.out.println("---------START PRINT--------");
        for (int i = 0; i < this.tamPop; i++) {
            System.out.print(this.pop.get(i).getDiferenca() + " (");
            for (Gene g : this.pop.get(i).genes) {
                System.out.print(g.getValor() + "\t");
            }
            System.out.print(")");
            System.out.println("");
        }
        System.out.println("---------FINISH PRINT--------");
    }


    /*
        Preenche os valores de soma e diferença do cromossomo e retorna a diferença
     */
    private static BigDecimal avaliaCromossomoDiferenca(Cromossomo c, Gerador gerador) {

        c.limpaCadaResultado();
        c.setSoma(Operadores.avaliaCromossomo(c, gerador.getEquacoes(), gerador.getResultadoEquacoes()));
        c.setDiferenca(gerador.getSomatorioEquacoes().subtract(c.getSoma()).abs());
        /*
        System.out.println("-------");
        System.out.println("Somatorio:" + gerador.getSomatorioEquacoes());
        System.out.println("Genes:");
        for (int i = 0; i < gerador.getNumeroDeGenes(); i++) {
            System.out.print(c.genes.get(i).getValor() + " ");
        }
        
        System.out.println("");
        System.out.println("Soma:" + c.getSoma());
        System.out.println("Diferença:" + c.getDiferenca());
        System.out.println("Diferença Eq:" + c.getDiferencaEquacoes());
        System.out.println("-------");
         */
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
        BigDecimal response;
        if (indice > -1 && indice < this.pop.size()) {
            try {
                response = BigDecimal.TEN.divide(pop.get(indice).getDiferenca(), 8, RoundingMode.HALF_UP);
                return response;
            } catch (ArithmeticException ae) {
                System.out.println("error divide");
                return BigDecimal.valueOf(Double.MAX_VALUE);
            }
        }
        return null;
    }

    /**
     * <DONE AND TESTED>
     */
    public BigDecimal getSurvivorValue(Cromossomo c) {
        if (c != null) {
            return BigDecimal.TEN.divide(c.getDiferenca(), 8, RoundingMode.HALF_UP);
        }
        return null;
    }

    public Cromossomo calculaDirecaoRun(int indice) {
        Cromossomo predador = this.pop.get(this.pop.size() - 1);
        Cromossomo presa = this.pop.get(indice);

        Gene[] direcaoY = new Gene[this.gerador.getNumeroDeGenes()];
        Gene[] direcaoFinal;
        for (int i = 0; i < direcaoY.length; i++) {
            direcaoY[i] = new Gene(Math.random());
        }
        double d1, d2;
        double sumSquareD1 = 0;
        double sumSquareD2 = 0;
        double sumSquareFinal = 0;

        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            sumSquareD1 += pow(predador.genes.get(i).getValor() - (presa.genes.get(i).getValor() + direcaoY[i].getValor()), 2);
            sumSquareD2 += pow(predador.genes.get(i).getValor() - (presa.genes.get(i).getValor() - direcaoY[i].getValor()), 2);
        }

        d1 = Math.sqrt(sumSquareD1);
        d2 = Math.sqrt(sumSquareD1);

        if (d1 < d2) {
            direcaoFinal = new Gene[this.gerador.getNumeroDeGenes()];
            for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
                direcaoFinal[i] = new Gene(direcaoY[i].getValor() * -1);
            }
        } else {
            direcaoFinal = direcaoY;
        }

        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            sumSquareFinal += pow(direcaoFinal[i].getValor(), 2);
        }
        sumSquareFinal = Math.sqrt(sumSquareFinal);
        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            direcaoFinal[i].replaceValor(direcaoFinal[i].getValor() / sumSquareFinal);
        }

        double passos = this.getPassos(indice);
        Cromossomo novaPresa = presa.clone();
        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            //novaPresa.genes.get(i).replaceValor(Math.abs((novaPresa.genes.get(i).getValor() + direcaoFinal[i].getValor()) * passos));
            novaPresa.genes.get(i).replaceValor((novaPresa.genes.get(i).getValor() + direcaoFinal[i].getValor()) * passos);
        }

        return novaPresa;///TESTAR
    }

    /**
     * Calcula nova direção considerando população ordenada em ordem decrescente
     * de SurvivorValue
     * <DONE AND TESTED>
     */
    public Cromossomo calculaDirecaoFollow(int indice) {

        // cria nova direção zerada
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        for (int i = 0; i < direcao.length; i++) {
            direcao[i] = new Gene(0);
        }

        double distancia, pow;
        double sumSquare = 0;
        // indice > 0 porque a melhor presa não caminha
        if (indice > 0 && indice < this.pop.size()) {
            // para cada presa cuja sobrevivencia é melhor que a minha
            //int j = indice-1;
            for (int j = 0; j < indice; j++) {
                //double NexpDivDist = pow(N, this.getSurvivorValue(j).doubleValue()) / distanciaEuclidiana(this.pop.get(indice), this.pop.get(j));
                //BigDecimal NexpDivDist = new BigDecimal(Math.exp(pow(this.getSurvivorValue(j).doubleValue(), this.Tau) - distanciaEuclidiana(this.pop.get(indice), this.pop.get(j))));
                distancia = distanciaEuclidiana(this.pop.get(indice), this.pop.get(j));
                pow = Math.pow(this.getSurvivorValue(j).doubleValue(), this.Tau);
                double NexpDivDist = Math.exp(pow - distancia);
                //System.out.println("dist :" + distancia + "| pow :" + pow + "| Nexp :" + NexpDivDist);

                for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
                    double Xj = this.pop.get(j).genes.get(geneIterator).getValor();
                    double Xi = this.pop.get(indice).genes.get(geneIterator).getValor();
                    //System.out.println("Xj :" + Xj + "| Xi :" + Xi + "| Nexp :" + NexpDivDist);
                    direcao[geneIterator].addValor(NexpDivDist * (Xj - Xi));
//                    BigDecimal Xj = new BigDecimal(this.pop.get(j).genes.get(geneIterator).getValor());
//                    BigDecimal Xi = new BigDecimal(this.pop.get(indice).genes.get(geneIterator).getValor());
//                    System.out.println("Xj :" + Xj + "| Xi :" + Xi + "| Nexp :" + NexpDivDist);
//                    direcao[geneIterator].addBigValor(NexpDivDist.multiply(Xj.subtract(Xi)));
                }
            }
        }
        //Normalizaço

        for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
            sumSquare += Math.pow(direcao[geneIterator].getValor(), 2);
        }
        sumSquare = Math.sqrt(sumSquare);
//        System.out.println("-------------");
//        System.out.print("Old Direction: ");

        for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
//            System.out.print(direcao[geneIterator].getValor() + " ");
            direcao[geneIterator].replaceValor(direcao[geneIterator].getValor() / sumSquare);
        }
//        System.out.println("");
//        System.out.print("Normalized Direction");
//        for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
//            System.out.print(direcao[geneIterator].getValor() + " ");
//        }
//        System.out.println("");

        double passos = this.getPassos(indice);
//        System.out.println("Steps: "+ passos);
//        System.out.println("-------------");
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
        //System.out.println("FOLLOW Steps : old(new)");
        //System.out.println("Passos :" + passos);
        for (int geneIterator = 0; geneIterator < it.getNumeroDeGenes(); geneIterator++) {
            //it.genes.get(geneIterator).addValor(direcao[geneIterator].getValor() * passos);
            //System.out.print(direcao[geneIterator].getValor() + "(" + direcao[geneIterator].getValor() * passos + ")");
            newIt.genes.get(geneIterator).replaceValor(direcao[geneIterator].getValor() * passos);
        }
        //System.out.println("---- EOFS ----");
        return newIt;
        /**
         * System.out.println("---------"); System.out.println("Old SV:" +
         * this.getSurvivorValue(it)); avaliaCromossomoDiferenca(it, gerador);
         * System.out.println("New SV:" + this.getSurvivorValue(it));
         */
    }

    /**
     * <DONE AND TESTED>
     */
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

    /**
     * <DONE AND TESTED>
     */
    private double getPassos(int cromossomoI) {
        this.Eps = Math.random() * 100;
        BigDecimal SVcromI = this.getSurvivorValue(cromossomoI);
        BigDecimal SVpredator = this.getSurvivorValue(this.tamPop - 1);
        //System.out.println("SVC :"+SVcromI + "|| SVP :"+SVpredator);
        BigDecimal diffSV = SVcromI.subtract(SVpredator, MathContext.DECIMAL128);
        double denominador = Math.exp(this.Beta * pow(Math.abs(diffSV.doubleValue()), this.w));
//        System.out.println("passos: "+ ((this.LambdaMAX * this.Eps * this.granularidade) / denominador) );
        return Math.nextUp((this.LambdaMAX * this.Eps * this.granularidade) / denominador);
    }

    /**
     * Calcula nova direção do predador baseado na pior presa
     * <DONE>
     */
    public Cromossomo calculaDirecaoPredator(int indice) {

        // cria nova direção zerada
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        for (int i = 0; i < direcao.length; i++) {
            direcao[i] = new Gene(0);
        }

        if (indice > 0 && indice < this.pop.size()) {

            Cromossomo newPredator = this.pop.get(indice);
            Gene[] randomNormalizado = this.generateRandomDirectionNormalized();
            Gene[] diferencaPresaNormalizada = this.generateDifferenceNormalizedVectorBetweenPredatorAndWorstPrey();
            Gene g;
            double termoRandomico, termoRelativo;
            // para cada gene compartilhado
            for (int geneIterator = 0; geneIterator < this.gerador.getNumeroDeGenes(); geneIterator++) {
                termoRandomico = (this.LambdaMAX * Math.random() * randomNormalizado[geneIterator].getValor());
                termoRelativo = (this.LambdaMIN * Math.random() * diferencaPresaNormalizada[geneIterator].getValor());
                g = new Gene(newPredator.genes.get(geneIterator).getValor() + termoRandomico + termoRelativo);
                newPredator.genes.add(geneIterator, g);
            }

            //avaliaCromossomoDiferenca(newPredator, gerador);
            return newPredator;
        } else {
            return null;
        }

    }

    private Gene[] generateDifferenceNormalizedVectorBetweenPredatorAndWorstPrey() {
        Gene[] direcao = new Gene[this.gerador.getNumeroDeGenes()];
        Cromossomo predator = this.pop.get(tamPop - 1);
        Cromossomo worstPrey = this.pop.get(tamPop - 2);

        double sumSquare = 0;
        double difference = 0;
        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            difference = worstPrey.genes.get(i).getValor() - predator.genes.get(i).getValor();
            direcao[i] = new Gene(difference);
            sumSquare += pow(difference, 2);
        }

        for (int i = 0; i < this.gerador.getNumeroDeGenes(); i++) {
            direcao[i].replaceValor(direcao[i].getValor() / Math.sqrt(sumSquare));
        }
        return direcao;
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
