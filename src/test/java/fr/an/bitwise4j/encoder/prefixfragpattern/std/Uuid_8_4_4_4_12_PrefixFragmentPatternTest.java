package fr.an.bitwise4j.encoder.prefixfragpattern.std;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentDecodeResult;
import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentEncodeResult;
import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentMatchResult;
import lombok.val;

public class Uuid_8_4_4_4_12_PrefixFragmentPatternTest {

	private static final String textUuid1 = "123e4567-e89b-12d3-a456-426614174001";
	private static final String[] textUuids = new String[] {
			"00000000-0000-0000-0000-000000000000",
			"00000000-0000-0000-0000-000000000001",
			"00000000-0000-0000-0000-000000000010",
			"00000000-0000-0000-0000-000000000100",
			"00000000-0000-0000-0000-000000001000",
			"00000000-0000-0000-0000-000000010000",
			"00000000-0000-0000-0000-000000100000",
			"00000000-0000-0000-0000-000001000000",
			"00000000-0000-0000-0000-000010000000",
			"00000000-0000-0000-0000-000100000000",
			"00000000-0000-0000-0000-001000000000",
			"00000000-0000-0000-0000-010000000000",
			"00000000-0000-0000-0000-100000000000",
			"00000000-0000-0000-0001-000000000000",
			"00000000-0000-0000-0010-000000000000",
			"00000000-0000-0000-0100-000000000000",
			"00000000-0000-0000-1000-000000000000",
			"00000000-0000-0001-0000-000000000000",
			"00000000-0000-0010-0000-000000000000",
			"00000000-0000-0100-0000-000000000000",
			"00000000-0000-1000-0000-000000000000",
			"00000000-0001-0000-0000-000000000000",
			"00000000-0010-0000-0000-000000000000",
			"00000000-0100-0000-0000-000000000000",
			"00000000-1000-0000-0000-000000000000",
			"00000001-0000-0000-0000-000000000000",
			"00000010-0000-0000-0000-000000000000",
			"00000100-0000-0000-0000-000000000000",
			"00001000-0000-0000-0000-000000000000",
			"00010000-0000-0000-0000-000000000000",
			"00100000-0000-0000-0000-000000000000",
			"01000000-0000-0000-0000-000000000000",
			"10000000-0000-0000-0000-000000000000",
			
			"00000000-0000-0000-0000-000000000000",
			"00000000-0000-0000-0000-00000000000f",
			"00000000-0000-0000-0000-0000000000f0",
			"00000000-0000-0000-0000-000000000f00",
			"00000000-0000-0000-0000-00000000f000",
			"00000000-0000-0000-0000-0000000f0000",
			"00000000-0000-0000-0000-000000f00000",
			"00000000-0000-0000-0000-00000f000000",
			"00000000-0000-0000-0000-0000f0000000",
			"00000000-0000-0000-0000-000f00000000",
			"00000000-0000-0000-0000-00f000000000",
			"00000000-0000-0000-0000-0f0000000000",
			"00000000-0000-0000-0000-f00000000000",
			"00000000-0000-0000-000f-000000000000",
			"00000000-0000-0000-00f0-000000000000",
			"00000000-0000-0000-0f00-000000000000",
			"00000000-0000-0000-f000-000000000000",
			"00000000-0000-000f-0000-000000000000",
			"00000000-0000-00f0-0000-000000000000",
			"00000000-0000-0f00-0000-000000000000",
			"00000000-0000-f000-0000-000000000000",
			"00000000-000f-0000-0000-000000000000",
			"00000000-00f0-0000-0000-000000000000",
			"00000000-0f00-0000-0000-000000000000",
			"00000000-f000-0000-0000-000000000000",
			"0000000f-0000-0000-0000-000000000000",
			"000000f0-0000-0000-0000-000000000000",
			"00000f00-0000-0000-0000-000000000000",
			"0000f000-0000-0000-0000-000000000000",
			"000f0000-0000-0000-0000-000000000000",
			"00f00000-0000-0000-0000-000000000000",
			"0f000000-0000-0000-0000-000000000000",
			"f0000000-0000-0000-0000-000000000000",
			
			"11111111-2222-3333-4444-555555555555",
			textUuid1,
			"123e4567-e89b-12d3-a456-426614174002",
			"023e4567-e89b-12d3-a456-426614174002",
			"023e4567-e89b-12d3-a456-426614174000",
			"023e4567-e89b-12d3-a456-4266141740ff"
		};
	
	private Uuid_8_4_4_4_12_PrefixFragmentPattern sut = new Uuid_8_4_4_4_12_PrefixFragmentPattern();
	
	
	@Test
	public void testEncodeThenDecode() throws IOException {
		doTestEncodeThenDecode("00000000-0000-0000-0000-000000010000");
		for(val text: textUuids) {
			doTestEncodeThenDecode(text);
		}
	}
	
	private void doTestEncodeThenDecode(String textUuid) throws IOException {
		val matchRes = new PrefixFragmentMatchResult();
		// startsWith
		boolean checkMatch = sut.startsWith(matchRes, textUuid);
		Assert.assertTrue(checkMatch);
		Assert.assertEquals(textUuid.length(), matchRes.toIndex);
		Assert.assertEquals(16, matchRes.encodedByteCount);
		
		val encodeRes = new PrefixFragmentEncodeResult();
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		val encodeOut = new DataOutputStream(outBuffer);
		// encode
		sut.encodePrefix(encodeRes, encodeOut, textUuid);
		val encodedBytes = outBuffer.toByteArray();
		Assert.assertEquals(matchRes.encodedByteCount, encodedBytes.length);

		val decodeRes = new PrefixFragmentDecodeResult();
		val decodeIn = new DataInputStream(new ByteArrayInputStream(encodedBytes));
		val decodeOut = new StringBuilder();
		// decode
		sut.decodePrefix(decodeRes, decodeOut, decodeIn);
		Assert.assertEquals(textUuid.length(), decodeRes.toIndex);
		Assert.assertEquals(textUuid, decodeOut.toString());
	}
	
}
