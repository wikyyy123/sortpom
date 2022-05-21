package sortpom.sort;


import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlFragment {
    public static Document createXmlFragment(String tagName) {
        DocumentBuilder builder = getDocumentBuilder();

        var document = builder.newDocument();
        document.appendChild(document.createElement(tagName));
        return document;
    }
    
    public static Document createXmlProjectFragment() {
        DocumentBuilder builder = getDocumentBuilder();

        var document = builder.newDocument();
        var element = document.createElement("project");
        document.appendChild(element);
        var attr = document.createAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi");

//        element.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", );
//
//        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//        Element rootElement = new Element("project")
//                .setNamespace(Namespace.getNamespace("http://maven.apache.org/POM/4.0.0"))
//                .setAttribute(
//                        "schemaLocation",
//                        "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd",
//                        xsi);
//        rootElement.addNamespaceDeclaration(xsi);
//        rootElement.addContent(new Element("Gurka"));
//
//        return new Document().setRootElement(rootElement);
        return document;
    }

       private static DocumentBuilder getDocumentBuilder()  {
           try {
               return DocumentBuilderFactory.newInstance().newDocumentBuilder();
           } catch (ParserConfigurationException e) {
               throw new RuntimeException(e);
           }
       }

}
