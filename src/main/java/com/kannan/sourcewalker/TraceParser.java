package com.kannan.sourcewalker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import com.kannan.sourcewalker.to.NeoHelper;

public class TraceParser {
	
	private static Stack<String[]> stack = new Stack<String[]>();
	private static Stack<String> shortstack = new Stack<String>();
	private String path = "";
	private static AtomicInteger callRank = new AtomicInteger();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "1::main::C::org/apache/hadoop/util/RunJar.<clinit>::D:: ()V ::ILN::# ::IC::#";
		System.out.println(" split " + Arrays.deepToString(s.split("::")));
		
		parse("/Users/arunjanarthnam/Projects/logs/testlog.log");
		

	}
	
	private static void parse(String filename){
		
		try {
			String sCurrentLine;
			BufferedReader br = openFile(filename);
			while ((sCurrentLine = br.readLine()) != null) {
				NeoHelper neoHelper = new NeoHelper();
				boolean multiCallFlag = false;
				int multiCallCount = 0;
				String[] multiCallStr = null;
				//parse the string
				String[] sArr = sCurrentLine.split("::");
				
				if(sArr.length>0){
					String type = sArr[2];
					if(type.equalsIgnoreCase("c")){//if call, put it in stack
						stack.push(sArr);
						
					}else if(type.equalsIgnoreCase("r")){//handling return
						//get the corresponding call from stack
						String[] currentStackArr = stack.pop();
						if(currentStackArr[3].equals(sArr[3])){
							//scenario 1 : no issues found our call-return combo
							//check to see for calls on loop
							//check for c1-r1, c1-r1, .... etc
							//TODO: HANDLE repititive better call by looking for loops
							if(!shortstack.isEmpty() ){
								if(shortstack.pop().equalsIgnoreCase(currentStackArr[3])){
									multiCallFlag = true;
									multiCallStr = currentStackArr;
									multiCallCount++;
									//scenario 1 A -  multiple call

								}else{
									
									if(multiCallFlag){
										//update neo4j call count	

										saveToNeo(currentStackArr, sArr, multiCallCount);
										multiCallCount =0;
										multiCallStr = null;
										multiCallFlag = false;
									}
									
									//add a new call to neo

									saveToNeo(currentStackArr, sArr, 1);
								}
									
								
							}
							shortstack.push(currentStackArr[3]);
							//send to neo4j
						}else{
							//scenario 2 : one-way call
							if(!shortstack.isEmpty() ){
								if(shortstack.pop().equalsIgnoreCase(currentStackArr[3])){
									//scenario 2 A - possible multiple call
									//update neo4j call count	
								}
							}
							shortstack.push(currentStackArr[3]);
							//submit currentStackArr to neo4j
							//submit sArr to neo4j
							
						}
					}else {
						//error scenario
						System.out.println(" invalid call type");
						break;
					}
				}
				
				
				//if return, write it to neo4j
				
				//if return, but doesn't match with call, mark the call as one-way
				//load teh one-way call
				//manage the original return
				
				/*
				 * merge (c:Class { name:'Class2' }) merge(m:Method { name:'getClass2' })   merge (c)-[r:calls]->(m) ON CREATE SET r.count = 1
					ON MATCH SET r.count = r.count+1
						RETURN r.count;

				 */
				
				
			}
			
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	private static boolean saveToNeo(String[] currentStackArr, String[] sArr, int count){
//		System.out.println(" currentStackArr " + Arrays.deepToString(currentStackArr));
//		System.out.println(" sArr " + Arrays.deepToString(sArr));
		//merge (c:Class { name:'Class2' }) merge(m:Method { name:'getClass2' })   merge (c)-[r:calls {order:3}]->(m)  SET r.count = 20;
		//get the calling method name
		//get the calling class name
		//get the called method name
		//get the called class name
		//get order
		StringBuilder sb = new StringBuilder();
		String[] callingClassMeth = currentStackArr[3].split("\\.");
		String[] calledClassMeth = sArr[3].split("\\.");
		sb.append("merge (sc:Class { name:\'").append(callingClassMeth[0]).append("\' }) ");//calling meth
		sb.append(System.getProperty("line.separator"));
		sb.append("merge (sm:Method { name:\'").append(callingClassMeth[1]).append("\' , parent:\'" + callingClassMeth[0] +  "\' }) ");//calling class
		sb.append(System.getProperty("line.separator"));
		sb.append("merge (ec:Class{ name:\'").append(calledClassMeth[0]).append("\' }) ");//called class
		sb.append(System.getProperty("line.separator"));
		sb.append("merge (em:Method { name:\'").append(calledClassMeth[1]).append("\', parent:\'" + calledClassMeth[0] +  "\' }) ");//called method
		int iln, rln = 0;
		if(currentStackArr[7].trim().equalsIgnoreCase("#")){
			iln = 0;
		}else{
			iln = Integer.parseInt(currentStackArr[7].trim());
		}
		if(sArr[5].trim().equalsIgnoreCase("#")){
			rln =Integer.parseInt(sArr[5].trim());
		}else{
			
		}
		sb.append(System.getProperty("line.separator"));
		sb.append("merge (sm)-[r:").append("calls").append(" {order:"+currentStackArr[0]+", iln:"+iln+", rln:"+rln).append("}]->(em) ");//actual call
		sb.append(System.getProperty("line.separator"));
		sb.append("merge (sc)-[r1:").append("contains").append(" ]->(sm) "); //class relationship
		sb.append(" SET r1.count = ").append(count);
		sb.append(System.getProperty("line.separator"));
		sb.append("merge (ec)-[r2:").append("contains").append(" ]->(em) "); //class relationship
		sb.append(" SET r2.count = ").append(count).append("");
		System.out.println(sb.toString());
		return true;
	}
	
	
	private  static  BufferedReader openFile(String filename){
		
		BufferedReader br = null;
		 
		try {

			br = new BufferedReader(new FileReader(filename));

		} catch (IOException e) {
			e.printStackTrace();
		} /*finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}*/
		return br;
		
	}

}
