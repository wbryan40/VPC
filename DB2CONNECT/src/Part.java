
public class Part {
	
	private String number;
	private String desc;
	private String newDesc = "";
	private String code = "";
	
	public Part(String number, String description){
		this.desc = description;
		this.number = number;
	}
	
	public String getDesc(){
		return desc;
	}
	
	public String getNum(){
		return number;
	}
	
	public String getCode(){
		return code;
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
