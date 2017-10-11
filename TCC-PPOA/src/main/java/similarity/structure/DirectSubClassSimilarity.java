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
public class DirectSubClassSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction  {
    private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
    private ICombination strategyCalculation = new DeepCombination();
    private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

    public DirectSubClassSimilarity(){
    }

    public DirectSubClassSimilarity(IEditDistance strategyEditDistance){
        this.strategyEditDistance = strategyEditDistance;

    }

    public DirectSubClassSimilarity(ICombination strategyCalculation){
        this.strategyCalculation = strategyCalculation;
    }

     public DirectSubClassSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
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

        List<? extends OntProperty> lista1 = p1.listSubProperties(true).toList();
        List<? extends OntProperty> lista2 = p2.listSubProperties(true).toList();

        int max = lista1.size();
        int min = lista2.size();

        for (OntProperty subClasse1 : lista1 )
        {
            for (OntProperty subClasse2 : lista2)
            {
                SimilarityVO similarityVO = new SimilarityVO();
                similarityVO.setElementA(subClasse1);
                similarityVO.setElementB(subClasse2);
                similarityVO.setSimilarity(strategyEditDistance.compute(subClasse1.getLocalName(), subClasse2.getLocalName()));
                similaridade.add(similarityVO);
            }
        }
        similaridade = strategyCalculation.explore(similaridade);

        return averageWithPenalty(sumSimilarities(), max, min);
     }


    private float execute(OntClass c1, OntClass c2)
    {
        List<? extends OntClass> lista1 = OntologyCache.getOrAddSubClasses(c1);
        List<? extends OntClass> lista2 = OntologyCache.getOrAddSubClasses(c2);

        int max = lista1.size();
        int min = lista2.size();

        for (OntClass subClasse1 : lista1)
        {
            for (OntClass subClasse2 : lista2)
            {
                SimilarityVO similarityVO = new SimilarityVO();
                similarityVO.setElementA(subClasse1);
                similarityVO.setElementB(subClasse2);
                similarityVO.setSimilarity(strategyEditDistance.compute(subClasse1.getLocalName(), subClasse2.getLocalName()));
                similaridade.add(similarityVO);
            }           
        }
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

