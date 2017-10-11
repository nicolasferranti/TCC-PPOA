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
public class SimilarityComparator implements Comparator<SimilarityVO>{

    public int compare(SimilarityVO o1, SimilarityVO o2) {
        return Float.compare(o2.getSimilarity(), o1.getSimilarity());
    }

    

}
