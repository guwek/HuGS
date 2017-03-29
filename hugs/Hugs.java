package hugs;

/* notes

   -- maybe need to remove timer for restore to get rid of swing stuff
   -- get rid of visualization and repaint
*/


import javax.swing.*;
import hugs.search.*;
import hugs.support.*;
import hugs.logging.*;
import hugs.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;
import java.io.*;

public class Hugs  
{

   public static boolean autoStart = true; // does getBest and start search.
    public static boolean useSearchMgr = true;
   public static boolean useGUI = true; 
   public static boolean demo = false;
   public static boolean FAST_MODE = true; // for experiments (don't ask)
   public static int RESTORE_INTERVAL = 10;
   public static String RESTORE_FILENAME = "restore";
   public static int DEFAULT_CYCLE_TIME = 1;
   public static String saveBest = null;

    
   public static String instanceName = ""; // used for offline experiments, mostly.
   protected Visualization makeVisualization () {
	// return new HugsVisualization ( Problem.X_NUM_NODES, Problem.Y_NUM_NODES,600,300);
	return null;
    }

   public MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
	// return new HugsMoveGenerator(mobilities,solution);
      return null;
   }

   public SearchAdjuster makeSearchAdjuster () {
      return null;
   }

   protected Problem makeProblem (String name) {
      return null;
   }

   public static boolean showChanges = true;
   private static SearchMgr[] searchMgrs = null;

   private static Score score = null;
   private static Score oldScore = null;
    private static SearchAdjuster searchAdjuster;

   private static java.util.List inputs = new ArrayList();
   public static IntParameter testTime = new IntParameter("testTime",0);
   private static IntParameter seed = new IntParameter("seed",0);
   public static IntParameter numSearchMgrs = new IntParameter("numSearchMgrs",1); 
   private static StringParameter searchNames =
      new StringParameter("searchNames","tabu steepest greedy tabuNgreedy tabuNsteep");
   private static StringParameter logName = new StringParameter("logName");
   private static StringParameter restoreInfo = new StringParameter("restoreInfo");
   private static StringParameter problemFileName =
      new StringParameter("problemFileName","");
   private static StringParameter precomputationFileName =
      new StringParameter("precompuationFileName","");
   private static StringParameter userName = new StringParameter("userName","");
   private static Trial comparison = null;
   {
      inputs.add(problemFileName);
      inputs.add(precomputationFileName);
      inputs.add(userName);
      inputs.add(numSearchMgrs);
      inputs.add(searchNames);
      inputs.add(logName);
      inputs.add(seed);
      inputs.add(restoreInfo);
      Utils.setSeed(seed.value);
      inputs.add(testTime);

   }

   private HugsGUI gui;
   
   private static Restore restore;
   public static void updateRestore () {
      updateRestore(Hugs.THIS.getBestFound());
      
   }
   
   public static void updateRestore (Solution best) {      
      if ( restore == null ) return;
      int time = Utils.getSeconds() - THIS.gui.getTimerStart();
      if ( time < RESTORE_INTERVAL ) return;
      restore.best = best;
      if ( testTime.value > 0 )
         restore.timeLeft = testTime.value - time;
      Utils.writeObject(restore,RESTORE_FILENAME);
   }

   private static void startRestore () {
      Solution best = Hugs.THIS.getBestFound();
      restore = new Restore(testTime.value,best,restoreInfo.value);
      updateRestore();
      ActionListener listen =   new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            updateRestore();
         }
      };
      javax.swing.Timer timer = new javax.swing.Timer (RESTORE_INTERVAL*1000,listen);
      timer.start();
   }
   
   // maybe should add: numSearchMgr, search algorithms
   private static Logger logger;
   private static void startLog () {
      if ( logName.value == null ||
           logName.value.equals("") ) return;
      Solution best = Hugs.THIS.getBestFound();
      Score startScore = best == null ? null : best.getScore();
      logger = new Logger(logName.value,startScore,inputs);
      if ( searchMgrs != null )
         for (int i = searchMgrs.length; i-->0;){
            searchMgrs[i].setLogger(logger);
         }
      saveLog();
   }

   public static void saveLog () { if ( logger != null ) logger.save(); }

   public static void closeLog () {
      if ( logger != null ) {
         logger.close(); 
         logger.save();
      }
   }

   public static Logger getLogger () { return logger; }
   public static void addMenuEvent (String description) {
      addEvent(new MenuEvent(Hugs.THIS.getCurrentSolution(),description));
   }
   public static void addEvent (LogEvent e) {
      if ( logger != null ){
         logger.add(e);
      }
   }

   
   public static void updateBackground() {
      boolean anyForeground = false;
      if ( searchMgrs != null ) {
	  for (int i = searchMgrs.length; i-->0;)
	      if ( !anyForeground && !searchMgrs[i].isBackground() &&
		   searchMgrs[i].isRunning() )
		  anyForeground = true;
	  for (int i = searchMgrs.length; i-->0;)
	      searchMgrs[i].updateBackground(anyForeground);
      }
   }
   
   public static void setScore(Score score){
      String s = null;
      if ( oldScore == null )
         s = "Score: " + score;
      else 
         s = score.comparisonString(oldScore);
      oldScore = score;
      Hugs.score = score;
      if (THIS != null && THIS.gui != null) 
         THIS.gui.setScoreText(s);
   }

   
   // private Visualization visualization;
   public Visualization getVisualization () {
      if ( gui == null ) return null;
      return gui.getVisualization();
   }

   public void repaint(){
      if ( gui != null )
         gui.repaint();
   }

    public SearchAdjuster getSearchAdjuster () {
	return searchAdjuster;
    }

   protected void actionGetBest () {
      Hugs.THIS.restoreBest();
   }
   
   protected void actionUndo () {
      Hugs.THIS.undo();
      gui.repaint();
   };
   
   protected void actionRedo(){
      Hugs.THIS.redo();
      gui.repaint();
   };

   protected void actionSetMobility (int value) {
      boolean selected[] = gui.getSelected();
      for (int i = 0; i < selected.length; i++)
         if ( selected[i] )
            getMobilities().setMobility(i,value);
      gui.repaint();
   }
   
   protected void actionHigh () { actionSetMobility(Mobilities.HIGH); }
   protected void actionMed (){actionSetMobility(Mobilities.MED); }
   protected void actionLow (){actionSetMobility(Mobilities.LOW); }
   
   protected void actionMarkSelected ( boolean value ){
      boolean selected[] = gui.getSelected();
      for (int i = 0; i < selected.length; i++)
         if ( selected[i] )
            getMarked()[i] = value;
      gui.repaint();
   }

   protected void actionMarkAll ( boolean value ){
      boolean marked[] = getMarked();
      for (int i = 0; i < marked.length; i++)
         marked[i] = value;
      gui.repaint();
   }

   protected void setAll (int value) {
      boolean selected[] = gui.getSelected();
      boolean marked[] = getMarked();
      for (int i = 0; i < selected.length; i++)
         if (!marked[i] )
            getMobilities().setMobility(i,value);
      gui.repaint();
   }


   ///////////////////////////////////////////////////////////
   // over-ride next four functions to make
   // human-readable i/o
   

   public Solution readSolution (String name) {
      return (Solution) Utils.readObject(name);
   }

   public void writeSolution (Solution solution, String name) {
      Utils.writeObject(solution,name);
   }

   public Problem readProblem (String name){
      return (Problem) Utils.readObject(name);
   }

   public void writeProblem (Problem problem, String name) {
      Utils.writeObject(problem,name);
   }

   /////////////////////////////////////////////

   public void actionSaveProblem (String name){
      Problem problem= getProblem();
      writeProblem(problem,name);
   }

   public void actionSaveSolution(String name, Solution solution){
      writeSolution(solution,name);
   }

   public void actionSaveSolution(String name){
      actionSaveSolution(name,getCurrentSolution());
   }
   

   protected void actionLoadSolution(String name){
      Solution s = readSolution(name);
      if (s != null) 
         updateSolution(s);
      gui.repaint();
   }

   public void actionLoadProblem(String name){
      System.out.println("loading " + name);
      Problem problem = readProblem(name);
      if ( problem != null )
         setProblem(problem,name);
   }

   


   
   protected void endSession () {
      if ( searchMgrs != null )
         for (int i = searchMgrs.length; i-->0;)
            searchMgrs[i].doStop();
      closeLog();
   }
   
   protected void actionCloseApplication () {
      endSession();
      System.exit(1);
   }
   



   public void actionLoadRanked (String name){
      Precompute precompute = Precompute.load(name);
      if ( precompute != null )
         precomputationFileName.value = name;
      Solution[] ranked = precompute.getRanked();
      for (int i = 0; i < ranked.length; i++){
         gui.addRanked(ranked[i]);
         if ( Hugs.THIS != null )
            Hugs.THIS.updateBestFound(ranked[i]);
      }
   }


   private Problem problem;

   public Problem getProblem () { return problem; }
   
   protected void setProblem (Problem problem, String name) {
      if (problem != null){
         System.out.println("HUGS set problem " + name);
         // setProblem(problem);
         if ( gui != null )
            gui.setProblem(problem);
         initProblem(problem);
         problemFileName.value = name;
         // initProblem(problem);
      }
      if ( gui != null )
         gui.repaint();
      if ( problem != null && searchMgrs != null )
         for (int i = searchMgrs.length; i-->0;)
            searchMgrs[i].setProblem(problem);
   }

   private void initProblem (Problem problem) {
      this.problem = problem;
      if ( getVisualization() != null)
         getVisualization().setSolution(null);
      // Problem.resetSize(problem);
      mobilities = new Mobilities(problem.size());
      bestSeen = null;
      bestFound = null;
      hugsInit();
   }

   private Mobilities mobilities = null;
   public Mobilities getMobilities (){return mobilities;}
   public void updateMobilities (Mobilities mobilities) {
      if ( mobilities == null ) return;
      for (int i = 0; i < mobilities.size(); i++)
         this.mobilities.setMobility(i,mobilities.getMobility(i));
      repaint();
   }
   
   private MultiSearchThread newMulti (int num, int time) {
       java.util.List tabus = new ArrayList(num);
       for (int i = num; i-->0;)
          tabus.add(new TabuThread() );
       return new MultiSearchThread(tabus,time);
   }
   
   private java.util.List newSearchList () {
      java.util.List list = new ArrayList(3);
      StringTokenizer tokenizer = new StringTokenizer(searchNames.value);
      while ( tokenizer.hasMoreElements() )
         list.add(getSearch((String) tokenizer.nextElement()));
      return list;
   }

    // this is all very very ugly, but will be throwing otu this code soon
    public static boolean ready = false;
   public Hugs (){
      SearchMgr.active = false;
   }


   protected void init (){
      searchAdjuster = makeSearchAdjuster();
      ready = true;
      if ( useSearchMgr ) {
	  searchMgrs = new SearchMgr[numSearchMgrs.value];
	  for (int i = 0; i < numSearchMgrs.value; i++){
	      searchMgrs[i] = new SearchMgr(i,DEFAULT_CYCLE_TIME, newSearchList(),useGUI);
	  }
      }
      if ( useGUI ) {
	  Visualization visualization = makeVisualization();
	  gui = new HugsGUI(this,demo,visualization,searchMgrs,testTime.value, comparison,searchNames.value);
          visualization.setShowChanges(showChanges);
          gui.getVisualization().setHugs(this);
      }
   }

   // public static Hugs hugs;
   public static Hugs THIS;

   public int getTime () {
      int startTime = (int) Calendar.getInstance().getTime().getTime()/1000;
      return startTime;
   }
   
    private static java.util.List tallyTimesEList = new ArrayList();
    private static java.util.List tallyTimesPList = new ArrayList();
   public static void processArg (String args[], int pos) {
      if (args[pos].equals("-numSearchMgrs")){
         numSearchMgrs.value = Utils.stringToInt(args[pos+1]);
         System.out.println("num search managers = " + numSearchMgrs.value);
      }
      else if (args[pos].equals("-logName")){
         logName.value = args[pos+1];
      }
      else if (args[pos].equals("-noGUI")){
         useGUI = false;
      }
      else if (args[pos].equals("-noAutoStart")){
         autoStart = false;
      }
      else if (args[pos].equals("-autoStart")){
         autoStart = true;
      }
      else if (args[pos].equals("-noUseSearchMgr")){
         useSearchMgr = false;
      }
      else if (args[pos].equals("-useSearchMgr")){
         useSearchMgr = true;
      }
      else if (args[pos].equals("-noshowChanges")){
         showChanges = false;
      }
      else if (args[pos].equals("-showChanges")){
         showChanges = true;
      }
      else if (args[pos].equals("-CHI")) {
         // showChanges = false;
         SearchMgr.restoreMobilities = false;
         SearchMgrGUI.FONT_SIZE = 14;
         SearchMgrGUI.showOutputs = false;
         SearchMgrGUI.showInputButton = false;
         HugsGUI.showTimer = false;
         GreedyThread.name = "exhaustive";
         
         
      }
      else if (args[pos].equals("-smallDemo")) {
         SearchMgr.restoreMobilities = false;
         SearchMgrGUI.FONT_SIZE = 14;
         SearchMgrGUI.showOutputs = false;
         // SearchMgrGUI.showInputButton = false;
         // HugsGUI.showTimer = false;
         // GreedyThread.name = "exhaustive";
      }
else if (args[pos].equals("-restoreInfo")){
         restoreInfo.value = args[pos+1];
      }
      else if (args[pos].equals("-userName")){
         userName.value = args[pos+1];
      }
      else if (args[pos].equals("-restore")){
         Restore restore = (Restore) Utils.readObject(RESTORE_FILENAME);
         if ( restore.best != null ) {
            if ( !madeBoard ) makeBoard(null);
            updateSolution(restore.best);
         }
         if ( restore.timeLeft >= 0 ) {
            System.out.println("setting testTime to " + restore.timeLeft);
            testTime.value = restore.timeLeft;
         }
      }
      else if (args[pos].equals("-setName")){
         instanceName  = args[pos+1];
      }
      else if (args[pos].equals("-setSaveBest")){
         saveBest  = args[pos+1];
      }
      else if (args[pos].equals("-searchNames")){
         searchNames.value = "";
         int index = pos+1;
         while (index < args.length && !args[index].startsWith("-") )
            searchNames.value += " " + (args[index++]);
         System.out.println("searchNames = " + searchNames.value);
      }
      else if (args[pos].equals("-grayScale")){
         System.out.println("setting grayScale to true");
         Node.grayScale = true;
      }
      else if (args[pos].equals("-demo")){
         demo = true;
         numSearchMgrs.value = 1;
         HugsGUI.showTimer = false;
         HugsGUI.SCORE_WIDTH = 500;
         HugsGUI.SCORE_HEIGHT = 30;
         HugsGUI.SCORE_FONT_SIZE = 18;
         TabuThread.name = "soph.";
      }
      else if (args[pos].equals("-seed")){
         seed.value = Utils.stringToInt(args[pos+1]);
         Utils.setSeed(seed.value);
      }
      else if (args[pos].equals("-tally")){
         java.util.List paramNames = new ArrayList();
         int index = pos+2;
	 while (index < args.length && !args[index].startsWith("-") )
	     paramNames.add(args[index++]);
	 // int num = Integer.valueOf(args[pos+2]).intValue();
	 // for (int i = 0; i < num; i++){
	 // paramNames.add(args[pos+3+i]);
         // }
         System.out.println("Tally: " + paramNames);
         ExpFunc.tally(args[pos+1],paramNames);
      }
      else if (args[pos].equals("-tallyTimes")){
         java.util.List paramNames = new ArrayList();
	 int inc = Integer.valueOf(args[pos+2]).intValue();
         int index = pos+3;
	 while (index < args.length && !args[index].startsWith("-") )
	     paramNames.add(args[index++]);
         System.out.println("TallyTimes: " + paramNames + ", inc=" + inc);
	 tallyTimesEList.add(args[pos+1]);
	 tallyTimesPList.add(paramNames);
         ExpFunc.tallyTimes(tallyTimesEList,tallyTimesPList,inc);
      }
      else if (args[pos].equals("-exp")){
         System.out.println("Usage: -exp [filename of experiment] [num trials]");
         if ( !madeBoard ) {
            System.out.println("making board");
            makeBoard(null);
         }
         int num = Integer.valueOf(args[pos+2]).intValue();
         ExpFunc.run(args[pos+1],num);
      }
      else if (args[pos].equals("-runTrialSet")){
         if ( !madeBoard ) {
            System.out.println("making board");
            makeBoard(null);
         }
         System.out.println("Usage: -runTrialSet [filename of experiment] [num trials]");
         int num = Integer.valueOf(args[pos+2]).intValue();
         ExpFunc.runTrialSet(args[pos+1],num);
      }
      else if (args[pos].equals("-runSupport")){
         if ( !madeBoard ) 
            makeBoard(null);
         System.out.println("Usage: -runSupport [filename of support] [num trials]");
         int num = Integer.valueOf(args[pos+2]).intValue();
         ExpFunc.runExperimentSupport(args[pos+1],num);
      }
      else if (args[pos].equals("-size")){
         // Problem.resetSize(Integer.valueOf(args[pos+1]).intValue(),
         // Integer.valueOf(args[pos+2]).intValue());
      }
      else if (args[pos].equals("-loadProblem")){
         if ( !madeBoard ) makeBoard(null);
         THIS.actionLoadProblem(args[pos+1]);
      }
      else if (args[pos].equals("-comparison")){
         comparison = (Trial) Utils.readObject(args[pos+1]);
         if ( comparison != null ){
            PrintWriter writer = Utils.openPrintWriter("temp.comparison");
            writer.println("Trial: " + comparison);
            System.out.println("comparision: " + comparison);
            if ( comparison != null && comparison.getResult() != null )
               comparison.getResult().print(writer);
         }
      }
      else if (args[pos].equals("-loadRanked")){
         THIS.actionLoadRanked(args[pos+1]);
      }
      else if (args[pos].equals("-exit")){
         System.exit(0);
      }
      else if (args[pos].equals("-make")){
         makeBoard(null);
         madeBoard = true;
      }
      else if (args[pos].equals("-saveProblem")){
         THIS.actionSaveProblem(args[pos+1]);
      }
      else if (args[pos].equals("-save")){
         THIS.actionSaveSolution(args[pos+1]);
      }
      else if (args[pos].equals("-load") ||
               args[pos].equals("-loadSolution") ) {
         if ( !madeBoard ) makeBoard(null);
         THIS.actionLoadSolution(args[pos+1]);
      }
      else if (args[pos].equals("-generate")){
         makeBoard(null);
         System.out.println("initial solution: " + Hugs.THIS.getCurrentSolution().getScore());
         THIS.actionSaveProblem(args[pos+1]);
         System.exit(0);
      }
      else if (args[pos].equals("-testTime")){
         if ( madeBoard )
            System.out.println("WARNING: -testTime should be done early in the args list");
         testTime.value = Utils.stringToInt(args[pos+1]);
      }
      else if (args[pos].equals("-generateN")){
         System.out.println("Usage: -generateN <root fileName> <num>");
         int num = Utils.stringToInt(args[pos+2]);
         for (int i = 0; i < num; i++) {
            Utils.setSeed(i);
            makeBoard(null);
            String name = args[pos+1] + i;
            System.out.println("initial solution for " + name + " : "
                               + Hugs.THIS.getCurrentSolution().getScore());
            THIS.actionSaveProblem(name);
         }
         System.exit(0);
      }
      else if (args[pos].equals("-readPrecompute")){
	  Precompute precompute = Precompute.load(args[pos+1]);
	  Solution[] ranked = precompute.getRanked();
	  System.out.println("Precompute: " + args[pos+1]);
	  for (int i = 0; i < ranked.length; i++)
	      System.out.println(i + ":" + ranked[i]);
      }
      else if (args[pos].equals("-precompute")){
         System.out.println("Usage: -precompute <searchName> <problem> <num to keep> <seconds to search> <filename> ");
         if ( !madeBoard ) {
            System.out.println("making board");
            makeBoard(null);
         }
         String searchName = args[pos+1];
         String problem = args[pos+2];
         int keep = Utils.stringToInt(args[pos+3]);
         int time = Utils.stringToInt(args[pos+4]);
         String fileName = args[pos+5];
         System.out.println("fileName= " + fileName);
         THIS.actionLoadProblem(problem);
         Precompute p = new Precompute(searchName,keep,fileName);
         p.run(Hugs.THIS.getProblem(),
               Hugs.THIS.getCurrentSolution(),Hugs.THIS.getMobilities(),time);
         p.save();
      }
   }


   public  static void makeBoard (String problemName){
      System.out.println("making board with " + problemName);
      if ( THIS == null || THIS.problem == null || problemName == null) {
         if (problemName == null)
            Hugs.THIS.problem = Hugs.THIS.makeProblem(null);
         else Hugs.THIS.problem = Hugs.THIS.makeProblem(problemName);
         Hugs.THIS.initProblem(Hugs.THIS.problem);
         Hugs.THIS.hugsInit();
      }
      else {
         Hugs.THIS.hugsSetProblem(THIS.makeProblem(problemName));
         Hugs.THIS.initProblem(Hugs.THIS.problem);
      }
      THIS.init();
      THIS.setScore(score);
      Problem problem = Hugs.THIS.getProblem();
      Solution solution = problem.getInitSolution();
      if ( THIS.gui != null ) {
         THIS.gui.setProblem(problem);
         if ( solution != null )
            THIS.gui.getVisualization().setSolution(solution);
      }
      if ( problem != null && searchMgrs != null )
         for (int i = searchMgrs.length; i-->0;){
	      searchMgrs[i].setProblem(problem);
         }
      madeBoard = true;
   }

   private static boolean madeBoard = false;
    public static void setup(String args[])
   {
      String problemName = null;
      if ( args.length > 1 && args[0].equals("-benchmark"))
         problemName = args[1];
      for (int i = 0; i < args.length; i++)
         processArg(args,i);
      if ( !madeBoard) 
         makeBoard(problemName);
      if ( THIS != null && THIS.gui != null &&
           THIS.gui.getVisualization() != null ) {
         THIS.startRestore();
         THIS.gui.getVisualization().clearJustChanged();
         THIS.repaint();
         THIS.startLog();
         System.out.println("FAST MODE = " + Hugs.THIS.FAST_MODE);
         System.out.println("setting active to true");
         SearchMgr.active = true;
         if ( autoStart ) {
            Hugs.THIS.actionGetBest();
            if ( Hugs.THIS.searchMgrs.length > 0 )
               Hugs.THIS.searchMgrs[0].engageSearch();
         }
      }
   }

   public static SearchThread getSearch (String name){
      if ( name == null ) return null;
      if ( name.equals("tabu") ) return new TabuThread();
      else if ( name.equals("tabuPrecompute") ) return new TabuPrecompute();
      else if ( name.equals("steepest") ) return new SteepThread();
      else if ( name.equals("greedy") ) return new GreedyThread();
      else if ( name.equals("tabuNgreedy") ) return new TabuNGreedyThread();
      else if ( name.equals("tabuNsteep") ) return new TabuNSteepThread();
      else if ( name.equals("tabuNgreedyPrecompute") )
         return new TabuNGreedyPrecompute();
      // else if ( name.equals("tabuNsteepPrecompute") ) return new TabuNSteepPrecompute();
      else System.out.println("UNKNOWN searchtype in Hugs.getSearch: " +
                              name);
      System.exit(0);
      return null;
   }
   
    public static void main(String args[]) {
	Hugs.THIS = new Hugs();
	setup(args);
    }

   ////////////////// from Hugs //////////////////////////////

   private Vector solutionList = new Vector(100);
   private Vector solutionStack = new Vector();
   private Vector redoStack = new Vector();

   private static DefaultListModel historyList = new DefaultListModel();
   public DefaultListModel getHistoryList () { return historyList; }
   private static ArrayList historySolutions = new ArrayList();

   public void adoptHistorySolution (int index) {
      adoptNewSolution((Solution)historySolutions.get(index));
      updateStuff();
   }
   
   private void adoptNewSolution (Solution s) {
      adoptNewSolution(s,true);
   }
   private void adoptNewSolution (Solution s, boolean keep){
      if ( Hugs.THIS != null && Hugs.THIS.gui != null && Hugs.ready == true)
         Hugs.THIS.getVisualization().setSolution(s);
      if ( keep )
         solutionStack.addElement(s);
   }

   private void popSolutionStack(){
      int size = solutionStack.size();
      if (size >= 2){
         Solution old = solutionStack.isEmpty() ? null :
            ((Solution) solutionStack.lastElement());
         solutionStack.removeElement(old);         
         Solution s = solutionStack.isEmpty() ? null :
            ((Solution) solutionStack.lastElement());
         if ( Hugs.THIS != null )
            Hugs.THIS.getVisualization().setSolution(s);
      }
   }
   
   public void hugsInit (){
      Node.initNodes();
      currentSolutionMenuNum = 0;
      solutionList = new Vector(100);
      solutionStack = new Vector();
      redoStack = new Vector();
      historyList.clear();
      historySolutions.clear();

      
      // mobilities = new Mobilities(problem.size());
      marked = new boolean[problem.size()];
      Solution initSolution = problem.getInitSolution();

      if ( Hugs.THIS != null && Hugs.THIS.getVisualization() != null)
         Hugs.THIS.getVisualization().setSolution(null);
      adoptNewSolution(initSolution);
      initSolution.computeScore();
      Solution s = ((Solution)solutionStack.lastElement());
      updateSolution(s);

   }
   

   public static Solution getCurrentSolution(){
      return (Solution) Hugs.THIS.solutionStack.lastElement();
   }
   
   private Solution bestSeen;
   private Solution bestFound;
   
   private synchronized void updateBestSeen (Solution s){
      if ( bestSeen == null || s.getScore().isBetter(bestSeen.getScore()) )
         bestSeen = s;
   }
   public Solution getBestSeen () { return bestSeen;}
   
   public synchronized void updateBestFound (Solution s){
      if ( bestFound == null ||
           s.getScore().isBetter(bestFound.getScore()) ) {
         bestFound = s;
         Hugs.THIS.addEvent(new BestFoundEvent(s));
         Hugs.THIS.updateRestore(s);
         if ( saveBest != null )
            writeSolution(s,saveBest);

      }
   }
   
   public Solution getBestFound () { return bestFound;}

   public void restoreBest (){
      if ( bestFound != null )
         adoptNewSolution(bestFound);
      updateStuff();
      Hugs.THIS.repaint();
   }
   
   private static void updateStuff () {
      updateStuff(getCurrentSolution());
   }
   
   private static void updateStuff (Solution solution) {
      if ( solution != null ){
         Score score = solution.computeScore(Hugs.THIS.searchAdjuster,Hugs.THIS.getMarkedList());
	 System.out.println("@@ hugs search adjuster " + THIS.searchAdjuster);
	  Hugs.setScore(score);
      }
   }
   
   public void undo(){
      if (solutionStack.size() <= 1) return;
      Solution s = (Solution) solutionStack.lastElement();
      popSolutionStack();
      redoStack.addElement(s);
      updateStuff();
   }
   
   public void redo(){
      if (redoStack.size() < 1) return;
      Solution s = (Solution) redoStack.lastElement();
      redoStack.removeElement(s);
      adoptNewSolution(s);
      updateStuff();
   }
   

   private static int currentSolutionMenuNum = 0;
   

   
   private static void updateHistory (Solution s){
      s.computeScore();
      String name = "Sol"+ Hugs.THIS.solutionList.size() + " [" + s.getScore() + "]";
      historyList.addElement(name);
      historySolutions.add(s);
   }
   

   public void manualMove (Move m){
      updateSolution(Utils.doMove(m,getCurrentSolution()));
      Hugs.THIS.addEvent(new MoveEvent(m,getCurrentSolution()));
   }

   public static void updateSolution(Solution s) {
      updateSolution(s,true);
   }
   public static void updateSolution(Solution s, boolean keep){
      Hugs.THIS.adoptNewSolution(s, keep);
      if ( keep )
         Hugs.THIS.solutionList.addElement(s);
      updateStuff(s);
      Hugs.THIS.updateBestFound(s);
      Hugs.THIS.updateBestSeen(s);
      if ( keep ) 
         updateHistory(s);
  }

      private boolean[] marked = null;
   public boolean[] getMarked () {return marked;}
   public java.util.List getMarkedList () {
      ArrayList markedList = new ArrayList();
      Node[] nodes = problem.getNodes();
      for (int i = marked.length; i-->0;)
         if ( marked[i] )
            markedList.add(nodes[i]);
      return markedList;
   }

   public void hugsSetProblem( Problem problem) {
      bestSeen = null;
      bestFound = null;
      hugsInit();
   }

   public static boolean ready(){
      return Hugs.THIS != null;
   }

}


