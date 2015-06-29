package cdac.in.jam.seat;

public class QuotaReadjust{
	   
	   String quota;
	   String programCode;
	   String paper;
	   int number;	 	

       QuotaReadjust(String quota, String programCode, String paper){
			this.quota = quota;
			this.programCode = programCode;	
			this.paper = paper;
			this.number = 0;
	   }
		
	   void print(){
			System.out.println(programCode+","+paper+", "+quota+", "+number);
	   }
}
