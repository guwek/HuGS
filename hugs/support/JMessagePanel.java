package hugs.support;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.*; // for linux
import javax.swing.filechooser.*; // for linux
import javax.swing.border.*; // for linux
import java.util.*;

public class JMessagePanel extends JFramedPanel{
   
   private int size = 1;
   private String[] text;
   private final static int DEFAULT_YOFFSET = 8;
   private int height = 15;
   private int offset;
   
   public JMessagePanel(){
      this(1);
   }

   public JMessagePanel(int x){
      this(x,DEFAULT_YOFFSET);
   }
   
   public JMessagePanel(int size, int offset){
      this.size = size;
      text = new String[size];
      height = getFont().getSize();
      this.offset = offset;
   }

   public void setText(String text){
      setText(text,0);
      for (int i = 1; i < size; i++)
         setText(null,i);
   }
   public void setText(String text, int num){
      this.text[num] = text;
   }
   public void paintComponent( Graphics g ) {
      super.paintComponent(g); //paint background
      g.setColor(Color.black);
      for (int i = 0; i < size; i++)
         if (text[i] != null)
            g.drawString(text[i],10,(offset+height)*(i+1));
   }
   
}

