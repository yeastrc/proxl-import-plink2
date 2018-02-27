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
		
		if( args.length < 1 || args[ 0 ].equals( "-h" ) ) {
			printHelp();
			System.exit( 0 );
		}
		
		CmdLineParser cmdLineParser = new CmdLineParser();
        
		CmdLineParser.Option plinkParamOpt = cmdLineParser.addStringOption( 'p', "param" );	
		CmdLineParser.Option outfileOpt = cmdLineParser.addStringOption( 'o', "out" );	
		CmdLineParser.Option installDirectoryOpt = cmdLineParser.addStringOption( 'b', "binary" );	
		CmdLineParser.Option dataDirectoryOpt = cmdLineParser.addStringOption( 'r', "reports" );
		CmdLineParser.Option fastaFileOpt = cmdLineParser.addStringOption( 'f', "fasta" );
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
        String outFile = (String)cmdLineParser.getOptionValue( outfileOpt );
        String binDirectory = (String)cmdLineParser.getOptionValue( installDirectoryOpt );
        String dataDirectory = (String)cmdLineParser.getOptionValue( dataDirectoryOpt );
        String fastaFilePath = (String)cmdLineParser.getOptionValue( fastaFileOpt );
        String linker = (String)cmdLineParser.getOptionValue( linkerOpt );
        
        MainProgram mp = new MainProgram();
        mp.convertSearch( paramFile, binDirectory, dataDirectory, outFile, fastaFilePath, linker );
        
	}
	
	/**
	 * Print help to STD OUT
	 */
	public static void printHelp() {
		
		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "help.txt" ) ) ) ) {
			
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				
			
		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}
	}
}
