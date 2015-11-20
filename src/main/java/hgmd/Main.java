package hgmd;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	static HgmdMap hgmdMap;
	static HgmdMap hgmdMapIndel;
	static OmimMap omimMap;
	static HgmdDiseaseMap hgmdDiseaseMap;
	static InhouseMap inhouseMap;
	
	public static void main(String[] args) throws Exception{
		
		DefaultParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption(Option.builder("nss").required().desc("nss file. tab separated").build());
		options.addOption(Option.builder("hgmd").required().desc("| separated").build());
		options.addOption(Option.builder("hgmdIndel").required().desc("| separated").build());
		options.addOption(Option.builder("hgmdDisease").required().desc("| separated").build());		
		options.addOption(Option.builder("omim").required().desc("omim . | sep").build());		
		options.addOption(Option.builder("inhouse").required().desc("inhouse").build());		
		
		String nssP, hgmdP, hgmdIndelP, omimP, hgmdDisP, inhouseP;
		
		try{
			CommandLine res = parser.parse(options, args);
			nssP = res.getOptionValue("nss");
			hgmdP = res.getOptionValue("hgmd"); //mutation.txt
			hgmdIndelP = res.getOptionValue("hgmdIndel"); //mutationIndel.txt
			hgmdDisP = res.getOptionValue("hgmdDisease");
			omimP = res.getOptionValue("omim");
			inhouseP = res.getOptionValue("inhouse");
		}catch(ParseException e) {
			System.out.println( "Unexpected exception:" + e.getMessage() );
			throw e;
		}
		
		hgmdMap = new HgmdMap(hgmdP);
		hgmdMapIndel = new HgmdMap(hgmdIndelP);
		omimMap = new OmimMap(omimP);
		hgmdDiseaseMap = new HgmdDiseaseMap(hgmdDisP);
		inhouseMap = new InhouseMap(inhouseP);
		
		BufferedReader brNss = null;
		try {
			brNss = new BufferedReader(new FileReader(new File(nssP)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		final Pattern incINDEL = Pattern.compile("^INDEL");
		String line;
		while( (line = brNss.readLine() ) != null) {
			if(line.isEmpty()){break;}
			String[] ls = line.split("\t");
			String chr = ls[0].substring(3, ls[0].length() ); //[1-22XY]
			final String leftPosStr = ls[1];
			int leftPos = Integer.parseInt(ls[1] );
			int rightPos = leftPos + ls[2].length() -1;
			assert leftPos <= rightPos;
					
			if ( !incINDEL.matcher(ls[7]).find() ) { // not indel
				hgmdMap.printAccnumTag(System.out, chr, leftPos, rightPos);
				System.out.print("\t");
				printDisease( ls[10] ); //ls[10] is genes
				System.out.print("\t");
				System.err.print( inhouseMap.get(chr, leftPosStr) );
			}else {
				System.out.print("-\t");
				hgmdMapIndel.printAccnumTag(System.out, chr, leftPos, rightPos);
				System.out.print("\t");
				printDisease( ls[10] ); //ls[10] is genes
				System.out.print("\t-");				
			}
		}
	}

	private static void printDisease(String genesStr) {

		String[] gs = genesStr.split(";");
		HashSet<String> genes = new HashSet<String>(); //set for uniqueness
		for(String gene_txt : gs) {
			String[] gene = gene_txt.split("[,()]");
			if(! gene[1].equals("") ) { genes.add(gene[1]); }
		}
		
		boolean isFirst = true;
		final String DIS_SEP = ";";
		StringBuilder diseaseHgmd = new StringBuilder();
		StringBuilder diseaseOmim = new StringBuilder();
		for(String g: genes) {
			if(!isFirst) {
				diseaseHgmd.append(DIS_SEP);
				diseaseOmim.append(DIS_SEP);
			}
			diseaseHgmd.append( "GENE:" + g + hgmdDiseaseMap.get(g) );
			diseaseHgmd.append( "GENE:" + g + omimMap.get(g) );
			isFirst = false;
		}
		
		System.out.print( diseaseHgmd.toString() );
		System.out.print( "\t" );
		System.out.print( diseaseOmim.toString() );
	}
}
