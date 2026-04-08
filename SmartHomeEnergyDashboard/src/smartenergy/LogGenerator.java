package smartenergy;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.*;

public class LogGenerator {

    private static final String[] appliances =
            {"AC","Cooler","Fan","LED","Refrigerator"};

    private static final Random random = new Random();
    
    private static void clearOldLogs() {

        for(int i=1;i<=10;i++){
            try(FileWriter fw = new FileWriter("log"+i+".txt", false)){
                // overwrite file (clears content)
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void start() {

        // 🔥 Clear old logs first
        clearOldLogs();

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(10);

        for(int i=1;i<=10;i++){

            final int fileNum = i;

            scheduler.scheduleAtFixedRate(() -> {
                writeLog("log"+fileNum+".txt");
            },0,1,TimeUnit.SECONDS);
        }
    }

    private static void writeLog(String file){

        try(FileWriter fw = new FileWriter(file,true)){

            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            for(String appliance: appliances){

                int watts = generateWatts(appliance);

                fw.write(time+" | "+appliance+" | "+watts+"\n");
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static int generateWatts(String a){

        switch(a){
            case "AC": return 1400+random.nextInt(300);
            case "Cooler": return 300+random.nextInt(100);
            case "Fan": return 60+random.nextInt(30);
            case "LED": return 8+random.nextInt(5);
            case "Refrigerator": return 250+random.nextInt(120);
        }
        return 100;
    }
}