//MR = manual revision
public class P410 {
	public static String validate410(String part, String num){
		
		String message = "";
		boolean d = false;
		boolean ignorePart = false;
		if(num.startsWith("410104")
				||num.startsWith("410112")
				||num.startsWith("410113")
				||num.startsWith("410114")
				||num.startsWith("410120")
				||num.startsWith("410122")
				||num.startsWith("410123")
				||num.startsWith("410124")
				||num.startsWith("410128")
				||num.startsWith("410130")
				){
			//REMOVE LEADING $ AND #
			if(part.startsWith("$") || part.startsWith("#") || part.startsWith("~")){
				part = part.substring(1);
				message = part;
				d = true;
			}
			
			//CAPITALIZE PART IF NOT ALREADY
			if(!part.equals(part.toUpperCase()) && !part.contains("i1") && !part.contains("i2")){
				part = part.toUpperCase();
				message = part;
				d = true;
			}
		}
		else {
			ignorePart = true;
		}
		
		String[] a = part.split(" ");
		
		if(!d) message="OK";
		if(num.startsWith("410104")){
			if(!a[0].equals("ITA") || !a[1].equals("90S")){
				message = "MR";
			}
		}
		
		if(num.startsWith("410112")){
			if(!a[0].equals("ITA") 
					&& !a[0].equals("PRTV")
					&& !a[0].equals("ENCL") 
					&& !a[0].equals("ID")){
				message = "MR";
			}
		}
		
		if(num.startsWith("410113")){
			if(!part.startsWith("ID 80S")){
				message = "MR";
			}
		}
		
		if(num.startsWith("410114")){
			if(!part.startsWith("ENCL ITA")){
				if(part.startsWith("ITA")){
					message = "ENCL " + part;
				}
			}
		}
		
		if(num.startsWith("410120")){
			//ITA WRD && ITA 2100S
			//all possible GXX 
			String[] gx = {"G14","G14X","G18","G18X","G20","G20X","GD20","G20X","G40","G40X","G2","G19","G19X","G11"};
			boolean g = false; //Part contains GXX
			String xmod;
			
			if(part.startsWith("ITA")){
				//check if part already contains G series
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
		
		//410122 - OK
		
		if(num.startsWith("410123")){
			if(part.contains("ICN")){
				part = part.replace("ICN", "ICON");
				message = part;
			}
			
			if(part.startsWith("ITA WRD ICON")){
				//410123455
				message = "ITA ICON WRD " + part.substring(14);
			}
			else if(part.startsWith("ITA ICON EMI WRD")){
				//410123791
				message = "ITA ICON WRD EMI " + part.substring(17);
			}
			
			if(!part.startsWith("ITA")) message = "MR";			
		}
		
		if(num.startsWith("410124")){
			if(part.contains("ICN") || (part.contains("960") && !part.contains(" 960 "))){
				part = part.replace("ICN", "ICON");
				part = part.replace("960", " 960 ");
				message = part;
			}
		}
		
		if(num.startsWith("410128")){
			if(part.startsWith("ITA WRD i2")){
				//410130424
				message = "ITA i2 WRD " + part.substring(11);
			}
			
			//if(a[2].equals("EMI") && a[3].equals("WRD")){
				//REMOVE EMI?
			//}

			if(!part.startsWith("ITA")) message = "MR";	
		}
		
		//410130 REVISE
		
		
		if(message.contains(",")){
			message = message.replace(",", "");
		}
		
		if(d && message.equals("OK")) message = part;
		
		if(ignorePart) message = "OS";
		
		if(!ignorePart && part.contains("OBS")){
			message = "OBS IFO " + Connect.findNumber(part);
		}
		
		return message;
	}
}
