/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.structure;

import similarity.ISimilarityFunction;
import similarity.combination.DeepCombination;
import similarity.combination.ICombination;
import similarity.common.SimilarityVO;
import similarity.common.WeightVO;
import similarity.editdistance.DamerauLevenshteinEditDistance;
import similarity.editdistance.IEditDistance;
import similarity.exception.WeightException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Jairo
 */
public class DirectIndividualSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{

   private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
   private ICombination strategyCalculation = new DeepCombination();
   private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

   private DirectIndividualbyNameSimilarity nameFunction = new DirectIndividualbyNameSimilarity(this.strategyEditDistance, this.strategyCalculation);
   private DirectIndividualbyPropertySimilarity propsFunction = new DirectIndividualbyPropertySimilarity(this.strategyEditDistance, this.strategyCalculation);

   
   public DirectIndividualSimilarity(){
       this(null, null);
   }

   public DirectIndividualSimilarity(IEditDistance strategyEditDistance){
       this(strategyEditDistance, null);
   }

   public DirectIndividualSimilarity(ICombination strategyCalculation){
       this(null, strategyCalculation);
   }

   public DirectIndividualSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
       if (strategyEditDistance != null) this.strategyEditDistance = strategyEditDistance;
       if (strategyCalculation  != null) this.strategyCalculation  = strategyCalculation;

       this.nameFunction  = new DirectIndividualbyNameSimilarity(strategyEditDistance, strategyCalculation);
       this.propsFunction = new DirectIndividualbyPropertySimilarity(strategyEditDistance, strategyCalculation);
   }

    @Override
    public void setPenalty(WeightVO penalty) throws WeightException{
        super.setPenalty(penalty);
        this.nameFunction.setPenalty(penalty);
        this.propsFunction.setPenalty(penalty);
    }
   
     public float execute(OntResource c1, OntResource c2){

         similaridade.clear();

         if (c1 instanceof OntClass)
            return execute((OntClass) c1, (OntClass) c2);
         else
            return execute((OntProperty) c1, (OntProperty) c2);
    }

    // Caso sejam propriedades, n�o verifica todos os valores das propriedades
     private float execute(OntProperty p1, OntProperty p2) {
           float similarity = propsFunction.execute(p1, p2);

           similaridade.add(new SimilarityVO(p1, p2, similarity));
           return similarity;
     }

     private float execute(OntClass c1, OntClass c2)
    {
        // Checa quantos individuos com o mesmo nome o conceito possui
        float similarity = nameFunction.execute(c1, c2);
        
        // Se o conceito n�o possui individuos com o mesmo nome, ent�o checa se o valor das propriedades dos individuos desse conceito s�o iguais
        if (similarity==0)
            similarity = propsFunction.execute(c1, c2);

        similaridade.add(new SimilarityVO(c1, c2, similarity));
        
        return similarity;
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
