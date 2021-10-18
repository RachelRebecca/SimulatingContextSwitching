package context_switch;

import java.util.Random;

public class SimProcessor
{
    private SimProcess currProcess;
    private int r1;
    private int r2;
    private int r3;
    private int r4;
    private int currInstruction;

    public SimProcessor()
    {
        currProcess = new SimProcess(-1, "Process", 0); //in order to initialize it to something
        currInstruction = 0;
        //randomly initialize these values until they are set in Main
        r1 = setRegisterValue();
        r2 = setRegisterValue();
        r3 = setRegisterValue();
        r4 = setRegisterValue();
    }

    //current process is determined when restoring a process
    public void setCurrProcess(SimProcess currentProcess)
    {
        currProcess = currentProcess;
    }

    public SimProcess getCurrProcess()
    {
        return currProcess;
    }

    public int setRegisterValue()
    {
        Random random = new Random();

        //return a random register value between 0 and 10,000
        return random.nextInt(10001);
    }

    //four R values are set when restoring a process to that PCB's R values
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

    public void setCurrInstruction(int currentInstruction)
    {
        //set when restoring a process to that PCB's current instruction
        currInstruction = currentInstruction;
    }

    public int getCurrInstruction()
    {
        return currInstruction;
    }

    public ProcessState executeNextInstruction()
    {
        //wrapper method for process's execute method
        //execute a process's next instruction
        ProcessState state = currProcess.execute(currInstruction);
        incrementCurrInstruction();
        //change register values since an instruction was executed
        r1 = setRegisterValue();
        r2 = setRegisterValue();
        r3 = setRegisterValue();
        r4 = setRegisterValue();
        return state;
    }

    private void incrementCurrInstruction()
    {
        //in OS, this number is assigned to the current process's PCB currInstruction
        currInstruction++;
    }

    public String toString()
    {
        return "Instruction: " + getCurrInstruction() +
                ", R1: " + r1 +
                ", R2: " + r2 +
                ", R3: " + r3 +
                ", R4: " + r4;
    }

}
