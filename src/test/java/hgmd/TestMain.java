package hgmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class TestMain {

	@Test
	public void testHgmdMap() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		String rt = System.getProperty("user.dir");
		String path = rt + "/src/test/resources/mut.txt";
		System.err.println("resource-path:" + path);
		HgmdMap hgmdMap = new HgmdMap(path);

		assertEquals(hgmdMap.getMaxIndelLen(), 1);

		// get1
		hgmdMap.printAccnumTag(ps, "1", 3, 3);
		ps.flush();
		assertEquals(baos.toString(), "no_entry");

		// get2
		baos.reset();
		hgmdMap.printAccnumTag(ps, "1", 8, 9);
		ps.flush();
		assertEquals(baos.toString(), "PP000002:CD");

		// get3
		baos.reset();
		hgmdMap.printAccnumTag(ps, "1", 2, 9);
		ps.flush();
		assertEquals(baos.toString(), "PP000001:AB,PP000002:CD");

		baos.reset();
		hgmdMap.printAccnumTag(ps, "1", 44, 44);
		ps.flush();
		assertEquals(baos.toString(), "PP000002:GH");

		baos.reset();
		hgmdMap.printAccnumTag(ps, "1", 45, 45);
		ps.flush();
		assertEquals(baos.toString(), "no_entry");

		assertTrue(hgmdMap.getMapSize("1") == 4);
		assertTrue(hgmdMap.getMapSize("2") == 0);
		assertTrue(hgmdMap.getMapSize("X") == 0);
		assertTrue(hgmdMap.getMapSize("Y") == 0);

	}

}
