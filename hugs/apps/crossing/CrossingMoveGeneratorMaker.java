package hugs.apps.crossing;

import hugs.*;
import hugs.support.*;

public class CrossingMoveGeneratorMaker implements MoveGeneratorMaker{
    
   public MoveGenerator makeGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return (MoveGenerator) new CrossingMoveGenerator(mobilities,solution,searchAdjuster);
   }
            
}

