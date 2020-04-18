import java.util.*;
import java.util.concurrent.atomic.*;


interface Channel {
    int FREE = 0;       // Indicates Channel is free
    int INUSE = 1;      // Indicates Channel is being used
}

class NewThread implements Runnable, Channel {
    String StationNumber;
    Thread t;
    static int stat = 0;
    static int ChannelStatus;           // Indicates if channel is being used
    int FrameNumber, MaxFrameNumber;
    private AtomicBoolean CheckIfSuccessfulTransmission;
    static int transmissionTime = 50;   // Transmission time
    private int NumberOfAttempts= 0;

    NewThread(String threadname, int MaxFrameNumber) {
        this.StationNumber = threadname;
        t = new Thread(this, StationNumber);
        FrameNumber = 1;
        this.MaxFrameNumber = MaxFrameNumber;
        CheckIfSuccessfulTransmission = new AtomicBoolean();
        t.start();

    }

    public void run() {
        Random rand = new Random();
        while (!CheckIfSuccessfulTransmission.get()) {
            
            while (this.FrameNumber <= this.MaxFrameNumber) {
                if (this.NumberOfAttempts < 15) {            // 15 is the maximum number of attempts
                    this.NumberOfAttempts++;
                    System.out.println(StationNumber + " is trying to transmit frame number : " + FrameNumber);
                    if (ChannelStatus == INUSE) {           //collision occurs
                        System.out.println("Channel is busy");
                        try {
                        // Exponential Back Off Timer concept
                            System.out.println(StationNumber+ " Number of Attempts:"+ NumberOfAttempts);
                            System.out.println();
                            int X = rand.nextInt((int) (Math.pow(2, NumberOfAttempts) - 1));
                            int BackOffTime = X * transmissionTime;
                            Thread.sleep(BackOffTime);
                        }
                        
                        catch (InterruptedException e) {
                            System.out.println(("Interrupt"));
                        }
                        
                    } 
                    else {                              //if channel is free
                        ChannelStatus = INUSE;          // set channel to in use
                        CheckIfSuccessfulTransmission.set(true);
                        System.out.println(StationNumber + ":  frame:  " + FrameNumber + " is successful \n");
                        // simulate transmission over some distance
                        try{
                            Thread.sleep(transmissionTime);
                        }
                        catch (InterruptedException e) {
                            System.out.println(("Interrupt"));
                        }
                        FrameNumber++;
                        ChannelStatus = FREE;
                    }
                    
                } 
                else {
                    CheckIfSuccessfulTransmission.set(true);
                    System.out.println("Too many attempts for frame:  " + FrameNumber + " of station: " + StationNumber);
                    break;  
                }

            }
        }
    }
}



class pure_aloha implements Channel {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        NewThread.ChannelStatus = FREE;      // initially channel is free
        System.out.println("Enter number of stations (min number of stations is six) ");
        int NumberOfStations;
        while(true){
            NumberOfStations=sc.nextInt();
            if(NumberOfStations<6){
                System.out.println("Minimum number of stations is six, please re-enter!");
                continue;
            }
            else
                break;
        }
        NewThread Objects[] = new NewThread[NumberOfStations + 1];
        int FrameArray[] = new int[NumberOfStations + 1];
        for (int i = 1; i <= NumberOfStations; i++) {
            System.out.println("Enter number of frames for Station " + i);
            FrameArray[i] = sc.nextInt();
        }
        for(int i = 1;i<=NumberOfStations;i++)
            Objects[i] = new NewThread("Station "+ Integer.toString(i),FrameArray[i]);
            
        //wait for stations to complete transmission
        try {

          for(int i=1;i<=NumberOfStations;i++)
                Objects[i].t.join();
        }
        catch (InterruptedException e) {
            System.out.println("Main Thread Interrupted");
        }  
            
        System.out.println("Transmission completed.");
    }
} 