
package hugs.utils;

import java.math.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Point;
import hugs.*;
import hugs.utils.*;

public class Utils {

   public static Object newInstance (Class cls) {
      try { return cls.newInstance(); }
      catch (Exception e) {
         System.out.println( "Error during newInstance for: "+cls + ": " + e);
      }
      return null;
   }

   
   public static List getArgsUntilDash (String[] args, int index) {
      List paramNames = new ArrayList();
      while (index < args.length && !args[index].startsWith("-") )
         paramNames.add(args[index++]);
      return paramNames;
   }
   
    public static int[] sortByDoubles(double[] values, boolean minFirst) {
	int size = values.length;
	DoubleIntPair[] pairs = new DoubleIntPair[size];
	for (int i =0; i < size; i++)
	    pairs[i] = new DoubleIntPair(values[i],i);
	Arrays.sort(pairs,new DoubleIntComparator(minFirst));
	int[] order = new int[size];
	for (int i =0; i < size; i++)
	    order[i] = pairs[i].i;
	return order;
    }

      public static int[] sortByInt(int[] values, int[] tieBreak, boolean minFirst) {
	int size = values.length;
	Triple[] triples = new Triple[size];
	for (int i =0; i < size; i++)
	    triples[i] = new Triple(values[i],tieBreak[i],i);
	Arrays.sort(triples,new TripleXComparator(minFirst));
	int[] order = new int[size];
	for (int i =0; i < size; i++)
           order[i] = triples[i].z;
	return order;
   }

   
   public static int[] sortByInt(int[] values, boolean minFirst) {
	int size = values.length;
	Point[] pairs = new Point[size];
	for (int i =0; i < size; i++)
	    pairs[i] = new Point(values[i],i);
	Arrays.sort(pairs,new PairXComparator(minFirst));
	int[] order = new int[size];
	for (int i =0; i < size; i++)
	    order[i] = pairs[i].y;
	return order;
    }

   public static String readLineIf (BufferedReader reader) {
      try {
         return reader.readLine();
      } catch (Exception e) {
      }
      return null;
   }
public static BufferedReader myOpenFile(String fileName) {
      BufferedReader r = null;
      try {
         r = new BufferedReader(new FileReader(fileName));
      } catch (FileNotFoundException e) {
         return null;
      }
      return r;
   }

   public static int[] randomize (int[] nums) {
      boolean[] used = new boolean[nums.length];
      int[] output = new int[nums.length];
      for (int i = nums.length; i-->0;){
         int pos = 0;
         int max = i + 1;
         int pick = randomInt(max);
         int count = 0;
         while ( count < pick) {
            while ( used[pos] ) pos++;
            pos++;
            count++;
         }
         while ( used[pos] ) pos++;
         output[i] = nums[pos];
         used[pos] = true;
      }
      return output;
   }
   public static String getValue (String key, String[] strings){
      if ( key == null ) return null;
      for (int i = (strings.length-1); i-->0;)
         if ( key.equals(strings[i]) )
            return strings[i+1];
      return null;
   }
   
   public static void myCloseFile(java.io.Reader r) {
    try {
      r.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

   public static NumberFormat nf =  NumberFormat.getInstance();

   public static int stringToInt (String s){
      return Integer.valueOf(s).intValue();
   }

   public static double stringToDouble (String s){
      return Double.valueOf(s).doubleValue();
   }

   public static String objectToNumberToString(Object o, int digits){
      if ( o instanceof Double ) return doubleToString(((Double)o).doubleValue());
      return o.toString();
   }
   
   public static String doubleToString(double d){
      return doubleToString(d,3);
   }
   
   public static String doubleToString(double d, int digits){
      nf.setMaximumFractionDigits(digits);
      nf.setMinimumFractionDigits(digits);
      nf.setGroupingUsed(false);
      return nf.format(d);
   }

   public static int getSeconds () {
      return (int) Calendar.getInstance().getTime().getTime()/1000;
   }

   public static void writeObject (Serializable o, String name){

         FileOutputStream ostream = null;
         ObjectOutputStream p = null;
         try{
            ostream = new FileOutputStream(name);
         } catch (Exception e) { System.out.println("Utils:can't open file " + name);}
         try{
            p = new ObjectOutputStream(ostream);
         } catch (Exception e) { System.out.println("Utils: object output");}
         try{
            p.writeObject(o);
         } catch (Exception e) { System.out.println("Utils:can't write object" + e);}
         try{
            p.flush();
         } catch (Exception e) { System.out.println("Utils:can't flush");}
         try{
            ostream.close();
         } catch (Exception e) { System.out.println("Utils: can't close");}
   }


   public static void makeFile (String fileName, List list) {
      PrintWriter w = openPrintWriter(fileName);
      if ( w == null ) return;
      for (Iterator i = list.iterator(); i.hasNext();)
         w.write(i.next() + "\n");
      w.close();
   }

   public static void makeFile (String fileName, String content) {
      PrintWriter w = openPrintWriter(fileName);
      if ( w == null ) return;
      w.write(content + "\n");
      w.close();
   }
   
   public static PrintWriter openPrintWriter (String name) {
      PrintWriter writer;
      try {
         writer = new PrintWriter (new FileWriter(new File(name)),true);
      }
      catch ( Exception e) {
         System.out.println("Can't open printwriter for " + name + ": " + e);
         writer = null;
      }
      return writer;
   }
   
   public static Object readObject (String name){
      FileInputStream ostream = null;
       ObjectInputStream p = null;
       Object object = null;
       try{
	   ostream = new FileInputStream(name);
       } catch (Exception e) { System.out.println("Utils:can't open file " + name);}
       try{
	   p = new ObjectInputStream(ostream);
       } catch (Exception e) { System.out.println("Utils: object input");}
       try{
	   object =  p.readObject();
      } catch (Exception e) { System.out.println("Utils:can't read object:" + e);}
       try{
	   ostream.close();
       } catch (Exception e) { System.out.println("Utils: can't close");}
       return object;
   }

   public static void enumerateList (List list){
      int count = 0;
      for (Iterator i = list.iterator(); i.hasNext();)
         System.out.println(count++ + ":" + i.next());
   }

   public static void enumerateLists (List list1, List list2){
      int size1 = list1.size();
      int size2 = list2.size(); 
      int size = Math.max(size1,size2);
      for (int i = 0; i < size; i++){
         System.out.print(i + ": ");
         if ( i < size1 ) System.out.print(list1.get(i));
         else System.out.print("-------");
         System.out.print("  ");
         if ( i < size2 ) System.out.print(list2.get(i));
         else System.out.print("-------");
         System.out.println();
      }
   }

   public static List makeList (Object o){
      return makeList(o,1);
   }

   public static List makeList (Object o1, Object o2){
      List l = makeList(o1,2);
      l.add(o2);
      return l;
   }
   public static List makeList (Object o, int size){
      List l = new ArrayList(size);
      l.add(o);
      return l;
   }
   public static void addTo (List addTo, List addFrom) {
      for (Iterator i = addFrom.iterator(); i.hasNext();)
         addTo.add(i.next());
   }
   private static Random random = new Random(1); 
   public static void setSeed (int seed) {random = new Random(seed); }
   public static int randomInt (int num) {return random.nextInt(num); }
   public static double randomDouble () {
      return random.nextDouble();
   }

   public static boolean containsString (List list, String s){
      if ( s == null ) return list.contains(null);
      for (Iterator i = list.iterator(); i.hasNext();)
         if ( s.equals((String)i.next()) ) return true;
      return false;
   }

   public static String secondsToTime (double seconds ){
      return secondsToTime((int)seconds);
   }
   public static String secondsToTime (int seconds ){
      if ( seconds <= 0 ) return (seconds + " seconds");
      String text = "";
      int hour = seconds / 3600;
      int left = seconds - hour * 3600;
      int min =  left / 60;
      int sec = left - min * 60;
      if ( hour > 0 ) {
         text += hour + " hour";
         if ( hour > 1) text += "s";
         if ( min > 0 || sec > 0 ) text += " and ";
      }
      if ( min > 0 ) {
         text += min + " minute";
         if ( min > 1) text += "s";
         if ( sec > 0 ) text += " and ";
      }
      if ( sec > 0 ){
         text += sec + " second";
         if ( sec > 1) text += "s";
      }
      return text;
   }

      
   public  static double distance (Point one, Point two) {
      double dx = (one.x - two.x);
      double dy = one.y - two.y;
      return Math.sqrt(dx*dx + dy*dy);
   }


   public static List copyList (List list) {
      List copy = new ArrayList(list.size());
      for (Iterator i = list.iterator(); i.hasNext();)
         copy.add(i.next());
      return copy;
   }
   public static String[] stringToArray(String string) {
      StringTokenizer tokenizer = new StringTokenizer(string);
      int count = 0;
      while ( tokenizer.hasMoreElements() ){
         count++;
         tokenizer.nextElement();
      }
      String[] array = new String[count];
      count = 0;
      tokenizer = new StringTokenizer(string);
      while ( tokenizer.hasMoreElements() )
         array[count++] = (String) tokenizer.nextElement();
      return array;
   }


    public static Solution doMove (Move move, Solution solution) {
	Solution copy = solution.copy();
	move.operateOn(copy);
	copy.computeScore();
	return copy; 
    }

   
   public static void main (String[] args) {
      for (int i = 0; i < 20; i++) 
         System.out.println("cp -f /homes/lesh/java/hugs/problems/jan02FakeAgo" + i + " /homes/lesh/java/hugs/tests/probs/train.short.hugs" +i+  " ;");
   }
}
