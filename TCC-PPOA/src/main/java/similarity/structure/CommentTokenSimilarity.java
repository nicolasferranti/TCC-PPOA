/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.structure;

import similarity.ISimilarityFunction;
import similarity.common.SimilarityVO;
import similarity.editdistance.IEditDistance;
import similarity.editdistance.TokenizerEditDistance;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author jairo
 */
public class CommentTokenSimilarity implements ISimilarityFunction, StringBasedFunction {

    private Collection<SimilarityVO> similaridade = new ArrayList<SimilarityVO>();
    private IEditDistance strategyEditDistance = new TokenizerEditDistance();

    public CommentTokenSimilarity(){
    }

    public CommentTokenSimilarity(IEditDistance strategyEditDistance){
        this.strategyEditDistance = strategyEditDistance;
    }

    public float execute(OntResource c1, OntResource c2)
    {
        similaridade.clear();

        float similarity = strategyEditDistance.compute(c1.getComment(null), c2.getComment(null));
        SimilarityVO similarityVO = new SimilarityVO();
        similarityVO.setElementA(c1);
        similarityVO.setElementB(c2);
        similarityVO.setSimilarity(similarity);
        similaridade.add(similarityVO);
        return similarity;
    }

    public float sumSimilarities() {
        float soma = 0;
        for (Iterator<SimilarityVO> i = similaridade.iterator(); i.hasNext();){
            SimilarityVO similarityVO = (SimilarityVO) i.next();
            soma = similarityVO.getSimilarity();
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
