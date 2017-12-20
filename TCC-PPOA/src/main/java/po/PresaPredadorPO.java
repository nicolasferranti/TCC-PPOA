/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package po;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import pp.domain.ArrayPesosGranulares;
import pp.domain.Cromossomo;
import pp.domain.Equacao;
import pp.domain.Gene;
import pp.domain.IFuncao;

/**
 *
 * @author nicolasferranti
 */
public class PresaPredadorPO {

    private GeradorPO gerador;
    private ArrayPesosGranulares pesos;
    private List<Cromossomo> pop;

    public PresaPredadorPO(GeradorPO g, int tamanhoPopulacao, double Granularidade) {
        this.gerador = g;
        this.pesos = new ArrayPesosGranulares(Granularidade);
        this.pop = this.gerador.criaPopulacao(1, pesos);
    }

    public ArrayList<IFuncao> getEquacoes() {
        Cromossomo c = this.pop.get(0);
        // 205 => double[] genes = {0.014651183754284, 0.08392184245449782, 0.8238437318662803, 0.2112636373678989, 0.49804128812448123, 0.11247062187710197, 0.1975091417000846, 0.07779951752686862, 0.0313184026041507};
        // 209 => double[] genes = {0.00998987138078153, 0.9190681670323625, 0.014984807071187982, 1.7925499026834724E-14, 0.9340529741034429, 0.06992909966544383, 0.574417604395245, 0.20479236330617598, 0.009989871380799457};
        // 230 => double[] genes = {0.07992257500546346, 0.0, 0.9141144516249884, 0.5494677031625613, 0.38462739221379294, 0.004995160937841466, 0.24975804689207332, 0.21479192032718306, 0.004995160937841466};
        double[] genes = {0.005, 0.895, 0.1, 0.19, 0.325, 0.0, 0.055, 0.795, 0.0};

        ArrayList<Gene> novosGenes = new ArrayList<>();
        for (double peso : genes) {
            novosGenes.add((new Gene(peso)));
        }
        c.genes = novosGenes;
        avaliaCromossomoDiferenca(c, gerador);
        return Equacao.getEquacoesComPesos(gerador.getEquacoes(), c);
    }

    public double[] MultiplicaPesos(String pesos, String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(pesos));
        ArrayList<Double> al = new ArrayList();

        while (br.ready()) {
            String line = br.readLine();
            if (line.startsWith("w")) {
                al.add(Double.parseDouble(line.split(" ")[1]));
            }
        }

        br = new BufferedReader(new FileReader(file));

        int k = 0;
        String[] line = br.readLine().split("\t");
        double[] genes = new double[line.length];

        for (int i = 0; i < line.length; i++) {
            genes[i] = Double.parseDouble(line[i]) * al.get(k);
        }
        k++;

        while (br.ready()) {
            line = br.readLine().split("\t");
            for (int i = 0; i < line.length; i++) {
                genes[i] = genes[i] + Double.parseDouble(line[i]) * al.get(k);
            }
            k++;
        }
        return genes;

    }

    public void SimulaVida() throws FileNotFoundException, IOException {
        Cromossomo c = this.pop.get(0);
        //generateCallGurobi();
        double[] genes = MultiplicaPesos("/home/nicolasferranti/Documentos/Trabalhos/PO/Chamadas ao Gurobi/Teste1_205.sol", "/home/nicolasferranti/Documentos/Trabalhos/PO/Individuos_por_instancia/205");
        System.out.println("work:");
        for (double k : genes) {
            System.out.println(k + " ");
        }
        /// GENES INSTANCIA 205 double[] genes = {0.05, 0.95, 0.015, 0.475, 0.1, 0.025, 0.645, 0.14, 0.005};
        /// GENES INSTANCIA 209 double[] genes = {0.085, 0.875, 0.005, 0.105, 0.815, 0.035, 0.585, 0.29, 0.025};
        /// GENES INSTANCIA 230 double[] genes = {0.105, 0.02, 0.785, 0.07, 0.845, 0.09, 0.99, 0.015, 0.015};
    }

    public void generateCallGurobi() throws FileNotFoundException, IOException {
        Cromossomo c = this.pop.get(0);

        String file = "/home/nicolasferranti/Documentos/Trabalhos/PO/Individuos_por_instancia/301_paraLeitura";
        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<BigDecimal[]> somatorioLinhas = new ArrayList<>();

        while (br.ready()) {
            String[] line = br.readLine().split("\t");
            /// GENES INSTANCIA 205 double[] genes = {0.05, 0.95, 0.015, 0.475, 0.1, 0.025, 0.645, 0.14, 0.005};
            /// GENES INSTANCIA 209 double[] genes = {0.085, 0.875, 0.005, 0.105, 0.815, 0.035, 0.585, 0.29, 0.025};
            /// GENES INSTANCIA 230 double[] genes = {0.105, 0.02, 0.785, 0.07, 0.845, 0.09, 0.99, 0.015, 0.015};
            double[] genes = new double[line.length];
            for (int i = 0; i < line.length; i++) {
                genes[i] = Double.parseDouble(line[i]);
            }

            //double[] genes = {0.83, 0.05, 0.12, 0.23, 0.295, 0.0, 0.235, 0.115, 0.0};
            ArrayList<Gene> novosGenes = new ArrayList<>();
            for (double peso : genes) {
                novosGenes.add((new Gene(peso)));
            }

            c.genes = novosGenes;
            System.out.println("--------------------------");
            System.out.print("Gene do cromossomo : (");
            for (Gene g : c.genes) {
                System.out.print(g.getValor() + " ");
            }
            System.out.print(")");
            System.out.println("");
            BigDecimal[] resultados = avaliaCromossomoDiferenca(c, gerador);
            somatorioLinhas.add(resultados);

            /*
            for (int i = 0; i < resultados.length; i++) {
                System.out.println("Soma da linha " + (i + 1) + " : " + resultados[i]);
            }
            System.out.println("--------------------------");
             */
        }
        int indiceW = 1;
        StringBuilder minimize = new StringBuilder();
        StringBuilder c0 = new StringBuilder("c0 : ");
        StringBuilder c1 = new StringBuilder("c1 : ");
        StringBuilder c2 = new StringBuilder("c2 : ");

        minimize.append("Minimize\n");
        minimize.append("1 e1 + 1 e2 + 1 e3 + 1 f1 + 1 f2 + 1 f3");
        for (BigDecimal[] r : somatorioLinhas) {
            c0.append(r[0] + " w" + indiceW + " + ");
            c1.append(r[1] + " w" + indiceW + " + ");
            c2.append(r[2] + " w" + indiceW + " + ");
            indiceW++;
        }
        c0.append("1 e1 + 1 f1 = 1\n");
        c1.append("1 e2 + 1 f2 = 1\n");
        c2.append("1 e3 + 1 f3 = 1\n");

        minimize.append("\n");
        minimize.append("Subject To\n");
        minimize.append(c0 + "\n");
        minimize.append(c1 + "\n");
        minimize.append(c2 + "\n");
        minimize.append("Integers\nBinaries\nEnd");
        System.out.println(minimize.toString());
    }

    private static BigDecimal[] avaliaCromossomoDiferenca(Cromossomo c, GeradorPO gerador) {

        c.limpaCadaResultado();
        c.setSoma(OperadorPO.avaliaCromossomo(c, gerador.getEquacoes(), gerador.getResultadoEquacoes()));
        c.setDiferenca(gerador.getSomatorioEquacoes().subtract(c.getSoma()).abs());
        /*
        System.out.println("-------");
        System.out.println("Somatorio:" + gerador.getSomatorioEquacoes());
        System.out.println("Genes:");
        for (int i = 0; i < gerador.getNumeroDeGenes(); i++) {
            System.out.print(c.genes.get(i).getValor() + " ");
        }
        
        System.out.println("");
        System.out.println("Soma:" + c.getSoma());
        System.out.println("Diferença:" + c.getDiferenca());
        System.out.println("Diferença Eq:" + c.getDiferencaEquacoes());
        System.out.println("-------");
         */
        return OperadorPO.avaliaCromossomoVet(c, gerador.getEquacoes(), gerador.getResultadoEquacoes());
    }

}
