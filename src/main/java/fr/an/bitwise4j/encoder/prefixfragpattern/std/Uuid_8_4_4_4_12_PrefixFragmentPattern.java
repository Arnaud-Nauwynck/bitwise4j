package fr.an.bitwise4j.encoder.prefixfragpattern.std;

import static fr.an.bitwise4j.encoder.prefixfragpattern.std.PatternCharUtils.allHexaChars;
import static fr.an.bitwise4j.encoder.prefixfragpattern.std.PatternCharUtils.isSep;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentDecodeResult;
import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentEncodeResult;
import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentMatchResult;
import fr.an.bitwise4j.encoder.prefixfragpattern.PrefixFragmentPattern;

/**
 * pattern prefix for UUID: "xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx"
 *  = 8-4-4-4-12 hexadecimal lower-case chars with '-' separator
 *  
 *  36 chars... encoded as binary 16 bytes (4 x int32)
 */
public class Uuid_8_4_4_4_12_PrefixFragmentPattern extends PrefixFragmentPattern {

	public static final int PATTERN_SIZE = 8+4+4+4+12 + (1+1+1+1); // = 36 (32 + 4 separators) 
	public static final int PATTERN_ENCODED_LEN = 16;

	@Override
	public boolean startsWith(PrefixFragmentMatchResult matchResult, 
			CharSequence text, int fromPos) {
		int textLen = text.length();
		int remainLen = textLen - fromPos;
		if (remainLen < PATTERN_SIZE) {
			return false;
		}
		boolean res= allHexaChars(text, fromPos, fromPos+8) // 8
				&& isSep(text, fromPos+8) //
				&& allHexaChars(text, fromPos+9, fromPos+13) // 4
				&& isSep(text, fromPos+13) //
				&& allHexaChars(text, fromPos+14, fromPos+18) // 4
				&& isSep(text, fromPos+18) //
				&& allHexaChars(text, fromPos+19, fromPos+23) // 4
				&& isSep(text, fromPos+23) //
				&& allHexaChars(text, fromPos+24, fromPos+36); // 12
		if (res) {
			matchResult.encodedByteCount = PATTERN_ENCODED_LEN; //
			matchResult.toIndex = fromPos + PATTERN_SIZE;
		}
		return res;
	}

	@Override
	public void encodePrefix(PrefixFragmentEncodeResult res, 
			DataOutputStream out,
			CharSequence text, int fromPos) throws IOException {
		PatternCharUtils.encode8HexaChars(out, text, fromPos);
		// "-"
		PatternCharUtils.encode4HexaChars(out, text, fromPos+9);
		// "-"
		PatternCharUtils.encode4HexaChars(out, text, fromPos+14);
		// "-"
		PatternCharUtils.encode4HexaChars(out, text, fromPos+19);
		// "-"
		PatternCharUtils.encode12HexaChars(out, text, fromPos+24);
		res.toIndex = fromPos + PATTERN_SIZE;
	}

	@Override
	public void decodePrefix(PrefixFragmentDecodeResult res, 
			StringBuilder out,
			DataInputStream in)  throws IOException {
		PatternCharUtils.decode8HexaChars(out, in);
		out.append("-");
		PatternCharUtils.decode4HexaChars(out, in);
		out.append("-");
		PatternCharUtils.decode4HexaChars(out, in);
		out.append("-");
		PatternCharUtils.decode4HexaChars(out, in);
		out.append("-");
		PatternCharUtils.decode12HexaChars(out, in);
		res.toIndex += PATTERN_SIZE;
	}
	
}
