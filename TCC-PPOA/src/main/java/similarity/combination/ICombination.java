/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.combination;

import similarity.common.SimilarityVO;
import java.util.Collection;

/**
 *
 * @author Isabella Pires Capobiango
 */
public interface ICombination {
    Collection<SimilarityVO> explore(Collection<SimilarityVO> similaridade);
}
