import java.io.*;
import java.sql.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.net.*;


//import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.ibm.db2.jcc.am.Connection;


public class Connect {
    static Statement stmt;
    static ResultSet rs;
    static Connection dbConn;
    static String prefix;
    static int validPart;

	public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, UnsupportedEncodingException{		
		validPart = 0;
		
		
		if(args[0]==null){
			System.err.println("Please input proper parameters");
			System.exit(0);
		}
		prefix = args[0]; //310, 410, 510, 610
	    
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
	    
	    
	    stmt = dbConn.createStatement(rs.TYPE_SCROLL_INSENSITIVE,
	    		rs.CONCUR_UPDATABLE);
	    dbConn.setReadOnly(false);	   
	    
	    rs = stmt.executeQuery("SELECT ITDSC, ITNBR, DPTNO FROM AMFLIBX.ITMRVA");

	    
	    long startTime = System.nanoTime();
	    
	    try {
	    	executeUpdate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    long endTime = System.nanoTime();
	    long duration = (endTime - startTime); 
	    
	    System.out.println("Duration of part updates: " + duration/1000000000.0 +" Seconds");
	    
	    
	    System.out.println("Finished");
	}
	
	public static void executeUpdate() throws SQLException, IOException{
		String row;
	    String itemDesc;
	    String dept;
	    String itemNum;
	    String toAdd;
	    String newDesc;
	    String code;
		Part p;
		int updated = 0;
	    
	    double numGood = 0;
	    double totNum = 0;
	    ArrayList<String> al = new ArrayList<String>();
	    
	    rs.first();
	    while (rs.next()) {
	        row = Integer.toString(rs.getRow());
	        itemDesc = convertFromEBCDIC(rs.getString("ITDSC"));
	        itemNum = convertFromEBCDIC(rs.getString("ITNBR"));
	        dept = convertFromEBCDIC(rs.getString("DPTNO"));
	        
	        p = new Part(itemNum, itemDesc, dept);
	        
	        if(itemNum.startsWith("310")) p = P310.validate310(p);
        	if(itemNum.startsWith("410")) p = P410.validate410(p);
        	if(itemNum.startsWith("510")) p = P510.validate510(p);
        	if(itemNum.startsWith("610")) p = P610.validate610(p);
        	
        	if(p.getCode().equals("CD")){
        		updated++;
//        		if(itemDesc.contains("\"")) itemDesc = itemDesc.replace("\"", "\"\"");
//		        toAdd = "\"" + row + "\",\"" + p.getNum() +"\",\""+ p.getDesc() + "\"" + "," + p.getNewDesc() + "," + p.getCode() + "," + p.getDept();
//	        	al.add(toAdd + "\n");
        		
	        	updatePart(p.getNum(), p.getNewDesc(), p.getDept());
        	}
	    }
	    System.out.println("Parts Updated: " + updated);
//	    createCSV(al);
	}
	

	public static void updatePart(String partNum, String replacement, String department) throws SQLException, IOException{

		String xml = ""
		+ "<?xml version='1.0' encoding='UTF-8'?>"
		+"<!DOCTYPE System-Link SYSTEM 'SystemLinkRequest.dtd'>"
		+"<System-Link>"
 
			+"<Login userId='jstegura' password='jspassw05' maxIdle='900000' "
				+"properties='com.pjx.cas.domain.EnvironmentId=XX, "
				+"com.pjx.cas.domain.SystemName=VPCSYS.VPC.local, "
				+"com.pjx.cas.user.LanguageId=en'/> "
 
   			+"<Request sessionHandle='*current' workHandle='*new' "
   				+"broker='EJB' maxIdle='1000'> "
				+"<Update name='updateObject_ItemRevision' domainClass='com.mapics.epdm.ItemRevision'> "
					+"<ApplyTemplate><![CDATA[(none)]]></ApplyTemplate> "
					+"<DomainEntity> "
					+"<Key> "
						+"<Property path='site'> " 
							+"<Value><![CDATA[VPC]]></Value> "
						+"</Property>"
						+"<Property path='item'>"
							+"<Value><![CDATA["+ partNum +"]]></Value>"
						+"</Property>"
				        +"<Property path='revision'>"
				            +"<Value><![CDATA[]]></Value>"
				        +"</Property>"
					+"</Key>"
						+"<Property path='description'>"
							+"<Value><![CDATA[" + replacement +"]]></Value>"
						+"</Property>"
						+"<Property path='department'>"
							+"<Value><![CDATA[" + department +"]]></Value>"
						+"</Property>"
					+"</DomainEntity>"
				+"</Update>"
			+"</Request>"
		+"</System-Link>";
		
		
		
		String urls = "http://vpcsys.vpc.local:36001/SystemLink/servlet/SystemLinkServlet"; 

		try
		{
		    URL url = new URL(urls);
		    URLConnection con = url.openConnection();
		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);
		    con.setConnectTimeout( 200000 );  // long timeout, but not infinite
		    con.setReadTimeout( 200000 );
		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);
		    // tell the web server what we are sending
		    con.setRequestProperty ( "Content-Type", "text/xml" );
		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    writer.write(xml);
		    writer.flush();
		    writer.close();
		    // reading the response
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );
		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;
		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }
		    String result = buf.toString();
		    System.err.println( "\nResponse from server after POST:\n" + result );
		    
		    if(!result.contains("hasErrors='true'")) {
		    	validPart++;
		    	System.out.println("Valid part update: " + validPart);
		    }
		    else{
		    	System.out.println("Unknown Error - Revise");
		    }
		}
		catch( Throwable t )
		{
		    t.printStackTrace( System.out );
		}
	}
	
	
	/*******************************************************************
	 * Creates a CSV File to display relevant information
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
	    String row;
	    String itemDesc;
	    String dept;
	    String itemNum;
	    String toAdd;
	    String newDesc;
	    String code;
		Part p;
	    
	    double numGood = 0;
	    double totNum = 0;
	    
	    rs.first();
	    while (rs.next()) {
	        row = Integer.toString(rs.getRow());
	        itemDesc = convertFromEBCDIC(rs.getString("ITDSC"));
	        itemNum = convertFromEBCDIC(rs.getString("ITNBR"));
	        dept = convertFromEBCDIC(rs.getString("DPTNO"));
	        toAdd = "";
	        
	        p = new Part(itemNum, itemDesc, dept);
	        
	        
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
		        
		        toAdd = "\"" + row + "\",\"" + itemNum +"\",\""+ itemDesc + "\"" + "," + newDesc + "," + code + "," + dept;
	        	lines.add(toAdd + "\n");   
	        }
	    }    
	    
	    return lines;
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
	
}	




	


