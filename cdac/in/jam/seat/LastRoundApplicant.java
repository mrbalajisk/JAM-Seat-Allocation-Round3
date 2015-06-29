package cdac.in.jam.seat;

public class LastRoundApplicant{

	String applicationId;
	String program;
	String paper;
	String quota;


	int choiceNo;
	int statusId;
	boolean autoUpgrade;
	boolean isProvisional;

	String  acceptancePath;	
	String  challanPath;
	String  declarationPath;
	String  undertakingPath;	

	LastRoundApplicant( String applicationId, String program, String paper, String quota, String choiceNo, String autoUpgrade, String isProvisional, String acceptancePath, String declarationPath, String undertakingPath, String challanPath, String statusId){

			this.applicationId = applicationId;
			this.program = program;
			this.paper = paper;	
			this.quota = quota;	
			this.choiceNo = Integer.parseInt( choiceNo ) - 1;
			this.statusId = Integer.parseInt( statusId ); 
	
			if( acceptancePath.trim().length() > 0 )
				this.acceptancePath = acceptancePath;	
			else
				this.acceptancePath = null;
			
			if( challanPath.trim().length() > 0 )
				this.challanPath =  challanPath;
			else
				this.challanPath =  null;

			if( declarationPath.trim().length() > 0 )
				this.declarationPath = declarationPath; 
			else
				this.declarationPath = null; 

			if( undertakingPath.trim().length() > 0 )
				this.undertakingPath = undertakingPath; 	
			else
				this.undertakingPath = null; 	


			this.autoUpgrade = false;
			this.isProvisional = false;
		
			if( autoUpgrade.equals("t") || autoUpgrade.equals("true") ){
				this.autoUpgrade = true;
			}

			if( isProvisional.equals("t") || isProvisional.equals("true") ){
				this.isProvisional = true;
			}
	}

}
