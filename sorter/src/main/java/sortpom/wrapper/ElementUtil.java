package sortpom.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Contains utility methods for Xml elements
 *
 * @author bjorn
 * @since 2013-10-21
 */
final class ElementUtil {
    /** Hidden constructor */
    private ElementUtil() {
    }

    /** Returns fully qualified name for an Xml element. */
    static String getDeepName(final Node element) {
        if (element == null) {
            return "";
        }
        return getDeepName(element.getParentNode()) + '/' + element.getNodeName();
    }

    /** Returns true if an elements parents name is same as argument */
    static boolean isElementParentName(Element element, String name) {
        Node parent = element.getParentNode();
        if (parent == null) {
            return false;
        }
        return isElementName(parent, name);
    }

    /** Returns true if an elements name is same as argument */
    static boolean isElementName(Node element, String name) {
        return element.getNodeName().equals(name);
    }
}
