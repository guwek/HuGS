package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

// this class just exists to avoid having thousands of files, one for
// each trial
public class TrialSet implements Serializable {

   private String fileName;
   private List trials;
   int numRun = 0;

   public TrialSet (List trials, String fileName){
      this.fileName = fileName;
      this.trials = trials;
      System.out.println("**** constructing trialSet ****");
      Utils.enumerateList(this.trials);
   }

   public List getTrials () { return trials; }

   public boolean allRun () {
      return numRun == trials.size();
   }
   
   public void runNext (){
      if ( allRun() ) return;
      Trial trial = (Trial) trials.get(numRun);
      System.out.println("Running #" + numRun + ":"  + trial);
      trial.run();
      numRun++;
      Utils.writeObject(this,fileName);
   }

   public static boolean run (String fileName, int num){
      TrialSet set = (TrialSet) Utils.readObject(fileName);
      for (int i = num; i-->0;)
         set.runNext();
      return set.allRun();
   }
   
}
