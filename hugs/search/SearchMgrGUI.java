package hugs.search;

import hugs.*;
import hugs.utils.*;
import hugs.support.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*; 
import hugs.logging.*;

public class SearchMgrGUI extends JToolBar {

   private final static int MESSAGE_WIDTH = 800;
   public static int FONT_SIZE = 14;
   public static boolean showOutputs = true;
   public static boolean showInputButton = true;
   
   private SearchMgr searchMgr;
   private java.util.List inputs;
   private java.util.List inputParams;
   private Logger logger;
   public SearchMgrGUI (SearchMgr searchMgr, java.util.List inputs, java.util.List inputParams, Logger logger, java.util.List searches){
      this.searchMgr = searchMgr;
      this.inputs = inputs;
      this.inputParams = inputParams;
      this.logger = logger;

      JPanel buttons = new JPanel();
      if ( showInputButton )
         buttons.setLayout(new GridLayout(0, 3));
      else
         buttons.setLayout(new GridLayout(0, 2));
      buttons.add(buttonStart);
      buttons.add(buttonStop);
      if ( showInputButton )
         buttons.add(buttonGetInputs);
      buttons.add(buttonGetBest);
      buttons.add(buttonGetCurrent);
      
      inputPanel.setLayout(new GridLayout(0, 2));
      JPanel searchPanel = new JPanel();
      searchPanel.setLayout(new GridLayout(0, 1));
      final SearchMgr finalSearchMgr = searchMgr;
      {
         {// selecting type of search;
            String[] names = new String[searches.size()];
            int count = 0;
            for (Iterator i = searches.iterator(); i.hasNext();)
               names[count++] = ((SearchThread) i.next()).getSearchName();
            searchList = new JComboBox(names);
            searchList.setSelectedIndex(0);
            searchList.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  JComboBox cb = (JComboBox)e.getSource();
                  int index = cb.getSelectedIndex();
                  finalSearchMgr.setSearch(index);
               }
            });
            searchPanel.add(searchList);
         }
      }
      searchPanel.add(initModes());
      if ( Hugs.numSearchMgrs.value > 1 ) {
         buttons.add(boxBack);
         buttons.add(boxBest);
      }
        
      add(searchPanel);
        
      add(buttons);
      add(outputPanel);
      initButtons();
    }


   public void addInput (ParamWidget param) {
      inputs.add(param);
      inputPanel.add(param);
      param.repaint();
   }

   public void setSearch (SearchThread search) {
      for (Iterator i = inputs.iterator(); i.hasNext();)
         inputPanel.remove((ParamWidget) i.next());
      inputs.clear();
      inputParams = search.getInputs();
      for (Iterator i = inputParams.iterator(); i.hasNext();)
         addInput(new ParamWidget((Parameter) i.next()));
      SearchAdjuster adjuster = search.getSearchAdjuster();
      if ( adjuster != null )
         for (Iterator i = adjuster.getInputs().iterator(); i.hasNext();)
            addInput(new ParamWidget((Parameter) i.next()));
      if ( Hugs.THIS != null )
         Hugs.THIS.repaint();
      repaint();
   }


    public void updateSearchMessage (String message, Solution best, int time, java.util.List outputs, int startTime, int lastBestTime, Score startScore) {
       if ( showOutputs ) {
          String line1 = "time= " + (time - startTime) + 
             ", sinceBest= " + (time - lastBestTime) +
             ", best: " // + ( best == null ? " null" : (" " + best.getScore()) );
             + (best == null ? "--" : best.getScore().comparisonString(startScore));
          // if ( best != null )
          // line1 += "(" + (best.getScore() - startScore) + ")";
          outputPanel.setText(line1,0);
          
          String line2 = "Outputs: ";
          int count = 0;
          if ( outputs != null )
             for (Iterator i = outputs.iterator(); i.hasNext();)
                line2 = line2 + (count++ > 0 ? ", " : "") + (Parameter) i.next();
          outputPanel.setText(line2,1);
       }
       else { // { for demos}
          String line1 = "time= " + (time - startTime) + 
             ", sinceBest= " + (time - lastBestTime);
          String line2 =
             (best == null ? "--" : best.getScore().toString());
             // (best == null ? "--" : best.getScore().comparisonString(startScore));
          outputPanel.setText(line1,0);
          outputPanel.setText(line2,1);
       }
       // outputPanel.setText(("Message:" + message),2);
       outputPanel.repaint();
    }

   
   JPanel inputPanel = new JPanel();
   JMessagePanel outputPanel;
    {
       if ( showOutputs )
          outputPanel = new JMessagePanel(2);
       else 
          outputPanel = new JMessagePanel(2,10);
       outputPanel.setPreferredSize(new Dimension(MESSAGE_WIDTH,60));
       Font font = new Font("SansSerif", Font.PLAIN , FONT_SIZE);
       outputPanel.setFont(font);
    }
   
   public void actionGetInputs (int id, String name) {
      String title = id + "| Inputs for " + name;
      JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(this),title);
      dialog.getContentPane().add(inputPanel);
      dialog.pack();
      dialog.show();

   }


   public void updateSearchColor (boolean running) {
      if ( running ) {
         searchList.setBackground(Color.orange);
         buttonStart.setLabel("Restart");
      }
      else {
         buttonStart.setLabel("Start");
         searchList.setBackground(getBackground());
      }
   }

   
   protected JButton buttonStart = new JButton("Start  ");
   protected JButton buttonStop = new JButton("Stop");
   protected JButton buttonGetBest = new JButton("Best");
   protected JButton buttonGetCurrent = new JButton("Current");
   protected JButton buttonGetInputs = new JButton("Inputs");
   protected JComboBox searchList = null;
   private void initButtons ()  {
      final SearchMgr finalSearchMgr = searchMgr;
       buttonStart.addActionListener(new ActionListener () {
              public void actionPerformed (ActionEvent event) {
                 if ( finalSearchMgr.isActive() ) {
                    finalSearchMgr.engageSearch();
                    // originalMobilities = (Mobilities) Hugs.THIS.getMobilities().clone();
                    // actionStart(originalMobilities);
                 }
              }
           });
	buttonStop.addActionListener(new ActionListener () {
           public void actionPerformed (ActionEvent event) {
              finalSearchMgr.actionStopSearch(true);
           }
        });
        buttonGetBest.addActionListener(new ActionListener () {
           public void actionPerformed (ActionEvent event) {
              finalSearchMgr.resetUnseenBest();
              finalSearchMgr.updateNewBest();
              finalSearchMgr.restoreMobilities();
              finalSearchMgr.updateSolution(finalSearchMgr.getBestSolution());
              if ( logger != null )
                 logger.add(new SearchEvent(finalSearchMgr.getBestSolution(),"getBest"));
           }
        });
        buttonGetCurrent.addActionListener(new ActionListener () {
              public void actionPerformed (ActionEvent event) {
                 finalSearchMgr.updateNewBest();
                 finalSearchMgr.actionGetCurrent(true);
                 if ( logger != null )
                    logger.add(new SearchEvent(finalSearchMgr.getBestSolution(),"getCurrent"));
              }
           });
        buttonGetInputs.addActionListener(new ActionListener () {
              public void actionPerformed (ActionEvent event) {
                 finalSearchMgr.actionGetInputs();
              }
           });
   }


   JCheckBox boxBack = new JCheckBox("Back",false);
   JCheckBox boxBest = new JCheckBox("Best",false);

   private JComboBox initModes (){
      String[] modes = new String[4];
      modes[SearchMgr.MODE_RUN] = "run";
      modes[SearchMgr.MODE_STEP] = "step";
      modes[SearchMgr.MODE_WATCH] = "detail";
      modes[SearchMgr.MODE_POLL] = "poll";
      JComboBox modeList = new JComboBox(modes);
      modeList.setSelectedIndex(SearchMgr.MODE_RUN);
      searchMgr.setMode(SearchMgr.MODE_RUN);
      final String[] myModes = modes;
      modeList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               JComboBox cb = (JComboBox)e.getSource();
               int index = cb.getSelectedIndex();
               searchMgr.setMode(index);
               if ( logger != null )
                  logger.add(new SearchEvent(searchMgr.getBestSolution(),("setMode" + myModes[index])));
            }
         });
      return modeList;
   }

   public boolean  useBest() { return boxBest.isSelected(); }
   public boolean  isBackground () { return boxBack.isSelected(); }
   public void updateBackground (boolean anyForeground ) {
      if ( boxBack.isSelected() )
         if ( !anyForeground && searchMgr.isHalted() ){
            if ( searchMgr.getOriginalMobilities() == null )
               searchMgr.setOriginalMobilities((Mobilities) Hugs.THIS.getMobilities().clone());
            searchMgr.actionStart(searchMgr.getOriginalMobilities());
         }
         else if (!searchMgr.isHalted() && anyForeground )
            searchMgr.actionStopSearch(false);
   }


  public void updateEnabled (boolean running) { 
      buttonStop.setEnabled(running);
      buttonGetCurrent.setEnabled(running);
  }

   public void setNewBest (boolean value, boolean allBest) {
      if ( value ) {
         if (allBest) buttonGetBest.setBackground(Color.green);
        else buttonGetBest.setBackground(Color.orange);
      }
      else buttonGetBest.setBackground(getBackground());
      repaint();
   }

   /*
   public void setNewBest (boolean value) {
      if ( value )
         buttonGetBest.setBackground(Color.green);
      else buttonGetBest.setBackground(getBackground());
      repaint();
   }
   */
   
   /*
   public void updateNewBest () {
      boolean isBest = false;
      if ( !searchMgr.isHalted() ) 
         if ( isBackground() ){
            Solution bestSeen = Hugs.THIS.getBestSeen();
            Solution myBest = searchMgr.getBestSolution();
            if ( myBest != null &&
                 (bestSeen == null ||
                  myBest.getScore().isBetter(bestSeen.getScore(),searchMgr.getSearchAdjuster())))
               isBest = true;
         }
         else isBest = searchMgr.getUnseenBest();
      if ( isBest )
         buttonGetBest.setBackground(Color.green);
      else buttonGetBest.setBackground(getBackground());
   }
   */

   
}
