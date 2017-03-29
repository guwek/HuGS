package hugs.search;

import hugs.*;
import hugs.support.*;
import hugs.utils.*;
import java.util.*;

public class TabuThread extends SearchThread {
   
    public static boolean AAAI_C_MODE = false; // take out after AAAI
    public static int DEFAULT_MEMORY = 10;
    public static double DEFAULT_MINDIV = .6;
    public static double DEFAULT_NOISE = 0.0;

    public static String name = "tabu";
    public String getSearchName () { return name;}
    private final static double MIN_IMPROVEMENT = -.001; // maybe there is floating point
    // errors that make things look like improvements.
   
   
    private DoubleParameter minDiversityP = new DoubleParameter("minD");
    private DoubleParameter noiseP = new DoubleParameter("noise");
    private IntParameter maxPlyP = new IntParameter("maxPly");
    private IntParameter memorySizeP = new IntParameter("memory");
    private int memorySize; // can decrease as search progresses
    private BooleanParameter greedyP = new BooleanParameter("greedy");
   
    private ScoreParameter currentP = new ScoreParameter("current");
    private IntParameter iterationP = new IntParameter("iter");
    private DoubleParameter aveHighP = new DoubleParameter("high");
    private DoubleParameter diversityP = new DoubleParameter("div");

    public TabuThread () {
	this(null,1,DEFAULT_MEMORY,false,
             DEFAULT_MINDIV,DEFAULT_NOISE); // defaults
    }
    
    public TabuThread (Problem problem,
		       int maxPly,
		       int memorySize,
		       boolean greedy,
		       double minDiversity,
		       double noiseLevel){
	super(problem);
	this.maxPlyP.value = maxPly;
	this.memorySizeP.value = memorySize;
	this.greedyP.value = greedy;
	this.minDiversityP.value = minDiversity;
	this.noiseP.value = noiseLevel;
        inputs.add(greedyP);
        inputs.add(maxPlyP);
        inputs.add(memorySizeP);
        inputs.add(minDiversityP);
        inputs.add(noiseP);
        
        outputs.add(iterationP);
	outputs.add(currentP);
	outputs.add(diversityP);
        outputs.add(aveHighP);
                
    }

    private double cumHigh;
    private double count;
    private Mobilities originalMobilities;
    private int blocked[];
    private Move memory[];
    private int position;
    private Score bestCost;
    private Diversity diversity;
    private Noise noise;

    public void initSearch (Solution s, Mobilities mobilities, List marked){
       this.marked = marked;
       this.solution = s;
       this.memorySize = memorySizeP.value;
       // this.mobilities = (Mobilities) mobilities.clone();
       System.out.println("Tabu: MinDiversity = " + minDiversityP.value + ", greedy = " + greedyP.value);
       cumHigh = 0;
       count = 0;
       originalMobilities = (Mobilities) mobilities.clone();
       bestSolution = solution.copy();
       blocked = new int[problem.size()];
       int max = 99999999;
       for (int i = problem.size(); i-->0;)
          if ( mobilities.getMobility(i) != Mobilities.HIGH )
             blocked[i] = max;
          else blocked[i] = 0;
       memory = new Move[memorySize];
       if (memorySize > 0)
          memory[0] = null;
       position = 0;
       bestCost = solution.computeScore(searchAdjuster,marked);
       diversity = new Diversity(mobilities,minDiversityP.value);
       noise = new Noise(mobilities,noiseP.value);
       iterationP.value = 0;
       currentP.value = null;
       currentSolution = solution;
       currentMobilities = mobilities;
       diversityP.value = 0;
       aveHighP.value = 0;
    }

    public void searchIteration () {
       
       if ( memorySize > 0 )
          if ( memory[position] != null ){
             Node[]  moved = memory[position].getMoved();
             for (int j = moved.length; j-->0;)
                if ( blocked[moved[j].getId()] <= iterationP.value )
                   mobilities.setMobility(moved[j].getId(),Mobilities.HIGH);
          }
       count++;
       diversity.freeze();
       noise.freeze();
       cumHigh += mobilities.getCount(Mobilities.HIGH);
       // System.out.println("about to search; " + solution + ", " + mobilities);
       // System.out.println("about to search; " + maxPlyP.value + " " + greedyP.value);

       Move bestMove = steepest(solution,mobilities,maxPlyP.value,greedyP.value);
       // steepest(solution,mobilities,maxPlyP.value,greedyP.value);
       // System.out.println("tabu bestMove: " + bestMove);
       boolean reset = false;
       if ( bestMove == null ) {
          if ( memorySize > 1 ) {
	      memorySize--;
	      // System.out.println(iterationP.value + " bestMove = null, setting memory to " + memorySize);
	  }
          reset = true;
          // halt = true;
          //          if ( searchMgr != null ) 
          // searchMgr.updateSearchMessage("Search over b/c no move found");
          // return;
       }
       else {
          diversity.registerMove(bestMove.getMoved());
          diversityP.value = diversity.aveDiversity(); // can change later in reset()
          solution = Utils.doMove(bestMove,solution);
       }
       // if ( solution != null ) System.out.println("tabu: " + solution.getScore());
       considerSolution(solution);
       // System.out.println("BestMove: " + bestMove + "  " + mobilities);
       // solution.print();

          // BUG: not sure if computeScore is needed
       boolean newBestScore = solution.computeScore(searchAdjuster,marked).isBetter(bestCost,searchAdjuster);
       // System.out.println("tabu new score: " + solution.getScore());
       if ( reset || newBestScore ) {
          // System.out.println("startSearch: new best: " + solution.getScore());
          // System.out.println("New best: " + bestMove + ", cost= " + bestCost);
          // bestSolution.print();
          clearMemory(memory,mobilities,newBestScore);
	  if ( newBestScore ) {
	      bestCost = solution.getScore();
	      bestSolution = solution.copy();
	      bestSolution.computeScore(searchAdjuster,marked);
	  }
          diversity.unfreeze();
          noise.unfreeze();
          diversity.reset();
          if ( searchMgr != null )
             searchMgr.updateSearchMessage();
          // position = 0;
       }
       else{
          if ( memorySize > 0 )
             memory[position] = bestMove;
          Node[]  moved = bestMove.getMoved();
          for (int j = moved.length; j-->0;){
             int id = moved[j].getId();
             // System.out.println("setting " + id + " to " + " medium " + " from " + mobilities.getMobility(id));
             if ( mobilities.getMobility(id) == Mobilities.HIGH ){
                mobilities.setMobility(id,Mobilities.MED);
                blocked[moved[j].getId()] = iterationP.value + memorySize;
             }
          }
          position++;
          if ( position == memorySize ) position = 0;
       }
       if ( mode != SearchMgr.MODE_RUN )
          currentMobilities = (Mobilities) mobilities.clone();
       diversity.unfreeze();
       noise.unfreeze();
       aveHighP.value = (cumHigh/count);
       currentP.value = solution.getScore();
       currentSolution = solution;
       iterationP.value ++;
    }


    ///////////////////////////////////

   private void setMovedMobilitiesTo(Mobilities mobilities, Move move, int value) {
      Node[] moved = move.getMoved();
      for (int i = moved.length; i-->0;)
         mobilities.setMobility(moved[i].getId(),value);
   }
   public void clearMemory (Move[] memory, Mobilities mobilities, boolean newBest) {
      for (int i = memory.length; i--> 0;){
         if ( memory[i] != null )
            //memory[i].setMovedMobilitiesTo(mobilities,Mobilities.HIGH);
            setMovedMobilitiesTo(mobilities,memory[i],Mobilities.HIGH);
         memory[i] = null;
      }
      if ( AAAI_C_MODE ) {
	  int max = 99999999;
          if (newBest) this.memorySize = memorySizeP.value;
	  for (int i = problem.size(); i-->0;)
	      if ( blocked[i] > 0 )
		  blocked[i] = max;
	  if (memorySize > 0)
	      memory[0] = null;
      }
      position = 0;
   }

   private Move steepest (Solution solution, Mobilities mobilities, int maxPly, boolean greedy){
      Score score = solution.computeScore(searchAdjuster,marked);
      if ( mobilities.getCount(Mobilities.HIGH) == 0){
	  if ( searchMgr != null )
	      searchMgr.updateSearchMessage("No live nodes -- widen search or increase tabuMemory");
	  return null;
      }
      MoveGenerator mover = Hugs.THIS.makeMoveGenerator(mobilities,solution,searchAdjuster);
      boolean steepHalt = false;
      Move bestMove = null;
      Solution bestSolution = null;
      Score bestScore = null;
      int count = 1;
      int currentPly = 0;
      solution.precompute();
      while (!steepHalt && !halt){
         Move move = mover.nextMove();
         count++;
         if (move == null) steepHalt = true;
         else{
            currentPly = move.getPly();
            if (currentPly > maxPly ) steepHalt = true;
            else {
		/// Solution newSolution = move.doMove(solution);
		Score newScore = move.evaluate(solution,bestScore,searchAdjuster,marked);
		/// if (newSolution != null){
		/// newSolution.computeScore(searchAdjuster,marked);
		if ( newScore != null ) {
		    if (bestScore == null || newScore.isBetter(bestScore,searchAdjuster)){
			bestScore = newScore;
			/// if ( bestScore != null)
			/// bestSolution = newSolution;
			bestMove = move;
			/// bestScore = newSolution.getScore();
			if ( greedy && bestScore.isBetter(score,searchAdjuster) )  {
			    steepHalt = true;
			}
		    }
		}
            }
         }
      }
      return bestMove;
   }

   // overriden in precomputation extention
   protected void considerSolution (Solution s){
   }
}




