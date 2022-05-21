package sortpom.wrapper;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import sortpom.exception.FailureException;
import sortpom.parameter.PluginParameters;
import sortpom.util.FileUtil;
import sortpom.wrapper.content.UnsortedWrapper;
import sortpom.wrapper.content.Wrapper;
import sortpom.wrapper.operation.HierarchyRootWrapper;
import sortpom.wrapper.operation.WrapperFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Concrete implementation of a wrapper factory that sorts xml according to
 * sort order from fileUtil.
 * <p>
 * Thank you Christian Haelg for your sortProperties patch.
 *
 * @author Bjorn Ekryd
 */
public class WrapperFactoryImpl implements WrapperFactory {

    /** How much the sort order index should increase for each element type */
    private static final int SORT_ORDER_INCREMENT = 100;

    /** Start value for sort order index. */
    private static final int SORT_ORDER_BASE = 1000;

    private final FileUtil fileUtil;

    private final ElementSortOrderMap elementSortOrderMap = new ElementSortOrderMap();
    private final ElementWrapperCreator elementWrapperCreator = new ElementWrapperCreator(elementSortOrderMap);
    private final TextWrapperCreator textWrapperCreator = new TextWrapperCreator();

    /**
     * Instantiates a new wrapper factory impl.
     *
     * @param fileUtil the file util
     */
    public WrapperFactoryImpl(final FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    /** Initializes the class with sortpom parameters. */
    public void setup(PluginParameters pluginParameters) {
        elementWrapperCreator.setup(pluginParameters);
        textWrapperCreator.setup(pluginParameters);
    }

    /** @see WrapperFactory#createFromRootElement(org.w3c.dom.Element) */
    public HierarchyRootWrapper createFromRootElement(final Element rootElement) {
        initializeSortOrderMap();
        return new HierarchyRootWrapper(create(rootElement));
    }

    /** Creates sort order map from chosen sort order. */
    private void initializeSortOrderMap() {
        try {
            Document document = createDocumentFromDefaultSortOrderFile();
            addElementsToSortOrderMap(document.getDocumentElement(), SORT_ORDER_BASE);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new FailureException(e.getMessage(), e);
        }
    }

    Document createDocumentFromDefaultSortOrderFile()
        throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(fileUtil.getDefaultSortOrderXml().getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Processes the chosen sort order. Adds sort order element and sort index to
     * a map.
     */
    private void addElementsToSortOrderMap(final Element element, int baseSortOrder) {
        elementSortOrderMap.addElement(element, baseSortOrder);
        var children = element.getChildNodes();
        // Increments the sort order index for each element
        int sortOrder = baseSortOrder;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                var childElement = (Element) children.item(i);
                sortOrder += SORT_ORDER_INCREMENT;
                addElementsToSortOrderMap(childElement, sortOrder);
            }
        }
    }

    /** @see WrapperFactory#create(org.w3c.dom.Node) */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> Wrapper<T> create(final T content) {
        if (content instanceof Element) {
            return (Wrapper<T>) elementWrapperCreator.createWrapper((Element) content);
        }
        if (content instanceof Comment) {
            return new UnsortedWrapper<>(content);
        }
        if (content instanceof Text) {
            return (Wrapper<T>) textWrapperCreator.createWrapper((Text) content);
        }
        return new UnsortedWrapper<>(content);
    }

}
