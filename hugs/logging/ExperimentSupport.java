package hugs.logging;

import hugs.search.*;
import java.util.*;
import hugs.*;
import java.io.*;
import hugs.utils.*;

public class ExperimentSupport implements Serializable {

   private List runnables = new ArrayList();
   private List filenames = new ArrayList();
   private String filename;
   private int numRun = 0;


   
   public ExperimentSupport (String filename ) {
      this.filename = filename;
   }

   public List getRunnables () { return runnables;}
   public List getFilenames () { return filenames;}
   
   public String addPrecompute (String searchName, Object[] paramValues,
                                String[] paramNames, int keep,
                                String problem, int time, String header) {
      int size = runnables.size();
      for (int i = 0; i < size; i++) {
         RunnableExperiment r = (RunnableExperiment) runnables.get(i);
         if ( r instanceof Precompute ) {
            Precompute p = (Precompute) r;
            if ( p.getSearchName().equals(searchName) &&
                 p.getKeep() == keep &&
                 p.getTime() == time &&
                 p.getProblemFilename() != null &&
                 p.getProblemFilename().equals(problem) &&
                 ExpFunc.paramsEqual(p.getParamValues(),p.getParamNames(),
                                     paramValues,paramNames) )
               return (String) filenames.get(i);
         }
      }
      Precompute p = new Precompute(searchName,paramValues,paramNames,
                                    keep,problem,time);
      String filename = header + "_pre_" + runnables.size();
      System.out.println("@@ExperimentSupport: new precompute= " + p);
      Utils.writeObject(p,filename);
      runnables.add(p); 
      filenames.add(filename); 
      save();
      return filename;
   }

   public String addComparison (String searchName,
                                Object[] paramValues,
                                String[] paramNames, 
                                String problem,
                                int time,
                                int cycle,
                                String precomputation,
                                String header){
      int size = runnables.size();
      for (int i = 0; i < size; i++) {
         RunnableExperiment r = (RunnableExperiment) runnables.get(i);
         if ( r instanceof Trial ) {
            Trial t = (Trial) r;
            if ( t.getSearchName().equals(searchName) &&
                 t.getCycle() == cycle &&
                 t.getTime() == time &&
                 t.getProblemFilename() != null &&
                 t.getProblemFilename().equals(problem) &&
                 ( (t.getPrecomputation() == null && precomputation == null ) ||
                  t.getPrecomputation().equals(precomputation) ) &&
                 ExpFunc.paramsEqual(t.getParamValues(),t.getParamNames(),
                                     paramValues,paramNames) )
               return (String) filenames.get(i);
         }
      }
      Trial t = new Trial(paramValues,paramNames,searchName,
                          time,cycle,problem,precomputation);
      String filename = header + "_comp_" + runnables.size();
      System.out.println("@@ExperimentSupport: new trial= " + t);
      Utils.writeObject(t,filename);
      runnables.add(t);
      filenames.add(filename);
      save();
      return filename;

   }
   
         
   public void save () {
      Utils.writeObject(this,filename);
   }

   public boolean allRun () {
      return numRun == runnables.size();
   }


   public void runNext (){
      if ( allRun() ) return;
      String runName = (String) filenames.get(numRun);
      RunnableExperiment r = (RunnableExperiment) Utils.readObject(runName);
      System.out.println("Running #" + numRun + ":"  + r);
      r.run();
      numRun++;
      Utils.writeObject(r,runName);
      Utils.writeObject(this,filename);
   }

   public static boolean run (String filename, int num){
      ExperimentSupport support =
         (ExperimentSupport) Utils.readObject(filename);
      for (int i = num; i-->0;)
         support.runNext();
      return support.allRun();
   }
}

