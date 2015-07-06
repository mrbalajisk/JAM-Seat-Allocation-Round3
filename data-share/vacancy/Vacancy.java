package vacancy;

import java.io.*;
import java.util.*;

class Allocation{

    int capacity;
    int allocation;

    Allocation(int capacity){
        this.capacity = capacity;
        this.allocation = 0;
    }

    int vacancy(){
        return capacity - allocation;
    }

}

class SeatMatrix{

    String program;
    String paper;
    String iit;

    Map<String, Allocation> quota;

    SeatMatrix(String program, String paper, String iit){
        this.program = program;
        this.paper = paper;
        this.iit = iit;
        quota = new LinkedHashMap<String, Allocation>();
    }

    static void header(){
        System.out.println("Program, Institute-Code, Paper, GEN, OBC, SC, ST, GEN-PwD, OBC-PwD, SC-PwD, ST-PwD");   
    }

    int totalVacancy(){
        return quota.get("GEN").vacancy() + quota.get("OBC").vacancy() + quota.get("SC").vacancy() + quota.get("ST").vacancy() + quota.get("GEN-PwD").vacancy() + quota.get("OBC-PwD").vacancy() + quota.get("SC-PwD").vacancy() + quota.get("ST-PwD").vacancy();
    }

    int totalAllocation(){
        return quota.get("GEN").allocation + quota.get("OBC").allocation + quota.get("SC").allocation + quota.get("ST").allocation + quota.get("GEN-PwD").allocation + quota.get("OBC-PwD").allocation + quota.get("SC-PwD").allocation + quota.get("ST-PwD").allocation;
            
    }

    int totalCapacity(){
        return quota.get("GEN").capacity + quota.get("OBC").capacity + quota.get("SC").capacity + quota.get("ST").capacity + quota.get("GEN-PwD").capacity + quota.get("OBC-PwD").capacity + quota.get("SC-PwD").capacity + quota.get("ST-PwD").capacity;
            
    }

    void print(boolean flag){
        System.out.println(program+", "+iit+", "+paper+", "+quota.get("GEN").allocation+", "+quota.get("OBC").allocation+", "+quota.get("SC").allocation+", "+quota.get("ST").allocation+", "+quota.get("GEN-PwD").allocation+", "+quota.get("OBC-PwD").allocation+", "+quota.get("SC-PwD").allocation+", "+quota.get("ST-PwD").allocation);

    }

    void print(){
        System.out.println(program+", "+iit+", "+paper+", "+quota.get("GEN").vacancy()+", "+quota.get("OBC").vacancy()+", "+quota.get("SC").vacancy()+", "+quota.get("ST").vacancy()+", "+quota.get("GEN-PwD").vacancy()+", "+quota.get("OBC-PwD").vacancy()+", "+quota.get("SC-PwD").vacancy()+", "+quota.get("ST-PwD").vacancy());

    }
}


class Vacancy{

    static Map<String, SeatMatrix> seatMatrixs = new TreeMap<String, SeatMatrix>();
    static int totalseats = 0;

    public static void main(String[] args){

        BufferedReader br = null; 

        try{

            br = new BufferedReader(new FileReader(new File("seatmatrix.csv") ) );
            String line = null;
            boolean header = true;
            while( (line = br.readLine() ) != null ){

                if( header ){
                    header = false;
                    continue;
                }

                String[] tk = line.split(",", -1);

                SeatMatrix sm = new SeatMatrix( tk[4].trim(), tk[0].trim(), tk[1].trim() );

                int GEN = 0;
                int OBC = 0;
                int SC = 0;
                int ST = 0;
                int GENPwD = 0;
                int OBCPwD = 0;
                int SCPwD = 0;
                int STPwD = 0;

                if( tk[5].trim().length() > 0) 
                    GEN = Integer.parseInt ( tk[5].trim() );
                if( tk[6].trim().length() > 0) 
                    OBC = Integer.parseInt ( tk[6].trim() );
                if( tk[7].trim().length() > 0) 
                    SC = Integer.parseInt ( tk[7].trim() );
                if( tk[8].trim().length() > 0) 
                    ST = Integer.parseInt ( tk[8].trim() );
                if( tk[10].trim().length() > 0) 
                    GENPwD = Integer.parseInt ( tk[10].trim() );
                if( tk[11].trim().length() > 0) 
                    OBCPwD = Integer.parseInt ( tk[11].trim() );
                if( tk[12].trim().length() > 0) 
                    SCPwD = Integer.parseInt ( tk[12].trim() );
                if( tk[13].trim().length() > 0) 
                    STPwD = Integer.parseInt ( tk[13].trim() );

               sm.quota.put("GEN", new Allocation(GEN - GENPwD) );
               sm.quota.put("OBC", new Allocation(OBC - OBCPwD) );
               sm.quota.put("SC", new Allocation(SC - SCPwD) );
               sm.quota.put("ST", new Allocation(ST - STPwD) ); 
               sm.quota.put("GEN-PwD", new Allocation( GENPwD ) ); 
               sm.quota.put("OBC-PwD", new Allocation( OBCPwD ) );
               sm.quota.put("SC-PwD", new Allocation( SCPwD ) );
               sm.quota.put("ST-PwD", new Allocation( STPwD ) );

                seatMatrixs.put( tk[4].trim()+""+tk[0].trim(), sm );
            }

            br = new BufferedReader(new InputStreamReader(System.in) );
            line = null;
            header = true;
            while( (line = br.readLine() ) != null ){

                if( header ){
                    header = false;
                    continue;
                }
                String []tk =  line.split(",");
                SeatMatrix sm = seatMatrixs.get( tk[0].trim()+""+tk[1].trim() );
                Allocation allocation = sm.quota.get( tk[2].trim() );    
                allocation.allocation++;
            }


            System.out.println("----------------------------- Vacancy Matrix --------------------------");
            SeatMatrix.header();
            Set<String> keys = seatMatrixs.keySet();
            int vacancy = 0;
            for(String key: keys){
                SeatMatrix sm = seatMatrixs.get(key);
                vacancy += sm.totalVacancy();
                sm.print();
            }

            System.out.println("----------------------------- Allocation Matrix --------------------------");

            SeatMatrix.header();
            keys = seatMatrixs.keySet();
            int allocation = 0;
            int capacity = 0;

            for(String key: keys){
                SeatMatrix sm = seatMatrixs.get(key);
                allocation += sm.totalAllocation();
                capacity += sm.totalCapacity();
                sm.print(true);
            }

            System.out.println("Total Capacity : "+capacity);
            System.out.println("Total Vacancy : "+vacancy);
            System.out.println("Total Allocation : "+allocation);

        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
