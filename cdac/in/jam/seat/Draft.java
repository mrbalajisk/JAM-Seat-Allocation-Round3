package cdac.in.jam.seat;

import java.util.List;
import java.util.ArrayList;

public class Draft{

		String program;
		List<String> applicants;	
		List<String> newapplicants;	

		Draft(String program){
			this.program = program;
			applicants = new ArrayList<String>();
		    newapplicants = new ArrayList<String>();	
		}			
}  
