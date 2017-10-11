package similarity.structure;

import similarity.ISimilarityFunction;
import similarity.TabelaSimilaridade;
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
 * @author Jairo
 */
public class DirectPropertybyRangeDomainSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{

    private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
    private ICombination strategyCalculation = new DeepCombination();
    private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

    public DirectPropertybyRangeDomainSimilarity(){

    }
    public DirectPropertybyRangeDomainSimilarity(IEditDistance strategyEditDistance){
       this.strategyEditDistance = strategyEditDistance;

    }
    public DirectPropertybyRangeDomainSimilarity( ICombination strategyCalculation){
       this.strategyCalculation = strategyCalculation;
    }
    public DirectPropertybyRangeDomainSimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
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


    //Se forem propriedades, pegar a similaridade do range e do domain e tirar a m�dia.
     private float execute(OntProperty p1, OntProperty p2) {
           float similarityRange = getSimilaridadeRange(p1, p2);
           float similarityDomain = getSimilaridadeDomain(p1, p2);
           
           float similarity = (similarityRange + similarityDomain) / 2;

           SimilarityVO similarityVO = new SimilarityVO();
           similarityVO.setElementA(p1);
           similarityVO.setElementB(p2);
           similarityVO.setSimilarity(similarity);
           similaridade.add(similarityVO);
           return similarity;
     }

     private float getSimilaridadeDomain(OntProperty op1, OntProperty op2) {
         OntResource or1 = op1.getDomain();
         OntResource or2 = op2.getDomain();

         return verificaOcorrencia(or1, or2);
     }

     private float getSimilaridadeRange(OntProperty op1, OntProperty op2) {
         OntResource or1 = op1.getRange();
         OntResource or2 = op2.getRange();

         return verificaOcorrencia(or1, or2);
     }

    private float verificaOcorrencia(OntResource or1, OntResource or2) {
        float similarity = -1;
        if (or1==null && or2==null) similarity = 1;
        else if (or1==null || or2== null) similarity = 0;

        //Pegar a similaridade calculada pelo sistema!
        // tenta verificar se a similaridade entre os dois conceitos de range j� foi calculada
        else {
          for (SimilarityVO similarityVO : TabelaSimilaridade.getListaSimilaridades()) 
              if (similarityVO.getElementA().getURI().equals(or1.getURI()) && similarityVO.getElementB().getURI().equals(or2.getURI())) {
                  similarity = similarityVO.getSimilarity();
                  break;
              }

          if (similarity == -1)
              similarity = strategyEditDistance.compute(or1.getLocalName(), or2.getLocalName());
      }

      return similarity;
    }

     // se forem conceitos, faz somente a similiaridade do range
     private float execute(OntClass c1, OntClass c2){
         int max = 0;
         Collection<OntProperty> listaProps2 = OntologyCache.getOrAddListProperties(c2);
         int min = 0;

         for (Iterator i = OntologyCache.getOrAddListProperties(c1).iterator(); i.hasNext(); ) {
             OntProperty p1 = (OntProperty) i.next();
             OntResource range1 = p1.getRange();
              if (range1 != null){
                  max += 1;
                  for (OntProperty p2 : listaProps2) {
                     OntResource range2 = p2.getRange();
                     if (range2 != null){
                       SimilarityVO similarityVO = new SimilarityVO();
                       similarityVO.setElementA(p1);
                       similarityVO.setElementB(p2);
                       similarityVO.setSimilarity(strategyEditDistance.compute(range1.getLocalName(), range2.getLocalName()));
                       similaridade.add(similarityVO);
                     }
                  }
              }
         }

        for (OntProperty p2 : listaProps2)  {
            OntResource range2 = p2.getRange();
            if (range2 != null) min++;
        }

         
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
