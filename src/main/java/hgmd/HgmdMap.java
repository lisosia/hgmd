package hgmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;


/*
 * hgmd_sqlite -> mutation.txt
 */
public class HgmdMap {
	final int maxIndelLen;
	HashMap<String , ArrayList<LineData> > m;

	public HgmdMap(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));

		{
			final String FIRSTLINE = br.readLine();
			final Pattern P = Pattern.compile("METADATA:maxIndelLength=(\\d+)");
			this.maxIndelLen = Integer.parseInt( P.matcher(FIRSTLINE).group(1) );
			assert maxIndelLen > 0;
		}
		
		// Construct ArrayMmp or like that
		ArrayList<String> chrList = new ArrayList<String>();
		for(int c=1; c<=22; c++) {
			chrList.add(Integer.toString(c));
		}
		chrList.add("X");
		chrList.add("Y");
		
		for(String c : chrList) {
			m.put(c,  new ArrayList<LineData>() );			
		}
		
		while(true) {
			String l = br.readLine();
			if(l.equals("") || null == l) {
				break;
			}
			
			String[] ls = l.split("|");
			final String chr_num = ls[0];
			final int startPos = Integer.parseInt( ls[1] );
			final int endPos   = Integer.parseInt(ls[2] );			
			final String acc_num  = ls[3];
			final String tag = ls[4];
			
			final String SEP = ":";
			m.get(chr_num).add( new LineData(startPos, endPos, acc_num+SEP+tag));
		}
		
		// may be slow , pre-sort(before this program) is better
		for(String c : chrList) {
			Collections.sort( m.get(c) , new LineDataCmpStart() );			
		}
		
		br.close();
	}
	
	static LineData tmpLineData = new LineData(-1, 0, null);
	/**
	 * 
	 * @param chr [1-22XY] "chr" is not needed
	 */
	private Vector<String> getData(final String chr, final int leftPos, final int rightPos) {
		ArrayList<LineData> hgmdArr = m.get(chr);
		final Vector<String> RET = new Vector<String>(1); // capacity is 1
		tmpLineData.setStartPos(leftPos);
		int pos = Collections.binarySearch( hgmdArr , tmpLineData , new LineDataCmpStart() );
		if(pos > 0) { //found
			while(pos > 0 && leftPos == hgmdArr.get(pos).startPos ) {pos -= 1;}
		}else { //not found
			pos = -(pos+1);
		}
		
		//now, arr[pos].start < leftpos
		
		int pos_b = pos;
		while(pos_b >= 0 ){
			LineData ld = hgmdArr.get(pos_b);
			if( ld.startPos > leftPos - this.maxIndelLen ) {break;}
			if( ld.endPos >= leftPos ) { RET.add( ld.info );}
			pos_b -= 1;
		}
		
		int pos_f = pos + 1;
		while( pos_f < hgmdArr.size() ) {
			LineData ld = hgmdArr.get(pos_f);
			if( ld.startPos > rightPos ){break;}
			RET.add(ld.info);
			pos_f += 1;
		}
		
		return RET;
		
	}
	
	/**
	 * @param chr [1-22XY] ,"chr" is not needed
	 */
	public void printAccnumTag(PrintStream pos,String chr, int leftPos, int rightPos) {
		final String SEPSUB = ",";
		Vector<String> data = getData(chr, leftPos, rightPos);
		if(data.size() != 0) {
			boolean isFirst = true;
			for(String s : data) {
				if(!isFirst) {pos.print(SEPSUB);}
				pos.print( s );
				isFirst = false;
			}
		}else {
			pos.print("no_entry");
		}
	}
}

final class LineData {
	int startPos, endPos;
	final String info;
	public LineData(int startPos, int endPos, String info) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.info = info;
	}
	
	public void setStartPos(int s){
		startPos = s;
	}
}

class LineDataCmpStart implements Comparator<LineData> {

	public int compare(LineData l1, LineData l2) {
		return (l1.startPos - l2.startPos);
	}
}