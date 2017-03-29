/*
 * jobshopConstants.java
 *
 * Created on February 3, 2001, 12:48 PM
 */
package hugs.apps.jobshop;

import java.lang.*;
import java.util.*;
/**
 * This class holds all the constants used in the jobshop project
 *
 * @author  Guy T. Schafer
 * @version 1.0
 */
public class jobshopConstants
{
    // Window titles:
    public final static String TITLE    = "Jobshop Optimizer";
    public final static String TITLE2   = "  v1.0 by Guy T. Schafer";
    public final static String STBTITLE = "Algorithm Server Process ";
    public final static String HISTTITLE= "Solution History";
    public final static String GRPTITLE = "Group Solutions";
    public final static String NAMTITLE = "Group Identifier";

    // Top level menus:
    public final static String FILE     = "File";
    public final static String VIEW     = "View";
    public final static String HIST     = "History";
    public final static String SERVER   = "Servers";
    public final static String MOBIL    = "Mobilities";
    public final static String HELP     = "Help";

    // Tools and Menu Items:
    public final static String OPEN     = "Open";
    public final static String SAVE     = "Save";
    public final static String SAVEAS   = "Save As";
    public final static String EXIT     = "Exit";
//    public final static String TOOLBAR  = "Toolbar";
    public final static String ATTRIB   = "Attributes";
//    public final static String PRIOR    = "Mobility";
    public final static String SHOW     = "Show ";
    public final static String JOBCOL   = "Job Colors";
    public final static String JOBID    = "Job IDs";
    public final static String CHANGE   = "Changes";
    public final static String CRIT     = "Critical Path";
    // The first L here is actually an I so the L in Low
    // is underlined for the menu shortcut:
    public final static String LOW      = "Set seIected to Low Mobility";
    public final static String MED      = "Set selected to Medium Mobility";
    public final static String HIGH     = "Set selected to High Mobility";
    public final static String LOWALL   = "Set all to Low Mobility";
    public final static String MEDALL   = "Set all to Medium Mobility";
    public final static String HIGHALL  = "Set all to High Mobility";
    public final static String DOUBLE   = " (double-click to set all)";
    public final static String SCALE    = "Scale";
    public final static String FIT      = "Fit on page";
    public final static String ZOOMINH  = "Stretch Horizontally";
    public final static String ZOOMOUTH = "Shrink Horizontally";
    public final static String ZOOMINV  = "Stretch Vertically";
    public final static String ZOOMOUTV = "Shrink Vertically";
    public final static String GO       = "Go";
    public final static String STOP     = "Stop";
    public final static String LOOK     = "Display solution / Add solution to history";
    public final static String REL      = "Kill this server process";
    public final static String SPAN     = "Spansize - improving any of these machines is an improvement";
    public final static String PLY      = "Minimum ply - no fewer than this many swaps";
    public final static String UNDO     = "Earlier Solution";
    public final static String REDO     = "Later Solution";
    public final static String PROC     = "Request server process";
    public final static String CONTENTS = "Contents";
    public final static String ABOUT    = "About";
    public final static String CHOOSE   = "Choose search algorithm";
    public final static String SHARE    = "Share this solution with group";
    
    // Labels for controls:
    public final static String SPANLBL  = "Span ";
    public final static String PLYLBL   = "min ply ";
    public final static String SOLHIST  = " History ";
    public final static String GRPHIST  = " Shared solutions ";
    public final static String GRPID    = " Group ";
    public final static String GRPNAME  = " Name ";
    public final static String GETNAME  = "Enter your name";
    // Some labels are in menus (with dots) and tooltips (without dots):
    public final static String DOTS     = "...";

    // Accelerators:
    public final static char FACC       = 'f';
    public final static char VACC       = 'v';
    public final static char SACC       = 's';
    public final static char HACC       = 'h';
    public final static char OACC       = 'o';
    public final static char AACC       = 'a';
    public final static char XACC       = 'x';
    public final static char IACC       = 'i';
    public final static char GACC       = 'g';
    public final static char TACC       = 't';
    public final static char UACC       = 'u';
    public final static char CACC       = 'c';
    public final static char PACC       = 'p';
    public final static char RACC       = 'r';
    public final static char MACC       = 'm';
    public final static char LACC       = 'l';
    public final static char WACC       = 'w';
    public final static char DACC       = 'd';
    public final static char EACC       = 'e';
    public final static char JACC       = 'j';

    // Status bar has solution history:
    public final static String NOHIST   = "No solutions";

    // Status bar messages:
    public final static String GOSTATUS = "Start server process...";
    public final static String STOPSTATUS = "Stop server process";
    public final static String ENOPROC  = "Server cannot supply more power";
    public final static String FILEOPEN = "File opened";
    public final static String ALERT    = "Better solution found";
    public final static String GRPSTATUS= "New schedule from group";
    public final static String ALGSTATUS= "New schedule from server";
    public final static String SHOWING  = "Showing ";
    public final static String HIDING   = "Hiding ";
    

    // Group info:
    // The max number of members in each group
    // (Also the max number of groups before the numbers
    // wrap - hopefully, by the time the 10,000th group
    // is created, group 0 is gone.  But if it isn't
    // unpredictable--but bad--behavior will result.)
    public final static int MAXMEMBERS  = 10000;

    // Algorithm names (and count):
    public final static int ALGOCNT     = 3;
    public final static String[] ALGO   = { "Local Greedy",
                                            "Local Deep",
                                            "Tabu Search"
                                          };

    // File Dialog strings:
    public final static String FILTER   = "Jobshop files (*.jsp)";
    public final static String EXT      = ".jobshop";

    // Group Name Input Dialog:
    public final static String CANCEL   = "Cancel";

    // About Dialog strings:
    public final static String COPYRIGHT= "  (c) 2001 Mitsubishi Electric Research Lab";
    public final static String OK       = "OK";

    // Client status messages:
    public final static String CONNORB  = "    Connecting to ORB...";
    public final static String OKORB    = "    Connected to ORB.";
    public final static String ENOORB   = "    ERROR: Failed to acquire ORB. Check tnameserv is running.";
    public final static String CONNALGO = "    Finding Algorithm Server...";
    public final static String OKALGO   = "    Found Algorithm Server.";
    public final static String ENOALGO  = "    ERROR: Failed to find Algorithm Server. Cannot continue.";
    public final static String CONNGROUP= "    Finding Group Server...";
    public final static String OKGROUP  = "    Found Group Server.";
    public final static String MONITOR  = "    Group Monitor Mode ON.";
    public final static String ENOGROUP = "    ERROR: Failed to find Group Server. Cannot be Monitor.";
    public final static String WNOGROUP = "    WARNING: Failed to find Group Server. Working alone...";
    public final static String ENOPACK  = "Invalid move. Cannot pack schedule.";
    // Output file comments:
    public final static String DEFCOM1  = "// MachineCount  JobCount:";
    public final static String DEFCOM2  = "// Each line is a job, data format: {machine# jobDuration}";
    public final static String HISTCOM1 = "// History of solutions:";
    public final static String HISTCOM2 = "// Each line is a job, data format: {machine# jobDuration startTime}";
    public final static String SLBLCOM  = "// Maxspan = ";  // Schedule label comment

    // Critical path (it is either critcal because of job
    // or machine or both so make the flags OR-able):
    public final static int    NOTCR    = 0;
    public final static int    CRJOB    = 1;
    public final static int    CRMACH   = 2;
    // Mobilites:
    public final static int    LOWMOB   = 4;
    public final static int    MEDMOB   = 2;
    public final static int    HIGHMOB  = 1;
    // Flags for menu to tell client to move in history:
    public final static int    NEXT     = -2;
    public final static int    PREV     = -4;
}
