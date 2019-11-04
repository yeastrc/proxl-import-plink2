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

package org.yeastrc.proxl.xml.plink2.builder;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import org.yeastrc.fasta.FASTAEntry;
import org.yeastrc.fasta.FASTAHeader;
import org.yeastrc.fasta.FASTAReader;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.taxonomy.main.GetTaxonomyId;

/**
 * Build the MatchedProteins section of the ProXL XML docs. This is done by finding all proteins in the FASTA
 * file that contains any of the peptide sequences found in the experiment. 
 * 
 * This is generalized enough to be usable by any pipeline
 * 
 * @author mriffle
 *
 */
public class MatchedProteinsBuilder {

	public static MatchedProteinsBuilder getInstance() { return new MatchedProteinsBuilder(); }
	
	/**
	 * Add all target proteins from the FASTA file that contain any of the peptides found in the experiment
	 * to the proxl xml document in the matched proteins sectioni.
	 * 
	 * @param proxlInputRoot
	 * @param fastaFile
	 * @param decoyIdentifiers
	 * @throws Exception
	 */
	public void buildMatchedProteins( ProxlInput proxlInputRoot, File fastaFile, Collection<String> decoyIdentifiers ) throws Exception {
		
		// get all distinct peptides found in this search
		Collection<String> allPetpideSequences = getDistinctPeptides( proxlInputRoot );
		
		// the proteins we've found
		Map<String, Collection<FastaProteinAnnotation>> proteins = getProteins( allPetpideSequences, fastaFile, decoyIdentifiers );
		
		// create the XML and add to root element
		buildAndAddMatchedProteinsToXML( proxlInputRoot, proteins );
		
	}
	
	/**
	 * Do the work of building the matched peptides element and adding to proxl xml root
	 * 
	 * @param proxlInputRoot
	 * @param proteins
	 * @throws Exception
	 */
	private void buildAndAddMatchedProteinsToXML( ProxlInput proxlInputRoot, Map<String, Collection<FastaProteinAnnotation>> proteins ) throws Exception {
		
		MatchedProteins xmlMatchedProteins = new MatchedProteins();
		proxlInputRoot.setMatchedProteins( xmlMatchedProteins );
		
		for( String sequence : proteins.keySet() ) {
			
			if( proteins.get( sequence ).isEmpty() ) continue;
			
			Protein xmlProtein = new Protein();
        	xmlMatchedProteins.getProtein().add( xmlProtein );
        	
        	xmlProtein.setSequence( sequence );
        	        	
        	for( FastaProteinAnnotation anno : proteins.get( sequence ) ) {
        		ProteinAnnotation xmlProteinAnnotation = new ProteinAnnotation();
        		xmlProtein.getProteinAnnotation().add( xmlProteinAnnotation );
        		
        		xmlProteinAnnotation.setName( anno.getName() );
        		
        		if( anno.getDescription() != null )
        			xmlProteinAnnotation.setDescription( anno.getDescription() );
        			
        		if( anno.getTaxonomId() != null )
        			xmlProteinAnnotation.setNcbiTaxonomyId( new BigInteger( anno.getTaxonomId().toString() ) );
        	}
		}
	}
	

	/**
	 * Get a map of the distinct target protein sequences mapped to a collection of target annotations for that sequence
	 * from the given fasta file, where the sequence contains any of the supplied peptide sequences
	 * 
	 * @param allPetpideSequences
	 * @param fastaFile
	 * @param decoyIdentifiers
	 * @return
	 * @throws Exception
	 */
	private Map<String, Collection<FastaProteinAnnotation>> getProteins( Collection<String> allPetpideSequences, File fastaFile, Collection<String> decoyIdentifiers ) throws Exception {
		
		Map<String, Collection<FastaProteinAnnotation>> proteinAnnotations = new HashMap<>();
		
		FASTAReader fastaReader = null;
		
		try {
			
			fastaReader = FASTAReader.getInstance( fastaFile );
			
			for( FASTAEntry entry = fastaReader.readNext(); entry != null; entry = fastaReader.readNext() ) {

				if( isDecoyFastaEntry( entry, decoyIdentifiers ) )
					continue;
				
				for( FASTAHeader header : entry.getHeaders() ) {
					
					if( !proteinAnnotations.containsKey( entry.getSequence() ) )
						proteinAnnotations.put( entry.getSequence(), new HashSet<FastaProteinAnnotation>() );
					
					FastaProteinAnnotation anno = new FastaProteinAnnotation();
					anno.setName( header.getName() );
					anno.setDescription( header.getDescription() );
					
            		Integer taxId = GetTaxonomyId.getInstance().getTaxonomyId( header.getName(), header.getDescription() );
            		if( taxId != null )
            			anno.setTaxonomId( taxId );
            		
					proteinAnnotations.get( entry.getSequence() ).add( anno );
				}
			}
			
			
		} finally {
			if( fastaReader != null ) {
				fastaReader.close();
				fastaReader = null;
			}
		}
		
		
		
		return proteinAnnotations;
	}
	
	/**
	 * Return true if the supplied FASTA entry is a decoy entry. False otherwise.
	 * An entry is considered a decoy if any of the supplied decoy identifiers are present
	 * in any of the FASTA names
	 * 
	 * @param entry
	 * @param decoyIdentifiers
	 * @return
	 */
	private boolean isDecoyFastaEntry( FASTAEntry entry, Collection<String> decoyIdentifiers ) {

		for( String decoyId : decoyIdentifiers ) {			
			for( FASTAHeader header : entry.getHeaders() ) {

				if( header.getName().contains( decoyId ) )
					return true;
				
			}
			
		}
		
		return false;
		
	}
	
	
	/**
	 * Get all distinct peptides from a proxlxml doc's reported peptide section
	 * 
	 * @param proxlInputRoot
	 * @return
	 * @throws Exception
	 */
	private Collection<String> getDistinctPeptides( ProxlInput proxlInputRoot ) throws Exception {
		
		Collection<String> allPeptideSequences = new HashSet<>();
		
		ReportedPeptides reportedPeptides = proxlInputRoot.getReportedPeptides();
		
		if ( reportedPeptides != null ) {

			List<ReportedPeptide> reportedPeptideList = reportedPeptides.getReportedPeptide();
			
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					
					if ( reportedPeptides != null ) {

						Peptides peptidesProxlXML = reportedPeptide.getPeptides();
						List<Peptide> peptideProxlXMLList = peptidesProxlXML.getPeptide();

						if ( peptideProxlXMLList != null && ( ! peptideProxlXMLList.isEmpty() ) ) {
							
							for ( Peptide peptideProxlXML : peptideProxlXMLList ) {
								
								allPeptideSequences.add( peptideProxlXML.getSequence() );
							}
						}
					}
				}
			}
		}
		
		
		return allPeptideSequences;
	}
	
	/**
	 * An annotation for a protein in a Fasta file
	 * 
	 * @author mriffle
	 *
	 */
	private class FastaProteinAnnotation {

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			FastaProteinAnnotation that = (FastaProteinAnnotation) o;
			return name.equals(that.name) &&
					Objects.equals(description, that.description) &&
					Objects.equals(taxonomId, that.taxonomId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, description, taxonomId);
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Integer getTaxonomId() {
			return taxonomId;
		}
		public void setTaxonomId(Integer taxonomId) {
			this.taxonomId = taxonomId;
		}

		
		
		private String name;
		private String description;
		private Integer taxonomId;
		
	}
	
}
