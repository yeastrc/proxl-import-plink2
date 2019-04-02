/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.proxl.xml.plink2.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;

import org.yeastrc.proxl.xml.plink2.builder.XMLBuilder;
import org.yeastrc.proxl.xml.plink2.constants.PLinkConverterConstants;
import org.yeastrc.proxl.xml.plink2.objects.PLinkResult;
import org.yeastrc.proxl.xml.plink2.reader.PLinkResultsLoader;
import org.yeastrc.proxl.xml.plink2.reader.PLinkSearchParameters;
import org.yeastrc.proxl.xml.plink2.reader.PLinkSearchParametersLoader;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

public class MainProgram {

	
	public void convertSearch( String plinkSearchParametersFile, String plinkBinDirectory, String plinkDataDirectory, String outfile, String fastaFilePath, String linkerOverride ) throws Exception {
		
		System.err.print( "Loading search parameters... " );
		PLinkSearchParameters params = PLinkSearchParametersLoader.getInstance().getPLinkSearch( plinkSearchParametersFile, plinkBinDirectory, linkerOverride );
		System.err.println( "Done." );
		
		System.err.print( "Loading search results... " );
		Collection<PLinkResult> results = PLinkResultsLoader.getInstance().getAllResults( params, plinkDataDirectory );
		System.err.println( "Done." );

		System.err.print( "Writing proxl XML... " );
		XMLBuilder builder = new XMLBuilder();
		builder.buildAndSaveXML(params, results, new File( outfile ), fastaFilePath );
		System.err.println( "Done." );
	}
	
	public static void main( String[] args ) throws Exception {
		
		printRuntimeInfo();
		
		if( args.length < 1 || args[ 0 ].equals( "-h" ) ) {
			printHelp();
			System.exit( 0 );
		}
		
		CmdLineParser cmdLineParser = new CmdLineParser();
		CmdLineParser.Option plinkParamOpt = cmdLineParser.addStringOption( 'p', "param" );	
		CmdLineParser.Option outfileOpt = cmdLineParser.addStringOption( 'o', "out" );
		CmdLineParser.Option fastaFileOpt = cmdLineParser.addStringOption( 'f', "fasta" );
		CmdLineParser.Option installDirectoryOpt = cmdLineParser.addStringOption( 'b', "binary" );	
		CmdLineParser.Option dataDirectoryOpt = cmdLineParser.addStringOption( 'r', "reports" );
		CmdLineParser.Option linkerOpt = cmdLineParser.addStringOption( 'l', "linker" );

        // parse command line options
        try { cmdLineParser.parse(args); }
        catch (IllegalOptionValueException e) {
        	printHelp();
            System.exit( 1 );
        }
        catch (UnknownOptionException e) {
           printHelp();
           System.exit( 1 );
        }
		
        String paramFile = (String)cmdLineParser.getOptionValue( plinkParamOpt );
        checkFileFromArgsExists( REQUIRED, paramFile, "p", "plink params" );
        
        String fastaFilePath = (String)cmdLineParser.getOptionValue( fastaFileOpt );
        checkFileFromArgsExists( REQUIRED, fastaFilePath, "f", "FASTA" );
        
        String outFile = (String)cmdLineParser.getOptionValue( outfileOpt );
        if( outFile == null ) {
        	System.err.println( "The -o option is required." );
        	System.err.println( "Run with the -h option for help." );
        	System.exit( 1 );
        }
        
        String binDirectory = (String)cmdLineParser.getOptionValue( installDirectoryOpt );
        checkDirectoryFromArgsExists( NOT_REQUIRED, binDirectory, "b", "plink install" );

        String dataDirectory = (String)cmdLineParser.getOptionValue( dataDirectoryOpt );
        checkDirectoryFromArgsExists( NOT_REQUIRED, dataDirectory, "r", "plink data" );
        
        String linker = (String)cmdLineParser.getOptionValue( linkerOpt );
        
        MainProgram mp = new MainProgram();
        
        try {
        	mp.convertSearch( paramFile, binDirectory, dataDirectory, outFile, fastaFilePath, linker );
        } catch( Throwable t ) {
        	System.err.println( "\n\nEncountered an error during conversion:" );
        	System.err.println( t.getMessage() );
        	System.exit( 1 );
        }
        
	}
	
	
	private static void checkDirectoryFromArgsExists( boolean required, String filePath, String param, String name ) {
		
        if( filePath == null ) {
        	
        	if( !required ) {
        		return;
        	}
        	
			System.err.println( "The -" + param + " parameter is required." );
        	System.err.println( "Run with the -h option for help." );
			System.exit( 0 );
        } else {
        	File file = new File( filePath );
            try {
	
	        	if( !file.exists() ) {
	        		System.err.println( "Could not find " + name + " directory: " + file.getAbsolutePath() );
	        		System.exit( 0 );
	        	}
	        	
	        	if( !file.isDirectory() ) {
	        		System.err.println( file.getAbsolutePath() + " does not point to a directory." );
	        		System.err.println( "Please provide the path to a directory for the -" + param + " parameter." );
	        		System.exit( 0 );
	        	}
	        	
            } catch( Exception e ) {
            	System.err.println( "Error accessing " + name + ". " );
            	System.err.println( "Error: " + e.getMessage() );
            	System.exit( 0 );
            }
        }
	}
	
	private static void checkFileFromArgsExists( boolean required, String filePath, String param, String name ) {
				
        if( filePath == null ) {
        	
        	if( !required ) {
        		return;
        	}
        	
			System.err.println( "The -" + param + " parameter is required." );
        	System.err.println( "Run with the -h option for help." );
			System.exit( 0 );
        } else {
        	File file = new File( filePath );
            try {
	
	        	if( !file.exists() ) {
	        		System.err.println( "Could not find " + name + " file: " + file.getAbsolutePath() );
	        		System.exit( 0 );
	        	}
	        	
	        	if( file.isDirectory() ) {
	        		System.err.println( file.getAbsolutePath() + " points to a directory." );
	        		System.err.println( "Please provide the path to a specific file for the -" + param + " parameter." );
	        		System.exit( 0 );
	        	}
	        	
	        	if( !file.canRead() ) {
	        		System.err.println( "Cannot read " + name + " file: " + file.getAbsolutePath() + ". Check permissions." );
	        		System.exit( 0 );
	        	}
	        	
            } catch( Exception e ) {
            	System.err.println( "Error accessing " + name + ". " );
            	System.err.println( "Error: " + e.getMessage() );
            	System.exit( 0 );
            }
        }
	}
	
	/**
	 * Print help to STD OUT
	 */
	public static void printHelp() {
		
		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "help.txt" ) ) ) ) {
			
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.err.println( line );				
			
		} catch ( Exception e ) {
			System.err.println( "Error printing help." );
		}
	}
	
	/**
	 * Print runtime info to STD ERR
	 * @throws Exception 
	 */
	public static void printRuntimeInfo() throws Exception {

		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "run.txt" ) ) ) ) {

			String line = null;
			while ( ( line = br.readLine() ) != null ) {

				line = line.replace( "{{URL}}", PLinkConverterConstants.CONVERSION_PROGRAM_URI );
				line = line.replace( "{{VERSION}}", PLinkConverterConstants.CONVERSION_PROGRAM_VERSION );

				System.err.println( line );
				
			}
			
			System.err.println( "" );

		} catch ( Exception e ) {
			System.out.println( "Error printing runtime information." );
			throw e;
		}
	}
	
	private static boolean REQUIRED = true;
	private static boolean NOT_REQUIRED = false;
}
