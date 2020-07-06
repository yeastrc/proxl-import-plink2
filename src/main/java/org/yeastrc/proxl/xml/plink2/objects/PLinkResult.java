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

/**
 * A PSM result as parsed from the plink results file.
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class PLinkResult {
	
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public PLinkReportedPeptide getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(PLinkReportedPeptide reportedPEptide) {
		this.reportedPeptide = reportedPEptide;
	}
	public double getEvalue() {
		return evalue;
	}
	public void setEvalue(double evalue) {
		this.evalue = evalue;
	}
	public double getCalculatedMass() {
		return calculatedMass;
	}
	public void setCalculatedMass(double calculatedMass) {
		this.calculatedMass = calculatedMass;
	}
	public double getDeltaMass() {
		return deltaMass;
	}
	public void setDeltaMass(double deltaMass) {
		this.deltaMass = deltaMass;
	}
	public double getDeltaMassPPM() {
		return deltaMassPPM;
	}
	public void setDeltaMassPPM(double deltaMassPPM) {
		this.deltaMassPPM = deltaMassPPM;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the precursorMass
	 */
	public double getPrecursorMass() {
		return precursorMass;
	}
	/**
	 * @param precursorMass the precursorMass to set
	 */
	public void setPrecursorMass(double precursorMass) {
		this.precursorMass = precursorMass;
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}
	/**
	 * @return the alphaMatched
	 */
	public double getAlphaMatched() {
		return alphaMatched;
	}
	/**
	 * @param alphaMatched the alphaMatched to set
	 */
	public void setAlphaMatched(double alphaMatched) {
		this.alphaMatched = alphaMatched;
	}
	/**
	 * @return the betaMatched
	 */
	public double getBetaMatched() {
		return betaMatched;
	}
	/**
	 * @param betaMatched the betaMatched to set
	 */
	public void setBetaMatched(double betaMatched) {
		this.betaMatched = betaMatched;
	}
	/**
	 * @return the alphaEValue
	 */
	public double getAlphaEValue() {
		return alphaEValue;
	}
	/**
	 * @param alphaEValue the alphaEValue to set
	 */
	public void setAlphaEValue(double alphaEValue) {
		this.alphaEValue = alphaEValue;
	}
	/**
	 * @return the betaEValue
	 */
	public double getBetaEValue() {
		return betaEValue;
	}
	/**
	 * @param betaEValue the betaEValue to set
	 */
	public void setBetaEValue(double betaEValue) {
		this.betaEValue = betaEValue;
	}

	public String getScanFilePrefix() {
		return scanFilePrefix;
	}

	public void setScanFilePrefix(String scanFilePrefix) {
		this.scanFilePrefix = scanFilePrefix;
	}

	private PLinkReportedPeptide reportedPeptide;

	private int scanNumber;
	private int charge;
	private String scanFilePrefix;

	private double precursorMass;
	
	
	private double evalue;		// the score reported by plink
	private double score;
	
	private double alphaMatched;
	private double betaMatched;
	private double alphaEValue;
	private double betaEValue;
	
	private double calculatedMass;
	private double deltaMass;
	private double deltaMassPPM;
	private int type;
	
}
