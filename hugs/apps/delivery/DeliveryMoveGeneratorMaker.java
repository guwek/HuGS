package hugs.apps.delivery;

import hugs.*;
import hugs.support.*;

public class DeliveryMoveGeneratorMaker implements MoveGeneratorMaker{
    
   public MoveGenerator makeGenerator (Mobilities mobilities, Solution solution, SearchAdjuster searchAdjuster) {
      return (MoveGenerator) new DeliveryMoveGenerator(mobilities,solution,searchAdjuster);
   }
            
}

