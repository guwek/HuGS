package hugs.apps.jobshop;

import hugs.*;

public class JobshopInsertMove implements Move {

   public static boolean trace = false;
   private JobshopSolution solution;
   private Node[] moved;
   private int insertJob;
   private int insertOp;
   private int insertMach;
   private int insertTarget;
   private Node[] nodes;

   
   public JobshopInsertMove (JobshopSolution solution, int insertJob, int insertOp,
                         int insertMach, int insertTarget ) {
      this.insertJob = insertJob;
      this.insertOp = insertOp;
      this.insertMach = insertMach;
      this.insertTarget = insertTarget;
      this.solution = solution;
      JobshopProblem problem = (JobshopProblem) Hugs.THIS.getProblem();
      nodes = problem.getNodes();
      int currentLoc = solution.machIdx[insertJob][insertOp];
      int delta = Math.abs(insertTarget-currentLoc);
      int start = Math.min(currentLoc,insertTarget);
      int machine = problem.machine[insertJob][insertOp];
      moved = new Node[delta+1];
      for (int i = 0; i <= delta; i++)
         moved[i] = solution.getNodeOnMachine(machine,start+i);
      // System.out.println("moved: " + java.util.Arrays.asList(moved));
   }

   public int getPly () { return 1; }
   
   public Node[] getMoved () { return moved; }

   public Solution tryMove (Solution current){
    Solution trial = doMove(current);
    trial.computeScore();
    if ( trial.getScore().isBetter(current.getScore()) )
      return trial;
    return null;
  }
   
    public void operateOn (Solution solution ) {
	JobshopSolution s = (JobshopSolution) solution;
	s.insert(insertJob, insertOp, insertMach, insertTarget);
    }
   
    public Score evaluate (Solution solution, Score score, 
                           SearchAdjuster searchAdjuster, java.util.List marked) {
	Solution s = hugs.utils.Utils.doMove(this,solution);
	if ( s == null ) return null;
	return s.getScore();
    }

   public Solution doMove (Solution solution) {
      JobshopSolution copy = (JobshopSolution) solution.copy();
      operateOn(copy);
      copy.computeScore();
      return copy;
   }


   public Move copy () {
      JobshopInsertMove move = new JobshopInsertMove(solution,insertJob, insertOp, insertMach, insertTarget);
      return (Move) move;
   }

   public void setMovedMobilitiesTo (Mobilities mobilities, int value){
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   
   public String toString () {
      return "[insert " + insertJob + "," + insertOp + " on machine " + insertMach + " into position " + insertTarget + "]";
   }
}






