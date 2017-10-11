/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pp.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author rooke
 */
public interface IFuncao{
    BigDecimal calcula();
    void setPeso(double peso);
    double getPeso();
    List<IFuncao> getFilhos();
}
