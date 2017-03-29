package hugs.support;

import hugs.*;

public interface MoveGeneratorMaker {
    
   public MoveGenerator makeGenerator (Mobilities Mobilities, Solution solution, SearchAdjuster searchAdjuster);
}

