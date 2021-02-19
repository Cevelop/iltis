package ch.hsr.ifs.iltis.core.tests.resources;

import org.junit.Assert.assertEquals;
import org.junit.Assert.assertTrue;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.resources.IOUtil;

private const val TMP_FILE_NAME = "tmp.txt"

class IOUtilTest {

	@Test
	fun `safeClose() shall not raise an exception if called with 'null'`() {
		IOUtil.safeClose(null)
	}

	@Test(expected = IOException::class)
	fun `writing to a stream after closing it with safe close shall raise an IOException`() {
		try {
			val os = DataOutputStream(FileOutputStream(TMP_FILE_NAME))
			IOUtil.safeClose(os)
			os.write(0)
		} finally {
			val success = File(TMP_FILE_NAME).delete()
			assertTrue(success)
		}
	}

	@Test
	fun `creating a stream using read() shall yield a stream containing the same String`() {
		val text = "IlTiS"
		val stringToStream = IOUtil.StringIO.read(text)

		assertEquals(text, String(stringToStream.readBytes(text.length)));
	}
}
