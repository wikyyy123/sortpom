package sortpom.wrapper.operation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sortpom.wrapper.content.Wrapper;

/**
 * Xml hierarchy operation that detaches a xml child from its parent. Used by
 * Used in HierarchyWrapper.processOperation(HierarchyWrapperOperation operation)
 *
 * @author bjorn
 * @since 2013-11-01
 */
class DetachOperation implements HierarchyWrapperOperation {
    /** Detach each 'other content' */
    @Override
    public void processOtherContent(Wrapper<Node> contentWrapper) {
        var content = contentWrapper.getContent();
        var parentNode = content.getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(content);
        }
    }

    /** Detach each xml element */
    @Override
    public void processElement(Wrapper<Element> elementWrapper) {
        Element content = elementWrapper.getContent();
        var parentNode = content.getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(content);
        }
//        content.detach();
//        content.removeContent();
    }

}
