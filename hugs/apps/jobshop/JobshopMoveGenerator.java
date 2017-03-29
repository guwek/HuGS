package hugs.apps.jobshop;

import hugs.*;
import java.util.*;

public class JobshopMoveGenerator implements MoveGenerator {

   public static boolean onlyMoveCritical = true;
   private JobshopSolution solution;
   private Mobilities mobilities;
   public JobshopMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster){
      this.mobilities = mobilities;
      this.solution = (JobshopSolution) solution;
      reset();
   }

   private List moves = new ArrayList();
   private int position = 0;

   private boolean testCritical (JobshopNode n) {
      if ( !onlyMoveCritical ) return true;
      return solution.critPath[n.getJob()][n.getOp()] != jobshopConstants.NOTCR;

   }
   
   public void reset () {
      JobshopVisualization viz = (JobshopVisualization) Hugs.THIS.getVisualization();
      
      moves.clear();
      position = 0;
      JobshopProblem problem =  (JobshopProblem) Hugs.THIS.getProblem();
      Node[] nodes = problem.getNodes();
      int size = nodes.length;
      int maxX = problem.getJobCount() -1;
      boolean feasible = true;
      if ( onlyMoveCritical ) {
	 JobshopScore score = (JobshopScore) solution.getScore();
	 feasible = score.isFeasible();
	 if ( feasible )
	     jobshopScheduleOps.markCritical(solution,Jobshop.spanSize);
      }

      for (int n = size; n-->0;){
         JobshopNode jobshopNode = (JobshopNode) nodes[n];
         if ( mobilities.getMobility(n) == Mobilities.HIGH &&
              (!feasible || testCritical(jobshopNode)) ) { 
            int index = solution.getPositionOnMachine(n);
            int machine = jobshopNode.getMachine();
            // go right
            if (index < maxX ) {
               Node node2 = solution.getNodeOnMachine(machine,index+1);
               // System.out.println("right " + jobshopNode + "," + node2);
               int n2 = node2.getId();
               if ( mobilities.getMobility(n2) != Mobilities.LOW &&
                    (!feasible || testCritical((JobshopNode)node2)) ) {
                  JobshopInsertMove move = new JobshopInsertMove(solution,jobshopNode.getJob(), jobshopNode.getOp(),machine,index+1);
                  moves.add(move);
               }
            }
            // go left
            if (index > 0 ) {
               Node node2 = solution.getNodeOnMachine(machine,index-1);
               // System.out.println("left " + machine + "," + (index-1) + "--" + jobshopNode + "," + node2);
               int n2 = node2.getId();
               if ( mobilities.getMobility(n2) != Mobilities.LOW &&
                    (!feasible || testCritical((JobshopNode)node2)) ) {
                  JobshopInsertMove move = new JobshopInsertMove(solution,jobshopNode.getJob(), jobshopNode.getOp(),machine,index-1);
                  moves.add(move);
               }
            }

            
         }
      }
      if ( onlyMoveCritical && feasible)
         jobshopScheduleOps.markCritical(solution,1);
   }
   
   public Move nextMove () {
      if ( position < moves.size() )
         return (Move) moves.get(position++);
      return null;
   }

}
