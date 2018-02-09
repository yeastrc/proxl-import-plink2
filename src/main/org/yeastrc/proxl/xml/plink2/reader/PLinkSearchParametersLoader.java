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

import org.yeastrc.proxl.xml.plink2.ini.ParsedINIFile;
import org.yeastrc.proxl.xml.plink2.objects.PLinkLinker;
import org.yeastrc.proxl.xml.plink2.utils.PLinkLinkerUtils;

public class PLinkSearchParametersLoader {
	
	public static PLinkSearchParametersLoader getInstance() { return _INSTANCE; }
	private static final PLinkSearchParametersLoader _INSTANCE = new PLinkSearchParametersLoader();
	private PLinkSearchParametersLoader() { }
	
	/**
	 * Load the plink search parameters. Also parses, verifies, and populates the linker used in this experiment.
	 * 
	 * @param pLinkINIFilename The full path to the pLink.ini file used in the experiment.
	 * @param pLinkBinDirectory (Optional) The full path the the bin directory for plink. If absent, value from the pLink.ini file is used.
	 * @return
	 * @throws Exception
	 */
	public PLinkSearchParameters getPLinkSearch( String pLinkINIFilename, String pLinkBinDirectory ) throws Exception {

		PLinkSearchParameters plinkSearch = new PLinkSearchParameters();

		// load and add the plink.ini to the search
		plinkSearch.setPlinkINI( new ParsedINIFile( pLinkINIFilename) );
		
		String plinkPath = null;

		// if no bin directory was supplied, attempt to find it in the expected location
		if( pLinkBinDirectory == null ) {
			String version = plinkSearch.getPlinkINI().getConfig().getString( "version/version" );
			if( version == null )
				throw new Exception( "Could not find version number in " + pLinkINIFilename );
			
			String directory = PLinkConstants._DEFAULT_PLINK_INSTALL_BIN_DIRECTORY;
			directory = directory.replace( "$$VERSION$$", version );
			plinkPath = directory;
		}
		else
			plinkPath = pLinkBinDirectory;
		
		// load and add the modify.ini
		plinkSearch.setModifyINI( new ParsedINIFile( new File(plinkPath, PLinkConstants.MODIFY_INI_FILENAME ) ) );
		
		// load and add the xlink.ini
		plinkSearch.setXlinkINI( new ParsedINIFile( new File(plinkPath, PLinkConstants.XLINK_INI_FILENAME ) ) );

		// load the referenced linker
						
		int linkerCount = plinkSearch.getPlinkINI().getConfig().getInt( "linker/linker_total" );
		
		if( linkerCount > 2 )
			throw new Exception( "Can not currently import data with multiple cross-linkers." );
		
		String linkerName = plinkSearch.getPlinkINI().getConfig().getString( "linker/linker1" );

		if( linkerName == null )
			throw new Exception( "Could not find linker name in plink parameters file for linker 1" );

		String linkerDefinition = plinkSearch.getXlinkINI().getConfig().getString( "xlink/" + linkerName );
			
		if( linkerDefinition == null )
			throw new Exception( "Could not find linker: \"" + linkerName + "\" in xlink.ini file." );

		PLinkLinker linker = PLinkLinkerUtils.getPLinkLinker( linkerName,  linkerDefinition );

		// since we only support 1 link, just set the linker to the first linker found
		plinkSearch.setLinker( linker );
		
		// ensure proxl supports this linker
		if( !PLinkConstants.LINKER_MAP_PLINK2PROXL.containsKey( plinkSearch.getLinker().getName() ) ) {
			throw new Exception( "Proxl does not currently support linker: " + plinkSearch.getLinker().getName() );
		}
		
		return plinkSearch;
	}
	
}
