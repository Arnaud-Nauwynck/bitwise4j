package fr.an.bitwise4j.encoder.huffman;

/**
 * Visitor design pattern
 */
public interface HuffmanNodeVisitor<T> {

	public void caseNode(HuffmanTreeNode<T> p);
	
	public void caseLeaf(HuffmanTreeLeaf<T> p);
	
}
