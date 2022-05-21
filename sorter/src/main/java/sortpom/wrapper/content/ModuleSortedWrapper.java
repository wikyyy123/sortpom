package sortpom.wrapper.content;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A wrapper that contains a module element. The module is sorted alphabetically.
 *
 * @author msbg
 */
public class ModuleSortedWrapper extends SortedWrapper {
    private final String text;

    /**
     * Instantiates a new child element sorted wrapper with a module element.
     *
     * @param element   the element
     * @param sortOrder the sort order
     */
    public ModuleSortedWrapper(final Element element, final int sortOrder) {
        super(element, sortOrder);
        text = element.getTextContent().trim();
    }


    @Override
    public boolean isBefore(final Wrapper<? extends Node> wrapper) {
        if (wrapper instanceof ModuleSortedWrapper) {
            return isBeforeAlphabeticalTextSortedWrapper((ModuleSortedWrapper) wrapper);
        }
        return super.isBefore(wrapper);
    }

    private boolean isBeforeAlphabeticalTextSortedWrapper(ModuleSortedWrapper wrapper) {
        // SortOrder will always be same for both ModuleSortedWrapper because there is only one tag under modules
        // that is named module, see sortpom.wrapper.ElementWrapperCreator.isModuleElement.
        // So comparing getSortOrder is not needed.

        return wrapper.text.compareTo(text) >= 0;
    }

    @Override
    public String toString() {
        return "ModuleSortedWrapper{content=" +
                getContent() +
                '}';
    }

}
