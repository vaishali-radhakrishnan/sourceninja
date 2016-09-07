 package com.kannan.sourcewalker;

import org.objectweb.asm.Label;
 
 public class Tracer
 {
   private static final TracerImpl tracer = new TracerImpl();
 
   public static void entry(String className, String mName) {
     tracer.entry(className, mName);
   }
   
   public static void invoke(String className, String mName, int lineNum) {
	     tracer.invoke(className, mName, lineNum);
	   }
   
   public static void entry(String className, String mName, String desc, Object[] paramValuesArray) {
	     tracer.entry(className, mName,desc, paramValuesArray);
	   }
   
   public static void entryLinenum(String descr, int linenum){
	   tracer.entryLinenum(descr, linenum);
   }
	 
 
   public static void exit(String className, String mName) {
     tracer.exit(className, mName);
   }
   

   
   public static void exitWithReturn(Object obj, String className, String mName, int returnLineNum ) {
	     tracer.exitWithReturn(obj, className, mName, returnLineNum );
	   }
   
/*   public static void jumpCall(Object obj, String className, String mName, Label label, int returnLineNum, int opcode ) {
	     tracer.jumpCall(obj, className, mName, label, returnLineNum, opcode );
	   }*/
   public static void jumpCall(String className, String mName, int label,  int returnLineNum, int opcode ) {
	     //tracer.jumpCall( className, mName, label, returnLineNum, opcode );
	   }
 
   public static void print(String str)
   {
     tracer.print(str);
   }

	public static void methodParams(String className, String mName, Object paramArray) {
	     tracer.methodParams(className, mName, paramArray);
	   }

 }

/* Location:           /Users/bdcoe/Downloads/MethodTracer.jar
 * Qualified Name:     rejeev.tracer.Tracer
 * JD-Core Version:    0.6.2
 */