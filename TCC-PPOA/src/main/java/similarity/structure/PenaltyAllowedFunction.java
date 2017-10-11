package similarity.structure;

import similarity.combination.ICombination;
import similarity.common.WeightVO;
import similarity.exception.WeightException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Isabella Pires Capobiango
 */
public abstract class PenaltyAllowedFunction {

    private WeightVO penalty;
    private ICombination combination;

    public void setCombinationFunction(ICombination strategyCalculation){
        combination = strategyCalculation;
    }

    public ICombination getCombinationFunction(){
        return combination;
    }

    public void setPenalty(WeightVO penalty) throws WeightException{
        if (penalty.getWeight() > 1)  {
            throw new WeightException("Penalidade maior que 1.");
        }else this.penalty = penalty;
    }

    public WeightVO getPenalty(){
        return penalty;
    }

     protected float averageWithPenalty(float soma, int max, int min){
         int aux;
         if (max == 0 && min == 0) return (float) 1.0;

         if (max < min){
             aux = max;
             max = min;
             min = aux;
         }
         float valorFinal =  (soma / (min + (penalty.getWeight() * (max - min))));
         if (Float.isNaN(valorFinal) || Float.isInfinite(valorFinal)) valorFinal = 0;
         return valorFinal;
     }

}
