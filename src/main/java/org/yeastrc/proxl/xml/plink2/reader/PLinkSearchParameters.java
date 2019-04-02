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

import org.yeastrc.proxl.xml.plink2.ini.ParsedINIFile;
import org.yeastrc.proxl.xml.plink2.objects.PLinkLinker;

/**
 * Access to the various INI files that define the search parameters of a plink search.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkSearchParameters {
	
	public ParsedINIFile getPlinkINI() {
		return plinkINI;
	}
	public void setPlinkINI(ParsedINIFile plinkINI) {
		this.plinkINI = plinkINI;
	}
	public ParsedINIFile getModifyINI() {
		return modifyINI;
	}
	public void setModifyINI(ParsedINIFile modifyINI) {
		this.modifyINI = modifyINI;
	}
	public ParsedINIFile getXlinkINI() {
		return xlinkINI;
	}
	public void setXlinkINI(ParsedINIFile xlinkINI) {
		this.xlinkINI = xlinkINI;
	}
	
	
	
	public PLinkLinker getLinker() {
		return linker;
	}
	public void setLinker(PLinkLinker linker) {
		this.linker = linker;
	}



	private PLinkLinker linker;
	
	private ParsedINIFile plinkINI;
	private ParsedINIFile modifyINI;
	private ParsedINIFile xlinkINI;
	
}
