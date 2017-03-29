package hugs.apps.crossing;

import hugs.*;
import hugs.utils.*;
import hugs.search.*;
import hugs.support.*;

public class Crossing extends Hugs {

   public static int X_SIZE = 3;
   public static int Y_SIZE = 8;
   {
      TabuThread.DEFAULT_MINDIV = .8;
      Hugs.FAST_MODE = true;
   }
   protected Visualization makeVisualization () {
      return new CrossingVisualization (X_SIZE, Y_SIZE);
   }

   public MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return new MultiMoveGenerator(mobilities, solution, searchAdjuster, new CrossingMoveGeneratorMaker());
   }

   public SearchAdjuster makeSearchAdjuster () {
      return new CrossingSearchAdjuster();
   }

   /*
   protected MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution) {
      return new CrossingMoveGenerator(mobilities,solution);
   }
   */
   public static void crossingProcessArg (String args[], int pos) {
      if (args[pos].equals("-size")) {
         X_SIZE = Utils.stringToInt(args[pos+1]);
         Y_SIZE = Utils.stringToInt(args[pos+2]);
      }
      else if (args[pos].equals("-ago")) {
         CrossingProblem.fakeAgo = true;
      }
      else if (args[pos].equals("-noVaryNodeSize")) {
         CrossingVisualization.varyNodeSize = false;
      }
      else if (args[pos].equals("-CHI")) {
         CrossingVisualization.varyNodeSize = false;
         Hugs.showChanges = false;
         X_SIZE = 12;
         Y_SIZE = 8;
      }
      else if (args[pos].equals("-smallDemo")) {
         X_SIZE = 12;
         Y_SIZE = 8;
      }
      
   }
         
    protected Problem makeProblem (String name) {
	if ( name == null ) return new CrossingProblem(X_SIZE,Y_SIZE);
	return new CrossingProblem(name,X_SIZE,Y_SIZE);
    }


    public static void main (String[] args){
	Hugs.THIS = new Crossing();
        for (int i = 0; i < args.length; i++)
           crossingProcessArg(args,i);
	setup(args);
        CrossingProblem p = (CrossingProblem) Hugs.THIS.getProblem();
        System.out.println("num edges: " +  p.getEdges().size());
    }
}

