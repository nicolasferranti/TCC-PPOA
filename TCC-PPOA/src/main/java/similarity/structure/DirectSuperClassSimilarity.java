/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.structure;

import similarity.ISimilarityFunction;
import similarity.combination.DeepCombination;
import similarity.combination.ICombination;
import similarity.common.SimilarityVO;
import similarity.editdistance.DamerauLevenshteinEditDistance;
import similarity.editdistance.IEditDistance;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Isabella Pires Capobiango
 */
public class DirectSuperClassSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{
    
   private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
   private ICombination strategyCalculation = new DeepCombination();
   private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

    public DirectSuperClassSimilarity(){
    }

    public DirectSuperClassSimilarity(IEditDistance strategyEditDistance){
        this.strategyEditDistance = strategyEditDistance;
    }

    public DirectSuperClassSimilarity(ICombination strategyCalculation){
        this.strategyCalculation = strategyCalculation;
    }

    public DirectSuperClassSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
        this.strategyEditDistance = strategyEditDistance;
        this.strategyCalculation = strategyCalculation;
    }


     //Na verdade est� sendo util tanto para DataTypeProperty quanto para ObjectProperty quando a entrada � OntProperty
     public float execute(OntResource c1, OntResource c2){

         similaridade.clear();

         if (c1 instanceof OntClass)
            return execute((OntClass) c1, (OntClass) c2);
         else
            return execute((OntProperty) c1, (OntProperty) c2);
    }


     private float execute(OntProperty p1, OntProperty p2) {

        List<? extends OntProperty> lista1 = p1.listSuperProperties(true).toList();
        List<? extends OntProperty> lista2 = p2.listSuperProperties(true).toList();

        int max = lista1.size();
        int min = lista2.size();
        for (OntProperty superClasse1 : lista1)
        {
            if (superClasse1.getLocalName() != null) {
                for (OntProperty superClasse2 : lista2) {
                    if (superClasse2.getLocalName() != null){
                        SimilarityVO similarityVO = new SimilarityVO();
                        similarityVO.setElementA(superClasse1);
                        similarityVO.setElementB(superClasse2);
                        similarityVO.setSimilarity(strategyEditDistance.compute(superClasse1.getLocalName(), superClasse2.getLocalName()));
                        similaridade.add(similarityVO);
                    } else min--;

                }
            } else max--;
        }
        similaridade = strategyCalculation.explore(similaridade);
        return averageWithPenalty(sumSimilarities(), max, min);
     }


    private float execute(OntClass c1, OntClass c2)
     {
        List<? extends OntClass> lista1 = OntologyCache.getOrAddSuperClasses(c1);
        List<? extends OntClass> lista2 = OntologyCache.getOrAddSuperClasses(c2);
        int max = lista1.size();
        int min=0;
        
        for (OntClass superClasse1 : lista1)
        {
            if (superClasse1.getLocalName() != null) {
                max++;
                for (OntClass superClasse2 : lista2) {
                    if (superClasse2.getLocalName() != null){
                        SimilarityVO similarityVO = new SimilarityVO();
                        similarityVO.setElementA(superClasse1);
                        similarityVO.setElementB(superClasse2);
                        similarityVO.setSimilarity(strategyEditDistance.compute(superClasse1.getLocalName(), superClasse2.getLocalName()));
                        similaridade.add(similarityVO);
                    };
                }
            } 
        }
        
        for (OntClass superClasse2 : lista2) 
            if (superClasse2.getLocalName() != null)
                min++;        
        
        similaridade = strategyCalculation.explore(similaridade);
        return averageWithPenalty(sumSimilarities(), max, min);
    }

     public float sumSimilarities() {
        float soma = 0;
        for (Iterator<SimilarityVO> i = similaridade.iterator(); i.hasNext();){
            SimilarityVO similarityVO = (SimilarityVO) i.next();
            soma += similarityVO.getSimilarity();
        }
        return soma;
    }
     
    public Collection<SimilarityVO> getSimilarityTable() {
        return similaridade;
    }

    public void setEditDistance(IEditDistance strategyEditDistance) {
        this.strategyEditDistance = strategyEditDistance;
    }

    public IEditDistance getEditDistance() {
        return strategyEditDistance;
    }

}
