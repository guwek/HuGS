package hugs.support;

import hugs.*;
import java.util.*;

public class MultiMove implements Move {

   private Move[] moves;
   private Node[] moved;
   public MultiMove (Move[] moves){
      this.moves = moves;
      int numMoves = 0;
      for (int i = moves.length; i-->0;)
         numMoves += moves[i].getMoved().length;
      moved = new Node[numMoves];
      int count = 0;
      for (int i = moves.length; i-->0;){
         Node[] miniMoved = moves[i].getMoved();
         for (int j = miniMoved.length;j-->0;)
            moved[count++] = miniMoved[j];
      }
   }
   
   public Node[] getMoved () { return moved; }
   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public int getPly() { return moves.length; }
   
   public Solution tryMove (Solution current) {
      Solution trial = doMove(current);
      trial.computeScore();
      if ( trial.getScore().isBetter(current.getScore()) )
         return trial;
      return null;
   }
   
    public void operateOn (Solution solution ) {
	for (int i = 0; i < moves.length; i++)
	    moves[i].operateOn(solution);
    }

    public Solution doMove (Solution solution) {
	Solution copy = solution.copy();
	operateOn(copy);
	if ( !Hugs.FAST_MODE ) copy.computeScore();
	// System.out.println("swapped score" + copy.getScore());
	return copy;
    }

    public Score evaluate (Solution solution, Score score, 
			   SearchAdjuster searchAdjuster, List marked) {
	if ( moves.length > 1 ) {
	    Solution s = doMove(solution);
	    if ( s == null ) return null;
	    return s.getScore();
	}
	if ( moves.length == 0 ) return null;
	return moves[0].evaluate(solution,score,searchAdjuster,marked);
    }
   public String toString (){
      if ( moves.length == 0 ) return "[Multi-- blank]";
      String out = "[Multi(" + moves.length + "): " + moves[0];
      for (int i = 1; i < moves.length; i++)
         out += " & " + moves[i];
      out += "]";
      return out;
   }
}
