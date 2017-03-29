package hugs.logging;

/* notes
- maybe make Logger class which maintains the Log class. it can have pointers
  to the Hugs.
- problem change SearchTrace so that it doesn't store the problem.

 */

import java.util.*;
import java.io.*;
import hugs.Score;
import hugs.Solution;
import hugs.utils.*;

public class Log implements Serializable {

   private String fileName;
   private String textFileName;
   private Score startScore;
   private Date startTime;
   private int startSeconds;
   private List events = new ArrayList();
   private List inputs;
   private boolean logOpen = true; // set to false when experiment is over.

   private Score bestScore; // special thing, can be updated with best score
   // found so far.  is designed essentially as an ad-hoc backup, in case
   // something slips by the other functions.  is not updated after log is
   // closed
   
   public Log (String fileName,
               String textFileName,
               Score startScore,
               List inputs){
      this.fileName = fileName;
      this.textFileName = textFileName;
      this.startScore = startScore;
      this.startTime = Calendar.getInstance().getTime();
      this.startSeconds = Utils.getSeconds();
      this.inputs = inputs;
      bestScore = startScore;
   }

   public void save () {
      Utils.writeObject(this,fileName);
   }

   public void openTextFile (PrintWriter p){
      p.println("STARTLOG");
      p.println("INITLOG fileName " +  fileName);
      p.println("INITLOG textFileName " +  textFileName);
      p.println("INITLOG startScoreDouble " +  startScore.toDouble());
      p.println("INITLOG startScore " +  startScore);
      p.println("INITLOG startTime " +  startTime);
      printInputs(p);
   }

   /*
   public void updateBestScore (Score score, PrintWriter p) {
      if ( score != null && logOpen )
         if ( score.isBetter(bestScore) ) {
            bestScore = score;
            printBestScore(p);
         }
   }
   */
   
   private void printInputs (PrintWriter p){
      if ( inputs == null ) return;
      for (Iterator i = inputs.iterator(); i.hasNext();)
         p.println("INPUT " + i.next());
   }
   
   private void printBestScore (PrintWriter p){
      p.println("LOGBestScore " + bestScore);
   }

   public void closeTextFile (PrintWriter p){
      logOpen = false;
      p.println("CLOSELOG");
      printBestScore(p);
   }
   
   public void add (LogEvent event) {
      events.add(event);
      Solution s = event.getEndSolution();
      if ( s != null && logOpen ) {
         Score score = s.getScore();
         if ( score.isBetter(bestScore) ) {
            bestScore = score;
         }
      }
   }

   public List getInputs () { return inputs; }
   public List getEvents () { return events; }
   public int getStartSeconds () { return startSeconds; }
   public Score getBestScore () { return bestScore; }
   
}
