public class P410 {
	public static Part validate410(Part p){
		
		String part = p.getDesc();
		String num = p.getNum();
		
		
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
				d = true;
			}
			
			//CAPITALIZE PART IF NOT ALREADY
			if(!part.equals(part.toUpperCase()) && !part.contains("i1") && !part.contains("i2")){
				part = part.toUpperCase();
				d = true;
			}
		}
		else {
			ignorePart = true;
		}
		
		String[] a = part.split(" ");
		
		if(!d) p.setCode("OK");
		if(num.startsWith("410104")){
			if(!a[0].equals("ITA") || !a[1].equals("90S")){
				p.setCode("MR");
			}
		}
		
		if(num.startsWith("410112")){
			if(!a[0].equals("ITA") 
					&& !a[0].equals("PRTV")
					&& !a[0].equals("ENCL") 
					&& !a[0].equals("ID")){
				p.setCode("MR");
			}
		}
		
		if(num.startsWith("410113")){
			if(!part.startsWith("ID 80S")){
				p.setCode("MR");
			}
		}
		
		if(num.startsWith("410114")){
			if(!part.startsWith("ENCL ITA")){
				if(part.startsWith("ITA")){
					part = "ENCL " + part;
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
				}
				else{
					p.setCode("OK");
				}
			}
		}
		
		//410122 - OK
		
		if(num.startsWith("410123")){
			if(part.contains("ICN")){
				part = part.replace("ICN", "ICON");
			}
			
			if(part.startsWith("ITA WRD ICON")){
				//410123455
				part = "ITA ICON WRD " + part.substring(14);
			}
			else if(part.startsWith("ITA ICON EMI WRD")){
				//410123791
				part = "ITA ICON WRD EMI " + part.substring(17);
			}
			
			if(!part.startsWith("ITA")) p.setCode("MR");;			
		}
		
		if(num.startsWith("410124")){
			if(part.contains("ICN") || (part.contains("960") && !part.contains(" 960 "))){
				part = part.replace("ICN", "ICON");
				part = part.replace("960", " 960 ");
			}
		}
		
		if(num.startsWith("410128")){
			if(part.startsWith("ITA WRD i2")){
				//410130424
				part = "ITA i2 WRD " + part.substring(11);
			}

			if(!part.startsWith("ITA")) p.setCode("MR");;	
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
		
		if(part.contains("OBS")){
			if(part.equalsIgnoreCase("OBS")||part.equalsIgnoreCase("OBSOLETE")){
				part = "OBS";
			}
			else{
				part = "OBS IFO " + Connect.findNumber(part);
				if(part.equals("OBS IFO ")) part = "OBS";
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
		
		if(part.contains("ENCL")) p.setCode("MR");
		if(p.getNewDesc().length() > 30) p.setCode("30");
		
		return p;
	}
}