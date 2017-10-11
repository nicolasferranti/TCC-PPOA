/*
 * Verifica se o range e igual, caso seja retorna 1
 */

package similarity.structure;


import similarity.ISimilarityFunction;
import similarity.common.SimilarityVO;
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
public class DirectDataTypePropertybyRangeEqualSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction{

     private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

     //Na verdade est� sendo util tanto para DataTypeProperty quanto para ObjectProperty quando a entrada sao OntProperty
     public float execute(OntResource c1, OntResource c2){

         similaridade.clear();

         if (c1 instanceof OntClass)
            return execute((OntClass) c1, (OntClass) c2);
         else
            return execute((OntProperty) c1, (OntProperty) c2);
    }

     private float execute(OntProperty p1, OntProperty p2) {
           float similarity = verificaSimilaridade(p1, p2);
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
             if (p1.getRange() != null && p1.isDatatypeProperty() && c1.hasDeclaredProperty(p1, true)) {
                 max += 1;

              for (OntProperty p2 : listaProps2) {
                  if (p2.isDatatypeProperty())
                  {
                      SimilarityVO similarityVO = new SimilarityVO();
                      similarityVO.setElementA(p1);
                      similarityVO.setElementB(p2);
                      similarityVO.setSimilarity(verificaSimilaridade(p1, p2));
                      similaridade.add(similarityVO);
                  }
              }
             }
         }

          for (OntProperty p2 : listaProps2)
             if (p2.isDatatypeProperty())
                      min += 1;

          return averageWithPenalty(sumSimilarities(), max, min);
     }

     private int verificaSimilaridade(OntProperty p1, OntProperty p2) {
         String r1 = p1.getRange()!=null ? p1.getRange().getURI().toString() : "";
         String r2 = p2.getRange()!=null ? p2.getRange().getURI().toString() : "";

         return (r1.equals(r2)) ? 1 : 0;
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
}
