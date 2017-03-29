package hugs;

import java.io.*;
import java.util.*;

public interface SearchAdjuster {

   public List getInputs();
   public void precompute(Solution s); // any computation that should happen once
   // for given settings of the inputs.
}
      
