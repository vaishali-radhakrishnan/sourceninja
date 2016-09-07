 package com.kannan.sourcewalker;
 
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 public class Configuration
 {
/*  19 */   private Properties props = new Properties();
/*  20 */   private static Configuration singleton = new Configuration();
   private Set<String> excludeList;
			private Set<String> trulyExcludeList;
   private Set<String> includeList;
   private boolean allThreadsLogged;
   private boolean loggingOn;
/*  25 */   private Set<String> threadList = new HashSet();
   private boolean file;
   private boolean console;
 
   private Configuration()
   {
     try
     {
/*  31 */       InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("tracer.properties");
/*  32 */       this.props.load(is);
/*  33 */       String excludedPackages = this.props.getProperty("excludedPackages");
				String trulyExcludedPackages = this.props.getProperty("trulyExcludedPackages");
/*  34 */       if (excludedPackages != null) this.excludeList = parse(excludedPackages); else
/*  35 */         this.excludeList = new HashSet();

				if (trulyExcludedPackages != null) this.trulyExcludeList = parse(trulyExcludedPackages); else
/*  35 */         this.trulyExcludeList = new HashSet();

/*  36 */       String allThreadsString = this.props.getProperty("allThreadsLogged");
/*  37 */       if ((allThreadsString != null) && (allThreadsString.equals("true"))) this.allThreadsLogged = true;
/*  38 */       String includedPackages = this.props.getProperty("includedPackages");
/*  39 */       if (includedPackages != null) this.includeList = parse(includedPackages);
/*  40 */       String threadListString = this.props.getProperty("threadList");
/*  41 */       if (threadListString != null) this.threadList = parse(threadListString);
/*  42 */       String loggingOnString = this.props.getProperty("loggingOnAtStartup");
/*  43 */       if ((loggingOnString != null) && (loggingOnString.equals("true"))) this.loggingOn = true;
/*  44 */       String fileString = this.props.getProperty("file");
/*  45 */       if ((fileString != null) && (fileString.equals("true"))) this.file = true;
/*  46 */       String consoleString = this.props.getProperty("console");
/*  47 */       if ((consoleString != null) && (consoleString.equals("true"))) this.console = true;
 
/*  49 */       System.out.println(this.excludeList);
/*  50 */       System.out.println("loggingON: " + this.loggingOn);
/*  51 */       System.out.println("all threads: " + this.allThreadsLogged);
     } catch (Exception e) {
/*  53 */       e.printStackTrace();
     }
   }
 
/*  57 */   public static Configuration getInstance() { return singleton; }
 
   public boolean isToBeExcluded(String className)
   {
     if (this.includeList != null) {
       boolean contains = false;
       for (String pkg : this.includeList) {
         if (className.startsWith(pkg)) {
           contains = true;
           break;
         }
       }
       if (!contains) {
					return true;
					
					}
     }
 
     for (String pkg : this.excludeList) {
       if (className.startsWith(pkg)) {
					if(className.startsWith("org/apache/hadoop") && !(className.startsWith("org/apache/hadoop/log/metrics/EventCounter"))){
						return false;
					}else{
						return true;
					}
							
				}
     }
     return false;
   }
 
   public boolean isToBeTrulyExcluded(String className)
   {
     if (this.includeList != null) {
       boolean contains = false;
       for (String pkg : this.includeList) {
         if (className.startsWith(pkg)) {
           contains = true;
           break;
         }
       }
       if (!contains) {
					return true;
					
					}
     }
 
     for (String pkg : this.trulyExcludeList) {
       if (className.startsWith(pkg)) {
					if(className.startsWith("org/apache/hadoop") && !(className.startsWith("org/apache/hadoop/log/metrics/EventCounter"))){
						return false;
					}else{
						return true;
					}
							
				}
     }
     return false;
   }
 
   
   
   public boolean isLoggingEnabled() {
     if (!this.loggingOn) return false;
     if (this.allThreadsLogged) return true;
     if (this.threadList.contains(Thread.currentThread().getName())) return true;
     return false;
   }
 
   public boolean isFile() {
     return this.file;
   }
 
   public boolean isConsole() {
     return this.console;
   }
 
   public void addThread(String threadName) {
    this.threadList.add(threadName);
   }
 
   public void removeThread(String threadName) {
     this.threadList.remove(threadName);
   }
 
   public void enableLogging(boolean value) {
     this.loggingOn = value;
   }
 
   private Set<String> parse(String str) {
     StringTokenizer tokens = new StringTokenizer(str, ",");
     Set returnValue = new HashSet();
     while (tokens.hasMoreTokens()) {
       returnValue.add(tokens.nextToken());
     }
     return returnValue;
   }
 }

