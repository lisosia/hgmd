package hgmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class OmimMap {
	HashMap<String, String> m;
	public OmimMap(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));

		// Construct ArrayMmp or like that
		m = new HashMap< String, String >();

		while(true) {
			String l = br.readLine();
			if(l.equals("") || null == l) {
				break;
			}
			String[] temp = l.split("|");
			String[] genes = temp[5].split("[, ]");
			for(String gene : genes) {
				String val;
				if( (val = m.get(gene) ) == null) {
					m.put(gene, val );
				}else {
					m.put(gene, val + "|" + gene );
				}
			}
		}
		br.close();
	}
	
	public String get( String gene) {
		String ret;
		if( (ret = m.get(gene) ) != null ){
			return ret;
		}else {
			return "no_entry";
		}
	}

}
