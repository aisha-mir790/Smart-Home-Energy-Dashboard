package smartenergy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EnergyCalculator {

    private static final double RATE = 8.0; // ₹8 per unit

    public static Map<String, Double> calculateEnergy(){

        Map<String, Double> energy = new HashMap<>();

        for(int i=1;i<=10;i++){

            try(BufferedReader br =
                    new BufferedReader(new FileReader("log"+i+".txt"))){

                String line;

                while((line=br.readLine())!=null){

                    String[] parts = line.split("\\|");

                    if(parts.length==3){

                        String appliance = parts[1].trim();
                        int watts = Integer.parseInt(parts[2].trim());

                        double kwh = (watts*1.0)/360000.0;

                        energy.put(appliance,
                                energy.getOrDefault(appliance,0.0)+kwh);
                    }
                }

            }catch(IOException ignored){}
        }

        return energy;
    }

    public static double totalBill(){

        double totalEnergy=0;

        for(double e: calculateEnergy().values()){
            totalEnergy+=e;
        }

        return totalEnergy*RATE;
    }
    
}