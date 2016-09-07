# Source Ninja

Authors: Abinaya and Vaishali.

We are final year Engineering students. We love to read through source code of opensource projects like Hadoop, Spring etc. 
Reading source code of succesful projects helps us to understand coding best practices, in-depth understanding of those projects 
and most importantly it provides a window into such talented developers thought process. 

Unfortunately, reading through a source code is a very time consuming task. Just finding the entry point itself can be daunting. We wanted to make this process less irritating and help not just ourselfs but the whole developer community. 

This same can be done by setting up the project in eclipse and debugging the code. But, setting up a open source project in local machine can be painful and most developers avoid it. Our project will create a website where users can read through 
source code with zero setup time. 

Our idea was to create a Java Agent that can trace and log all relevant method call stacks. For example, let's say you want to understand
how node manager works in Hadoop. In hadoop YARN scripts, we add a -javaagent option to the line that invokes node manager using the jar created 
by this project. 

SourceNinja will trace and log all relevant method call stacks. That log file is then parsed, converted to a graph structure and 
fed to a Neo4J, a graph database. 

Then we use a web application to show how the method calls happen while initiating a Node Manager along with source code for those methods.
One of our modules read code from Github or from local files. For reading from local files, we index those java files in MySQL.  

Future Enhancements: We want to log method parameters and return types. With those data, we can further help a reader understanding of the code base. Unfortunately, we ran into stack overflow issues while trying to unmarshall java objects. 
We beleive we have a workaround. Fingers crossed. 

This project has uses beyond open source projects. It can be used in corporate companies, to help new hires learn those companies code base quickly. 
It can also be used for debugging hard to find bugs. 

