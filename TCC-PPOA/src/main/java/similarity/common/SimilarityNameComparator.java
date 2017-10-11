/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.common;

import java.util.Comparator;

/**
 *
 * @author jairo
 */
public class SimilarityNameComparator implements Comparator<SimilarityVO>{

    public int compare(SimilarityVO o1, SimilarityVO o2) {
        if (o1.getElementA().getLocalName().equals(o2.getElementA().getLocalName()))
            return Float.compare(o2.getSimilarity(), o1.getSimilarity());
        else 
            return o1.getElementA().getLocalName().compareTo(o2.getElementA().getLocalName());
    }

    

}
