/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import pp.domain.ComparatorCromossomo;
import pp.domain.Cromossomo;


public class Printer {

	public static int debugTela;
        public static int debugArquivo;

        private static PrintWriter fout;

        public static void imprime(StringBuffer sb) {
            if (debugArquivo ==  1) {
                try {
                    if (fout == null) {
                        FileOutputStream fos = new FileOutputStream("genetics-" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".log", true);
                        fout = new PrintWriter(fos,true);
                    }
                    fout.write(sb.toString());
                } catch (FileNotFoundException ex) {  }

            }

            System.out.print(sb);
        }

	public static void imprimeInformacoesInicioGeracao(int n) {
		imprime(imprimeNumeroGeracao(n));
	}
	
	public static void imprimeInformacoesFimGeracao(int tamPopulacao, int tentativasCruzamento, int contMutacao, int contReproducao, double fit, Cromossomo vencedor) {
		imprime(Printer.imprimeDadosPopulacao(
				tamPopulacao, 
				tentativasCruzamento, 
				contMutacao, 
				contReproducao, 
				fit));
		if (debugTela > 0) imprime(Printer.imprimeDadosCromossomo("Best Solution:", vencedor));

		imprime(Printer.imprimeTodosResultadosCromossomo(vencedor));
	}

	public static void imprimeInicio(int pop, int gen) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n[INFO] ----------------------------------------\n");
	    sb.append("[INFO] Starting: creating "+pop+" individuals and running "+gen+" generations...\n");
            sb.append("[INFO] ----------------------------------------\n");
            imprime(sb);
	}

	public static void imprimeFim(Cromossomo vencedor) {
            StringBuffer sb = new StringBuffer();
            sb.append("\n[INFO] ----------------------------------------\n");
            sb.append("[INFO] Finished! Check the solution below:\n");
            sb.append(imprimeDadosCromossomo("Best solution:",vencedor));
            sb.append("[INFO] ----------------------------------------\n");
            imprime(sb);
	}


	
	

	private static StringBuffer imprimeDadosCromossomo(String str, Cromossomo vencedor) {
		StringBuffer sb = new StringBuffer();
                sb.append("[INFO][Individual] "+str+" #"+vencedor.getId());
		sb.append(" (");
		for (int j = 0; j < vencedor.getNumeroDeGenes(); j++) {
			sb.append(vencedor.getGene(j).getValor());
			if (j + 1 < vencedor.getNumeroDeGenes())
				sb.append(",");
		}
		sb.append(")");
		sb.append(" Difference: " + (ComparatorCromossomo.tipoComparacao==0?vencedor.getDiferenca():vencedor.getDiferencaEquacoes()));
		sb.append(", Sum: " + vencedor.getSoma());
                sb.append("\n");
                return sb;
	}

	/**
	 * 
	 * @param tamanho
	 * @param tentativasCruzamento
	 * @param qtdMutacoes
	 * @param qtdReproducoes
	 */
	private static StringBuffer imprimeDadosPopulacao(int tamanho, int tentativasCruzamento, int qtdMutacoes, int qtdReproducoes, double fit) {
		StringBuffer sb = new StringBuffer();
                if (debugTela > 0) {
			sb.append("[INFO][Population size="+tamanho+" fitness= " + fit +"] " + qtdReproducoes + " births in " + tentativasCruzamento + " reproduction attempts. " + qtdMutacoes + " mutations. ");
			sb.append("Rates = {reprodution:"+(qtdReproducoes*100f/(float)(tentativasCruzamento*2))+"%; mutation:" + (qtdMutacoes*100f)/qtdReproducoes + "%}");
                        sb.append("\n");
		}
                return sb;
	}
	
	private static StringBuffer imprimeTodosResultadosCromossomo(Cromossomo c) {
		StringBuffer sb = new StringBuffer();
		if (debugTela > 0) {
			sb.append("[INFO][EquationSolution] ");
			for(int i = 0;i < c.getResultadoCadaEquacao().size();i++) {
				sb.append("K" + i + " = " + c.getResultadoCadaEquacao().get(i) + "  ");
			}
			sb.append("\n");
		}
                return sb;
	}
		
	private static StringBuffer imprimeNumeroGeracao(int g) {
            StringBuffer sb = new StringBuffer();
            if (debugTela > 0)
		sb.append("\n[INFO]--> Generation: " + g+"\n");
            return sb;
	}

	public static void imprimePopulacao(List<Cromossomo> populacao) {
		if (debugTela == 2)
			for (Cromossomo cromossomo : populacao)
				imprime(imprimeDadosCromossomo("",cromossomo));
		
	}

    public static void imprimeDiversificacaoIntensificacao(Cromossomo venc, ArrayList<Cromossomo> al) {
        StringBuffer sb = new StringBuffer();
        if(debugTela == 2) {
            sb.append("-----------------------------------\n");
            sb.append("[DEBUG] Local search.\n");
            sb.append(imprimeDadosCromossomo("Initial solution:", venc));
            for (int i = 0; i < al.size(); i++)
                sb.append(imprimeDadosCromossomo("New solution:", al.get(i)));
            sb.append("-----------------------------------\n");
        }
        imprime(sb);
    }

    public static void imprimeDiversificacaoIntensificacaoInicio() {
        if (debugTela > 0) 
            imprime(new StringBuffer("[INFO] Analysing local search...\n"));
    }

	
	
	
	/*
	public static  void imprimeTodosCromossomos() {
		for (int i = 0; i < popInicial.size(); i++) {
			Cromossomo aux;
			aux = popInicial.get(i);
			System.out.print("Foi mutado? " + aux.getFoiMutado() + " Cromossomo: " + i + " ");
			System.out.print("    (");
			for (int j = 0; j < aux.getNumeroDeGenes(); j++) {
				System.out.print(" ");
				System.out.print(aux.getGene(j).getValor());
				if (j + 1 < aux.getNumeroDeGenes())
					System.out.print(",");
			}
			System.out.print(")  ");
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	public static  void imprimeCromossomosSelecionados() {
		for (int i = 0; i < popSelecionada.size(); i++) {
			Cromossomo aux;
			aux = popSelecionada.get(i);
			System.out.print("Foi mutado? " + aux.getFoiMutado() + " Cromossomo: " + i + " ");
			System.out.print("    (");
			for (int j = 0; j < aux.getNumeroDeGenes(); j++) {
				System.out.print(" ");
				System.out.print(aux.getGene(j).getValor());
				if (j + 1 < aux.getNumeroDeGenes())
					System.out.print(",");
			}
			System.out.print(")  ");
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	public static  void imprimeCromossomosFinal() {
		for (int i = 0; i < popCruzada.size(); i++) {
			Cromossomo aux;
			aux = popCruzada.get(i);
			System.out.print("Foi mutado? " + aux.getFoiMutado() + " Cromossomo: " + i + " ");
			System.out.print("    (");
			for (int j = 0; j < aux.getNumeroDeGenes(); j++) {
				System.out.print(" ");
				System.out.print(aux.getGene(j).getValor());
				if (j + 1 < aux.getNumeroDeGenes())
					System.out.print(",");
			}
			System.out.print(")  ");
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}
	public static  void imprimeDiferencaCromossomosInicial() {
		for (int i = 0; i < popInicial.size(); i++) {
			Cromossomo aux;
			aux = popInicial.get(i);
			System.out.print("Cromossomo: " + i + " ");
			System.out.println(aux.getDiferenca());
		}
		System.out.println();
		System.out.println();
	}
	public static  void imprimeDiferencaCromossomosSelecionados() {
		for (int i = 0; i < popSelecionada.size(); i++) {
			Cromossomo aux;
			aux = popSelecionada.get(i);
			System.out.print("Cromossomo: " + i + " ");
			System.out.println(aux.getDiferenca());
		}
		System.out.println();
		System.out.println();
	}
	public static  void imprimeDiferencaCromossomosFinal() {
		for (int i = 0; i < popCruzada.size(); i++) {
			Cromossomo aux;
			aux = popCruzada.get(i);
			System.out.print("Cromossomo: " + i + " ");
			System.out.println(aux.getDiferenca());
		}
		System.out.println();
		System.out.println();
	}
*/
	
	
	
	
    
}