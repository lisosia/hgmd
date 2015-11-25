package hgmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Unil {
	public static void PrepareMap(Map<String, HashMap<String, String>> m) {
		ArrayList<String> chrList = new ArrayList<String>();
		for (int c = 1; c <= 22; c++) {
			chrList.add(Integer.toString(c));
		}
		chrList.add("X");
		chrList.add("Y");

		for (String c : chrList) {
			m.put(c, new HashMap<String, String>());
		}
	}

}
