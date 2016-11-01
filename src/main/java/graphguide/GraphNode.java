package graphguide;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import customization.Constants;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Comparator;

public class GraphNode implements GeneralValueNode {

    private final String SPLIT_REGEX = "[^0-9a-zA-Z]+";

    public static final int GRAPHNODETYPE = 0; // 0 for literal Nodes and 1 for
    // entity Nodes

    public int type = GRAPHNODETYPE;
    public String name;
    public String relationPathFromRoot;
    public GraphNode parent;
    public int level;

    public String normalizedName;

    /**
     * ****************** CONSTRACTOR ************************************
     */
    public GraphNode(int type, String name, String relation, GraphNode parent,
            int level) {
        this.type = type; // literal or entity node
        this.name = name;
        this.relationPathFromRoot = relation;
        this.parent = parent;
        this.level = level;

        if (type == 0) {
            this.normalizedName = normalization(name);
        }
    }

    /**
     * ******************* COMPARE *****************************
     */
    static class SimpleCompare implements Comparator<GraphNode> {

        @Override
        public int compare(GraphNode gn1, GraphNode gn2) {
            return gn1.name.compareToIgnoreCase(gn2.name);
        }

    };

    public static class NormalizeCompare implements Comparator<GraphNode> {

        @Override
        public int compare(GraphNode gn1, GraphNode gn2) {
            return gn1.normalizedName.compareToIgnoreCase(gn2.normalizedName);
        }

    }

    /**
     * ** COMPARE the normalized values of a GrahpNode and a ValueNode ***
     */
    public static class NormalizeCompareMIX implements
            Comparator<GeneralValueNode> {

        @Override
        public int compare(GeneralValueNode gn1, GeneralValueNode gn2) {
            return gn1.getNormalizedNodeValue().compareToIgnoreCase(
                    gn2.getNormalizedNodeValue());
        }

    }

    /**
     * **** String Normalization ********
     */
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
            if (i == words.length - 1) {
                normalizedString = normalizedString + words[i];
            } else {
                if (words[i].equals("") && i == 0) {
                    continue;
                }
                normalizedString = normalizedString + words[i] + " ";
            }
        }
        /**
         * For small numbers
         */
        if (!normalizedString.equals("") && isNumeric(normalizedString) && normalizedString.length()<=2) {
            if (Integer.parseInt(normalizedString) <= 50 && Integer.parseInt(normalizedString)>=0) {
                return "";
            }
        }

        return normalizedString;
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    @Override
    public String getNormalizedNodeValue() {

        return this.normalizedName;
    }

}
