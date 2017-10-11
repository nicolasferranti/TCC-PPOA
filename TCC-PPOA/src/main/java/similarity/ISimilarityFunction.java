/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity;

import similarity.common.SimilarityVO;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.Collection;

/**
 *
 * @author Isabella Pires Capobiango
 */
public interface ISimilarityFunction {

    float execute(OntResource c1, OntResource c2);
    float sumSimilarities();
    Collection<SimilarityVO> getSimilarityTable();

}
