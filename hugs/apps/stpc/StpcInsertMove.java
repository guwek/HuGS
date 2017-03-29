package hugs.apps.stpc;

import hugs.*;

public class StpcInsertMove implements Move {

   public static boolean trace = false;
   private StpcSolution solution;
   private Node [] moved;
   private int nNode;
   
   public StpcInsertMove (StpcSolution solution, int nNode) {
      this.solution = solution;
      this.nNode = nNode;

		StpcProblem p = solution.getProblem();
		StpcNode [] nodes = (StpcNode []) p.getNodes();
		moved = new Node[1];
		moved[0] = nodes[nNode];
   }

   public Node[] getMoved () { return moved; }

   public int getPly () { return 1; }
   
    public void operateOn (Solution solution )
    {
    	StpcSolution s = (StpcSolution) solution;
		s.addNode(nNode);
    }

   public Move copy () {
      StpcInsertMove move = new StpcInsertMove(solution, nNode);
      return (Move) move;
   }

   
   public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked)
	{
		StpcSolution sol = (StpcSolution) solution;
		boolean [] nodeTaken = sol.getNodeTaken();
		boolean t = nodeTaken[nNode];
		
		nodeTaken[nNode] = true;
		sol.construct();
		Score s = sol.computeScore();
		nodeTaken[nNode] = t;
		sol.construct();
		return s;
		
/*		Solution s = hugs.utils.Utils.doMove(this,solution);
		if ( s == null ) return null;
		return s.getScore();*/
   }

/*   public void setMovedMobilitiesTo (Mobilities mobilities, int value)
   {
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }*/
   
   public String toString () {
	return new String("Insert Node " + nNode);
      }
}






