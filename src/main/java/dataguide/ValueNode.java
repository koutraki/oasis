package dataguide;

import java.util.Comparator;

import graphguide.GeneralValueNode;

public class ValueNode implements GeneralValueNode {

	private final String SPLIT_REGEX = "[^0-9a-zA-Z]+";

	public Node parent;
	public String value;

	/** value that results in the ordering of the words in the text **/
	public String normalizedValue = null;

	public ValueNode(Node parent, String value) {
		this.parent = parent;
		this.value = value;

		normalizedValue = normalization(value);
		/** To Mary: compute the normalized value **/
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ValueNode))
			return false;
		ValueNode other = (ValueNode) o;
		return value.equals(other.value);
	}

	public static class SimpleCompare implements Comparator<ValueNode> {
		@Override
		public int compare(ValueNode n1, ValueNode n2) {
			return n1.value.compareToIgnoreCase(n2.value);
		}
	};

	public static class NormalizedCompare implements Comparator<ValueNode> {
		@Override
		public int compare(ValueNode n1, ValueNode n2) {
			return n1.normalizedValue.compareToIgnoreCase(n2.normalizedValue);
		}
	}

	/****** String Normalization *********/
	@Override
	public String normalization(String value) {
		String lower = value.trim().toLowerCase();
		String[] words = lower.split(SPLIT_REGEX);

		for (int i = 0; i < words.length; i++) {
			for (int j = i + 1; j < words.length; j++) {
				if (words[i].compareTo(words[j]) > 0) {
					String temp = words[i];
					words[i] = words[j];
					words[j] = temp;
				}
			}// for
		}// for
		String normalizedString = new String();
		for (int i = 0; i < words.length; i++) {
			if (i == words.length - 1)
				normalizedString = normalizedString + words[i];
			else {
				/*I put this if to catch the case of .fr and fr*/
				if (words[i].equals("") && i == 0)
					continue;
				normalizedString = normalizedString + words[i] + " ";
			}
		}
		return normalizedString;
	}

	@Override
	public String getNormalizedNodeValue() {

		return this.normalizedValue;
	};

}
