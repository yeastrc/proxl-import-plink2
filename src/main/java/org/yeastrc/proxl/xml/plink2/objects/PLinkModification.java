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

import java.util.Collection;

/**
 * Represents a plink modification as defined by the syntax in the modify.ini file
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkModification {
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMonoisotopicMass() {
		return monoisotopicMass;
	}
	public void setMonoisotopicMass(double monoisotopicMass) {
		this.monoisotopicMass = monoisotopicMass;
	}
	public double getAverageMass() {
		return averageMass;
	}
	public void setAverageMass(double averageMass) {
		this.averageMass = averageMass;
	}
	public boolean isMonolink() {
		return isMonolink;
	}
	public void setMonolink(boolean isMonolink) {
		this.isMonolink = isMonolink;
	}
	public Collection<String> getResidues() {
		return residues;
	}
	public void setResidues(Collection<String> residues) {
		this.residues = residues;
	}



	private String name;
	private double monoisotopicMass;
	private double averageMass;
	private boolean isMonolink = false;
	private Collection<String> residues;
}
