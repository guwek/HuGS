package hugs.apps.stpc;

import hugs.*;
import java.util.*;

public class StpcMoveGenerator implements MoveGenerator {

   public static int MAX_SPLICE_WINDOW = 10;
   private StpcSolution solution;
   private Mobilities mobilities;
   private StpcSearchAdjuster searchAdjuster;

      public StpcMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster){
      this.mobilities = mobilities;
      this.solution = (StpcSolution) solution;
      this.searchAdjuster = (StpcSearchAdjuster) searchAdjuster;
      reset();
   }

   private List moves = new ArrayList();
   private int position = 0;
   
   public void reset () {
      StpcVisualization viz = (StpcVisualization) Hugs.THIS.getVisualization();
      int size = solution.size();
      moves.clear();
      position = 0;
      StpcProblem problem = (StpcProblem) Hugs.THIS.getProblem();
      StpcNode[] nodes = (StpcNode []) problem.getNodes();
		StpcEdge [] edges = problem.getEdges();
		Vector [] inzEdges = problem.getInzEdges();
		
		int [] degrees = solution.getDegrees();
		boolean [] nodeTaken = solution.getNodeTaken();
		boolean [] edgeTaken = solution.getEdgeTaken();

		// Remove Moves
      for (int i = problem.getDepots(); i<nodes.length; i++)
			if (nodeTaken[i] && mobilities.getMobility(i) == Mobilities.HIGH)
				moves.add(new StpcRemoveMove(solution, i));		

		if (solution.size() != 0)					
			// Insert Moves
			for (int i = edges.length; i-->0;)
			{
				if (  nodeTaken[edges[i].from] &&
					!nodeTaken[edges[i].to] &&
						mobilities.getMobility(edges[i].from) != Mobilities.LOW &&
						mobilities.getMobility(edges[i].to  ) == Mobilities.HIGH)
					moves.add(new StpcInsertMove(solution, edges[i].to));
			
				if ( !nodeTaken[edges[i].from] &&
						nodeTaken[edges[i].to] &&
						mobilities.getMobility(edges[i].from) == Mobilities.HIGH &&
						mobilities.getMobility(edges[i].to  ) != Mobilities.LOW)
					moves.add(new StpcInsertMove(solution, edges[i].from));			
			}
		else
  			for (int i = nodes.length; i-->0;)
				moves.add(new StpcInsertMove(solution, i));

   }
	
   public Move nextMove () {
      if ( position < moves.size() )
         return (Move) moves.get(position++);
      return null;
   }
}
