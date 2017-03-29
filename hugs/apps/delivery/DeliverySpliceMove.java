package hugs.apps.delivery;

import hugs.*;

public class DeliverySpliceMove implements Move {

   public static boolean trace = false;
   
   private int ply;
   private int[][] splices;
   private int[][] ids;
   private DeliverySolution solution;
   private Node[] moved;

   private Node[] nodes;
   public DeliverySpliceMove (DeliverySolution solution, int ply) {
      this.ply = ply;
      splices = new int[ply][2];
      ids = new int[ply][2];
      moved = new Node[ply*2];
      this.solution = solution;
      nodes = Hugs.THIS.getProblem().getNodes();
   }

   public int getPly () { return ply; }
   public void set (int ply, int first, int target){
      splices[ply][0] = first;
      splices[ply][1] = target;
      ids[ply][0] = solution.getId(first);
      ids[ply][1] = solution.getId(target);
      moved[ply*2] = nodes[ids[ply][0]];
      moved[ply*2+1] = nodes[ids[ply][1]];
      if ( moved[ply*2] == null )
         System.out.println("moved0: " + moved[ply*2]);
      if ( moved[ply*2+1] == null )
         System.out.println("moved1: " + moved[ply*2+1]);
   }

   public Node[] getMoved () { return moved; }

   public Solution tryMove (Solution solution) {
      double delta = apply();
      if ( delta < 0 ) return solution.copy();
      undo();
      return null;
   }

    public void operateOn (Solution solution ) {
	DeliverySolution s = (DeliverySolution) solution;
	applyTo(s);
    }

   public Solution doMove (Solution solution) {
      DeliverySolution copy = (DeliverySolution) solution.copy();
      operateOn(copy);
      return copy;
   }

    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }


   public double applyTo (DeliverySolution s) {
      double delta = 0;
      for (int i = 0; i < ply; i++)
         delta += s.splice(splices[i][0],splices[i][1]);
      return delta;
   }
   
   
   public double apply () {
      double delta = 0;
      for (int i = 0; i < ply; i++)
         delta += solution.splice(splices[i][0],splices[i][1]);
      return delta;
   }

   public void undo () {
      for (int i = ply; i-- > 0; )
         solution.splice(splices[i][0],splices[i][1]);
   }

   public Move copy () {
      DeliverySpliceMove move = new DeliverySpliceMove(solution,ply);
      for (int i = 0; i < ply; i++){
         move.splices[i][0] = this.splices[i][0];
         move.splices[i][1] = this.splices[i][1];
         move.ids[i][0] = this.ids[i][0];
         move.ids[i][1] = this.ids[i][1];
         move.moved[i*2] = this.moved[i*2];
         move.moved[i*2+1] = this.moved[i*2+1];
      }
      return (Move) move;
   }

   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = 0; i < ply; i++){
         mobilities.setMobility(ids[i][0],value);
         mobilities.setMobility(ids[i][1],value);
         // System.out.println("setting " + ids[i][0] + " and " + ids[i][1] + " to " + value);
      }
   }
   
   public String toString () {
      String out = "[splice " + ply + ": ";
      for (int i = 0; i < ply; i++){
         if ( i > 0 ) out = out + ";";
         out = out +  ids[i][0] + " & " + ids[i][1];
      }
      out = out + "]";
      return out;
   }
}






