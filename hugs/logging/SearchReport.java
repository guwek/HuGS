package hugs.logging;

import hugs.*;
import java.util.*;
import java.io.*;

public class SearchReport implements Serializable {

   private int time;
   private Score score;
   private List outputs;

   public SearchReport (int time, Score score, List outputs){
      this.time = time;
      this.score = score.copy();
      this.outputs = new ArrayList(outputs.size());
      for (Iterator i = outputs.iterator(); i.hasNext();)
         this.outputs.add(((Parameter)i.next()).copy());
   }

   public int getTime () { return time; }
   public Score getScore () { return score; }
   public List getOutputs () { return outputs; }

   public String toString () {
      return "[SearchReport: time= " + time + "; score= " + score + "; outputs= " + outputs + "]";
   }
}
