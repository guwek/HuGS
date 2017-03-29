package hugs.apps.protein;

import hugs.*;
import java.awt.Point;
import java.util.*;

public class ProteinMoveGenerator implements MoveGenerator {

   private ProteinSolution solution;
   private Mobilities mobilities;
   private Node[] nodes;
   private ProteinProblem problem;
   private int xSize;
   private int ySize;
   private int xMax;
   private int yMax;
   private int[][] board;
   private ProteinSearchAdjuster searchAdjuster;
   

   public ProteinMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster){
      this.searchAdjuster = (ProteinSearchAdjuster) searchAdjuster;
      this.mobilities = mobilities;
      this.solution = (ProteinSolution) solution;
      problem = (ProteinProblem) Hugs.THIS.getProblem();
      nodes = problem.getNodes();
      xSize = problem.getXSize();
      ySize = problem.getYSize();
      xMax = xSize - 1;
      yMax = ySize - 1;
      board = this.solution.getBoard();
      reset();
   }

   private List moves = new ArrayList();
   private int position = 0;

   
   private Move makeCarryMove (int id, int x, int y, boolean flipRight, boolean flipLeft) {
      if ( x >= 0 && y >= 0 && x < xSize && y < ySize ){
         Move m = solution.carryMove(id, new Point(x,y),flipRight,flipLeft);
         if ( m == null ) return (Move) m;
         Node[] moved = m.getMoved();
         for (int i = moved.length; i-->0;)
            if ( mobilities.getMobility(moved[i].getId()) == Mobilities.LOW )
               return null;
         /*
           ProteinSolution s = (ProteinSolution) m.doMove(solution);
         if ( s.hasOverlap() ) {
            System.out.println("bad move: " + m );
            System.out.println("original solution: " + solution);
            System.out.println("final solution: " + s);
            System.exit(0);
         }
         */
         return (Move) m;
      }
      return null;
   }


   private Move makeFollowMove (int id, int x, int y) {
      if ( x >= 0 && y >= 0 && x < xSize && y < ySize ){
         if ( board[x][y] != -1 ) return null;
         Move m = solution.followMove(id, new Point(x,y));
         if ( m == null ) return (Move) m;
         Node[] moved = m.getMoved();
         for (int i = moved.length; i-->0;)
            if ( mobilities.getMobility(moved[i].getId()) == Mobilities.LOW )
               return null;
         /*
           ProteinSolution s = (ProteinSolution) m.doMove(solution);
         if ( s.hasOverlap() ) {
            System.out.println("bad move: " + m );
            System.out.println("original solution: " + solution);
            System.out.println("final solution: " + s);
            System.exit(0);
         }
         */
         return (Move) m;
      }
      return null;
   }

   
   // if the first space is free then it returns null.
   private Move seekFollowMove (int id, int x, int y, int dX, int dY) {
      // System.out.println("@@seek: " + x + " " + y + " " + dX + " " + dY);
      x += dX;
      y += dY;
      if ( x < 0 || y < 0 || x > xMax || y > yMax ) return null;
      if ( board[x][y] == -1 ) return null;
      // System.out.println("@@ board " + x + " " + y + " " +  board[x][y]);
      while ( board[x][y] != -1 && x > 0 && y > 0 && x < xMax && y < yMax ){
         // System.out.println("@@2 board " + x + " " + y + " " +  board[x][y]);
         x += dX;
         y += dY;
      }
      // System.out.println("@@seek2: " + x + " " + y + ": " + makeFollowMove(id,x,y));
      return makeFollowMove(id,x,y);
   }
   

   
   public void reset () {
      moves.clear();
      int size = nodes.length;
      position = 0;
      for (int i = 0; i < size; i++){
         Node node = nodes[i];
         int id = node.getId();
         if ( mobilities.getMobility(id) == Mobilities.HIGH ){
            Point loc = solution.getLocation(node);
            Move m;
            if ( searchAdjuster.diags.value ) {
               if ( (m = makeFollowMove(id,loc.x-1,loc.y-1)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x-1,loc.y+1)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x+1,loc.y-1)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x+1,loc.y+1)) != null )
                  moves.add(m);
            }
            if ( searchAdjuster.rotates.value ) {
               if ( (m = makeCarryMove(id,loc.x-1,loc.y-1,true,false)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x-1,loc.y-1,false,true)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x-1,loc.y+1,true,false)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x-1,loc.y+1,false,true)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x+1,loc.y-1,true,false)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x+1,loc.y-1,false,true)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x+1,loc.y+1,true,false)) != null )
                  moves.add(m);
               if ( (m = makeCarryMove(id,loc.x+1,loc.y+1,false,true)) != null )
                  moves.add(m);
            }
            /*
            if ( doOnes ) {
               if ( (m = makeFollowMove(id,loc.x,loc.y-1)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x,loc.y+1)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x+1,loc.y)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x-1,loc.y)) != null )
                  moves.add(m);
            }
            */
            if ( searchAdjuster.twos.value ) {
               if ( (m = makeFollowMove(id,loc.x,loc.y-2)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x,loc.y+2)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x+2,loc.y)) != null )
                  moves.add(m);
               if ( (m = makeFollowMove(id,loc.x-2,loc.y)) != null )
                  moves.add(m);
            }
            if ( searchAdjuster.seeks.value ) {
               if ( (m = seekFollowMove(id,loc.x,loc.y,-1,0)) != null )
                  moves.add(m);
               if ( (m = seekFollowMove(id,loc.x,loc.y,1,0)) != null )
                  moves.add(m);
               if ( (m = seekFollowMove(id,loc.x,loc.y,0,-1)) != null )
                  moves.add(m);
               if ( (m = seekFollowMove(id,loc.x,loc.y,0,1)) != null )
                  moves.add(m);
            }
         }
      }
   }
   
   public Move nextMove () {
      if ( position < moves.size() )
         return (Move) moves.get(position++);
      return null;
   }
}

