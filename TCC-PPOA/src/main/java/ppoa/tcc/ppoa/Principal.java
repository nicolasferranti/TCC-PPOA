/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pp.domain.ArrayPesosGranulares;
import pp.domain.Cromossomo;
import pp.domain.FuncaoComposta;
import pp.domain.FuncaoSimples;
import pp.domain.Gene;
import pp.domain.IFuncao;
import ppoa.tcc.ppoa.BuscaLocal;
import similarity.Analyser;

/**
 *
 * @author nicolasferranti
 */
public class Principal {

    public static void main(String[] args) throws Exception {
        //MatcherBenchmak2007 mb = new MatcherBenchmak2007("/home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/xml");
        //mb.runTest(101, "xml/xml_benchmark_Nicolas.xml", true);
        //mb.runTest(101, "xml/benchmark/bench101.xml", true);
        //mb.runTest(103, "xml/benchmark/bench103.xml", true);
        //mb.runTest(104, "xml/benchmark/bench104.xml", true);
        //mb.runTest(201, "xml/benchmark/bench201.xml", true);
        //mb.runTest(202, "xml/benchmark/bench202.xml", true);
        //mb.runTest(203, "xml/benchmark/bench203.xml", true);
        //mb.runTest(204, "xml/benchmark/bench204.xml", true);
        //mb.runTest(205, "xml/benchmark/bench205.xml", true);
        //mb.runTest(206, "xml/benchmark/bench206.xml", true);
        //mb.runTest(208, "xml/benchmark/bench208.xml", true);
        //mb.runTest(209, "xml/benchmark/bench209.xml", true);
        //mb.runTest(210, "xml/benchmark/bench210.xml", true);
        //mb.runTest(221, "xml/benchmark/bench221.xml", true);
        //mb.runTest(222, "xml/benchmark/bench222.xml", true);
        //mb.runTest(223, "xml/benchmark/bench223.xml", true);
        //mb.runTest(224, "xml/benchmark/bench224.xml", true);
        //mb.runTest(225, "xml/benchmark/bench225.xml", true);
        //mb.runTest(228, "xml/benchmark/bench228.xml", true);
        //mb.runTest(230, "xml/benchmark/bench230.xml", true);
        //mb.runTest(232, "xml/benchmark/bench232.xml", true);
        //mb.runTest(233, "xml/benchmark/bench233.xml", true);
        //mb.runTest(236, "xml/benchmark/bench236.xml", true);
        //mb.runTest(237, "xml/benchmark/bench237.xml", true);
        //mb.runTest(238, "xml/benchmark/bench238.xml", true);
        //mb.runTest(239, "xml/benchmark/bench239.xml", true);
        //mb.runTest(240, "xml/benchmark/bench240.xml", true);
        //mb.runTest(241, "xml/benchmark/bench241.xml", true);
        //mb.runTest(246, "xml/benchmark/bench246.xml", true);
        //mb.runTest(247, "xml/benchmark/bench247.xml", true);
        //mb.runTest(301, "xml/benchmark/bench301.xml", true);
        //mb.runTest(302, "xml/benchmark/bench302.xml", true);
        //mb.runTest(303, "xml/benchmark/bench303.xml", true);
        //mb.runTest(304, "xml/benchmark/bench304.xml", true);

//        MatcherBenchmak2007 mb = new MatcherBenchmak2007("").runTest(0, xmlFuncoes, true);
//        System.out.println(mb.showPrecisionRecall(0, O1, O2, al));
//        Analyser analyser = new Analyser("/home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/xml/xml_benchmark_Nicolas.xml");
//        analyser.process().imprimeTabela(new java.io.PrintWriter("/home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/xml/OutTest.txt"));
        try {

            
            ArrayList<IFuncao> equacoes = new ArrayList<IFuncao>();

            //Valor da soma de cada equaÃ§Ã£o em ordem 1Âª, 2Âª, 3Âª etc...
            final double resultadoDasEquacoes[] = {1, 1, 1};

            final FuncaoComposta fc1 = new FuncaoComposta();
            fc1.add(new FuncaoSimples(0.5));
            fc1.add(new FuncaoSimples(0.1));
            fc1.add(new FuncaoSimples(0.2));
            final FuncaoComposta fc2 = new FuncaoComposta();
            fc2.add(new FuncaoSimples(0.6));
            fc2.add(new FuncaoSimples(0.8));
            fc2.add(new FuncaoSimples(0.1));
            final FuncaoComposta fc3 = new FuncaoComposta();
            fc3.add(new FuncaoSimples(0.1));
            fc3.add(new FuncaoSimples(0.5));
            fc3.add(new FuncaoSimples(0.8));
            equacoes.add(fc1);
            equacoes.add(fc2);
            equacoes.add(fc3);
            
            /*
            //Função K1 (fc3)
            final FuncaoSimples f1 = new FuncaoSimples(0.4);
            final FuncaoSimples f2 = new FuncaoSimples(0.1);
            final FuncaoSimples f3 = new FuncaoSimples(0.2);
            final FuncaoComposta fc1 = new FuncaoComposta();
            fc1.add(f1);
            fc1.add(f2);
            fc1.add(f3);

            final FuncaoSimples f4 = new FuncaoSimples(0.1);
            final FuncaoComposta fc2 = new FuncaoComposta();
            fc2.add(fc1);
            fc2.add(f4);

            final FuncaoSimples f5 = new FuncaoSimples(0.2);
            final FuncaoSimples f6 = new FuncaoSimples(0.4);
            final FuncaoComposta fc3 = new FuncaoComposta();
            fc3.add(f5);
            fc3.add(f6);
            fc3.add(fc2);

            //Função K2 (fc32)
            final FuncaoSimples f12 = new FuncaoSimples(0.3);
            final FuncaoSimples f22 = new FuncaoSimples(0.2);
            final FuncaoSimples f32 = new FuncaoSimples(0.4);
            final FuncaoComposta fc12 = new FuncaoComposta();
            fc12.add(f12);
            fc12.add(f22);
            fc12.add(f32);

            final FuncaoSimples f42 = new FuncaoSimples(0.7);
            final FuncaoComposta fc22 = new FuncaoComposta();
            fc22.add(fc12);
            fc22.add(f42);

            final FuncaoSimples f52 = new FuncaoSimples(0.1);
            final FuncaoSimples f62 = new FuncaoSimples(0.5);
            final FuncaoComposta fc32 = new FuncaoComposta();
            fc32.add(f52);
            fc32.add(f62);
            fc32.add(fc22);

            //Função K3 (fc33)
            final FuncaoSimples f13 = new FuncaoSimples(0.5);
            final FuncaoSimples f23 = new FuncaoSimples(0.1);
            final FuncaoSimples f33 = new FuncaoSimples(0.1);
            final FuncaoComposta fc13 = new FuncaoComposta();
            fc13.add(f13);
            fc13.add(f23);
            fc13.add(f33);

            final FuncaoSimples f43 = new FuncaoSimples(0.4);
            final FuncaoComposta fc23 = new FuncaoComposta();
            fc23.add(fc13);
            fc23.add(f43);

            final FuncaoSimples f53 = new FuncaoSimples(0.7);
            final FuncaoSimples f63 = new FuncaoSimples(0.2);
            final FuncaoComposta fc33 = new FuncaoComposta();
            fc33.add(f53);
            fc33.add(f63);
            fc33.add(fc23);

            equacoes.add(fc3);
            equacoes.add(fc32);
            equacoes.add(fc33);
            */
            Gerador g = new Gerador(equacoes, resultadoDasEquacoes);

            ArrayPesosGranulares apg = new ArrayPesosGranulares(0.005);

            int tamPop = 60;

            List<Cromossomo> pop = g.criaPopulacao(tamPop, apg);

            PresaPredador pp = new PresaPredador(g, tamPop, 0.005);
            pp.SimulaVida(1000, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //System.out.println(e.getCause());
            e.printStackTrace();

        }
//        pp.printDiferenca();
//        pp.ordenaPorFitness();
//        pp.printDiferenca();
        //pp.calculaDirecao(3);
//        for (int i = 0; i < tamPop; i++) {
//            for (Iterator<Gene> it = pop.get(i).genes.iterator(); it.hasNext();) {
//                Gene ge = it.next();
//                System.out.print(ge.getValor() + " ");
//            }
//            BuscaLocal.avaliaCromossomo(pop.get(i), g);
//            System.out.println("| Difference: " + pop.get(i).getDiferenca());
//        }
//        System.out.println("--------------");
//        Cromossomo win = BuscaLocal.runGRASP2(pop, g);
//
//        for (Iterator<Gene> it = win.genes.iterator(); it.hasNext();) {
//            Gene ge = it.next();
//            System.out.print(ge.getValor() + " ");
//        }
//        BuscaLocal.avaliaCromossomo(win, g);
//        System.out.println("| Difference: " + win.getDiferenca());
    }

}
