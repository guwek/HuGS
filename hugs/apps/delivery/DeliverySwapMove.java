package hugs.apps.delivery;

import hugs.*;


public class DeliverySwapMove implements Move {

   public static boolean trace = false;
   
   private DeliverySolution solution;
   private Node[] moved; 
   private int first;
   private Node[] nodes;
   public DeliverySwapMove (DeliverySolution solution, int first) {
      this.first = first;
      nodes = Hugs.THIS.getProblem().getNodes();
      moved = new Node[2];
      moved[0] = nodes[solution.getId(first)];
      moved[1] = nodes[solution.getId(first+1)];
      this.solution = solution;
   }

   public int getPly () { return 1; }

   public Node[] getMoved () {
      return moved;
   }

   public Solution tryMove (Solution current) {
      Solution trial = doMove(current);
      trial.computeScore();
      if ( trial.getScore().isBetter(current.getScore()) )
         return trial;
      return null;
   }

    public void operateOn (Solution solution ) {
	DeliverySolution s = (DeliverySolution) solution;
	s.swap(first);
    }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }


   public Solution doMove (Solution solution) {
      DeliverySolution copy = (DeliverySolution) solution.copy();
      operateOn(copy);
      copy.computeScore();
      return copy; 
   }

   public Move copy () {
      DeliveryRemoveMove move = new DeliveryRemoveMove(solution,first);
      return (Move) move;
   }

   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public String toString () {
      return "[swap " + moved[0].getId() + " & " + moved[1].getId() + "]";
   }
}






