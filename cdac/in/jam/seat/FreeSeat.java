package cdac.in.jam.seat;

public class FreeSeat{

	Quota quota;
	Applicant applicant;

	FreeSeat(Quota quota, Applicant applicant){
		this.quota = quota;
		this.applicant = applicant;
	}
	
	public void free(){
		quota.free( applicant );	
	}

}
