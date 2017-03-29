package hugs.search;

import hugs.*;
import hugs.support.*;
import hugs.utils.*;
import java.util.*;

public class SteepThread extends SearchThread {

   public static boolean trace = false;
   
   public String getSearchName () { return "steepest";}

   Move bestMove;
   Score bestScore;
   MoveGenerator mover;
   private IntParameter iterationP = new IntParameter("iter");
   private IntParameter plyP = new IntParameter("ply");
   private IntParameter bestPlyP = new IntParameter("bestPly");

   public SteepThread () {
      this(null);
   }
   
   public SteepThread (Problem problem) {
      super(problem);
      outputs.add(iterationP);
      outputs.add(plyP);
      outputs.add(bestPlyP);
   }

   public void initSearch (Solution s, Mobilities mobilities, List marked){
      this.marked = marked;
       this.solution = s;
       this.mobilities = (Mobilities) mobilities.clone();
       currentMobilities = (Mobilities) mobilities.clone();
       mover = Hugs.THIS.makeMoveGenerator(mobilities,solution,searchAdjuster);
      bestMove = null;
      bestSolution = null;
      bestScore = null;
      iterationP.value = 0;
      plyP.value = 0;
   }

   public void searchIteration () {
      Move move = mover.nextMove();
      System.out.println("@@ steepest: " + move);
      iterationP.value++;
      if (move == null) halt = true;
      else{
         plyP.value = move.getPly();
         Solution newSolution = Utils.doMove(move,solution);
         currentSolution = newSolution;
         if (newSolution != null){
            newSolution.computeScore(searchAdjuster,marked);
            if (bestScore == null ||
                newSolution.getScore().isBetter(bestScore,searchAdjuster)) {
               bestSolution = newSolution;
               bestMove = move;
               bestScore = newSolution.getScore();
               if ( searchMgr != null )
                  searchMgr.updateSearchMessage();
               bestPlyP.value = bestMove.getPly();
               if ( trace ) {
                  System.out.println("New best move: " + bestMove);
                  System.out.println("Best solution: " + bestSolution);
                  System.out.println("Score: " + bestSolution.computeScore());
               }
            }
         }
      }
   }


   
}


   
