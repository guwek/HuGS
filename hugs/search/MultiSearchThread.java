package hugs.search;

import hugs.*;
import hugs.utils.*;
import java.util.*;

public class MultiSearchThread extends SearchThread {
  
   public String getSearchName () { return "multi";}
   private List searchers;
   private int switchTime;
   private int position;
   private int cycle;
   private SearchThread search;
   private boolean simulateParallel = false;
   
   public MultiSearchThread (List searchers, int cycle) {
      this(null, searchers, cycle, false);
   }
   
   public MultiSearchThread (Problem problem, List searchers, int cycle, boolean simulateParallel) {
      super(problem);
      if ( searchers == null ) this.searchers = new ArrayList();
      else this.searchers = searchers;
      this.cycle = cycle;
      this.simulateParallel = simulateParallel;
      for (Iterator i = this.searchers.iterator(); i.hasNext();){
         SearchThread s = (SearchThread) i.next();
         Utils.addTo(inputs,s.getInputs());
      }
   }

   public List getOutputs () {
      return search == null ? null : search.getOutputs();
   }

   public Solution getBestSolution () {
      Solution s = search == null ? null : search.getBestSolution();
      if ( s != null && (bestSolution == null ||
                         s.getScore().isBetter(bestSolution.getScore(),searchAdjuster) ) )
         return s;
      return bestSolution;
   }
   
   public Solution getCurrentSolution () {
      return search == null ? null : search.getCurrentSolution();
   }
   public Mobilities getCurrentMobilities () {
      return search == null ? null : search.getCurrentMobilities();
   }
   
   public void addSearch (SearchThread s ){
      searchers.add(s);
      Utils.addTo(inputs,s.getInputs());
   }
   public void setProblem (Problem problem) {
	this.problem = problem;
        for (Iterator i = searchers.iterator(); i.hasNext();)
           ((SearchThread) i.next()).setProblem(problem);
   }

   public void setSearchAdjuster (SearchAdjuster searchAdjuster) {
      this.searchAdjuster = searchAdjuster;
      for (Iterator i = searchers.iterator(); i.hasNext();)
         ((SearchThread) i.next()).setSearchAdjuster(searchAdjuster);
   }

   int count;
   Solution newBestSolution;
   public void initSearch (Solution s, Mobilities mobilities, List marked){
      initSearch(s,mobilities,marked,null);
   }

   private void initSearch (Solution s, Mobilities mobilities, List marked, SearchThread ignoreSearch){
      newBestSolution = null;
      count = 0;
      // System.out.println("init search: " + searchers);
      // System.out.println("mobilities: " + mobilities);
      // System.out.println("solution: " + s);
      this.marked = marked;
      if ( mobilities != null )
         this.mobilities = (Mobilities) mobilities.clone();
      bestSolution = s;
      s.computeScore(searchAdjuster,marked);
      for (Iterator i = searchers.iterator(); i.hasNext();){
         SearchThread srch = (SearchThread) i.next();
         if ( srch != ignoreSearch ) {
            System.out.println("initializing " + srch);
            Mobilities m= (Mobilities)mobilities.clone();
            srch.setMobilities (m);
            Solution copy = s.copy();
            copy.computeScore(searchAdjuster,marked);
            srch.initSearch(copy,m,marked);
         }
      }
      switchTime = cycle + 
         ((int) Calendar.getInstance().getTime().getTime()/1000);
      if ( ignoreSearch == null ) {
         position = 0;
         search = (SearchThread) searchers.get(0);
      }
   }

   public void searchIteration () {
      int time = ((int) Calendar.getInstance().getTime().getTime()/1000);
      if ( time > switchTime ){
         switchTime = time + cycle;
         Solution b = search.getBestSolution();
         b.computeScore(searchAdjuster,marked);
         if ( b != null )
            System.out.println("search best solution score = " + b.getScore());
         if ( bestSolution != null ) {
            System.out.println("best solution score = " + bestSolution.getScore());
            System.out.println("is better: " + b.getScore().isBetter(bestSolution.getScore(),searchAdjuster));
            System.out.println("searchAdjster : " + searchAdjuster);
         }
         if ( b != null &&
              (bestSolution == null ||
               b.getScore().isBetter(bestSolution.getScore(),searchAdjuster)) )
            if ( newBestSolution == null ||
                 b.getScore().isBetter(newBestSolution.getScore(),searchAdjuster)){
               bestSolution = b;
               newBestSolution = b;
               // System.out.println("newBestSol: " + newBestSolution);
            }
               
         // System.out.println("Switching from " + position + ":" + search +
         // " at time " + time + " newSwitch = " + switchTime);
         // System.out.println("outputs: " + search.getOutputs());
         // System.out.println("inputs: " + search.getInputs());
         position++;
         if ( !simulateParallel || position == searchers.size() ){
            if (newBestSolution != null ) {
               System.out.println("new best: " + newBestSolution.getScore());
               initSearch(newBestSolution,mobilities,marked,search);
               if ( simulateParallel ) return;
             }
            else System.out.println("not new best: " + (newBestSolution == null ? null : newBestSolution.getScore()));
         }
         if ( position == searchers.size() ) 
            position = 0;
         search = (SearchThread) searchers.get(position);
      }
      search.halt = false;
      search.searchIteration();
   }

}










