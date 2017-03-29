package hugs;

import java.io.*;

public interface Score extends Serializable{

   public boolean isBetter (Score score);
   public boolean isBetter (Score score, SearchAdjuster searchAdjuster);
   public String comparisonString (Score score);
   public Score copy ();
   public double toDouble (); // used for comparing experiments
}
      
