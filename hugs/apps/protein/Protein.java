package hugs.apps.protein;

import hugs.*;
import hugs.search.*;
import hugs.support.*;
import hugs.utils.*;

public class Protein extends Hugs {

   private static int xSize = 25;
   private static int ySize = 25;
   private static int problemSize = 80;

   // Human-Readable I/O

   public Solution readSolution (String name) {
      return ProteinSolution.read(name);
   }

   public void writeSolution (Solution solution, String name) {
      ProteinSolution.write((ProteinSolution) solution,name);
   }

   public Problem readProblem (String name){
      ProteinProblem p =  ProteinProblem.read(name);
      if ( p != null ) problemSize = p.size();
      return p;
   }

   public void writeProblem (Problem problem, String name) {
      ProteinProblem.write((ProteinProblem) problem,name);
   }


   {
      TabuThread.DEFAULT_MINDIV = .4;
      TabuThread.DEFAULT_MEMORY = 5;
      System.out.println("*******  setting AAAI_C_MODE *******");
      TabuThread.AAAI_C_MODE = true;
      // System.out.println("PROTEIN: setting active to true");
      // SearchMgr.active = true;
   }
   

   protected Visualization makeVisualization () {
      return new ProteinVisualization (xSize,ySize);
   }

   public MoveGenerator makeMoveGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return new MultiMoveGenerator(mobilities, solution, searchAdjuster, new ProteinMoveGeneratorMaker());
   }

   public SearchAdjuster makeSearchAdjuster () {
      return new ProteinSearchAdjuster();
   }

   protected Problem makeProblem (String name) {
      return new ProteinProblem(xSize,ySize,problemSize);
   }

   public static void proteinProcessArg (String args[], int pos) {
      if (args[pos].equals("-size")) {
         problemSize = Utils.stringToInt(args[pos+1]);
         System.out.println("problemSize = " + problemSize);
      }
      if (args[pos].equals("-board")) {
         xSize = Utils.stringToInt(args[pos+1]);
         ySize = Utils.stringToInt(args[pos+2]);
      }
      else if (args[pos].equals("-CHI")) {
         xSize = 20;
         ySize = 20;
         problemSize = 60;
      }
      else if (args[pos].equals("-smallDemo")) {
         xSize = 20;
         ySize = 20;
         problemSize = 60;
      }
      else if (args[pos].equals("-doDiags")) {
         ProteinSearchAdjuster.doDiag = true;
      }
      else if (args[pos].equals("-noDiags")) {
         ProteinSearchAdjuster.doDiag = false;
      }
      /*
      else if (args[pos].equals("-doOnes")) {
         ProteinSearchAdjuster.doOnes = true;
      }
      else if (args[pos].equals("-noOnes")) {
         ProteinSearchAdjuster.doOnes = false;
      }
      */
      else if (args[pos].equals("-doRotates")) {
         ProteinSearchAdjuster.doRotates = true;
      }
      else if (args[pos].equals("-noRotates")) {
         ProteinSearchAdjuster.doRotates = false;
      }
      else if (args[pos].equals("-doTwos")) {
         ProteinSearchAdjuster.doTwos = true;
      }
      else if (args[pos].equals("-noTwos")) {
         ProteinSearchAdjuster.doTwos = false;
      }
      else if (args[pos].equals("-doSeeks")) {
         ProteinSearchAdjuster.doSeeks = true;
      }
      else if (args[pos].equals("-noSeeks")) {
         ProteinSearchAdjuster.doSeeks = false;
      }
   }

   
    public static void main (String[] args){
       {
          // System.out.println("******* WARNINg: checking for overlap in ProteinSolution....is expensive.");
          System.out.println("******* WARNINg: not checking for overlap in ProteinSolution, but thre may be a bug");
          // System.out.println("******* WARNING: doing experimental stuff in ProteinRepositionMove!!!!");
       }
       for (int i = 0; i < args.length; i++)
          proteinProcessArg(args,i);
       System.out.println("doDiag= " + ProteinSearchAdjuster.doDiag  +
                          ", doTwos= " + ProteinSearchAdjuster.doTwos +
                          ", doRotates= " + ProteinSearchAdjuster.doRotates +
                          ", doSeeks " + ProteinSearchAdjuster.doSeeks);  
       Hugs.THIS = new Protein();
       setup(args);
    }
}

