/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
*/

package similarity.common;

import similarity.exception.WeightException;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class WeightVO {

    private float weight;

    public void setWeight(float weight){
        this.weight = weight;
    }

    public float getWeight(){
        return weight;
    }

    public WeightVO(float value) throws WeightException{
        if (value > 1 || value < 0){
            throw new WeightException("Valor Inválido.");
        }
        else this.weight = value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.getWeight());
    }


}
