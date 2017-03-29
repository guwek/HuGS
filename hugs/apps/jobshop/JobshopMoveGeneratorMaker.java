package hugs.apps.jobshop;

import hugs.*;
import hugs.support.*;

public class JobshopMoveGeneratorMaker implements MoveGeneratorMaker{
    
   public MoveGenerator makeGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return (MoveGenerator) new JobshopMoveGenerator(mobilities,solution,searchAdjuster);
   }
            
}

