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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PLinkConstants {

	public static final String SEARCH_PROGRAM_NAME = "pLink";	
	
	public static final String SEARCH_PROGRAM_VERSION = "2.x";
	
	public static final String _DEFAULT_PLINK_INSTALL_BIN_DIRECTORY = "C:\\pFindStudio\\pLink\\$$VERSION$$\\bin";
	
	public static final int LINK_TYPE_MONOLINK = 0;
	public static final int LINK_TYPE_CROSSLINK = 1;
	public static final int LINK_TYPE_LOOPLINK = 2;
	public static final int LINK_TYPE_UNLINKED = 3;
	
	public static final String MODIFY_INI_FILENAME = "modification.ini";
	public static final String XLINK_INI_FILENAME = "xlink.ini";
	
	public static final String DATA_SUBDIRECTORY = "reports";
	
	/**
	 * The string with which plink refers to the types of linked peptides, used
	 * to find subdirectories for data
	 */
	public static final Map<Integer, String> PLINK_NAME_FOR_TYPE;
	
	/**
	 * The crosslinkers that proxl currently knows how to deal with (e.g., linking rules)
	 */
	public static final Map<String, String> LINKER_MAP_PLINK2PROXL;
	
	static {
        Map<String, String> tempLinkerMap = new HashMap<String, String>();
        
        tempLinkerMap.put( "BS3_STY", "bs3.sty" );
        tempLinkerMap.put( "BS3", "bs3" );
        tempLinkerMap.put( "DSS_STY",  "dss.sty" );
        tempLinkerMap.put( "DSS",  "dss" );
        tempLinkerMap.put( "DSSO",  "dsso" );
        tempLinkerMap.put( "EDC-DE",  "edc" );
        tempLinkerMap.put( "EDC",  "edc" );
        tempLinkerMap.put( "BS2G",  "bs2" );
        tempLinkerMap.put( "TG", "tg" );
        
        LINKER_MAP_PLINK2PROXL = Collections.unmodifiableMap(tempLinkerMap);
        
        
        
        
        Map<Integer, String> tempLinkTypeMap = new HashMap<Integer, String>();
        tempLinkTypeMap.put( LINK_TYPE_MONOLINK, "mono-linked" );
        tempLinkTypeMap.put( LINK_TYPE_CROSSLINK, "cross-linked" );
        tempLinkTypeMap.put( LINK_TYPE_LOOPLINK, "loop-linked" );
        tempLinkTypeMap.put( LINK_TYPE_UNLINKED, "regular" );
        
        PLINK_NAME_FOR_TYPE = Collections.unmodifiableMap( tempLinkTypeMap );
	}
	
	
}
