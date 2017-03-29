package hugs.apps.delivery;

import hugs.*;

public class DeliveryRemoveMove implements Move {

   public static boolean trace = false;
   private DeliverySolution solution;
   private Node[] moved;
   private int removed;
   private Node[] nodes;
   
   public DeliveryRemoveMove (DeliverySolution solution, int removed) {
      this.removed = removed;
      nodes = Hugs.THIS.getProblem().getNodes();
      moved = new Node[1];
      moved[0] = nodes[solution.getId(removed)];
      this.solution = solution;
   }

   public int getPly () { return 1; }
   
   public Node[] getMoved () { return moved; }

   public Solution tryMove (Solution current){
    Solution trial = doMove(current);
    trial.computeScore();
    if ( trial.getScore().isBetter(current.getScore()) )
      return trial;
    return null;
  }
   
    public void operateOn (Solution solution ) {
	DeliverySolution s = (DeliverySolution) solution;
	s.remove(removed);
    }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
       // if ( true ) return null;
       if ( score != null ) {
          DeliveryScore s = (DeliveryScore) score;
          DeliveryScore compare = (DeliveryScore) solution.getScore();
          // System.out.println("test " + score + " " + s.isFeasible() + " " + compare);
          if ( s.isFeasible() && (s.getDeliveries() <= compare.getDeliveries() ) ) // note the goal is to minimize deliveries, even though the print functions for DeliveryScore make it look like the goal is to maximize deliveries
             {
                System.out.println("reject");
                return null;
             }
       }

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
      DeliveryRemoveMove move = new DeliveryRemoveMove(solution,removed);
      return (Move) move;
   }

   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public String toString () {
      return "[remove #" + removed + " in path]";
   }
}






