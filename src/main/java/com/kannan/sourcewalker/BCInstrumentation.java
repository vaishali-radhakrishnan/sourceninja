 package com.kannan.sourcewalker;
 
 import java.io.PrintStream;
 import java.lang.instrument.Instrumentation;
 import java.lang.management.ManagementFactory;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import javax.management.MBeanServer;
 import javax.management.ObjectName;
 import javax.management.StandardMBean;
 
 public class BCInstrumentation
 {
   private static Instrumentation instrumentation;
 
   public static void premain(String args, Instrumentation inst)
   {
     try
     {
       instrumentation = inst;
       //registerMBean();
       SourceWalkerTransformer transformer = new SourceWalkerTransformer(); 
       instrumentation.addTransformer(transformer);
       System.out.println("premain is called - trying for jar ");
     }
     catch (Exception ex) {
    	 System.out.println("ex premain " + ex.getMessage());
       ex.printStackTrace();
       Tracer.print(ex.toString());
       Logger.getLogger(BCInstrumentation.class.getName()).log(Level.SEVERE, null, ex);
     }
   }
 
   /*private static void registerMBean()
        {
          try
          {
         Thread t = new Thread()
            {
              public void run() {
                try {
              Thread.sleep(10000L);
              ObjectName name = new ObjectName("tracer:type=custom,name=configuration");
              TracerConfigurationMBean bean = new TracerConfigurationMBeanImpl();
              StandardMBean mbean = new StandardMBean(bean, TracerConfigurationMBean.class, false);
               MBeanServer server = ManagementFactory.getPlatformMBeanServer();
               server.registerMBean(mbean, name);
                } catch (Exception e) {
              Tracer.print(e.toString());
                }
              }
            };
        t.start();
          } catch (Exception e) {
        Tracer.print(e.toString());
          }
        }*/
 }

