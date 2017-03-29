package hugs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Node implements Serializable{

   
   private String name;
   protected int id;
   private boolean justMoved = false;
   public boolean getJustMoved(){return justMoved;}
   public void setJustMoved(boolean b){this.justMoved = b;}
   public static boolean grayScale = false;
   
   private static int numNodes = 0;
   
   // seems very dangerous
   public static void initNodes () { numNodes = 0; }
   
   private static final Color HIGH_COLOR = Color.green;
   private static final Color MED_COLOR = Color.yellow;
   private static final Color LOW_COLOR = Color.red;
   private static final Color GRAY_HIGH_COLOR = new Color(145,255,120);
   private static final Color GRAY_MED_COLOR = new Color(147,135,60);
   private static final Color GRAY_LOW_COLOR = new Color(150,0,0);

   public Node (String name){
      this.name = name;
      id = numNodes;
      numNodes++;
   }
   
   public String getName(){return name;}
   
   public int getId(){return id;}
   public Color getMobilityColor(){
      int value = Hugs.THIS.getMobilities().getMobility(id);
      if ( grayScale ) {
         if (value == Mobilities.HIGH)
            return GRAY_HIGH_COLOR;
         if (value == Mobilities.MED)
            return GRAY_MED_COLOR;
         if (value == Mobilities.LOW)
            return GRAY_LOW_COLOR;
      }
      if (value == Mobilities.HIGH)
         return HIGH_COLOR;
      if (value == Mobilities.MED)
         return MED_COLOR;
      if (value == Mobilities.LOW)
         return LOW_COLOR;
      System.err.println("Illegal probability value for node in getMobilityColor");
      return null;
   }
   
   public String toString(){
      return "n" + id;
   }
}
