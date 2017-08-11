import java.util.ArrayList;
import java.util.Arrays;

public class P310 {

	//Check if entry is valid change to return correction string
	public static Part validate310(Part p){
		
		String part = p.getDesc();
		String num = p.getNum();
		
		boolean pfx = false;
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
			
			//REMOVE LEADING CHARACTERS
			if(part.startsWith("$") || part.startsWith("#")) {
				part = part.substring(1);
				pfx = true;
			}
			
			//CAPITALIZE PART FOR STANDARDIZATION
			if(!part.equals(part.toUpperCase())){
				part = part.toUpperCase();
				pfx = true;
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
		if(!pfx) p.setCode("OK");
		if(num.startsWith("310104")){
			if(a[0].equals("RCV")){
				if(a[1].equals("90S")){
					//&& isNumber(a[3]) && a[4].equals("MOD")
					if(part.contains(" QT")){
						part = part.replace(" QT", "");
					}
				}
				else if(a[1].equals("QT") && a[2].equals("90S")){
					part = new String("RCV 90S QT "+ part.substring(11));
				}
				else p.setCode("MR");;
			}
			else {
				p.setCode("MR");;
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
					part = part.replace("RCV ","");
				}
			}
			else {
				 p.setCode("MR");
			}
		}
		
		/* 114
		 * Only 6 - Inspect separately
		 * */
		else if (num.startsWith("310114")){
			p.setCode("MR");
		}
		
		/* 115
		 * RCV 80S   XX MOD
		 *     CASS  CASS
		 * */
		else if (num.startsWith("310115")){
			if(part.startsWith("RCV 80S")){
				p.setCode("OK");
			}
			else p.setCode("MR");; 
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
				}
				else p.setCode("OK");
			}
		}
		
		/* 122
		 * RCV 90S SX
		 * */
		else if (num.startsWith("310122")){
			if(part.startsWith("RCV 90S SX")) p.setCode("OK");
			else if (!pfx) p.setCode("MR");;
			
		}
		
		/* 123
		 * RCV ICON
		 * */
		else if (num.startsWith("310123")){
			if(part.startsWith("RCV ICON")) p.setCode("OK");
			else if (!pfx) p.setCode("MR");
			
			if(part.contains("ICN")){
				part = part.replace("ICN", "ICON");
			}
			
		}
		
		/* 124
		 * RCV ICON 960
		 * */
		else if (num.startsWith("310124")){
			if(part.contains("ICN")){
				part = part.replace("ICN", "ICON");
			}
			else if(!pfx) p.setCode("MR");
		}
		
		/* 128
		 * RCV i1
		 * */
		else if (num.startsWith("310128")){
			
			if(part.startsWith("RCV i1 WRD")){
				p.setCode("OK");				
			}
			else if(!pfx) p.setCode("MR");
		}
		
		/* 130
		 * RCV i2
		 * */
		else if (num.startsWith("310130")){
			
			if(part.startsWith("RCV i2 MX EMI") || part.startsWith("RCV i2 WRD")){
				p.setCode("OK");				
			}
			else if(!pfx) p.setCode("MR");				
		}
		
		
		if(part.contains(",")) part = part.replace(",", "");
    	if(part.contains("MOD") && !part.contains(" MOD")) part = part.replace("MOD", " MOD");
		
		if(part.contains("90S") && part.contains("MOD") && (part.contains("25") || part.contains("50") || part.contains("75"))){
			part = part.replace("75", "9075");
			part = part.replace("50", "9050");
			part = part.replace("25", "9025");
			part = part.replace("MOD", "");
			part = part.replace("90S", "");
		}
		else if (part.contains("90S")){
			part = part.replace("90S", "");
		}
		
		
		//Validate length
		//REPLACE UNCONVENTIONAL OBSOLETE PART DESCRIPTIONS
		if(part.contains("OBS")){
			if(part.equalsIgnoreCase("OBS")||part.equalsIgnoreCase("OBSOLETE")){
				part = "OBS";
			}
			else{
				part = "OBS IFO " + Connect.findNumber(part);
			}
		}

		part = part.trim();
    	part = part.replace("  ", " ");
    	part = part.replace("  ", " ");
    	
		if(!part.equals(p.getDesc().trim())){
			p.setNewDesc(part);
			p.setCode("CD");
		}
		
		if(ignorePart) {
			p.setCode("OS");
			p.setNewDesc("");
		}
		
		if(p.getNewDesc().length() > 30) p.setCode("30");
		
		return p;
	}
	
}
