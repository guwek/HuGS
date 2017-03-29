package hugs.logging;

import hugs.search.*;
import hugs.*;
import java.util.*;
import java.io.*;

public class SearchTrace extends LogEvent {

   private Problem problem;
   private Solution initialSolution;
   private Mobilities mobilities;
   private String searchName;
   private Date date;
   private Score initialScore;
   private List reports = new ArrayList();
   private List inputs;
   public SearchTrace (Problem problem, Solution solution,
                       Mobilities mobilities, List inputs,
                       SearchThread search)
   {
      this.problem = problem;
      this.initialSolution = solution.copy();
      this.initialScore = solution.computeScore().copy();
      this.searchName = search.getSearchName();
      this.date = Calendar.getInstance().getTime();
      this.mobilities = (Mobilities) mobilities.clone();
      this.inputs = new ArrayList(inputs.size());
      for (Iterator i = inputs.iterator(); i.hasNext();)
         this.inputs.add(((Parameter)i.next()).copy());
   }

   public void addReport (SearchReport r) { reports.add(r);}
   public Problem getProblem () { return problem; }
   public Solution getInitialSolution () { return initialSolution; }
   public Mobilities getMobilities () { return mobilities; }
   public String getSearchName () { return searchName; }
   public Date getDate () { return date; }
   public Score getInitialScore () { return initialScore; }
   public List getInputs () { return inputs; }
   public List getReports () { return reports; }

   private Score finalScore () {
      return reports.size() == 0 ? null :
         ((SearchReport) reports.get(reports.size()-1)).getScore();
   }

   // public void print () { print (System.out);  }
   public void printBody (PrintWriter p) {
      printAux(p);
   }
   public void print () {
      System.out.println("Figure out how to use printAux with System.out");
   }
   
   public void printAux (PrintWriter p) {
      p.println("SearchTrace");
      p.println(" #problem: " + problem);
      p.println(" #solution: " + initialSolution);
      p.println(" #mobilities: " + mobilities);
      p.println(" #searchName: " + searchName);
      p.println(" #inputs: " + inputs);
      p.println(" #initialScore: " + initialScore);
      p.println(" #finalScore: " + finalScore());
      p.println(" #date: " + date);
      p.println(" #reports: " + reports.size());
      for (Iterator i = reports.iterator(); i.hasNext();)
         p.println("  " + i.next());
   }

   public String toString () {
      return "[SearchTrace: " + searchName + " from initial score " + initialScore + " and " +
         inputs + " producing " + reports.size() + " reports and final score " + finalScore() + "]";
   }

}
