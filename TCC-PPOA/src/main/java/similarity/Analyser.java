/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity;

//import br.ufjf.ontology.gnosis.AGConnectorUtil;
import parser.XMLReader;
import similarity.common.FunctionWeightVO;
import similarity.common.SimilarityVO;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import ppoa.tcc.ppoa.PPConnectorUtil;

/**
 *
 * @author Jairo F. de Souza
 */
public class Analyser {

    private XMLReader xml;
    private Collection<FunctionWeightVO> functionWeightVOs;
    private Collection<List<OntResource>> conceitos;
    MainContainer mainContainer;

    public Analyser(String xmlFile) throws Exception {
        this.xml = new XMLReader(xmlFile);
        this.functionWeightVOs = xml.getFunctions();
        this.conceitos = xml.getOntology();
    }

    public TabelaSimilaridade process() throws Exception {

        TabelaSimilaridade.inializa();
        mainContainer = new MainContainer();
        mainContainer.setContainer(functionWeightVOs);
        //Recupera as duas listas de conceitos para verificar a similaridade
        Iterator i = conceitos.iterator();
        List<OntResource> conceitos1 = (List<OntResource>) i.next();
        List<OntResource> conceitos2 = (List<OntResource>) i.next();

        if (xml.callAG()) {
            File file = xml.getAlignmentFile();
            if (!file.exists()) throw new IOException("Arquivo de alinhamento não encontrado");

            new PPConnectorUtil(file.getAbsolutePath(), conceitos1, conceitos2).setPesosToContainer(mainContainer);
        }
        /////////////////////////// Ta pra cima /\ o acesso ao melhor individuo
        processaSimilaridades(conceitos1, conceitos2, mainContainer);

        //mainContainer.setSimilarityTable(listaSimilaridades);

        // Eu sei, o c�digo abaixo n�o est� bem modelado, mas estou no final do doutorado e � um bacalhau para ganhar tempo
        return new TabelaSimilaridade(TabelaSimilaridade.getListaSimilaridades());
    }

    public Collection<FunctionWeightVO> getFuncoesUsadas() {
        return this.mainContainer.getContainer();
    }


    // N�o estamos interessados em alinhamentos entre propriedades e conceitos.
    // Assim, filtramos a entrada na tabela para evitar que propriedades sejam analisadas com conceitos.
    // Essa decis�o tamb�m evita com que o tamanho da tabela cres�a consideravelmente.
    private void processaSimilaridades(List<OntResource> conceitos1, List<OntResource> conceitos2, FunctionContainer mainContainer) {

        //Pega todos as entidades das duas ontologias e cria a tabela de similaridade
        for (Iterator i1 = conceitos1.iterator(); i1.hasNext();) {
            OntResource c1 = (OntResource) i1.next();
            if (c1.getLocalName() != null) {
                System.out.println("Processing the alignment for " + c1.getURI() + " ...");
                for (Iterator i2 = conceitos2.iterator(); i2.hasNext();) {
                    OntResource c2 = (OntResource) i2.next();
                    if (possuiMesmoTipo(c1,c2) && c2.getLocalName() != null) {
                        SimilarityVO similarityVO = new SimilarityVO();
                        similarityVO.setElementA(c1);
                        similarityVO.setElementB(c2);
                        similarityVO.setSimilarity(mainContainer.execute(c1, c2));
                        TabelaSimilaridade.adiciona(similarityVO);
                    }
                }
            }
        }
    }

    // retorna true os recursos sejam ambos conceitos ou ambos propriedades
    private boolean possuiMesmoTipo(OntResource or1, OntResource or2) {
        return (or1 instanceof OntClass && or2 instanceof OntClass) || (or1 instanceof OntProperty && or2 instanceof OntProperty);
    }

}
