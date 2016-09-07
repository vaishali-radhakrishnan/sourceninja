package com.kannan.sourcewalker;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

import com.kannan.sourcewalker.Configuration;

public class ClassInfoGatherer extends ClassVisitor {

  private String owner;

  private boolean isInterface;
  private boolean exclude;
  private final String classFName = "_ClassID_";
  private final String classOName = "_ObjectID_";
  private final String counterName = "_COUNTER_";
  private boolean isClassFieldPresent;
  private boolean isObjFieldPresent;
  private boolean isCounterPresent;
  private boolean visitedStaticBlock; 
  private boolean trulyExclude;

  public ClassInfoGatherer(ClassVisitor cv) {
    super(ASM5, cv);
   // System.out.println(" inside cv");
  }

  public void visit(int version, int access, String name,
      String signature, String superName, String[] interfaces) {
    cv.visit(version, access, name, signature, superName, interfaces);
    this.owner = name;
    this.isInterface = (access & ACC_INTERFACE) != 0;
    this.exclude = Configuration.getInstance().isToBeExcluded( name);
    this.trulyExclude = Configuration.getInstance().isToBeTrulyExcluded( name);
    /*if (!this.trulyExclude){ 
    	System.out.println("truly instrumenting class: " +  name ); 
    	
    	//ObjectPrinter.saveLogs("{  \"instrumenting class\": \" " + name + "\" }");
	}else {
	 System.out.println("truly NOT instrumenting class: " +  name );
	 //ObjectPrinter.saveLogs("{  \"not instrumenting class\": \" " + name + "\" }");
    }*/

  }
  
  @Override
  public FieldVisitor visitField(int access, String name, String desc,
  String signature, Object value) {
	  if (name.equals(classFName)) {
		  isClassFieldPresent = true;
	  }
	  if (name.equals(classOName)) {
		  isObjFieldPresent = true;
	  }
	  if(name.equals(counterName)){
		  isCounterPresent = true;
	  }
	  return cv.visitField(access, name, desc, signature, value);
  }

  public MethodVisitor visitMethod(int access, String name,
      String desc, String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
        exceptions);
    //System.out.println(" name " + name + " access " + access);
    
/*    if (!this.exclude && !isInterface && mv != null && name.equals("<clinit>") && !visitedStaticBlock) {
    	System.out.println(" see this");
    	visitedStaticBlock = true;
        mv = new StaticBlockMethodVisitor(mv);
    }
    
    if (!this.exclude && !isInterface && mv != null && name.equals("<init>") ) {
    	if(!visitedStaticBlock){
    		
    		System.out.println( " might seethis");
    		visitedStaticBlock = true;
            mv = new StaticBlockMethodVisitor(mv);
    	}
        mv = new NonStaticBlockMethodVisitor(mv);
    }*/
    	if (!this.exclude && !isInterface && mv !=null){
   // if (!this.exclude && !isInterface && mv != null && !name.equals("<init>")) {
   // if ( (!trulyExclude && !isInterface  &&  mv != null  ) ) {
    	//System.out.println(" name " +  this.owner  + " " + name) ;
    	Type[] parameterTypeArray = Type.getArgumentTypes(desc);
    	Type returnType = Type.getReturnType(desc);
    	//SWHelper swHelper = new SWHelper();
    	
    	//mv = new LineNumberAdapter(access, mv, access, name, desc,  this.owner, swHelper);
    	
        mv = new MethodInfoGatherer1(access, name, desc, mv, parameterTypeArray, this.owner, returnType, this.exclude, 0);
    }
    return mv;
  }

  public void visitEnd() {
    //if (!isInterface && !isCounterPresent && !isClassFieldPresent && !isObjFieldPresent) {
    if (!isInterface && !this.exclude ) {
      //System.out.println( " in visit end creating all 3 vfields");
     // FieldVisitor fv ;
    	// fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, "timer", "J", null, null);
	      /*if (fv != null) {
	        fv.visitEnd();
	      }*/

		// fv = cv.visitField(ACC_PUBLIC  +  ACC_STATIC, classOName, "Ljava/util/UUID;",null, null);
		/*if (fvc != null) {
			fv.visitEnd();
		}*/
		
		// fv = cv.visitField(ACC_PUBLIC  +  ACC_STATIC, counterName, "Ljava/util/concurrent/atomic/AtomicInteger;",null, null);
		/*if (fvco != null) {
			fv.visitEnd();
		}*/

		// fv = cv.visitField(ACC_PUBLIC  , classFName, "Ljava/lang/String;",null, null);
		
	//	if (fv != null) {
	//		fv.visitEnd();
	//	}
	}
      
      
      cv.visitEnd();
    }
 
 

  class MethodInfoGatherer1 extends LocalVariablesSorter {

	   // private int time;

	  private Label lTryBlockStart;
      private Label lTryBlockEnd;
      private Label lCatchBlockStart;
      private Label lCatchBlockEnd;
      private int currentLine;
      private int opcode;
      private int methodInvocationLine;
      private int startLineNum;
      private int startLine;
      private String desc;
      
	    private Type[] parameterTypeArray = null;
		  private int access;
		  private String methodName = null;
		  private String className = null;
		  private Type returnType = null;
		  private boolean isExClass = true;

	    public MethodInfoGatherer1(int access, String name, String desc, MethodVisitor mv, Type[] parameterTypeArray, String className , Type returnType, boolean isExcluded, int startLineNum) {
	      super(ASM4, access, desc, mv);
	      this.parameterTypeArray = parameterTypeArray;
	      this.access = access;
	      this.methodName = name;
	      this.className = className;
	      this.returnType = returnType;
	      this.isExClass = isExcluded;
	      this.startLineNum = startLineNum;
	      this.desc = desc;
	    }

	    
	    public void visitLineNumber(int line, Label start) {
			if(this.startLine == 0){
				this.startLine = line;			
			}
	    	this.currentLine = line;
	    	super.visitLineNumber(line, start);
	    	
	    	}
	    
	    public void visitMethodInsn(int opcode,String owner,  String name,String desc){
	    	//System.out.println(" owner " + owner);
	    	if( !(Configuration.getInstance().isToBeExcluded( owner))){
	    		
	    		mv.visitLdcInsn(this.className+"."+this.methodName);
	    		//mv.visitLdcInsn(owner);
	    		mv.visitLdcInsn(owner+"."+name);
	    		//mv.visitLdcInsn(desc);
	    		this.methodInvocationLine = this.currentLine;
	    		mv.visitLdcInsn(this.currentLine);
		         mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "invoke", "(Ljava/lang/String;Ljava/lang/String;I)V");
	    	
	    	}
	    	
	    	mv.visitMethodInsn(opcode, owner, name, desc );
	    }
	    
	    public void visitCode() {
	      super.visitCode();
	      if(!this.methodName.startsWith("hashCode")){
	    	//  if(!this.isExClass){
	    	  if(true){
	/*	      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J");
		      time = newLocal(Type.LONG_TYPE);
		      mv.visitVarInsn(Opcodes.LSTORE, time);*/
	
		      
		      //TODO - put all these code in catch block
	
		    	int off = (this.access & Opcodes.ACC_STATIC) > 0 ? 0 : 1;
		    	
		    	 /*mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		    	 mv.visitLdcInsn("access " + this.access  +" static " + Opcodes.ACC_STATIC+ " off " + off );
		    	 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"); */
		    	 
		    	 
		    	if(null!= this.parameterTypeArray  ){  
		    	  
		    	// Create array with length equal to number of parameters
		    	    mv.visitIntInsn(Opcodes.BIPUSH, parameterTypeArray.length);
		    	    mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
		    	    
	
		  	        int array = newLocal(Type.getObjectType("java/lang/Object"));
		  	        mv.visitVarInsn(Opcodes.ASTORE, array);
	
			  	  	int i = 0 + off;
			  	  	int arrayIndex = 0;
			  	  	//System.out.println( "off " + off + " name " + this.name + "length " + this.parameterTypeArray.length );
			         for (Type tp : parameterTypeArray) {
			        	 mv.visitVarInsn(Opcodes.ALOAD, array);
			             mv.visitIntInsn(Opcodes.BIPUSH, arrayIndex);
	
		        	     if (tp.equals(Type.BOOLEAN_TYPE)) {																	
		        	    	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		        	    	 mv.visitVarInsn(Opcodes.ILOAD, i);	
		        	    	 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
		        	    	 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V"); 
			             } 
			                else  if (tp.equals(Type.BYTE_TYPE)) {
			            	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");                 
			                 mv.visitVarInsn(Opcodes.ILOAD, i);
			                 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			                 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(B)V"); 	
			             }
			             else if (tp.equals(Type.CHAR_TYPE)) {
			            	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); 
			                 mv.visitVarInsn(Opcodes.ILOAD, i);
			                 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			                 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V"); 
			             }
			             else if (tp.equals(Type.SHORT_TYPE)) {
			            	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); 
			                 mv.visitVarInsn(Opcodes.ILOAD, i);
			                 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			                 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(S)V"); 
			             }
			             else if (tp.equals(Type.INT_TYPE)) {
			                //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); 
			                mv.visitVarInsn(Opcodes.ILOAD, i);
			                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			                //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");	            	 
			            }
			             else if (tp.equals(Type.LONG_TYPE)) {
			            	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); 
			                 mv.visitVarInsn(Opcodes.LLOAD, i);
			                 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			                 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V"); 
			                 i++;
			             }
			             else if (tp.equals(Type.FLOAT_TYPE)) {
			            	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			                 mv.visitVarInsn(Opcodes.FLOAD, i);
			                 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			                 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V"); 
			             }
			             else if (tp.equals(Type.DOUBLE_TYPE)) {
			            	 //mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			                 mv.visitVarInsn(Opcodes.DLOAD, i);
			                 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			                 //mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V"); ;
			                 i++;
			             }
			                else {
			                	//mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			                	mv.visitVarInsn(Opcodes.ALOAD, i);
			                	//mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V"); 
			                }	
		        	     mv.visitInsn(Opcodes.AASTORE);//storing the object in array..index pos is i
			             i++;
			             arrayIndex++;
			         }
			         
			         
			         
			         lTryBlockStart = new Label();
		             lTryBlockEnd = new Label();
		             lCatchBlockStart = new Label();
		             lCatchBlockEnd = new Label();

		             // set up try-catch block for RuntimeException
		             visitTryCatchBlock(lTryBlockStart, lTryBlockEnd, lCatchBlockStart, "java/lang/NoClassDefFoundError");

	                // started the try block
	                visitLabel(lTryBlockStart);
	                
	                 mv.visitLdcInsn(this.className);
			         mv.visitLdcInsn(this.methodName);
			         mv.visitLdcInsn(this.desc);
			         mv.visitVarInsn(Opcodes.ALOAD, array);
			         mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "entry", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V");
		    	
			         // closing the try block
		             visitLabel(lTryBlockEnd);
		             
		             // when here, no exception was thrown, so skip exception handler
		               visitJumpInsn(Opcodes.GOTO, lCatchBlockEnd);
		               
		            // exception handler starts here, with RuntimeException stored
		                // on stack
		                visitLabel(lCatchBlockStart);
		                mv.visitInsn(Opcodes.POP);
		                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		                mv.visitLdcInsn("Profiler error in calling print " + this.className + "." + this.methodName);
				    	 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"); 
				    	// exception handler ends here:
			                visitLabel(lCatchBlockEnd);
			         
		    	}else{
		    		
		    	/*	lTryBlockStart = new Label();
		             lTryBlockEnd = new Label();
		             lCatchBlockStart = new Label();
		             lCatchBlockEnd = new Label();

		             // set up try-catch block for RuntimeException
		             visitTryCatchBlock(lTryBlockStart, lTryBlockEnd, lCatchBlockStart, "java/lang/Exception");

	                // started the try block
	                visitLabel(lTryBlockStart);*/
	                
		    		 mv.visitLdcInsn( this.className );
			         mv.visitLdcInsn(this.methodName);
			         //mv.visitLdcInsn(this.currentLine);
			         mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "entry", "(Ljava/lang/String;Ljava/lang/String;)V");
			    /*  // closing the try block
		             visitLabel(lTryBlockEnd);
		             
		             // when here, no exception was thrown, so skip exception handler
		               visitJumpInsn(Opcodes.GOTO, lCatchBlockEnd);
		               
		            // exception handler starts here, with RuntimeException stored
		                // on stack
		                visitLabel(lCatchBlockStart);
		                
		                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
				    	 mv.visitLdcInsn("Profiler error in calling print " );
				    	 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"); 
				    	// exception handler ends here:
			                visitLabel(lCatchBlockEnd);*/
		    	}
		      }else{

		    		if( this.methodName.equals("<init>") && !(this.className.contains("$"))){
			    		lTryBlockStart = new Label();
			             lTryBlockEnd = new Label();
			             lCatchBlockStart = new Label();
			             lCatchBlockEnd = new Label();

			             // set up try-catch block for RuntimeException
			             visitTryCatchBlock(lTryBlockStart, lTryBlockEnd, lCatchBlockStart, "java/lang/NoClassDefFoundError");

		                // started the try block
		                visitLabel(lTryBlockStart);
		    			//mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
					    //     mv.visitLdcInsn(" test " + this.className + " " + this.methodName);
					    //	 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"); 
					    	
			    		 mv.visitLdcInsn(" test " + this.className );
				         mv.visitLdcInsn(this.methodName);
				         mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "entry", "(Ljava/lang/String;Ljava/lang/String;)V");
				    
				        
				           // closing the try block
			             visitLabel(lTryBlockEnd);
			             
			             // when here, no exception was thrown, so skip exception handler
			               visitJumpInsn(Opcodes.GOTO, lCatchBlockEnd);
			               
			            // exception handler starts here, with RuntimeException stored
			                // on stack
			                visitLabel(lCatchBlockStart);
			                mv.visitInsn(Opcodes.POP);
			                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
					    	 mv.visitLdcInsn("Profiler error in calling print " + this.className + "." + this.methodName);
					    	 mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"); 
					    	// exception handler ends here:
				                visitLabel(lCatchBlockEnd);
		    		}
		      }
	    	}
	    
	    }


	    public void visitInsn (int opcode){
	    	
	    
	    	if(!this.methodName.startsWith("hashCode") && ((opcode >= Opcodes.IRETURN && opcode <=Opcodes.RETURN) || (opcode == Opcodes.ATHROW))){  		
	            
	    		
	    		
	             if(opcode == Opcodes.ARETURN){
	     	    	mv.visitInsn(Opcodes.DUP);
	     	    	mv.visitLdcInsn(this.className);
			        mv.visitLdcInsn(this.methodName);
			        mv.visitLdcInsn(this.currentLine);
		            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
		             
	     	    	
		       	}else if(opcode == Opcodes.IRETURN){
			    	 mv.visitInsn(Opcodes.DUP);
			    	 mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			    	 mv.visitLdcInsn(this.className);
			         mv.visitLdcInsn(this.methodName);
			         mv.visitLdcInsn(this.currentLine);
		             mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
			            
				}else if(opcode == Opcodes.DRETURN){
	        		mv.visitInsn(Opcodes.DUP2);
	        		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
	        		mv.visitLdcInsn(this.className);
			        mv.visitLdcInsn(this.methodName);
			        mv.visitLdcInsn(this.currentLine);
		            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
			         
	        	}else if(opcode == Opcodes.LRETURN){
	        		mv.visitInsn(Opcodes.DUP2);
	        		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
	        		mv.visitLdcInsn(this.className);
			        mv.visitLdcInsn(this.methodName);
			        mv.visitLdcInsn(this.currentLine);
		            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
			         
	        	}
	        	else if(opcode == Opcodes.FRETURN){
	        		mv.visitInsn(Opcodes.DUP);	
	        		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
	        		mv.visitLdcInsn(this.className);
			        mv.visitLdcInsn(this.methodName);
			        mv.visitLdcInsn(this.currentLine);
		            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
			         
	        	}else if(opcode == Opcodes.RETURN){
	        		mv.visitLdcInsn("void return");
	        		mv.visitLdcInsn(this.className);
			        mv.visitLdcInsn(this.methodName);
			        mv.visitLdcInsn(this.currentLine);
		            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
			         
	        	}else if(opcode == Opcodes.ATHROW){
	     	    	mv.visitInsn(Opcodes.DUP);
	     	    	mv.visitLdcInsn(this.className);
			        mv.visitLdcInsn(this.methodName);
			        mv.visitLdcInsn(this.currentLine);
			       // mv.visitLdcInsn(this.startLine);
		            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "exitWithReturn", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)V");
			         
	        	}
	       
	          
	            
	            

	    	}
	    	
	    	mv.visitInsn(opcode);
	  	
	    }
	    
/*	    public void visitJumpInsn(int opcode, Label label) {
	 	    	
        	mv.visitLdcInsn(this.className);
	        mv.visitLdcInsn(this.methodName);
		        try {
		        	//int i = 0;
		        	int k = -1 ;
		        	k = label.getOffset();
		        	if( k >0){
		        		i = k;
		        	}
		        	//mv.visitLdcInsn(i+"");
		        	mv.visitLdcInsn(this.currentLine);
		        	mv.visitLdcInsn(this.currentLine);
		        	this.opcode = opcode;
		        	mv.visitLdcInsn(this.opcode);
					//mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "jumpCall", "(Ljava/lang/String;Ljava/lang/String;Lorg/objectweb/asm/Label;II)V");
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "jumpCall", "(Ljava/lang/String;Ljava/lang/String;III)V");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	    	 	
	            mv.visitJumpInsn(opcode, label);

	    }*/
	    
	    /*	    public void visitEnd() {
            super.visitEnd();
            mv.visitLdcInsn(this.className);
	        mv.visitLdcInsn(this.methodName);
	        mv.visitLdcInsn(" ");
	        mv.visitLdcInsn(this.currentLine);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "invoke", "(Ljava/lang/String;Ljava/lang/String;I)V");
        }
	    */
	    public void visitMaxs(int maxStack, int maxLocals) {
	    	mv.visitLdcInsn(this.className);
	        mv.visitLdcInsn(this.methodName);
	        mv.visitLdcInsn("MAX");
	        mv.visitLdcInsn(this.currentLine);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kannan/sourcewalker/Tracer", "invoke", "(Ljava/lang/String;Ljava/lang/String;I)V");
        
	      super.visitMaxs(maxStack + 4, maxLocals);
	    }
	    

	    
	  }
  

}
