package context_switch;

import java.util.Objects;
import java.util.Random;

public class SimProcess
{
    private int pid;
    private String procName;
    private int ttlInstructions;

    public SimProcess(int processID, String processName, int totalInstructions)
    {
        setPID(processID);
        setProcName(processName);
        setTotalInstructions(totalInstructions);
    }

    public void setPID(int processID)
    {
        if (processID >= 0)
        {
            pid = processID;
        }
        else
            pid = -1;
    }

    public void setProcName(String processName)
    {
        procName = Objects.requireNonNullElse(processName, "Process");
    }

    public void setTotalInstructions(int totalInstructions)
    {
        if (totalInstructions >= 0)
        {
            ttlInstructions = totalInstructions;
        }
        else
            ttlInstructions = -1;
    }

    public int getPID()
    {
        return pid;
    }

    public String getProcName()
    {
        return procName;
    }

    public int getTotalInstructions()
    {
        return ttlInstructions;
    }

    public ProcessState execute(int i)
    {
        //If instruction number is >= the total number of instructions, process is finished,
        //or process could blocked with a 15% probability. Otherwise, it stays on the ready list.
        System.out.println(this + ", executing instruction: " + i);
        if (i >= getTotalInstructions())
            return ProcessState.FINISHED;
        else if (determineBlocked())
             return ProcessState.BLOCKED;
        else
            return ProcessState.READY;
    }

    public boolean determineBlocked()
    {
        Random random = new Random();

        //if n between 0 and 99 is between 0 and 14 returns true
        return random.nextInt(100) < 15;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("\t\tProcess ID: ").append(getPID());
        builder.append(", Process Name: ").append(getProcName());

        return builder.toString();
    }
}
