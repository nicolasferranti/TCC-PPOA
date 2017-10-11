/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import pp.domain.ArrayPesosGranulares;
import pp.domain.ComparatorCromossomo;
import pp.domain.Cromossomo;
import pp.domain.Equacao;
import pp.domain.FuncaoComposta;
import pp.domain.FuncaoSimples;
import pp.domain.Gene;
import pp.domain.IFuncao;
import similarity.Analyser;
import ppoa.tcc.ppoa.*;
import ppoa.tcc.ppoa.Operadores;

/**
 *
 * @author nicolasferranti
 */
public class Principal {

    private static BigDecimal avaliaCromossomo(Cromossomo c, Gerador gerador) {

        //this.__contAvaliacoes++;
        c.limpaCadaResultado();
        c.setSoma(Operadores.avaliaCromossomo(c, gerador.getEquacoes(), gerador.getResultadoEquacoes()));
        c.setDiferenca(gerador.getSomatorioEquacoes().subtract(c.getSoma()).abs());
        return c.getSoma();
    }

    public static Cromossomo runGRASP2(List<Cromossomo> populacao, Gerador gerador) {
        //Printer.imprimeInicio(this.parametros.getInt("ag.populacao.inicial"), this.parametros.getInt("ag.geracoes"));

        Cromossomo vencedorAntigo = null;
        int nGer = 0;
        Cromossomo _vencedor = null;

        do {
            vencedorAntigo = populacao.get(0);
            Printer.imprimeInformacoesInicioGeracao(++nGer);

            //BigDecimal fitnessTotal = avaliaPopulacao();
            fazDiversificacaoIntensificacao(populacao, gerador);
            _vencedor = populacao.get(0);

            Printer.imprimeFim(populacao.get(0));
        } while (vencedorAntigo != _vencedor);

        System.out.println("Número de gerações utilizadas no GRASP: " + nGer);

        //retorna a equacoes já alteradas
        Equacao.getEquacoesComPesos(gerador.getEquacoes(), populacao.get(0));

        return populacao.get(0);
    }

    private static void fazDiversificacaoIntensificacao(List<Cromossomo> populacao, Gerador gerador) {

        Printer.imprimeDiversificacaoIntensificacaoInicio();

        double granularidade = 0.005;
        //double granularidade = this.parametros.getDouble("ag.granularidade");
        //Cria colecao com solucoes variadas a partir do vencedor
        Cromossomo venc = populacao.get(0);

        ArrayList<Cromossomo> al = new ArrayList<Cromossomo>();
        for (int i = 0; i < venc.getNumeroDeGenes(); i++) {
            //Cria cromossomo com um gene somado de granularidade
            Cromossomo cAux = venc.clone();
            cAux.getGene(i).addValor(granularidade);
            avaliaCromossomo(cAux, gerador);
            al.add(cAux);

            //Cria cromossomo com um gene subtraído de granularidade
            if (venc.getGene(i).getValor() >= granularidade) {
                cAux = venc.clone();
                cAux.getGene(i).subtValor(granularidade);
                avaliaCromossomo(cAux, gerador);
                al.add(cAux);
            }
        }

        //Faz a busca local, ou seja, verifica se alguma solução na vizinhança é melhor que a solução inicial
        ComparatorCromossomo cc = new ComparatorCromossomo(false);
        Collections.sort(al, cc);

        Printer.imprimeDiversificacaoIntensificacao(venc, al);

        // Caso o primeiro individuo seja melhor que o vencedor atual, insira-o na população
        if (cc.compare(venc, al.get(0)) > 0) {
            populacao.add(0, al.get(0));
        }

        // Insere algumas das soluções de vizinhança na população
//        if (this.parametros.getInt("ag.buscalocal.insercao") != 0) {
//            int qtd = (int) (al.size() / this.parametros.getInt("ag.buscalocal.insercao"));
//            for (int i = 1; i < qtd; i++) {
//                this.populacao.add(al.get(i));
//            }
//        }
    }

    public static void main(String[] args) throws Exception {
//        Analyser analyser = new Analyser("/home/nicolasferranti/NetBeansProjects/TCC-PPOA/xml/xml_benchmark_Nicolas.xml");
//        analyser.process().imprimeTabela(new java.io.PrintWriter("/home/nicolasferranti/NetBeansProjects/TCC-PPOA/xml/OutTest.txt"));
//
        ArrayList<IFuncao> equacoes = new ArrayList<IFuncao>();

        //Valor da soma de cada equaÃ§Ã£o em ordem 1Âª, 2Âª, 3Âª etc...
        final double resultadoDasEquacoes[] = {1, 1, 1};

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
        Gerador g = new Gerador(equacoes, resultadoDasEquacoes);

        ArrayPesosGranulares apg = new ArrayPesosGranulares(0.005);

        int tamPop = 5;

        List<Cromossomo> pop = g.criaPopulacao(tamPop, apg);

        for (int i = 0; i < tamPop; i++) {
            for (Iterator<Gene> it = pop.get(i).genes.iterator(); it.hasNext();) {
                Gene ge = it.next();
                System.out.print(ge.getValor() + " ");
            }
            avaliaCromossomo(pop.get(i), g);
            System.out.println("| Difference: " + pop.get(i).getDiferenca());
        }
        System.out.println("--------------");
        Cromossomo win = runGRASP2(pop, g);
        
        for (Iterator<Gene> it = win.genes.iterator(); it.hasNext();) {
            Gene ge = it.next();
            System.out.print(ge.getValor() + " ");
        }
        avaliaCromossomo(win, g);
        System.out.println("| Difference: " + win.getDiferenca());

    }

}
