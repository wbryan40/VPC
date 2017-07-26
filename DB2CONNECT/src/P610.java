
public class P610 {
	
	public static String validate610(String part, String num){
		
		String message = "";
		boolean d = false;
		if(num.startsWith("610"))
		{
			//REMOVE LEADING $ AND #
			if(part.startsWith("$") || part.startsWith("#") || part.startsWith("~")){
				part = part.substring(1);
				message = part;
				d = true;
			}	
			
			//CAPITALIZE PART IF NOT ALREADY
			if(!part.equals(part.toUpperCase())){
				part = part.toUpperCase();
				part = part.replace("I1", "i1");
				part = part.replace("I2", "i2");
				message = part;
				d = true;
			}
		}
		
		String[] a = part.split(" ");
		
		if(!d) message = "OK";
		
		
		
		
		
		
		
		
		
		
		
		
		return message;
	}
}
