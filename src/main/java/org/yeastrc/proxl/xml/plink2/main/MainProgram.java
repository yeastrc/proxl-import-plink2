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

import picocli.CommandLine;

@CommandLine.Command(name = "java -jar " + PLinkConverterConstants.CONVERSION_PROGRAM_NAME,
		mixinStandardHelpOptions = true,
		version = PLinkConverterConstants.CONVERSION_PROGRAM_NAME + " " + PLinkConverterConstants.CONVERSION_PROGRAM_VERSION,
		sortOptions = false,
		synopsisHeading = "%n",
		descriptionHeading = "%n@|bold,underline Description:|@%n%n",
		optionListHeading = "%n@|bold,underline Options:|@%n",
		description = "Convert the results of a pLink 2.x analysis to a ProXL XML file suitable for import into ProXL. " +
				"Designed to work only with single, label-free searches that used a single cross-linker.",
		footer = {
				"",
				"@|bold,underline Examples|@:",
				"java -jar " + PLinkConverterConstants.CONVERSION_PROGRAM_NAME + " ^\n" +
						"-p \"c:\\plink run\\run_name.plink\" ^\n" +
						"-o \"c:\\out put\\mySearch.proxl.xml\" ^\n" +
						"-f c:\\fastas\\myFasta.fasta",
				"",
				"java -jar " + PLinkConverterConstants.CONVERSION_PROGRAM_NAME + " ^\n" +
						" -p \"C:\\Users\\PLink User\\Desktop\\gTuSC\\pLink_test.plink\" ^\n" +
						" -o C:\\Users\\User\\Desktop\\gTuSC.proxl.xml ^\n" +
						" -f C:\\fastas\\myFasta.fasta ^\n" +
						" -b C:\\pFindStudio\\pLink\\2.3.0\\bin ^\n" +
						" -r C:\\Users\\User\\Desktop\\gTuSC\\pLink_test\\reports",
				""
		}
)
public class MainProgram implements Runnable {

	@CommandLine.Option(names = { "-p", "--param" }, required = true, description = "[Required] Full path to pLink 2 " +
			"parameters file used in the search (ends in .plink).")
	private String paramFile;

	@CommandLine.Option(names = { "-o", "--out" }, required = true, description = "[Required] Full path to use for the " +
			"ProXL XML output file (including file name).")
	private String outFile;

	@CommandLine.Option(names = { "-f", "--fasta" }, required = true, description = "[Required] Full path to FASTA file " +
			"used in the experiment.")
	private String fastaFile;

	@CommandLine.Option(names = { "-b", "--bin" }, description = "[Optional] Full path to the pLink installation directory, " +
			"where modification.ini and xlink.ini may be found. This defaults to: C:\\pFindStudio\\pLink\\$VERSION\\bin " +
			"Where $VERSION is found in the parameters file.")
	private String binDirectory;

	@CommandLine.Option(names = { "-r", "--reports" }, description = "[Optional] Full path to the data reports " +
			"directory for pLink 2 results. This directory contains run_name.date.csv " +
			"(e.g. my_plink_search_2018.02.25.csv) If not present, value from the parameters file will be used.")
	private String dataDirectory;

	@CommandLine.Option(names = { "-l", "--linker" }, description = "[Optional] Specify the name of the cross-linker, " +
			"e.g., edc or dss" )
	private String linker;


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

	public void run()  {

		printRuntimeInfo();

		checkFileFromArgsExists( paramFile, "p", "plink params" );

		checkFileFromArgsExists(fastaFile, "f", "FASTA" );

		if( binDirectory != null ) {
			checkDirectoryFromArgsExists(binDirectory, "b", "plink install");
		}

		if( dataDirectory != null ) {
			checkDirectoryFromArgsExists(dataDirectory, "r", "plink data");
		}

		MainProgram mp = new MainProgram();

		try {
			mp.convertSearch( paramFile, binDirectory, dataDirectory, outFile, fastaFile, linker );
		} catch( Throwable t ) {
			System.err.println( "\n\nEncountered an error during conversion:" );
			System.err.println( t.getMessage() );
			System.exit( 1 );
		}

	}
	
	public static void main( String[] args ) {

		CommandLine.run(new MainProgram(), args);

	}
	
	
	private static void checkDirectoryFromArgsExists( String filePath, String param, String name ) {

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
	
	private static void checkFileFromArgsExists( String filePath, String param, String name ) {

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
	
	/**
	 * Print runtime info to STD ERR
	 * @throws Exception 
	 */
	public static void printRuntimeInfo()  {

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
		}
	}

}
