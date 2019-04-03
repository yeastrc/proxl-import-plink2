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

import java.util.List;

/**
 * Represents a pLink cross-linker, as defined by the syntax in modify.ini
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkLinker {

	@Override
	public String toString() {
		return "PLinkLinker{" +
				"name='" + name + '\'' +
				", monoCrosslinkMass=" + monoCrosslinkMass +
				", monoMonolinkMass=" + monoMonolinkMass +
				", averageMonolinkMass=" + averageMonolinkMass +
				", linkerEnds=" + linkerEnds +
				", cleavedLinkerMasses=" + cleavedLinkerMasses +
				", formula='" + formula + '\'' +
				'}';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMonoCrosslinkMass() {
		return monoCrosslinkMass;
	}

	public void setMonoCrosslinkMass(Double monoCrosslinkMass) {
		this.monoCrosslinkMass = monoCrosslinkMass;
	}

	public Double getMonoMonolinkMass() {
		return monoMonolinkMass;
	}

	public void setMonoMonolinkMass(Double monoMonolinkMass) {
		this.monoMonolinkMass = monoMonolinkMass;
	}

	public Double getAverageMonolinkMass() {
		return averageMonolinkMass;
	}

	public void setAverageMonolinkMass(Double averageMonolinkMass) {
		this.averageMonolinkMass = averageMonolinkMass;
	}

	public List<PLinkLinkerEnd> getLinkerEnds() {
		return linkerEnds;
	}

	public void setLinkerEnds(List<PLinkLinkerEnd> linkerEnds) {
		this.linkerEnds = linkerEnds;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public List<Double> getCleavedLinkerMasses() {
		return cleavedLinkerMasses;
	}

	public void setCleavedLinkerMasses(List<Double> cleavedLinkerMasses) {
		this.cleavedLinkerMasses = cleavedLinkerMasses;
	}

	private String name;
	private Double monoCrosslinkMass;
	private Double monoMonolinkMass;
	private Double averageMonolinkMass;
	private List<PLinkLinkerEnd> linkerEnds;
	private List<Double> cleavedLinkerMasses;
	private String formula;
	
}
