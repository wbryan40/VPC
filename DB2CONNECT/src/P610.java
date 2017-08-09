
public class P610 {
	
	public static Part validate610(Part p){
		
		String num = p.getNum();
		String part = p.getDesc();
		
		boolean d = false;
		boolean ignorePart = false;
		
		if(part.startsWith("610105") ||
				part.startsWith("610106") ||
				part.startsWith("610107") ||
				part.startsWith("610108") ||
				part.startsWith("610109") ||
				part.startsWith("610111") ||
				part.startsWith("610112") ||
				part.startsWith("610114") ||
				part.startsWith("610118") ||
				part.startsWith("610119") ||
				part.startsWith("610120") || 
				part.startsWith("610121") ||
				part.startsWith("610122") ||
				part.startsWith("610124") ||
				part.startsWith("610125") ||
				part.startsWith("610126") ||
				part.startsWith("610127") ||
				part.startsWith("610128") ||
				part.startsWith("610129") ||
				part.startsWith("610130") ||
				part.startsWith("610131") ||
				part.startsWith("610134") ||
				part.startsWith("610135") ||
				part.startsWith("610136") ||
				part.startsWith("610137") ||
				part.contains(" BDT ")    ||
				part.contains("60S")      ||
				part.contains("50S")      ||
				part.contains("40S")      ||
				part.contains("30S")
				)
		{
			ignorePart = true;
		}
		
		if(part.startsWith("$") || part.startsWith("#") || part.startsWith("~")){
			part = part.substring(1);
			d = true;
		}	
		
		//CAPITALIZE PART IF NOT ALREADY
		if(!part.equals(part.toUpperCase())){
			part = part.toUpperCase();
			part = part.replace("I1", "i1");
			part = part.replace("I2", "i2");
			d = true;
		}
		
		String[] a = part.split(" ");
		
		if(!d) p.setCode("MR");		
		
		
		
		
		
		
		
		//INSERT CLEANUP CODE HERE
		
		
		
		
		

		
		if(part.contains(",")) part = part.replace(",", "");
    	if(part.contains("MOD") && !part.contains(" MOD")) part = part.replace("MOD", " MOD");
		
		if(part.contains("90S") && part.contains("MOD") && (part.contains("25") || part.contains("50") || part.contains("75"))){
			part = part.replace("75", "9075");
			part = part.replace("50", "9050");
			part = part.replace("25", "9025");
			part = part.replace("MOD", "");
			part = part.replace("90S", "");
		}

		
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

		if(!part.equals(p.getDesc())){
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
