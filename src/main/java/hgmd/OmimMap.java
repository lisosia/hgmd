package hgmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class OmimMap {
	HashMap<String, String> m = new HashMap<String, String>();

	public OmimMap(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));

		while (true) {
			String l = br.readLine();
			if (null == l || l.isEmpty()) {
				break;
			}
			String[] temp = l.split("\\|", -1); // -1 mens accept wmpty colums
												// when input is like:|||
			String[] genes = temp[5].split(", ", -1);
			if (temp.length <= 11) {
				br.close();
				throw new RuntimeException("invalid omim input, invalid line:" + l);
			}

			for (String gene : genes) {
				gene = gene.trim();
				String val = temp[11];
				String prev;
				if ((prev = m.get(gene)) == null) {
					m.put(gene, val);
				} else {
					m.put(gene, prev + "|" + val);
				}
			}
		}
		br.close();
	}

	public String get(String gene) {
		String ret = m.get(gene);
		if (ret == null) {
			return "no_entry";
		} else if (ret.isEmpty()) {
			return "empty";
		} else {
			return ret;
		}
	}

}
