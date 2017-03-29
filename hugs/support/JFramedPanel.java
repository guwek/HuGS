
package hugs.support;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.*; // for linux
import javax.swing.filechooser.*; // for linux
import javax.swing.border.*; // for linux
import java.util.*;

public class JFramedPanel extends JPanel{
  public void paintComponent( Graphics g ) {
      super.paintComponent(g); //paint background
      Rectangle b = bounds();
      g.setColor(Color.black);
      g.drawRect(0,0,b.width-1,b.height-1);
      }
}
