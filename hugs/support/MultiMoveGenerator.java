package hugs.support;

import java.util.*;
import hugs.*;
import hugs.utils.*;

public class MultiMoveGenerator implements MoveGenerator {

   public static boolean trace = false;
   /*
   List generators;
   public MultiMoveGenerator (List generators){
      this.generators = generators;
      reset();
   }
   public MultiMoveGenerator (MoveGenerator generator){
      generators = new ArrayList(1);
      generators.add(generator);
      reset();
   }
   */

   private MoveGeneratorMaker maker;
   private Mobilities mobilities;
   private Solution solution;
   private SearchAdjuster searchAdjuster;
   public MultiMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster, MoveGeneratorMaker maker){
      this.maker = maker;
      this.solution = solution;
      this.mobilities = mobilities;
      this.searchAdjuster = searchAdjuster;
      reset();
   }

   public void reset (){
      initNPly(1);
   }

   private MoveGenerator[] generators;
   private Solution[] solutions;
   private Move[] moves;
   private int ply;
   private int level;

   private Move getNextMove () {
      if ( level < 0 ) return null; // done with ply
      Move m = generators[level].nextMove();
      // System.out.println("move at level " + level + ": " + m);
      moves[level] = m;
      if ( m == null ) {
         level--;
         return getNextMove();
      }
      if ( level == (ply-1) ) {
         if ( trace ){
            System.out.print("About to make move with:");
            for (int i = 0; i < moves.length;i++)
               System.out.print(moves[i] +  " ");
            System.out.println();
         }
         Move multi = new MultiMove(moves); // next move
         Move[] newMoves = new Move[ply];
         for (int i = 0; i < moves.length;i++)
            newMoves[i] = moves[i];
         moves = newMoves;
         // System.out.println("returning " + multi);
         return multi;
      }
      // System.out.println("---going up");
      solutions[level+1] = Utils.doMove(m,solutions[level]);
      // System.out.println("solution at level: " + (level+1));
      // solutions[level+1].print();
      level++;
      generators[level] = maker.makeGenerator(mobilities,solutions[level],searchAdjuster);
      return getNextMove();
   }
   
   private void initNPly(int ply){
      if ( trace ) System.out.println("******** ply " + ply + " **************");
      this.ply = ply;
      generators = new MoveGenerator[ply];
      solutions = new Solution[ply];
      moves = new Move[ply];
      generators[0] = maker.makeGenerator(mobilities,solution,searchAdjuster);
      solutions[0] = solution.copy();
      level = 0;
   }

   private static int countMoves = 0;
   public Move nextMove () {
      Move m = getNextMove();
      if ( trace ) System.out.println(countMoves++ + "] multi: " + m);
      if ( m != null ) return m;
      ply++;
      initNPly(ply);
      m = getNextMove();
      if ( trace ) System.out.println(countMoves++ + ") multi: " + m);
      return m;
   }
}
