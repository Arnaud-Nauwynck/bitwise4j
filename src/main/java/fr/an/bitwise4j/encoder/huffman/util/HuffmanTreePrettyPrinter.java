package fr.an.bitwise4j.encoder.huffman.util;

import java.io.PrintStream;

import fr.an.bitwise4j.encoder.huffman.HuffmanNodeVisitor;
import fr.an.bitwise4j.encoder.huffman.HuffmanTreeLeaf;
import fr.an.bitwise4j.encoder.huffman.HuffmanTreeNode;

/**
 * simple PrettyPrinter for HuffmanTree
 */
public class HuffmanTreePrettyPrinter<T> implements HuffmanNodeVisitor<T> {

	private PrintStream out;
	private int indentLevel;

	// ------------------------------------------------------------------------

	public HuffmanTreePrettyPrinter(PrintStream out) {
		this.out = out;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void caseLeaf(HuffmanTreeLeaf<T> p) {
		printIndent();
		out.print("Leaf sym=" + p.getSymbol() 
				+ ", freq=" + p.getFrequency()
				+ ", code=" + p.getResultCode()
				+ ", seqNumber=" + p.getSequenceNumber());
		out.println();
	}

	@Override
	public void caseNode(HuffmanTreeNode<T> p) {
		printIndent();
		out.print("Node " 
				+ ", sumFreq=" + p.getFrequency()
				+ ", code=" + p.getResultCode()
				+ ", seqNumber=" + p.getSequenceNumber());
		out.println();
		indentLevel++;
		
		printIndent();
		out.print("left:");
		out.println();
		if (p.getLeft() != null) {
			p.getLeft().accept(this);
		}
		
		printIndent();
		out.print("right:");
		out.println();
		if (p.getRight() != null) {
			p.getRight().accept(this);
		}
		
		indentLevel--;
		out.println();
	}
	
	private void printIndent() {
		for (int i = 0; i < indentLevel; i++) {
			out.append(' ');
		}
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "HuffmanTreePrettyPrinter";
	}
	
}
