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
				String val = temp[11];
				if ((val = m.get(gene)) == null) {
					m.put(gene, val);
				} else {
					m.put(gene, val + "|" + gene);
				}
			}
		}
		br.close();
	}

	public String get(String gene) {
		String ret;
		if ((ret = m.get(gene)) != null) {
			return ret;
		} else {
			return "no_entry";
		}
	}

}
