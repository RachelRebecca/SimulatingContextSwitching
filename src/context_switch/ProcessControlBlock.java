package context_switch;

import java.util.Random;

public class ProcessControlBlock
{
    private SimProcess processObject;
    private int currInstruction;
    private int r1;
    private int r2;
    private int r3;
    private int r4;

    public ProcessControlBlock(SimProcess process)
    {
        setProcessObject(process);
        currInstruction = 0;
        //Initialize these values to something
        r1 = setRegisterValue();
        r2 = setRegisterValue();
        r3 = setRegisterValue();
        r4 = setRegisterValue();
    }

    public void setProcessObject(SimProcess processObject)
    {
        this.processObject = processObject;
    }

    public SimProcess getProcessObject()
    {
        return processObject;
    }

    //instruction number is set by Processor during a context switch
    public void setCurrInstruction(int currentInstruction)
    {
        if (currentInstruction >= 0)
        {
            currInstruction = currentInstruction;
        }
        else
            currInstruction = 0;
    }

    public int getCurrInstruction()
    {
        return currInstruction;
    }

    public int setRegisterValue()
    {
        //in this class, method is used only to initialize the variables
        Random random = new Random();

        //return a random register value between 0 and 10,000
        return random.nextInt(10001);
    }

    //four R values are set by Processor during a context switch
    public void setR1(int reg1)
    {
        r1 = reg1;
    }

    public void setR2(int reg2)
    {
        r2 = reg2;
    }

    public void setR3(int reg3)
    {
        r3 = reg3;
    }

    public void setR4(int reg4)
    {
        r4 = reg4;
    }

    public int getR1()
    {
        return r1;
    }

    public int getR2()
    {
        return r2;
    }

    public int getR3()
    {
        return r3;
    }

    public int getR4()
    {
        return r4;
    }

    public String toString()
    {
        return "\nInstruction: " + getCurrInstruction() +
                ", R1: " + r1 +
                ", R2: " + r2 +
                ", R3: " + r3 +
                ", R4: " + r4;
    }
}
