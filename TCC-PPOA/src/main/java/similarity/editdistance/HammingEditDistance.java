/**
 * A distância HammingEditDistance entre duas strings de comprimento igual, é o número de
 * posições que os caracteres correspondentes são diferentes, ou seja, o numero de
 * operações de substituição necessarias para mudar uma string para outra.
 * Ou seja, quando mais proximo de 1 as strings são iguais, e mais proximo
 * de 0 diferentes
 */

package similarity.editdistance;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class HammingEditDistance implements IEditDistance{

    public float compute(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        
        float d = 0;
        if (s1.length() == s2.length())
        {
            for (int i = 0; i < s1.length(); i++)
            {
                 if (s1.charAt(i) != s2.charAt(i))   d++;
            }
            return (float)1 - (d/s1.length());
        }
        else
        {
            return (float) 0.00;
        }
    }
}
