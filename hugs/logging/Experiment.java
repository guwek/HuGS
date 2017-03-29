package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class Experiment implements Serializable { 

   private String searchName;
   private List values = new ArrayList();
   private String[] paramNames;
   private String name;
   private String filename;
   private int timeLimit;
   private int cycle;
   private List trialSets = new ArrayList();
   private boolean[] tried;

   public void update() {
      paramNames = new String[values.size()];
      int count = 0;
      for (Iterator i = values.iterator(); i.hasNext();)
         paramNames[count++] = ((ParamValues) i.next()).name;
   }

   public String getSearchName () { return searchName;}
   public List getValues () { return values;}
   public String[] getParamNames () { return paramNames; }
   public String getName () { return name; }
   public String getFilename () { return filename; }
   public int getTimeLimit () { return timeLimit; }
   public int getCycle () { return cycle; }
   public List getTrialSets () { return trialSets; }

   public void setSearchName (String s) { searchName = s; }
   public void setValues (List l) {  values = l;}
   public void setParamNames (String[] s) {  paramNames = s; }
   public void setName (String s) {  name =s; }
   public void setFilename (String s) {  filename =s; }
   public void setTimeLimit (int s) {  timeLimit = s; }
   public void setCycle (int s) {  cycle = s; }
   public void setTrialSets (List s) {  trialSets =s; }
   

   public List makeTrials () {
      List trials = new ArrayList() ;
      addTrials(paramNames,trials);
      return trials;
   }

   public void print () {
	System.out.println("name: " + name);
	System.out.println("timeLimit: " + timeLimit);
        System.out.println("cycle: " + cycle);
	System.out.println("search: " + searchName);
	System.out.println("param specs");
	for (Iterator i = values.iterator(); i.hasNext();)
	    System.out.println("  " + i.next());
	System.out.println("trialSets: " + trialSets.size());
	// for (Iterator i = trials.iterator(); i.hasNext();)
        // System.out.println("  " + i.next());
    }

   /*
    public Trial firstUntried (String[] names) {
	int[] positions = new int[values.size()];
	for (int i = positions.length; i-->0;)
	    positions[i] = 0;
	return firstUntried(positions,names);
    }

    public boolean isTried (Object[] possible) {
	for (Iterator i = trials.iterator(); i.hasNext();){
	    Trial trial = (Trial) i.next();
	    if ( trial == null ) continue;
	    boolean tried = true;
	    for (int j = possible.length; j-->0;)
               if (possible[j] != trial.getParams()[j])
		    tried = false;
	    if ( tried ) return true;
	}
	return false;
    }
    public Trial firstUntried (int [] positions, String[] names) {
	Object[] possible = new Object[positions.length];
	for (int i = positions.length; i-->0;){
	    possible[i] = ((ParamValues) values.get(i)).values.get(positions[i]);
	}
	if ( !isTried(possible) ) {
 	    return new Trial(possible, names);
	}
	for (int i = positions.length; i-->0;){
	    ParamValues p = (ParamValues) values.get(i);
	    if ( positions[i] < (p.values.size()-1) ) {
		positions[i]++;
		for (int j = i +1; j < positions.length; j++) 
		    positions[j] = 0;
		return firstUntried(positions,names);
	    }
	}
	return null;
    }

   */
   
   private void addTrials (String[] names, List trials) {
      int[] positions = new int[values.size()];
      for (int i = positions.length; i-->0;)
         positions[i] = 0;
      addTrials(positions,names, trials);
   }

   private void addTrial (Object[] possible, String[] names, List trials) {
      /*
      System.out.print("addTrial: ");
      for (int i = 0; i < possible.length; i++)
         System.out.print(possible[i] + " ");
      System.out.println();
      */
      Trial trial = new Trial(possible,names,searchName,timeLimit,cycle);
      trials.add(trial);
   }

   private void addTrials (int [] positions, String[] names, List trials) {
      Object[] possible = new Object[positions.length];
      for (int i = positions.length; i-->0;){
         possible[i] = ((ParamValues) values.get(i)).values.get(positions[i]);
      }
      addTrial(possible,names,trials);
      for (int i = positions.length; i-->0;){
	    ParamValues p = (ParamValues) values.get(i);
	    if ( positions[i] < (p.values.size()-1) ) {
		positions[i]++;
		for (int j = i +1; j < positions.length; j++) 
		    positions[j] = 0;
		addTrials(positions,names,trials);
	    }
	}
   }


}
