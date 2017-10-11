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
public class DirectIndividualbyNameSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{

   private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
   private ICombination strategyCalculation = new DeepCombination();
   private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

   public DirectIndividualbyNameSimilarity(){
   }

   public DirectIndividualbyNameSimilarity(IEditDistance strategyEditDistance){
        this(strategyEditDistance, null);
   }

   public DirectIndividualbyNameSimilarity(ICombination strategyCalculation){
        this(null, strategyCalculation);
   }

    public DirectIndividualbyNameSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
       if (strategyEditDistance != null) this.strategyEditDistance = strategyEditDistance;
       if (strategyCalculation  != null) this.strategyCalculation  = strategyCalculation;
    }

     public float execute(OntResource c1, OntResource c2){

         similaridade.clear();

         if (c1 instanceof OntClass)
            return execute((OntClass) c1, (OntClass) c2);
         else
            return execute((OntProperty) c1, (OntProperty) c2);
    }

    // Caso sejam propriedades, n�o possuem individuos, entao o retorno � sempre 1.
     private float execute(OntProperty p1, OntProperty p2) {
           float similarity = 1;
           SimilarityVO similarityVO = new SimilarityVO();
           similarityVO.setElementA(p1);
           similarityVO.setElementB(p2);
           similarityVO.setSimilarity(similarity);
           similaridade.add(similarityVO);
           return similarity;
     }

     private float execute(OntClass c1, OntClass c2)
    {
        List<? extends OntResource> lista1 = OntologyCache.getOrAddInstances(c1);
        List<? extends OntResource> lista2 = OntologyCache.getOrAddInstances(c2);

        int max=0;
        int min=0;

        for (OntResource ind1 : lista1)
        {
            if (ind1.getLocalName() != null){ max++;
            for (OntResource ind2 : lista2)
            {
                if (ind1.getLocalName().equals(ind2.getLocalName())){
                similaridade.add(new SimilarityVO(ind1, ind2, 1));
                }
            }
            }
        }

        for (OntResource ind2 : lista2)
            if(ind2.getLocalName()!=null) 
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
