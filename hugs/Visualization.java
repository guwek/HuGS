package hugs;

import hugs.support.*;
import javax.swing.*; 

public abstract class Visualization extends JFramedPanel {

   public void setSolution (Solution solution) {}
   public void setProblem (Problem problem) {}
   public void setHugs (Hugs hugs) {};
   public boolean[] getSelected(){ return null;}
   public void clearSelected () {};
   public void clearJustChanged() {};
   public JPanel getMasterPane () { return this; }
   protected boolean showChanges = true;
   public void setShowChanges (boolean v) { showChanges = v; }

} 
