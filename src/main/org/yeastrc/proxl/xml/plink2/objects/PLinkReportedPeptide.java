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

import org.yeastrc.proxl.xml.plink2.reader.PLinkConstants;

/**
 * Represents a "reported peptide" as reported by the plink results files. A reported peptide is
 * the unique combination of peptide sequence(s), modifications, and linked positions. Contains
 * the parsed peptide(s), linked positions in those peptides, and all mods on each peptide.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkReportedPeptide {
	
	@Override
	public boolean equals( Object o ) {
		
		if( !( o instanceof PLinkReportedPeptide) )
			return false;
		
		return this.toString().equals( ((PLinkReportedPeptide)o).toString() );
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	/**
	 * A string representation of this reported peptide in the format of:
	 * PEP[12.321]TIDE (for unlinked peptides)
	 * PEP[156.2340]TIDE (for monolinked peptides--proxl treats monolinks as dynamic mods)
	 * PEP[12.2123]TIDE(1,3) (for looplinked peptides)
	 * PEP[12.2123]TIDE(1)-PEPTIDE (for crosslinked peptides)
	 */
	@Override
	public String toString() {

		if( this.getType() == PLinkConstants.LINK_TYPE_UNLINKED ) {
			return this.getPeptide1().toString();
		} else if( this.getType() == PLinkConstants.LINK_TYPE_MONOLINK ) {
			return this.getPeptide1().toString() + "(" + this.getPosition1() + ")";
		} else if( this.getType() == PLinkConstants.LINK_TYPE_LOOPLINK ) {
			return this.getPeptide1().toString() + "(" + this.getPosition1() + "," + this.getPosition2() + ")";
		} else if( this.getType() == PLinkConstants.LINK_TYPE_CROSSLINK ) {
			return this.getPeptide1().toString() + "(" + this.getPosition1() + ")" + "-" +
        		   this.getPeptide2().toString() + "(" + this.getPosition2() + ")";
		}
		
		return "Error: unknown peptide type";
		
	}
	
	public PLinkPeptide getPeptide1() {
		return peptide1;
	}

	public void setPeptide1(PLinkPeptide peptide1) {
		this.peptide1 = peptide1;
	}

	public PLinkPeptide getPeptide2() {
		return peptide2;
	}

	public void setPeptide2(PLinkPeptide peptide2) {
		this.peptide2 = peptide2;
	}

	public int getPosition1() {
		return position1;
	}

	public void setPosition1(int position1) {
		this.position1 = position1;
	}

	public int getPosition2() {
		return position2;
	}

	public void setPosition2(int position2) {
		this.position2 = position2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private PLinkPeptide peptide1;
	private PLinkPeptide peptide2;
	
	private int position1;
	private int position2;
	
	private int type;
	
}
