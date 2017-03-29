/*
 * jobshopScheduleDisplay.java
 *
 * Created on February 3, 2001, 11:24 AM
*/

/******

       neal did a somewhat quick hack to make this fit into the hugs-code-mold.
       here are some of the issues:
        - this code maintains a two-dimensional array to track which operations
          are selected, but then this has to be translated into a one dimensional
          one to handle the getSelected() query.


 ****/ 
package hugs.apps.jobshop;

// import jobshop.data.*;
// import jobshop.ui.*;
import hugs.*;
import hugs.support.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;             // Vector needed
import javax.swing.*;
//import javax.swing.event.*;
/**
 *
 * @author  Guy T. Schafer
 * @version 1.0
 */
public class JobshopVisualization extends Visualization
    implements MouseListener, MouseMotionListener
{
    public static final int MINZOOM   = 11; // Minimum scaling factor

    private static final int SPACEH   = 30; // Horizontal boundaries
    private static final int SPACEV   = 5;  // Vertical boundaries
    private static final int MIN      = 1;  // Default Pixels per minute
    private static final int MHEIGHT  = 45; // Machine height
    
   // private jobshopClient       parent;
   private JobshopSolution     schedule = null;    // This is what's displayed
    private JobshopSolution     lastSchedule = null;   // Previous displayed (for changes)
    private double          scaleHoriz;     // The current Horiz zoom factor
    private double          scaleVert;      // The current Vert zoom factor
    private int             curPixWidth;    // Panel width in pixels
    private double          pixelsPerMin;   // Actual Pixels / min
    private int             maxMin;         // Maximum minute displayed
    private Color[]         colorArray;

    private JPanel          logoPanel;      // The 3 diamonds

    private boolean         stripeChanges = true,  // Turn on changes stripe
                            stripeCritPath = true, // Turn on crit path stripe
                            stripeMobil = true,    // Turn on mobilities stripe
                            stripeJobId,    // Turn on Job ID labels
                            stripeJobColor; // Turn on Job colors

    private boolean[][]     isChanged,      // This op is in a different order
                            isCritPath,     // This op is on the critical path
                            isSelected;     // This op is selected
   // private int[][]         mobilities;     // low, medium, or high mobility/op

    private boolean         isDragging,     // Not just a click
                            isDraggingOp;   // Dragging an op or selection box
    private Rectangle       selectRect;     // Rect dragged during drag and drop
    private int             oldX;           // Used to track op dragging
    private int             selectJob, selectOp, selectMach;    // Used to track shuffling
    private int             selectOrigX,
                            selectOrigY;    // Track selection Box
    private Point           insertPoint;    // Where the drag will drop (physically)
    private int             insertOrd;      // Where the drag drops (logically)

    // Colors that will be used multiple times:
    private Color           colorLightGray, colorDarkGray,
                            colorWhite, colorBlack,
                            colorRed, colorYellow, colorGreen,
                            colorPurple;

   private JobshopProblem problem;
   private boolean[] selected;


   private void initializeStuff (JobshopProblem problem) {
      isSelected = new boolean[problem.jobCount][problem.machineCount];
      
      // Create an array of colors for use as job colors
      // This will create up to 27 colors.  More jobs
      // (not likely solvable with current methods) will
      // 'wrap' and reuse the same colors.
      colorArray = new Color[problem.jobCount + 2];
      for (int i=0; i<problem.jobCount + 2; i++)
         colorArray[i] = new Color((int)(((i/9)+1)*75),
                                   (int)((((i/3)%3)+1)*75),
                                   (int)(((i%3)+1)*75));
   }

   protected Hugs hugs = null;
   public void setHugs (Hugs hugs) {
      this.hugs = hugs;
   }

   
   public void setProblem (Problem problem) {
      System.out.println("JobshopVisualization: setting problem" + problem);
      this.problem = (JobshopProblem) problem;
      initializeStuff(this.problem);
      //setSolution(problem.getInitSolution());
      selected = new boolean[problem.size()];
   }
   public boolean[] getSelected(){
      for (int j=0; j<problem.jobCount; j++)
         for (int m=0; m<problem.machineCount; m++)
            selected[problem.jobNop2id(j,m)] = isSelected[j][m];
      return selected;
   }

    boolean[][] marked;
    private void computeMarked () {
	boolean[] oneDMarked = Hugs.THIS.getMarked();
	if ( marked == null ||
	     marked.length < problem.jobCount ||
	     marked[0].length < problem.machineCount ) 
	    marked = new boolean[problem.jobCount][problem.machineCount];
	for (int j=0; j<problem.jobCount; j++)
	    for (int m=0; m<problem.machineCount; m++)
		marked[j][m] = oneDMarked[problem.jobNop2id(j,m)];
    }
    public void clearSelected () {
      for (int j=0; j<problem.jobCount; j++)
         for (int m=0; m<problem.machineCount; m++)
            isSelected[j][m] = false;
   }
   public void clearJustChanged() {
      System.out.println("BUG: implement clearJustChanged in JobshopVisulization");
   };

   
    /** Creates new JobshopVisualization */
    public JobshopVisualization()
    {

       // Initialize variables:
        scaleHoriz = 1;
        scaleVert = 1;
        curPixWidth = 0;
        selectRect = null;
        selectJob = 0;
        selectOp = 0;
        selectMach = 0;
        oldX = 0;
        stripeJobId = true;
        stripeJobColor = true;

        // Set up some colors that will be used multiple times:
        colorLightGray = new Color(216, 216, 216);  // Lines, Unsel ops
        colorDarkGray = new Color(128, 128, 128);   // Labels & Lines
        colorWhite = new Color(255, 255, 255);      // SelBorder, font
        colorBlack = new Color(0, 0, 0);            // Selected ops, font
        colorPurple = new Color(255, 0, 255);       // Changed stripe
        colorRed = new Color(255, 0, 0);            // Low Mobil, Crit stripe
        colorYellow = new Color(255, 255, 0);       // Med Mobil
        colorGreen = new Color(0, 216, 0);          // High Mobil

        // Give me a picture to use:
        ImageIcon icon = getImageIcon("gif/logo.gif");
        JLabel logoLabel = new JLabel(icon);

        logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.add(logoLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(logoPanel, BorderLayout.EAST);

        // I am responsible for my own mouse listening:
        addMouseListener(this);
        addMouseMotionListener(this);

        JPanel control = getControlPane();
        if ( control != null ) {
           master = new JFramedPanel();
           master.setLayout(new BorderLayout());
	   JScrollPane scroll = new JScrollPane(this);
	   master.add(scroll,"Center");
           // master.add(this,"Center");
           master.add(control,"North");
        }
        else master = this;

        // Set size:
        setPreferredSize(new Dimension(600, 400));
    }

   private boolean coherent = true;
   public void setSolution (Solution s) {
      if ( s != null ) {
	  // System.out.println("JOBSHOP Visualization setSolution " + s + " with score " + s.getScore());
         setSchedule((JobshopSolution) s);
      }
   }
   
   private void setSchedule(JobshopSolution sched)
    {
       if (sched.problem != this.problem) {
          coherent = false;
          setProblem(sched.problem);
          return;
       }
       coherent = true;
       // System.out.println("JOBSHOP Visualization setSchedule " + sched + " with score " + sched.getScore());
        lastSchedule = schedule;
        schedule = sched;

        // Calculate the changes from the previous displayed schedule
        // (in case the user turns it on at any moment):
        if (lastSchedule != null){
           isChanged = jobshopScheduleOps.getChanges(problem,lastSchedule, schedule);
        }
        else
        {
           isChanged = new boolean[problem.jobCount][problem.machineCount];
            for (int j=0; j<problem.jobCount; j++)
                for (int m=0; m<problem.machineCount; m++)
                    isChanged[j][m] = false;
        }
           maxMin = 0;
           for (int j=0; j<problem.jobCount; j++)
              for (int m=0; m<problem.machineCount; m++)
                 {
                    // Set maxMinutes.
                    // Look through entire schedule and find the latest
                    // (greatest {start + duration})
                // Note that even huge jobs by current standards still have
                    // a small product: (jobs * machines).  Also, the setSchedule
                    // is called infrequently.  So nested loops like
                    // this one shouldn't be too big a deal.
                    if ((schedule.start[j][m] + problem.duration[j][m]) > maxMin)
                       maxMin = schedule.start[j][m] + problem.duration[j][m];
                    
                    // Initialize some array stuff:
                    // isSelected[j][m] = false;
                 }

        // Now calculate the current width in pixels:
        pixelsPerMin = MIN * scaleHoriz;
        curPixWidth = (int)(maxMin * pixelsPerMin);
        curPixWidth += (int)(2 * SPACEH);    // add the l/r margins

        // Now that there's a schedule, eliminate the logo:
        remove(logoPanel);
        
        // Resize this panel to hold new schedule:
        setPreferredSize(new Dimension(
            curPixWidth,
            (int)((((MHEIGHT + SPACEV) * problem.machineCount) + MHEIGHT) * scaleVert)));
        repaint();
    }


   JPanel master = null;
   public JPanel getMasterPane () { return master;}
   
   JPanel controlPane;
   protected JPanel getControlPane () {
      setupButtons();
      if ( controlPane == null ) {
         controlPane = new JFramedPanel();
         controlPane.add(buttonClear);
         controlPane.add(buttonZoomOutHoriz);
         controlPane.add(buttonZoomInHoriz);
         controlPane.add(buttonZoomOutVert);
         controlPane.add(buttonZoomInVert);
         controlPane.add(showJobIdTogButton);
         controlPane.add(showJobColorTogButton);
         controlPane.add(showCritPathTogButton);
         controlPane.add(showChangesTogButton);
         controlPane.add(showMobilTogButton);
         controlPane.add(buttonIncreaseSpansize);
         controlPane.add(buttonDecreaseSpansize);
      }
      return controlPane;
   }

   JButton buttonIncreaseSpansize;
   JButton buttonDecreaseSpansize; 
   JButton buttonClear;
   JButton buttonZoomOutHoriz;
   JButton buttonZoomInHoriz;
   JButton buttonZoomOutVert;
   JButton buttonZoomInVert;
   private JToggleButton
      showJobColorTogButton,  // Keep job color visible
      showJobIdTogButton,     // Show job and op #
      showChangesTogButton,   // Highlight changes from last
      showCritPathTogButton,  // Highlight critical path
      showMobilTogButton;     // Highlight mobilities

   private void setupButtons (){
      buttonClear = new JButton("Clear");
      buttonIncreaseSpansize = new JButton("Inc. span");
      buttonDecreaseSpansize = new JButton("Dec. span");
      buttonClear.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            clearJustChanged();
            selectAllJob();
         }
      });
      buttonIncreaseSpansize.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            Jobshop.spanSize++;
            repaint();
         }
      });
      buttonDecreaseSpansize.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            if ( Jobshop.spanSize > 1 )
               Jobshop.spanSize--;
            repaint();
         }
      });
            
      // buttonZoomOutHoriz = new JButton(getImageIcon("gif/zoomouth.gif"));
      buttonZoomOutHoriz = new JButton(getImageIcon("gif/zoomouth.gif"));
      buttonZoomOutHoriz.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            zoomOutHoriz();
            repaint();
         }
         });

      buttonZoomInHoriz = new JButton(getImageIcon("gif/zoominh.gif"));
      buttonZoomInHoriz.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            zoomInHoriz();
            repaint();
         }
         });
      buttonZoomOutVert = new JButton(getImageIcon("gif/zoomoutv.gif"));
      buttonZoomOutVert.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            zoomOutVert();
            repaint();
         }
         });

      buttonZoomInVert = new JButton(getImageIcon("gif/zoominv.gif"));
      buttonZoomInVert.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            zoomInVert();
            repaint();
         }
         });
      showJobIdTogButton = new JToggleButton(getImageIcon("gif/jobid.gif"),stripeJobId);
      showJobIdTogButton.setToolTipText(jobshopConstants.SHOW + jobshopConstants.JOBID);

      showJobColorTogButton = new JToggleButton(getImageIcon("gif/jobcol.gif"),stripeJobColor);
      showJobColorTogButton.setToolTipText(jobshopConstants.SHOW + jobshopConstants.JOBCOL);
      showCritPathTogButton = new JToggleButton(getImageIcon("gif/crit.gif"),stripeCritPath);
      showCritPathTogButton.setToolTipText(jobshopConstants.SHOW + jobshopConstants.CRIT);
      showChangesTogButton = new JToggleButton(getImageIcon("gif/change.gif"),stripeChanges);
      showChangesTogButton.setToolTipText(jobshopConstants.SHOW + jobshopConstants.CHANGE);
      showMobilTogButton = new JToggleButton(getImageIcon("gif/mobil.gif"),stripeMobil);
      showMobilTogButton.setToolTipText(jobshopConstants.SHOW + jobshopConstants.MOBIL);
        
      showJobColorTogButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            showJobColor(showJobColorTogButton.isSelected());
            repaint();
         }});
      showMobilTogButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            showMobil(showMobilTogButton.isSelected());
            repaint();
         }});
      showCritPathTogButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            showCritPath(showCritPathTogButton.isSelected());
            repaint();
         }});
      showJobIdTogButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            showJobId(showJobIdTogButton.isSelected());
            repaint();
         }});
      showChangesTogButton.addActionListener(new ActionListener () {
         public void actionPerformed (ActionEvent event) {
            showChanges(showChangesTogButton.isSelected());
            repaint();
         }});


   }
   
   public void showChanges(boolean on)
    {
        stripeChanges = on;
        repaint();
    }
    public void showCritPath(boolean on)
    {
        stripeCritPath = on;
        repaint();
    }
    public void showJobColor(boolean on)
    {
        stripeJobColor = on;
        repaint();
    }
    public void showJobId(boolean on)
    {
        stripeJobId = on;
        repaint();
    }
    public void showMobil(boolean on)
    {
        stripeMobil = on;
        repaint();
    }

    public int zoomInHoriz()
    {
        scaleHoriz += 0.1;
        // Calibrate the increments:
        pixelsPerMin = MIN * scaleHoriz;
        curPixWidth = (int)(maxMin * pixelsPerMin);
        curPixWidth += (int)(2 * SPACEH);    // add the l/r margins

        // Resize this panel to new zoom level:
        setPreferredSize(new Dimension(
            curPixWidth,
            (int)((((MHEIGHT + SPACEV) * problem.machineCount) + MHEIGHT) * scaleVert)));

        return (int)(scaleHoriz * 100);
    }
    
    public int zoomOutHoriz()
    {
        if (scaleHoriz > (MINZOOM/100.0))
        {
            scaleHoriz -= 0.1;
            // Calibrate the increments:
            pixelsPerMin = MIN * scaleHoriz;
            curPixWidth = (int)(maxMin * pixelsPerMin);
            curPixWidth += (int)(2 * SPACEH);    // add the l/r margins

            // Resize this panel to new zoom level:
            setPreferredSize(new Dimension(
                curPixWidth,
            (int)((((MHEIGHT + SPACEV) * problem.machineCount) + MHEIGHT) * scaleVert)));
        }
        return (int)(scaleHoriz * 100);
    }

    public int zoomInVert()
    {
        scaleVert += 0.1;
        // Resize this panel to new zoom level:
        setPreferredSize(new Dimension(
            curPixWidth,
            (int)((((MHEIGHT + SPACEV) * problem.machineCount) + MHEIGHT) * scaleVert)));

        return (int)(scaleVert * 100);
    }

    public int zoomOutVert()
    {
        if (scaleVert > (MINZOOM/100.0))
        {
            scaleVert -= 0.1;
            // Resize this panel to new zoom level:
            setPreferredSize(new Dimension(
                curPixWidth,
            (int)((((MHEIGHT + SPACEV) * problem.machineCount) + MHEIGHT) * scaleVert)));
        }
        return (int)(scaleVert * 100);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // If no schedule, do nothing:
        if (schedule == null || !coherent)
            return;

        // Set up a scaled font:
        int fontSize = (int)(MHEIGHT * scaleVert / 2);
        if (fontSize > 24)
            fontSize = 24;
        else if (fontSize < 7)
            fontSize = 7;
        g.setFont(new Font("Dialog", Font.PLAIN, fontSize));

        // Draw the background area:
        g.setColor(colorWhite);
        g.fillRect(0,0,
            curPixWidth,
            (int)((((MHEIGHT + SPACEV) * problem.machineCount) + MHEIGHT) * scaleVert));

        // Draw the minute lines at least 10 pixels apart:
        int minLine;
        if (pixelsPerMin > 11) minLine = 1;
        else if (pixelsPerMin > 1.1) minLine = 10;
        else if (pixelsPerMin > 0.11) minLine = 100;
        else if (pixelsPerMin > 0.011) minLine = 1000;
        else if (pixelsPerMin > 0.0011) minLine = 10000;
        else minLine = 100000;
        for (int i=0; i<=maxMin/minLine; i++)
        {
            // Label some of the lines:
            if ((i % 5) == 0)
            {
                g.setColor(colorDarkGray);
                g.drawString("" + (i * minLine),
                    (int)((pixelsPerMin * i * minLine) + SPACEH) - 4,
                    (int)((((MHEIGHT + SPACEV) * problem.machineCount) + SPACEV) * scaleVert) +  fontSize + 1);
                g.drawLine((int)(pixelsPerMin * i * minLine + SPACEH),
                    (int)(SPACEV * scaleVert/2), 
                    (int)((pixelsPerMin * i * minLine) + SPACEH),
                    (int)((((MHEIGHT + SPACEV) * problem.machineCount) + SPACEV) * scaleVert));
                g.setColor(colorLightGray);
            }
            else
                g.drawLine((int)((pixelsPerMin * i * minLine) + SPACEH),
                    (int)(SPACEV * scaleVert/2), 
                    (int)((pixelsPerMin * i * minLine) + SPACEH),
                    (int)((((MHEIGHT + SPACEV) * problem.machineCount) + SPACEV) * scaleVert));
        }
        // Draw machine timelines (like tracks):
        g.setColor(colorDarkGray);         // dark borders
        for (int i=0; i<problem.machineCount; i++)
        {
            g.drawString("" + i,
                (int)((3 * SPACEH/4) - (fontSize/2)),
                (int)((((SPACEV * (i+1)) + (MHEIGHT * i) + (MHEIGHT/2)) * scaleVert) + (fontSize/2)));
            // drawRect (x, y, w, h)
            g.drawRect(
                SPACEH - 1,
                (int)(((SPACEV * (i+1)) + (MHEIGHT * i)) * scaleVert) - 1,
                (int)(maxMin * pixelsPerMin) + 1,
                (int)(MHEIGHT * scaleVert) + 1);
        }
        
        // Draw the current schedule:
        int x, y, w, h;
        // Height is the same for all ops.  Only calulate it once:
        h = (int)(MHEIGHT * scaleVert);
        int colCount = 1;   // Start with a cool color
	computeMarked();
        for (int j=0; j<problem.jobCount; j++)
        {
            // Every job is a different constant color:
            colCount++;
            for (int m=0; m<problem.machineCount; m++)
            {
                // Using these numbers alot.  store 'em:
               x = (int)((schedule.start[j][m] * pixelsPerMin) + SPACEH) + 1;
                y = (int)(((SPACEV * (problem.machine[j][m]+1)) + (MHEIGHT * problem.machine[j][m])) * scaleVert);
                w = (int)(problem.duration[j][m] * pixelsPerMin) - 1;

                // Paint this op rectangle:
                if (isSelected[j][m])
                    g.setColor(colorBlack);
                else
                    if (stripeJobColor)
                       g.setColor(colorArray[colCount]);
                    else
                        g.setColor(colorLightGray);
                g.fillRect(x, y, w, h);

                if (stripeJobColor && isSelected[j][m]) // Don't let selected cover entire job color
                {
                    g.setColor(colorArray[colCount]);
                    g.fillRect(x, y, w, (int)(h/6));
                }

                // Perhaps a band (called border because of old behavior):
                // first band for critical path:
                if (stripeCritPath && (schedule.critPath[j][m] > jobshopConstants.NOTCR))
                {
                    g.setColor(colorRed);
                    if ((int)(h/2) + (int)(h/6) < (int)(2*h/3))
                        g.fillRect(x, y + (int)(h/2), w, (int)(h/6) + 1);
                    else
                        g.fillRect(x, y + (int)(h/2), w, (int)(h/6));
                }
                // The next band for changed:
                if (stripeChanges && isChanged[j][m])
                {
                    g.setColor(colorPurple);
                    if ((int)(2*h/3) + (int)(h/6) < (int)(5*h/6))
                        g.fillRect(x, y + (int)(2*h/3), w, (int)(h/6) + 1);
                    else
                        g.fillRect(x, y + (int)(2*h/3), w, (int)(h/6));
                }
                // Lowest band shows mobilities:

                if (stripeMobil)
                {
                   Node node = problem.getNodes()[problem.jobNop2id(j,m)];
                   g.setColor(node.getMobilityColor());
                   // Fill the entire bottom with mobility stripe:
                   if ((int)(5*h/6) + (int)(h/6) < h)
                      g.fillRect(x, y + (int)(5*h/6), w, (int)(h/6) + 1);
                   else
                      g.fillRect(x, y + (int)(5*h/6), w, (int)(h/6));
                }

                g.setColor(colorBlack);
                // Mark selected:
                if (isSelected[j][m])
                {
                   // make select a little more obvious:
                    g.drawLine(x, y, x, y+h);
                    g.drawLine(x+1, y, x+1, y+h);
                    g.drawLine(x+w-2, y, x+w-2, y+h);
                    g.drawLine(x+w-1, y, x+w-1, y+h);
                    g.setColor(colorWhite);
                    g.drawRect(x-1, y-1, w+1, h+1);
                }
                else
                    g.drawRect(x-1, y-1, w+1, h+1);
                
		if ( marked[j][m] ) {
		    Graphics2D g2 = (Graphics2D) g;
		    g2.setStroke(NodeVisualization.threeStroke);
		    g.setColor(Color.black);
		    g.drawLine(x,y,x+w,y+h);
		    g2.setStroke(NodeVisualization.oneStroke);
		}
		
                // Put up the label:
                if (stripeJobId)
                {
                    if (isSelected[j][m])
                        g.setColor(colorWhite);
                    else
                        g.setColor(colorBlack);
                    g.drawString("" + j + "." + m,
                                (int)(x + w/2 - 2*fontSize/3),
                                (int)(y + h/6 + fontSize));
                }
            }
        }

        // Draw the selected Rectangle (if one exists):
        if (selectRect == null)
            return;
        g.setColor(colorBlack);
        g.drawRect((int)selectRect.getX(), (int)selectRect.getY(),
            (int)selectRect.getWidth() - 1, (int)selectRect.getHeight() - 1);
        g.drawRect((int)selectRect.getX() + 1, (int)selectRect.getY() + 1,
            (int)selectRect.getWidth() - 3, (int)selectRect.getHeight() - 3);

        // Show insertion point (if any):
        if (insertPoint == null)
            return;
        g.setColor(colorRed);
        int margin = (int)(SPACEV * scaleVert);
        g.drawLine(insertPoint.x - 1, insertPoint.y - margin,
            insertPoint.x - 1, insertPoint.y + h + margin - 1); // h is still set
        g.drawLine(insertPoint.x, insertPoint.y - margin,
            insertPoint.x, insertPoint.y + h + margin - 1);
    }


    //////////////////////////////////////////////////////////////////
    // Implement the MouseMotionListener (to listen for mouse moves):
    //////////////////////////////////////////////////////////////////
    public void mouseDragged(MouseEvent evt)
    {
        if (schedule == null)
            return;

        isDragging = true;      // Not a simple click

        if (isDraggingOp)
        {
            // With no ctrl-key, only this one is selected
            if (!evt.isControlDown())
            {
		/*
                // Unselect any others:
                for (int j=0; j<problem.jobCount; j++)
                    for (int m=0; m<problem.machineCount; m++)
                        isSelected[j][m] = false;
		*/
            }
            // Make this one selected:
            isSelected[selectJob][selectOp] = true;
            
            // Give paint the selection rectangle to paint:
            selectRect.translate(evt.getX() - oldX, 0);
            oldX = evt.getX();
            // Give paint the insertion point to paint:
            // (The insertion point is always on the left edge of the
            // operation BEFORE which the dragged op will be inserted.
            // so count from the front.)
            int job;
            int x, y;
            
            // Go through every job on this machine:
            int offset = 0;
            for (int mIdx=0; mIdx<problem.jobCount; mIdx++)
            {
                // Do each job in order on this machine:
                if (offset > 0)
                    offset++;
                job = schedule.machSeq[selectMach][mIdx];
                if (job == selectJob)
                    offset++;
                // Find the op of this job that is on this machine:
                int op;
                for (op=0; op<problem.machineCount; op++)
                    if (problem.machine[job][op] == selectMach)
                        break;

                x = (int)((schedule.start[job][op] * pixelsPerMin) + SPACEH);
                // Last op on the machine has special behavior:
                if (mIdx == problem.jobCount-1)
                {
                    int endX = x + (int)(problem.duration[job][op] * pixelsPerMin);
                    if (endX < selectRect.x)
                    {
                        insertPoint.x = endX;
                        insertPoint.y = (int)(((SPACEV * (problem.machine[job][op]+1)) + (MHEIGHT * problem.machine[job][op])) * scaleVert);
                        insertOrd = mIdx;
                        break;
                    }
                }
                if (x > selectRect.x)
                {
                    insertPoint.x = x;
                    insertPoint.y = (int)(((SPACEV * (problem.machine[job][op]+1)) + (MHEIGHT * problem.machine[job][op])) * scaleVert);
                    if (offset >= 2)
                        insertOrd = mIdx - 1;
                    else
                        insertOrd = mIdx;
                    break;
                }
            }
        }
        else // Selection Box
        {
            int     origX, origY;       // Top left corner
            int     width, height;      // Absolute value of diffs

            // Make sure h&w are positive for all quadrants:
            if (selectOrigX > evt.getX())
            {
                origX = evt.getX();
                width = selectOrigX - origX;
            }
            else
            {
                origX = selectOrigX;
                width = evt.getX() - origX;
            }
            if (selectOrigY > evt.getY())
            {
                origY = evt.getY();
                height = selectOrigY - origY;
            }
            else
            {
                origY = selectOrigY;
                height = evt.getY() - origY;
            }
            // Create the paintable Selection Box:    
            selectRect.setRect(origX, origY, width, height);
            // Also select any ops inside:
            Rectangle r = new Rectangle();
            for (int j=0; j<problem.jobCount; j++)
            {
                for (int m=0; m<problem.machineCount; m++)
                {
                    if (false && !evt.isControlDown())
                        isSelected[j][m] = false;   // Maybe user just excluded it
                    // fillRect (x, y, w, h)
                    r.setRect(
                        (int)((schedule.start[j][m] * pixelsPerMin) + SPACEH),
                        (int)(((SPACEV * (problem.machine[j][m]+1)) + (MHEIGHT * problem.machine[j][m])) * scaleVert),
                        (int)(problem.duration[j][m] * pixelsPerMin),
                        (int)(MHEIGHT * scaleVert));
                    if (r.intersects(selectRect))
                        isSelected[j][m] = true;
                }
            }
        }
        // Regardless of whether it's a mass select with a box or an individual
        // slide with the mouse, still gotta show it:
        repaint();
    }
    public void mouseMoved(MouseEvent evt)   { }


   public void selectAllJob () {
      // This may get tedious for large spaces:
      int jobNo = -1;
      for (int j=0; j<problem.jobCount; j++)
         for (int m =0; m < problem.machineCount; m++)
            if (isSelected[j][m] ) jobNo = j;
      if (jobNo >= 0)
         {
            for (int m=0; m<problem.machineCount; m++) 
               isSelected[jobNo][m] = true;
         }
      repaint();
      /*
        if ( parent != null )
        parent.setStatus(status);
      */
      return;
   }

    // Implement the MouseListener (to listen for mouse clicks):
    public void mouseClicked(MouseEvent evt)
    {
        // If no schedule, don't pay attention to the mouse:
        if (schedule == null)
            return;

        Rectangle r = new Rectangle();
        String status;

        // On double-click (left or right), select entire job:
        if (evt.getClickCount() == 2)
        {
            // This may get tedious for large spaces:
            status = " ";
            int jobNo = -1;
            for (int j=0; j<problem.jobCount; j++)
            {
                for (int m=0; m<problem.machineCount; m++)
                {
                    // fillRect (x, y, w, h)
                    r.setRect(
                        (int)((schedule.start[j][m] * pixelsPerMin) + SPACEH),
                        (int)(((SPACEV * (problem.machine[j][m]+1)) + (MHEIGHT * problem.machine[j][m])) * scaleVert),
                        (int)(problem.duration[j][m] * pixelsPerMin),
                        (int)(MHEIGHT * scaleVert));
                    // Ctrl-select for multiple selects:
                    if (false && !evt.isControlDown())
                        isSelected[j][m] = false;   // Turn off old selects (unless ctrl)
                    if (r.contains(evt.getX(), evt.getY()))
                        jobNo = j;
                }
            }
            if (jobNo >= 0)
            {
               for (int m=0; m<problem.machineCount; m++) 
                  isSelected[jobNo][m] = true;
               status = "Job " + jobNo;
            }
	    else 
		clearSelected();
            repaint();
            /*
            if ( parent != null )
               parent.setStatus(status);
            */
            return;
        }
        // This may get tedious for large spaces:
        status = " ";
        for (int j=0; j<problem.jobCount; j++)
        {
            for (int m=0; m<problem.machineCount; m++)
            {
                // fillRect (x, y, w, h)
                r.setRect(
                    (int)((schedule.start[j][m] * pixelsPerMin) + SPACEH),
                    (int)(((SPACEV * (problem.machine[j][m]+1)) + (MHEIGHT * problem.machine[j][m])) * scaleVert),
                    (int)(problem.duration[j][m] * pixelsPerMin),
                    (int)(MHEIGHT * scaleVert));
                // Ctrl-select for multiple selects:
                if (false && !evt.isControlDown())
                    isSelected[j][m] = false;
                if (r.contains(evt.getX(), evt.getY()))
                {
                    // Remember which one was clicked on (for drag & drop):
                    isSelected[j][m] = !isSelected[j][m];
                    isDraggingOp = false;
                    status = "Job " + j + " Op " + m +
                             ":   start@" + schedule.start[j][m] +
                             " end@" + (schedule.start[j][m] + problem.duration[j][m]);
                    // Normally we could return here but we
                    // have to turn off any selections that are beyond
                    // this operation, so let the loops finish out.
                }
            }
        }
        // Empty space means nothing selected, otherwise the
        // correct ones are selected now:
        repaint();
        /*
        if ( parent != null )
           parent.setStatus(status);
        */
    }
    public void mouseEntered(MouseEvent evt) { }    
    public void mouseExited(MouseEvent evt)  { }
    public void mousePressed(MouseEvent evt)
    {
        // If no schedule, don't pay attention to the mouse:
        if (schedule == null)
            return;
        
        Rectangle r = new Rectangle();
        isDragging = false;

        // If ctrl-select, can't be a sliding move
        // (this helps prevent accidentally wiping out
        // a complicated multi-select because the user's
        // hand happens to slip a little, looking like a drag):
        if (!evt.isControlDown()) // disable control thing
        {
            int x, y;
            // This may get tedious for large spaces:
            for (int j=0; j<problem.jobCount; j++)
            {
                for (int m=0; m<problem.machineCount; m++)
                {
                    // setRect (x, y, w, h)
                    x = (int)((schedule.start[j][m] * pixelsPerMin) + SPACEH);
                    y = (int)(((SPACEV * (problem.machine[j][m]+1)) + (MHEIGHT * problem.machine[j][m])) * scaleVert);
                    r.setRect(x, y,
                        (int)(problem.duration[j][m] * pixelsPerMin),
                        (int)(MHEIGHT * scaleVert));
                    if (r.contains(evt.getX(), evt.getY()))
                    {
                        isDraggingOp = true;
                        // Remember which one was clicked on (for drag & drop):
                        selectJob = j;
                        selectOp = m;
                        /*
                        if ( parent != null ) 
                           parent.setStatus("Job " + j + " Op " + m +
                                            ":   start@" + schedule.start[j][m] +
                                            " end@" + (schedule.start[j][m] + problem.duration[j][m]));
                        */
                        selectMach = problem.machine[j][m];
                        selectRect = r;
                        // Create an insertion point that gets recalculated at every drag:
                        // (mouseReleased kills it)
                        insertPoint = new Point();
                        insertPoint.x = x;
                        insertPoint.y = y;
                        oldX = evt.getX();
                        repaint();
                        return;
                    }
                }
            }
        }
        // Either clicked on an empty space or had ctrl key down
        // Start selection box:
        isDraggingOp = false;
        selectOrigX = evt.getX();
        selectOrigY = evt.getY();
        r.setRect(selectOrigX, selectOrigY, 1, 1);
        selectRect = r;
        /*
        if ( parent != null )
           parent.clearStatus();
        */
    }
   
    public void mouseReleased(MouseEvent evt)
    {
        if (selectRect == null)
            return;

        // Reorder and repack:
        if (isDragging && isDraggingOp)
        {
            // Pack is not guaranteed to work.  Some moves may lead to
            // deadlocks or cycles.  So don't manipulate the schedule until
            // we're sure everything is going to be cool.
            // Clone schedule:

           Move m = new JobshopInsertMove(schedule, selectJob,selectOp,selectMach,insertOrd);
           Hugs.THIS.manualMove(m);
           ((JobshopSolution)Hugs.THIS.getCurrentSolution()).print();
            System.out.println("JobshopVisualization: look into addSchedule");
           /*
            JobshopSolution packSched = jobshopScheduleOps.copy(schedule);

            packSched.insert(selectJob,selectOp,selectMach,insertOrd);
           
            if (jobshopScheduleOps.repack(packSched) == 0)
               setSchedule(packSched);
            packSched.print();
           */
           
            //parent.addSchedule(packSched);
            //             else
            {
                // Deadlock or cycle prevents packing:
               /*
               if ( parent != null )
                  parent.setStatus(jobshopConstants.ENOPACK);
               */
//                selectRect = null;
//                insertPoint = null;
//                repaint();
            }
        }
//        else
//        {
            // End the selection:
//            selectRect = null;
//            insertPoint = null;
//            repaint();
//        }
        selectRect = null;
        insertPoint = null;
        repaint();
    }

public ImageIcon getImageIcon (String name) {
    return new ImageIcon(getClass().getResource(name));
}

}
