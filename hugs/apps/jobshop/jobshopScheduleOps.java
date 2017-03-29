/**
 * jobshop
 * @copy (c) 2001 Mitsubishi Electric Research Lab
 * @version 1.0
 * @author Guy T. Schafer
 */

/** Operations that can be performed on jobshopSchedule
 */

package hugs.apps.jobshop;
import hugs.utils.*;
import java.util.*;         // Vector needed
import java.math.*;         // Random needed (for testing)

public class jobshopScheduleOps
{
    // Uncallable ctor (don't need an instance):
    protected jobshopScheduleOps() { }

    public static boolean[][] getChanges(JobshopProblem problem,
					 JobshopSolution prevSched,
                                         JobshopSolution curSched)
    {
        boolean[][] changes = new boolean[problem.jobCount][problem.machineCount];
        for (int j=0; j<problem.jobCount; j++)
            for (int op=0; op<problem.machineCount; op++)
            {
                changes[j][op] = false;
                if (prevSched.machIdx[j][op] != curSched.machIdx[j][op])
                    changes[j][op] = true;
            }
        return changes;
    }
    public static JobshopProblem copy(JobshopProblem from)
    {
        JobshopProblem to = new JobshopProblem();
        to.machineCount = from.machineCount;
        to.jobCount = from.jobCount;
        to.machine = new int[from.jobCount][from.machineCount];
        to.start = new int[from.jobCount][from.machineCount];
        to.duration = new int[from.jobCount][from.machineCount];
        to.mobilities = new int[from.jobCount][from.machineCount];
        to.critPath = new int[from.jobCount][from.machineCount];
        to.machSeq = new int[from.machineCount][from.jobCount];
        to.machIdx = new int[from.jobCount][from.machineCount];
        for (int j=0; j<from.jobCount; j++)
            for (int op=0; op<from.machineCount; op++)
            {
                to.machine[j][op] = from.machine[j][op];
                to.start[j][op] = from.start[j][op];
                to.duration[j][op] = from.duration[j][op];
                to.mobilities[j][op] = from.mobilities[j][op];
                to.critPath[j][op] = from.critPath[j][op];
                to.machSeq[op][j] = from.machSeq[op][j];
                to.machIdx[j][op] = from.machIdx[j][op];
            }
        return to;
    }


   public static JobshopSolution copy(JobshopSolution from)
   {
      JobshopProblem problem = from.problem;
      int[][] start = new int[from.problem.jobCount][from.problem.machineCount];
      int[][] critPath = new int[from.problem.jobCount][from.problem.machineCount];
      int[][] machSeq = new int[from.problem.machineCount][from.problem.jobCount];
      int[][] machIdx = new int[from.problem.jobCount][from.problem.machineCount];
      for (int j=0; j<from.problem.jobCount; j++)
         for (int op=0; op<from.problem.machineCount; op++)
            {
               start[j][op] = from.start[j][op];
               critPath[j][op] = from.critPath[j][op];
               machSeq[op][j] = from.machSeq[op][j];
               machIdx[j][op] = from.machIdx[j][op];
            }
      JobshopSolution to = new JobshopSolution(problem,start,critPath,machSeq,machIdx);
      return to;
   }

    public static int getMaxspan(JobshopSolution sched)
    {
        int maxspan = 0;

        for (int j=0; j<sched.problem.jobCount; j++)
            for (int op=0; op<sched.problem.machineCount; op++)
                if (maxspan < sched.start[j][op] + sched.problem.duration[j][op])
                    maxspan = sched.start[j][op] + sched.problem.duration[j][op];

        return maxspan;
    }

    public static boolean isSameProblem(JobshopProblem schedA, JobshopProblem schedB)
    {
        // Are they the same size?
        if ((schedA.machineCount != schedB.machineCount) ||
            (schedA.jobCount != schedB.jobCount))
            return false;

        // Do they have the same order and durations?
        for (int j=0; j<schedA.jobCount; j++)
            for (int op=0; op<schedA.machineCount; op++)
                if ((schedA.duration[j][op] != schedB.duration[j][op]) ||
                    (schedA.machine[j][op] != schedB.machine[j][op]))
                    return false;
        
        return true;
    }

   public static void markCritical (JobshopSolution solution, int spanSize) {
      int size = solution.problem.machineCount;
      int lastJob = solution.problem.jobCount-1;
      int[] makespans = new int[size];
      for (int i = 0; i < size; i++) {
         JobshopNode n = solution.getNodeOnMachine(i,lastJob);
         int op = n.getOp();
         int j = n.getJob();
         makespans[i] = solution.start[j][op] + solution.problem.duration[j][op];
      }
      int[] order = Utils.sortByInt(makespans,false);
      for (int i = 0; i < spanSize; i++) {
         // System.out.print(" " + order[i]);
         JobshopNode n = solution.getNodeOnMachine(order[i],lastJob);
         int op = n.getOp();
         int j = n.getJob();
         markCritical(solution,j,op);
      }
      // System.out.println();
   }
   
   public static void markCritical (JobshopSolution sched, int job, int op) {
      int index = sched.machIdx[job][op];
      int m = sched.problem.machine[job][op];
      // System.out.println(job + " " + op + " " + m +  " " + index );
      markCritical(sched,job,op,m,index);
   }


   public static void markCritical (JobshopSolution sched, int job, int op, int machine, int index) {

      // System.out.println("op: " + op);
      sched.critPath[job][op] = jobshopConstants.CRJOB; 
      int start = sched.start[job][op];
      // previous node on job
      if ( op > 0 ) {
         int end = sched.start[job][op-1] + sched.problem.duration[job][op-1];
         if ( end >= start ) {
            markCritical(sched,job,(op-1));
	 }
      }
      // previous node on machine
      if ( index > 0 ) {
         JobshopNode n = sched.getNodeOnMachine(machine,index-1);
         int nOp = n.getOp();
         int nJob = n.getJob();
         int end = sched.start[nJob][nOp] + sched.problem.duration[nJob][nOp];
         if ( end >= start )
	     markCritical(sched,nJob,nOp,machine,(index-1));
      }
   }
   public static void criticalPath (JobshopSolution sched) {
      int numJobs = sched.problem.jobCount;
      int lastJobNum = numJobs -1;
      for (int j=0; j<numJobs; j++)
         for (int op=0; op<sched.problem.machineCount; op++)
            sched.critPath[j][op] = jobshopConstants.NOTCR;
      int lastOp = 0;
      int lastJob = 0;
      int lastTime = 0;
      for (int m=0; m<sched.problem.machineCount; m++) {
         JobshopNode n = sched.getNodeOnMachine(m,lastJobNum);
         int nOp = n.getOp();
         int nJob = n.getJob();
         int end = sched.start[nJob][nOp] + sched.problem.duration[nJob][nOp];
         if ( end > lastTime ) {
            lastOp = nOp;
            lastJob = nJob;
            lastTime = end;
         }
      }
      markCritical(sched,lastJob,lastOp);
      // markCritical(sched,Jobshop.spanSize);
   }
   
    public static int repack(JobshopSolution sched)
    {
        /* This is from Neal Lesh of MERL fame:
         * so I had a go at a straight C-to-Java conversion.  This was a snap:
        > int Jobshop::Pack(){
        > _makespan = 0;
        > const int numOps = _numJobs * _numMachs;
        >
        >
        > // Before an Operation can be placed (i.e. assigned a start time),
        > // its two predecessors (within the job and within the machine) must
        > // be placed.  Initially, reached[id] is false for all ids.  When
        > // the first predecessor for Operation id is placed, reached[id] is
        > // set to true.  When the second predecessor for Operation id is
        > // placed, id is placed on the ready list.  Operations on the ready
        > // list are immediately placeable.
        > vector<bool> reached(numOps, false);
        > list<int> ready; // id's of placeable jobs
        > int numPlaced = 0; // number of jobs that have been placed
        >
        > // Promote Operations that are first within a Job
        > for (int j = 0; j < _numJobs; ++j) reached[j * _numMachs] = true;
        >
        > // Promote Operations that are first on a Machine
        > for (int m = 0; m < _numMachs; ++m) {
        > int id = _machSeq[m][0];
        > if (reached[id]) ready.push_back(id);
        > reached[id] = true;
        > }
        >
        > // Place jobs
        > while (!ready.empty()) {
        > const int id = ready.front();
        > ready.pop_front();
        > int job = id / _numMachs;
        > int jIdx = id % _numMachs;
        > int mIdx = _machIdx[job][jIdx];
        > int mach = _operations[job][jIdx].mach();
        > // Place job
        > double start = 0.0;
        > if (jIdx > 0) {
        > double d = _operations[job][jIdx - 1].end();
        > if (d > start) {
        > _critPaths[job][jIdx] = CRJOB;
        > start = d;
        > }
        > }
        > if (mIdx > 0) {
        > int id2 = _machSeq[mach][mIdx - 1];
        > double d = operation(id2).end();
        > if (d >= start) {
        > if (d == start) _critPaths[job][jIdx] |= CRMACH; else
        _critPaths[job][jIdx] = CRMACH;
        > start = d;
        > }
        > }
        > _operations[job][jIdx].setStart(start);
        > if(_operations[job][jIdx].end() > _makespan){
        > _makespan = _operations[job][jIdx].end();
        > _lastop = id;
        > }
        > ++numPlaced;
        > // Promote job successor
        > if (jIdx < _numMachs - 1) {
        > const int id2 = id + 1;
        > if (reached[id2]) ready.push_back(id2);
        > reached[id2] = true;
        > }
        > // Promote machine successor
        > if (mIdx < _numJobs - 1) {
        > const int id2 = _machSeq[mach][mIdx + 1];
        > if (reached[id2]) ready.push_back(id2);
        > reached[id2] = true;
        > }
        > }
        >
        > // return number unplaced; a nonzero value indicates an error
        > _feasible = !(numOps - numPlaced);
        > return numOps - numPlaced;
        > }
        */
        ////////////////////////////////////////////////////////////////////
        int makespan = 0;
        int numOps = sched.problem.jobCount * sched.problem.machineCount;
        // Before an Operation can be placed (i.e. assigned a start time),
        // its two predecessors (within the job and within the machine) must
        // be placed.  Initially, reached[id] is false for all ids.  When
        // the first predecessor for Operation id is placed, reached[id] is
        // set to true.  When the second predecessor for Operation id is
        // placed, id is placed on the ready list.  Operations on the ready
        // list are immediately placeable.
        boolean[][] reached = new boolean[sched.problem.jobCount][sched.problem.machineCount];

        for (int j=0; j<sched.problem.jobCount; j++)
           for (int op=0; op<sched.problem.machineCount; op++){
                reached[j][op] = false;
                // sched.critPath[j][op] = jobshopConstants.NOTCR;
           }

        Vector ready = new Vector();        // id's of placeable jobs
        int numPlaced = 0; // number of jobs that have been placed

        // Promote Operations that are first within a Job
        for (int j=0; j<sched.problem.jobCount; j++)
            reached[j][0] = true;

        // Promote Operations that are first on a Machine
        for (int m=0; m<sched.problem.machineCount; m++)
        {
            int job = sched.machSeq[m][0];
            // Got the job #, now find the op# of first op:
            int op;
            for (op=0; op<sched.problem.machineCount; op++)
                if ((sched.problem.machine[job][op] == m) && (sched.machIdx[job][op] == 0))
                    break;          // found the first op on machine m
            if (reached[job][op])
                ready.add(new operation(job, op));
            reached[job][op] = true;
        }

        // DEBUG:
//        System.out.println("ready.size() = " + ready.size());
//        for (int i=0; i<ready.size(); i++)
//        {
//            int id = ((Integer)ready.get(i)).intValue();    // front();
            //ready.removeElementAt(0);       // pop_front();
//            int job = (int)(id / sched.machineCount);
//            int jIdx = id % sched.machineCount;
//            int mIdx = sched.machIdx[job][jIdx];
//            int mach = sched.machine[job][jIdx];
//            System.out.println("job="+job + " jIdx="+jIdx + "; mach="+mach + " mIdx="+mIdx);
//        }
//        System.exit(0);

        while (!ready.isEmpty())
        {
            int job = ((operation)ready.get(0)).job;
            int op = ((operation)ready.get(0)).op;
//            System.out.println("ready.size() = " + ready.size());
            ready.removeElementAt(0);       // pop_front();
//            System.out.println("ready.size() = " + ready.size());
            int mIdx = sched.machIdx[job][op];
            int mach = sched.problem.machine[job][op];
//            System.out.println("Placing job" + job + " op" + op);
//            System.out.println("  (machine=" + mach + ": " + mIdx+ "th op)");
            // Place job
            int start = 0;
            if (op > 0)
            {
                // Find the end of the previous operation of this job:
               int end = sched.start[job][op-1] + sched.problem.duration[job][op-1];
                // If it's the hang up, then it's critical (by job):
                if (end > start)
                {
                   sched.critPath[job][op] = jobshopConstants.CRJOB;
                   start = end;
                }
            }
            if (mIdx > 0)
            {
                int j = sched.machSeq[mach][mIdx - 1];
                // Find the end of the previous operation on this machine:
                int end = 0;
                // I know which job, but not which op.  Go through all the machines:
                for (int o=0; o<sched.problem.machineCount; o++)
                    // Looking for the operation that is on this machine:
                    if (sched.problem.machine[j][o] == mach)
                    {
                        // Found it:
                        end = sched.start[j][o] + sched.problem.duration[j][o];
                        break;
                    }
                if (end >= start)
                {
                    if (end == start)
                        sched.critPath[job][op] += jobshopConstants.CRMACH;
                    else
                        sched.critPath[job][op] = jobshopConstants.CRMACH;
                    start = end;
                }
            }
            // Set the start time:
//            System.out.println("Placing job" + job + " op" + op + " @ " + start);
            sched.start[job][op] = start;
/*          if (sched.start[job][jIdx] + sched.duration[job][jIdx] > makespan)
            {
                makespan = sched.start[job][jIdx] + sched.duration[job][jIdx];
                lastop = id;
            } */
            numPlaced++;

            // Promote job successor
            if (op < sched.problem.machineCount - 1)
            {
//                System.out.println("Next op...reached["+job+"]["+op+"]");
                op++;
                if (reached[job][op])
                    ready.add(new operation(job, op));
                reached[job][op] = true;
            }
            // Promote machine successor
            if (mIdx < sched.problem.jobCount - 1)
            {
                int j = sched.machSeq[mach][mIdx + 1];
                int o;
                // Find the previous operation on this machine:
                // I know which job, but not which op.  Go through all the machines:
                for (o=0; o<sched.problem.machineCount; o++)
                    // Looking for the operation that is on this machine:
                   if (sched.problem.machine[j][o] == mach)
                        // Found it:
                        break;      // op is now the previous op on machine mach

//                System.out.println("Next op...reached["+j+"]["+o+"]");
                if (reached[j][o])
                    ready.add(new operation(j, o));
                reached[j][o] = true;
            }
        }
        // return number unplaced; a nonzero value indicates an error
        // _feasible = !(numOps - numPlaced);
        if ( numPlaced == numOps ) 
           criticalPath(sched);
        return numOps - numPlaced;
    }

    public static String showStats(JobshopProblem sched)
    {
        // It is likely that most of the string generated here is going to
        // be truncated.  So let's put the interesting information up front.
        // That is, the spans must be sorted from high to low.
        String stats = "";
        int[] spans = new int[sched.machineCount];
        int[] machs = new int[sched.machineCount];

        for (int m=0; m<sched.machineCount; m++)
        {
            int machSpan = 0;
            for (int j=0; j<sched.jobCount; j++)
                for (int op=0; op<sched.machineCount; op++)
                    if (sched.machine[j][op] == m)
                        if (machSpan < sched.start[j][op] + sched.duration[j][op])
                            machSpan = sched.start[j][op] + sched.duration[j][op];
            spans[m] = machSpan;
            machs[m] = m;
        }
        // On the theory that a few dozen machines puts jobshop out of our reach,
        // we can do things inefficiently since the numbers can never be large
        int tmp;
        for (int m=0; m<sched.machineCount-1; m++)
            for (int k=0; k<sched.machineCount-m-1; k++)
                if (spans[k] < spans[k+1])
                {
                    tmp = spans[k];
                    spans[k] = spans[k+1];
                    spans[k+1] = tmp;
                    tmp = machs[k];
                    machs[k] = machs[k+1];
                    machs[k+1] = tmp;
                }

        for (int m=0; m<sched.machineCount; m++)
            stats += " " + machs[m] + ": " + spans[m] + " ";
        return stats;
    }
}

/*
class operation
{
    public int jobNum, opNum;
    public operation(int j, int o)
    {
        jobNum = j;
        opNum = o;
    }
}
*/
