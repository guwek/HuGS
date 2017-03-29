package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class Abacus {

   
      
   private static final int NUM_BENCHMARKS = 10;

   private static String abacusDirectory = "/home/lesh/hugsExperiments/";
   private static String workDirectory = "/homes/lesh/java/hugsExperiments/";
   private static String wispDirectory = "/java/hugsExperiments/";
   private static String float9Directory = "/lesh/java/hugsExperiments/";
   private static String baseDirectory = abacusDirectory;
   private static int time = 600;
   private static int cycle = 30;

   private static int minMemory = 3;
   private static int maxMemory = 8;
   private static double minNoise = 0;
   private static double maxNoise = 0.15;
   private static double minMinD = 0.2;
   private static double maxMinD = 0.45;

   private static String slamRegularArgs = " ";
   private static String[] slamRandomArgs = {" -doTwos ", " -doSeeks ", " -doRotates " , " -doRotates " , " -doRotates " };
   private static double slamProb = 0.15;
   private static int slamNum = 35;
   private static int slamTime = 3600 * 15; // 15 hr
   private static int slamCycle = 180;

   private static String randomDouble (double min, double max) {
      double delta = max - min;
      return Utils.doubleToString(Utils.randomDouble() * delta + min,3);
   }
   private static String randomInt (int min, int max) {
      int delta = max - min + 1;
      return "" + (Utils.randomInt(delta) + min);
   }

   public static void processParams (String args[]) {
      // time
      String timeString = Utils.getValue("-time",args);
      if ( timeString != null )
         time = Utils.stringToInt(timeString);
      // slamTime
      String slamTimeString = Utils.getValue("-slamTime",args);
      if ( slamTimeString != null )
         slamTime = Utils.stringToInt(slamTimeString);
      // cycle
      String cycleString = Utils.getValue("-cycle",args);
      if ( cycleString != null )
         cycle = Utils.stringToInt(cycleString);
      // slamCycle
      String slamCycleString = Utils.getValue("-slamCycle",args);
      if ( slamCycleString != null )
         slamCycle = Utils.stringToInt(slamCycleString);
      // slamNum
      String slamNumString = Utils.getValue("-slamNum",args);
      if ( slamNumString != null )
         slamNum = Utils.stringToInt(slamNumString);
      // minNoise
      String minNoiseString = Utils.getValue("-minNoise",args);
      if ( minNoiseString != null )
         minNoise = Utils.stringToDouble(minNoiseString);
      // maxNoise
      String maxNoiseString = Utils.getValue("-maxNoise",args);
      if ( maxNoiseString != null )
         maxNoise = Utils.stringToDouble(maxNoiseString);
      // minMinD
      String minMinDString = Utils.getValue("-minMinD",args);
      if ( minMinDString != null )
         minMinD = Utils.stringToDouble(minMinDString);
      // maxMinD
      String maxMinDString = Utils.getValue("-maxMinD",args);
      if ( maxMinDString != null )
         maxMinD = Utils.stringToDouble(maxMinDString);
      // minMemory
      String minMemoryString = Utils.getValue("-minMemory",args);
      if ( minMemoryString != null )
         minMemory = Utils.stringToInt(minMemoryString);
      // maxMemory
      String maxMemoryString = Utils.getValue("-maxMemory",args);
      if ( maxMemoryString != null )
         maxMemory = Utils.stringToInt(maxMemoryString);



      
   }
   
   public static void abacusProcessArg (String args[], int pos) {
      if (args[pos].equals("-work")) {
         baseDirectory = workDirectory;
      }
      else if (args[pos].equals("-float9")) {
         baseDirectory = float9Directory;
      }
      else if (args[pos].equals("-wisp")) {
         baseDirectory = wispDirectory;
      }
      else if (args[pos].equals("-makeProtein")) {
         String name = args[pos+1];
         String arguments = Utils.getValue("-args",args);

         String dir = baseDirectory + "protein/";
         String argString = "-search tabu -values tabuP " +
            " -dir " + dir + " -name " + name +
            " -problem " + baseDirectory + "problems/protein -num " + NUM_BENCHMARKS +
            " -timeLimit " + time + " -cycle " + cycle + " -numSets " + NUM_BENCHMARKS;
         System.out.println("argString : " + argString);
         ExpFunc.make123(Utils.stringToArray(argString));
         List script = new ArrayList();

         for (int i =0; i < NUM_BENCHMARKS; i++)
            script.add("java hugs.apps.protein.Protein -noGUI -noUseSearchMgr " + arguments + " -runTrialSet " + baseDirectory + "protein/" + name + "Trial" + i + " 1" + " -exit");
         String scriptFilename = baseDirectory + "protein/" + name + ".script";
         String tallyFilename = baseDirectory + "protein/" + name + ".tally";
         Utils.makeFile(scriptFilename,script);
         Utils.makeFile(tallyFilename,("java hugs.Hugs -noGUI -noUseSearchMgr -tallyTimes " + baseDirectory + "protein/" + name + " 60 PROBLEM -tallyTimes " + baseDirectory + "protein/" + name + " 60 -exit"));
         System.out.println("\n----------------------------------------\n");
         System.out.println("Reminder, put node list in " + baseDirectory +"nodes");
         System.out.println();
         System.out.println("TO RUN: /usr/local/bin/batchrun " + scriptFilename + " " + baseDirectory +"nodes");
         System.out.println("TO TALLY: " + tallyFilename);
         
      }
      else if (args[pos].equals("-slamProtein")) {
         String testName = args[pos+1];
         String problem = args[pos+2];
         slam(testName,problem,"protein","hugs.apps.protein.Protein");
      }
      else if (args[pos].equals("-slamJobshop")) {
         String testName = args[pos+1];
         String problem = args[pos+2];
         String command = "hugs.apps.jobshop.Jobshop -setDefault " +
            baseDirectory + "problems/prob4x4.jsp ";
         slam(testName,problem,"jobshop",command);
      }
      
   }

   private static void slam (String testName, String problem, String type, String function) {
         List script = new ArrayList();
         String tally = "java hugs.Hugs -noGUI -noUseSearchMgr ";
         for (int i = 0; i < slamNum; i++) {
            String arguments = " " + slamRegularArgs;
            for (int r = 0; r < slamRandomArgs.length; r++)
               if ( Utils.randomDouble() <= slamProb )
                  arguments += slamRandomArgs[r];
            String dir = baseDirectory + type + "/";
            String name = testName + "." +  i;
            
            String values = "-minD " + randomDouble(minMinD,maxMinD) +
               " -noise " + randomDouble(minNoise,maxNoise) +
               " -memory " + randomInt(minMemory,maxMemory) + " ";
            String argString = "-search tabu -values specify " + values + 
               " -dir " + dir + " -name " + name +
               " -problem " + baseDirectory + "problems/" + problem + " -num " + 0 +
               " -timeLimit " + slamTime + " -cycle " + slamCycle;
            System.out.println("run# " + i + " argString : " + argString);
            ExpFunc.make123(Utils.stringToArray(argString));
            String iname = "run#" + i + " " + arguments + values;
            String silly = " .";
            iname = iname.replace(silly.charAt(0),silly.charAt(1));
            System.out.println("iname = " + iname);
            String saveName = baseDirectory + "solutions/" + name + ".sol";
            script.add("java " + function + " -noGUI -noUseSearchMgr -setSaveBest " + saveName + " -setName " + iname + " " + arguments + " -runTrialSet " + baseDirectory + type + "/" + name + "Trial" + 0 + " 1 " + " -exit");
            tally += " -tallyTimes " + baseDirectory + type + "/" + name + " 60 PROBLEM ";
         }

         String scriptFilename = baseDirectory + type + "/" + testName + ".script";
         String tallyFilename = baseDirectory + type + "/" + testName + ".tally";
         Utils.makeFile(scriptFilename,script);
         Utils.makeFile(tallyFilename,tally);
         System.out.println("\n----------------------------------------\n");
         System.out.println("Reminder, put node list in " + baseDirectory +"nodes");
         System.out.println();
         System.out.println("TO RUN: /usr/local/bin/batchrun " + scriptFilename + " " + baseDirectory +"nodes");
         System.out.println("TO TALLY: " + tallyFilename);
         
   }
   
   public static void main (String[] args ){
      processParams(args);
      for (int i = 0; i < args.length; i++)
         abacusProcessArg(args,i);
   }

}

