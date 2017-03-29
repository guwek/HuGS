package hugs.logging;

import hugs.*;
import java.io.*;
import java.util.*;
import hugs.utils.*;

public class UserTrial implements Serializable {

   public String problemType; // e.g. delivery, hugs,
   public String problemFile; // e.g. tests/problems/deliveryShort1
   public int num; // problem num
   public int time; // timeLimit
   public String precompute; // filename for precompuation
   public String searches; // list of search algorithms
   public String comparison; // filename for comparison file
   public String user;  // user name
   public int numMgrs;
   
   public UserTrial (String problemType, String problemFile,
                     int num, int time, String precompute,
                     String searches, String comparison,
                     String user, int numMgrs) {
      this.problemType = problemType;
      this.problemFile = problemFile;
      this.num = num;
      this.time = time;
      this.precompute = precompute;
      this.searches = searches;
      this.comparison = comparison;
      this.user = user;
      this.numMgrs = numMgrs;
   }

   public String toString () {
      return "[UserTrial " + user + "," + problemType + "," +  problemFile + ","
         + num + ","
         + time + "," +   precompute + ","
         + numMgrs + ","
         +   searches + "," +   comparison + "]";
   }
      
   public void print () {
      System.out.println("user: " + user);
      System.out.println("type: " + problemType);
      System.out.println("problem: " + problemFile);
      System.out.println("time limit: " + time);
      System.out.println("numMgrs: " + numMgrs);
      System.out.println("search algs: " + searches);
      System.out.println("comparison file: " + comparison);
   }

}
      
