package hugs.search;

import java.util.*;
import hugs.*;
import hugs.logging.*;
import hugs.utils.*;
import java.io.*;

public class Precompute implements Serializable, RunnableExperiment {

   private String searchName;
   private int keep;
   private int num;
   private String fileName;
   private Comparator comparator = new SolutionComparator();
   private String[] paramNames;
   private Object[] paramValues;
   private String problemFilename;
   private int time;

   // NEED TO CLEAN THIS UP.
   public Precompute (String searchName, int keep, String fileName){
      this (searchName,null,null,keep,null,0);
      this.fileName = fileName;
   }

   public Precompute (String searchName, Object[] paramValues,
                      String[] paramNames,
                      int keep, String problemFilename, int time ) {
      this.searchName = searchName;
      this.paramNames = paramNames;
      this.paramValues = paramValues;
      this.time = time;
      this.keep = keep;
      this.problemFilename = problemFilename;
      bestSolutions = new Solution[keep];
      // this.fileName = fileName; should be an input
      num = 0;
   }

   public String getSearchName () { return searchName; }
   public int getKeep () { return keep; }
   public int getNum () { return num; }
   public String getFileName () { return fileName; }
   public String[] getParamNames () { return paramNames; }
   public Object[] getParamValues () { return paramValues; }
   public String getProblemFilename () { return problemFilename; }
   public int getTime () { return time; }

   
   public void run () {
      Hugs.THIS.actionLoadProblem(problemFilename);
      Problem p = (Problem) Utils.readObject(problemFilename);
      Solution s = p.getInitSolution();
      Mobilities m = Hugs.THIS.getMobilities();
      m.setAll(Mobilities.HIGH);
      run (p,s,m,time);
   }
   
   public void run (Problem p, Solution s, Mobilities m, int time){
      System.out.println("solution " + s + ", mob " + m);
      List nullMarked = new ArrayList(0);
      SearchThread search = Hugs.getSearch(searchName);
      if ( paramValues != null && paramNames != null )
         ExpFunc.setSearchParams(search,paramValues,paramNames);
      ((PrecomputeThread)search).setPrecompute(this);
      search.setProblem(p);
      // search.start();
      search.startSearch(s,m,nullMarked);
      System.out.println("**** need to implement Precompute now that we have de-threaded search algorithm!");
      if (true)
      System.exit(0);
      try { Thread.currentThread().sleep(time * 1000); }
      catch (InterruptedException e) { System.err.println("Sleep Interrupted");};
      search.stopSearch();
      // search.stop();
   }

   public Solution[] getRanked () {
      return bestSolutions;
   }
   private Solution[] bestSolutions;
   public void registerSolution (Solution s ){
      for (int i = 0; i < num; i++)
         if ( bestSolutions[i].equals(s) ) return;
      boolean add = false;
      if ( num < keep ) {
         bestSolutions[num++] = s;
         add = true;
      }
      else {
         Solution last = bestSolutions[keep-1];
         if ( s.getScore().isBetter(last.getScore()) ){
            bestSolutions[keep-1] = s;
            add = true;
         }
      }
      if ( add ) {
         Arrays.sort(bestSolutions,comparator);
         System.out.println("New keeper: " + s);
      }
      else {
         System.out.print(".");
      }
   }

   public void save () {
      Utils.writeObject(this,fileName);
   }

   
   public String toString () {
      String pV =  paramValues == null ? "" : (","+Arrays.asList(paramValues));
      String pN = paramNames == null ? "" : (","+Arrays.asList(paramNames));
      return "[Precompute " + searchName + pV // + pN
         + ",keep=" + keep + ","
         + problemFilename 
         + ",time=" + time + "]";
   }
   public static Precompute load (String fileName) {
      return (Precompute) Utils.readObject(fileName);
   }
}


