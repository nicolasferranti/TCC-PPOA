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

/**
 *
 * @author Isabella Pires Capobiango
 */
public class DirectObjectPropertybyRangeSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{

    private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
    private ICombination strategyCalculation = new DeepCombination();
    private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

    public DirectObjectPropertybyRangeSimilarity(){

    }
    public DirectObjectPropertybyRangeSimilarity(IEditDistance strategyEditDistance){
       this.strategyEditDistance = strategyEditDistance;

    }
    public DirectObjectPropertybyRangeSimilarity( ICombination strategyCalculation){
       this.strategyCalculation = strategyCalculation;
    }
    public DirectObjectPropertybyRangeSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
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
           OntResource or1 = p1.getRange();
           OntResource or2 = p2.getRange();
            
           float similarity;
           if (or1==null && or2==null) similarity = 1;
           else if (or1==null || or2== null) similarity = 0;
           else similarity = strategyEditDistance.compute(or1.getLocalName(), or2.getLocalName());

           SimilarityVO similarityVO = new SimilarityVO();
           similarityVO.setElementA(p1);
           similarityVO.setElementB(p2);
           similarityVO.setSimilarity(similarity);
           similaridade.add(similarityVO);
           return similarity;
     }

     private float execute(OntClass c1, OntClass c2){
         int max = 0, min = 0;
         Collection<OntProperty> listaProps2 = OntologyCache.getOrAddListProperties(c2);

         for (Iterator i = OntologyCache.getOrAddListProperties(c1).iterator(); i.hasNext(); ) {
             OntProperty p1 = (OntProperty) i.next();
             OntResource range1 = p1.getRange();
              if (p1.isObjectProperty() && range1 != null){
                  max += 1;
                  for (OntProperty p2 : listaProps2) {
                     OntResource range2 = p2.getRange();
                     if (p2.isObjectProperty() && range2 != null){
                       SimilarityVO similarityVO = new SimilarityVO();
                       similarityVO.setElementA(p1);
                       similarityVO.setElementB(p2);
                       similarityVO.setSimilarity(strategyEditDistance.compute(range1.getLocalName(), range2.getLocalName()));
                       similaridade.add(similarityVO);
                     }
                  }
              }
         }
          for (OntProperty p2 : listaProps2)
              if (p2.isObjectProperty())
                  min += 1;                   
          
         similaridade = strategyCalculation.explore(similaridade);
         return  averageWithPenalty(sumSimilarities(), max, min);
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
