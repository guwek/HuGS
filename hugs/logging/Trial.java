package hugs.logging;

import hugs.*;
import hugs.search.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;
import java.awt.event.*;

public class Trial implements Serializable, RunnableExperiment {
   private Object[] params;
   private String[] names;
   private SearchTrace result = null;
   private String searchName;
   private int timeLimit;
   private int cycle;
   private String precomputation;
   private String problemFilename;

   public String getSearchName () { return searchName; }
   public int getTime () { return timeLimit; }
   public int getCycle () { return cycle; }
   public String getPrecomputation () { return precomputation; }
   public String getProblemFilename () { return problemFilename; }
   public Object[] getParamValues () { return params;}
   public String[] getParamNames () { return names;}
   public SearchTrace getResult () { return result;}
   public Trial (Object[] params, String names[], String searchName,
                  int timeLimit, int cycle ){
      this(params,names,searchName,timeLimit,cycle,null,null);
   }
   
   public Trial (Object[] params, String names[], String searchName,
                  int timeLimit, int cycle, String problemFilename,
                  String precomputation ){
      this.params = params;
      this.names = names;
      this.searchName = searchName;
      this.timeLimit = timeLimit;
      this.cycle = cycle;
      this.problemFilename = problemFilename;
      this.precomputation = precomputation;
   }

   /*
   private void setParam (Parameter input, Object value ){
      if ( input instanceof BooleanParameter &&  value instanceof Boolean )
         ((BooleanParameter) input).value = (((Boolean)value).equals(Boolean.TRUE));
      else if ( input instanceof IntParameter &&  value instanceof Integer )
         ((IntParameter) input).value = ((Integer)value).intValue();
      else if ( input instanceof DoubleParameter &&  value instanceof Double )
         ((DoubleParameter) input).value = ((Double)value).doubleValue();
      else {
             System.out.println("Error in Experiment.Trial.setParam -- can't set " + input.getName() + " to " + value);
             System.exit(0);
          }
       }
   */


   private SearchTrace searchTrace;
    // private SearchThread search;
    private int startTime;
    private int stopTime = -1;
    private boolean halt = false;
    private void startTrace (Solution s, SearchThread search){
	searchTrace = new SearchTrace(Hugs.THIS.getProblem(),
				      s, 
				      Hugs.THIS.getMobilities(),
				      search.getInputs(),
				      search);
	startTime = Utils.getSeconds();
    }
   public void updateTrace (SearchThread search){
      if ( searchTrace == null ) return;
      int time = Utils.getSeconds() -startTime;
      Solution s = search.getBestSolution();
      Score best = s == null ? null : s.getScore();
      java.util.List outputs = search.getOutputs();
      if ( s != null )
         searchTrace.addReport(new SearchReport(time,best,outputs));
      System.out.println(Hugs.instanceName + " update@" + time + ": " + best + " " + outputs);
      if ( stopTime > 0 && time > stopTime )
	  halt = true;
   }
   
    private void runExperimentByTime (int cycle, int time, final SearchThread search){
	stopTime = time;
	javax.swing.Timer timer;
	ActionListener listen =   new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    updateTrace(search);
		}
	    };
	timer = new javax.swing.Timer (cycle*1000,listen);
	timer.start();
	Solution solution = Hugs.THIS.getCurrentSolution();
	startTrace(solution,search);
	Mobilities mobilities = Hugs.THIS.getMobilities();
	List marked = Hugs.THIS.getMarkedList();
	search.startSearch(solution,mobilities,marked);
	search.initSearch(search.getSolution(),search.getMobilities(),search.getMarked());
	halt = false;
	while (!halt) {
	    search.searchIteration();
	}
    }

   private void runExperimentByTrial (int cycle, int num,
                                      int time, final SearchThread search){
      stopTime = time;
      Solution solution = Hugs.THIS.getCurrentSolution();
      startTrace(solution,search);
      Mobilities mobilities = Hugs.THIS.getMobilities();
      List marked = Hugs.THIS.getMarkedList();
      search.startSearch(solution,mobilities,marked);
      search.initSearch(search.getSolution(),search.getMobilities(),search.getMarked());
      boolean halt = false;
      while ( !halt ) {
         for (int i = 0; i < num; i++)
            search.searchIteration();
         updateTrace(search);
         Hugs.THIS.updateBestFound(search.getBestSolution());
      }
   }
   
   public void run () { // String searchName, int timeLimit, int cycle){
       SearchThread search = Hugs.getSearch(searchName);
       search.setSearchAdjuster(Hugs.THIS.makeSearchAdjuster());
      String problemName = null;
      if ( problemFilename != null ) 
         problemName = problemFilename;
      else {
         for (int j = names.length; j-->0;)
            if ( names[j].equals("PROBLEM") )
               problemName = (String) params[j];
      }
      if ( problemName == null ){
         System.out.println("No problem specified for trial! " + this);
         System.exit(0);
      }
      if (Hugs.THIS == null )
         System.out.println("*** need to make hugs !!! ***");
      Hugs.THIS.actionLoadProblem(problemName);
      search.setProblem(Hugs.THIS.getProblem());
      if ( precomputation != null ) {
         Precompute p = (Precompute) Utils.readObject(precomputation);
         if ( p == null ) {
            System.out.println("Trial: bad precomputation = " + precomputation);
            System.exit(1);
         }
         Solution best = p.getRanked()[0];
         Hugs.THIS.updateSolution(best);
      }
      ExpFunc.setSearchParams(search,params,names);

      // runExperimentByTime(cycle, timeLimit,search);
      runExperimentByTrial(cycle, 5000, 100, search);
      result = searchTrace;
      System.out.println("Result: " + result);
      result.print();
      
   }

   public String toString () {
      String pV =  params == null ? "" : (","+Arrays.asList(params));
      String pN = names == null ? "" : (","+Arrays.asList(names));
      return "[Trial " + searchName + pV // + pN
         + ",cycle=" + cycle + ","
         + problemFilename + ","
         + precomputation 
         + ",time=" + timeLimit + "]";
   }
   
}


