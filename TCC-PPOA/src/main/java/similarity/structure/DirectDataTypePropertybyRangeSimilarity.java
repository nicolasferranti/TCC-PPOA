/*
 * Verifica a semelhança dos DataTypeProperty pelo Range, os tipos de ranges
 * foram divididos em 5 listas e 4 ficaram sem correlação. Quando dois ranges
 * diferentes se encontram na msm lista são considerados iguais.
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
public class DirectDataTypePropertybyRangeSimilarity extends PenaltyAllowedFunction implements ISimilarityFunction {

    private ArrayList listaRangeNumero = new ArrayList();
    private ArrayList listaRangeData = new ArrayList();
    private ArrayList listaRangeString = new ArrayList();
    private ArrayList listaRangeReferencias = new ArrayList();
    private ArrayList listaRangeCoringas = new ArrayList();
    private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

    public DirectDataTypePropertybyRangeSimilarity() {
        listaRangeNumero.add("decimal");
        listaRangeNumero.add("float");
        listaRangeNumero.add("float");
        listaRangeNumero.add("int");
        listaRangeNumero.add("integer");
        listaRangeNumero.add("long");
        listaRangeNumero.add("negativeInteger");
        listaRangeNumero.add("nonNegativeInteger");
        listaRangeNumero.add("nonPositiveInteger");
        listaRangeNumero.add("positiveInteger");
        listaRangeNumero.add("unsignedInt");
        listaRangeNumero.add("unsignedLong");
        listaRangeNumero.add("unsignedShort");
        listaRangeNumero.add("short");
        listaRangeNumero.add("byte");
        listaRangeNumero.add("unsignedByte");
        listaRangeNumero.add("hexBinary");

        listaRangeData.add("date");
        listaRangeData.add("dateTime");
        listaRangeData.add("dateTimeStamp");
        listaRangeData.add("duration");
        listaRangeData.add("gDay");
        listaRangeData.add("gMonth");
        listaRangeData.add("gMonthYear");
        listaRangeData.add("gYear");
        listaRangeData.add("gYearMonth");
        listaRangeData.add("time");

        listaRangeString.add("normalizedString");
        listaRangeString.add("string");
        listaRangeString.add("token");
        listaRangeString.add("NMTOKEN");
        listaRangeString.add("NMTOKENS");

        listaRangeReferencias.add("ID");
        listaRangeReferencias.add("NCName");
        listaRangeReferencias.add("IDREF");
        listaRangeReferencias.add("IRDREFS");
        listaRangeReferencias.add("QName");

        listaRangeCoringas.add("anySimpleType");
        listaRangeCoringas.add("anyType");
    }
    
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
             if (p1.isDatatypeProperty()){
                 max += 1;
              for (OntProperty p2 : listaProps2) {
                  if (p2.isDatatypeProperty()){
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
        int verifica = 0;
        OntResource or = p1.getRange();
        String p1Range = or != null ? or.getLocalName() : "";
        or = p2.getRange();
        String p2Range = or != null ? or.getLocalName() : "";

        if (!p1Range.equals(p2Range)) {
            if (listaRangeNumero.contains(p1Range) && listaRangeNumero.contains(p2Range)) {
                verifica = 1;
            } else if (listaRangeData.contains(p1Range) && listaRangeData.contains(p2Range)) {
                verifica = 1;
            } else if (listaRangeString.contains(p1Range) && listaRangeString.contains(p2Range)) {
                verifica = 1;
            } else if (listaRangeReferencias.contains(p1Range) && listaRangeReferencias.contains(p2Range)) {
                verifica = 1;
            } else if (listaRangeCoringas.contains(p1Range) && listaRangeCoringas.contains(p2Range)) {
                verifica = 1;
            }
        } else {
            verifica = 1;
        }
        return verifica;
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