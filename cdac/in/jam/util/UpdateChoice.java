package cdac.in.jam.util;

import java.io.*;
import java.util.*;

public class UpdateChoice{

	static Map<String, String> rejectMap =  new HashMap<String, String>();

	public static void main(String[] args){
		try{

			String rejectFile = null;	
			String updateFile = null;
			
			int i = 0;
			while( i < args.length ){
					if( args[i].trim().equals("-r") ){
						rejectFile = args[i+1];
					}else if( args[i].trim().equals("-u") ){
						updateFile = args[i+1];
					}
			i++;
			}

			if( rejectFile == null ||  updateFile == null){
				System.out.println("java cdac.in.jam.seat.UpdateChoice -r <reject-applicant-file>  -u <update-chocie-file>");
				System.exit(0);
			}

			BufferedReader br = new BufferedReader(new FileReader(new File( rejectFile )) );
			String line = null;
			boolean header = true;
			while( (line = br.readLine()) != null ){
				
				if( header ){
					header = false;
					continue;
				}

				String[] tokens = line.split(",");	
				rejectMap.put( tokens[0].trim(), tokens[1].trim() );
			}


			br = new BufferedReader(new FileReader(updateFile) );
			line = null;
			header = true;
			while ( (line = br.readLine()) != null ){

					if( header ){
						header = false;
						continue;
					}

					String [] token =  line.split(",");
					String applicationId =  token[0].trim();
					String chocies =  token[1].trim();

					String program = rejectMap.get( applicationId );
					chocies = chocies.replaceAll(program+":V", program+":I");
					System.out.println("UPDATE sa_applicant set seat_preference_allocation = '"+chocies+"' where application_id = '"+applicationId+"';"); 
			}	

		}catch(Exception e){
			e.printStackTrace();
		}

	}

}

