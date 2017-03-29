/**
 * jobshop
 * @copy (c) 2001 Mitsubishi Electric Research Lab
 * @version 1.0
 * @author Guy T. Schafer
 */

/** Operations that can be performed on jobshopSchedule
 */
package hugs.apps.jobshop;


public class operation
{
    public int job;
    public int op;
    public operation(int j, int o)
    {
        job = j; op = o;
    }
}

