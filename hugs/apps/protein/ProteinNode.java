package hugs.apps.protein;

import hugs.Node;
import java.awt.*;

public class ProteinNode extends Node {

   private boolean value;
   public ProteinNode (boolean value) {
      super(null);
      this.value = value;
   }

   public String toString (){
      return "n" + id + "=" + value;
   }

   public boolean getValue () {return value;}
}
