package hugs.apps.protein;

import hugs.*;
import hugs.support.*;

public class ProteinMoveGeneratorMaker implements MoveGeneratorMaker{
    
   public MoveGenerator makeGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return (MoveGenerator) new ProteinMoveGenerator(mobilities,solution,searchAdjuster);
   }
            
}

