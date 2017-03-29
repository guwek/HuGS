package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class UserSet implements Serializable {

   private UserTrial[] trials;
   private List[] logNames;

   public UserTrial[] getTrials () { return trials; }
   public List[] getLogNames() { return logNames; }
   
   public UserSet (List trialList) {
      int size = trialList.size();
      trials = new UserTrial[size];
      logNames = new List[size];
      for (int i = 0; i < size; i++) {
         logNames[i] = new ArrayList();
         trials[i] = (UserTrial) trialList.get(i);
      }
   }
   
   public UserTrial getTrial (int num, String logName) {
      if ( num >= trials.length ) {
         System.out.println("UserSet: out of bounds number " + num);
         return null;
      }
      logNames[num].add(logName);
      return trials[num];
   }
   
   public UserTrial getNextTrial (String logName) {
      for (int i = 0; i < trials.length; i++)
         if ( logNames[i].isEmpty() )
            return getTrial(i,logName);
      System.out.println("done with userset");
      return null;
   }

   public void print () {
      System.out.println("User set");
      for (int i = 0; i < trials.length; i++){
         int size = logNames[i].size();
         System.out.println("  (" + size + ") " + trials[i]);
         for (int j = 0; j < size; j++)
            System.out.println("      --" + logNames[i].get(j));
      }
   }
}
