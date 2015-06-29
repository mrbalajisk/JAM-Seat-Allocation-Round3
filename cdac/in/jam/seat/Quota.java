package cdac.in.jam.seat;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Quota{

	String paper;
	String name;
	String printname;
	String programCode;

	int seat;
	int originalSeat;
	int allocated;
	int supernumeri;
	int openingRank;
	int closingRank;

	List<Applicant> allocatedCandidate;

	Quota(String programCode, String name, String printname, int seat, String paper){

		this.programCode = programCode;
		this.name = name;
		this.printname = printname;	
		this.seat = seat;
		this.originalSeat = seat;
		this.paper = paper;
		this.allocated = 0;
		this.supernumeri = 0;
		this.openingRank = 0;
		this.closingRank = 0; 
		allocatedCandidate = new ArrayList<Applicant>();    
	}

	public void allocate(Applicant applicant){

		allocatedCandidate.add( applicant ); 
		allocated++;

		if( allocated > seat && !applicant.lastRoundSeat ){
			applicant.isSupernumeri = true;
			supernumeri++;
		}    

		updateOpeningClosingRank();
	}

	public void free(Applicant applicant){

		allocatedCandidate.remove( applicant ); 
		allocated--;

		if( allocated <= seat ){
			supernumeri = 0;
		}    

		updateOpeningClosingRank();
	}

	public String toString(){
		return paper+", "+programCode+", "+printname;
	}

	public void print(){
		System.out.print(", "+paper+"#"+originalSeat+"#"+allocated+"#"+openingRank+"-"+closingRank+"#"+supernumeri); 
		//System.out.print(", "+(originalSeat - allocated) ); 
	}

	void updateOpeningClosingRank(){

		if( allocatedCandidate.size() > 0){
			SortByPaperRank sbpr = new SortByPaperRank();
			sbpr.paper = paper.trim();
			Collections.sort(allocatedCandidate, sbpr);
			this.openingRank = allocatedCandidate.get(0).ranks.get( paper ).rank;
			this.closingRank = allocatedCandidate.get( allocatedCandidate.size() - 1).ranks.get( paper ).rank;
		}
		else{

			this.openingRank = 0;
			this.closingRank = 0; 

		}
	}
}

