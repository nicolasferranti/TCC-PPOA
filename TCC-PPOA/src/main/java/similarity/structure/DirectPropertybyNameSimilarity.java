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
public class DirectPropertybyNameSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{

    private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
    private ICombination strategyCalculation = new DeepCombination();
    private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

    public DirectPropertybyNameSimilarity(){

    }

    public DirectPropertybyNameSimilarity(ICombination strategyCalculation ){
        this.strategyCalculation = strategyCalculation;
    }

    public DirectPropertybyNameSimilarity(IEditDistance strategyEditDistance){
        this.strategyEditDistance = strategyEditDistance;
    }

    public DirectPropertybyNameSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation ){
        this.strategyEditDistance = strategyEditDistance;
        this.strategyCalculation = strategyCalculation;
    }

     public float execute(OntResource c1, OntResource c2){

         similaridade.clear();

         if (c1 instanceof OntClass)
            return execute((OntClass) c1, (OntClass) c2);
         else
            return execute((OntProperty) c1, (OntProperty) c2);
    }

     private float execute(OntProperty c1, OntProperty c2) {
           float similarity = strategyEditDistance.compute(c1.getLocalName(), c2.getLocalName());
           SimilarityVO similarityVO = new SimilarityVO();
           similarityVO.setElementA(c1);
           similarityVO.setElementB(c2);
           similarityVO.setSimilarity(similarity);
           similaridade.add(similarityVO);
           return similarity;
     }

     private float execute(OntClass c1, OntClass c2){

         Collection<OntProperty> listaProps2 = OntologyCache.getOrAddListProperties(c2);
         int max = 0;
         int min = listaProps2.size();

         for (Iterator i = OntologyCache.getOrAddListProperties(c1).iterator(); i.hasNext(); ) {
             OntProperty p1 = (OntProperty) i.next();
             max += 1;
             for (OntProperty p2 : listaProps2) {
                   SimilarityVO similarityVO = new SimilarityVO();
                   similarityVO.setElementA(p1);
                   similarityVO.setElementB(p2);
                   similarityVO.setSimilarity(strategyEditDistance.compute(p1.getLocalName(), p2.getLocalName()));
                   similaridade.add(similarityVO);
             }
          }

         similaridade = strategyCalculation.explore(similaridade);
         return averageWithPenalty(sumSimilarities(), max, min) ;
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
