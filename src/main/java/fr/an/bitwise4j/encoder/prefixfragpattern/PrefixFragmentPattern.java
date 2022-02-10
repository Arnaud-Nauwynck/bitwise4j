package fr.an.bitwise4j.encoder.prefixfragpattern;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * simple base class for compression encoding/decoding String as prefix fragments patterns
 *
 */
public abstract class PrefixFragmentPattern {

	public abstract boolean startsWith(PrefixFragmentMatchResult matchResult, 
			CharSequence text, int fromPos); 

	public abstract void encodePrefix(PrefixFragmentEncodeResult res, 
			DataOutputStream out,
			CharSequence text, int fromPos
			) throws IOException;
	
	public abstract void decodePrefix(PrefixFragmentDecodeResult res, 
			StringBuilder out,
			DataInputStream in
			)  throws IOException;

	
	/** helper for startsWith(.., 0); */
	public boolean startsWith(PrefixFragmentMatchResult matchResult, 
			CharSequence text) {
		return startsWith(matchResult, text, 0);
	}

	/** helper for encodePrefix(.., 0); */
	public void encodePrefix(PrefixFragmentEncodeResult res, 
			DataOutputStream out,
			CharSequence text
			) throws IOException {
		encodePrefix(res, out, text, 0);
	}
	
	
}
