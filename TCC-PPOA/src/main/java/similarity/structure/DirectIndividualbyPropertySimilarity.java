
package similarity.structure;

import similarity.ISimilarityFunction;
import similarity.combination.DeepCombination;
import similarity.combination.ICombination;
import similarity.common.SimilarityVO;
import similarity.editdistance.DamerauLevenshteinEditDistance;
import similarity.editdistance.IEditDistance;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jairo
 */
public class DirectIndividualbyPropertySimilarity extends PenaltyAllowedFunction implements ISimilarityFunction, StringBasedFunction{

   private IEditDistance strategyEditDistance = new DamerauLevenshteinEditDistance();
   private ICombination strategyCalculation = new DeepCombination();
   private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();

   public DirectIndividualbyPropertySimilarity(){
   }

   public DirectIndividualbyPropertySimilarity(IEditDistance strategyEditDistance){
        this(strategyEditDistance, null);
   }

   public DirectIndividualbyPropertySimilarity(ICombination strategyCalculation){
        this(null, strategyCalculation);
   }

   public DirectIndividualbyPropertySimilarity(IEditDistance strategyEditDistance, ICombination strategyCalculation){
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

    // Caso sejam propriedades ent�o verifica TODOS os valores da propriedade
     private float execute(OntProperty p1, OntProperty p2) {

         int max=0,min=0;
         //Testar se o c�digo abaixo est� OK. Principalmente com o listProperties(p1 e p2)
         //Caso o c�digo esteja ok, ainda � necess�rio fazer o c�lculo da similaridade
            // Ou seja, inserir os pares no array, depois a combina��o  e depois o valor da similaridade
         
        for (Individual ind : (List<Individual>) OntologyCache.getAllIndividuals(1))
            for (Statement st1 : (List<Statement>) OntologyCache.getOrAddListIndividualProperty(ind, p1)) {
                max++;
                String valor1 = getValorPropriedade(st1);
                for (Individual ind2 : (List<Individual>) OntologyCache.getAllIndividuals(2)) 
                    for (Statement st2 : (List<Statement>) OntologyCache.getOrAddListIndividualProperty(ind2, p2)) {
                        String valor2 = getValorPropriedade(st2);
                        similaridade.add(new SimilarityVO(ind, ind2, strategyEditDistance.compute(valor1, valor2)));
                    }
            }


        for (Individual ind2 : (List<Individual>) OntologyCache.getAllIndividuals(2)) {
             List aux = OntologyCache.getOrAddListIndividualProperty(ind2, p2);
             min += aux!=null ? aux.size() : 0;
        }
        
        similaridade = strategyCalculation.explore(similaridade);

        return averageWithPenalty(sumSimilarities(), max, min);
     }

    private String getValorPropriedade(Statement st2) {
        //if(st2.getObject().isAnon())
        //    return "nonulo";
        String valor2 = st2.getObject().toString();
        if (valor2.indexOf("^^") != -1) valor2 = valor2.substring(0, valor2.indexOf("^^"));
        return valor2;
    }

     private float execute(OntClass c1, OntClass c2)
    {
        List<? extends OntResource> lista1 = OntologyCache.getOrAddInstances(c1);
        List<? extends OntResource> lista2 = OntologyCache.getOrAddInstances(c2);

        List<OntProperty> propriedadesOnto1 = OntologyCache.getOrAddListIndirectProperties(c1);
        List<OntProperty> propriedadesOnto2 = OntologyCache.getOrAddListIndirectProperties(c2);
        
        int max = lista1.size();
        int min = lista2.size();

        for (OntResource ind1 : lista1)
            for (OntResource ind2 : lista2) {
                SimilarityVO similarityVO = new SimilarityVO(
                        ind1, 
                        ind2, 
                        calculaSimilaridade(
                            (List<StatementImpl>) OntologyCache.getOrAddListIndividualProperties(ind1),
                            (List<StatementImpl>) OntologyCache.getOrAddListIndividualProperties(ind2),
                            propriedadesOnto1, propriedadesOnto2
                        ));
                similaridade.add(similarityVO);
                }
            
            
        
        similaridade = strategyCalculation.explore(similaridade);
        return averageWithPenalty(sumSimilarities(), max, min);
    }
     
     private float calculaSimilaridade(List<StatementImpl> lista1, List<StatementImpl> lista2, List propsOnto1, List propsOnto2) {
         
         Collection<SimilarityVO> similaridadesAux = new ArrayList<SimilarityVO>(); 
         
         int max=0;
         int min=0;
         
         for (StatementImpl prop1 : lista1) 
             if (!prop1.getPredicate().getLocalName().equals("type") && !prop1.getPredicate().getLocalName().equals("label")) {
                String valor1 = getValor(prop1);
                String nome1 = prop1.getPredicate().getLocalName();

                OntResource or1 = getOntResource(nome1, propsOnto1);
                if (or1==null)
                    continue;
                
                max++;
                for(StatementImpl prop2 : lista2) 
                    if(!prop2.getPredicate().getLocalName().equals("type") && !prop2.getPredicate().getLocalName().equals("label")) {
                        String valor2 = getValor(prop2);
                        String nome2 = prop2.getPredicate().getLocalName();
                        float sim = strategyEditDistance.compute(nome1, nome2)
                                + strategyEditDistance.compute(valor1, valor2);

                        OntResource or2 = getOntResource(nome2, propsOnto2);
                        if (or2==null) 
                            continue;
                        
                        
                        similaridadesAux.add(new SimilarityVO(or1, or2, sim/2));
                }
            }
         
         for (StatementImpl prop : lista2)
             if(!prop.getPredicate().getLocalName().equals("type") 
                     && !prop.getPredicate().getLocalName().equals("label")
                     && getOntResource(prop.getPredicate().getLocalName(), propsOnto2) != null)
                 min++;
         
         similaridadesAux = strategyCalculation.explore(similaridadesAux);
         return averageWithPenalty(sumSimilarities(similaridadesAux), max, min);
     }

    private OntResource getOntResource(String re, List lista) {
        
        for(OntProperty prop : (List<OntProperty>) lista) 
            if (prop.getLocalName().equals(re))
                return prop;        
        
        return null;
        
    }
     
     
    private String getValor(StatementImpl prop1) {
        String valor = prop1.getObject().toString();
        if (valor.indexOf("^^") != -1)
            valor = valor.substring(0, valor.indexOf("^^"));
        if (prop1.getObject().isAnon())
            valor = "nonulo";
        return valor;
    }
     
     
    public float sumSimilarities() {
        return sumSimilarities(this.similaridade);
    }

    private float sumSimilarities(Collection<SimilarityVO> colecao) {
        float soma = 0;
        for (Iterator<SimilarityVO> i = colecao.iterator(); i.hasNext();){
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
