package sortpom.wrapper.operation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sortpom.wrapper.content.Wrapper;

/**
 * Creates wrappers around xml fragments.
 *
 * @author Bjorn Ekryd
 */
public interface WrapperFactory {

    /** Creates wrapper around a root element. */
    HierarchyRootWrapper createFromRootElement(final Element rootElement);

    /** Creates wrapper around xml content. */
    <T extends Node> Wrapper<T> create(final T content);

}
