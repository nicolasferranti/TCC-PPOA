/**
 *A distancia Levenshtein-Damerau � determinada pela contagem m�nima do
 *n�mero de opera��es necess�rias para transformar uma string em outra,
 *as opera��es s�o, a inser��o, a exclus�o, a substitui��o ou a transposi��o
 *de dois caracteres adjacentes.
 * Quando mais proximo de 1 elas s�o iguais.
 */
package similarity.editdistance;

/**
 *
 * @author Isabella Pires Capobiango
 */
public class DamerauLevenshteinEditDistance implements IEditDistance {

     public  float compute(String s1, String s2) {

        if (s2 == null) s2 = "";
        if (s1 == null) s1 = "";
         
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        
        //inicializa a matriz
        float[][] dp = new float[s1.length() + 1][s2.length() + 1];
        float max = Math.max(s1.length(), s2.length());

        //preenche cada linha da esquerda para direita
	for (int i = 0; i < dp.length; i++ ) {
		for (int j = 0; j < dp[i].length; j++) {
                    //preenche a matriz linha 0 valores de 0 a i-1 e coluna 0 valores de 0 a j-1
                    dp[i][j] = i == 0 ? j : j == 0 ? i : 0;
                    //para outros valores realiza de baixo para cima
			if (i > 0 && j > 0) {
				if (s1.charAt(i - 1) == s2.charAt(j - 1))
 					dp[i][j] = dp[i - 1][j - 1];
 					else{
                                            //pega o resultado minino das opera��es inserir horizontal, substituir diagonal ou deletar vertical
					    dp[i][j] = Math.min(dp[i][j - 1] + 1, Math.min(dp[i - 1][j - 1] + 1, dp[i - 1][j] + 1));
                                            if ((i > 1) && (j > 1) && (s1.charAt(i - 1) == s2.charAt(j - 2)) && (s1.charAt(i - 2) == s2.charAt(j - 1)))
                                                dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1); //Transposi��o
                                        }
                        }


                }

        }
     return ((float) 1 - (dp[s1.length()][s2.length()]/max));
    }
}




