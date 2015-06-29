package cdac.in.jam.seat;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;


class PaperAllocation{

	String paper;
	List<Applicant> allocated;
	int applicants;

	PaperAllocation(String paper){
		this.paper = paper;
		this.applicants = 0;
		this.allocated = new ArrayList<Applicant>();
	}	

}

public class AllocationDetails{

	List<Applicant> allocatedApplicants = new ArrayList<Applicant>();
	List<Applicant> notAllocated = new ArrayList<Applicant>();
	Map<String, PaperAllocation> paperwiseAllocations = new HashMap<String, PaperAllocation>();
	String totalSeatsAfterSeatReduction = null;
    	int totalSeats = 0;

	void allocated(Applicant applicant){

		allocatedApplicants.add( applicant );

		PaperAllocation pwa = paperwiseAllocations.get( applicant.allocatedQuota.paper );
		if(  pwa == null ){
			pwa = new PaperAllocation( applicant.allocatedQuota.paper );
		}
		pwa.allocated.add( applicant );
		paperwiseAllocations.put( applicant.allocatedQuota.paper, pwa );
	}
	
	void notAllocated(Applicant applicant){
		notAllocated.add ( applicant );
	}

	void print(){

		System.out.println("-----------------------------------------------------------");
		if( Allocation.printTable ){

			Applicant.printHeaderTable();

			for(Applicant applicant:  allocatedApplicants){
				applicant.printAllocationTable( Allocation.round );
			}

		}else if( Allocation.printAllocation ){
			Applicant.printHeaderAllocation();
			for(Applicant applicant:  allocatedApplicants){
				applicant.printAllocation( Allocation.round );
			}
		}else{
		
			Applicant.header();
			for(Applicant applicant:  allocatedApplicants){
				applicant.print();
			}
		}	

		System.out.println("-----------------------------------------------------------");
		Applicant.header();
		for(Applicant applicant:  notAllocated){
			applicant.print();
		}

		System.out.println("-----------------------------------------------------------");
		Set<String> papers = paperwiseAllocations.keySet();

		for(String paper: papers){
			System.out.println(paper+", Allocated: "+paperwiseAllocations.get(paper).allocated.size()+", Total: "+paperwiseAllocations.get(paper).applicants );
		}

		System.out.println("-----------------------------------------------------------");
		System.out.println("Total Seats Avilable (all programs): "+ totalSeats );
		System.out.println("Total Seats Avilable (After Seat Reduction Due to Supernemeri): "+totalSeatsAfterSeatReduction );
		System.out.println("Total Allocation: "+ allocatedApplicants.size() );
		System.out.println("Total Not Allocation: "+ notAllocated.size() );
		System.out.println("Total candidate: "+ (allocatedApplicants.size() + notAllocated.size() ));
		
	}
}
