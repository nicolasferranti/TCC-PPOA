/**
 * Retorna 1 para strings iguais e 0 para diferentes.
 */

package similarity.editdistance;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class EqualsEditDistance implements IEditDistance {

     public float compute(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        return (float) (s1.equals(s2) ? 1.0 : 0.0);
     }
}


