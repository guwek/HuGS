package hugs.apps.stpc;

import hugs.*;

public class StpcRemoveMove implements Move {

   public static boolean trace = false;
   private StpcSolution solution;
   private Node [] moved;
   private int nNode;
   
   public StpcRemoveMove (StpcSolution solution, int nNode) {
      this.solution = solution;
      this.nNode = nNode;

		StpcProblem p = solution.getProblem();
		StpcEdge [] edges = p.getEdges();
		StpcNode [] nodes = (StpcNode []) p.getNodes();
		moved = new Node[1];
		moved[0] = nodes[nNode];		
   }

   public Node[] getMoved () { return moved; }

   public int getPly () { return 1; }
   
    public void operateOn (Solution solution)
    {
    	StpcSolution s = (StpcSolution) solution;
		s.removeNode(nNode);
    }

   public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked)
	{
		Solution s = hugs.utils.Utils.doMove(this,solution);
		if ( s == null ) return null;
		return s.getScore();
   }

   public Move copy () {
      StpcRemoveMove move = new StpcRemoveMove(solution, nNode);
      return (Move) move;
   }

   public void setMovedMobilitiesTo (Mobilities mobilities, int value)
   {
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public String toString () {
	return new String("remove Node " + nNode);
      }
}






