package hugs.apps.crossing;

import hugs.*;
import java.awt.Point;

public class CrossingSwapMove implements Move {

   public static boolean trace = false;
   
   private CrossingSolution solution;
   private Node[] moved; 
   private int nodeId;
   private int neighborId;

   public int getNodeId() { return nodeId;}
   public int getNeighborId() { return neighborId;}
   
   private Node[] nodes;
   public CrossingSwapMove (CrossingSolution solution, int nodeId, int neighborId) {
      this.neighborId = neighborId;
      this.nodeId = nodeId;
      nodes = Hugs.THIS.getProblem().getNodes();
      moved = new Node[2];
      moved[0] = nodes[nodeId];
      moved[1] = nodes[neighborId];
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
	((CrossingSolution)solution).swap(nodeId,neighborId);
    }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }   


   public Solution doMove (Solution solution) {
      CrossingSolution copy = (CrossingSolution) solution.copy();
      operateOn(copy);
      // copy.computeScore();
      return copy; 
   }

   /*
   public Move copy () {
      CrossingSwapMove move = new CrossingSwapMove(solution,nodeId);
      return (Move) move;
   }
   */
   
   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }

   public String toString () {
      return "[swap " + moved[0].getId() + " & " + moved[1].getId() + "]";
   }
}






