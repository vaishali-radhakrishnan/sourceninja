 package com.kannan.sourcewalker;
 
 import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import org.apache.log4j.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.objectweb.asm.Label;

 
 public class TracerImpl extends Thread
 {
   private Configuration conf = Configuration.getInstance();
  // private String logName = "method_trace" + Thread.currentThread().getId() +".log";
   PrintWriter out;
   private Map<String, String> threadMap = new HashMap();
   private Stack<String> tempStack = new Stack<String>();
   private AtomicInteger id = null; 
   
   //Logger logger = LoggerFactory.getLogger(TracerImpl.class);
    //static Logger logger = Logger.getLogger(TracerImpl.class);
  // static  Logger logger = Logger.getLogger("swLogger");
   private static final Logger LOGGER = Logger.getLogger(TracerImpl.class.getName());

   public TracerImpl() {
     try {
    	 id = new AtomicInteger();
    	 LOGGER.setUseParentHandlers(false);
     // this.out = new PrintWriter(new BufferedWriter(new FileWriter(this.logName), 1024));
       Runtime.getRuntime().addShutdownHook(this);
     } catch (Exception ex) {
       ex.printStackTrace();
     }
     
     	Handler consoleHandler = null;
		Handler fileHandler  = null;
		MyLogFormatter simpleFormatter  = null;
		try{
			
			Logger globalLogger = Logger.getLogger("global");
			Handler[] handlers = globalLogger.getHandlers();
			for(Handler handler : handlers) {
			    globalLogger.removeHandler(handler);
			}
			
			
			 simpleFormatter = new MyLogFormatter();

			//Creating consoleHandler and fileHandler
			//consoleHandler = new ConsoleHandler();///Users/arunjanarthnam/Projects
			 String logFileName = System.getProperty("profileName", "profile");
			 System.out.println( " args " + System.getProperty("profileName"));
			 String logFilePath = System.getProperty("logPath", "/usr/customlogs/");
			 System.out.println("log filePath = " + logFilePath);
			 String vmid = new java.rmi.dgc.VMID().toString();
			 String filePath = logFilePath + logFileName +"-"+vmid+".log";
			 System.out.println("filePath = " + filePath);
			fileHandler  = new FileHandler(filePath);
			
			//Assigning handlers to LOGGER object
			//LOGGER.addHandler(consoleHandler);
			LOGGER.addHandler(fileHandler);
			fileHandler.setFormatter(simpleFormatter);

			//Setting levels to handlers and LOGGER
			//consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			
			//LOGGER.config("Configuration done.");
			
			//Console handler removed
			//LOGGER.removeHandler(consoleHandler);
			
			//LOGGER.log(Level.FINE, "Finer logged");
		}catch(IOException exception){
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
		
		//LOGGER.finer("Finest example on LOGGER handler completed.");
		
		
   }
 
   public void entry(String cName, String mName) { 
	   if (!this.conf.isLoggingEnabled()) return;
/*     String threadName = Thread.currentThread().getName();
     String indent = (String)this.threadMap.get(threadName);
     if (indent == null) indent = threadName;
     indent = indent + "  ";
     this.threadMap.put(threadName, indent);
     print(indent + "CALL T :: " + cName + "." + mName + ""); */
	   
	   this.entry(cName, mName, "" , null);
     }
   
   
   public  void invoke(String caller, String callee,  int lineNum) {
	   tempStack.push(caller+"::"+callee+"::"+lineNum);
	  /* if (!this.conf.isLoggingEnabled()) return;
     String threadName = Thread.currentThread().getName();
     String indent = (String)this.threadMap.get(threadName);
     if (indent == null) indent = threadName;
     indent = indent + "  ";
     this.threadMap.put(threadName, indent);
    

     //print(indent + "CALL :: " + cName + "." + mName );
   //  print(indent + "   " +  "Params :: "  + ObjectPrinter.print(objArray)); 
     LOGGER.info(indent + "INVOKE :: " + className + "." + mName +  " :: linenum :: " + lineNum +  " :: DESC :: " + desc);
     
    // LOGGER.info(indent + "CALL :: " + cName + "." + mName +  " :: linenum :: " + lineNum + " :: Params :: "  + ObjectPrinter.print(objArray));
  //   ObjectPrinter.saveLogs(ObjectPrinter.print(objArray));
*/
     
}
   public void entry(String cName, String mName, String desc, Object[] objArray) { 
	   if (!this.conf.isLoggingEnabled()) return;
     String threadName = Thread.currentThread().getName();
     String indent = (String)this.threadMap.get(threadName);
     if (indent == null) indent = threadName;
     indent = indent + "  ";
     this.threadMap.put(threadName, indent);
     String fn = cName+"."+mName;
   	 String iLn = "#"; 
   	String iC = "#"; 
   	if(!this.tempStack.isEmpty()){
	     String invoke = this.tempStack.pop();
	     if(null!=invoke && !invoke.isEmpty()){
	    	 String[] strArr = invoke.split("::");
	    	 if(null!=strArr && strArr.length>1){
	    		 if(strArr[1].equalsIgnoreCase(fn)){
	    			 iLn = strArr[2];
	    			 if(!strArr[0].equalsIgnoreCase(fn)){
	    				iC =  strArr[0];
	    			 }
	    			 
	    		 }
	    	 }
	     }
   	}
     //print(indent + "CALL :: " + cName + "." + mName );
   //  print(indent + "   " +  "Params :: "  + ObjectPrinter.print(objArray)); 
     LOGGER.info(id.incrementAndGet()+"::"+indent +"::"+  "C::" + fn +  "::D:: " + desc + " ::ILN::" + iLn  + " ::IC::" + iC);// + " ::p:: " );
    // ObjectPrinter.print(objArray, LOGGER );
     
    // LOGGER.info(indent + "CALL :: " + cName + "." + mName +  " :: linenum :: " + lineNum + " :: Params :: "  + ObjectPrinter.print(objArray));
  //   ObjectPrinter.saveLogs(ObjectPrinter.print(objArray));

     }
   
   public void entryLinenum(String descr, int linenum){
	   if (!this.conf.isLoggingEnabled()) return;
     String threadName = Thread.currentThread().getName();
     String indent = (String)this.threadMap.get(threadName);
     if (indent == null) indent = threadName;
     indent = indent + "  ";
     this.threadMap.put(threadName, indent);
 
     LOGGER.info(indent + "LineNum of CALL :: " + descr + " :: Line Num :: " + linenum );
     
    // LOGGER.info(indent + "CALL :: " + cName + "." + mName +  " :: linenum :: " + lineNum + " :: Params :: "  + ObjectPrinter.print(objArray));
  //   ObjectPrinter.saveLogs(ObjectPrinter.print(objArray));

     }
   
   public void entry(String cName, String mName, StringBuilder sb) { 
	   if (!this.conf.isLoggingEnabled()) return;
     String threadName = Thread.currentThread().getName();
     String indent = (String)this.threadMap.get(threadName);
     if (indent == null) indent = threadName;
     indent = indent + "  ";
     this.threadMap.put(threadName, indent);
     //String[] strArra= ( String[]) objArray;
     print(indent + "CALL :: " + cName + "." + mName ); 
     print(indent + indent + "Params :: " + sb.toString());
     }
   
   
   public  void methodParams(String cName, String mName, Object paramArray) {
	   
	   if (!this.conf.isLoggingEnabled()) return;
	   String[] temp = (String[])paramArray;
	     String threadName = Thread.currentThread().getName();
	     String indent = (String)this.threadMap.get(threadName);
	     if (indent == null) indent = threadName;
	     indent = indent ;
	     this.threadMap.put(threadName, indent);
	     print(id.incrementAndGet()+"::"+indent + "CALL :: " + cName + "." + mName + "" + " Params :: " + Arrays.toString(temp) ); 
	   
   }
 
   public void exit(String cName, String mName)
   {
     if (!this.conf.isLoggingEnabled()) return;
     String threadName = Thread.currentThread().getName();
     String indent = (String)this.threadMap.get(threadName);
     if (indent == null) indent = threadName + "    ";
     if (indent.length() < threadName.length() + 2) {
    	 LOGGER.info("some error. please report to rejeev@gmail.com");
       indent = threadName + "  ";
     }
     LOGGER.info(indent + "RETURN ::" + cName + "." + mName + ">");
     indent = indent.substring(0, indent.length() - 2);
     this.threadMap.put(threadName, indent);
   }
   
   public void jumpCall(Object obj, String className, String mName, Label label, int returnLineNum, int opcode ) {
	   	String threadName = Thread.currentThread().getName();
	     String indent = (String)this.threadMap.get(threadName);
	   	try {
			LOGGER.info("::"+indent +"::"+   "J::" + className + "." + mName +  ":: ln:: " + returnLineNum+  ":: lable:: " + label.getOffset()+  ":: opcode:: " + opcode   );//+ "::RO:: " + "{\"returnObject\":{");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(" error in label");
		}
	    
	   }

   
   public void jumpCall(String className, String mName,  int returnLineNum, int opcode ) {
	   	String threadName = Thread.currentThread().getName();
	     String indent = (String)this.threadMap.get(threadName);
	   	try {
			LOGGER.info("::"+indent +"::"+   "J::" + className + "." + mName +  ":: ln:: " + returnLineNum+  ":: opcode:: " + opcode   );//+ "::RO:: " + "{\"returnObject\":{");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(" error in label");
		}
	    
	   }
   
   public void jumpCall(String className, String mName, int label,  int returnLineNum, int opcode ) {
	   	String threadName = Thread.currentThread().getName();
	     String indent = (String)this.threadMap.get(threadName);
	   	try {
	   		
			LOGGER.info("::"+indent +"::"+   "J::" + className + "." + mName+  ":: label:: " + label  +  ":: ln:: " + returnLineNum+  ":: opcode:: " + opcode   );//+ "::RO:: " + "{\"returnObject\":{");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(" error in label");
		}
	    
	   }

   
   
   public  void exitWithReturn(Object o, String className, String mName, int returnLineNum) {

	     if (!this.conf.isLoggingEnabled()) return;
	     String threadName = Thread.currentThread().getName();
	     String indent = (String)this.threadMap.get(threadName);
	     if (indent == null) indent = threadName + "    ";
	     if (indent.length() < threadName.length() + 2) {
	       print("some error. please report to rejeev@gmail.com");
	       indent = threadName ;
	     }
	    
	    //print(indent + "RETURN ::" + className + "." + mName );
	    // print( indent + "  " + "{ \"returnObject\" : { " + ObjectPrinter.print(o) + " } ");
	     LOGGER.info(id.incrementAndGet()+"::"+indent +"::"+   "R::" + className + "." + mName +  ":: rLn:: " + returnLineNum );//+ "::RO:: " + "{\"returnObject\":{");
	    // ObjectPrinter.print(o, LOGGER);
	    // LOGGER.info( "}");
		    
	     //LOGGER.info(indent + "RETURN ::" + className + "." + mName +  ":: linenum :: " + lineNum + " :: ReturnObj :: " + "{ \"returnObject\" : { " + ObjectPrinter.print(o) + " } ");
	     indent = indent.substring(0, indent.length() - 2);
	     this.threadMap.put(threadName, indent);
	   
	   }
 
   public void print(String str1)
   {
	   
	   String str= str1.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
     if (this.conf.isConsole()) {
		//	System.out.println(str);
    	
    	// this.out.println(str);
		}
     if (this.conf.isFile()){ //this.out.println(str);
	
			/*	try{

				//System.out.println("print");
					//File file =new File("/Users/bdcoe/swlogs/sw.log");
					
					 File folder = new File(System.getProperty("user.home"), "swlogs");
					if(!folder.exists() && !folder.mkdirs()) {
					   //failed to create the folder, probably exit
					   throw new RuntimeException("Failed to create save directory.");
					}
					//true = append file
					 File myFile = new File(folder, "sw3.log");
					FileWriter fileWritter = new FileWriter(myFile,true);
				        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				        bufferWritter.write(str);
				        bufferWritter.close();
				

				
				}catch(IOException e){
					e.printStackTrace();
				}*/
				
			}
   }
 
   public void run()
   {
	   	//System.out.println("closing log file");
/* 80 */    // this.out.close();
   }
 }

/* Location:           /Users/bdcoe/Downloads/MethodTracer.jar
 * Qualified Name:     rejeev.tracer.TracerImpl
 * JD-Core Version:    0.6.2
 */