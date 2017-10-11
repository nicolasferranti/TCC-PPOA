/**
 * Na distância Jaro-Winkler quanto mais proximo de 1, mais as strings
 * são semelhantes. Sua utilização é mais adequada para strings curtas.
 * A operação realizada é a transposição.
 */

package similarity.editdistance;

import java.util.Arrays;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class JaroWinklerEditDistance implements IEditDistance {

    private final float mWeightThreshold;
    private final int mNumChars;

    /**
     *Define a distância basica do Jaro, sem as modificações de Winkler.
     */     
    public JaroWinklerEditDistance() {
        this(Float.POSITIVE_INFINITY,0);
    }

    /**
     * Defini a distância Jaro Winkler com modificações do limite de peso e
     * do número inicial dos caracteres.
     */
    public JaroWinklerEditDistance(float weightThreshold, int numChars) {
        mNumChars = numChars;
        mWeightThreshold = weightThreshold;
    }

     /**
     * Distância constante. Valores iguais a do construtor sem parametros
     */
    public static final JaroWinklerEditDistance JARO_DISTANCE
        = new JaroWinklerEditDistance();

    /**
     * Distância constante com os padões definidos como Winkler
     */
    public static final JaroWinklerEditDistance JARO_WINKLER_DISTANCE
        = new JaroWinklerEditDistance((float) 0.7,4);

    /**
     * Retorna o valor da comparação das strings.
     * A distância cairá no intervalo de 0(nenhuma semelhança) a 1(combinação perfeita)
     */
    public float compute(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        
        int len1 = s1.length();
        int len2 = s2.length();

        //verifica se a string é diferente de vazio.
        if (len1 == 0)
            return (float) (len2 == 0 ? 1.0 : 0.0);

        //verifica o numero de combinações
        int  searchRange = Math.max(0,Math.max(len1,len2)/2 - 1);

        //preenche os vetores das strings
        boolean[] matched1 = new boolean[len1];
        Arrays.fill(matched1,false);
        boolean[] matched2 = new boolean[len2];
        Arrays.fill(matched2,false);

        int numCommon = 0;
        for (int i = 0; i < len1; ++i) {
            int start = Math.max(0,i-searchRange);
            int end = Math.min(i+searchRange+1,len2);
            for (int j = start; j < end; ++j) {
                if (matched2[j]) continue;
                if (s1.charAt(i) != s2.charAt(j))
                    continue;
                matched1[i] = true;
                matched2[j] = true;
                ++numCommon;
                break;
            }
        }
        if (numCommon == 0) return (float) 0.0;

        int numHalfTransposed = 0;
        int j = 0;
        for (int i = 0; i < len1; ++i) {
            if (!matched1[i]) continue;
            while (!matched2[j]) ++j;
            if (s1.charAt(i) != s2.charAt(j))
                ++numHalfTransposed;
            ++j;
        }

        int numTransposed = numHalfTransposed/2;

        float numCommonD = numCommon;
        double weight = (numCommonD/len1
                         + numCommonD/len2
                         + (numCommon - numTransposed)/numCommonD)/3.0;

        if (weight <= mWeightThreshold) return (float) weight;
        int max = Math.min(mNumChars,Math.min(s1.length(),s2.length()));
        int pos = 0;
        while (pos < max && s1.charAt(pos) == s2.charAt(pos))
            ++pos;
        if (pos == 0) return (float) (1 - weight);
           return (float) (weight + 0.1 * pos * (1.0 - weight));
    }
}
