package cdac.in.jam.seat;

import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;;

public class Allocation{

	private TreeMap<String, TreeMap<String,Course>> coursesMap = null; 
	private Map<String, List<Applicant>> paperWiseApplicant =  null;
	private List<Applicant> applicants = null;

	private List<String> rejectedApplicants = null;

	private Map<String, Rank> applicantRanks = null;
	private AllocationDetails allocationDetails = null;

	private boolean flagSuperNumeri = false;
	private boolean SuperNumeriReduced = false;
	public static boolean printAllocation = false;
	public static boolean printTable = false;
	public static String  round = "round2";

	static private Map<String, QuotaReadjust> readjustQuotas = new HashMap<String, QuotaReadjust>();
	static private Map<String, Draft> draftAllocation =  new TreeMap<String, Draft>();
	static private Map<String, LastRoundApplicant> lastRoundAllocation =  new TreeMap<String, LastRoundApplicant>();

	private void reset(){

		coursesMap = new TreeMap <String, TreeMap<String,Course> > ();
		paperWiseApplicant = new HashMap<String, List<Applicant>>();
		applicants = new ArrayList<Applicant>();
		rejectedApplicants = new ArrayList<String>();
		applicantRanks = new HashMap<String, Rank>();
		allocationDetails = new AllocationDetails();

	}


	private void readRejectedApplicant(String file, boolean fileHeader){
		BufferedReader br = null;
		try{

			br = new BufferedReader( new FileReader(new File(file) ) );
			String line = null;
			int lineno = 0;

			while( ( line = br.readLine() )!= null ){

				if( fileHeader ){
					fileHeader = false;
					continue;
				}	

				rejectedApplicants.add( line.trim() );
			}
		}catch(Exception e){	
			e.printStackTrace();
		}
	}

	private void readDraft(String file, boolean fileHeader){

		BufferedReader br = null;

		try{
			br = new BufferedReader( new FileReader(new File(file) ) );
			String line = null;
			int lineno = 0;

			while( ( line = br.readLine() ) != null ){

				lineno++;
				if( fileHeader ){
					fileHeader = false;
					continue;
				}
				String []tokens = line.split(",");

				if( tokens.length <  2){
					System.out.println("Error in reading "+file+" file");
					System.out.println("Line No:"+lineno+"::"+line);
					System.exit(0);

				}

				String applicant = tokens[0].trim();		
				String program = tokens[1].trim();		
				Draft draft = draftAllocation.get( program );
				if( draft == null ){
					draft = new Draft( program );
				}
				draft.applicants.add( applicant );
				draftAllocation.put( program, draft );
			}		

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( br != null)
					br.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void readApplicantRank(String file, boolean fileHeader){

		BufferedReader br = null;

		try{
			br = new BufferedReader( new FileReader(new File(file) ) );
			String line = null;
			int lineno = 0;

			while( ( line = br.readLine() ) != null ){

				lineno++;
				if( fileHeader ){
					fileHeader = false;
					continue;
				}
				String []tokens = line.split(",");

				if( tokens.length <  3){
					System.out.println("Error in reading "+file+" file");
					System.out.println("Line No:"+lineno+"::"+line);
					System.exit(0);

				}
				applicantRanks.put( tokens[0].trim(), new Rank( tokens[0].trim(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim() ) );
			}
			System.out.println("Total Qualified registrationIds: "+ applicantRanks.size() );

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( br != null)
					br.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}


	void readLastRoundAllocation(String file, boolean header){

		BufferedReader br = null;

		try{

			br = new BufferedReader( new FileReader(new File(file) ) );
			String line = null;

			while( (line = br.readLine()) != null ){
				if( header ){
					header = false;
					continue;
				}

				String[] tokens = line.split(",");

				String applicationId = tokens[0].trim();
				String program = tokens[1].trim();
				String paper = tokens[2].trim();
				String quota = tokens[3].trim();
				String choiceNo = tokens[4].trim();
				String upgrade =  tokens[5].trim();
				String provisional = tokens[6].trim();
				String acceptancePath = tokens[7].trim(); 
				String declarationPath = tokens[8].trim();
				String undertakingPath = tokens[9].trim();
				String challanPath = tokens[10].trim();

				String statusId = tokens[11].trim();

				lastRoundAllocation.put(applicationId, new LastRoundApplicant( applicationId, program, paper, quota, choiceNo, upgrade, provisional,acceptancePath, declarationPath, undertakingPath, challanPath, statusId));

				//System.out.println(applicationId+","+ program +","+ paper +", "+ quota +", "+ choiceNo+", "+upgrade +", "+ provisional +", "+acceptancePath+", "+ declarationPath+", "+ undertakingPath+", "+ challanPath+", "+ statusId );
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( br != null)
					br.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}

	void readApplicant(String file, boolean fileHeader){

		BufferedReader br = null;

		int status4count = 0;
		int status5count = 0;
		int status6count = 0;

		try{
			br = new BufferedReader( new FileReader(new File(file) ) );
			String line = null;
			int lineno = 0;
			int el = 0;

			while( ( line = br.readLine() ) != null ){

				lineno++;
				if( fileHeader ){
					fileHeader = false;
					continue;
				}

				String []tokens = line.split(",");
				if( tokens.length <  10){
					System.out.println("Error in reading "+file+" file ");
					System.out.println("Line No:"+lineno+"::"+line);
					System.out.println("Tokens expected 13 : found "+tokens.length);
				}

				String applicationId = tokens[0].trim();

				if( rejectedApplicants.contains( applicationId ) ) // Candidate not accepted the offer in privious round
					continue;

				/* Foreign National Candidate */

				if( applicationId.equals("M104H15") ){	
					System.out.println("Foreign candidate :["+applicationId+"] Not consider for Current Round Allocation\n");
					System.err.println("Foreign candidate :["+applicationId+"] Not consider for Current Round Allocation\n");
					continue;
				}

				String regId1 = tokens[1].trim();
				String regId2 = tokens[2].trim();
				String dob = tokens[3].trim();
				String category = tokens[4].trim();
				String pwd = tokens[5].trim();
				String orgPref = tokens[6].trim();
				String statusId = tokens[7].trim();
				String validPref = tokens[8].trim();

				if( statusId.equals("4") || statusId.equals("5") || statusId.equals("6") ){

					el++;
					if( statusId.equals("4") )
						status4count++;
					else if ( statusId.equals("5") )
						status5count++;
					else if ( statusId.equals("6") )
						status6count++;

					Applicant applicant = new Applicant( applicationId, validPref, orgPref, dob, category, pwd);

					Rank rank1 = applicantRanks.get( regId1 );

					if( rank1 != null && rank1.enrollmentId.equals( applicationId ) ){
						applicant.ranks.put( rank1.paperCode, rank1);
					}

					Rank rank2 = applicantRanks.get( regId2 );
					if( rank2 != null && rank2.enrollmentId.equals( applicationId ) ){
						applicant.ranks.put( rank2.paperCode, rank2);
					}


					if( rank1 != null && rank1.enrollmentId.equals( applicationId ) ){
						List<Applicant> applicants = paperWiseApplicant.get( rank1.paperCode );
						if( applicants == null)	
							applicants = new ArrayList<Applicant>();
						applicants.add( applicant );	
						paperWiseApplicant.put( rank1.paperCode, applicants );
					}

					if( rank2 != null && rank2.enrollmentId.equals( applicationId ) ){
						List<Applicant> applicants = paperWiseApplicant.get( rank2.paperCode );
						if( applicants == null)	
							applicants = new ArrayList<Applicant>();
						applicants.add( applicant );	
						paperWiseApplicant.put( rank2.paperCode, applicants );
					}

					applicants.add( applicant );
				}
			}

			System.out.println("Total Eligibale Candidate for Allocation: "+el);
			System.out.println("Candidate with status id 4: "+status4count);
			System.out.println("Candidate with status id 5: "+status5count);
			System.out.println("Candidate with status id 6: "+status6count);

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( br != null)
					br.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}		

	void lastRoundAllocation(){

			int allocation  = 0;

			for(Applicant applicant: applicants){

					LastRoundApplicant lastRoundApplicant = lastRoundAllocation.get( applicant.applicationId );	

					if( lastRoundApplicant == null )
						continue;
				
					if( lastRoundApplicant.statusId == 3 ){
						
							allocation++;

							String paper = lastRoundApplicant.paper;
							String program = lastRoundApplicant.program;
							String quotaType = lastRoundApplicant.quota;

							String []toks = quotaType.split("-", -1);
							if( toks.length == 2){
								quotaType = toks[0].trim()+"PWD";	
							}

							Map<String, Course> courses = coursesMap.get( paper );
							Course course = courses.get( program );	
							Quota quota = course.quotas.get( quotaType );	

							applicant.autoUpgrade = lastRoundApplicant.autoUpgrade; 

							if( applicant.autoUpgrade )
								applicant.statusId = 4;
							else
								applicant.statusId = 3;

							applicant.isSubmitted = true;

							applicant.lastRoundSeat = true;

							quota.allocate( applicant );

							applicant.allocatedQuota = quota;
							applicant.lastRoundQuota = quota.name;

							applicant.isAllocated = true;
							applicant.isSupernumeri = false;

							applicant.allocatedChoice =  lastRoundApplicant.choiceNo;
							applicant.lastRoundChoiceNo = applicant.allocatedChoice;
							applicant.isProvisional = lastRoundApplicant.isProvisional;

							applicant.acceptancePath = lastRoundApplicant.acceptancePath;
							applicant.challanPath = lastRoundApplicant.challanPath;
							applicant.declarationPath = lastRoundApplicant.declarationPath;
							applicant.undertakingPath = lastRoundApplicant.undertakingPath;

							if( quota.allocated > quota.seat && quotaType.indexOf("PwD") < 0 ){

                                quota.seat++;
								Quota qu = course.quotas.get( quotaType+"PWD" );					
								qu.seat--;
							//	System.out.println("Converted Seat form Program name:"+ course.programCode+" quotaType: "+quotaType+" From: "+quotaType+"PWD");	
							}

					}
			}	
			System.out.println("Total allocation accepted form last Round: "+allocation);
	}

	void readCourse(String file, boolean fileHeader){

		BufferedReader br = null;
		String line = null;
		try{

			br = new BufferedReader( new FileReader(new File(file) ) );

			int lineno = 0;
			while( ( line = br.readLine() ) != null ){	

				lineno++;

				if( fileHeader ){
					fileHeader = false;
					continue;
				}

				String []tokens = line.split(",");

				if( tokens.length <  13){
					System.out.println("Error in reading Seat Matrix file");
					System.out.println("Line No:"+lineno+"::"+line);
				}
				String paperCode = tokens[0].trim();
				String instituteCode = tokens[1].trim();
				String instituteName = tokens[2].trim();
				String program = tokens[3].trim();
				String programCode = tokens[4].trim();

				int gen = 0;
				if( tokens[5].trim().length() > 0)
					gen = Integer.parseInt( tokens[5].trim() );

				int obc = 0;
				if( tokens[6].trim().length() > 0)
					obc = Integer.parseInt( tokens[6].trim() );

				int sc = 0;
				if( tokens[7].trim().length() > 0)
					sc = Integer.parseInt( tokens[7].trim() );

				int st = 0;
				if( tokens[8].trim().length() > 0)
					st = Integer.parseInt( tokens[8].trim() );

				int total = 0;
				if( tokens[9].trim().length() > 0)
					total = Integer.parseInt( tokens[9].trim() );


				int pwdGen = 0;
				if( tokens[10].trim().length() > 0)
					pwdGen = Integer.parseInt( tokens[10].trim() );

				int pwdObc = 0;
				if( tokens[11].trim().length() > 0)
					pwdObc = Integer.parseInt( tokens[11].trim() );

				int pwdSc = 0;
				if( tokens[12].trim().length() > 0)
					pwdSc = Integer.parseInt( tokens[12].trim() );

				int pwdSt = 0;
				if( tokens[13].trim().length() > 0)
					pwdSt = Integer.parseInt( tokens[13].trim() );

				Course course = new Course( paperCode, instituteCode, programCode, program, gen - pwdGen, obc - pwdObc, sc - pwdSc, st - pwdSt, pwdGen, pwdObc, pwdSc, pwdSt, total);
				TreeMap<String, Course> map = coursesMap.get( paperCode );

				if( map == null ){
					map = new TreeMap<String, Course>();
				}
				map.put( course.programCode, course );
				coursesMap.put( paperCode, map);
				allocationDetails.totalSeats += course.totalSeats;
			}
			allocationDetails.totalSeatsAfterSeatReduction = totalSeats();

		}catch(Exception e){
			System.out.println( line );
			e.printStackTrace();

		}finally{

			try{
				if( br != null)
					br.close();

			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}


	void detailsAllocation(){

		for(Applicant applicant: applicants){

			if( applicant.isAllocated )

				allocationDetails.allocated( applicant );
			else
				allocationDetails.notAllocated( applicant );
		}

		Set<String> papers = paperWiseApplicant.keySet();

		for(String paper: papers){

			allocationDetails.paperwiseAllocations.get(paper).applicants = paperWiseApplicant.get(paper).size();
		}

		allocationDetails.totalSeatsAfterSeatReduction = totalSeats();
		Collections.sort( allocationDetails.allocatedApplicants, new SortByAllocatedPaperRank() ) ;
		allocationDetails.print();	
	}


	private void printPaperWise(String paper, List<Applicant> applicants){

		Applicant.printHeader1( );
		for(Applicant applicant: applicants){
			applicant.print1( paper );	
		}	
	}

	void SortApplicants(){

		Set<String> papers = paperWiseApplicant.keySet();
		for(String paper: papers){
			List<Applicant> applicants = paperWiseApplicant.get(paper);
			SortByPaperRank sbpr = new SortByPaperRank();
			sbpr.paper = paper.trim();
			Collections.sort(applicants, sbpr);
			paperWiseApplicant.put( paper, applicants );
			System.out.println("Eligibale Candidate for: "+paper+" is: "+applicants.size());
			//printPaperWise(paper, applicants);	
		}
	}

	void printCourse(){
		Course.printHeader();
		Set<String> papers = coursesMap.keySet();
		int totalSeat = 0;
		for(String paper: papers){
			TreeMap<String, Course> courses = coursesMap.get( paper );
			Set<String> coursesCodes =  courses.keySet();
			for(String code: coursesCodes){
				Course course = courses.get( code );
				course.print();
			}
		}
	}

	boolean allocate(String type, Course course, Applicant applicant, int choiceNo, List<FreeSeat> freeSeats){

		Quota quota = course.quotas.get( type );

		/* seat avlibility */ 

		if( ( quota.seat - quota.allocated )  > 0 ){             

			if( applicant.allocatedQuota != null ){

				freeSeats.add( new FreeSeat( applicant.allocatedQuota, applicant ) );

			}

			quota.allocate( applicant );

			applicant.allocatedQuota = quota;

			applicant.isAllocated = true;

			applicant.isSupernumeri = false;

			if( applicant.autoUpgrade ){
				
				if(	applicant.allocatedChoice > choiceNo){			

					applicant.acceptancePath = null;	
					applicant.declarationPath = null;
					applicant.undertakingPath = null;	

					applicant.isProvisional = false;
					applicant.lastRoundSeat = false;
					applicant.isSubmitted = false;

                    if( applicant.statusId == 4)
                         applicant.statusId = 3;   
                    else if( applicant.statusId != 3 )
					    applicant.statusId = 1;
			    }

				applicant.allocatedChoice = choiceNo;
			}
			return true;
		}

		/* Supernumeri case*/

		if( flagSuperNumeri ){

			if( ( ( quota.seat - quota.allocated )  <= 0 ) && ( quota.closingRank != 0 ) 
					&& (quota.closingRank >= applicant.ranks.get( quota.paper ).rank ) ){      
				if( applicant.allocatedQuota != null ){
					freeSeats.add( new FreeSeat( applicant.allocatedQuota, applicant ) );
				}

				quota.allocate( applicant );
				applicant.allocatedQuota = quota;
				applicant.isAllocated = true;
				applicant.allocatedChoice = choiceNo;
				applicant.isSupernumeri = true;
				return true;
			}   

		}	

		return false;    
	}

	boolean allocation(String paper, List<Applicant> applicants, Map<String, Course> courses){

		boolean flag = false;

		List<FreeSeat> freedSeats = new ArrayList<FreeSeat>();

		for(Applicant applicant: applicants){

			for( int choiceNo = 0; choiceNo < applicant.validChoices.length && choiceNo <= applicant.allocatedChoice; choiceNo++){

				Course course = courses.get( applicant.validChoices[ choiceNo ] );

				if( course != null ){

					if( choiceNo == applicant.allocatedChoice ){           /* same-choice  */

						if( applicant.allocatedQuota.name.equals("GEN") ){
							break;    
						}

						if( allocate("GEN", course, applicant, choiceNo, freedSeats ) ){
							flag = true;

							//if( applicant.statusId == 3 || applicant.statusId == 4 )
							//	System.out.println("Quota Change: "+applicant.applicationId);
							break;    
						}    
				

				    }else if ( applicant.autoUpgrade  ) {                                                 /* better-choice */

						if( allocate("GEN", course, applicant, choiceNo, freedSeats ) ){
							flag = true;
							break;      
						}else if( ! applicant.category.equals("GEN") && allocate(applicant.category, course, applicant, choiceNo, freedSeats ) ){
							flag = true;
							break;      
						}else if( applicant.isPd  && allocate( applicant.category+"PWD", course, applicant, choiceNo, freedSeats ) ){
							flag = true;
							break;      
						}
					}
				}        
			}
		}

		System.err.println("Freeing-Seats: "+paper+"=> "+freedSeats.size() );

		for(FreeSeat freeSeat: freedSeats){
			freeSeat.free();
		}

		freedSeats = null;
		return flag; 
	}

	void allocationVerification(){

		Set<String>	papers = paperWiseApplicant.keySet();

		int count = 0;

		System.out.println("Sr.No, ApplicationId, Paper, ProgramCode, Program-Quota, Program-Closing Rank, Applicant-Rank");

		for(String paper: papers){

			List<Applicant> applicants = paperWiseApplicant.get(paper);
			Map<String, Course> courses = coursesMap.get( paper );

			for(Applicant applicant: applicants){

				if( ! applicant.autoUpgrade )
					continue;

				//System.out.println(applicant.applicationId+", UPgrade: "+applicant.autoUpgrade );

				//System.out.println(applicant.applicationId+", "+applicant.allocatedChoice );

				for(int i = 0; i < ( applicant.allocatedChoice - 1) && i < applicant.validChoices.length; i++){

					Course course = courses.get( applicant.validChoices[i] );

					if( course != null ){

						if( course.quotas.get("GEN").closingRank >= applicant.ranks.get(paper).rank ){
							count++;
							System.out.println(count+". "+applicant.applicationId+", "+paper+", "+course.programCode+", GEN, "+ course.quotas.get("GEN").closingRank+", "+applicant.ranks.get(paper).rank);

						}
						if( !applicant.category.equals("GEN") && (course.quotas.get(applicant.category ).closingRank >= applicant.ranks.get(paper).rank ) ) {            
							count++;
							System.out.println(count+". "+applicant.applicationId+", "+paper+", "+course.programCode+", "+applicant.category+", "+course.quotas.get( applicant.category ).closingRank+", "+applicant.ranks.get(paper).rank);

						}    
						if( applicant.isPd && (course.quotas.get( applicant.category+"PWD" ).closingRank >= applicant.ranks.get(paper).rank ) ){            
							count++;
							System.out.println(count+". "+applicant.applicationId+", "+paper+", "+course.programCode+", "+applicant.category+"PWD , "+course.quotas.get( applicant.category+"PWD" ).closingRank+", "+applicant.ranks.get(paper).rank);

						}

					}
				}    
			}
		}
		if( count == 0){
			System.out.println("------------------- No case found --------------");
		}
	}



	private boolean supernumeriIssue(){

		Set<String>	papers = paperWiseApplicant.keySet();

		System.out.println("------------- Supernumeri cases found in allocation -----------");

		for(String paper: papers){

			List<Applicant> applicants = paperWiseApplicant.get(paper);
			Map<String, Course> courses = coursesMap.get( paper );

			for(Applicant applicant: applicants){

				if( !applicant.autoUpgrade )
					continue;

				for(int i = 0; i < ( applicant.allocatedChoice - 1) && i < applicant.validChoices.length; i++){

					Course course = courses.get( applicant.validChoices[i] );

					if( course != null ){

						Quota quota = null;

						if( course.quotas.get("GEN").closingRank >= applicant.ranks.get(paper).rank ){

							quota = course.quotas.get("GEN");
						}
						else if( !applicant.category.equals("GEN") && (course.quotas.get(applicant.category ).closingRank >= applicant.ranks.get(paper).rank ) ) {            
							quota = course.quotas.get( applicant.category );
						}    
						else if( applicant.isPd && (course.quotas.get( applicant.category+"PWD" ).closingRank >= applicant.ranks.get(paper).rank ) ){            
							quota = course.quotas.get( applicant.category+"PWD" );
						}

						if( quota != null){

							String key = quota.name+""+quota.programCode+""+quota.paper;

							QuotaReadjust readjustQuota = readjustQuotas.get(key);

							if( readjustQuota == null){

								readjustQuota = new QuotaReadjust( quota.name, quota.programCode, quota.paper );																  }

							while( quota.closingRank == applicant.ranks.get(paper).rank ){

								Applicant app = quota.allocatedCandidate.remove( quota.allocatedCandidate.size() - 1); 
								System.out.println(app.applicationId+", "+app.allocatedQuota.name+", "+app.allocatedQuota.paper+", "+app.allocatedQuota.programCode+", "+app.ranks.get( app.allocatedQuota.paper ).rank );				
								quota.updateOpeningClosingRank();
								readjustQuota.number++;
							}

							if( readjustQuota.number > 0 ){
								readjustQuotas.put( key, readjustQuota );
								return true;
							}else{
								return false;
							}
						}

					}else{
						//System.out.println("System error while supernumeriIssue");
						//System.exit(0);
					}       
				}    
			}
		}
		return false;	
	}

	private String totalSeats(){

		Set<String> papers = coursesMap.keySet();

		int total =  0;
		int gen = 0;
		int obc = 0;
		int sc = 0;
		int st = 0;
		int pwdGen = 0;
		int pwdObc = 0;
		int pwdSc = 0;
		int pwdSt = 0;

		for(String paper: papers){

			Map<String, Course>  courses = coursesMap.get(paper);
			Set<String> programCodes = courses.keySet();
			for(String programCode: programCodes){

				Course course = courses.get( programCode );
				total += course.totalSeats;
				gen += course.quotas.get("GEN").seat; 
				obc += course.quotas.get("OBC").seat;
				sc  += course.quotas.get("SC").seat;
				st  += course.quotas.get("ST").seat;
				pwdGen += course.quotas.get("GENPWD").seat;
				pwdObc += course.quotas.get("OBCPWD").seat;
				pwdSc += course.quotas.get("SCPWD").seat;
				pwdSt += course.quotas.get("STPWD").seat;
					
			}
		}

		return "GEN: "+gen+", OBC: "+obc+", SC: "+sc+", ST: "+st+", GEN-PwD: "+pwdGen+", OBC-PwD: "+pwdObc+", SC-PwD: "+pwdSc+", ST-PwD: "+pwdSt+", TOTAL: "+total;
	}


	void pwdSeatConversion(){

		Set<String>  papers = coursesMap.keySet();
		for(String paper: papers){

			Map<String, Course>  courses = coursesMap.get(paper);
			Set<String> programCodes = courses.keySet();
			for(String programCode: programCodes){

				Course course = courses.get( programCode );

				if( course.quotas.get("GENPWD").seat - course.quotas.get("GENPWD").allocated > 0 ){

					System.err.println(paper+" "+programCode+" GENPWD "+(course.quotas.get("GENPWD").seat - course.quotas.get("GENPWD").allocated) );
					System.out.println(paper+" "+programCode+" GENPWD "+(course.quotas.get("GENPWD").seat - course.quotas.get("GENPWD").allocated) );
					course.quotas.get("GEN").seat += ( course.quotas.get("GENPWD").seat - course.quotas.get("GENPWD").allocated );
					course.quotas.get("GEN").originalSeat += ( course.quotas.get("GENPWD").seat - course.quotas.get("GENPWD").allocated );

					course.quotas.get("GENPWD").seat = 0;

				}
				if( course.quotas.get("OBCPWD").seat - course.quotas.get("OBCPWD").allocated > 0 ){

					System.err.println(paper+" "+programCode+" OBCPWD "+(course.quotas.get("OBCPWD").seat - course.quotas.get("OBCPWD").allocated) );
					System.out.println(paper+" "+programCode+" OBCPWD "+(course.quotas.get("OBCPWD").seat - course.quotas.get("OBCPWD").allocated) );
					course.quotas.get("OBC").seat += ( course.quotas.get("OBCPWD").seat - course.quotas.get("OBCPWD").allocated );
					course.quotas.get("OBC").originalSeat += ( course.quotas.get("OBCPWD").seat - course.quotas.get("OBCPWD").allocated );

					course.quotas.get("OBCPWD").seat = 0;

				}
				if( course.quotas.get("SCPWD").seat - course.quotas.get("SCPWD").allocated > 0 ){

					System.err.println(paper+" "+programCode+" SCPWD "+(course.quotas.get("SCPWD").seat - course.quotas.get("SCPWD").allocated) );
					System.out.println(paper+" "+programCode+" SCPWD "+(course.quotas.get("SCPWD").seat - course.quotas.get("SCPWD").allocated) );
					course.quotas.get("SC").seat += ( course.quotas.get("SCPWD").seat - course.quotas.get("SCPWD").allocated );
					course.quotas.get("SC").originalSeat += ( course.quotas.get("SCPWD").seat - course.quotas.get("SCPWD").allocated );

					course.quotas.get("SCPWD").seat = 0;

				}
				if( course.quotas.get("STPWD").seat - course.quotas.get("STPWD").allocated > 0 ){

					System.err.println(paper+" "+programCode+" STPWD "+(course.quotas.get("STPWD").seat - course.quotas.get("STPWD").allocated) );
					System.out.println(paper+" "+programCode+" STPWD "+(course.quotas.get("STPWD").seat - course.quotas.get("STPWD").allocated) );
					course.quotas.get("ST").seat += ( course.quotas.get("STPWD").seat - course.quotas.get("STPWD").allocated );
					course.quotas.get("ST").originalSeat += ( course.quotas.get("STPWD").seat - course.quotas.get("STPWD").allocated );

					course.quotas.get("STPWD").seat = 0;
				}
			}

		} 

	}

	private boolean readjustment(){

		boolean flag = supernumeriIssue(); 

		if( flag ){
			System.out.println("---------------- Seat To Be Reducted for: "+readjustQuotas.size()+" Courses ---------------");
			Set<String> quotas = readjustQuotas.keySet();
			for(String key: quotas ){
				readjustQuotas.get( key ).print( );
			}
			System.out.println("---------------- Reset the data for reallocation ---------------");
			read(); 	
			quotas = readjustQuotas.keySet();
			for(String key: quotas ){
				QuotaReadjust qr = readjustQuotas.get( key );
				Map<String, Course>  courses = coursesMap.get( qr.paper );
				if( courses != null ){
					Course course = courses.get( qr.programCode );
					Quota quota = course.quotas.get( qr.quota );	
					course.totalSeats -= qr.number;
					quota.seat -= qr.number;
				}else{
					System.out.println("System Error while readjusment!");
					System.exit(0);
				}
			}
			System.out.println("Total Seats After Reduced: "+totalSeats());
			System.out.println("-----------------------------------------------------------");
			return true;	
		}
		System.out.println("Total Seats After Reduced: "+totalSeats());
		System.out.println("-----------------------------------------------------------");
		return false;
	}

	private void read(){

		reset();	

		System.out.println("-----------------------Data Reading -----------------------");

		readCourse("./data/seat-matrix.csv", true);
		readApplicantRank("./data/ranks.csv", true);
		readRejectedApplicant("./data/rejected.csv", true);
		readLastRoundAllocation("./data/lastRoundAllocation.csv", true);
		readApplicant("./data/applicant-choices.csv", true);
		SortApplicants();

		lastRoundAllocation();

		System.out.println("-----------------------------------------------------------");
		System.out.println("Total Seats After Reading: "+totalSeats());
		System.out.println("-----------------------------------------------------------");
	}

	private void allocation(int iterration){

		System.out.println("Global Interation: "+iterration);	
		System.err.println("Global Interation: "+iterration);	

		paperWiseallocation();	

		System.err.println("PWD-Seat-Conversion");	
		System.out.println("PWD-Seat-Conversion");	

		pwdSeatConversion();

		paperWiseallocation();

		if( SuperNumeriReduced && readjustment() ){
			allocation( iterration + 1 ); 	
		}
	}

	void paperWiseallocation(){

		boolean flag = true;
		int i = 0;
		while( flag ){

			flag = false;
			Set<String>	papers = paperWiseApplicant.keySet();
			for(String paper: papers){

				if( allocation( paper, paperWiseApplicant.get(paper), coursesMap.get( paper ) ) ){
					flag = true;				
				}

			}
			i++;
			System.err.println("All-paper-allocation-Iteration: "+i);

			System.out.println("All-paper-allocation-Iteration: "+i);
		}
	}

	private void notInDraft(){

		List<Applicant> applicants = allocationDetails.allocatedApplicants;
		Applicant.printHeader3();

		for(Applicant applicant: applicants){

			String program = applicant.allocatedQuota.programCode;

			Draft draft = draftAllocation.get( program );

			if( draft == null ){
				System.out.println("No Draft Found for: "+program);
				System.err.println("No Draft Found for: "+program);
			}else{
				if( ! draft.applicants.contains( applicant.applicationId ) ){
					draft.newapplicants.add( applicant.applicationId );        
					applicant.print3();
				}
			}
		}

		Set<String> programs = draftAllocation.keySet();
		int count = 0;
		for(String program: programs){

			List<String> napplicants =  draftAllocation.get( program ).newapplicants;
			if( napplicants.size() > 0 ){
				System.out.print(program+": ");
				boolean flag = true;
				for(String applicant: napplicants){
					if( flag )
						System.out.print(applicant);
					else
						System.out.print(", "+applicant);

					flag = false;    
					count++;    
				}   
				System.out.println(); 
			}
		}    

		System.out.println("Total "+count+" Not in last-draft Allocation" );
		System.err.println("Total "+count+" Not in last-draft Allocation" );
	}

	public static void main(String[] args){
		try{

			Allocation allocation = new Allocation();
			int i = 0;

			while( i < args.length ){
				if( args[i].equals("-isup") ){
					allocation.flagSuperNumeri = true;					
				}else if( args[i].equals("-isupr") ){
					allocation.SuperNumeriReduced = true;	
				}else if( args[i].equals("-pa") ){
					allocation.printAllocation = true;
				}else if( args[i].equals("-pt") ){
					allocation.printTable = true;
				}else if( args[i].equals("-r") ){
					allocation.round = args[i+1].trim();
				}else if( args[i].equals("-help") || args[i].equals("?") || args[i].equals("-?") ){
					System.out.println("java cdac.in.jam.seat.Allocation [-isup] [-isupr] [-help] ");
					System.out.println("-help :  provide all the options" );
					System.out.println("-?    :  provide all the options" );
					System.out.println("\"?\"   :  provide all the options" );
					System.out.println("-pa   :  print output in allocation data format [defalut false] " );
					System.out.println("-pt   :  print output for 2nd round data upload for table [defalut false] " );
					System.out.println("-r  <round-name>  :  round name [defalut 'round1'] " );
					System.out.println("-isup :  supernumeri seat allocation [defalut false] " );
					System.out.println("-isupr: supernumeri seat reduced  [default false] " );
					System.out.println("*NOTE : -isup -isupr, no effect of -isupr" );
					System.exit(0);
				}

				i++;
			}

			allocation.readDraft("./data/round1.csv", true);
			allocation.read();
			allocation.allocation( 1 );    
			System.out.println("------------------- Allocation Verification ----------------");
			allocation.allocationVerification();
			System.out.println("-----------------------------------------------------------");
			allocation.printCourse();
			System.out.println("-----------------------------------------------------------");
			allocation.detailsAllocation();
			System.out.println("-----------------------------------------------------------");
			allocation.notInDraft();
			System.out.println("-----------------------------------------------------------");


		}catch(Exception e){
			e.printStackTrace();	
		}
	}
}
