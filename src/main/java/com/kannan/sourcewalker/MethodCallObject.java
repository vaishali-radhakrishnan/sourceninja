package com.kannan.sourcewalker;

public class MethodCallObject {
	
	private String methodName;
	private String className;
	private String invoker;
	private int invokingLine;
	
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getInvoker() {
		return invoker;
	}
	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}
	public int getInvokingLine() {
		return invokingLine;
	}
	public void setInvokingLine(int invokingLine) {
		this.invokingLine = invokingLine;
	}
	
	
	
	

}
