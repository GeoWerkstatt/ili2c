package ch.interlis.ili2c;

import java.io.*;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.ErrorListener;
import ch.interlis.ili2c.parser.Ili2Parser;
import ch.interlis.ili2c.parser.Ili22Parser;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

import ch.interlis.ili2c.config.*;
import ch.ehi.basics.logging.EhiLogger;

public class Main
{
  private static String version=null;
  protected static boolean hasArg(String v1, String v2, String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals(v1) || args[i].equals(v2))
        return true;
    }
    return false;
  }

  protected static void printVersion ()
  {
    System.err.println("INTERLIS Compiler, Version "+getVersion());
    System.err.println("  Distributed by the Coordination of Geographic Information");
    System.err.println("  and Geographic Information Systems Group (COSIG), CH-3084 Wabern");
    System.err.println("  Developed by Adasys AG, CH-8005 Zurich");
    System.err.println("  Maintained by Eisenhut Informatik AG, CH-3400 Burgdorf");
    System.err.println("  See http://www.interlis.ch for information about INTERLIS");
    System.err.println("  Parts of this program have been generated by ANTLR; see http://www.antlr.org");
    System.err.println("  This product includes software developed by the");
    System.err.println("  Apache Software Foundation (http://www.apache.org/).");
  }


  protected static void printDescription ()
  {
    System.err.println("DESCRIPTION");
    System.err.println("  Parses and compiles INTERLIS Version 2.3 data model definitions.");
    System.err.println("  Other options include conversion from INTERLIS Version 1 and back");
    System.err.println("  (option -o1) and generation of an XML-Schema, released 2001 (option -oXSD).");
  }


  protected static void printUsage (String progName)
  {
    System.err.println ("USAGE");
    System.err.println("  " + progName + " [Options] file1.ili file2.ili ...");
  }


  protected static void printExamples (String progName)
  {
    System.err.println ("EXAMPLES");
    System.err.println ();
    System.err.println ("Check whether an INTERLIS definition in \"file1.ili\" is valid:");
    System.err.println ("    " + progName + " file1.ili");
    System.err.println ();
    System.err.println ("Check whether a definition distributed over several files is valid:");
    System.err.println ("    " + progName + " file1.ili file2.ili");
    System.err.println ();
    System.err.println ("Generate an INTERLIS-1 definition:");
    System.err.println ("    " + progName + " -o1 file1.ili file2.ili");
    System.err.println ();
    System.err.println ("Generate an INTERLIS-2 definition:");
    System.err.println ("    " + progName + " -o2 file1.ili file2.ili");
    System.err.println ();
    System.err.println ("Generate a definition of the predefined MODEL INTERLIS:");
    System.err.println ("    " + progName + " -o2 --with-predefined");
    System.err.println ();
    System.err.println ("Generate an XML-Schema:");
    System.err.println ("    " + progName + " -oXSD file1.ili file2.ili");
    System.err.println ();
  }

  public static void main (String[] args)
  {
    boolean emitPredefined = false;
    int     emitVersion = 0;
    boolean emitXSD = false;
    boolean emitFMT = false;
    boolean emitJAVA = false;
	boolean emitIOM = false;
	boolean doAuto=true;
    boolean checkMetaObjs=false;
    boolean withWarnings=true;
    int     numErrorsWhileGenerating = 0;
    String  progName = "ili2c";
    String  notifyOnError = "compiler@interlis.ch";

	EhiLogger.getInstance().addListener(LogListener.getInstance());
	EhiLogger.getInstance().removeListener(ch.ehi.basics.logging.StdListener.getInstance());
	
	if(args.length==0){
	        ch.interlis.ili2c.gui.Main.main(args);
		return;
	}

    if (hasArg ("-u", "--usage", args))
    {
      printUsage (progName);
      return;
    }

    if (hasArg("-h", "--help", args)
        || args.length == 0)
    {
      printVersion ();
      System.err.println();
      printDescription ();
      System.err.println();
      printUsage (progName);
      System.err.println();
      System.err.println("OPTIONS");
      System.err.println();
	  System.err.println("--no-auto             don't look automatically after required models.");
      System.err.println("-o0                   Generate no output (default).");
      System.err.println("-o1                   Generate INTERLIS-1 output.");
      System.err.println("-o2                   Generate INTERLIS-2 output.");
      System.err.println("-oXSD                 Generate an XML-Schema.");
      System.err.println("-oFMT                 Generate an INTERLIS-1 Format.");
      System.err.println("-oJAVA                Generate JAVA classes.");
	  System.err.println("-oIOM                 Generate Model as INTERLIS-Transfer (XTF).");
      System.err.println("--with-predefined     Include the predefined MODEL INTERLIS in");
      System.err.println("                      the output. Usually, this is omitted.");
      System.err.println("--without-warnings    Report only errors, no warnings. Usually,");
      System.err.println("                      warnings are generated as well.");
	  System.err.println("--trace               Display detailed trace messages.");
      System.err.println("-h|--help             Display this help text.");
      System.err.println("-u|--usage            Display short information about usage.");
      System.err.println("-v|--version          Display the version of " + progName + ".");
      System.err.println();
      printExamples (progName);
      return;
    }

    if (hasArg("-v", "--version", args))
    {
      printVersion ();
      return;
    }

    try {
	
		ArrayList ilifilev=new ArrayList();
      for (int i = 0; i < args.length; i++)
      {
        java.io.InputStream stream;
        String              streamName;

        if (args[i].equals("--with-predefined"))
        {
          emitPredefined = true;
          continue;
        }
		if (args[i].equals("--trace"))
		{
		  EhiLogger.getInstance().setTraceFiler(false);
		  continue;
		}
		if (args[i].equals("--no-auto"))
		{
		  doAuto=false;
		  continue;
		}
        else if (args[i].equals("-o0"))
        {
          emitVersion = 0;
          emitXSD = false;
          emitFMT = false;
          emitJAVA = false;
		  emitIOM = false;
          continue;
        }
        else if (args[i].equals("-o1"))
        {
          emitVersion = 1;
          emitXSD = false;
          emitFMT = false;
          emitJAVA = false;
		  emitIOM = false;
          continue;
        }
        else if (args[i].equals("-o2"))
        {
          emitVersion = 2;
          emitXSD = false;
          emitFMT = false;
          emitJAVA = false;
		  emitIOM = false;
          continue;
        }
        else if (args[i].equals("-oXSD"))
        {
          emitVersion = 0;
          emitXSD = true;
          emitFMT = false;
          emitJAVA = false;
		  emitIOM = false;
          continue;
        }
        else if (args[i].equals("-oFMT"))
        {
          emitVersion = 0;
          emitXSD = false;
          emitFMT = true;
          emitJAVA = false;
		  emitIOM = false;
          continue;
        }
        else if (args[i].equals("-oJAVA"))
        {
          emitVersion = 0;
          emitXSD = false;
          emitFMT = false;
          emitJAVA = true;
		  emitIOM = false;
          continue;
        }
		else if (args[i].equals("-oIOM"))
		{
		  emitVersion = 0;
		  emitXSD = false;
		  emitFMT = false;
		  emitJAVA = false;
		  emitIOM = true;
		  continue;
		}
        else if (args[i].equals("-"))
        {
          stream = new DataInputStream (System.in);
          streamName = null;
        }
        else if (args[i].equals ("--without-warnings"))
        {
			withWarnings=false;
          continue;
        }
        else if (args[i].equals ("--with-warnings"))
        {
			withWarnings=true;
          continue;
        }
        else if (args[i].charAt(0) == '-')
        {
          System.err.println (progName + ":Unknown option: " + args[i]);
          continue;
        }
        else
        {
        	String filename=args[i];
        	if(new File(filename).isFile()){
				ilifilev.add(filename);
        	}else{
				EhiLogger.logError(args[i] + ": There is no such file.");
        	}
        }

      }

	  Configuration config=null;
      if(doAuto){
		// get dirs
		ArrayList ilipathv = getIliLookupPaths(ilifilev);
		// scan models
		config=ModelScan.getConfigWithFiles(ilipathv,ilifilev);
		if(config==null){
			EhiLogger.logError("ili-file scan failed");
			return;
		}
		if(emitVersion >0 
			|| emitXSD
			|| emitFMT
			|| emitJAVA
			|| emitIOM){
			// skip listing of used ili-files
		}else{
			Iterator filei=config.iteratorFileEntry();
			while(filei.hasNext()){
				FileEntry file=(FileEntry)filei.next();
				String filename=file.getFilename();
				EhiLogger.logState(filename+" "+ModelScan.getIliFileVersion(new File(filename)));
			}
		}

      }else{
      	config=new Configuration();
      	Iterator ilifilei=ilifilev.iterator();
      	while(ilifilei.hasNext()){
      		String ilifile=(String)ilifilei.next();
			FileEntry file=new FileEntry(ilifile,FileEntryKind.ILIMODELFILE);
			config.addFileEntry(file);
      	}
      }
      config.setGenerateWarnings(withWarnings);
      
		// compile models
		TransferDescription td=runCompiler(config);

      switch (emitVersion)
      {
      case 1:
      	try{
			numErrorsWhileGenerating = ch.interlis.ili2c.generator.Interlis1Generator.generate(
			  new java.io.PrintWriter(System.out), td);
      	}catch(Exception ex){
      		EhiLogger.logError(ex);
      	}
        break;

      case 2:
		  try{
			ch.interlis.ili2c.generator.Interlis2Generator gen=new ch.interlis.ili2c.generator.Interlis2Generator();
			numErrorsWhileGenerating = gen.generate(
			  new java.io.PrintWriter(System.out), td, emitPredefined);
		  }catch(Exception ex){
			  EhiLogger.logError(ex);
		  }
        break;

      default:
	  	java.io.PrintWriter out=new java.io.PrintWriter(System.out);
		if (emitXSD) {
			try{
				if(td.getLastModel().getIliVersion().equals("2.2")){
					numErrorsWhileGenerating =
						ch.interlis.ili2c.generator.XSD22Generator.generate(
							out,
							td);
				}else{
					numErrorsWhileGenerating =
						ch.interlis.ili2c.generator.XSDGenerator.generate(
							out,
							td);
				}
			}catch(Exception ex){
				EhiLogger.logError(ex);
			}
		} else if (emitFMT) {
			try{
				numErrorsWhileGenerating =
					ch.interlis.ili2c.generator.Interlis1Generator.generateFmt(
						out,
						td);
			}catch(Exception ex){
				EhiLogger.logError(ex);
			}
		} else if (emitIOM) {
			try{
				numErrorsWhileGenerating =
					ch.interlis.ili2c.generator.iom.IomGenerator.generate(
						out,
						td);
			}catch(Exception ex){
				EhiLogger.logError(ex);
			}
		}
		out.close();
        break;
      }
    }
    catch(Exception ex)
    {
      EhiLogger.logError(progName + ":An internal error has occured. Please notify " + notifyOnError,ex);
    }
  }

public static ArrayList getIliLookupPaths(ArrayList ilifilev) {
	ArrayList ilipathv=new ArrayList();
	java.util.HashSet seenDirs=new java.util.HashSet();
	Iterator ilifilei=ilifilev.iterator();
	while(ilifilei.hasNext()){
		String ilifile=(String)ilifilei.next();
		String parentdir=new java.io.File(ilifile).getAbsoluteFile().getParent();
		if(!seenDirs.contains(parentdir)){
			seenDirs.add(parentdir);
			ilipathv.add(parentdir);
		}
	}
	String ili2cHome=getIli2cHome();
	if(ili2cHome!=null){
		ilipathv.add(ili2cHome+java.io.File.separator+"standard");
	}
	return ilipathv;
}
  static public String getIli2cHome()
  {
	String classpath = System.getProperty("java.class.path");
	int index = classpath.toLowerCase().indexOf("ili2c.jar");
	int start = classpath.lastIndexOf(java.io.File.pathSeparator,index) + 1;
	if(index > start)
	{
		return classpath.substring(start,index - 1);
	}
	return null;
  }
  static public TransferDescription runCompiler(Configuration config){
	  
	  ArrayList filev=new ArrayList();
	  if(config.isAutoCompleteModelList()){
		  ArrayList ilifilev=new ArrayList();
	        Iterator filei=config.iteratorFileEntry();
	        while(filei.hasNext()){
	          FileEntry e=(FileEntry)filei.next();
	          if(e.getKind()==FileEntryKind.ILIMODELFILE){
	            String fileName = e.getFilename();
	            ilifilev.add(fileName);
	          }
	        }
			ArrayList modeldirv = getIliLookupPaths(ilifilev);
		  ch.interlis.ili2c.config.Configuration files=ch.interlis.ili2c.ModelScan.getConfigWithFiles(modeldirv,ilifilev);
		  if(files==null){
 			EhiLogger.logError("ili-file scan failed");
			  return null;
		  }
		  logIliFiles(files);
		  // copy result of scan to original config
	        filei=files.iteratorFileEntry();
	        while(filei.hasNext()){
	          FileEntry e=(FileEntry)filei.next();
			  filev.add(e);
	        }
	  }else{
	        Iterator filei=config.iteratorFileEntry();
	        while(filei.hasNext()){
	          FileEntry e=(FileEntry)filei.next();
	          filev.add(e);
	        }
	  }
      TransferDescription   desc = new TransferDescription ();
      boolean emitPredefined=config.isIncPredefModel();
      boolean checkMetaObjs=config.isCheckMetaObjs();
	  CompilerLogEvent.enableWarnings(config.isGenerateWarnings());

        // boid  to basket mappings
        Iterator boidi=config.iteratorBoidEntry();
        while(boidi.hasNext()){
          BoidEntry e=(BoidEntry)boidi.next();
          desc.addMetadataMapping(e.getMetaDataUseDef(),e.getBoid());
        }


        // model and metadata files
        double version=0.0;
        Iterator filei=filev.iterator();
        while(filei.hasNext()){
          FileEntry e=(FileEntry)filei.next();
          if(e.getKind()==FileEntryKind.METADATAFILE){
            if(checkMetaObjs){
              /* Don't continue if there is a fatal error. */
              if(!ch.interlis.ili2c.parser.MetaObjectParser.parse (
                desc, e.getFilename())){
              	return null;
              }
            }
          }else{
            String streamName = e.getFilename();
            if(version==0.0){
            	version=ModelScan.getIliFileVersion(new File(streamName));
            }
            FileInputStream stream = null;
            try {
              stream = new FileInputStream(streamName);
            } catch (Exception ex) {
              EhiLogger.logError(ex);
              return null;
            }
			ch.ehi.basics.logging.ErrorTracker tracker=null;
			try{
				tracker=new ch.ehi.basics.logging.ErrorTracker();
				EhiLogger.getInstance().addListener(tracker);
				if(version==2.2){
					if (!Ili22Parser.parseIliFile (desc,streamName, stream, checkMetaObjs)){
					   return null;
					}
				}else{
					if (!Ili2Parser.parseIliFile (desc,streamName, stream, checkMetaObjs)){
					   return null;
					}
				}
				if(tracker.hasSeenErrors()){
					return null;
				}
			}catch(java.lang.Exception ex){
			  EhiLogger.logError(ex);
			  return null;
			}finally{
				if(tracker!=null){
					EhiLogger.getInstance().removeListener(tracker);
					tracker=null;
				}
				try{
				  stream.close();
				}catch(java.io.IOException ex){
				  EhiLogger.logError(ex);
				}
			}
          }
        }

        // output options
        BufferedWriter out=null;
        switch(config.getOutputKind()){
          case GenerateOutputKind.NOOUTPUT:
            break;
          case GenerateOutputKind.ILI1:
            if("-".equals(config.getOutputFile())){
              out=new BufferedWriter(new OutputStreamWriter(System.out));;
            }else{
              try{
                out = new BufferedWriter(new FileWriter(config.getOutputFile()));
              }catch(IOException ex){
                EhiLogger.logError(ex);
                return desc;
              }
            }
            ch.interlis.ili2c.generator.Interlis1Generator.generate(
              out, desc);
            break;
          case GenerateOutputKind.ILI2:
            if("-".equals(config.getOutputFile())){
              out=new BufferedWriter(new OutputStreamWriter(System.out));;
            }else{
              try{
                out = new BufferedWriter(new FileWriter(config.getOutputFile()));
              }catch(IOException ex){
                EhiLogger.logError(ex);
                return desc;
              }
            }
			ch.interlis.ili2c.generator.Interlis2Generator gen=new ch.interlis.ili2c.generator.Interlis2Generator();
           gen.generate(
              out, desc, emitPredefined);
            break;
          case GenerateOutputKind.XMLSCHEMA:
            if("-".equals(config.getOutputFile())){
              out=new BufferedWriter(new OutputStreamWriter(System.out));;
            }else{
              try{
                out = new BufferedWriter(new FileWriter(config.getOutputFile()));
              }catch(IOException ex){
                  EhiLogger.logError(ex);
                  return desc;
              }
            }
			if(desc.getLastModel().getIliVersion().equals("2.2")){
				ch.interlis.ili2c.generator.XSD22Generator.generate (
				  out, desc);
			}else{
				ch.interlis.ili2c.generator.XSDGenerator.generate (
				  out, desc);
			}
            break;
          case GenerateOutputKind.ILI1FMTDESC:
            if("-".equals(config.getOutputFile())){
              out=new BufferedWriter(new OutputStreamWriter(System.out));;
            }else{
              try{
                out = new BufferedWriter(new FileWriter(config.getOutputFile()));
              }catch(IOException ex){
                EhiLogger.logError(ex);
                return desc;
              }
            }
            ch.interlis.ili2c.generator.Interlis1Generator.generateFmt(out, desc);
            break;
		  case GenerateOutputKind.GML32:
			  ch.interlis.ili2c.generator.Gml32Generator.generate(desc, config.getOutputFile());
			  break;
		  case GenerateOutputKind.IOM:
				  if("-".equals(config.getOutputFile())){
					out=new BufferedWriter(new OutputStreamWriter(System.out));;
				  }else{
					try{
					  out = new BufferedWriter(new FileWriter(config.getOutputFile()));
					}catch(IOException ex){
					  EhiLogger.logError(ex);
					  return desc;
					}
				  }
				ch.interlis.ili2c.generator.iom.IomGenerator.generate(out,desc);
				break;
          default:
            // ignore
            break;
        }
        if(out!=null){
          try{
            out.close();
          }catch(java.io.IOException ex){
            EhiLogger.logError(ex);
          }
        }
        return desc;
  }
  static public boolean editConfig(Configuration config){
	ch.interlis.ili2c.gui.Main dialog=new ch.interlis.ili2c.gui.Main();
        return dialog.showDialog();
  }
  public static String getVersion() {
        if(version==null){
	  java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle("ch/interlis/ili2c/Version");
          // Major version numbers identify significant functional changes.
          // Minor version numbers identify smaller extensions to the functionality.
          // Micro versions are even finer grained versions.
          StringBuffer ret=new StringBuffer(20);
	  ret.append(resVersion.getString("versionMajor"));
          ret.append('.');
	  ret.append(resVersion.getString("versionMinor"));
          ret.append('.');
	  ret.append(resVersion.getString("versionMicro"));
          ret.append('-');
	  ret.append(resVersion.getString("versionDate"));
          version=ret.toString();
        }
        return version;
  }
  /** compiles a set of ili models.
   */
  static public TransferDescription compileIliModels(ArrayList modelv,ArrayList modeldirv,String ilxFile){
	  ch.interlis.ili2c.config.Configuration config=ch.interlis.ili2c.ModelScan.getConfig(modeldirv,modelv);
	  if(config==null){
		  return null;
	  }
	  config.setGenerateWarnings(false);
	  logIliFiles(config);
	  if(ilxFile!=null){
		  config.setOutputKind(ch.interlis.ili2c.config.GenerateOutputKind.IOM);
		  config.setOutputFile(ilxFile);
	  }else{
		  config.setOutputKind(ch.interlis.ili2c.config.GenerateOutputKind.NOOUTPUT);
	  }
	  TransferDescription ret=ch.interlis.ili2c.Main.runCompiler(config);
	  return ret;
  }
  /** compiles a set of ili files.
   */
  static public TransferDescription compileIliFiles(ArrayList filev,ArrayList modeldirv,String ilxFile){

	  ch.interlis.ili2c.config.Configuration config=ch.interlis.ili2c.ModelScan.getConfigWithFiles(modeldirv,filev);
	  if(config==null){
		  return null;
	  }
	  logIliFiles(config);
	  config.setGenerateWarnings(false);
	  if(ilxFile!=null){
		  config.setOutputKind(ch.interlis.ili2c.config.GenerateOutputKind.IOM);
		  config.setOutputFile(ilxFile);
	  }else{
		  config.setOutputKind(ch.interlis.ili2c.config.GenerateOutputKind.NOOUTPUT);
	  }
	  TransferDescription ret=ch.interlis.ili2c.Main.runCompiler(config);
	  return ret;
  }
  static public void logIliFiles(ch.interlis.ili2c.config.Configuration config)
  {
	  java.util.Iterator filei=config.iteratorFileEntry();
	  while(filei.hasNext()){
		  ch.interlis.ili2c.config.FileEntry file=(ch.interlis.ili2c.config.FileEntry)filei.next();
		  EhiLogger.logState("ilifile <"+file.getFilename()+">");
	  }
  }
}

