/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2016-2018 University of Washington - Seattle, WA
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

package org.yeastrc.proxl.xml.plink2.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import org.yeastrc.proxl.xml.plink2.objects.PLinkResult;
import org.yeastrc.proxl.xml.plink2.utils.PLinkUtils;

/**
 * Get the results of the supplied type based on the supplied parameters
 * @author mriffle
 *
 */
public class PLinkResultsLoader {

	private static PLinkResultsLoader _INSTANCE = new PLinkResultsLoader();
	public static PLinkResultsLoader getInstance() { return _INSTANCE; }
	
	private PLinkResultsLoader() { }
	
	/**
	 * Read and return all results from the plink analysis for the given type of peptide
	 * @param params The params file for the search
	 * @param dataDirectory Optional, the directory in which the data live. If null, the value from the plink.ini file will be used
	 * @param type The type of the peptide (e.g. PLinkConstants.LINK_TYPE_CROSSLINK)
	 * @return All of the corresponding results
	 * @throws Exception
	 */
	private Collection<PLinkResult> getResults( PLinkSearchParameters params, String dataDirectory, int type ) throws Exception {
		Collection<PLinkResult> results = new ArrayList<PLinkResult>();
		
		if( dataDirectory == null )
			dataDirectory = PLinkUtils.getOutputDirectory( params );
		
		File fullDataDirectory = new File( dataDirectory, PLinkConstants.DATA_SUBDIRECTORY );
		if( !fullDataDirectory.exists() )
			throw new Exception( "can not find data directory: " + fullDataDirectory );
		
		String filename = PLinkUtils.getSearchTitle( params );
		filename += ".filtered_" + PLinkConstants.PLINK_NAME_FOR_TYPE.get( type ) + "_";
		filename += "spectra.csv";
		
		File dataFile = new File( fullDataDirectory, filename );
		if( !dataFile.exists() )
			throw new FileNotFoundException( "can not find data file: " + dataFile );
		
		PLinkResultsFileReader plReader = null;
		
		try {
			plReader = PLinkResultsFileReader.getPLinkResultsFileReader( dataFile, type, params );
			
			PLinkResult result = plReader.readNextResult();
		
			while( result != null ) {
				results.add( result );
				result = plReader.readNextResult();
			}

		} finally {
			if( plReader != null )
				plReader.close();
		}
				
		return results;
	}
	
	/**
	 * Read and return all results from the plink analysis
	 * @param params The params file for the search
	 * @param dataDirectory Optional, the directory in which the data live. If null, the value from the plink.ini file will be used
	 * @return All of the corresponding results
	 * @throws Exception
	 * @param params
	 * @param dataDirectory
	 * @return
	 * @throws Exception
	 */
	public Collection<PLinkResult> getAllResults( PLinkSearchParameters params, String dataDirectory ) throws Exception {
		Collection<PLinkResult> results = new ArrayList<PLinkResult>();
		
		int TYPE = PLinkConstants.LINK_TYPE_CROSSLINK;
		try {
			results.addAll( this.getResults( params, dataDirectory, TYPE ) );
		} catch (FileNotFoundException e) {
			System.err.println( "Warning: Could not load " + PLinkConstants.PLINK_NAME_FOR_TYPE.get( TYPE ) + " data." );
			System.err.println( "Reason: " + e.getMessage() );
			System.err.println( "Skipping." );
		}
		
		TYPE = PLinkConstants.LINK_TYPE_LOOPLINK;
		try {
			results.addAll( this.getResults( params, dataDirectory, TYPE ) );
		} catch (FileNotFoundException e) {
			System.err.println( "Warning: Could not load " + PLinkConstants.PLINK_NAME_FOR_TYPE.get( TYPE ) + " data." );
			System.err.println( "Reason: " + e.getMessage() );
			System.err.println( "Skipping." );
		}
		
		TYPE = PLinkConstants.LINK_TYPE_MONOLINK;
		try {
			results.addAll( this.getResults( params, dataDirectory, TYPE ) );
		} catch (FileNotFoundException e) {
			System.err.println( "Warning: Could not load " + PLinkConstants.PLINK_NAME_FOR_TYPE.get( TYPE ) + " data." );
			System.err.println( "Reason: " + e.getMessage() );
			System.err.println( "Skipping." );
		}
		
		TYPE = PLinkConstants.LINK_TYPE_UNLINKED;
		try {
			results.addAll( this.getResults( params, dataDirectory, TYPE ) );
		} catch (FileNotFoundException e) {
			System.err.println( "Warning: Could not load " + PLinkConstants.PLINK_NAME_FOR_TYPE.get( TYPE ) + " data." );
			System.err.println( "Reason: " + e.getMessage() );
			System.err.println( "Skipping." );
		}

		if( results.size() == 0 ) {
			throw new Exception( "Could not find any data at location specified...\nLocation: " + PLinkUtils.getOutputDirectory( params ) );
		}
		
		return results;
	}
	
}
