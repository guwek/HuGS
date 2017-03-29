package hugs.apps.delivery;

import hugs.*;
import hugs.search.*;
import hugs.support.*;

public class Delivery extends Hugs {

   private static int xSize = 40; // 30
   private static int ySize = 40; // 40
   private static int problemSize = 300;

   {
      TabuThread.DEFAULT_MINDIV = .4;

   }
   protected Visualization makeVisualization () {
      return new DeliveryVisualization (xSize,ySize);
   }

   public SearchAdjuster makeSearchAdjuster () {
      return new DeliverySearchAdjuster();
   }

   public MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      // return new DeliveryMoveGenerator(mobilities,solution);
      return new MultiMoveGenerator(mobilities, solution, searchAdjuster, new DeliveryMoveGeneratorMaker());
   }

    protected Problem makeProblem (String name) {
       //if ( name == null ) return new DeliveryProblem();
       return new DeliveryProblem(xSize,ySize,problemSize);
    }

   public static void deliveryProcessArg (String args[], int pos) {
      if (args[pos].equals("-CHI")) {
         xSize = 40;
         ySize = 30;
         problemSize = 300;
         DeliverySolution.TIME_PER_DISTANCE = .004;
         DeliveryVisualization.aspectRatio =  2.15;
         DeliveryMoveGenerator.MAX_SPLICE_WINDOW = 1;
      }
      else if (args[pos].equals("-smallDemo")) {
         xSize = 40;
         ySize = 30;
         problemSize = 300;
         DeliverySolution.TIME_PER_DISTANCE = .004;
         DeliveryVisualization.aspectRatio =  2.15;
         DeliveryMoveGenerator.MAX_SPLICE_WINDOW = 1;
      }
   }
   
    public static void main (String[] args){
       Hugs.THIS = new Delivery();
       System.out.println("****** AFTER AAAI, take out FAST_MODE & probably SearchMgr.active*******");
       for (int i = 0; i < args.length; i++)
          deliveryProcessArg(args,i);
       setup(args);
    }


   public void writeProblem (Problem problem, String name) {
       DeliveryProblem.write((DeliveryProblem) problem,name);
   }

   public Problem readProblem (String name){
      DeliveryProblem p =  DeliveryProblem.read(name);
      return p;
   }



}

