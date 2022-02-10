package fr.an.bitwise4j.encoder.prefixfragpattern.std;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PatternCharUtils {

	private static final String hexaDigits = "0123456789abcdef";
	
	public static boolean allHexaChars(CharSequence text, int start, int endExcluded) {
		for(int i = start; i < endExcluded; i++) {
			char ch = text.charAt(i);
			if (! isHexa(ch)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isHexa(char ch) {
		return ('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'f');
	}
	
	public static boolean isSep(CharSequence text, int pos) {
		char ch = text.charAt(pos);
		return '-' == ch;
	}

	public static void encode2HexaChars(OutputStream out, //
			CharSequence text, int pos
			) throws IOException {
		char ch0 = text.charAt(pos);
		char ch1 = text.charAt(pos+1);
		int v0 = hexaCharToValue(ch0);
		int v1 = hexaCharToValue(ch1);
		int b = ((v0 << 4) & 0xF0)
				| (v1 & 0x0F);
		out.write(b);
	}

	public static void decode2HexaChars(StringBuilder out,
			InputStream in
			) throws IOException {
		int b = in.read();
		int v0 = (b >>> 4) & 0xF;
		int v1 =  b & 0xF;
		char ch0 = valueToHexaChar(v0);
		char ch1 = valueToHexaChar(v1);
		out.append(ch0);
		out.append(ch1);
	}

	public static void encode4HexaChars(OutputStream out, //
			CharSequence text, int pos
			) throws IOException {
		encode2HexaChars(out, text, pos);
		encode2HexaChars(out, text, pos + 2);
	}

	public static void decode4HexaChars(StringBuilder out,
			InputStream in
			) throws IOException {
		decode2HexaChars(out, in);
		decode2HexaChars(out, in);
	}
	
	public static void encode8HexaChars(OutputStream out,
			CharSequence text, int pos
			) throws IOException {
		encode2HexaChars(out, text, pos);
		encode2HexaChars(out, text, pos + 2);
		encode2HexaChars(out, text, pos + 4);
		encode2HexaChars(out, text, pos + 6);
	}

	public static void decode8HexaChars(StringBuilder out,
			InputStream in
			) throws IOException {
		decode2HexaChars(out, in);
		decode2HexaChars(out, in);
		decode2HexaChars(out, in);
		decode2HexaChars(out, in);
	}
	
	
	public static void encode12HexaChars(OutputStream out, //
			CharSequence text, int pos
			) throws IOException {
		encode4HexaChars(out, text, pos);
		encode8HexaChars(out, text, pos + 4);
	}

	public static void decode12HexaChars(StringBuilder out,
			InputStream in
			) throws IOException {
		decode4HexaChars(out, in);
		decode8HexaChars(out, in);
	}
	
	
	

	public static int hexaCharToInt(CharSequence text, int pos) {
		char ch = text.charAt(pos);
		return hexaCharToValue(ch);
	}
	
	public static int hexaCharToValue(char ch) {
		if ('0' <= ch && ch <= '9') return (ch-'0');
		else return 10+ch-'a';
	}

	public static char valueToHexaChar(int value) {
		return hexaDigits.charAt(value);
	}

	public static byte hexa2CharsToByte(char ch0, char ch1) {
		int v0 = hexaCharToValue(ch0);
		int v1 = hexaCharToValue(ch1);
		return (byte) (
				((v0 << 4) & 0xF0)
				| (v1 & 0x0F)
				);
	}

	
}
