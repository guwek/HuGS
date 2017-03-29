package hugs.apps.stpc;

import hugs.*;

public class StpcImproveMove implements Move {

   public static boolean trace = false;
   private StpcSolution solution;
   private Node [] moved;
   
   public StpcImproveMove (StpcSolution solution) {
      this.solution = solution;

		moved = new Node[0];
   }

   public Node[] getMoved () { return moved; }

   public int getPly () { return 1; }
   
    public void operateOn (Solution solution )
    {
    	((StpcSolution) solution).improve();
    }

   public Move copy () {
      StpcImproveMove move = new StpcImproveMove(solution);
      return (Move) move;
   }

   
   public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked)
	{
		Solution s = hugs.utils.Utils.doMove(this,solution);
		if ( s == null ) return null;
		return s.getScore();
   }

/*   public void setMovedMobilitiesTo (Mobilities mobilities, int value)
   {
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }*/
   
   public String toString () {
	return new String("Improve Solution");
      }
}






