
public class Part {
	
	private String number;
	private String desc;
	private String newDesc = "";
	private String code = "";
	private String department = "";
	
	public Part(String number, String description, String dept){
		this.desc = description;
		this.number = number;
		this.department = dept;
		

	}
	
	public String getDesc(){
		return desc;
	}
	
	public String getNum(){
		return number;
	}
	
	//OS, MR, OK, +30
	/*
		OS - OUT OF SCOPE
		CD - CHANGED
		MR - MANUAL REVISION
		OK - OK
		30 - ALL ITEMS AT OR ABOVE 30 CHARACTERS
	 */
	public String getCode(){
		return code;
	}
	
	public String getDept(){
		if(department.trim().length() == 0){
			if(number.trim().equals("510127584")) this.department = "W";
			else 						  		  this.department = "C";
		}
		if(department.trim().equals("O")){
			if(number.startsWith("310") || number.startsWith("410")) department = "M";
			if(number.startsWith("510") || number.startsWith("610")) department = "C";
		}
		return department;
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
	public void setNewDesc(String newDesc){
		this.newDesc = newDesc;
	}
	
	public String getNewDesc(){
		return newDesc;
	} 
	
	
	
}
