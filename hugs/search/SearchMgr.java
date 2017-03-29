package hugs.search;

import hugs.*;
import hugs.utils.*;
import hugs.support.*;
import java.util.*;
import hugs.logging.*;

public class SearchMgr {

   public static boolean active = true; // hack for experiments
   public static boolean restoreMobilities = true;
      
   ///    private int MESSAGE_WIDTH = 600;
    private java.util.List searches;
    private SearchMgrThread searcher;
    private int cycle;
    private int id;
    private int startTime = 0;
    private Score startScore = null;
    private int lastBestTime = 0;
    private Score lastBestScore = null;
    private Problem problem = null;
    private int expTime = 0;
    private java.util.List inputParams;
    private java.util.List inputs = new ArrayList();
   private Mobilities originalMobilities = null;
   private Solution originalSolution;
   private boolean unseenBest = false;
   private boolean unseenGlobalBest = false;
   private boolean running = false;

   public boolean  getUnseenBest () { return unseenBest; }
   public void resetUnseenBest () { unseenBest = false; }

   public void setSearch (int index) {setSearch((SearchThread) searches.get(index));}
   
   private void addInput (ParamWidget param) {
      if ( gui != null )
         gui.addInput(param);
   }
   
   public void setSearch (SearchThread search) {
      searcher.setSearch(search);
      if ( gui != null )
         gui.setSearch(search);
   }
   

   private Logger logger;
   public void setLogger (Logger logger ){ this.logger = logger; }
   
   public void setProblem (Problem problem) {
	this.problem = problem;
        for (Iterator i = searches.iterator(); i.hasNext();)
           ((SearchThread) i.next()).setProblem(problem);
        searcher.setProblem(problem);
    }

   public boolean useBest () {
      if ( gui == null ) return false;
      return gui.useBest();
   }
                            
   private Solution getStartingSolution () {
      return  useBest() ?
         Hugs.THIS.getBestFound() :
         Hugs.THIS.getCurrentSolution();
   }
   
   public void runExperiment (int time) {
      Solution solution = getStartingSolution();
      startTrace(solution);
      searcher.search.initSearch(solution, Hugs.THIS.getMobilities(),Hugs.THIS.getMarkedList());
      expTime = time;
      searcher.setExperiment(true);
      searcher.startSearch(solution,Hugs.THIS.getMobilities(),Hugs.THIS.getMarkedList());
      
      while ( !searcher.halt ){
         try { Thread.currentThread().sleep(cycle); }
         catch (InterruptedException e) { System.err.println("Sleep Interrupted");};
      }
      System.out.println("done with runExperiment");
   }
   
   private void updateInputs (Solution s){
      
      for (Iterator i = inputs.iterator(); i.hasNext();)
         ((ParamWidget) i.next()).setParam();
      SearchAdjuster a = searcher.search.getSearchAdjuster();
      if ( a != null )
         a.precompute(s);

   }

    public void initSearch (Solution solution, Mobilities p, java.util.List marked) {
         unseenBest = false;
         unseenGlobalBest = false;
         startTime = (int) Calendar.getInstance().getTime().getTime()/1000;//dethread
         lastBestScore = solution.computeScore(searcher.search.getSearchAdjuster(),marked);
         startScore = lastBestScore;
         originalMobilities = (Mobilities) p.clone();
         originalSolution = solution.copy();

    }
   private SearchTrace searchTrace;
   private void startTrace (Solution s){
      searchTrace = new SearchTrace(Hugs.THIS.getProblem(),
                                    s, 
                                    Hugs.THIS.getMobilities(),
                                    searcher.search.getInputs(),
                                    searcher.search);
   }
   private void logTrace () {
      if (logger != null && searchTrace != null) {
         updateTrace();
         logger.add(searchTrace);
      }
   }
   
   public SearchTrace getSearchTrace (){ return searchTrace;}
   public void updateTrace () {
      if ( searchTrace == null ) return;
      int time = ((int) Calendar.getInstance().getTime().getTime()/1000)
         -startTime;
      Solution s = searcher.search.getBestSolution();
      Score best = s == null ? null : s.getScore();
      java.util.List outputs = searcher.search.getOutputs();
      if ( s != null )
         searchTrace.addReport(new SearchReport(time,best,outputs));
      System.out.println(Hugs.instanceName + " update@" + time + ": " + best + " " + outputs);
      if ( searcher.isExperiment && time >= expTime ) {
         searcher.interrupt();
         searcher.stopSearch();
      }
   }
    public void updateSearchMessage () { updateSearchMessage(null); }
    public void updateSearchMessage (String message) {
       Solution best = searcher.search.getBestSolution();
       int time = (int) Calendar.getInstance().getTime().getTime()/1000;
       if ( best != null && best.getScore().isBetter(lastBestScore,searcher.search.getSearchAdjuster()) ){
          unseenBest = true;
          unseenGlobalBest = (best.getScore().isBetter(Hugs.THIS.getBestFound().getScore()));
          lastBestTime = time;
          lastBestScore = best.getScore();
          Hugs.THIS.updateBestFound(best);
       }
       java.util.List outputs = searcher.search.getOutputs();
       if ( gui != null )
          gui.updateSearchMessage(message,best,time,outputs,startTime,lastBestTime,startScore);
       updateNewBest();
       /*
       String line1 = "time= " + (time - startTime) + 
          ", sinceBest= " + (time - lastBestTime) +
          ", best: " // + ( best == null ? " null" : (" " + best.getScore()) );
          + (best == null ? "--" : best.getScore().comparisonString(startScore));
       String line2 = "Outputs: ";
       int count = 0;
       if ( outputs != null )
          for (Iterator i = outputs.iterator(); i.hasNext();)
             line2 = line2 + (count++ > 0 ? ", " : "") + (Parameter) i.next();
       outputPanel.setText(line1,0);
       outputPanel.setText(line2,1);
       outputPanel.setText(("Message:" + message),2);
       outputPanel.repaint();
       */
    }


   private boolean stepState = true;

   public void actionGetInputs () {
      if ( gui != null )
         gui.actionGetInputs(id,searcher.search.getSearchName());
   }
   
   public void actionGetCurrent () { actionGetCurrent(false); }
   protected void actionGetCurrent (boolean keep) {
      if (mode == MODE_STEP && stepState ){
	  if ( searcher != null ) 
	      searcher.halt = false;
         stepState = false;
      }
      else {
         if ( mode == MODE_STEP ) stepState = true;
         updateSolution(searcher.search.getCurrentSolution(),keep);
         // System.out.println("updating mobilities: " + searcher.search.getCurrentMobilities());
         if ( restoreMobilities) 
            Hugs.THIS.updateMobilities(searcher.search.getCurrentMobilities());
         updateSearchMessage();
         Hugs.THIS.repaint();
      }
   }

   public void engageSearch() {
      originalMobilities = (Mobilities) Hugs.THIS.getMobilities().clone();
      actionStart(originalMobilities);
   }
   
   protected void actionStart (Mobilities mobilities) {
      if ( !active ) return;
      lastBestTime = startTime;
      running = true;
      updateSearchColor();
      if (!searcher.halt) {
         Solution restart = getStartingSolution();
         updateInputs(restart);         
         searcher.restart = restart;
         logTrace();
         startTrace(restart);
      }
      else {
         Solution solution= getStartingSolution();
         updateInputs(solution);
         startTrace(solution);
         searcher.startSearch(solution,mobilities,Hugs.THIS.getMarkedList());
         updateEnabled();
         Hugs.THIS.updateBackground();
      }
   }
   
   protected void actionStopSearch (boolean update){
      running = false;
      updateSearchColor();
      stepState = true;
      searcher.interrupt();
      searcher.stopSearch();
      if ( update ) {
         updateSolution(searcher.search.getBestSolution());
         restoreMobilities();
      }
      logTrace();
      updateSearchMessage("Done with search");
      updateEnabled();
      Hugs.THIS.updateBackground();
   }

   private void updateSearchColor () {
      if ( gui != null )
         gui.updateSearchColor(running);
   }
   
   public void restoreMobilities () {
      if ( restoreMobilities ) 
         Hugs.THIS.updateMobilities(originalMobilities);
   }
   public void updateSolution (Solution solution) {
      updateSolution(solution,true);
   }
   
   public void updateSolution (Solution solution, boolean keep) {
      if (solution != null )
         Hugs.updateSolution(solution, keep);
      Hugs.getCurrentSolution().computeScore(searcher.search.getSearchAdjuster(),
                                             searcher.search.getMarked());
      Hugs.THIS.repaint();
   }

   public Solution getBestSolution () {
      return searcher.search.getBestSolution();
   }
         
   
   public SearchMgr (int cycle, SearchThread search, boolean useGUI ) {
      this(-1,cycle, Utils.makeList(search), useGUI);
   }
   public SearchMgr (int id, int cycle, SearchThread search, boolean useGUI ) {
      this(id,cycle, Utils.makeList(search),useGUI);
   }
   public SearchMgr (int cycle, java.util.List searches, boolean useGUI ) {
      this(-1,cycle,searches,useGUI);
   }

   
   private SearchMgrGUI gui;
   public SearchMgrGUI getGUI () { return gui;}
   public SearchMgr (int id, int cycle, java.util.List searches, boolean useGUI ) {
      this.id = id;
	this.cycle = cycle * 1000;
	this.searches = searches;
        boolean first = true;
	
	SearchAdjuster adjuster = ( id == 0  ) ? Hugs.THIS.getSearchAdjuster() : 
	    Hugs.THIS.makeSearchAdjuster();
        for (Iterator i = searches.iterator(); i.hasNext();) {
           SearchThread s = (SearchThread) i.next();
           s.setSearchAdjuster(adjuster);
           /// s.start(); // dethread
        }
        /*
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0, 3));
	buttons.add(buttonStart);
	buttons.add(buttonStop);
        buttons.add(buttonGetInputs);
        buttons.add(buttonGetBest);
        buttons.add(buttonGetCurrent);

        inputPanel.setLayout(new GridLayout(0, 2));
        // JScrollPane scroll = new JScrollPane(inputPanel);
        // scroll.setPreferredSize(new Dimension(150, 100));
        // add(scroll, BorderLayout.CENTER);
        // add(inputPanel);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(0, 1));
        {
           {// selecting type of search;
              String[] names = new String[searches.size()];
              int count = 0;
              for (Iterator i = searches.iterator(); i.hasNext();)
                 names[count++] = ((SearchThread) i.next()).getSearchName();
              searchList = new JComboBox(names);
              searchList.setSelectedIndex(0);
              searchList.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                       JComboBox cb = (JComboBox)e.getSource();
                       int index = cb.getSelectedIndex();
                       setSearch(index);
                    }
                 });
              searchPanel.add(searchList);
           }
        }
        searchPanel.add(initModes());

        if ( Hugs.numSearchMgrs.value > 1 ) {
           buttons.add(boxBack);
           buttons.add(boxBest);
        }
        
        add(searchPanel);
        
        add(buttons);
        add(outputPanel);
        */

        SearchThread s = (SearchThread)searches.get(0);
        searcher = new SearchMgrThread(null,s,this.cycle);
        if ( useGUI ) 
           gui = new SearchMgrGUI (this,inputs,inputParams,logger,searches);
        updateEnabled();
        setSearch(s);
        searcher.start();
        searcher.setSearchMgr(this);
    }

   // this is like the user pressing the stop button
   public void doStop () {
      if ( isRunning() )
         actionStopSearch(true);
   }

      // this stops the threads
   public void stopSearches (){
       //dethread
       // for (Iterator i = searches.iterator(); i.hasNext();) {
       // SearchThread s = (SearchThread) i.next();
         // s.stop();
       // }
      searcher.stop();
   }

   /******************************/
   /*        modes               */
   /******************************/

   public static int MODE_RUN = 0;
   public static int MODE_STEP = 1;
   public static int MODE_WATCH = 2;
   public static int MODE_POLL = 3;
   private int mode;
      /*

   private JComboBox initModes (){
      String[] modes = new String[4];
      modes[MODE_RUN] = "run";
      modes[MODE_STEP] = "step";
      modes[MODE_WATCH] = "auto";
      modes[MODE_POLL] = "poll";
      JComboBox modeList = new JComboBox(modes);
      modeList.setSelectedIndex(MODE_RUN);
      mode = MODE_RUN;
      final String[] myModes = modes;
      modeList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               JComboBox cb = (JComboBox)e.getSource();
               int index = cb.getSelectedIndex();
               setMode(index);
               if ( logger != null )
                  logger.add(new SearchEvent(searcher.search.getBestSolution(),("setMode" + myModes[index])));
            }
         });
      return modeList;
   }
   // Minor bug: not quite right if you change Mode numbers arbitrarily
   */

   public int getMode () { return mode; }
   public void setMode (int index) {
      int oldMode = mode;
      mode = index;
      searcher.setMode(mode);
      searcher.search.setMode(mode);
      if ( running && oldMode == MODE_STEP && mode != MODE_STEP ){
         searcher.halt = false;
         searcher.search.halt = false;
      }
   }

   

   public SearchAdjuster getSearchAdjuster () { return searcher.search.getSearchAdjuster(); }
   public Mobilities getOriginalMobilities () { return originalMobilities; }
   public void setOriginalMobilities (Mobilities m) {originalMobilities = m;}
   public boolean isHalted () { return searcher.halt; }
   public boolean isRunning () { return !searcher.halt; }
   public boolean isActive () { return active;}
   public boolean isBackground () {
      if ( gui == null ) return false;
      return gui.isBackground();
   }
   
   public void updateBackground (boolean anyForeground ) {
      if ( gui != null && gui.isBackground() )
         if ( !anyForeground && searcher.halt ){
            if ( originalMobilities == null )
               originalMobilities = (Mobilities) Hugs.THIS.getMobilities().clone();
            actionStart(originalMobilities);
         }
         else if (!searcher.halt && anyForeground )
            actionStopSearch(false);
   }


   public void updateNewBest () {
      boolean isBest = false;
      if (gui == null) return;
      if ( !searcher.halt ) 
         if ( isBackground() ){
            Solution bestSeen = Hugs.THIS.getBestSeen();
            Solution myBest = searcher.search.getBestSolution();
            if ( myBest != null &&
                 (bestSeen == null ||
                  myBest.getScore().isBetter(bestSeen.getScore(),searcher.search.getSearchAdjuster())))
               isBest = true;
         }
         else isBest = unseenBest;
      if ( gui != null )
         if ( isBest)
            gui.setNewBest(true,unseenGlobalBest);
         else gui.setNewBest(false,unseenGlobalBest);
   }
   private void updateEnabled () { 
      if ( gui != null )
         gui.updateEnabled(running);
      updateNewBest();
   }
   /******************************/
   
}

    

