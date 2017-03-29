package hugs.logging;

import hugs.*;
import hugs.search.*;
import hugs.support.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class ExpFunc {

   public static int CYCLE_TIME = 2;


   private static void setParam (Parameter input, Object value ){
      if ( input instanceof BooleanParameter &&  value instanceof Boolean )
         ((BooleanParameter) input).value = (((Boolean)value).equals(Boolean.TRUE));
      else if ( input instanceof IntParameter &&  value instanceof Integer )
         ((IntParameter) input).value = ((Integer)value).intValue();
      else if ( input instanceof DoubleParameter &&  value instanceof Double )
         ((DoubleParameter) input).value = ((Double)value).doubleValue();
      else {
         System.out.println("Error in ExpFunc.setParam -- can't set " + input.getName() + " to " + value);
         System.exit(0);
      }
   }

   public static void setSearchParams (SearchThread search,
                                       Object[] values, String[] names) {
      List inputs = search.getInputs();
      for (Iterator i = inputs.iterator(); i.hasNext();){
         Parameter input = (Parameter) i.next();
         String name = input.getName();
         for (int j = names.length; j-->0;)
            if ( names[j].equals(name) ){
               setParam(input,values[j]);
            }
      }
   }

   private static Object getValue (String name, Object[] values, String[] names ){
      if ( name == null ) return null;
      for (int i = names.length; i-->0;)
         if ( name.equals(names[i]) )
            return values[i];
      return null;
   }

   private static boolean paramsEqual (Object o1, Object o2) {
      if ( o1 instanceof Boolean && o2 instanceof Boolean )
         return ((Boolean)o1).equals((Boolean)o2);
      if ( o1 instanceof Integer && o2 instanceof Integer )
         return ((Integer)o1).intValue() == ((Integer)o2).intValue();
      if ( o1 instanceof Double && o2 instanceof Double )
         return ((Double)o1).doubleValue() == ((Double)o2).doubleValue();
      return false;
   }
   public static boolean paramsEqual ( Object[] values1, String[] names1,
                                       Object[] values2, String[] names2) {
      if ( values1 == null && values2 == null ) return true;
      if ( values1.length != values2.length ) return false;
      for (int i = names1.length; i-->0;) {
         Object value = getValue(names1[i],values2,names2);
         if ( value == null ) return false;
         if ( !paramsEqual(values1[i],value) ) {
            System.out.println("@@@ not equal " + values1[i] + "/" + value);
            return false;
         }
      }
      return true;
   }
                                       

   
   private static List allPermutations (List values) {
      int[] positions = new int[values.size()];
      for (int i = positions.length; i-->0;)
         positions[i] = 0;
      List permutations = new ArrayList();
      addPermutations(positions,values,permutations);
      return permutations;
   }

   private static boolean isMatch (Object[] possible, List values, Trial trial) {
      String[] trialParamNames = trial.getParamNames();
      Object[] trialParamValues = trial.getParamValues();
      for (int j = possible.length; j-->0;){
         String name = (String) ((ParamValues) values.get(j)).name;
         for (int i = trialParamNames.length; i-->0;)
            if ( name.equals(trialParamNames[i]) &&
                 !possible[j].toString().equals(trialParamValues[i].toString()) ){
               return false;
            }
      }
      return true;
   }
   
   private static List matches (Object[] possible, List values, List trials) {
      List matches = new ArrayList();
      for (Iterator i = trials.iterator(); i.hasNext();){
         Trial trial = (Trial) i.next();
         if ( trial == null ) continue;
         if ( isMatch(possible,values,trial) )
            matches.add(trial);
      }
      return matches;
   }


   public static void addPermutations (int [] positions, List values, List list) {
	Object[] possible = new Object[positions.length];
	for (int i = positions.length; i-->0;){
	    possible[i] = ((ParamValues) values.get(i)).values.get(positions[i]);
	}
	list.add(possible);
	for (int i = positions.length; i-->0;){
	    ParamValues p = (ParamValues) values.get(i);
	    if ( positions[i] < (p.values.size()-1) ) {
		positions[i]++;
		for (int j = i +1; j < positions.length; j++) 
		    positions[j] = 0;
		addPermutations(positions,values,list);
	    }
	}
   }

   public static void make123 (String[] args) {
	Experiment e = new Experiment();
        String searchName = Utils.getValue("-search",args);
        if ( searchName == null ) System.out.println("ERROR: must specify search(name)");
        String name = Utils.getValue("-name",args);
        if ( name == null ) System.out.println("ERROR: must specify name");
        String problem = Utils.getValue("-problem",args);
        if ( problem == null ) System.out.println("ERROR: must specify problem");
        String dir = Utils.getValue("-dir",args);
        if ( dir == null ) System.out.println("ERROR: must specific dir");
        String numString = Utils.getValue("-num",args);
        if ( numString == null ) System.out.println("ERROR: must specify a num of problems to run on");
        String cycleString = Utils.getValue("-cycle",args);
        if ( cycleString == null ) System.out.println("ERROR: must specify a num of problems to run on");
        String timeLimitString = Utils.getValue("-timeLimit",args);
        if ( timeLimitString == null ) System.out.println("ERROR: must specify a num of problems to run on");
        if ( name == null || problem == null || dir == null || searchName == null ||
             numString == null || timeLimitString == null ||
             cycleString == null)
           System.exit(0);
        String trialsPerSetString = Utils.getValue("-trialsPerSet",args);
        int trialsPerSet = ( trialsPerSetString == null ) ? 20 : 
           Utils.stringToInt(trialsPerSetString);
        String numSetsString = Utils.getValue("-numSets",args);
        int userNumSets = ( numSetsString == null ) ? -1 : 
           Utils.stringToInt(numSetsString);
        int num = Utils.stringToInt(numString);
        int cycle = Utils.stringToInt(cycleString);
        int timeLimit = Utils.stringToInt(timeLimitString);

	e.setName(name);
	e.setTimeLimit(timeLimit);
        e.setCycle(cycle);

        /* 
	int number = 10;
	int time = 5;
        java.util.List tabus = new ArrayList(number);
        for (int i = number; i-->0;)
           tabus.add(new TabuThread() );
	e.setSearch(new MultiSearchThread(tabus,time));       
        */
        
	e.setSearchName(searchName);
        ParamValues problems = new ParamValues("PROBLEM");
        if ( num == 0 ) {
           problems.values.add(problem);
           num = 1;
        }
        else
           for (int i = 0; i < num; i++)
              problems.values.add(problem+i);
        e.getValues().add(problems);
        
        if ( searchName.equals("tabu") ){
           ParamValues minD = new ParamValues("minD");
           ParamValues noise = new ParamValues("noise");
           ParamValues memory = new ParamValues("memory");
           ParamValues maxPly = new ParamValues("maxPly");
           ParamValues greedy = new ParamValues("greedy");
           String valueKey = Utils.getValue("-values",args);
           if ( valueKey != null && valueKey.equals("specify") ){
              String s;
              s = Utils.getValue("-minD",args);
              if ( s == null )
                 minD.values.add(new Double(0.0));
              else minD.values.add(new Double(Utils.stringToDouble(s)));
              s = Utils.getValue("-greedy",args);
              if ( s == null )
                 greedy.values.add(Boolean.FALSE);
              else
                 greedy.values.add("greedy".equals(s) ? Boolean.FALSE : Boolean.TRUE);
              s = Utils.getValue("-memory",args);
              if ( s == null )
                 memory.values.add(new Integer(10));
              else
                 memory.values.add(new Integer(Utils.stringToInt(s)));
              s = Utils.getValue("-maxPly",args);
              if ( s == null )
                 maxPly.values.add(new Integer(1));
              else
                 maxPly.values.add(new Integer(Utils.stringToInt(s)));
              s = Utils.getValue("-noise",args);
              if ( s == null )
                 noise.values.add(new Double(0));
              else
                 noise.values.add(new Double(Utils.stringToDouble(s)));
           }
           else if ( valueKey != null && valueKey.equals("tabu0") ){
              minD.values.add(new Double(.4));
              greedy.values.add(Boolean.FALSE);
              memory.values.add(new Integer(10));
              maxPly.values.add(new Integer(1));
              noise.values.add(new Double(0));
           }
           else if ( valueKey != null && valueKey.equals("tabuAAAI") ){
              for (int i = 0; i < 10; i++)
                 minD.values.add(new Double((double) i * .1));
              greedy.values.add(Boolean.FALSE);
              memory.values.add(new Integer(10));
              maxPly.values.add(new Integer(1));
              noise.values.add(new Double(0));
           }
           else if ( valueKey != null && valueKey.equals("tabuT") ){
              minD.values.add(new Double(.8));
              greedy.values.add(Boolean.FALSE);
              memory.values.add(new Integer(10));
              maxPly.values.add(new Integer(1));
              noise.values.add(new Double(0));
           }
           else if ( valueKey != null && valueKey.equals("tabuP") ){
              minD.values.add(new Double(.4));
              greedy.values.add(Boolean.FALSE);
              memory.values.add(new Integer(5));
              maxPly.values.add(new Integer(1));
              noise.values.add(new Double(0));
           }
           else if ( valueKey != null && valueKey.equals("tabu1") ){
              // minD.values.add(new Double(0.2));
              minD.values.add(new Double(0.4));
              // minD.values.add(new Double(0.6));
              minD.values.add(new Double(0.8));
              greedy.values.add(Boolean.FALSE);
              memory.values.add(new Integer(10));
              maxPly.values.add(new Integer(1));
              noise.values.add(new Double(0));

              /*
              for (int i = 0; i < 4; i++){
                 minD.values.add(new Double((double) i * .2));
                 noise.values.add(new Double((double) i * .2));
              }
              memory.values.add(new Integer(5));
              memory.values.add(new Integer(10));
              // memory.values.add(new Integer(1));
              memory.values.add(new Integer(15));
              maxPly.values.add(new Integer(1));
              // maxPly.values.add(new Integer(2));
              greedy.values.add(Boolean.TRUE);
              greedy.values.add(Boolean.FALSE);
              */
           }
           else if ( valueKey != null && valueKey.equals("tabu2") ){
              minD.values.add(new Double(.4));
              noise.values.add(new Double(0));
              memory.values.add(new Integer(5));
              maxPly.values.add(new Integer(1));
              greedy.values.add(Boolean.TRUE);
              greedy.values.add(Boolean.FALSE);
           }
           else if ( valueKey != null && valueKey.equals("tabu3") ){
              minD.values.add(new Double(.4));
              minD.values.add(new Double(.5));
              minD.values.add(new Double(.6));
              noise.values.add(new Double(0));
              memory.values.add(new Integer(5));
              memory.values.add(new Integer(10));
              maxPly.values.add(new Integer(1));
              greedy.values.add(Boolean.FALSE);
           }
           else if ( valueKey != null && valueKey.equals("tabu4") ){
              minD.values.add(new Double(.3));
              minD.values.add(new Double(.4));
              minD.values.add(new Double(.5));
              minD.values.add(new Double(.6));
              minD.values.add(new Double(.7));
              noise.values.add(new Double(0));
              memory.values.add(new Integer(10));
              memory.values.add(new Integer(15));
              maxPly.values.add(new Integer(1));
              greedy.values.add(Boolean.FALSE);
              greedy.values.add(Boolean.TRUE);
           }
           else if ( valueKey != null && valueKey.equals("tabu5") ){
              minD.values.add(new Double(.35));
              minD.values.add(new Double(.5));
              noise.values.add(new Double(0));
              memory.values.add(new Integer(10));
              maxPly.values.add(new Integer(1));
              greedy.values.add(Boolean.FALSE);
           } 
           else{
              System.out.println("\n ***should specify a '-values' such as tabu1 or tabu2 (see ExpFunc.java)");
              System.exit(0);
           }
           e.getValues().add(greedy);	
           e.getValues().add(memory);
           e.getValues().add(maxPly);
           e.getValues().add(noise);
           e.getValues().add(minD);
        }
        

	e.update();
	e.print();
	e.setFilename(dir + e.getName());
        List allTrials = e.makeTrials();
        int size  = allTrials.size();
        int numSets = 0;
        if ( userNumSets > 0 ) {
           numSets = userNumSets;
           trialsPerSet = size / numSets;
        }
        else numSets = size / trialsPerSet;
        if ( numSets * trialsPerSet < size )
           numSets++;
        List sets = new ArrayList(numSets);
        int count = 0;
        for (int i = 0; i < numSets; i++) {
           List trials = new ArrayList(trialsPerSet);
           for (int j = 0; j < trialsPerSet; j++) {
              int index = i * trialsPerSet + j;
              if ( index < size ) {
                 System.out.println(count++ + ":" + i + "/" + j + " "
                                    + allTrials.get(index));
                 trials.add(allTrials.get(index));
              }
           }
           String fileName = dir + name + "Trial" + i;
           System.out.println("Filename = " + fileName);
           TrialSet set = new TrialSet(trials,fileName);
           sets.add(fileName);
           Utils.writeObject(set,fileName);
        }
        e.setTrialSets(sets);
        Utils.enumerateList(sets);
	Utils.writeObject(e,e.getFilename());
    }

   private static ParamValues getParamValues (List values, String name) {
      if ( name == null ) return null;
      for (Iterator i = values.iterator(); i.hasNext();){
         ParamValues p = (ParamValues)i.next();
         if ( name.equals(p.name)) return p;
      }
      return null;
   }

   private static List getRunTrials (Experiment e){
      List trials = new ArrayList();
      List sets = e.getTrialSets();
      for (Iterator i = sets.iterator(); i.hasNext(); ){
         TrialSet set = (TrialSet) Utils.readObject((String) i.next());
         for (Iterator j = set.getTrials().iterator(); j.hasNext();){
            Trial trial = (Trial) j.next();
            if ( trial.getResult() != null )
               trials.add(trial);
         }
      }
      return trials;
   }
   
   private static SearchCollection[] getCollections (Experiment experiment, List paramNames){
      List allValues = experiment.getValues();
      List values = new ArrayList();
      for (Iterator i = paramNames.iterator(); i.hasNext();){
         String n = (String) i.next();
         ParamValues p = getParamValues(allValues,n);
         if ( p == null ){
            System.out.println("ExpFunc: no param matching" + n);
            System.exit(0);
         }
         values.add(p);
      }
      List permutations = allPermutations(values);
      int count = 0;
      List trials = getRunTrials(experiment);
      System.out.println("NumTrials= " + trials.size());
      List allCollections = new ArrayList();
      for (Iterator i = permutations.iterator(); i.hasNext();){
         Object[] permutation = (Object[]) i.next();
         List matches = matches(permutation,values,trials);
         if ( matches.size() > 0 ) {
            count++;
            // System.out.print(count + ": " + matches.size() + " ");
            // print(permutation,values);
            SearchCollection collection = new SearchCollection(permutation,values,matches);
            allCollections.add(collection);
            // System.out.println(" " + collection);
            // Utils.enumerateList(matches);
         }
      }
      SearchCollection[] collections = new SearchCollection[allCollections.size()];
      for (int i = allCollections.size();i-->0;)
         collections[i] = (SearchCollection)allCollections.get(i);
      return collections;
   }

    
   public static void tallyTimes (List expFileNames, List paramNamesList, int timeInc){
       System.out.println("----TallyTimes----");
       List names = new ArrayList();
       List times = new ArrayList();
       int kSize = expFileNames.size();
       int max = 0;
       for (int k = 0; k< kSize; k++){
	   String expFileName = (String) expFileNames.get(k);
	   List paramNames = (List) paramNamesList.get(k);
	   Experiment experiment = (Experiment) Utils.readObject(expFileName);
	   SearchCollection[] collections = getCollections(experiment,paramNames);
	   for (int i = 0; i < collections.length; i++){
	       double[] scores = collections[i].getScoreByTime(timeInc);
	       times.add(scores);
	       if ( scores.length > max) 
		   max = scores.length;
	       String name = experiment.getSearchName() + 
		   collections[i].getPermuteString();
	       names.add(name);
	   }
       }
       
       int size = names.size();
       for (int i = 0; i < size; i++)
	   System.out.print(names.get(i) + "  ");
       System.out.println();
       for (int i = 0; i < max; i ++){
	   System.out.print(i * timeInc + ": ");
	   for (int j = 0; j < size; j++){
	       double[] scores = (double[]) times.get(j);
	       System.out.print(Utils.doubleToString(scores[i],1) + "  ");
	   }
	   System.out.println();
       }
   }

   public static Integer timeBeforeBeatScore (Trial trial, Score score ){
      return timeBeforeBeatScore(trial.getResult(),score);
   }
   public static Integer timeBeforeBeatScore (SearchTrace trace, Score score ){
      return timeBeforeBeatScore(trace.getReports(),score);
   }
   public static Integer timeBeforeBeatScore (List reports, Score score ){
      int time = 0;
      for (Iterator j = reports.iterator(); j.hasNext();){
         SearchReport report = (SearchReport) j.next();
         Score rScore = report.getScore();
         if ( rScore != null && rScore.isBetter(score) )
            return new Integer(time);
         time = report.getTime();
      }
      return null;
   }


   
   public static Score scoreAtTime (Trial trial, int time ){
      return scoreAtTime(trial.getResult(),time);
   }
   public static Score scoreAtTime  (SearchTrace trace, int time ){
      return scoreAtTime(trace.getReports(),time);
   }
   public static Score scoreAtTime (List reports, int time ){
      SearchReport last = null;
      int lastTime = 0;
      for (Iterator j = reports.iterator(); j.hasNext();){
         SearchReport report = (SearchReport) j.next();
         if ( report.getTime() <= time &&
              (last == null || report.getTime() > lastTime) ){
            lastTime = report.getTime();
            last = report;
         }
      }
      if ( last == null ) return null;
      return last.getScore();
   }
   
   public static Integer timeBeforeMatchScore (Trial trial, Score score ){
      return timeBeforeMatchScore(trial.getResult(),score);
   }
   public static Integer timeBeforeMatchScore (SearchTrace trace, Score score ){
      return timeBeforeMatchScore(trace.getReports(),score);
   }
   public static Integer timeBeforeMatchScore (List reports, Score score ){
      int time = 0;
      for (Iterator j = reports.iterator(); j.hasNext();){
         SearchReport report = (SearchReport) j.next();
         Score rScore = report.getScore();
         if ( rScore != null && !score.isBetter(rScore) )
            return new Integer(time);
         time = report.getTime();
      }
      return null;
   }

   public static Score bestScore (SearchTrace trace ){
      List reports = trace.getReports();
      Score best = null;
      for (Iterator j = reports.iterator(); j.hasNext();){
         SearchReport report = (SearchReport) j.next();
         Score rScore = report.getScore();
         if ( rScore != null && (best == null || rScore.isBetter(best) ) )
            best = rScore;
      }
      return best;
   }
   
   public static void oldTallyTimes (String expFileName, List paramNames, int timeInc){
       Experiment experiment = (Experiment) Utils.readObject(expFileName);
       SearchCollection[] collections = getCollections(experiment,paramNames);
       List times = new ArrayList(collections.length);
       int max = 0;
       for (int i = 0; i < collections.length; i++){
	   double[] scores = collections[i].getScoreByTime(timeInc);
	   times.add(scores);
	   if ( scores.length > max) 
	       max = scores.length;
       }
       for (int i = 0; i < collections.length; i++)
	   System.out.print(collections[i].getPermuteString() + "  ");
       System.out.println();
       for (int i = 0; i < max; i ++){
	   System.out.print(i * timeInc + ": ");
	   for (int j = 0; j < collections.length; j++){
	       double[] scores = (double[]) times.get(j);
	       System.out.print(Utils.doubleToString(scores[i],1) + "  ");
	   }
	   System.out.println();
       }
   }

   public static void tally (String expFileName, List paramNames){
      Experiment experiment = (Experiment) Utils.readObject(expFileName);
      SearchCollection[] collections = getCollections(experiment,paramNames);
      Arrays.sort(collections,new ScoreComparator());
      for (int i = collections.length; i-->0;)
         System.out.println((collections.length - i) + ": " + collections[i]);
   }

   public static void run (String file){ run(file,1); }
   public static void run (String file, int num){
      Experiment experiment = (Experiment) Utils.readObject(file);
      int count = 0;
      int runs = 0;
      int index = 0;
      List sets = experiment.getTrialSets();
      while ( runs < num && index < sets.size() ) {
         String name = (String) sets.get(index);
         TrialSet set = (TrialSet) Utils.readObject(name);
         if ( set.allRun() ) 
            index++;
         else {
            set.runNext();
            runs++;
         }
         count++;
      }
      if ( index == sets.size() )
         System.out.println("Experiment " + file + " is done");
      else
         System.out.println("Experiment " + file + ": working on set #"
                            + index + " of " + sets.size());
   }

   public static void runTrialSet (String file, int num){
      TrialSet set = (TrialSet) Utils.readObject(file);
      int runs = 0;
      while ( runs < num && !set.allRun() ) { 
         set.runNext();
         runs++;
      }
      if ( set.allRun() )
         System.out.println("TrialSet " + file + " is done");
      else
         System.out.println("TrialSet report: ran " + runs + " trials, not done yet");
   }

   public static void runExperimentSupport (String file, int num){
      ExperimentSupport set = (ExperimentSupport) Utils.readObject(file);
      int runs = 0;
      while ( runs < num && !set.allRun() ) { 
         set.runNext();
         runs++;
      }
      if ( set.allRun() )
         System.out.println("ExperimentSupport " + file + " is done");
      else
         System.out.println("ExperimentSupport report: ran " + runs + " runnables, not done yet");
   }

   
   public static void main (String[] args ){
	if ( args.length > 0 ){
           if ( args[0].equals("-make") ){
              System.out.println("UsageNote: -make -search <searchname> -dir <directory/> -name <experiment name>  -timeLimit <timeLimit> -cycle <cycleSize> -problem <header for all problem names> -num <num files> (e.g, looks for hugs0, hugs1, hugs2..");
              System.out.println("Example: java hugs.logging.ExpFunc -make -search tabu -values tabu1 -dir experiments/prot/ -name expProtein -problem problems/protein -num 4 -timeLimit 300 -cycle 20 -trialsPerSet 10");
              make123(args); 
           }
	    else if ( args[0].equals("-run") )
		run(args[1]);
	}
    }
}
