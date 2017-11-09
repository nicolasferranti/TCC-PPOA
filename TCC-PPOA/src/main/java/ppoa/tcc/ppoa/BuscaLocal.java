/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pp.domain.ComparatorCromossomo;
import pp.domain.Cromossomo;
import pp.domain.Equacao;

/**
 *
 * @author nicolasferranti
 */
public class BuscaLocal {

    public static BigDecimal avaliaCromossomo(Cromossomo c, Gerador gerador) {

        //this.__contAvaliacoes++;
        c.limpaCadaResultado();
        c.setSoma(Operadores.avaliaCromossomo(c, gerador.getEquacoes(), gerador.getResultadoEquacoes()));
        c.setDiferenca(gerador.getSomatorioEquacoes().subtract(c.getSoma()).abs());
        return c.getSoma();
    }

    public static Cromossomo runGRASP2(List<Cromossomo> populacao, Gerador gerador, double Granularidade) {
        //Printer.imprimeInicio(this.parametros.getInt("ag.populacao.inicial"), this.parametros.getInt("ag.geracoes"));
        
        Cromossomo vencedorAntigo = null;
        int nGer = 0;
        Cromossomo _vencedor = null;

        do {
            vencedorAntigo = populacao.get(0);
            //Printer.imprimeInformacoesInicioGeracao(++nGer);

            //BigDecimal fitnessTotal = avaliaPopulacao();
            fazDiversificacaoIntensificacao(populacao, gerador, Granularidade);
            _vencedor = populacao.get(0);

            //Printer.imprimeFim(populacao.get(0));
        } while (vencedorAntigo != _vencedor);

        //System.out.println("Número de gerações utilizadas no GRASP: " + nGer);
        //retorna a equacoes já alteradas
        Equacao.getEquacoesComPesos(gerador.getEquacoes(), populacao.get(0));

        return populacao.get(0);
    }

    private static void fazDiversificacaoIntensificacao(List<Cromossomo> populacao, Gerador gerador, double Granularidade) {

        //Printer.imprimeDiversificacaoIntensificacaoInicio();

        double granularidade = Granularidade;
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

        //Printer.imprimeDiversificacaoIntensificacao(venc, al);

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

}
