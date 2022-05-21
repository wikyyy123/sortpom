package sortpom.wrapper.content;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.stream.IntStream;

import static sortpom.wrapper.content.Phase.compareTo;

/**
 * A wrapper that contains a execution element. The element is sorted according to:
 * 1 no phase and no id
 * 2 no phase and id (sorted by id value)
 * 3 standard phase and no id (sorted by Maven phase order)
 * 4 standard phase and id (sorted by Maven phase order and then id value)
 * 5 other phase and no id (sorted by phase value)
 * 6 other phase and id (sorted by phase value and then id value)
 */
public class ExecutionSortedWrapper extends SortedWrapper {
    private final Phase phase;
    private final String id;

    /**
     * Instantiates a new child element sorted wrapper with a plugin element.
     *
     * @param element   the element
     * @param sortOrder the sort order
     */
    public ExecutionSortedWrapper(final Element element, final int sortOrder) {
        super(element, sortOrder);
        var children = getContent().getChildNodes();
        var childStream = IntStream.range(0, children.getLength())
                                   .mapToObj(children::item);
        phase = childStream
                .filter(e -> e.getNodeName().equals("phase") && e.getTextContent() != null)
                .map(e -> Phase.getPhase(e.getTextContent().trim()))
                .findFirst()
                .orElse(null);

        id = childStream
                .filter(e -> e.getNodeName().equals("id"))
                .map(element1 -> element1.getTextContent().trim())
                .findFirst()
                .orElse("");

    }

    @Override
    public boolean isBefore(final Wrapper<? extends Node> wrapper) {
        if (wrapper instanceof ExecutionSortedWrapper) {
            return isBeforeWrapper((ExecutionSortedWrapper) wrapper);
        }
        return super.isBefore(wrapper);
    }

    private boolean isBeforeWrapper(final ExecutionSortedWrapper wrapper) {
        int compare = compareTo(phase, wrapper.phase);
        if (compare != 0) {
            return compare < 0;
        }
        return id.compareTo(wrapper.id) < 0;
    }

    @Override
    public String toString() {
        return "ExecutionSortedWrapper{" +
                "phase=" + phase +
                ", id='" + id + '\'' +
                '}';
    }
}
