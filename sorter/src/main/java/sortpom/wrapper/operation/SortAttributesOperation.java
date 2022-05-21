package sortpom.wrapper.operation;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import sortpom.wrapper.content.Wrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Xml hierarchy operation that sort all attributes of xml elements. Used by
 * Used in HierarchyWrapper.processOperation(HierarchyWrapperOperation operation)
 * @author bjorn
 * @since 2013-11-01
 */
class SortAttributesOperation implements HierarchyWrapperOperation {
    private static final Comparator<Attr> ATTRIBUTE_COMPARATOR = Comparator.comparing(Attr::getName);

    /** Sort attributes of each element */
    @Override
    public void processElement(Wrapper<Element> elementWrapper) {
        Element element = elementWrapper.getContent();
        var sortedAttributes = getSortedAttributes(element);
        sortedAttributes.forEach(element::setAttributeNode);
    }

    private List<Attr> getSortedAttributes(Element element) {
        if (!element.hasAttributes()) {
            return List.of();
        }

        var attributes = element.getAttributes();

        var sortedAttributes = new ArrayList<Attr>();
        for (int i = 0; i < attributes.getLength(); i++) {
            sortedAttributes.add((Attr) attributes.item(i));
        }
        sortedAttributes.forEach(attr -> element.removeAttribute(attr.getName()));

        sortedAttributes.sort(ATTRIBUTE_COMPARATOR);
        return sortedAttributes;
    }

}
