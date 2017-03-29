package hugs.search;

import hugs.*;
import hugs.support.*;
import hugs.utils.*;
import java.util.*;

public class GreedyThread extends SearchThread {

   public static String name = "greedy";
   public String getSearchName () { return name; }
   MoveGenerator mover;
   private IntParameter iterationP = new IntParameter("iter");
   private IntParameter plyP = new IntParameter("ply");
   private IntParameter movesP = new IntParameter("moves");

   public GreedyThread () {
      this(null);
   }
   
   public GreedyThread (Problem problem) {
      super(problem);
      outputs.add(iterationP);
      outputs.add(plyP);
      outputs.add(movesP);
   }

   public void initSearch (Solution s, Mobilities mobilities, List marked){
      this.marked = marked;
       this.solution = s;
       this.mobilities = (Mobilities) mobilities.clone();
       currentMobilities = (Mobilities) mobilities.clone();
      System.out.println("greedy mobilities: " + mobilities);
      mover = Hugs.THIS.makeMoveGenerator(mobilities,solution,searchAdjuster);
      bestSolution = solution;
      iterationP.value = 0;
      plyP.value = 0;
      movesP.value = 0;
   }

   public void searchIteration () {
      Move move = mover.nextMove();
      iterationP.value++;
      if (move == null) halt = true;
      else{
         plyP.value = move.getPly();
         Solution newSolution = Utils.doMove(move,bestSolution);
         newSolution.computeScore(searchAdjuster,marked);
         currentSolution = newSolution;
         if (newSolution.getScore().isBetter(bestSolution.getScore(),searchAdjuster)){
            movesP.value++;
            bestSolution = newSolution;
            bestSolution.computeScore(searchAdjuster,marked);
            if ( searchMgr != null )
               searchMgr.updateSearchMessage();
            mover = Hugs.THIS.makeMoveGenerator(mobilities,bestSolution,searchAdjuster);
         }
      }
   }
}


   
