/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package po;

import com.hp.hpl.jena.ontology.OntResource;
import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.DistanceAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLManager;
import org.xml.sax.SAXException;
import ppoa.tcc.ppoa.NewPRecEvaluator;
import similarity.FunctionContainer;
import similarity.TabelaSimilaridade;
import similarity.common.FunctionWeightVO;
import similarity.common.SimilarityVO;

/**
 *
 * @author nicolasferranti
 */
public class MatcherPO {

    private String dirBaseBenchmark;
    Collection<FunctionWeightVO> pesosUsados;

    public MatcherPO(String CWD) {
        this.dirBaseBenchmark = CWD;
    }

    // Caso seja verdadeiro, persiste informaçõs no arquivo gnosis.log dentro da pasta de testes
    public void runTest(int testNumber, String xmlFuncoes, boolean printInformation) throws Exception {

        TabelaSimilaridade ts = callAnalyser(xmlFuncoes);

        //Preenche a classe de alinhamento
        URI uri1 = new URI("file://" + this.dirBaseBenchmark + "/101/onto.rdf");
        URI uri2 = new URI("file://" + this.dirBaseBenchmark + "/" + testNumber + "/onto.rdf");
        OWLOntology O1 = loadOntology(uri1);
        OWLOntology O2 = loadOntology(uri2);
        Alignment al = readAlignments(O1, O2, ts);

        saveAlignments(testNumber, al);

        showInformation(new PrintWriter(System.out), testNumber, O1, O2, al, ts);
        if (printInformation) {
            showInformation(new PrintWriter(new File(this.dirBaseBenchmark + "/" + testNumber + "/gnosis.log")), testNumber, O1, O2, al, ts);
        }
    }

    private void showInformation(PrintWriter printer, int testNumber, OWLOntology O1, OWLOntology O2, Alignment al, TabelaSimilaridade ts) throws Exception {
        StringBuffer str = showPrecisionRecall(testNumber, O1, O2, al);
        showPesos(str, this.pesosUsados, 0);
        printer.print(str);
        printer.print("\n\n");
        ts.imprimeLinha(printer);
        printer.flush();
        printer.close();
    }

    private void showPesos(StringBuffer str, Collection<FunctionWeightVO> pesos, int i) {
        if (this.pesosUsados == null) {
            return;
        }

        for (FunctionWeightVO function : pesos) {
            str = str.append("\n\nFuncao").append(++i).append("=").append(function.getFunction().toString())
                    .append("\nPeso").append(i).append("=").append(function.getWeight());

            if (function.getFunction() instanceof FunctionContainer) {
                showPesos(str, ((FunctionContainer) function.getFunction()).getContainer(), i);
            }

        }
    }

    public StringBuffer showPrecisionRecall(int testNumber, OWLOntology O1, OWLOntology O2, Alignment al) throws ParserConfigurationException, IOException, AlignmentException, SAXException {
        //Agora somente imprime os valores de precisao e recall encontrados
        ArrayList<String[]> expectedAlign = new ArrayList<String[]>();

        StringBuffer str = new StringBuffer();
        AlignmentParser parser = new AlignmentParser(0);
        Alignment refalign = parser.parse("file://" + this.dirBaseBenchmark + "/" + testNumber + "/refalign.rdf");
        NewPRecEvaluator eval = new NewPRecEvaluator(refalign, al, expectedAlign);
        eval.eval(new BasicParameters());

        str = str.
                append("############################################").
                append("\nTeste=").append(testNumber).
                append("\nPrecisao=").append(eval.getPrecision()).
                append("\nRecall=").append(eval.getRecall()).
                append("\nEncontrados=").append(eval.getFound()).
                append("\nEsperados=").append(eval.getExpected()).
                append("\n############################################");

        str = str.append("\nAlinhamentos esperados:\n");
        for (String[] strings : expectedAlign) {
            str = str.append(strings[0]).append("\n=\n").append(strings[1]).append("\n\n");
        }

        return str;
    }

    private void saveAlignments(int testNumber, Alignment al) throws UnsupportedEncodingException, FileNotFoundException, AlignmentException {
        //Grava o resultado em um arquivo rdf
        PrintWriter wrt = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.dirBaseBenchmark + "/" + testNumber + "/gnosis.rdf")), "UTF-8")), true);
        AlignmentVisitor V = new RDFRendererVisitor(wrt);
        al.render(V);
        wrt.flush();
    }

    private Alignment readAlignments(OWLOntology O1, OWLOntology O2, TabelaSimilaridade ts) throws OWLException, AlignmentException {
        Alignment al = new DistanceAlignment();
        al.init(O1, O2);
        // al.setType("11");
        //Entrada para o método addAlignCell deve ser OWLEntities...
        //Vou ter que passar de uma biblioteca para outra, pois o benchmark usa owlapi e eu estou usando o JENA
        //Pegar os maiores valores...
        List<SimilarityVO> coll = (List) ts.getSimilaridadeOneToOne(true);
        for (SimilarityVO similarityVO : coll) //if(similarityVO.getSimilarity() > 0.15) 
        {
            al.addAlignCell(
                    getEntity((OWLOntology) al.getOntology1(), similarityVO.getElementA()),
                    getEntity((OWLOntology) al.getOntology2(), similarityVO.getElementB()),
                    "=",
                    similarityVO.getSimilarity()
            );
        }

        return al;
    }

    private OWLEntity getEntity(OWLOntology ontology, OntResource resource) throws OWLException {

        if (resource.isClass()) {
            return ontology.getClass(URI.create(resource.getURI()));
        }

        if (resource.isObjectProperty()) {
            return ontology.getObjectProperty(URI.create(resource.getURI()));
        }

        if (resource.isDatatypeProperty()) {
            return ontology.getDataProperty(URI.create(resource.getURI()));
        }

        if (resource.isIndividual()) {
            return ontology.getIndividual(URI.create(resource.getURI()));
        }

        return null;

    }

    private TabelaSimilaridade callAnalyser(String xmlFuncoes) throws Exception {
        AnalyserPO analyser = new AnalyserPO(xmlFuncoes);
        TabelaSimilaridade ts = analyser.process();
        this.pesosUsados = analyser.getFuncoesUsadas();
        return ts;
    }

    private OWLOntology loadOntology(URI uri) throws Exception {
        OWLRDFParser parser = new OWLRDFParser();
        parser.setConnection(OWLManager.getOWLConnection());
        return parser.parseOntology(uri);

    }
}
