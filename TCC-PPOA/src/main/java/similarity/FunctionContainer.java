/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity;

import similarity.common.FunctionWeightVO;
import similarity.common.SimilarityVO;
import similarity.common.WeightVO;
import similarity.exception.WeightException;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class FunctionContainer implements ISimilarityFunction {

    private Collection<FunctionWeightVO> container = new ArrayList<FunctionWeightVO>();
    private String nome;

    public void addFunction(ISimilarityFunction function, WeightVO weight){
        FunctionWeightVO functionWeightVO = new FunctionWeightVO();
        functionWeightVO.setFunction(function);
        functionWeightVO.setWeight(weight);
        container.add(functionWeightVO);
    }

   public Collection<FunctionWeightVO> getContainer(){
       return container;
   }

   public void setContainer(Collection<FunctionWeightVO> functions){
       container = functions;
   }

    public boolean validateWeights() throws WeightException{
        Iterator i = container.iterator();
        boolean verifica = true;
        float soma = 0;
        while (i.hasNext()){
            FunctionWeightVO functionWeightVO = (FunctionWeightVO)i.next();
            if (functionWeightVO.getFunction() instanceof FunctionContainer){
                FunctionContainer functionContainer = (FunctionContainer) functionWeightVO.getFunction();
                verifica = functionContainer.validateWeights();
            }else soma += functionWeightVO.getWeight().getWeight();
        }

        if (soma > 1 || !verifica) {
            verifica = false;
            throw new WeightException("Valor Inválido.");
        }
        return verifica;
    }

    public float execute(OntResource c1, OntResource c2) {
        float result = 0;
        //try {
            //if (validateWeights()) {
                Collection<FunctionWeightVO> functions = container;
                Iterator j = (Iterator) functions.iterator();
                while (j.hasNext()) {
                    FunctionWeightVO functionWeightVO = (FunctionWeightVO) j.next();
                    result += functionWeightVO.getWeight().getWeight() * functionWeightVO.getFunction().execute(c1, c2);
                }
//        } catch (WeightException ex) {
//            Logger.getLogger(FunctionContainer.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return result;
    }

    public float sumSimilarities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<SimilarityVO> getSimilarityTable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return FunctionContainer.class.toString() + "@" + this.nome;
    }

    


}



