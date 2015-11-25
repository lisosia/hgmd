package hgmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class InhouseMap {
	HashMap<String, HashMap<String, String>> m = new HashMap<String, HashMap<String, String>>();

	// input has 7 or 8 colums , tab-separated, begins with chr~~
	public InhouseMap(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		hgmd.Unil.PrepareMap(m);
		while (true) {
			String l = br.readLine();
			if (null == l || l.isEmpty()) {
				break;
			}
			String[] ls = l.split("\\t", -1);
			assert ls.length == 7 || ls.length == 8;
			assert ls[0].substring(0, 3).equals("chr");
			String chr = ls[0].substring(3, ls[0].length());
			assert chr.equals("X") || chr.equals("Y") || 1 <= Integer.parseInt(chr) && Integer.parseInt(chr) <= 22;
			assert m.get(chr) != null;
			m.get(chr).put(ls[1], ls[3] + ";" + ls[4] + ";" + ((ls.length == 8) ? ls[7] : ""));
		}
		br.close();
	}

	public String get(String chr, String pos) {
		String ret;
		if ((ret = m.get(chr).get(pos)) != null) {
			return ret;
		} else {
			return "no_entry";
		}
	}

}
