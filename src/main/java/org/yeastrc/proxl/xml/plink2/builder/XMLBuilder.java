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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.yeastrc.proxl.xml.plink2.annotations.PSMAnnotationTypes;
import org.yeastrc.proxl.xml.plink2.annotations.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.proxl.xml.plink2.objects.PLinkLinkerEnd;
import org.yeastrc.proxl.xml.plink2.objects.PLinkModification;
import org.yeastrc.proxl.xml.plink2.objects.PLinkReportedPeptide;
import org.yeastrc.proxl.xml.plink2.objects.PLinkResult;
import org.yeastrc.proxl.xml.plink2.reader.PLinkConstants;
import org.yeastrc.proxl.xml.plink2.reader.PLinkSearchParameters;
import org.yeastrc.proxl.xml.plink2.utils.ModificationLookupUtils;
import org.yeastrc.proxl.xml.plink2.utils.NumberUtils;
import org.yeastrc.proxl.xml.plink2.utils.PLinkUtils;
import org.yeastrc.proxl_import.api.xml_dto.*;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;

/**
 * Take the populated pLink objects, convert to XML and write the XML file
 * 
 * @author Michael Riffle
 * @date Mar 23, 2016
 *
 */
public class XMLBuilder {

	/**
	 * Take the populated pLink objects, convert to XML and write the XML file
	 * 
	 * @param params The PLinkSearchParameters associated with this search
	 * @param results The results parsed from the plink output
	 * @param outfile The file to which the XML will be written
	 * @throws Exception
	 */
	public void buildAndSaveXML( PLinkSearchParameters params, Collection<PLinkResult> results, File outfile, String fastaFilePath ) throws Exception {

		ProxlInput proxlInputRoot = new ProxlInput();

		proxlInputRoot.setFastaFilename( ( new File( fastaFilePath ) ).getName() );
		
		// if they didn't specify a fasta file on the command line, use the one in the INI file
		if( fastaFilePath == null )
			throw new Exception( "fastaFilePath cannot be null." );
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		proxlInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );
		
		SearchProgram searchProgram = new SearchProgram();
		searchPrograms.getSearchProgram().add( searchProgram );
		
		searchProgram.setName( PLinkConstants.SEARCH_PROGRAM_NAME );
		searchProgram.setDisplayName( PLinkConstants.SEARCH_PROGRAM_NAME );
		searchProgram.setVersion( PLinkUtils.getVersion( params ) );
		
		//
		// Define the annotation types present in PLink data
		//
		PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
		searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
		
		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
		psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
		
		// only add e-value if it was generated for this search
		for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes() ) {
			if( !annoType.getName().equals( PSMAnnotationTypes.ANNOTATION_TYPE_EVALUE ) )
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			else {
				if( PLinkUtils.evaluePresent( params ) )
					filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
		}
				
		DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes = new DescriptivePsmAnnotationTypes();
		psmAnnotationTypes.setDescriptivePsmAnnotationTypes( descriptivePsmAnnotationTypes );
		descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType().addAll( PSMAnnotationTypes.getDescriptivePsmAnnotationTypes() );
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		// only make e-value default visibile if it's present in this search
		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			if( !sa.getAnnotationName().equals( PSMAnnotationTypes.ANNOTATION_TYPE_EVALUE ) )
				xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
			else {
				if( PLinkUtils.evaluePresent( params ) )
					xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
			}	
		}
				
		//
		// Define the linker information
		//
		Linkers linkers = new Linkers();
		proxlInputRoot.setLinkers( linkers );

		Linker linker = new Linker();
		linkers.getLinker().add( linker );
		
		linker.setName( params.getLinker().getName().toLowerCase() );
		
		CrosslinkMasses masses = new CrosslinkMasses();
		linker.setCrosslinkMasses( masses );
		
		CrosslinkMass xlinkMass = new CrosslinkMass();
		if( params.getLinker().getFormula() != null ) {
			xlinkMass.setChemicalFormula( params.getLinker().getFormula() );
		}
		linker.getCrosslinkMasses().getCrosslinkMass().add( xlinkMass );


		if( params.getLinker().getCleavedLinkerMasses() != null && params.getLinker().getCleavedLinkerMasses().size() > 0 ) {

			for( Double mass : params.getLinker().getCleavedLinkerMasses() ) {

				CleavedCrosslinkMass cleavedMass = new CleavedCrosslinkMass();
				cleavedMass.setMass(BigDecimal.valueOf( mass ) );
				masses.getCleavedCrosslinkMass().add( cleavedMass );
			}

		}

		// set the mass for this crosslinker to the calculated mass for the crosslinker, as defined in the properties file
		xlinkMass.setMass( NumberUtils.getRoundedBigDecimal( params.getLinker().getMonoCrosslinkMass() ) );

		//
		// Add in the linkable/reactable linker ends as defined in the stavrox config file
		//
		LinkedEnds xLinkedEnds = new LinkedEnds();
		linker.setLinkedEnds( xLinkedEnds );

		for(PLinkLinkerEnd plinkLinkerEnd : params.getLinker().getLinkerEnds() ) {

			LinkedEnd xLinkedEnd = new LinkedEnd();
			xLinkedEnds.getLinkedEnd().add( xLinkedEnd );

			Residues xResidues = new Residues();
			xResidues.getResidue().addAll( plinkLinkerEnd.getResidues() );

			xLinkedEnd.setResidues( xResidues );

			ProteinTermini xProteinTermini = null;

			if( plinkLinkerEnd.isLinksCTerminus() || plinkLinkerEnd.isLinksNTerminus() ) {

				if( xProteinTermini == null ) {
					xProteinTermini = new ProteinTermini();
					xLinkedEnd.setProteinTermini( xProteinTermini );
				}

				if( plinkLinkerEnd.isLinksCTerminus() ) {
					ProteinTerminus xProteinTerminus = new ProteinTerminus();
					xProteinTermini.getProteinTerminus().add( xProteinTerminus );

					xProteinTerminus.setTerminusEnd( ProteinTerminusDesignation.C );
					xProteinTerminus.setDistanceFromTerminus(BigInteger.ZERO);
				}

				if( plinkLinkerEnd.isLinksNTerminus() ) {

					{
						ProteinTerminus xProteinTerminus = new ProteinTerminus();
						xProteinTermini.getProteinTerminus().add(xProteinTerminus);

						xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.N);
						xProteinTerminus.setDistanceFromTerminus(BigInteger.ZERO);
					}

					{
						ProteinTerminus xProteinTerminus = new ProteinTerminus();
						xProteinTermini.getProteinTerminus().add(xProteinTerminus);

						xProteinTerminus.setTerminusEnd(ProteinTerminusDesignation.N);
						xProteinTerminus.setDistanceFromTerminus(BigInteger.ONE);
					}
				}
			}
		}

		//
		// Define the static mods
		//
		if(ModificationLookupUtils.getStaticModificationNames( params ).size() > 0) {
			StaticModifications smods = new StaticModifications();
			proxlInputRoot.setStaticModifications(smods);

			for (String modName : ModificationLookupUtils.getStaticModificationNames(params)) {
				PLinkModification smod = ModificationLookupUtils.getPLinkModificationFromParameters(modName, params);

				// a single defined mod can affect multiple residue types, get them all added
				for (String residue : smod.getResidues()) {

					StaticModification xmlSmod = new StaticModification();
					xmlSmod.setAminoAcid(residue);
					xmlSmod.setMassChange(NumberUtils.getRoundedBigDecimal(smod.getMonoisotopicMass()));

					smods.getStaticModification().add(xmlSmod);
				}
			}
		}
		
		//
		// Add decoy labels (optional)
		//
		
		Collection<String> decoyLabels = new HashSet<>();
		decoyLabels.add( "random" );
		decoyLabels.add( "decoy" );
		decoyLabels.add( "reverse" );
		decoyLabels.add( "shuffle" );
		
		DecoyLabels xmlDecoyLabels = new DecoyLabels();
		proxlInputRoot.setDecoyLabels( xmlDecoyLabels );
		
		for( String decoyLabel : decoyLabels ) {
			DecoyLabel xmlDecoyLabel = new DecoyLabel();
			xmlDecoyLabels.getDecoyLabel().add( xmlDecoyLabel );
			
			xmlDecoyLabel.setPrefix( decoyLabel );
		}
		
		
		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		proxlInputRoot.setReportedPeptides( reportedPeptides );
		
		// need to organize all results by distinct reported peptide strings
		Map<PLinkReportedPeptide, Collection<PLinkResult>> resultsByReportedPeptide = new HashMap<PLinkReportedPeptide, Collection<PLinkResult>>();
		for( PLinkResult result : results ) {
			
			if( !resultsByReportedPeptide.containsKey( result.getReportedPeptide() ) )
				resultsByReportedPeptide.put( result.getReportedPeptide(), new ArrayList<PLinkResult>() );
			
			resultsByReportedPeptide.get( result.getReportedPeptide() ).add( result );
		}
		
		// iterate over each distinct reported peptide
		for( PLinkReportedPeptide rp : resultsByReportedPeptide.keySet() ) {
			
			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( rp.toString() );
			
			if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK )
				xmlReportedPeptide.setType( LinkType.CROSSLINK );
			else if( rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK )
				xmlReportedPeptide.setType( LinkType.LOOPLINK );
			else
				xmlReportedPeptide.setType( LinkType.UNLINKED );	// monolinked peptide with no cross- or loop-link are considered unlinked (monolinks are considered mods)
			
			Peptides xmlPeptides = new Peptides();
			xmlReportedPeptide.setPeptides( xmlPeptides );
			
			// add in the 1st parsed peptide
			{
				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add( xmlPeptide );
				
				xmlPeptide.setSequence( rp.getPeptide1().getSequence() );
				
				// add in the mods for this peptide
				if( rp.getPeptide1().getMods() != null && rp.getPeptide1().getMods().keySet().size() > 0 ) {
					
					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications( xmlModifications );
					
					for( int position : rp.getPeptide1().getMods().keySet() ) {
						for( PLinkModification mod : rp.getPeptide1().getMods().get( position ) ) {
	
							Modification xmlModification = new Modification();
							xmlModifications.getModification().add( xmlModification );
							
							xmlModification.setMass( NumberUtils.getRoundedBigDecimal( mod.getMonoisotopicMass() ) );

							if( position == 0 ) {    // handle n-terminal
								xmlModification.setIsNTerminal(true);
							} else if( position == rp.getPeptide1().getSequence().length() + 1 ) {	// handle c-terminal
								xmlModification.setIsCTerminal(true);
							} else {
								xmlModification.setPosition(new BigInteger(String.valueOf(position)));
							}

							xmlModification.setIsMonolink( mod.isMonolink() );
							
						}
					}
				}
				
				// add in the linked position(s) in this peptide
				if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK || rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK ) {
					
					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions( xmlLinkedPositions );
					
					LinkedPosition xmlLinkedPosition = new LinkedPosition();
					xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
					xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition1() ) ) );
					
					if( rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK ) {
						
						xmlLinkedPosition = new LinkedPosition();
						xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
						xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition2() ) ) );
						
					}
				}
				
			}
			
			
			// add in the 2nd parsed peptide, if it exists
			if( rp.getPeptide2() != null ) {
				
				Peptide xmlPeptide = new Peptide();
				xmlPeptides.getPeptide().add( xmlPeptide );
				
				xmlPeptide.setSequence( rp.getPeptide2().getSequence() );
				
				// add in the mods for this peptide
				if( rp.getPeptide2().getMods() != null && rp.getPeptide2().getMods().keySet().size() > 0 ) {
					
					Modifications xmlModifications = new Modifications();
					xmlPeptide.setModifications( xmlModifications );
					
					for( int position : rp.getPeptide2().getMods().keySet() ) {
						for( PLinkModification mod : rp.getPeptide2().getMods().get( position ) ) {

							Modification xmlModification = new Modification();
							xmlModifications.getModification().add( xmlModification );
							
							xmlModification.setMass( NumberUtils.getRoundedBigDecimal( mod.getMonoisotopicMass() ) );

							if( position == 0 ) {    // handle n-terminal
								xmlModification.setIsNTerminal(true);
							} else if( position == rp.getPeptide2().getSequence().length() + 1 ) {	// handle c-terminal
								xmlModification.setIsCTerminal(true);
							} else {
								xmlModification.setPosition(new BigInteger(String.valueOf(position)));
							}

							xmlModification.setIsMonolink( mod.isMonolink() );
							
						}
					}
				}
				
				// add in the linked position in this peptide
				if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK ) {
					
					LinkedPositions xmlLinkedPositions = new LinkedPositions();
					xmlPeptide.setLinkedPositions( xmlLinkedPositions );
					
					LinkedPosition xmlLinkedPosition = new LinkedPosition();
					xmlLinkedPositions.getLinkedPosition().add( xmlLinkedPosition );
					xmlLinkedPosition.setPosition( new BigInteger( String.valueOf( rp.getPosition2() ) ) );
				}
			}
			
			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );
			
			// iterate over all PSMs for this reported peptide
			for( PLinkResult result : resultsByReportedPeptide.get( rp ) ) {
				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );
				
				xmlPsm.setScanNumber( new BigInteger( String.valueOf( result.getScanNumber() ) ) );
				xmlPsm.setScanFileName(result.getScanFilePrefix());
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( result.getCharge() ) ) );
				
				if( rp.getType() == PLinkConstants.LINK_TYPE_CROSSLINK || rp.getType() == PLinkConstants.LINK_TYPE_LOOPLINK )
					xmlPsm.setLinkerMass( NumberUtils.getRoundedBigDecimal( params.getLinker().getMonoCrosslinkMass() ) );
				
				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );
				
				// handle score
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					xmlFilterablePsmAnnotation.setValue( NumberUtils.getScientificNotationBigDecimal( result.getScore() ) );
				}
				
				
				// handle evalue
				if( PLinkUtils.evaluePresent( params ) ) {
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_EVALUE );
					xmlFilterablePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					xmlFilterablePsmAnnotation.setValue( NumberUtils.getScientificNotationBigDecimal( result.getEvalue() ) );
				}
				
				// add in the non-filterable descriptive annotations (e.g., calculated mass)
				DescriptivePsmAnnotations xmlDescriptivePsmAnnotations = new DescriptivePsmAnnotations();
				xmlPsm.setDescriptivePsmAnnotations( xmlDescriptivePsmAnnotations );
				
				{
					// handle calc mass
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_CALC_MASS );
					xmlDescriptivePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					
					// try to limit this value to the chosen number of decimal places
					try {
						xmlDescriptivePsmAnnotation.setValue( NumberUtils.getRoundedBigDecimal( Double.valueOf( result.getCalculatedMass() ) ).toString() );
					} catch( Exception e ) {
						xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getCalculatedMass() ) );
					}
				}
				
				{
					// handle delta mass
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_DELTA_MASS );
					xmlDescriptivePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					
					// try to limit this value to the chosen number of decimal places
					try {
						xmlDescriptivePsmAnnotation.setValue( NumberUtils.getRoundedBigDecimal( Double.valueOf( result.getDeltaMass() ) ).toString() );
					} catch( Exception e ) {
						xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getDeltaMass() ) );
					}
				}
				
				{
					// handle delta mass ppm
					DescriptivePsmAnnotation xmlDescriptivePsmAnnotation = new DescriptivePsmAnnotation();
					xmlDescriptivePsmAnnotations.getDescriptivePsmAnnotation().add( xmlDescriptivePsmAnnotation );
					
					xmlDescriptivePsmAnnotation.setAnnotationName( PSMAnnotationTypes.ANNOTATION_TYPE_PPM );
					xmlDescriptivePsmAnnotation.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
					
					// try to limit this value to the chosen number of decimal places
					try {
						xmlDescriptivePsmAnnotation.setValue( NumberUtils.getRoundedBigDecimal( Double.valueOf( result.getDeltaMassPPM() ) ).toString() );
					} catch( Exception e ) {
						xmlDescriptivePsmAnnotation.setValue( String.valueOf( result.getDeltaMassPPM() ) );
					}
				}
				
				
			}//end iterating over all PSMs for a reported peptide
			
			
		}// end iterating over distinct reported peptides

		
		
		// add in the matched proteins section
		MatchedProteinsBuilder.getInstance().buildMatchedProteins( proxlInputRoot, new File( fastaFilePath ), decoyLabels );
		
		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		proxlInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
		
		xmlConfigurationFile.setSearchProgram( PLinkConstants.SEARCH_PROGRAM_NAME );
		xmlConfigurationFile.setFileName( ( new File( params.getPlinkINI().getFilename() ) ).getName() );
		xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( params.getPlinkINI().getFilename() ) ) );
		
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain(outfile, proxlInputRoot);
		
	}
	
}
