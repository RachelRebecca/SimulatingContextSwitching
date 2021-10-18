package context_switch;

import java.util.ArrayList;
import java.util.Random;

public class Main
{
    private static final int QUANTUM = 5;

    private static SimProcessor processor;

    private static ArrayList<SimProcess> allProcesses;
    private static ArrayList<ProcessControlBlock> allPCBs;

    private static ArrayList<SimProcess> readyList;
    private static ArrayList<SimProcess> blockedList;


    private static ProcessControlBlock currentPCB;

    private static int numTimesPerProcess;

    private static boolean nextStepIsContextSwitch;
    private static boolean nextStepIsRestoreProcess;
    private static boolean nextStepIsNoAvailableProcess;
    private static boolean contextSwitchOccurredButNoAvailableProcesses;

    public static void main(String[] args)
    {

        initializeVariables();

        initializeProcesses();

        setUpMain();

        mainMethod();
    }

    private static void initializeVariables()
    {
        processor = new SimProcessor();

        allProcesses = new ArrayList<>();
        allPCBs = new ArrayList<>();

        readyList = new ArrayList<>();
        blockedList = new ArrayList<>();
    }

    private static void initializeProcesses()
    {
        //create 10 process objects and to accompany them, 10 PCB (set in the method)
        createProcessObject(0, "Microsoft Word", 101);
        createProcessObject(1, "Microsoft Excel", 152);
        createProcessObject(2, "PowerPoint", 263);
        createProcessObject(3, "FireFox", 251);
        createProcessObject(4, "Google Chrome", 399);
        createProcessObject(5, "Safari", 316);
        createProcessObject(6, "IntelliJ", 252);
        createProcessObject(7, "PyCharm", 186);
        createProcessObject(8, "CLion", 315);
        createProcessObject(9, "Android Studio", 199);
    }

    private static void createProcessObject(int PID, String ProcName, int totalInstructions)
    {
        SimProcess process = new SimProcess(PID, ProcName, totalInstructions);
        allProcesses.add(process); //Contains all processes
        allPCBs.add(new ProcessControlBlock(process)); //Contains all PCBs at same index as their corresponding process
    }

    private static void setUpMain()
    {
        //initialize the ready list by making all processes ready
        readyList.addAll(allProcesses);

        //initialize the current PCB
        currentPCB = allPCBs.get(0);

        //initialize processor object
        processor.setCurrProcess(currentPCB.getProcessObject());
        processor.setR1(currentPCB.getR1());
        processor.setR2(currentPCB.getR2());
        processor.setR3(currentPCB.getR3());
        processor.setR4(currentPCB.getR4());

        //initialize number of times looping through a process to zero,
        //in order to increase it by 1 when checking for a quantum
        numTimesPerProcess = 0;

        //initialize booleans which are used to determine the next processor step
        nextStepIsContextSwitch = false;
        nextStepIsRestoreProcess = false;
        nextStepIsNoAvailableProcess = false;
        contextSwitchOccurredButNoAvailableProcesses = false;
    }

    private static void mainMethod()
    {
        for (int i = 0; i < 3000; i++)
        {

            System.out.println("Processor Step #" + i);
            if (nextStepIsRestoreProcess)
                updateProcessorObject(); //either restore a different process from ready list
            else if (nextStepIsNoAvailableProcess)
                System.out.println("\t\t*** No available ready processes ***"); //or there are no available processes
            else if (nextStepIsContextSwitch)
                performContextSwitch(); //or you update PCB with current r values and instruction num in context switch
            else
                performNextExecute(); //or you execute the next instruction

            //at the end of each loop, wake up blocked processes 30% probability
            wakeUpBlockedProcesses();

            //to determine next steps, check if any process has been woken up from blocked state
            if (!nextStepIsContextSwitch)
                checkIfAnyProcessesAreAwake();
        }
    }

    private static void performNextExecute()
    {
        ProcessState state = processor.executeNextInstruction();
        numTimesPerProcess++; //increase this to check if reaches quantum

        if (state == ProcessState.FINISHED)
        {
            //a finished process is taken off all ready and blocked list and a context switch occurs
            readyList.remove(currentPCB.getProcessObject());
            blockedList.remove(currentPCB.getProcessObject());
            System.out.println("\t\t*** " + currentPCB.getProcessObject().getProcName() + " Process completed ***");
            nextStepIsContextSwitch = true;
        }
        else if (state == ProcessState.BLOCKED)
        {
            //a blocked process is taken off ready list and put onto blocked list and a context switch occurs
            readyList.remove(currentPCB.getProcessObject());
            if (!blockedList.contains(currentPCB.getProcessObject()))
                blockedList.add(currentPCB.getProcessObject());
            System.out.println("\t\t*** " + currentPCB.getProcessObject().getProcName() + " Process blocked ***");
            nextStepIsContextSwitch = true;

        }
        else if (numTimesPerProcess == QUANTUM)
        {
            //ready process has reached its number of turns with processor
            //it gets taken off ready list and put back on it and a context switch occurs
            System.out.println("\t\t*** " + currentPCB.getProcessObject().getProcName() + " Quantum expired ***");
            readyList.remove(currentPCB.getProcessObject());
            if (!readyList.contains(currentPCB.getProcessObject()))
                readyList.add(currentPCB.getProcessObject());
            nextStepIsContextSwitch = true;
        }
    }

    private static void performContextSwitch()
    {
        System.out.println("\t\tContext Switch: Saving process: " + processor.getCurrProcess().getProcName());
        System.out.println("\t\t" + processor);

        numTimesPerProcess = 0; //reset this value

        //the current PCB gets all the relevant information
        currentPCB.setCurrInstruction(processor.getCurrInstruction());
        currentPCB.setR1(processor.getR1());
        currentPCB.setR2(processor.getR2());
        currentPCB.setR3(processor.getR3());
        currentPCB.setR4(processor.getR4());

        nextStepIsContextSwitch = false; //reset this value

        //if there are more available processes then restore the next available one
        //otherwise, send a message that there are no available processes
        if (setNewCurrentPCB())
            nextStepIsRestoreProcess = true;
        else
        {
            contextSwitchOccurredButNoAvailableProcesses = true;
            nextStepIsNoAvailableProcess = true;
        }

    }

    private static boolean setNewCurrentPCB()
    {
        //if there are available ready processes, get the PCB of the currently oldest process on the ready list
        boolean currPCBFound = false;

        if (readyList.size() > 0)
        {
            //the current PCB is the one that has the same index as the process in the allProcesses ArrayList
            //which is the oldest process on the readyList
            for (int i = 0; i < allProcesses.size(); i++)
            {
                if (allProcesses.get(i) == readyList.get(0))
                {
                    currentPCB = allPCBs.get(i);
                    currPCBFound = true;
                }
            }
        }

        return currPCBFound;
    }

    private static void updateProcessorObject()
    {
        //restore a process currently on the ready list
        processor.setCurrProcess(readyList.get(0));
        System.out.println("\t\tContext Switch: Restoring process " + processor.getCurrProcess().getProcName());
        processor.setCurrInstruction(currentPCB.getCurrInstruction());
        processor.setR1(currentPCB.getR1());
        processor.setR2(currentPCB.getR2());
        processor.setR3(currentPCB.getR3());
        processor.setR4(currentPCB.getR4());
        System.out.println("\t\t" + processor);

        nextStepIsRestoreProcess = false; //reset this value
    }


    private static void wakeUpBlockedProcesses()
    {
        //loop through blocked list, wake up processes on the blocked list with 30% probability
        ArrayList<SimProcess> movingProcesses = new ArrayList<>();
        Random random = new Random();

        if (blockedList != null && blockedList.size() != 0)
        {
            for (int i = 0; i < blockedList.size(); i++)
            {
                boolean isAwake = random.nextInt(100) < 30; //30% probability
                if (isAwake)
                {
                    movingProcesses.add(blockedList.get(i));
                }
            }

            for (int i = 0; i < movingProcesses.size(); i++)
            {
                blockedList.remove(movingProcesses.get(i));
                if (!readyList.contains(movingProcesses.get(i)))
                    readyList.add(movingProcesses.get(i));
                System.out.print("\n\t\t--" + movingProcesses.get(i).getProcName() + " is no longer blocked.--\n");
            }
        }
    }

    private static void checkIfAnyProcessesAreAwake()
    {
        //if no processes are awake, the next step is going to be messaging no available processes
        if (readyList == null || readyList.size() == 0)
            nextStepIsNoAvailableProcess = true;
        else
        {
            nextStepIsNoAvailableProcess = false;
            //if a prior process was saved, and then there was no available process to restore,
            //it should now restore a process from the ready list, since a blocked process has woken up
            if (contextSwitchOccurredButNoAvailableProcesses)
            {
                nextStepIsRestoreProcess = true;
                contextSwitchOccurredButNoAvailableProcesses = false;
            }
        }
    }
}
