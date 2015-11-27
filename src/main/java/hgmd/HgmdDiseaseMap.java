package hgmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
 * input from allgenes
 * sqlite3 hgmd.sqlite3 "select gene,disease from allgenes" > allgenes
 */
public class HgmdDiseaseMap {
	HashMap<String, String> m = new HashMap<String, String>();
	final static String OUTPUT_SEP = ";";

	public HgmdDiseaseMap(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));

		while (true) {
			String l = br.readLine();
			if (null == l || l.isEmpty()) {
				break;
			}
			String[] temp = l.split("\\t", -1);
			if (temp.length != 2) {
				br.close();
				throw new RuntimeException("illegal hgmdDiseaseListFromat\n" + "filename:" + path + "\n" + "line:" + l);
			}
			String gene = temp[0];
			String dis = (temp.length == 2) ? temp[1] : "";

			String val;
			if ((val = m.get(gene)) != null) {
				m.put(gene, val + OUTPUT_SEP + dis);
			} else {
				m.put(gene, dis);
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
