package hugs.apps.protein;

import hugs.*;
import java.util.*;
import java.awt.Point;

public class ProteinRepositionMove implements Move {

   public static boolean trace = false;
   private ProteinSolution solution;
   private Node[] moved;
   private Point[] destinations;
   private Node[] nodes;

   public ProteinRepositionMove (){
      nodes = Hugs.THIS.getProblem().getNodes();
   }
   public ProteinRepositionMove (ProteinSolution solution, int id, int x, int y) {
      this();
      moved = new Node[1];
      moved[0] = nodes[id];
      destinations = new Point[1];
      destinations[0] = new Point(x,y);
      this.solution = solution;
   }

   public ProteinRepositionMove (ProteinSolution solution, Node[] moved, Point[] destinations){
      this();
      this.solution = solution;
      /*
      // EXPERIMENTAL!!!!
      if ( moved.length > 2 ) {
	  this.moved = new Node[2];
	  this.moved[0] = moved[0];
	  this.moved[1] = moved[moved.length-1];
      }
      else this.moved = moved;
      */
      this.moved = moved;


      this.destinations = destinations;
   }

   public int getPly () { return moved.length; }
   
   public Node[] getMoved () { return moved; }

   public Solution tryMove (Solution current){
    Solution trial = doMove(current);
    trial.computeScore();
    if ( trial.getScore().isBetter(current.getScore()) )
      return trial;
    return null;
  }
   
    public void operateOn (Solution solution ) {
	ProteinSolution s = (ProteinSolution) solution;
	for (int i = 0; i < moved.length; i++)
	    s.setLocation(moved[i].getId(),destinations[i]);
	
    }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }

   public Solution doMove (Solution solution) {
      ProteinSolution copy = (ProteinSolution) solution.copy();
      operateOn(copy);
      copy.computeScore();
      return copy;
   }


   /*
   public Move copy () {
      ProteinRepositionMove move = new ProteinRepositionMove(solution,moved,destinations);
      return (Move) move;
   }
   */
   
   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public String toString () {
      String s = "[reposition ";
      for (int i = 0; i < moved.length; i++) {
         if ( i > 0 ) s+= ";";
         s += + moved[i].getId() + " to " + destinations[i];
      }
      return s;
   }
}






