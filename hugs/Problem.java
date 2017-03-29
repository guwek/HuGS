package hugs;

import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import java.math.*;
import java.io.*;
import java.text.*;
import hugs.utils.*;

public interface Problem extends Serializable{

   public Node[] getNodes ();
   public Solution getInitSolution();
   public int size();
    // public Solution randomSolution();

    // these should go away
    // public static void resetSize (Problem problem);
    // public static void resetSize (int x, int y);

}

