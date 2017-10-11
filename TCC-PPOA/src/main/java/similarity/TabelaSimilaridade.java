/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity;

import similarity.combination.FirstMatchCombination;
import similarity.combination.ICombination;
import similarity.common.SimilarityNameComparator;
import similarity.common.SimilarityVO;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 *
 * @author Isabella
 */
public class TabelaSimilaridade {

    private static List<SimilarityVO> listaConceitos;

    public TabelaSimilaridade(List<SimilarityVO> lista) {
        listaConceitos = lista;
    }

    public static void inializa() {
        listaConceitos = new ArrayList<SimilarityVO>();
    }

    public static void adiciona(SimilarityVO vo) {
        listaConceitos.add(vo);
    }

    public static List<SimilarityVO> getListaSimilaridades() {
        return listaConceitos;
    }

    private void espaco(int espaco, PrintWriter printer) {
        for (int i = 0; i < espaco; i++) {
            printer.print(" ");
        }
    }

    private void tamanhoNome(int tam, String nome, PrintWriter printer) {
        if (nome.length() > tam) {
            printer.print(nome.substring(0, tam) + ".    ");
        } else {
            printer.print(nome);
            espaco(tam+5 - nome.length(), printer);
        }
    }

    public void imprimeLinha(PrintWriter printer) {
        
        Collections.sort(this.listaConceitos, new SimilarityNameComparator());
        printer.print("############################\n#Similaridade dos recursos\n\n");
        for(SimilarityVO similarityVO : listaConceitos) {
            printer.print("(");
            tamanhoNome(15, similarityVO.getElementA().getLocalName(), printer);
            printer.print(",");
            tamanhoNome(15, similarityVO.getElementB().getLocalName(), printer);
            printer.print(","+similarityVO.getSimilarity()+")\n");
        }
    }
    
    
    public void imprimeTabela(PrintWriter printer) {
        espaco(15, printer);
        String nome = "";
        NumberFormat f = new DecimalFormat("0.00");
        for (Iterator<SimilarityVO> j = listaConceitos.iterator(); j.hasNext();) {
            SimilarityVO s = (SimilarityVO) j.next();
            if (nome.isEmpty()) {
                nome = s.getElementB().getLocalName();
                tamanhoNome(10, nome, printer);
            } else {
                if (nome.equals(s.getElementB().getLocalName())) {
                    break;
                }
                tamanhoNome(10, s.getElementB().getLocalName(), printer);
            }
        }
        nome = "";
        for (Iterator<SimilarityVO> j = listaConceitos.iterator(); j.hasNext();) {
            SimilarityVO s1 = (SimilarityVO) j.next();
            if (!nome.equals(s1.getElementA().getLocalName())) {
                tamanhoNome(10, "\n" + s1.getElementA().getLocalName(), printer);
                nome = s1.getElementA().getLocalName();
            }
            espaco(2, printer);
            printer.print(f.format(s1.getSimilarity()));
            espaco(9, printer);
        }
    }

    private boolean contains(Collection<SimilarityVO> collec, SimilarityVO sVO) {
        for (SimilarityVO similarityVO : collec) {
            if (similarityVO.getElementA()== sVO.getElementA())// || similarityVO.getElementB() == sVO.getElementB())
                return true;
        }

        return false;
    }

    private Collection<SimilarityVO> getSimilaridadeOneToOneTrue() {        
        
        ICombination fm = new FirstMatchCombination();
        List<SimilarityVO> a = (List) fm.explore(listaConceitos);
        Collections.sort(a, new SimilarityNameComparator());
        return a;
    }


    /**
     * Esse m�todo retorna os maiores alinhamentos encontrados para cada recurso
     * O par�metro booleano determina se deseja alinhamentos ser�o sempre 1:1 ou se poder�o ser n:1.
     * Ou seja, caso o par�metro seja verdadeiro, os alinhamentos ser�o somente 1:1. 
     * Caso seja falso, poder�o ser n:1, o que indica que mais de um conceito da ontologia 1 poder� ser alinhado com um elemento da ontologia 2.
     * @param allAlign 
     * @return 
     */
    public Collection<SimilarityVO> getSimilaridadeOneToOne(boolean allAlign) {        
        return allAlign ? getSimilaridadeOneToOneTrue() : getSimilarityOneToOneFalse();
    }

    private Collection<SimilarityVO> getSimilarityOneToOneFalse() {
                
        Collections.sort(listaConceitos, new SimilarityNameComparator());

        Collection<SimilarityVO> al = new ArrayList<SimilarityVO>();
        for (SimilarityVO similarityVO : listaConceitos) 
            if (!this.contains(al, similarityVO) && similarityVO.getSimilarity()>0 )
                al.add(similarityVO);

        return al;
    }
}
