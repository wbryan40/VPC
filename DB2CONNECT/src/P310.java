import java.util.ArrayList;
import java.util.Arrays;

public class P310 {

	//Check if entry is valid change to return correction string
	public static String validate310(String part, String num){
		String message = "";
		boolean d = false;
		boolean ignorePart = false;
		if(num.startsWith("310104")
				||num.startsWith("310113")
				||num.startsWith("310114")
				||num.startsWith("310115")
				||num.startsWith("310120")
				||num.startsWith("310122")
				||num.startsWith("310123")
				||num.startsWith("310124")
				||num.startsWith("310128")
				||num.startsWith("310130")
				){
			
			//REMOVE WHITESPACE
			
			
			//REMOVE LEADING CHARACTERS
			if(part.startsWith("$") || part.startsWith("#")) {
				part = part.substring(1);
				message = part;
				d = true;
			}
			
			//CAPITALIZE PART FOR STANDARDIZATION
			if(!part.equals(part.toUpperCase())){
				part = part.toUpperCase();
				message = part;
				d = true;
			}
			//commen
			
			//REPLACE UNCONVENTIONAL OBSOLETE PART DESCRIPTIONS
			if(part.toUpperCase().startsWith("OBS") || part.toUpperCase().startsWith("USE")){
				message = "OBS IFO " + Connect.findNumber(part);
			}
		}
		else {
			ignorePart = true;
		}
		
		
		//AT THIS POINT PART WILL BE CAPITALIZED WITHOUT LEADING CHARACTERS
		
		String[] a = part.split(" ");
		
		
		//104,113,114,115,120,122,123,124,128,130
		
		/* 104
		 * RCV 90S X MOD
		 * RCV XXS QT X MOD      
		 * */
		if(!d) message="OK";
		if(num.startsWith("310104")){
			if(a[0].equals("RCV")){
				if(a[1].equals("90S")){
					//&& isNumber(a[3]) && a[4].equals("MOD")
					if(part.contains(" QT")){
						message = part.replace(" QT", "");
					}
				}
				else if(a[1].equals("QT") && a[2].equals("90S")){
					message = new String("RCV 90S QT "+ part.substring(11));
				}
				else message = "MR";
			}
			else {
				message = "MR";
			}
		}		

		/* 113
		 * RCV AC
		 * RCV ADPTR
		 * PRTV CV
		 * VHMF RCV    
		 * */
		else if (num.startsWith("310113")){
			
			//310113317
			if(part.startsWith("VHMF")){
				if(part.contains("RCV")){
					message = part.replace("RCV ","");
				}
			}
			else {
				message = "MR";
			}
		}
		
		/* 114
		 * Only 6 - Inspect separately
		 * */
		else if (num.startsWith("310114")){
			message = "MR";
		}
		
		/* 115
		 * RCV 80S   XX MOD
		 *     CASS  CASS
		 * */
		else if (num.startsWith("310115")){
			if(part.startsWith("RCV 80S")){
				message = "OK";
			}
			else message = "MR"; 
		}
		
		/* 120
		 * RCV 2100S
		 *     PXI
		 *     GXX
		 * */
		else if (num.startsWith("310120")){
			//all possible GXX 
			String[] gx = {"G14","G14X","G18","G18X","G20","G20X","GD20","G20X","G40","G40X","G2","G19","G19X","G11"};
			boolean g = false; //Part contains GXX
			String xmod;
			
			if(part.startsWith("RCV")){
				//check if part already contains G series
				//310120143 310120146
				for(String s:gx){
					if(part.contains(s)) g=true;
				}
				if(part.contains("2100")||part.contains("MOD")||part.contains("TWO")){
					part = part.replace("2100S ", "");
					part = part.replace("2100 ", "");
					part = part.replace(" MOD", "");
					part = part.replace("MOD", "");
					part = part.replace("TWO", "2");
					if(!g){
						String n = Connect.findNumber(part);
						String gr = "G"+n;
						if(Connect.isNumber(n)) part = part.replace(n, "G"+n);
					}
					
					//310120174
					a = part.split(" ");
					if(a[1].equals("20X")){
						part = part.replace("20X","G20X");
					}
					
					message = part;
				}
				else{
					message = "OK";
				}
			}
		}
		
		/* 122
		 * RCV 90S SX
		 * */
		else if (num.startsWith("310122")){
			if(part.startsWith("RCV 90S SX")) message = "OK";
			else if (!d) message = "MR";
		}
		
		/* 123
		 * RCV ICON
		 * */
		else if (num.startsWith("310123")){
			if(part.startsWith("RCV ICON")) message = "OK";
			else if (!d) message = "MR";
		}
		
		/* 124
		 * RCV ICON 960
		 * */
		else if (num.startsWith("310124")){
			if(part.startsWith("RCV ICN 960")){
				message = "RCV ICON 960 " + part.substring(12);
			}
			else if(!d) message = "MR";
		}
		
		/* 128
		 * RCV i1
		 * */
		else if (num.startsWith("310128")){
			
			if(part.startsWith("RCV i1 WRD")){
				message="OK";				
			}
			else if(!d) message="MR";
		}
		
		/* 130
		 * RCV i2
		 * */
		else if (num.startsWith("310130")){
			
			if(part.startsWith("RCV i2 MX EMI") || part.startsWith("RCV i2 WRD")){
				message="OK";				
			}
			else if(!d)message="MR";
			
		}
		
		if(message.contains(",")) message = message.replace(",", "");
		
		if(d && message.equals("OK")) message = part;
		
		if(ignorePart) message = "OS";
		
		//Validate length
		
		
		return message;
	}
	
}
