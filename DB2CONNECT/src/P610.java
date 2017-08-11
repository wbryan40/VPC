public class P610 {
	
	public static Part validate610(Part p){
		
		String num = p.getNum();
		String part = p.getDesc();
		
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
		
		}	
		
		//CAPITALIZE PART IF NOT ALREADY
		if(!part.equals(part.toUpperCase())){
			part = part.toUpperCase();
			part = part.replace("I1", "i1");
			part = part.replace("I2", "i2");
		
		}
		
		
		if(num.startsWith("610103")){
			if(!part.startsWith("CNT ITA CX") && !part.startsWith("CNT ITA MCX") && !part.startsWith("OBS")){
				p.setCode("MR");
			}
		}
		
		if(num.startsWith("610104")){
			if(part.contains("90S")) p.setCode("MR");
		}
		
		if(num.startsWith("610110")){
			
			if(part.contains("AU")) p.setCode("MR");
			
			if(part.contains("CNT") && (part.contains("ML") || part.contains("ITA")) && part.contains("SIG") && !part.contains("FML")){
				
				part = part.replace("CNT", "");
				part = part.replace("ML", "");
				part = part.replace("ITA", "");
				part = part.replace("TRIPDL", "");
				part = part.replace("SIG", "");
				part = "CNT ITA TPDL"+part;
			}
			else if(part.contains("CNT") && (part.contains("FML")||part.contains("RCV")) && (part.contains("SIG")||part.contains("TRIPDL"))){
				part = part.replace("CNT", "");
				part = part.replace("FML", "");
				part = part.replace("RCV", "");
				part = part.replace("TRIPDL", "");
				part = part.replace("SIG", "");
				part = "CNT RCV TPDL"+part;
			}
			else if(part.contains("TRIPDL") || part.contains("TRPDL") ){
				part = part.replace("TRIPDL", "TPDL");
				part = part.replace("TRPDL", "TPDL");
			}
			else if(part.contains("ML")){
				part = part.replace("FML", "RCV");
				part = part.replace("ML", "ITA");
			}
		}
		
		if(num.startsWith("610116")||
		   num.startsWith("610142")||
		   num.startsWith("610143")){
			p.setCode("OK");
		}
		
		if(num.startsWith("610113")||
		   num.startsWith("610123")||
		   num.startsWith("610131")||
		   num.startsWith("610132")){
			p.setCode("MR");
		}
		
		if(num.startsWith("610138")){
			
		}
		
		if(part.contains("MAU")){
			part = part.replace("MAU", "µAU");
		}
		else if(part.contains("AU")){
			part = part.replace("AU", "µAU");
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

		
		if(part.contains("OBS")){
			if(part.trim().equals("OBS")||part.trim().equals("OBSOLETE")){
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
			if(!p.getCode().equals("MR")) p.setCode("CD");
		}
		else{
			p.setCode("OK");
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
