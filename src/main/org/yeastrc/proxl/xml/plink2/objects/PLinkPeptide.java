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

package org.yeastrc.proxl.xml.plink2.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proxl.xml.plink2.utils.NumberUtils;

/**
 * A peptide as parsed from the reported peptide string in the plink results file. This is a single peptide,
 * so a cross-linked pair of peptides will be presented by two of these.
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkPeptide {
	
	/**
	 * Get the string representation of this peptide that includes mods, in the form of:
	 * PEP[12.2932,15.993]TI[12.2932]DE
	 */
	@Override
	public String toString() {
		
		String str = "";
		
		for( int i = 1; i <= this.getSequence().length(); i++ ) {
			String r = String.valueOf( this.getSequence().charAt( i - 1 ) );
			str += r;
			
			if( this.getMods() != null ) {
				List<String> modsAtPosition = new ArrayList<String>();
				
				if( this.getMods().get( i ) != null ) {
					for( PLinkModification mod : this.getMods().get( i ) ) {
						modsAtPosition.add( NumberUtils.getRoundedBigDecimal( mod.getMonoisotopicMass() ).toString() );
					}
					
					if( modsAtPosition.size() > 0 ) {
	
						// sort these strings on double values
						Collections.sort( modsAtPosition, new Comparator<String>() {
						       public int compare(String s1, String s2) {
						           return Double.valueOf( s1 ).compareTo( Double.valueOf( s2 ) );
						        }
						});
						
						String modsString = StringUtils.join( modsAtPosition, "," );
						str += "[" + modsString + "]";
					}
				}
			}
		}
		
		return str;
	}
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public Map<Integer, Collection<PLinkModification>> getMods() {
		return mods;
	}
	public void setMods(Map<Integer, Collection<PLinkModification>> mods) {
		this.mods = mods;
	}
	
	private String sequence;
	private Map<Integer, Collection<PLinkModification>> mods;
	
}
