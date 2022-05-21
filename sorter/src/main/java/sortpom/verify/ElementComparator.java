package sortpom.verify;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sortpom.util.XmlOrderedResult;

/**
 * @author bjorn
 * @since 2012-07-01
 */
public class ElementComparator {
    private final Element originalElement;
    private final Element newElement;

    public ElementComparator(Element originalElement, Element newElement) {
        this.originalElement = originalElement;
        this.newElement = newElement;
    }

    public XmlOrderedResult isElementOrdered() {
        if (!originalElement.getNodeName().equals(newElement.getNodeName())) {
            return XmlOrderedResult.nameDiffers(originalElement.getNodeName(), newElement.getNodeName());
        }
        if (isEqualsIgnoringWhitespace()) {
            return XmlOrderedResult.textContentDiffers(originalElement.getNodeName(), originalElement.getTextContent(), newElement.getTextContent());
        }
        //noinspection unchecked
        return isChildrenOrdered(originalElement.getNodeName(), originalElement.getChildNodes(), newElement.getChildNodes());
    }

    private boolean isEqualsIgnoringWhitespace() {
        return !originalElement.getTextContent().replaceAll("\\s", "").equals(newElement.getTextContent().replaceAll("\\s", ""));
    }

    private XmlOrderedResult isChildrenOrdered(String name, NodeList originalElementChildren, NodeList newElementChildren) {
        int size = Math.min(originalElementChildren.getLength(), newElementChildren.getLength());
        for (int i = 0; i < size; i++) {
            ElementComparator elementComparator = new ElementComparator((Element) originalElementChildren.item(i), (Element) newElementChildren.item(i));
            XmlOrderedResult elementOrdered = elementComparator.isElementOrdered();
            if (!elementOrdered.isOrdered()) {
                return elementOrdered;
            }
        }
        if (originalElementChildren.getLength() != newElementChildren.getLength()) {
            return XmlOrderedResult.childElementDiffers(name, originalElementChildren.getLength(), newElementChildren.getLength());
        }
        return XmlOrderedResult.ordered();
    }
}
