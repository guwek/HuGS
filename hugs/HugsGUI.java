package hugs;

import hugs.search.*;
import hugs.support.*;
import hugs.logging.*;
import hugs.utils.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import hugs.logging.*;
import java.io.*;

public class HugsGUI implements MouseListener {


   public static int HISTORY_WIDTH = 300;
   public static int HISTORY_HEIGHT = 300;
   public static int SCORE_WIDTH = 440;
   public static int SCORE_HEIGHT = 20;
   public static int SCORE_FONT_SIZE = 18;
   public static int TIMER_WIDTH = 50;
   public static int TIMER_HEIGHT = 20;

   public static boolean showTimer = true;
   protected JButton buttonSave = new JButton("Save");
   protected JButton buttonLoad = new JButton("Load");
   protected JButton buttonStart = new JButton("Start");
   protected JButton buttonStop = new JButton("Stop");
   // protected JButton buttonUndo = new JButton("Undo");
   // protected JButton buttonRedo = new JButton("Redo");
   protected JButton undoButton;
   protected JButton redoButton; 
   protected JButton mobilLowButton;
   protected JButton mobilMedButton;
   protected JButton mobilHighButton;
   
   private JMenuBar historyMenuBar= new JMenuBar();
   private JMenu historyMenu= null;
   private JMenuBar solutionMenuBar= new JMenuBar();
   private JMenu solutionMenu= null;
   private JMenuBar toolMenuBar= new JMenuBar();
   private JMenu toolMenu= new JMenu("History");
   private JMenuBar rankMenuBar= new JMenuBar();
   private JMenu rankMenu= new JMenu("Ranked");
   private JMenuBar priorityMenuBar= new JMenuBar();
   private JMenu priorityMenu= new JMenu("Selections");
   private JMenuBar fileMenuBar= new JMenuBar();
   private JMenu fileMenu= new JMenu("File");
   private final JFileChooser fileChooser = new JFileChooser("/homes/lesh/java/hugs/");
   
   protected JMessagePanel scoreLabelPane = new JMessagePanel(3);
   protected JMessagePanel timerPane = null;

   
   

   private JFrame frame;

   private Hugs hugs;
   private Visualization visualization;
   private int testTime;
   private Trial comparison;
   private String searchNames;
   public HugsGUI (Hugs hugs,
                   boolean demo,
                   Visualization visualization,
                   SearchMgr[] searchMgrs,
                   int testTime,
                   Trial comparison,
                   String searchNames
                   ) {
      this.testTime = testTime;
      this.hugs = hugs;
      this.visualization = visualization;
      this.comparison = comparison;
      this.searchNames = searchNames;
      final Hugs finalHugs = hugs;
      frame = new JFrame("Hugs");
      buttonLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                   File file = fileChooser.getSelectedFile();
                   finalHugs.actionLoadSolution(file.getPath());
                   finalHugs.addMenuEvent("loadSolution");
                } 
            }
        });

      JPanel topPane = new JPanel();
      topPane.setLayout(new FlowLayout(FlowLayout.LEFT));
         scoreLabelPane.setPreferredSize(new Dimension(SCORE_WIDTH,SCORE_HEIGHT));
      Font font = new Font("SansSerif", Font.PLAIN , SCORE_FONT_SIZE);
      scoreLabelPane.setFont(font);

      // topPane.add(scoreLabelPane);
      
      topPane.add( fileMenuBar);
      fileMenuBar.add(fileMenu);

      topPane.add( toolMenuBar);
      toolMenuBar.add(toolMenu);
      topPane.add( priorityMenuBar);
      priorityMenuBar.add(priorityMenu);

      if ( !demo) {
         // topPane.add( rankMenuBar);
         // toolMenuBar.add(rankMenu);
      }
      
      // topPane.add( buttonUndo );
      // topPane.add( buttonRedo );

      JMenuItem saveSol = new JMenuItem("save solution");
      saveSol.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		  int returnVal = fileChooser.showSaveDialog(frame);
		  if (returnVal == JFileChooser.APPROVE_OPTION) {
                     File file = fileChooser.getSelectedFile();
                     finalHugs.actionSaveSolution(file.getPath());
                     finalHugs.addMenuEvent("saveSolution");
                  }
         }
         });
      
      JMenuItem loadSol = new JMenuItem("load solution");
      loadSol.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
	     int returnVal = fileChooser.showOpenDialog(frame);
	     if (returnVal == JFileChooser.APPROVE_OPTION) {
		 File file = fileChooser.getSelectedFile();
		 finalHugs.actionLoadSolution(file.getPath());
                 finalHugs.addMenuEvent("loadSolution");
	     } 
         }
	  });
      JMenuItem saveProb = new JMenuItem("save problem");
      saveProb.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		  int returnVal = fileChooser.showSaveDialog(frame);
		  if (returnVal == JFileChooser.APPROVE_OPTION) {
		      File file = fileChooser.getSelectedFile();
		      finalHugs.actionSaveProblem(file.getPath());
                      finalHugs.addMenuEvent("saveProblem");
		  } 
	      }
	  });
      JMenuItem loadProb = new JMenuItem("load problem");
      loadProb.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		  int returnVal = fileChooser.showOpenDialog(frame);
		  if (returnVal == JFileChooser.APPROVE_OPTION) {
		      File file = fileChooser.getSelectedFile();
		      finalHugs.actionLoadProblem(file.getPath());
                      finalHugs.addMenuEvent("loadProblem");
		  } 
	      }
	  });

      
      fileMenu.add(saveSol);
      fileMenu.add(loadSol);
      fileMenu.addSeparator();
      fileMenu.add(saveProb);
      fileMenu.add(loadProb);
      
       
      JMenuItem setHigh = new JMenuItem("set high");
      setHigh.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 finalHugs.actionHigh();
	      }
	  }); 
      JMenuItem setMed = new JMenuItem("set medium");
      setMed.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 finalHugs.actionMed();
	      }
	  }); 
      JMenuItem setLow = new JMenuItem("set low");
      setLow.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 finalHugs.actionLow();
	      }
	  }); 
      JMenuItem setAllHigh = new JMenuItem("set all high");
      setAllHigh.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 finalHugs.setAll(Mobilities.HIGH);
	      }
	  }); 
      JMenuItem setAllMed = new JMenuItem("set all medium");
      setAllMed.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 finalHugs.setAll(Mobilities.MED);
	      }
	  }); 
      JMenuItem setAllLow = new JMenuItem("set all low");
      setAllLow.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 finalHugs.setAll(Mobilities.LOW);
	      }
	  }); 

      JMenuItem clear = new JMenuItem("clear selection");
      clear.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
                 actionClear();
	      }
	  });

      JMenuItem unmarkAll = new JMenuItem("unmark all");
      unmarkAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               finalHugs.actionMarkAll(false);
            }
         });
      JMenuItem markAll = new JMenuItem("mark all");
      markAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               finalHugs.actionMarkAll(true);
            }
         });

      JMenuItem mark = new JMenuItem("mark");
      mark.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               finalHugs.actionMarkSelected(true);
            }
         }); 

      JMenuItem unmark = new JMenuItem("unmark");
      unmark.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               finalHugs.actionMarkSelected(false);
            }
         }); 

      priorityMenu.add(clear);
      priorityMenu.addSeparator();
      priorityMenu.add(setLow);
      priorityMenu.add(setMed);
      priorityMenu.add(setHigh);
      priorityMenu.addSeparator();
      priorityMenu.add(setAllLow);
      priorityMenu.add(setAllMed);
      priorityMenu.add(setAllHigh);
      priorityMenu.addSeparator();
      priorityMenu.add(mark);
      priorityMenu.add(unmark);
      priorityMenu.add(markAll);
      priorityMenu.add(unmarkAll);
      
      JMenuItem getBest = new JMenuItem("get best");
      getBest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               finalHugs.actionGetBest();
               finalHugs.addMenuEvent("getBestFound");
            }
         });
      toolMenu.add(getBest);
      JMenuItem openHistory = new JMenuItem("history");
      openHistory.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            actionOpenHistory();
            finalHugs.addMenuEvent("openHistory");
         }
      });
      toolMenu.add(openHistory);

      
      timerPane = new JMessagePanel(1,2);
      timerPane.setPreferredSize(new Dimension(TIMER_WIDTH,TIMER_HEIGHT));
      if ( showTimer ) 
         topPane.add(timerPane);
         
      // board = new Board( Problem.X_NUM_NODES, Problem.Y_NUM_NODES,600,300);
      // visualization.getMasterPane().setSize(600,300);
      // topPane.setLayout(new FlowLayout());
      JPanel info = new JPanel();
      JPanel search = new JPanel();

      JPanel northPane = new JPanel();
      northPane.setLayout(new GridLayout(0,1));

      JPanel controlPane = new JPanel();
      controlPane.add(topPane);
      northPane.add(controlPane);


      JToolBar bottomControlPane = new JToolBar();
      controlPane.add(bottomControlPane);
      controlPane.setLayout(new GridLayout(0,1));

      undoButton = new JButton(new ImageIcon(getClass().getResource("data/gif/undo.gif")));
      undoButton.setToolTipText("Undo");
      redoButton = new JButton(new ImageIcon(getClass().getResource("data/gif/redo.gif")));
      redoButton.setToolTipText("Redo");
      mobilLowButton = new JButton(new ImageIcon(getClass().getResource("data/gif/low.gif")));
      mobilLowButton.setToolTipText("Low Mobility (double-click to set all)");
      mobilMedButton = new JButton(new ImageIcon(getClass().getResource("data/gif/med.gif")));
      mobilMedButton.setToolTipText("Med Mobility (double-click to set all)");
      mobilHighButton = new JButton(new ImageIcon(getClass().getResource("data/gif/high.gif")));
      mobilHighButton.setToolTipText("High Mobility (double-click to set all)");

      undoButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            finalHugs.actionUndo();
            finalHugs.addMenuEvent("undo");
         }
         });
      redoButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            finalHugs.actionRedo();
            finalHugs.addMenuEvent("redo");
         }});

      mobilLowButton.addMouseListener(this);
      mobilMedButton.addMouseListener(this);
      mobilHighButton.addMouseListener(this);
      
      bottomControlPane.add(undoButton);
      bottomControlPane.add(redoButton);
      bottomControlPane.add(mobilLowButton);
      bottomControlPane.add(mobilMedButton);
      bottomControlPane.add(mobilHighButton);
      bottomControlPane.add(scoreLabelPane);
      
      
      for (int i = 0; i < searchMgrs.length; i++)
         northPane.add(searchMgrs[i].getGUI());
      
      frame.getContentPane().add("North",northPane);
      frame.getContentPane().add("Center",visualization.getMasterPane());
      frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
               finalHugs.actionCloseApplication ();
            }
         });
      ///      int s = java.awt.Frame.MAXIMIZED_BOTH;

      // maximize
      Toolkit tk = frame.getToolkit();
      int width = (int) tk.getScreenSize().getWidth();
      int height = (int) (.9 * tk.getScreenSize().getHeight());


      frame.pack();
      frame.show();
      frame.setSize(width,height);
      
      
      initHistory();
      initializeTimerPane(testTime);
      if ( testTime > 0 ) 
         JOptionPane.showMessageDialog(frame, timerStartMessage());
      

  }

   private String timerStartMessage () {
      String text = "This is a timed test that will last for " + Utils.secondsToTime(testTime) + ".\n";
      text += "The search algorithm(s) you can use are:  " + searchNames + ".    \n";
      text += "Another dialogue box will appear when the time is up.\nThe timer will start when you press OK.\nHave fun.";
      return text;
   }
   

   protected void addRanked (Solution s){
      final Solution sol = s;
      final Hugs finalHugs = hugs;
      JMenuItem menuItem = new JMenuItem(s.toString());
      menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               Hugs.THIS.updateSolution(sol);
               Hugs.THIS.repaint();
               finalHugs.addMenuEvent("loadRanked");
            }
         });
      rankMenu.add(menuItem);
   }

   public void repaint () {
      if ( visualization != null )
         visualization.repaint();
      if ( scoreLabelPane != null )
         scoreLabelPane.repaint();
      if ( timerPane != null )
         timerPane.repaint();
   }

      private boolean testOver;
   private int timerEnd;
   private int timerStart;
   
   private javax.swing.Timer timer;
   private void initializeTimerPane (int testTime) {
      timerStart = Utils.getSeconds();
      timerEnd = testTime + timerStart;
      ActionListener listen =   new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            updateTimerPane();
         }
      };
      timer = new javax.swing.Timer (1000,listen);
      testOver = false;
      timer.start();
   }
   
   private void updateTimerPane (){
      int current = (int) Calendar.getInstance().getTime().getTime()/1000;
      String text = null;
      int delta;
      if ( testTime > 0 ) {
         delta = timerEnd-current;
         if ( delta < 0 && !testOver){
            hugs.endSession();
            Score best = Hugs.THIS.getBestFound().getScore();
            String endText = "Thanks. The test is over and your results have been saved\nYour best score was " + best + "\n";
            if ( comparison != null && comparison.getResult() != null){
               int compTime = comparison.getTime();
               Integer comp = ExpFunc.timeBeforeMatchScore(comparison,best);
               if ( comp != null )
                  endText += "It took the computer " + Utils.secondsToTime(comp.intValue()) + " to match that score.\n";
               else 
                  endText += "The computer did not get as good a score as that even after " + Utils.secondsToTime(compTime) + " of unguided search.\n";
               endText += "The computer's best score after " + Utils.secondsToTime(compTime) + " of unguided search was " + ExpFunc.bestScore(comparison.getResult()) + ".\n";
               endText += "Quit the application at your leisure.";
            }
            JOptionPane.showMessageDialog(frame, endText);
            testOver = true;
         }
      }
      else {
         delta = current - timerStart;
      }
      if ( testOver )
         text = "--";
      else {
         int min =  delta / 60;
         int sec = delta - (min * 60);
         text = min + ":" + (sec < 10 ? "0" : "") + sec;
      }
      timerPane.setText(text);
      timerPane.repaint();
   }

   public int getTimerStart () { return timerStart; }
   
   public Visualization getVisualization () { return visualization; }
   
   public boolean[] getSelected () { return visualization.getSelected(); }
   
   public void setScoreText (String s) {
      scoreLabelPane.setText(s);
      scoreLabelPane.repaint();
   }


   protected void actionClear (){
      visualization.clearSelected();
      repaint();
   };

   

   private static JScrollPane historyScrollPane;
   
   private void initHistory () {
      JList historyJList = new JList(Hugs.THIS.getHistoryList());
      historyScrollPane = new JScrollPane(historyJList);
      historyScrollPane.setPreferredSize(new Dimension(HISTORY_WIDTH,HISTORY_HEIGHT));
      historyJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      // historyJList.setSelectedIndex(0);
      historyJList.addListSelectionListener(
         new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting())
               return;
            JList theList = (JList)e.getSource();
            if (theList.isSelectionEmpty()) {
            } else {
               int index = theList.getSelectedIndex();
               Hugs.THIS.adoptHistorySolution(index);
               //Hugs.THIS.adoptNewSolution((Solution)historySolutions.get(index));
               // Hugs.THIS.updateStuff();
               repaint();
            }
         }
      }
         );
   }


   protected void actionOpenHistory () {
      JDialog dialog = new JDialog(frame,"history");
      JPanel panel = new JPanel();
      panel.add(historyScrollPane);
      dialog.getContentPane().add(panel);
      dialog.pack();
      dialog.show();
   }

   public void setProblem (Problem problem) {
      visualization.setProblem(problem);
   }


   public void mouseReleased (MouseEvent e) {
      int count = e.getClickCount();
      if (e.getSource() == mobilLowButton) {
         if ( count == 1 )
            hugs.actionLow();
         else if ( count == 2 )
            hugs.setAll(Mobilities.LOW);
      }
      else if (e.getSource() == mobilMedButton) {
         if ( count == 1 )
            hugs.actionMed();
         else if ( count == 2 )
            hugs.setAll(Mobilities.MED);
      }
      else if (e.getSource() == mobilHighButton) {
         if ( count == 1 )
            hugs.actionHigh();
         else if ( count == 2 )
            hugs.setAll(Mobilities.HIGH);
      }
   }
      public void mouseEntered(MouseEvent e)  { }    
    public void mousePressed(MouseEvent e)  { }    
    public void mouseExited(MouseEvent e)   { }    
    public void mouseClicked(MouseEvent e)  { }
}
    
