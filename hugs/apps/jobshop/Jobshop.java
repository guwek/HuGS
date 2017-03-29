package hugs.apps.jobshop;

import hugs.*;
import hugs.search.*;
import hugs.utils.*;
import hugs.support.*;
import java.util.*;         // StringTokenizer
import java.io.*;           // BufferedReader

public class Jobshop extends Hugs {

   public static int spanSize = 3; // need a better mechanism for controllable objective functions.
   
   private int xSize = 40; // 30
   private int ySize = 40; // 40
   private int problemSize = 300;

   {
      TabuThread.DEFAULT_MINDIV = .6;
      System.out.println("*******  setting AAAI_C_MODE *******");
      TabuThread.AAAI_C_MODE = true;
      System.out.println("JOBSHOP: setting active to true");
      SearchMgr.active = true;
   }
   
   protected Visualization makeVisualization () {
      return new JobshopVisualization ();
   }

   public MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return new MultiMoveGenerator(mobilities, solution, searchAdjuster, new JobshopMoveGeneratorMaker());
   }

   public SearchAdjuster makeSearchAdjuster () {
       return new JobshopSearchAdjuster();
   }


   protected static String defaultProblem =  "/homes/lesh/java/hugs/apps/jobshop/problems/prob4x4.jsp";
    protected Problem makeProblem (String name) {
       //if ( name == null ) return new JobshopProblem();
       // return new JobshopProblem(xSize,ySize,problemSize);
       // JobshopProblem p =  readJobshop("/homes/lesh/java/hugs/problems/LA38.demo.jobshop");
       // JobshopProblem p =  readJobshop("/homes/lesh/java/hugs/jobshop/problems/swv01.jobshop");
       JobshopProblem p =  readJobshop(defaultProblem);
       System.out.println("problem:");
       ((JobshopSolution)p.getInitSolution()).print();
       return p;
    }

   public void actionLoadSolution(String name){
      System.out.println("JOBSHOP loading solution from: " + name);
      JobshopSolution  s = (JobshopSolution) Utils.readObject(name);
      System.out.println("solution= " + s);
      super.actionLoadSolution(name);
      JobshopProblem p = s.problem;
      p.getInitSolution();
      setProblem(p,name);

   }
   
   public void actionLoadProblem(String name){

      System.out.println("JOBSHOP loading problem from: " + name);
      JobshopProblem  p = (JobshopProblem) Utils.readObject(name);
      if ( p == null ) {
         System.out.println("JOBSHOP reading text file");
         p =  readJobshop(name);
      }
      p.getInitSolution();
      setProblem(p,name);
   }

   
   private static void convert (String from, String to){
      System.out.println("Coverting jobshop from " + from + " to " + to);
      JobshopProblem p =  readJobshop(from);
      p.getInitSolution();
      Utils.writeObject(p,to);
   }

   private static void jobshopProcessArg (String[] args, int pos) {
      if ( "-critical".equals(args[pos]) ){
         System.out.println("**** setting critical to true");
         JobshopMoveGenerator.onlyMoveCritical = true;
      }
      else if ( "-setDefault".equals(args[pos]) ) {
         defaultProblem = args[pos+1];
      }
      else if ( "-nocritical".equals(args[pos]) ) {
         System.out.println("**** setting critical to false");
         JobshopMoveGenerator.onlyMoveCritical = false;
      }
      else if ( "-spanSize".equals(args[pos]) ) 
         spanSize = Utils.stringToInt(args[pos+1]);
      else if ( "-convertN".equals(args[pos]) ){
         System.out.println("Usage: -convertN <num> <fromName> <toName>");
         int num = Utils.stringToInt(args[pos+1]);
         String from = args[pos+2];
         String to = args[pos+3];
         for (int i = 0; i < num; i++)
            convert((from+i),(to+i));
         System.exit(0);
      }
      else if ( "-convert".equals(args[pos]) ){
         System.out.println("Usage: -convert  <fromName> <toName>");
         String from = args[pos+1];
         String to = args[pos+2];
         convert(from,to);
         System.exit(0);
      }
   }
    public static void main (String[] args){
                
       for (int i = 0; i < args.length; i++)
          jobshopProcessArg(args,i);
       Hugs.THIS = new Jobshop();
       setup(args);
    }

   /////////////////////////////////////////////////////////

   public static JobshopProblem readJobshop (String name) {
      BufferedReader br = null;
      // Get a schedule ready for filling:

      JobshopProblem js = new JobshopProblem ();
      String  line = "";          // Initialized to keep compiler quiet
      int     cumDuration = 0;
      int machCnt, jobCnt;    // Read once from file, used for filling history

      boolean keepTrying;     // For skipping empty or comment lines
      try
         {
            // Open the file:
            // br = new BufferedReader(new FileReader(new File (name)));
            br = Utils.myOpenFile(name);
            if ( br == null ) {
               System.out.println("file doesn't exist: " + name);
               System.exit(0);
            }
            else System.out.println("br = " + br);
            // Find first non-comment line:
            keepTrying = true;
            while (keepTrying)
               {
                  line = br.readLine();
                    if (!((line.length() == 0) ||
                          line.startsWith(";") || line.startsWith("'") ||
                        line.startsWith("#") || line.startsWith("/")))
                        keepTrying = false;
                }
                // First line has machineCount jobCount
                StringTokenizer st = new StringTokenizer(line);
                jobCnt = Integer.parseInt(st.nextToken());
                machCnt = Integer.parseInt(st.nextToken());

                // Note that this assumes a proper Jobshop problem, i.e.,
                // there are exactly as many operations on every machine
                // as there are jobs (every job must visit every machine).

                js.machineCount = machCnt;
                js.jobCount = jobCnt;
                // Now set up the arrays:
                js.machine = new int[jobCnt][machCnt];
                js.duration = new int[jobCnt][machCnt];
                js.start = new int[jobCnt][machCnt];
                js.machIdx = new int[jobCnt][machCnt];
                js.machSeq = new int[machCnt][jobCnt];
                js.mobilities = new int[jobCnt][machCnt];
                js.critPath = new int[jobCnt][machCnt];

                // Reset the operation counter:
                int opCount = 0;        // Number of operations
                cumDuration = 0;    // Cumulative duration
                // Read the next jobCount (one job per) lines of the file:
                for (int i=0; i<jobCnt; i++)
                {
                    keepTrying = true;
                    while (keepTrying)
                    {
                        line = br.readLine();
                        if (!((line.length() == 0) ||
                            line.startsWith(";") || line.startsWith("'") ||
                            line.startsWith("#") || line.startsWith("/")))
                            keepTrying = false;
                    }
                    st = new StringTokenizer(line);
                    opCount = 0;
                    while (st.hasMoreTokens())
                    {
                       // Note from neal to neal: this describes a lot. just
                       // look at sample problem.  should be creating nodes
                       // here.  should get rid of operation.class, since that
                       // is what the nodes are.
                        js.machine[i][opCount] = Integer.parseInt(st.nextToken());
                        js.duration[i][opCount] = Integer.parseInt(st.nextToken());
                        js.machIdx[i][opCount] = i;
                        js.machSeq[opCount][i] = i;
                        js.mobilities[i][opCount] = jobshopConstants.HIGHMOB;
                        opCount++;
                    }
                }
         }
            catch (Exception e)
            {
                try { br.close(); }
                catch (Exception e2) { }
            }
      return js;
   }
}

