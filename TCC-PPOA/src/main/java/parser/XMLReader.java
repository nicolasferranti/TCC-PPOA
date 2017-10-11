/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import similarity.FunctionContainer;
import similarity.ISimilarityFunction;
import similarity.combination.ICombination;
import similarity.common.FunctionWeightVO;
import similarity.common.WeightVO;
import similarity.editdistance.IEditDistance;
import similarity.exception.WeightException;
import similarity.structure.OntologyCache;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author Isabella Pires Capobiango
 */
public class XMLReader  {
    
    private Collection<FunctionWeightVO> listFunctions = new ArrayList<FunctionWeightVO>();
    private Collection<List<OntResource>> listOntologies = new ArrayList<List<OntResource>>();
    private boolean callAG = false;
    private String alignURL;

    public XMLReader(String xmlPath) throws WeightException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, JDOMException, IOException, InvocationTargetException{
        FileInputStream f = new FileInputStream(xmlPath);

        //Criamos uma classe SAXBuilder que vai processar o XML4
        SAXBuilder sb = new SAXBuilder();
        //Este documento agora possui toda a estrutura do arquivo.
        Document d = sb.build(f);
        //Recuperamos o elemento root
        Element root = d.getRootElement();
        listOntologies = createOntologies(root);
        //Recuperamos o elemento filho do root que se trata de um Container
        Element containerPrincipal = root.getChild(XMLElements.container_XML);
        listFunctions = createFunctions(containerPrincipal);
    }

    private Collection<FunctionWeightVO> createFunctions(Element containerPrincipal) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, WeightException, IllegalArgumentException, InvocationTargetException, IllegalAccessException, DataConversionException {
        Collection<FunctionWeightVO> functions = new ArrayList<FunctionWeightVO>();
        List filhosContainer = containerPrincipal.getChildren();
        Iterator i = filhosContainer.iterator();
        int verificador = 0;

        //Iteramos com os elementos filhos
        while (i.hasNext()) {
            Element element = (Element) i.next();
            FunctionWeightVO functionWeightVO = new FunctionWeightVO();

            //Verifica se o ele Ã© uma function ou um container
            if (element.getName().equals(XMLElements.function_XML)){
                //Seta a classe que se encontra no XML
                Class classe = Class.forName(element.getChild(XMLElements.class_XML).getValue());

                Element combination = element.getChild(XMLElements.strategyCombination_XML);
                Element editDistance = element.getChild(XMLElements.strategyEditDistance_XML);


                if (editDistance != null) {
                    verificador = 1;
                    if (combination != null) verificador = 2;
                }else {
                    if (combination != null) verificador = 1;
                    else verificador = 0;}

                Class partypes[] = new Class[verificador];
                Object arglist[] = new Object[verificador];

                //Seta os parametros para achar e chamar o construtor utilizando reflect
                switch (verificador){
                    case 1:
                        if (combination == null){
                            partypes[0] = IEditDistance.class;
                            Class strategyEditDistance = Class.forName(editDistance.getValue());
                            arglist[0] = strategyEditDistance.newInstance();
                        }else {
                            partypes[0] = ICombination.class;
                            Class strategyCombination = Class.forName(combination.getValue());
                            arglist[0] = strategyCombination.newInstance();
                        }
                        break;
                    case 2:
                        partypes[0] = IEditDistance.class;
                        partypes[1] = ICombination.class;
                        Class strategyEditDistance = Class.forName(editDistance.getValue());
                        Class strategyCombination = Class.forName(combination.getValue());
                        arglist[0] = strategyEditDistance.newInstance();
                        arglist[1] = strategyCombination.newInstance();
                        break;
                }


                //Recupera o construtor
                Constructor ct = classe.getDeclaredConstructor(partypes);
                ct.setAccessible(true);

                //Cria o objeto
                Object function = ct.newInstance(arglist);

                //Recupera o valor da penalidade que serÃ¡ passada para o metodo
                Element elementPenalidade = element.getChild(XMLElements.penalty_XML);
                if (elementPenalidade != null) {
                    WeightVO penalty = new WeightVO(Float.parseFloat((elementPenalidade.getValue())));
                    Object argumento[] = new Object[1];
                    argumento[0] = penalty;

                    //Recupera as classes que serÃ£o passadas para o metodo
                    Class tipo[] = new Class[1];
                    tipo[0] = WeightVO.class;

                    //Recupera o metodo
                    Method method = function.getClass().getMethod("setPenalty", tipo);

                    //invoca o metodo
                    method.invoke(function, argumento);
                }
                
                //Seta o functionWeightVo com a FunÃ§Ã£o e o Peso
                functionWeightVO.setFunction((ISimilarityFunction) function);
                WeightVO peso = new WeightVO(Float.parseFloat((element.getAttributeValue(XMLElements.weight_XML))));
                functionWeightVO.setWeight(peso);

                //Adiciona na lista o functionWeightVo
                functions.add(functionWeightVO);
            }

            //É um container
            else {
                Collection<FunctionWeightVO> lista = new ArrayList<FunctionWeightVO>();
                lista = createFunctions(element);
                WeightVO peso = new WeightVO(Float.parseFloat((element.getAttributeValue(XMLElements.weight_XML))));
                FunctionContainer fc = new FunctionContainer();
                fc.setNome(element.getAttributeValue(XMLElements.container_name_XML));
                Iterator itLista = lista.iterator();
                while (itLista.hasNext()){
                    FunctionWeightVO fw = (FunctionWeightVO)itLista.next();
                    fc.addFunction(fw.getFunction(), fw.getWeight());
                }
                functionWeightVO.setFunction((ISimilarityFunction)fc);
                functionWeightVO.setWeight(peso);
                functions.add(functionWeightVO);
            }
        }
        return functions;
    }

    private Collection<List<OntResource>> createOntologies(Element root) {
        //Strings para guardar o nome e/ou caminho das duas ontologias
        String[] owlNome = new String[2];

        //Listas para guardar as classes que serÃ£o verificadas
        Collection<List<OntResource>> classesOntologias = new ArrayList<List<OntResource>>();
        List<OntResource> elementosOnt1 = new ArrayList<OntResource>();
        List<OntResource> elementosOnt2 = new ArrayList<OntResource>();

        //Recuperamos os elementos filhos (children) da Ontologia
        Element ontologia = root.getChild(XMLElements.ontologies_XML);
        List ontologias = ontologia.getChildren(XMLElements.ontology_XML);
        Iterator i = ontologias.iterator();
        Element element1 = (Element) i.next();
        Element element2 = (Element) i.next();
        owlNome[0] = element1.getAttributeValue("id");
        owlNome[1] = element2.getAttributeValue("id");

        //Cria ontologia sem associar a uma linguagem especifica.
        OntModel m1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        OntModel m2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);

        //Definir se as ontologias associadas devem ou nao ser carregadas, no caso Ã© nÃ£o.
        OntDocumentManager dm1 = m1.getDocumentManager();
        dm1.setProcessImports(false);
        OntDocumentManager dm2 = m2.getDocumentManager();
        dm2.setProcessImports(false);

        //Carrega a ontologia
        //* EDITADO NICOLAS System.out.println("O1 :" +owlNome[0]);
        m1.read("file:///home/nicolasferranti/NetBeansProjects/TCC-PPOA/" + owlNome[0],  null);
        m2.read("file:///home/nicolasferranti/NetBeansProjects/TCC-PPOA/" + owlNome[1],  null);

        OntologyCache.setListaIndividuosOnto1(m1.listIndividuals().toList());
        OntologyCache.setListaIndividuosOnto2(m2.listIndividuals().toList());      
        
        //Recupera as classes que vamos verificar a semelhanÃ§a.
        List conceitos1 = element1.getChildren(XMLElements.concept_XML);
        preencheLista(m1, conceitos1, elementosOnt1);

        //Recupera as classes que vamos verificar a semelhanÃ§a.
        List conceitos2 = element2.getChildren(XMLElements.concept_XML);
        preencheLista(m2, conceitos2, elementosOnt2);

        classesOntologias.add(elementosOnt1);
        classesOntologias.add(elementosOnt2);


        // Verifica se usará os pre-alinhamentos
        Element prealign = ontologia.getChild(XMLElements.prealign_XML);
        if (prealign != null) {
            this.callAG = true;
            this.alignURL = prealign.getValue();
        }

        return classesOntologias;
    }

    private void preencheLista(OntModel model, List conceitos, List<OntResource> elementosOnto) {

        ExtendedIterator c1 = model.listClasses().filterDrop(new Filter<OntClass>() {
            public boolean accept(OntClass t) {
                return t.hasURI("http://www.w3.org/2002/07/owl#Thing");
            }
        });

        // Caso o usuário não especificou classes da ontologia
        // Neste caso, serão acrescentadas na lista todas as classes e propriedades da ontologia
        if (conceitos.isEmpty())
            preencheLista(c1, model, elementosOnto);

        // Caso o usuário especificou classes da ontologia
        // TODO permitir que o usuário possa especificar Recursos, ao invés de classes, o que significa que poderia colocar também propriedades no XML.
        else for (ExtendedIterator x = c1; x.hasNext();) {
                OntClass c = (OntClass) x.next();
                for (Iterator j = conceitos.iterator(); j.hasNext();) {
                    Element e = (Element) j.next();
                    if (e.getValue().equals(c.getLocalName())) {
                        elementosOnto.add(c);
                        break;
                    }
                }
            }
        
    }

    private void preencheLista(ExtendedIterator conceito, OntModel modelo, List<OntResource> lista) {

        // Adiciona classes
        for (ExtendedIterator x = conceito; x.hasNext();) {
            OntClass c = (OntClass) x.next();
            if (modelo.getNsPrefixURI("").equals(c.getNameSpace()))
                lista.add(c);
        }


        // Adiciona datatype properties
       for (ExtendedIterator<DatatypeProperty> x = modelo.listDatatypeProperties(); x.hasNext();) {
           OntResource resource = x.next();
           if (modelo.getNsPrefixURI("").equals(resource.getNameSpace()))
                lista.add(resource);
       }

        // Adiciona object properties
       for (ExtendedIterator<ObjectProperty> x = modelo.listObjectProperties(); x.hasNext();) {
           OntResource resource = x.next();
           if (modelo.getNsPrefixURI("").equals(resource.getNameSpace()))
                lista.add(resource);
       }
    }
   

    public Collection<List<OntResource>> getOntology() {
        return listOntologies;
    }

    public Collection<FunctionWeightVO> getFunctions() throws WeightException, DataConversionException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        return listFunctions;
    }

    public boolean callAG() {
        return this.callAG;
    }

    public File getAlignmentFile() {
        return new File(this.alignURL);
    }

}