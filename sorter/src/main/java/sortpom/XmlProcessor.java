package sortpom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import sortpom.util.XmlOrderedResult;
import sortpom.verify.ElementComparator;
import sortpom.wrapper.operation.HierarchyRootWrapper;
import sortpom.wrapper.operation.WrapperFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates xml structure and sorts it.
 *
 * @author Bjorn Ekryd
 */
public class XmlProcessor {
    private final WrapperFactory factory;

    private Document originalDocument;
    private Document newDocument;

    public XmlProcessor(WrapperFactory factory) {
        this.factory = factory;
    }

    /**
     * Sets the original xml that should be sorted. Builds a dom document of the
     * xml.
     *
     * @param originalXml the new original xml
     * @throws org.jdom.JDOMException the jDOM exception
     * @throws java.io.IOException    Signals that an I/O exception has occurred.
     */
    public void setOriginalXml(final InputStream originalXml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        originalDocument = builder.parse(originalXml);
    }

    /** Creates a new dom document that contains the sorted xml. */
    public void sortXml() {
        newDocument = (Document) originalDocument.cloneNode(false);
        final Element rootElement = (Element) originalDocument.getDocumentElement().cloneNode(true);

        HierarchyRootWrapper rootWrapper = factory.createFromRootElement(rootElement);

        rootWrapper.createWrappedStructure(factory);
        rootWrapper.detachStructure();
        rootWrapper.sortStructureAttributes();
        rootWrapper.sortStructureElements();
        rootWrapper.connectXmlStructure();

//        var node = newDocument.importNode(rootWrapper.getElementContent().getContent(), true);
        var node = newDocument.adoptNode(rootWrapper.getElementContent().getContent());
        //newDocument.replaceChild(node, originalDocument.getDocumentElement());
        newDocument.appendChild(node);
    }

    public Document getNewDocument() {
        return newDocument;
    }

    public XmlOrderedResult isXmlOrdered() {
        ElementComparator elementComparator = new ElementComparator(originalDocument.getDocumentElement(), newDocument.getDocumentElement());
        return elementComparator.isElementOrdered();
    }

}
