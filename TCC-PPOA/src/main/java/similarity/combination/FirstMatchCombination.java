/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.combination;

import similarity.common.SimilarityComparator;
import similarity.common.SimilarityVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class FirstMatchCombination implements ICombination {

    public Collection<SimilarityVO> explore(Collection<SimilarityVO> similaridade){
        
        //Ordenar para que as maiores similaridaes fiquem no topo
        Collections.sort((List)similaridade, new SimilarityComparator());
        
        //Copia uma lista de similaridade para poder fazer a remoção
        Collection<SimilarityVO> similaridadeAUX = new ArrayList<SimilarityVO>();
        Collection<SimilarityVO> similaridadeFINAL = new ArrayList<SimilarityVO>();
        similaridadeAUX.addAll(similaridade);


       for (SimilarityVO similarityVO : similaridade){
           if (similaridadeAUX.contains(similarityVO)){
               similaridadeFINAL.add(similarityVO);
               //Procura em todas as linhas os indicadores já combinados
               for (SimilarityVO similarityVOAUX :similaridade){
                   if (!similaridadeFINAL.contains(similarityVOAUX)) {
                       if (similarityVO.getElementA() == similarityVOAUX.getElementA() || similarityVO.getElementB() == similarityVOAUX.getElementB()){
                           similaridadeAUX.remove(similarityVOAUX);
                       }
                   }
               }
           }
        }
        return similaridadeAUX;
    }
}
