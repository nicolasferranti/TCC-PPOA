/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity.structure;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Essa classe � utilizada para mapear entidades (conceitos, relacoes, individuos, etc) j� recuperados da ontologia
 * A �nica utilidade da classe � melhorar o desempenho do sistema pois as fun��es de listar entidades do JENA s�o muito custosas
 *
 * @author jairo
 */
public class OntologyCache {

    private static HashMap<OntResource, List> mapaPropriedadesIndividuoOWL = new HashMap<OntResource, List>();
    private static HashMap<OntResource, Set> mapaPropriedades = new HashMap<OntResource, Set>();
    private static HashMap<OntResource, List> mapaIndividuos = new HashMap<OntResource, List>();
    private static HashMap<OntResource, List> mapaSubClasses = new HashMap<OntResource, List>();
    private static HashMap<OntResource, List> mapaSuperClasses = new HashMap<OntResource, List>();
    private static HashMap<OntResource, List> mapaPropriedadesIndiretas = new HashMap<OntResource, List>();
    private static List listaIndividuosOnto1;
    private static List listaIndividuosOnto2;
    private static HashMap<Individual, HashMap<OntProperty, List>> mapaIndividuoPropriedadeRDF = new HashMap<Individual, HashMap<OntProperty, List>>();


    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as propriedades do conceito
     * @param ont
     * @return
     */
    public static Set getOrAddListProperties(OntClass ont) {

      Set<OntProperty> listaProps = OntologyCache.getListProperties(ont);
      
      if (listaProps == null) {
          listaProps = new HashSet<OntProperty>();
          listaProps.addAll(ont.listDeclaredProperties(true).toList());
          addPropriedadesOnRestriction(ont, listaProps);
          OntologyCache.addListProperties(ont, listaProps);
      }
      
      return listaProps;
    }
    public static void addListProperties(OntResource ont, Set props) {mapaPropriedades.put(ont, props); }
    public static Set getListProperties(OntResource ont) {return mapaPropriedades.get(ont);}
    private static void addPropriedadesOnRestriction(OntClass classe, Set lista) {
        for (OntClass superclasses : classe.listSuperClasses(true).toList()) {
            if (!superclasses.isAnon() && superclasses.getURI().startsWith(classe.getURI().substring(0,classe.getURI().indexOf("#"))))
                addPropriedadesOnRestriction(superclasses, lista);
            else if (superclasses.isRestriction())
                lista.add(superclasses.asRestriction().getOnProperty());
        }

    }


    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as instancias do conceito
     * @param ont
     * @return
     */
    public static List getOrAddInstances(OntClass ont) {

      List lista = OntologyCache.getListInstances(ont);

      if (lista == null) {
          lista = ont.listInstances(true).toList();
          OntologyCache.addListInstances(ont, lista);
      }

      return lista;
    }
    public static void addListInstances(OntResource ont, List props) {mapaIndividuos.put(ont, props);}
    public static List getListInstances(OntResource ont) {return mapaIndividuos.get(ont);}


    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as subclasses do conceito
     * @param ont
     * @return
     */
    public static List getOrAddSubClasses(OntClass ont) {

      List lista = OntologyCache.getListSubClasses(ont);

      if (lista == null) {
          lista = ont.listSubClasses(true).toList();
          OntologyCache.addListSubClasses(ont, lista);
      }

      return lista;
    }
    public static void addListSubClasses(OntResource ont, List props) {mapaSubClasses.put(ont, props);}
    public static List getListSubClasses(OntResource ont) {return mapaSubClasses.get(ont);}


    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as superclasses do conceito
     * @param ont
     * @return
     */
    public static List getOrAddSuperClasses(OntClass ont) {

      List lista = OntologyCache.getListSuperClasses(ont);

      if (lista == null) {
          lista = ont.listSuperClasses(true).toList();
          OntologyCache.addListSuperClasses(ont, lista);
      }

      return lista;
    }
    public static void addListSuperClasses(OntResource ont, List props) {mapaSuperClasses.put(ont, props);}
    public static List getListSuperClasses(OntResource ont) {return mapaSuperClasses.get(ont);}


    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as propriedades de um dado individuo
     * @param ont
     * @return
     */
    public static List getOrAddListIndividualProperties(OntResource ont) {

      List lista = OntologyCache.getListIndividualProperties(ont);
      if (lista == null) {
          lista = ont.listProperties().toList();
          OntologyCache.addListIndividualProperties(ont, lista);
      }

      return lista;
    }
    public static void addListIndividualProperties(OntResource ont, List props) {mapaPropriedadesIndividuoOWL.put(ont, props);}
    public static List getListIndividualProperties(OntResource ont) {return mapaPropriedadesIndividuoOWL.get(ont);}

    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as propriedades indiretas de uma dada classe
     * @param ont
     * @return
     */
    public static List getOrAddListIndirectProperties(OntClass ont) {

      List lista = OntologyCache.getListIndirectProperties(ont);
      if (lista == null) {
          lista = ont.listDeclaredProperties(false).toList();
          OntologyCache.addListIndirectProperties(ont, lista);
      }

      return lista;
    }
    public static void addListIndirectProperties(OntResource ont, List props) {mapaPropriedadesIndiretas.put(ont, props);}
    public static List getListIndirectProperties(OntResource ont) {return mapaPropriedadesIndiretas.get(ont);}

    
    
    
    public static List getAllIndividuals(int i) {

      if (i == 1) return listaIndividuosOnto1;
      if (i == 2) return listaIndividuosOnto2;
        
      return null;
    }
    public static void setListaIndividuosOnto1(List listaIndividuos1) { listaIndividuosOnto1 = listaIndividuos1; }
    public static void setListaIndividuosOnto2(List listaIndividuos2) { listaIndividuosOnto2 = listaIndividuos2; }
    

    /***
     * Esse m�todo foi projetado somente retornar (e, se precisar, adicionar no mapa) todas as instancias 
     * de uma dada propriedade direta de um dado individuo
     * @param ont
     * @param prop
     * @return lista de propriedades do individuo
     */
    public static List getOrAddListIndividualProperty(Individual ont, OntProperty prop) {

      List lista = OntologyCache.getListIndividualProperty(ont, prop);
      if (lista == null) {
          lista = ont.listProperties(prop).toList();
          OntologyCache.addListIndividualProperty(ont, prop, lista);
      }

      return lista;
    }
    public static void addListIndividualProperty(Individual ont, OntProperty prop, List props) {
        if (mapaIndividuoPropriedadeRDF.get(ont) == null)
            mapaIndividuoPropriedadeRDF.put(ont, new HashMap<OntProperty, List>());
        mapaIndividuoPropriedadeRDF.get(ont).put(prop, props);
    }
    public static List getListIndividualProperty(Individual ont, OntProperty prop) {
        return mapaIndividuoPropriedadeRDF.get(ont) != null ?  mapaIndividuoPropriedadeRDF.get(ont).get(prop) : null;
    }

    
    
}
