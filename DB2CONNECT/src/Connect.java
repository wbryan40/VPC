import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.bind.DatatypeConverter;

import com.ibm.db2.jcc.am.Connection;

public class Connect {
    static Statement stmt;
    static ResultSet rs;
    static Connection dbConn;
    static String prefix;

	public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, UnsupportedEncodingException{		
		
		if(args[0]==null || args[1]==null){
			System.err.println("Please input proper parameters");
			System.exit(0);
		}
		prefix = args[0];
		
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
	   dbConn = (Connection) DriverManager.getConnection("jdbc:db2://vpcsys:446/S06ad634", "jstegura", "jspassw05");
	    if (dbConn != null){
	    	System.out.println("DB2 Database Connected");
	    } else {
	    	System.out.println("DB2 Connection Failed");
	    }
	    
	    stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	    		ResultSet.CONCUR_UPDATABLE);
	    
	    rs = stmt.executeQuery("SELECT ITDSC, ITNBR FROM AMFLIBX.ITMRVA");
	    
	    //pass in string to parse method
	    createCSV(createAL(args[0]));
	    
	    //create CSV File here 
	    System.out.println("Finished");
	}
	
	//Convert raw hex data to string
	public static String convertFromEBCDIC(String s) throws UnsupportedEncodingException{		
		byte[] bytes = DatatypeConverter.parseHexBinary(s);
		return new String(bytes, "CP1047");
	}
	
	public static String convertToEBCDIC(String s) throws UnsupportedEncodingException{		
		return bytesToHex(s.getBytes("Cp1047"));
	}
	
	public static String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789abcdef".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
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
	 * @throws UnsupportedEncodingException 
	 ******************************************************************/	
	public static List<String> createAL(String section) throws SQLException, UnsupportedEncodingException{

		List<String> lines = new ArrayList<String>();
		String valid = "";
	    String row;
	    String itemDesc;
	    String itemNum;
	    String toAdd;
	    String newDesc;
	    String code;
		Part p;
	    
	    double numGood = 0;
	    double totNum = 0;
	    
	    while (rs.next()) {
	        row = Integer.toString(rs.getRow());
	        itemDesc = convertFromEBCDIC(rs.getString("ITDSC"));
	        itemNum = convertFromEBCDIC(rs.getString("ITNBR"));
	        toAdd = ""; 
	        
	        
	        p = new Part(itemNum,itemDesc);
	        
	        
	        if(itemNum.startsWith(section)) {
	        	totNum++;
	        	
	        	if(section.equals("310")) p = P310.validate310(p);
	        	if(section.equals("410")) p = P410.validate410(p);
	        	if(section.equals("510")) p = P510.validate510(p);
	        	if(section.equals("610")) p = P610.validate610(p);
	        	
	        	newDesc = p.getNewDesc();
	        	code = p.getCode();
	        	
		        if(itemDesc.contains("\"")){
		        	itemDesc = itemDesc.replace("\"", "\"\"");
		        }
		        
		        toAdd = "\"" + row + "\",\"" + itemNum +"\",\""+ itemDesc + "\"" + "," + newDesc + "," + code;
	        	lines.add(toAdd + "\n");   
	        }
	    }    
	    
	    return lines;
	}
	
	
	public static void updateRS(String section) throws SQLException, UnsupportedEncodingException{
		String itemDesc = "";
		String newDesc = "";
		String itemNum = "";
		Part p;
		
        while (rs.next()) {
        	itemDesc = convertFromEBCDIC(rs.getString("ITDSC"));;
        	itemNum = convertFromEBCDIC(rs.getString("ITNBR"));
        	
        	p = new Part(itemNum,itemDesc);
        	
        	if(section.equals("310")) p = P310.validate310(p);
        	if(section.equals("410")) p = P410.validate410(p);
        	if(section.equals("510")) p = P510.validate510(p);
        	if(section.equals("610")) p = P610.validate610(p);
        	
        	newDesc = p.getNewDesc();
        	
        	
        	if(!itemDesc.equals(newDesc)){
	        	newDesc = convertToEBCDIC(newDesc);
	        	rs.updateString("ITDSC", newDesc);
	            rs.updateRow();
        	}
        }
	}
}	






	


