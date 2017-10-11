
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.combination;

import similarity.common.SimilarityVO;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author Isabella Pires Capobiango
 */

@Deprecated
//TODO essa classe est� depreciada pois descobri que ela n�o funciona corretamente.
public class DeepCombination implements ICombination {

    @Deprecated
    public  Collection<SimilarityVO> explore(Collection<SimilarityVO> valores){
        Collection<SimilarityVO> valorFinal = new ArrayList<SimilarityVO>();
        Collection<SimilarityVO> copiaValores = new ArrayList<SimilarityVO>();
        Collection<SimilarityVO> copiaValoresInicial = new ArrayList<SimilarityVO>();
        OntResource v1Aux = null, v2Aux = null;
        double max = 0, soma = 0;
        boolean caminha = true;
        int cont = 0, cont1 = 0, cont2 = 0;

        //Calcule o numero de elementos que o resultado final ira possuir
        int value;
        if (valores.size()%2 == 0 ){
            value = valores.size()/2;
        }else {
            value = valores.size()/2 - valores.size()%2;
            if (value == -1) value = 1;
        }
        
        for(Iterator<SimilarityVO> j = valores.iterator(); j.hasNext();){
            j.next();
            copiaValores.clear();
            for (Iterator<SimilarityVO> i = valores.iterator(); i.hasNext();){
                SimilarityVO s1 = (SimilarityVO) i.next();
                if (cont2 < cont1){
                   s1.setSimilarity(0);
                 }
                copiaValores.add(s1);
                copiaValoresInicial.add(s1);
                ++cont2;
            }
            cont2 = 0;
            for (Iterator<SimilarityVO> i = valores.iterator(); i.hasNext();){
               SimilarityVO s2 = (SimilarityVO) i.next();
               if (s2.getSimilarity() != 0){
                   ++cont;
                   v1Aux = s2.getElementA();
                   v2Aux = s2.getElementB();
                   for (Iterator<SimilarityVO> x = copiaValores.iterator(); x.hasNext();){
                       SimilarityVO s3 = (SimilarityVO) x.next();
                       if (caminha){
                           while(!s2.equals(s3)){
                                s3 = (SimilarityVO) x.next();
                           }                          
                           caminha = false;
                       }
                       if (!s2.equals(s3))
                       if (s3.getElementA().equals(v1Aux) || s3.getElementB().equals(v2Aux)){
                           s3.setSimilarity(0);
                       }
                   }
                   if (cont == 1) {
                       copiaValoresInicial.clear();
                       copiaValoresInicial.addAll(copiaValores);
                   }
                   if (cont == value){
                   for (Iterator<SimilarityVO> x = copiaValores.iterator(); x.hasNext();){
                       SimilarityVO s3 = (SimilarityVO) x.next();
                       if (s3.getSimilarity() != 0){
                           soma += s3.getSimilarity();
                       }
                   }

                   //Verifica se a soma dos valores é a maior e seta os valores na matriz final
                   if (soma > max){
                       max = soma;
                       for (Iterator<SimilarityVO> x = copiaValores.iterator(); x.hasNext();){
                           SimilarityVO s3 = (SimilarityVO) x.next();
                           if (s3.getSimilarity() != 0){
                               //Cria um novo objeto setando os seus valores para guardar ao resultado final
                               SimilarityVO similarityVO = new SimilarityVO();
                               similarityVO.setElementA(s3.getElementA());
                               similarityVO.setElementB(s3.getElementB());
                               similarityVO.setSimilarity(s3.getSimilarity());
                               valorFinal.add(similarityVO);
                           }
                       }
                   }
                        s2.setSimilarity(0);
                        copiaValoresInicial.add(s2);
                        copiaValores.clear();
                        copiaValores.addAll(copiaValoresInicial);
                        cont = 1;
                   }
               }
           }
           cont = 0;
           ++cont1;
           cont2 = 0;
           caminha = true;
        }

        return valorFinal;

    }
}
