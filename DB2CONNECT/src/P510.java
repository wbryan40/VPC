
public class P510 {
	
	public static Part validate510(Part p){
		
		String num = p.getNum();
		String part = p.getDesc();
		
		boolean d = false;
		boolean ignorePart = false;
		if(num.startsWith("510104")
				||num.startsWith("510108")
				||num.startsWith("510109")
				||num.startsWith("510113")
				||num.startsWith("510114")
				||num.startsWith("510127")
				||num.startsWith("510133")
				||num.startsWith("510134")
				||num.startsWith("510136")
				||num.startsWith("510140")
				||num.startsWith("510150")
				||num.startsWith("510151")
				||num.startsWith("510160")
				||num.startsWith("510161")
				||num.startsWith("510170")
				||num.startsWith("510171")
				||num.startsWith("510180")
				||num.startsWith("510181")
				)
		{
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
		else{
			ignorePart = true;
		}
		
		String[] a = part.split(" ");
		
		if(!d) p.setCode("OK");
		
//104		
		if(num.startsWith("510104")){
			//mod rcv no 90s and mod 90s
			if(a[0].equals("MOD")){
				
				//510104238
				if(a[1].equals("RCV") && !a[2].equals("90S") && !a[2].equals("30S") ){
					part = "MOD RCV 90S " + part.substring(8);
				}
				else if(a[1].equals("90S")){
					//510104288
					if(part.contains("RCV")){
						part = part.replace("RCV", "");
						part = "MOD RCV 90S " + part.substring(8);
					}
					//510104121
					else part = "MOD RCV 90S " + part.substring(8);
				}
				
			} else p.setCode("OK");
		}

//108		
		if(num.startsWith("510108")){
			if(part.contains("MOD") && (part.contains("90S") || part.contains("ITA"))){
				//510108251
				//510108242
				//510108275       
				if(!part.startsWith("MOD ITA 90S") && !part.contains("30S")){
					part = part.replace(" 90 ", " 90S ");
					part = part.replace("90S", "");
					part = part.replace("ITA", "");
					part = part.replace("MOD ", "");
					part = "MOD ITA 90S " + part;
				}
				else{
					p.setCode("OK");
				}
			}
			else{
				p.setCode("MR");
			}
		}

//109		
		if(num.startsWith("510109")){
			//MOD AC DOES AND DOES NOT CONTAIN 90S
			if(part.startsWith("MOD AC")){
				if(!a[2].equals("90S")){
					//PART OUT OF ORDER
					//510109162
					if(part.contains("30S") || part.contains("40S") || part.contains("50S")){
						part = part.replace("MOD ", "");
						part = part.replace("AC ", "");
						part = "MOD AC " + part;
					}
					//510109300
					//510109297
					else if(part.contains("90S")){
						part = part.replace("90S ", "");
						part = part.replace("MOD ", "");
						part = part.replace("AC ", "");
						part = "MOD AC 90S " + part;
					}
					else if(!a[2].equals("PXI")){
						//510109329
						part = "MOD AC 90S " + part.substring(7);
					}
				}
			}
		}

//113
		if(num.startsWith("510113")){
			if(part.startsWith("MOD RCV")){
				if(a[2].equals("ID")){
					//510113139
					part = part.replace("ID ", "");
				}
				else if(!a[2].equals("80S")){
					//510113132
					part = "MOD RCV 80S " + part.substring(8);
				}
				
			}
		}
		
//114 ALL GOOD		
		
//127		
		if(num.startsWith("510127")){
			if(part.startsWith("+WDA")){
				//510127580
				part = part.replace("+WDA", "WDA");
			}
			
			//510127313
			if(part.startsWith("WDA") && !part.startsWith("WDA VXI ")){
				part = part.replace("WDA", "WDA VXI");
			}
			
			//510127355
			//510127316
			if(part.contains("VXI TECH") || part.contains("VXITECH")){
				part = part.replace("VXI TECH", "VTITECH");
				part = part.replace("VXITECH", "VTITECH");
			}
		}
	
//133		
		if(num.startsWith("510133")){
			if(part.contains("2100") && !part.contains("2100S")){
				//510133122
				part = part.replace("2100", "2100S");
			}
			else if(!a[2].equals("2100S") && !part.contains("2100")){
				//510133128
				part = "MOD RCV 2100S " + part.substring(8);
			}
		}
		
//134 ALL GOOD	
		
//136	
		if(num.startsWith("510136")){
			if(part.startsWith("#")){
				part = part.replace("#","");
			}
		}
		
//140, 150, 160, 161, 170,171  CHECK OVER WITH ENGINEERING
		if(num.startsWith("510150") || num.startsWith("510151")){
			if(part.contains("QPDL")){
				part = part.replace("QPDL", "QP");
			}
			
			if(part.startsWith("MOD RCV") && !part.contains("90S")){
				//510150202
				part = part.replace("MOD RCV", "MOD RCV 90S ");
			}
			
			if(part.startsWith("MOD ITA") && !part.contains("90S")){
				//510150202
				part = part.replace("MOD ITA", "MOD ITA 90S ");
			}
			
		}
		
//180 & 181 ALL GOOD		
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
