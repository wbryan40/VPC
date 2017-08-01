import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.bind.DatatypeConverter;

import com.ibm.db2.jcc.am.Connection;

public class Connect {
    static Statement stmt;
    static ResultSet rs;
    static String prefix;
    static String filter;

	public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException{		
		
		if(args[0]==null || args[1]==null){
			System.err.println("Please input proper parameters");
			System.exit(0);
		}
		prefix = args[0];
		filter = args[1]; 
		
		//OS, MR, OK, +30, ALL
		/*
			OS - OUT OF SCOPE
			CD - CHANGED
			MR - MANUAL REVISION
			OK - OK
			30 - ALL ITEMS AT OR ABOVE 30 CHARACTERS
			ALL - ALL ITEMS
		 */
	    
	    //Load Driver
	    Class.forName("com.ibm.db2.jcc.DB2Driver");
	    System.out.println("Driver loaded");

	    //Establish database connection
	    Connection dbConn = (Connection) DriverManager.getConnection("jdbc:db2://vpcsys:446/S06ad634", "jstegura", "jspassw05");
	    if (dbConn != null){
	    	System.out.println("DB2 Database Connected");
	    } else {
	    	System.out.println("DB2 Connection Failed");
	    }
	    
	    stmt = dbConn.createStatement(rs.TYPE_SCROLL_INSENSITIVE,
	    	    rs.CONCUR_READ_ONLY);
	    
	    rs = stmt.executeQuery("SELECT ITDSC, ITNBR FROM AMFLIBX.ITMRVA");
	    
	    //pass in string to parse method
	    createCSV(createAL(args[0]));
	    
	    //create CSV File here 
	    System.out.println("Finished");
	}
	
	//Convert raw hex data to string
	public static String convertFromEBCDIC(String s){		
		try {
			byte[] bytes = DatatypeConverter.parseHexBinary(s);
			return new String(bytes, "CP1047");
		} catch (Exception e) {
			return s;
		}
	}
	
	//Finds first occurrence of a number in a given string
	public static String findNumber(String s){
		String[] a = s.split(" ");
		for (String p:a){
			if(isNumber(p)){
				return p;
			}
		}
		return "";
	}
	
	//Returns true if string is a number
	public static boolean isNumber(String s){
		try{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException nfe){
			return false;
		}
	}
	
	/*******************************************************************
	 * Creates a CSV File to display relevant information from the 310s
	 * 
	 * @param lines
	 *******************************************************************/
	public static void createCSV(List<String> lines) throws SQLException{
		//go through each entry, replace all " with "" and surround entry in "
		PrintWriter pw = null;
		try {pw = new PrintWriter(new File("Data.csv"));} 
		catch (FileNotFoundException e) {e.printStackTrace();}

	    Iterator<String> i = lines.iterator();
	    System.out.println(lines.size() + " Items");
	    rs.first();
	    while (rs.next()) {
	    	try{
	    		pw.write(i.next());
	    	} catch(NoSuchElementException e){}
	    }
	    pw.close();
	}
	
	
	/******************************************************************
	 * Creates and arrayList of terms to add to the CSV File
	 * 
	 * @param section is 310, 410, 510, etc...
	 ******************************************************************/	
	public static List<String> createAL(String section) throws SQLException{

		List<String> lines = new ArrayList<String>();
		String valid = "";
	    String row;
	    String itemDesc;
	    String itemNum;
	    
	    double numGood = 0;
	    double totNum = 0;
	    
	    while (rs.next()) {
	        row = Integer.toString(rs.getRow());
	        itemDesc = convertFromEBCDIC(rs.getString("ITDSC"));
	        itemNum = convertFromEBCDIC(rs.getString("ITNBR"));
	        String toAdd = ""; 
	        boolean p30 = false; //more than 30 characters (Top, most strict)
	        boolean cd =  false; //changed 				  (Less Strict)
	        boolean g2 =  false; //more than 2 characters  (MR, OK, OS)
	        
	        if(itemNum.startsWith(section)) {
	        	totNum++;
	        	
	        	if(section.equals("310")) valid = P310.validate310(itemDesc, itemNum);
	        	else if(section.equals("410")) valid = P410.validate410(itemDesc, itemNum);
	        	else if(section.equals("510")) valid = P510.validate510(itemDesc, itemNum);
	        	else if(section.equals("610")) valid = P610.validate610(itemDesc, itemNum);
	        	
	        	if(valid.contains("MOD") && !valid.contains(" MOD")) valid = valid.replace("MOD", " MOD");
	        	valid = valid.replace("  ", " ");
	        	valid = valid.trim();
	        	
		        if(itemDesc.contains("\"")){
		        	itemDesc = itemDesc.replace("\"", "\"\"");
		        }
		        
		        //SET BOOLEANS
		        p30 = valid.length()>30 && (filter.equals("+30") || filter.equals("ALL"));
		        cd = filter.equals("CD") || filter.equals("ALL");
		        g2 = valid.length()>2 || filter.equals("ALL");
		        

		        if(valid.length()==2){
		        	toAdd = "\"" + row + "\",\"" + itemNum +"\",\""+ itemDesc + "\",,"+valid;
		        	lines.add(toAdd + "\n");
		        }
		        else if(p30){
		        	toAdd = "\"" + row + "\",\"" + itemNum +"\",\""+ itemDesc + "\"" + "," + valid+",+30";
		        	lines.add(toAdd + "\n");
		        }
		        else if(cd || g2){
		        	toAdd = "\"" + row + "\",\"" + itemNum +"\",\""+ itemDesc + "\"" + "," + valid + ",CD";
	        		lines.add(toAdd + "\n");
		        }
	        }
	    }
	    
	    return lines;
	}
	

}



	


