/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppoa.tcc.ppoa;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Parameters;

import fr.inrialpes.exmo.align.impl.BasicEvaluator;
import fr.inrialpes.exmo.align.impl.BasicAlignment;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;

/**
 * Evaluate proximity between two alignments.
 * This function implements Precision/Recall/Fallout. The first alignment
 * is thus the expected one.
 *
 * @author Jerome Euzenat
 * @version $Id: PRecEvaluator.java 391 2007-02-06 20:13:02Z euzenat $
 */

public class NewPRecEvaluator extends BasicEvaluator {

    private List<String[]> expected;

    private double precision = 0.;

    private double recall = 0.;

    private double fallout = 0.;

    private double overall = 0.;

    private double fmeasure = 0.;

    private long time = 0;

    private int nbexpected = 0;

    private int nbfound = 0;

    private int nbcorrect = 0; // nb of cells correctly identified

    /** Creation **/
    public NewPRecEvaluator(Alignment align1, Alignment align2, List<String[]> expected) throws AlignmentException {
	super(((BasicAlignment)align1).toURIAlignment(), ((BasicAlignment)align2).toURIAlignment());

        this.expected = expected;
    }

    public void init(){
	precision = 0.;
	recall = 0.;
	fallout = 0.;
	overall = 0.;
	fmeasure = 0.;
	time = 0;
	nbexpected = 0;
	nbfound = 0;
	nbcorrect = 0;
    }

    /**
     *
     * The formulas are standard:
     * given a reference alignment A
     * given an obtained alignment B
     * which are sets of cells (linking one entity of ontology O to another of ontolohy O').
     *
     * P = |A inter B| / |B|
     * R = |A inter B| / |A|
     * F = 2PR/(P+R)
     * with inter = set intersection and |.| cardinal.
     *
     * In the implementation |B|=nbfound, |A|=nbexpected and |A inter B|=nbcorrect.
     */
    public double eval(Parameters params) throws AlignmentException {
	return eval( params, (Object)null );
    }

    private boolean isPropertyAlignment(URI uri) {
        String str = uri.getFragment();
        return (str.charAt(0) >= 97) && (str.charAt(0) <= 122) ;
    }

    public double eval(Parameters params, Object cache) throws AlignmentException {
	init();
	nbexpected = 0;
	nbfound = align2.nbCells();
	precision = 0.;
	recall = 0.;


	for ( Enumeration e = align1.getElements(); e.hasMoreElements(); nbexpected++) {
	    Cell c1 = (Cell)e.nextElement();
	    URI uri1 = c1.getObject2AsURI();
	    Set s2 = (Set)align2.getAlignCells1( c1.getObject1() );

            boolean foundAlign = false;
	    if( s2 != null ){
		for( Iterator it2 = s2.iterator(); it2.hasNext() && !foundAlign; ){
		    Cell c2 = (Cell)it2.next();
		    URI uri2 = c2.getObject2AsURI();

		    if (uri1.toString().equals(uri2.toString())) {
			nbcorrect++;
                        foundAlign = true;
			//c1 = null; // out of the loop.
		    }
		}
            }

            if (!foundAlign && this.expected != null) {
                String[] str = new String[2];
                str[0] = c1.getObject1AsURI().toString();
                str[1] = c1.getObject2AsURI().toString();
                this.expected.add(str);
            }

	}

	// What is the definition if:
	// nbfound is 0 (p, r are 0)
	// nbexpected is 0 [=> nbcorrect is 0] (r=NaN, p=0[if nbfound>0, NaN otherwise])
	// precision+recall is 0 [= nbcorrect is 0]
	// precision is 0 [= nbcorrect is 0]
	precision = (double) nbcorrect / (double) nbfound;
	recall = (double) nbcorrect / (double) nbexpected;
	fallout = (double) (nbfound - nbcorrect) / (double) nbfound;
	fmeasure = 2 * precision * recall / (precision + recall);
	overall = recall * (2 - (1 / precision));
	result = recall / precision;
	String timeExt = align2.getExtension("time");
	if ( timeExt != null ) time = Long.parseLong(timeExt);
	//System.err.println(">>>> " + nbcorrect + " : " + nbfound + " : " + nbexpected);

	return (result);
    }

    /**
     * This now output the results in Lockheed format.
     */
    public void write(PrintWriter writer) throws java.io.IOException {
	writer.println("<?xml version='1.0' encoding='utf-8' standalone='yes'?>");
	writer.println("<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n  xmlns:map='http://www.atl.external.lmco.com/projects/ontology/ResultsOntology.n3#'>");
	writer.println("  <map:output rdf:about=''>");
	//if ( ) {
	//    writer.println("    <map:algorithm rdf:resource=\"http://co4.inrialpes.fr/align/algo/"+align1.get+"\">");
	//}
	try {
	    writer.println("    <map:input1 rdf:resource=\""+align1.getOntology1URI()+"\"/>");
	    writer.println("    <map:input2 rdf:resource=\""+align1.getOntology2URI()+"\"/>");
	} catch (AlignmentException e) { e.printStackTrace(); };
	// Other missing items (easy to get)
	// writer.println("    <map:falseNegative>");
	// writer.println("    <map:falsePositive>");
	writer.print("    <map:precision>");
	writer.print(precision);
	writer.print("</map:precision>\n    <map:recall>");
	writer.print(recall);
	writer.print("</map:recall>\n    <fallout>");
	writer.print(fallout);
	writer.print("</fallout>\n    <map:fMeasure>");
	writer.print(fmeasure);
	writer.print("</map:fMeasure>\n    <map:oMeasure>");
	writer.print(overall);
	writer.print("</map:oMeasure>\n");
	if ( time != 0 ) writer.print("    <time>"+time+"</time>\n");
    	writer.print("    <result>"+result);
	writer.print("</result>\n  </map:output>\n</rdf:RDF>\n");
    }

    public double getPrecision() { return precision; }
    public double getRecall() {	return recall; }
    public double getOverall() { return overall; }
    public double getFallout() { return fallout; }
    public double getFmeasure() { return fmeasure; }
    public int getExpected() { return nbexpected; }
    public int getFound() { return nbfound; }
    public int getCorrect() { return nbcorrect; }
    public long getTime() { return time; }
    public List<String[]> getExpectedAlignment() { return expected; }
}
