
public class P510 {
	
	public static String validate510(String part, String num){
		
		String message = "";
		boolean d = false;
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
		
		String[] a = part.split(" ");
		
		if(!d) message="OK";
		
//104		
		if(num.startsWith("510104")){
			//mod rcv no 90s and mod 90s
			if(a[0].equals("MOD")){
				
				//510104238
				if(a[1].equals("RCV") && !a[2].equals("90S") && !a[2].equals("30S") ){
					message = "MOD RCV 90S " + part.substring(8);
				}
				else if(a[1].equals("90S")){
					//510104288
					if(part.contains("RCV")){
						part = part.replace("RCV", "");
						message = "MOD RCV 90S " + part.substring(8);
					}
					//510104121
					else message = "MOD RCV 90S " + part.substring(8);
				}
				
			} else message = "MR";
		}

//109		
		if(num.startsWith("510108")){
			if(part.contains("MOD") && (part.contains("90S") || part.contains("ITA"))){
				//510108251
				//510108242
				//510108275       
				if(!part.startsWith("MOD ITA 90S")){
					part = part.replace(" 90 ", " 90S ");
					part = part.replace("90S", "");
					part = part.replace("ITA", "");
					part = part.replace("MOD ", "");
					message = "MOD ITA 90S " + part;
				}
			}
		}

//109		
		if(num.startsWith("510109")){
			//MOD AC DOES AND DOES NOT CONTAIN 90S
			if(part.startsWith("MOD AC")){
				if(!a[2].equals("90S")){
					//PART OUT OF ORDER
					//510109300
					//510109297
					if(part.contains("90S")){
						part = part.replace("90S ", "");
						part = part.replace("MOD ", "");
						part = part.replace("AC ", "");
						message = "MOD AC 90S " + part;
					}
					else if(!a[2].equals("PXI")){
						//510109329
						message = "MOD AC 90S " + part.substring(7);
					}
				}
			}
		}

//113
		if(num.startsWith("510113")){
			if(part.startsWith("MOD RCV")){
				if(a[2].equals("ID")){
					//510113139
					message = part = part.replace("ID ", "");
				}
				else if(!a[2].equals("80S")){
					//510113132
					message = "MOD RCV 80S " + part.substring(8);
				}
				
			}
		}
		
//114 ALL GOOD		
		
//127		
		if(num.startsWith("510127")){
			if(part.startsWith("+WDA")){
				//510127580
				message = part.replace("+WDA", "WDA");
			}
		}
	
//133		
		if(num.startsWith("510133")){
			if(a[2].equals("2100")){
				//510133122
				message = part.replace("2100", "2100S");
			}
			else if(!a[2].equals("2100S")){
				//510133128
				message = "MOD RCV 2100S " + part.substring(8);
			}
		}
		
//134 ALL GOOD	
		
//136	
		if(num.startsWith("510136")){
			if(part.startsWith("#")){
				message = part.replace("#","");
			}
		}
		
//140, 150, 160, 161, 170,171  CHECK OVER WITH ENGINEERING
		if(num.startsWith("510151")){
			if(part.startsWith("MOD ITA") && a[2].equals("QPDL")){
				message = part.replace("QPDL", "90S QPDL");
			}
		}
		
//180 & 181 ALL GOOD		
		if(message.contains(",")){
			message = message.replace(",", " ");
		}
		
		return message;
			
	}
}
