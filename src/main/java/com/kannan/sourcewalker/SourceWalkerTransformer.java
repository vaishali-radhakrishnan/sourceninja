package com.kannan.sourcewalker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;*/
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class SourceWalkerTransformer implements ClassFileTransformer {
	
	//Logger logger = LoggerFactory.getLogger(SourceWalkerTransformer.class);
	
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		 try {
	         //System.out.println(" inside transformer " + className);   
			// logger.error( "inside transformer " + className);
			 ClassReader cr = new ClassReader(classfileBuffer);
			   ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
	            CheckClassAdapter cxa = new CheckClassAdapter(cw);
			     ClassVisitor cv = new ClassInfoGatherer(cxa);
			      cr.accept(cv, ClassReader.EXPAND_FRAMES);
	            byte[] b = cw.toByteArray(); 
	           // System.out.println(" inside transformer - returning" + b.length);  
	          //  logger.error( "inside transformer  - returning" + b.length);
	           // print();
	            return b;
	        } catch (Throwable t) {
	        	t.printStackTrace();
	        	StringWriter sw = new StringWriter();
	        	t.printStackTrace(new PrintWriter(sw));
	        	String exceptionAsString = sw.toString();
	        	
	        	System.out.println( " throwable found " + exceptionAsString) ;
		

	        }/*catch (Exception e) {
	        	System.out.println( " class might not be found " + e.getMessage()) ;
	            try {
					throw new ClassNotFoundException(className, e);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }*/
		return null;
	}
	
	/*private void print(){
    	try {

			//System.out.println(" schema str " + schemaInfo.getSchema());
			//make a call to metadata service
			HttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("http://localhost:8091/MetaDataService/hello");


	      //  StringEntity se = new StringEntity(schema);
	       // httppost.setEntity();

			//Execute and get the response.
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity respEntity = response.getEntity();


		
		} catch (Exception e) {
				// TODO Auto-generated catch block
			System.err.println(" error in sw print");
		}
	}*/
	
	

}
