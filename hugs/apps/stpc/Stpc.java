package hugs.apps.stpc;

import hugs.*;
import hugs.search.*;
import hugs.support.*;

public class Stpc extends Hugs {

   private static int xSize = 100; // 30
   private static int ySize = 48; // 40
   private static int problemSize = 300;
   
   private static String instance = null;

   {
      TabuThread.DEFAULT_MINDIV = .4;
   }
   
   protected Visualization makeVisualization () {
      return new StpcVisualization (xSize,ySize);
   }

   public SearchAdjuster makeSearchAdjuster () {
      return new StpcSearchAdjuster();
   }

   public MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      // return new DeliveryMoveGenerator(mobilities,solution);
      return new MultiMoveGenerator(mobilities, solution, searchAdjuster, new StpcMoveGeneratorMaker());
   }

    protected Problem makeProblem (String name) {
    	if (instance == null)
	      return new StpcProblem(xSize, ySize, problemSize, 3);
	   else
	   	return StpcProblem.read(instance);
    }

   public static void stpcProcessArg (String args[], int pos)
   {
		if (args[pos].equals("-rand") || args[pos].equals("-r"))
			hugs.utils.Utils.setSeed((int) System.currentTimeMillis());
   }
   
    public static void main (String[] args){
       Hugs.THIS = new Stpc();
       for (int i = 0; i < args.length; i++)
          stpcProcessArg(args,i);
       setup(args);
    }


   public void writeProblem (Problem problem, String name) {
	StpcProblem.write((StpcProblem) problem,name);
   }

   public Problem readProblem (String name){
      StpcProblem p =  StpcProblem.read(name);
      return p;
   }



}

