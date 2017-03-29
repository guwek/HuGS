package hugs.search;

import hugs.*;
import hugs.utils.*;
import java.util.*;
import java.io.*;

public class SearchThread implements Serializable { 
   
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
   
    public SearchThread (Problem problem){
	this.problem = problem;
    }
   
   public String getSearchName () { return "no name"; }
   public Solution getBestSolution () { return bestSolution; }
   public Solution getCurrentSolution () { return currentSolution; }
   public Mobilities getCurrentMobilities () { return currentMobilities; }
   public boolean isHalted () { return halt; }
   public void setSearchMgr (SearchMgr m) { searchMgr = m; }
   public List getOutputs () { return outputs; }
   public List getInputs () { return inputs; }

   public void clearBestSolution () { bestSolution = null; }
   public Solution getSolution () { return solution;}
   public Mobilities getMobilities () { return mobilities;}
   public void setMobilities ( Mobilities mobilities ) { this.mobilities = mobilities; }

    public void startSearch (Solution solution, Mobilities mobilities, List marked) {
       this.solution = solution.copy();
       this.mobilities = (Mobilities) mobilities.clone();
       this.marked = Utils.copyList(marked);
       invoke = true;
       halt = false;
    }
   
    public void continueSearch () {
	halt = false;
    }
   
    public void stopSearch () {
	halt = true;
	while ( running ); // wait for iteration to stop
    }

    public void searchIteration () {}
    public void initSearch (Solution s, Mobilities m, List marked) {}
    public String toString () { return "[Search: " + getSearchName() + "]"; }
}
