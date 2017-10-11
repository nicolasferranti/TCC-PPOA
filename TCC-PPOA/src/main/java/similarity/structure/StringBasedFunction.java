/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.structure;

import similarity.editdistance.IEditDistance;


/**
 *
 * @author Isabella Pires Capobiango
 */
public interface StringBasedFunction{

    void setEditDistance(IEditDistance strategyEditDistance);
    IEditDistance getEditDistance();
}
