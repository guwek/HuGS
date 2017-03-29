package hugs.apps.crossing;

import hugs.*;
import java.io.*;    
import java.util.*;
import hugs.utils.*;


public class convertToLEDA {

    public static void main (String[] args) {
	// usage: convertToLEDA javaProblemFileName ledaFileName
	
	String javaProblemFileName = args[0];
	String ledaProblemFileName = args[1];
	
	System.out.println("loading " + javaProblemFileName);
	FileInputStream ostream = null;
	ObjectInputStream p = null;
	CrossingProblem problem = null;
	try {
	    ostream = new FileInputStream(javaProblemFileName);
	} catch (Exception e) { 
	    System.out.println("convertToLEDA: can't open file: " + javaProblemFileName);
	}
	try {
	    p = new ObjectInputStream(ostream);
	} catch (Exception e) { 
	    System.out.println("convertToLEDA: object input");
	}
	try {
	    problem = (CrossingProblem) p.readObject(); 
	} catch (Exception e) { 
	    System.out.println("convertToLEDA: can't read object:" + 
			       javaProblemFileName);
	}
	try {
	    ostream.close();
	} catch (Exception e) { 
	    System.out.println("convertToLEDA: can't close");
	}

	PrintWriter w = null;

// 	try {
// 	    w = new FileWriter(ledaProblemFileName);
// 	} catch (Exception e) {
// 	    System.err.println("convertToLEDA: can't open file: " + 
// 			       ledaProblemFileName);
// 	}

	System.out.println("writing LEDA graph " + ledaProblemFileName);
	
	try {
	    w = Utils.openPrintWriter(ledaProblemFileName);
	} catch (RuntimeException e) {
	    System.err.println("convertToLEDA: can't get PrintWriter");
	}

	w.write("LEDA.GRAPH\n");
	w.write("void\n");
	w.write("void\n");
	w.write(problem.size() + "\n");
	for (int i = problem.size(); i-- > 0; ) {
	    w.write("|{}|" + "\n");
	}
	w.write(problem.getNumEdges() + "\n");

	Vector allEdges = new Vector(200);
	allEdges = problem.getEdges();

	for (int i = problem.getNumEdges(); i-- > 0; ) {
	    Edge e = (Edge) allEdges.elementAt(i);
	    w.write((e.from.getId() + 1) + " " + (e.to.getId() + 1) + " 0 |{}|" + "\n");
	}
	w.close();
    }
}
