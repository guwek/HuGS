package hugs.apps.crossing;

import hugs.*;
import java.util.*;
import java.awt.Point;

public class CrossingMoveGenerator implements MoveGenerator {

   private CrossingSolution solution;
   private Mobilities mobilities;
   public CrossingMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster){
      this.mobilities = mobilities;
      this.solution = (CrossingSolution) solution;
      reset();
   }

   private List moves = new ArrayList();
   private int position = 0;
   
   public void reset () {
      moves.clear();
      position = 0;
      CrossingProblem crossingProblem = (CrossingProblem) Hugs.THIS.getProblem();
      Node[] nodes = crossingProblem.getNodes();
      int maxX = crossingProblem.getXSize()- 1;
      
      for (int n = nodes.length; n-->0;){
         if ( mobilities.getMobility(n) == Mobilities.HIGH ) {
            Point p1 = solution.getLocation(n);
            // go right
            if (p1.x < maxX ) {
               Node node2 = solution.getNode(p1.x+1,p1.y);
               int n2 = node2.getId();
               if ( mobilities.getMobility(n2) != Mobilities.LOW ) {
                  CrossingSwapMove move = new CrossingSwapMove(solution,n,n2);
                  moves.add(move);
               }
            }
            // go left, only if left neighbor is medium (otherwise will swap with you)
            if ( p1.x > 0 ) {
               Node node2 = solution.getNode(p1.x-1,p1.y);
               int n2 = node2.getId();
               if ( mobilities.getMobility(n2) == Mobilities.MED ) {
                  CrossingSwapMove move = new CrossingSwapMove(solution,n2,n);
                  moves.add(move);
               }
            }
         }
      }
      // hugs.utils.Utils.enumerateList(moves);
   }
   
   public Move nextMove () {
      if ( position < moves.size() )
         return (Move) moves.get(position++);
      return null;
   }

}
