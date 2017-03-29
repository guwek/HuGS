package hugs.apps.delivery;

import hugs.*;

public class DeliveryReinsertMove implements Move {

   public static boolean trace = false;
   private DeliverySolution solution;
   private Node[] moved;
   private int reinserted;
   private int location;
   private Node[] nodes;
   
   public DeliveryReinsertMove (DeliverySolution solution, int reinserted, int location) {
      this.reinserted = reinserted;
      this.location = location;
      nodes = Hugs.THIS.getProblem().getNodes();
      moved = new Node[1];
      moved[0] = nodes[solution.getId(reinserted)];
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
	s.reinsert(reinserted,location);
    }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }


   public Solution doMove (Solution solution) {
      // System.out.println("before move");
      // solution.print();
      DeliverySolution copy = (DeliverySolution) solution.copy();
      operateOn(copy);
      copy.computeScore();
      // System.out.println("after move");
      // copy.print();
      return copy;
   }


   public Move copy () {
      DeliveryReinsertMove move = new DeliveryReinsertMove(solution,reinserted,location);
      return (Move) move;
   }

   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public String toString () {
      return "[reinsert " + reinserted + " into position " + location + "]";
   }
}






