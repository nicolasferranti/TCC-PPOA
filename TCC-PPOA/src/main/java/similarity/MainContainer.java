/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity;

import similarity.common.SimilarityVO;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class MainContainer extends FunctionContainer{
    
    private Collection<SimilarityVO> conceitos = new ArrayList<SimilarityVO>();

    public void setSimilarityTable(Collection<SimilarityVO> listaConceitos){
        this.conceitos = listaConceitos;
    }


}
