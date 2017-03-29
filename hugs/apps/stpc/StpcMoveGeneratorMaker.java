package hugs.apps.stpc;

import hugs.*;
import hugs.support.*;

public class StpcMoveGeneratorMaker implements MoveGeneratorMaker{
    
   public MoveGenerator makeGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return (MoveGenerator) new StpcMoveGenerator(mobilities,solution,searchAdjuster);
   }
            
}

