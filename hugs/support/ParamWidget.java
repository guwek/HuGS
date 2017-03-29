package hugs.support;

import hugs.*;
import hugs.support.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; // for linux

public class ParamWidget extends JPanel {

   Parameter parameter;
   JTextField text;
   JCheckBox box;
   public ParamWidget (Parameter p) {
      this.parameter = p;
      setLayout(new GridLayout(0, 1));
      if ( p == null ) 
         System.out.println("ParamWidget: null parameter!");
      add(new Label(parameter.getName()));
      if ( parameter instanceof BooleanParameter){
         box = new JCheckBox();
         if ( (Boolean) ((BooleanParameter) parameter).getValue() == Boolean.TRUE )
            box.setSelected(true);
         add(box);
      }
      else {
         text = new JTextField(parameter.getValue().toString());
         add(text);
      }
   }

   public void setParam () {
      if ( parameter instanceof DoubleParameter )
         ((DoubleParameter)parameter).value = textToDouble(text.getText());
      else if ( parameter instanceof IntParameter )
         ((IntParameter)parameter).value = textToInt(text.getText());
      else if ( parameter instanceof BooleanParameter )
         ((BooleanParameter)parameter).value = box.isSelected();
   }
   
   private static double textToDouble (String s){
      if ( s == null || s.equals("")) return -1;
      return Double.valueOf(s).doubleValue();
   }
   private static int textToInt (String s){
      if ( s == null || s.equals("")) return -1;
      return Integer.valueOf(s).intValue();
   }

   
}
