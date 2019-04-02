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

package org.yeastrc.proxl.xml.plink2.utils;

import org.yeastrc.proxl.xml.plink2.objects.PLinkLinker;

public class PLinkLinkerUtils {

	/**
	 * Get a PLinkLinker object populated according to the data presented in the space delimited
	 * text associated with a linker, as found in the xlink.ini file
	 *
	 * @param pLinkLinkerTextFormat
	 * @return
	 * @throws Exception
	 */
	public static PLinkLinker getPLinkLinker( String name, String pLinkLinkerTextFormat ) throws Exception {
		PLinkLinker linker = new PLinkLinker();
		
		String[] fields = pLinkLinkerTextFormat.split( " " );
		if( fields.length != 10 )
			throw new Exception( "Did not get ten fields in linker representation: " + pLinkLinkerTextFormat );
		
		linker.setName( name );
		linker.setFirstLinkedResidueMotif( fields[ 0 ] );
		linker.setSecondLinkedResidueMotif( fields[ 1 ] );
		linker.setMonoCrosslinkMass( Double.valueOf( fields[ 2 ] ) );

		if( !fields[ 3 ].equals( "" ) && !fields[ 3 ].equals( "0" ) )
			linker.setAverageCrosslinkMass( Double.valueOf( fields[ 3 ] ) );
		
		if( !fields[ 4 ].equals( "" ) && !fields[ 4 ].equals( "0" ) )
			linker.setMonoMonolinkMass( Double.valueOf( fields[ 4 ] ) );

		if( !fields[ 5 ].equals( "" ) && !fields[ 5 ].equals( "0" ) )
			linker.setAverageMonolinkMass( Double.valueOf( fields[ 5 ] ) );
		
		return linker;
	}
	
}
