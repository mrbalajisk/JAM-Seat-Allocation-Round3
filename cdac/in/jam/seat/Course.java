package cdac.in.jam.seat;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

public class Course{

	String paperCode;	
	String centreCode;
	String couseName;;
	String programCode;
	Map<String, Quota> quotas;
	int aspirant;
	int totalSeats;

	Course(String paperCode, String centreCode, String programCode, String courseName, int gen, int obc, int sc, int st, int pwdGen, int pwdObc, int pwdSc, int pwdSt, int total){

		quotas = new LinkedHashMap<String, Quota>();
		this.paperCode = paperCode; 
		this.programCode = programCode;

		quotas.put("GEN", new Quota(programCode, "GEN", "GEN", gen, paperCode) ) ;
		quotas.put("OBC", new Quota(programCode, "OBC", "OBC", obc, paperCode) ) ;
		quotas.put("SC", new Quota(programCode, "SC", "SC", sc, paperCode) ) ;
		quotas.put("ST", new Quota(programCode, "ST", "ST", st, paperCode) ) ;
		quotas.put("GENPWD", new Quota(programCode, "GENPWD", "GEN-PwD", pwdGen, paperCode) ) ;
		quotas.put("OBCPWD", new Quota(programCode, "OBCPWD","OBC-PwD", pwdObc, paperCode) ) ;
		quotas.put("SCPWD", new Quota(programCode, "SCPWD", "SC-PwD", pwdSc, paperCode) ) ;
		quotas.put("STPWD", new Quota(programCode, "STPWD", "ST-PwD", pwdSt, paperCode) ) ;

		this.totalSeats = gen + obc + sc + st + pwdGen + pwdObc + pwdSc + pwdSt;	

		this.aspirant = 0;

		if( this.totalSeats != total){

			System.out.println(paperCode+":"+centreCode+":"+programCode+" Total Avilable Seat-count not matching, actual-total-count:"+this.totalSeats+", seat-matrix-count: "+total );
		}

	}	

	static void printHeader(){
		System.out.println("PaperCode, ProgramCode, Aaspirant, GEN-Quota, OBC-Quota, SC-Quota, ST-Quota, GEN-Pd-Quota, OBC-Pd-Quota, SC-Pd-Quota, ST-Pd-Quota");
	}

	void print(){
		System.out.print( paperCode+", "+programCode+", "+aspirant);
		Set<String> qts = quotas.keySet();
		for(String qt: qts){
			Quota quota = quotas.get( qt );
			quota.print();
		}    
		System.out.println();
	}
}
