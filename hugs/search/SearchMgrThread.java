package hugs.search;

import hugs.*;
import hugs.utils.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;

public class SearchMgrThread extends Thread implements Serializable { 
   
    protected boolean halt = true;    
    protected boolean invoke = false;
    protected boolean running = false;
    protected Solution bestSolution = null;
    protected Solution currentSolution = null;
    protected Mobilities currentMobilities = null;
    protected int mode = SearchMgr.MODE_RUN;
    protected SearchMgr searchMgr = null;
    protected List outputs = new ArrayList();
    protected List inputs = new ArrayList();
    protected Problem problem;
    protected Solution solution;
    protected Mobilities mobilities;
    protected SearchAdjuster searchAdjuster;
    protected List marked = null;
   
   public void setMode (int mode) {
      this.mode = mode;
   }
   
    public void setProblem (Problem problem) {
	this.problem = problem;
    }

   public void setSearchAdjuster (SearchAdjuster searchAdjuster) {
      this.searchAdjuster = searchAdjuster;
    }
   public SearchAdjuster getSearchAdjuster () { return searchAdjuster; }
   public void setMarked (List marked) {
      this.marked = Utils.copyList(marked);
    }
   public List getMarked () { return marked; }

   public boolean getHalt () { return halt; }
   
   public String getSearchName () { return "no name"; }
   public Solution getBestSolution () { return bestSolution; }
   public Solution getCurrentSolution () { return currentSolution; }
   public Mobilities getCurrentMobilities () { return currentMobilities; }
   public boolean isHalted () { return halt; }
   public void setSearchMgr (SearchMgr m) { searchMgr = m; }
   public List getOutputs () { return outputs; }
   public List getInputs () { return inputs; }

   public void clearBestSolution () { bestSolution = null; }
   public void setMobilities ( Mobilities mobilities ) { this.mobilities = mobilities; }

    javax.swing.Timer timer;


    public void startSearch (Solution solution, Mobilities mobilities, List marked) {
       this.solution = solution.copy();
       this.mobilities = (Mobilities) mobilities.clone();
       this.marked = Utils.copyList(marked);
       invoke = true;
       halt = false;
       timer.start();

    }
   
    public void continueSearch () {
	halt = false;
    }
   

    private void updateSearchMessage() {
	if ( mode == SearchMgr.MODE_POLL ) searchMgr.actionGetCurrent();
	searchMgr.updateSearchMessage();
	
    }

    public void run (){
	while (true){
	    if ( invoke ){
		invoke = false;
		initSearch(solution, mobilities, marked);
	    }
	    else if (!halt && !invoke) {
		running = true;
                   searchIteration();
                   if ( searchMgr != null )
                      if ( mode == SearchMgr.MODE_STEP ){
                         searchMgr.actionGetCurrent();
                         halt = true;
                      }
                      else if ( mode == SearchMgr.MODE_WATCH ){
                         searchMgr.actionGetCurrent();
                         try { Thread.currentThread().sleep(50); }
                         catch (InterruptedException e) {
                            System.err.println("Sleep Interrupted");
                         };
                      }
                   // maybe BUG: adding this quick sleep makes the stop button
                   // much more responsive, but maybe slows down search a lot?
                   if ( !Hugs.FAST_MODE ) {
                      try { Thread.currentThread().sleep(1); }
                      catch (InterruptedException e) { System.err.println("Sleep Interrupted");};
                   }
		running = false;
	    }
            else {
               try { Thread.currentThread().sleep(200); }
               catch (InterruptedException e) { System.err.println("Sleep Interrupted");};
            }
	}
    }


   public String toString () { return "[Search: " + getSearchName() + "]"; }



    /////----
      protected Solution restart;
      protected SearchThread search;
      protected int cycle;
      protected boolean isExperiment = false;

    public SearchMgrThread (Problem problem, SearchThread search, int cycle) {
	this.problem = problem;
	this.search = search;
	this.cycle = cycle;
	ActionListener listen =   new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    updateSearchMessage();
		}
	    };
	timer = new javax.swing.Timer (cycle,listen);
    }
   


      public void setExperiment (boolean b) {isExperiment = b;}
      public void setSearch (SearchThread s){ search = s;}
      

      public void initSearch (Solution solution, Mobilities p, java.util.List marked) {
         search.stopSearch();
         search.clearBestSolution();
         search.setSearchMgr(searchMgr);
	 SearchAdjuster searchAdjuster = search.getSearchAdjuster();
	 if ( searchAdjuster != null )
	     searchAdjuster.precompute(solution);

	 searchMgr.initSearch(solution,p,marked);

         search.setMode(searchMgr.getMode());
         search.startSearch(solution,Hugs.THIS.getMobilities(),Hugs.THIS.getMarkedList());
	 search.initSearch(search.solution,search.mobilities,search.marked);

      }

      public void stopSearch () {
	  // if ( searchMgr.mode != SearchMgr.MODE_RUN )
	  // updateSolution(originalSolution, false);
         search.stopSearch();
         halt = true;
	 timer.stop();
      }

      public void searchIteration () {
         Solution myBest;
         Solution bestFound;
         if ( restart != null ){
            initSearch(restart,(Mobilities)searchMgr.getOriginalMobilities().clone(), Hugs.THIS.getMarkedList());
            restart = null;
         }
         else if ( searchMgr.useBest() &&
		   searchMgr.getOriginalMobilities() != null &&
              (bestFound=Hugs.THIS.getBestFound()) != null &&
              ((myBest = search.getBestSolution()) == null ||
               bestFound.getScore().isBetter(myBest.getScore(),search.getSearchAdjuster())) ){
            initSearch(bestFound.copy(),(Mobilities)searchMgr.getOriginalMobilities().clone(),Hugs.THIS.getMarkedList());
         }
         
         if ( search.halt ) {
            System.out.println("searchMgrThread halting search from below");
            stopSearch();
         }
         else {
            if ( isExperiment )
		searchMgr.updateTrace();
	    search.searchIteration();
	    /* dethread
            if ( searchMgr.getMode() == SearchMgr.MODE_POLL ) actionGetCurrent();
            try { Thread.currentThread().sleep(cycle); }
            catch (InterruptedException e) {
               System.err.println("Sleep Interrupted");
            };
	    */
         }
      }
   }



