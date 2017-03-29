package hugs.apps.delivery;

import hugs.*;
import java.util.*;

public class DeliveryMoveGenerator implements MoveGenerator {

   public static int MAX_SPLICE_WINDOW = 10;
   private DeliverySolution solution;
   private Mobilities mobilities;
   private DeliverySearchAdjuster searchAdjuster;
   public DeliveryMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster){
      this.mobilities = mobilities;
      this.solution = (DeliverySolution) solution;
      this.searchAdjuster = (DeliverySearchAdjuster) searchAdjuster;
      reset();
   }

   private List moves = new ArrayList();
   private int position = 0;
   
   public void reset () {
      DeliveryVisualization viz = (DeliveryVisualization) Hugs.THIS.getVisualization();
      int size = solution.size();
      moves.clear();
      position = 0;
      Node[] nodes = Hugs.THIS.getProblem().getNodes();

      // removes first
      if ( searchAdjuster.removes.value )
         for (int i = nodes.length; i-->0;){
            int id = nodes[i].getId();
            if ( mobilities.getMobility(id) == Mobilities.HIGH ) {
               int position = solution.getPosition(id);
               if ( position >= 0 ) 
                  moves.add(new DeliveryRemoveMove(solution,position));
            }
         }

      if ( searchAdjuster.swaps.value )
         for (int i = 0; i < size-1; i++){         
            if ( mobilities.getMobility(solution.getId(i)) == Mobilities.HIGH &&
                 mobilities.getMobility(solution.getId(i+1)) != Mobilities.LOW ) {
               DeliverySwapMove move = new DeliverySwapMove(solution,i);
               moves.add(move);
            }
         }

      if ( searchAdjuster.inserts.value )
         for (int i = nodes.length; i-->0;){
            int id = nodes[i].getId();
            if ( mobilities.getMobility(id) == Mobilities.HIGH ) {
               int position = solution.getPosition(id);
               if ( position < 0 && id != 0) {
                  int target = solution.closestInsert(id);
                  moves.add(new DeliveryInsertMove(solution,id,target));
                  if ( mobilities.getMobility(target) == Mobilities.LOW ) {
                     // System.out.println("BUG in DeliveryMoveGenerator: need to check for priority of insert");
                  }
                  /*
                  // skip depot 
                  for (int j = size; j-->0;){
                     int insertId = solution.getId(j);
                     if ( mobilities.getMobility(insertId) != Mobilities.LOW )
                        moves.add(new DeliveryInsertMove(solution,id,j));
                  }
                  */
               }
            }
         }

      if ( searchAdjuster.splices.value )
         for (int i = 0; i < size; i++){
            int max = Math.min(size,i+searchAdjuster.spliceSize.value );
            for (int j = i+2; j < max; j++){
               if ( i != j && canSplice(i,j) ) {
                  DeliverySpliceMove move = new DeliverySpliceMove(solution, 1);
                  move.set(0,i,j);
                  moves.add(move);
               }
            }
         }
      
      // System.out.println("add moves");
      // Utils.enumerateList(moves);
      // Utils.enumerateList(moves);
   }
   
   public Move nextMove () {
      if ( position < moves.size() )
         return (Move) moves.get(position++);
      return null;
   }

   public boolean canSplice (int first, int target  ){
      int start = solution.getId(first);
      int stop = solution.getId(target);
      if ( mobilities.getMobility(start) != Mobilities.HIGH ||
           mobilities.getMobility(stop) != Mobilities.HIGH )
         return false;
      if ( start > stop ){
         int temp = stop;
         stop = start;
         start = temp;
      }
      for (int i = start+1; i < stop; i++)
         if (mobilities.getMobility(i) == Mobilities.LOW)
            return false;
      return true;
   }
}
