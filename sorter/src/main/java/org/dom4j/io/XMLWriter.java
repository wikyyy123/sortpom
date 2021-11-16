/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.dom4j.io;

import org.dom4j.*;
import org.dom4j.tree.NamespaceStack;
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static org.dom4j.util.StringUtils.endsWithWhitespace;
import static org.dom4j.util.StringUtils.startsWithWhitespace;

/**
 * <p>
 * <code>XMLWriter</code> takes a DOM4J tree and formats it to a stream as
 * XML. It can also take SAX events too so can be used by SAX clients as this
 * object implements the {@link org.xml.sax.ContentHandler}and {@link
 * LexicalHandler} interfaces. as well. This formatter performs typical document
 * formatting. The XML declaration and processing instructions are always on
 * their own lines. An {@link OutputFormat}object can be used to define how
 * whitespace is handled when printing and allows various configuration options,
 * such as to allow suppression of the XML declaration, the encoding declaration
 * or whether empty documents are collapsed.
 * </p>
 *
 * <p>
 * There are <code>write(...)</code> methods to print any of the standard
 * DOM4J classes, including <code>Document</code> and <code>Element</code>,
 * to either a <code>Writer</code> or an <code>OutputStream</code>.
 * Warning: using your own <code>Writer</code> may cause the writer's
 * preferred character encoding to be ignored. If you use encodings other than
 * UTF8, we recommend using the method that takes an OutputStream instead.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @author Joseph Bowbeer
 * @version $Revision: 1.83 $
 */
public class XMLWriter extends XMLFilterImpl implements LexicalHandler {
    private static final String PAD_TEXT = " ";

    @SuppressWarnings("HttpUrlsUsage")
    protected static final String[] LEXICAL_HANDLER_NAMES = {
        "http://xml.org/sax/properties/lexical-handler",
        "http://xml.org/sax/handlers/LexicalHandler"};

    /**
     * Stores the last type of node written so algorithms can refer to the
     * previous node type
     */
    protected int lastOutputNodeType;

    /**
     * Stores if the last written element node was a closing tag or an opening
     * tag.
     */
    private boolean lastElementClosed = false;

    /** Stores the xml:space attribute value of preserve for whitespace flag */
    protected boolean preserve = false;

    /** The Writer used to output to */
    protected final Writer writer;

    /** The Stack of namespaceStack written so far */
    private final NamespaceStack namespaceStack = new NamespaceStack();

    /** The format used by this writer */
    private final OutputFormat format;

    /** whether we should escape text */
    private final boolean escapeText = true;

    /**
     * The initial number of indentations (so you can print a whole document
     * indented, if you like)
     */
    private int indentLevel = 0;

    /** buffer used when escaping strings */
    private final StringBuffer buffer = new StringBuffer();

    /**
     * whether we have added characters before from the same chunk of characters
     */
    private boolean charsAdded = false;

    private char lastChar;

    /** Lexical handler we should delegate to */
    private LexicalHandler lexicalHandler;

    /** Is the writer currently inside a DTD definition? */
    private boolean inDTD;

    /** The namespaces used for the current element when consuming SAX events */
    private Map<String, String> namespacesMap;

    /**
     * what is the maximum allowed character code such as 127 in US-ASCII (7
     * bit) or 255 in ISO- (8 bit) or -1 to not escape any characters (other
     * than the special XML characters like &lt; &gt; &amp;)
     */
    private int maximumAllowedCharacter;

    public XMLWriter(Writer writer, OutputFormat format) {
        this.writer = writer;
        this.format = format;
        namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    /**
     * Returns the maximum allowed character code that should be allowed
     * unescaped which defaults to 127 in US-ASCII (7 bit) or 255 in ISO- (8
     * bit).
     *
     * @return DOCUMENT ME!
     */
    public int getMaximumAllowedCharacter() {
        if (maximumAllowedCharacter == 0) {
            maximumAllowedCharacter = defaultMaximumAllowedCharacter();
        }

        return maximumAllowedCharacter;
    }

    /**
     * Writes the new line text to the underlying Writer
     *
     * @throws IOException DOCUMENT ME!
     */
    public void println() throws IOException {
        writer.write(format.getLineSeparator());
    }

    /**
     * <p>
     * This will print the <code>Document</code> to the current Writer.
     * </p>
     *
     * <p>
     * Warning: using your own Writer may cause the writer's preferred character
     * encoding to be ignored. If you use encodings other than UTF8, we
     * recommend using the method that takes an OutputStream instead.
     * </p>
     *
     * <p>
     * Note: as with all Writers, you may need to flush() yours after this
     * method returns.
     * </p>
     *
     * @param doc <code>Document</code> to format.
     * @throws IOException if there's any problem writing.
     */
    public void write(Document doc) throws IOException {
        writeDeclaration();

        if (doc.getDocType() != null) {
            indent();
            writeDocType(doc.getDocType());
        }

        for (int i = 0, size = doc.nodeCount(); i < size; i++) {
            Node node = doc.node(i);
            writeNode(node);
        }

        writePrintln();
    }

    /**
     * Writes the given {@link Text}.
     *
     * @param text <code>Text</code> to output.
     * @throws IOException DOCUMENT ME!
     */
    public void write(Text text) throws IOException {
        writeString(text.getText());
    }

    // XMLFilterImpl methods
    // -------------------------------------------------------------------------
    @Override
    public void parse(InputSource source) throws IOException, SAXException {
        installLexicalHandler();
        super.parse(source);
    }

    @Override
    public void setProperty(String name, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        for (String lexicalHandlerName : LEXICAL_HANDLER_NAMES) {
            if (lexicalHandlerName.equals(name)) {
                setLexicalHandler((LexicalHandler) value);

                return;
            }
        }

        super.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException,
        SAXNotSupportedException {
        for (String lexicalHandlerName : LEXICAL_HANDLER_NAMES) {
            if (lexicalHandlerName.equals(name)) {
                return getLexicalHandler();
            }
        }

        return super.getProperty(name);
    }

    public void setLexicalHandler(LexicalHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null lexical handler");
        } else {
            this.lexicalHandler = handler;
        }
    }

    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            writeDeclaration();
            super.startDocument();
        } catch (IOException e) {
            handleException(e);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
        if (namespacesMap == null) {
            namespacesMap = new HashMap<>();
        }

        namespacesMap.put(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attributes) throws SAXException {
        try {
            charsAdded = false;

            writePrintln();
            indent();
            writer.write("<");
            writer.write(qName);
            writeNamespaces();
            writeAttributes(attributes);
            writer.write(">");
            ++indentLevel;
            lastOutputNodeType = Node.ELEMENT_NODE;
            lastElementClosed = false;

            super.startElement(namespaceURI, localName, qName, attributes);
        } catch (IOException e) {
            handleException(e);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException {
        try {
            charsAdded = false;
            --indentLevel;

            if (lastElementClosed) {
                writePrintln();
                indent();
            }

            writeClose(qName);

            lastOutputNodeType = Node.ELEMENT_NODE;
            lastElementClosed = true;

            super.endElement(namespaceURI, localName, qName);
        } catch (IOException e) {
            handleException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException {
        if ((ch == null) || (ch.length == 0) || (length <= 0)) {
            return;
        }

        try {
            /*
             * we can't use the writeString method here because it's possible we
             * don't receive all characters at once and calling writeString
             * would cause unwanted spaces to be added in between these chunks
             * of character arrays.
             */
            String string = String.valueOf(ch, start, length);

            if (escapeText) {
                string = escapeElementEntities(string);
            }

            if (format.isTrimText()) {
                if ((lastOutputNodeType == Node.TEXT_NODE) && !charsAdded) {
                    writer.write(' ');
                } else if (charsAdded && Character.isWhitespace(lastChar)) {
                    writer.write(' ');
                } else if (lastOutputNodeType == Node.ELEMENT_NODE
                    && format.isPadText() && lastElementClosed
                    && Character.isWhitespace(ch[0])) {
                    writer.write(PAD_TEXT);
                }

                String delim = "";
                StringTokenizer tokens = new StringTokenizer(string);

                while (tokens.hasMoreTokens()) {
                    writer.write(delim);
                    writer.write(tokens.nextToken());
                    delim = " ";
                }
            } else {
                writer.write(string);
            }

            charsAdded = true;
            lastChar = ch[(start + length) - 1];
            lastOutputNodeType = Node.TEXT_NODE;

            super.characters(ch, start, length);
        } catch (IOException e) {
            handleException(e);
        }
    }

    @Override
    public void processingInstruction(String target, String data)
        throws SAXException {
        try {
            indent();
            writer.write("<?");
            writer.write(target);
            writer.write(" ");
            writer.write(data);
            writer.write("?>");
            writePrintln();
            lastOutputNodeType = Node.PROCESSING_INSTRUCTION_NODE;

            super.processingInstruction(target, data);
        } catch (IOException e) {
            handleException(e);
        }
    }

    // LexicalHandler interface
    // -------------------------------------------------------------------------
    @Override
    public void startDTD(String name, String publicID, String systemID)
        throws SAXException {
        inDTD = true;

        try {
            writeDocType(name, publicID, systemID);
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicID, systemID);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        inDTD = false;

        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        try {
            writer.write("<![CDATA[");
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        try {
            writer.write("]]>");
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        try {
            writeEntityRef(name);
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endEntity(name);
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (!inDTD) {
            try {
                charsAdded = false;
                writeComment(new String(ch, start, length));
            } catch (IOException e) {
                handleException(e);
            }
        }

        if (lexicalHandler != null) {
            lexicalHandler.comment(ch, start, length);
        }
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected void writeElement(Element element) throws IOException {
        int size = element.nodeCount();
        String qualifiedName = element.getQualifiedName();

        writePrintln();
        indent();

        writer.write("<");
        writer.write(qualifiedName);

        int previouslyDeclaredNamespaces = namespaceStack.size();
        Namespace ns = element.getNamespace();

        if (isNamespaceDeclaration(ns)) {
            namespaceStack.push(ns);
            writeNamespace(ns);
        }

        // Print out additional namespace declarations
        boolean textOnly = true;

        for (int i = 0; i < size; i++) {
            Node node = element.node(i);

            if (node instanceof Namespace) {
                Namespace additional = (Namespace) node;

                if (isNamespaceDeclaration(additional)) {
                    namespaceStack.push(additional);
                    writeNamespace(additional);
                }
            } else if (node instanceof Element) {
                textOnly = false;
            } else if (node instanceof Comment) {
                textOnly = false;
            }
        }

        writeAttributes(element);

        lastOutputNodeType = Node.ELEMENT_NODE;

        if (size <= 0) {
            writeEmptyElementClose(qualifiedName);
        } else {
            writer.write(">");

            if (textOnly) {
                // we have at least one text node so let's assume
                // that its non-empty
                writeElementContent(element);
            } else {
                // we know it's not null or empty from above
                ++indentLevel;

                writeElementContent(element);

                --indentLevel;

                writePrintln();
                indent();
            }

            writer.write("</");
            writer.write(qualifiedName);
            writer.write(">");
        }

        // remove declared namespaceStack from stack
        while (namespaceStack.size() > previouslyDeclaredNamespaces) {
            namespaceStack.pop();
        }

        lastOutputNodeType = Node.ELEMENT_NODE;
    }

    /**
     * Determines if element is a special case of XML elements where it contains
     * a xml:space attribute of "preserve". If it does, then retain whitespace.
     *
     * @param element DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected final boolean isElementSpacePreserved(Element element) {
        final Attribute attr = element.attribute("space");
        boolean preserveFound = preserve; // default to global state

        if (attr != null) {
            preserveFound = "xml".equals(attr.getNamespacePrefix()) && "preserve".equals(attr.getText());
        }

        return preserveFound;
    }

    /**
     * Outputs the content of the given element. If whitespace trimming is
     * enabled then all adjacent text nodes are appended together before the
     * whitespace trimming occurs to avoid problems with multiple text nodes
     * being created due to text content that spans parser buffers in a SAX
     * parser.
     *
     * @param element DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void writeElementContent(Element element) throws IOException {
        boolean trim = format.isTrimText();
        boolean oldPreserve = preserve;

        if (trim) { // verify we have to before more expensive test
            preserve = isElementSpacePreserved(element);
            trim = !preserve;
        }

        if (trim) {
            // concatenate adjacent text nodes together
            // so that whitespace trimming works properly
            Text lastTextNode = null;
            StringBuilder buff = null;
            boolean textOnly = true;

            for (Node node : element.content()) {
                if (node instanceof Text) {
                    if (lastTextNode == null) {
                        lastTextNode = (Text) node;
                    } else {
                        if (buff == null) {
                            buff = new StringBuilder(lastTextNode.getText());
                        }

                        buff.append((node).getText());
                    }
                } else {
                    if (!textOnly && format.isPadText()) {
                        // only add the PAD_TEXT if the text itself starts with
                        // whitespace
                        final boolean startsWithWhitespace;
                        if (buff != null) {
                            startsWithWhitespace = startsWithWhitespace(buff);
                        } else if (lastTextNode != null) {
                            startsWithWhitespace = startsWithWhitespace(lastTextNode.getText());
                        } else {
                            startsWithWhitespace = false;
                        }

                        if (startsWithWhitespace) {
                            writer.write(PAD_TEXT);
                        }
                    }

                    if (lastTextNode != null) {
                        if (buff != null) {
                            writeString(buff.toString());
                            buff = null;
                        } else {
                            writeString(lastTextNode.getText());
                        }

                        if (format.isPadText()) {
                            // only add the PAD_TEXT if the text itself ends
                            // with whitespace
                            final boolean endsWithWhitespace;
                            endsWithWhitespace = endsWithWhitespace(lastTextNode.getText());

                            if (endsWithWhitespace) {
                                writer.write(PAD_TEXT);
                            }
                        }

                        lastTextNode = null;
                    }

                    textOnly = false;
                    writeNode(node);
                }
            }

            if (lastTextNode != null) {
                if (!textOnly && format.isPadText()) {
                    // only add the PAD_TEXT if the text itself starts with
                    // whitespace
                    final boolean startsWithWhitespace;
                    if (buff != null) {
                        startsWithWhitespace = startsWithWhitespace(buff);
                    } else {
                        startsWithWhitespace = startsWithWhitespace(lastTextNode.getText());
                    }

                    if (startsWithWhitespace) {
                        writer.write(PAD_TEXT);
                    }
                }

                if (buff != null) {
                    writeString(buff.toString());
                } else {
                    writeString(lastTextNode.getText());
                }

            }
        } else {
            Node lastTextNode = null;

            for (Node node : element.content()) {
                if (node instanceof Text) {
                    writeNode(node);
                    lastTextNode = node;
                } else {
                    if ((lastTextNode != null) && format.isPadText()) {
                        // only add the PAD_TEXT if the text itself ends with
                        // whitespace
                        if (endsWithWhitespace(lastTextNode.getText())) {
                            writer.write(PAD_TEXT);
                        }
                    }

                    writeNode(node);

                    lastTextNode = null;
                }
            }
        }

        preserve = oldPreserve;
    }

    protected void writeCDATA(String text) throws IOException {
        writer.write("<![CDATA[");

        if (text != null) {
            writer.write(text);
        }

        writer.write("]]>");

        lastOutputNodeType = Node.CDATA_SECTION_NODE;
    }

    protected void writeDocType(DocumentType docType) throws IOException {
        if (docType != null) {
            docType.write(writer);
            writePrintln();
        }
    }

    protected void writeNamespace(Namespace namespace) throws IOException {
        if (namespace != null) {
            writeNamespace(namespace.getPrefix(), namespace.getURI());
        }
    }

    /**
     * Writes the SAX namespaces
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeNamespaces() throws IOException {
        if (namespacesMap != null) {
            for (Map.Entry<String, String> entry : namespacesMap.entrySet()) {
                String prefix = entry.getKey();
                String uri = entry.getValue();
                writeNamespace(prefix, uri);
            }

            namespacesMap = null;
        }
    }

    /**
     * Writes the SAX namespaces
     *
     * @param prefix the prefix
     * @param uri    the namespace uri
     * @throws IOException DOCUMENT ME!
     */
    protected void writeNamespace(String prefix, String uri)
        throws IOException {
        if ((prefix != null) && (prefix.length() > 0)) {
            writer.write(" xmlns:");
            writer.write(prefix);
            writer.write("=\"");
        } else {
            writer.write(" xmlns=\"");
        }

        writer.write(uri);
        writer.write("\"");
    }

    protected void writeProcessingInstruction(ProcessingInstruction pi)
        throws IOException {
        // indent();
        writer.write("<?");
        writer.write(pi.getName());
        writer.write(" ");
        writer.write(pi.getText());
        writer.write("?>");
        writePrintln();

        lastOutputNodeType = Node.PROCESSING_INSTRUCTION_NODE;
    }

    protected void writeString(String text) throws IOException {
        if ((text != null) && (text.length() > 0)) {
            if (escapeText) {
                text = escapeElementEntities(text);
            }

            if (format.isTrimText()) {
                boolean first = true;
                StringTokenizer tokenizer = new StringTokenizer(text);

                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    if (first) {
                        first = false;

                        if (lastOutputNodeType == Node.TEXT_NODE) {
                            writer.write(" ");
                        }
                    } else {
                        writer.write(" ");
                    }

                    writer.write(token);
                    lastOutputNodeType = Node.TEXT_NODE;
                }
            } else {
                lastOutputNodeType = Node.TEXT_NODE;
                writer.write(text);
            }
        }
    }

    /**
     * This method is used to write out Nodes that contain text and still allow
     * for xml:space to be handled properly.
     *
     * @param node DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void writeNodeText(Node node) throws IOException {
        String text = node.getText();

        if ((text != null) && (text.length() > 0)) {
            if (escapeText) {
                text = escapeElementEntities(text);
            }

            lastOutputNodeType = Node.TEXT_NODE;
            writer.write(text);
        }
    }

    protected void writeNode(Node node) throws IOException {
        int nodeType = node.getNodeType();

        switch (nodeType) {
            case Node.ELEMENT_NODE:
                writeElement((Element) node);

                break;

            case Node.ATTRIBUTE_NODE:
                writeAttribute((Attribute) node);

                break;

            case Node.TEXT_NODE:
                writeNodeText(node);

                // write((Text) node);
                break;

            case Node.CDATA_SECTION_NODE:
                writeCDATA(node.getText());

                break;

            case Node.ENTITY_REFERENCE_NODE:
                writeEntity((Entity) node);

                break;

            case Node.PROCESSING_INSTRUCTION_NODE:
                writeProcessingInstruction((ProcessingInstruction) node);

                break;

            case Node.COMMENT_NODE:
                writeComment(node.getText());

                break;

            case Node.DOCUMENT_NODE:
                write((Document) node);

                break;

            case Node.DOCUMENT_TYPE_NODE:
                writeDocType((DocumentType) node);

                break;

            case Node.NAMESPACE_NODE:

                // Will be output with attributes
                // write((Namespace) node);
                break;

            default:
                throw new IOException("Invalid node type: " + node);
        }
    }

    protected void installLexicalHandler() {
        XMLReader parent = getParent();

        if (parent == null) {
            throw new NullPointerException("No parent for filter");
        }

        // try to register for lexical events
        for (String lexicalHandlerName : LEXICAL_HANDLER_NAMES) {
            try {
                parent.setProperty(lexicalHandlerName, this);

                break;
            } catch (SAXNotRecognizedException | SAXNotSupportedException ex) {
                // ignore
            }
        }
    }

    protected void writeDocType(String name, String publicID, String systemID)
        throws IOException {
        boolean hasPublic = false;

        writer.write("<!DOCTYPE ");
        writer.write(name);

        if ((publicID != null) && (!publicID.equals(""))) {
            writer.write(" PUBLIC \"");
            writer.write(publicID);
            writer.write("\"");
            hasPublic = true;
        }

        if ((systemID != null) && (!systemID.equals(""))) {
            if (!hasPublic) {
                writer.write(" SYSTEM");
            }

            writer.write(" \"");
            writer.write(systemID);
            writer.write("\"");
        }

        writer.write(">");
        writePrintln();
    }

    protected void writeEntity(Entity entity) throws IOException {
        /* Should entityRefs by resolved when writing ? */
        writer.write(entity.getText());
    }

    protected void writeEntityRef(String name) throws IOException {
        writer.write("&");
        writer.write(name);
        writer.write(";");

        lastOutputNodeType = Node.ENTITY_REFERENCE_NODE;
    }

    protected void writeComment(String text) throws IOException {
        if (format.isNewlines()) {
            println();
            indent();
        }

        writer.write("<!--");
        writer.write(text);
        writer.write("-->");

        lastOutputNodeType = Node.COMMENT_NODE;
    }

    /**
     * Writes the attributes of the given element
     *
     * @param element DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    protected void writeAttributes(Element element) throws IOException {
        // I do not yet handle the case where the same prefix maps to
        // two different URIs. For attributes on the same element
        // this is illegal; but as yet we don't throw an exception
        // if someone tries to do this
        for (int i = 0, size = element.attributeCount(); i < size; i++) {
            Attribute attribute = element.attribute(i);
            Namespace ns = attribute.getNamespace();

            if ((ns != null) && (ns != Namespace.NO_NAMESPACE)
                && (ns != Namespace.XML_NAMESPACE)) {
                String prefix = ns.getPrefix();
                String uri = namespaceStack.getURI(prefix);

                if (!ns.getURI().equals(uri)) {
                    writeNamespace(ns);
                    namespaceStack.push(ns);
                }
            }

            // If the attribute is a namespace declaration, check if we have
            // already written that declaration elsewhere (if that's the case,
            // it must be in the namespace stack
            String attName = attribute.getName();

            if (attName.startsWith("xmlns:")) {
                String prefix = attName.substring(6);

                if (namespaceStack.getNamespaceForPrefix(prefix) == null) {
                    String uri = attribute.getValue();
                    namespaceStack.push(prefix, uri);
                    writeNamespace(prefix, uri);
                }
            } else if (attName.equals("xmlns")) {
                if (namespaceStack.getDefaultNamespace() == null) {
                    String uri = attribute.getValue();
                    namespaceStack.push(null, uri);
                    writeNamespace(null, uri);
                }
            } else {
                writeAttribute(attribute);
            }
        }
    }

    protected void writeAttribute(Attribute attribute) throws IOException {
        writer.write(" ");
        writer.write(attribute.getQualifiedName());
        writer.write("=");

        char quote = format.getAttributeQuoteCharacter();
        writer.write(quote);

        writeEscapeAttributeEntities(attribute.getValue());

        writer.write(quote);
        lastOutputNodeType = Node.ATTRIBUTE_NODE;
    }

    protected void writeAttributes(Attributes attributes) throws IOException {
        for (int i = 0, size = attributes.getLength(); i < size; i++) {
            writeAttribute(attributes, i);
        }
    }

    protected void writeAttribute(Attributes attributes, int index)
        throws IOException {
        char quote = format.getAttributeQuoteCharacter();
        writer.write(" ");
        writer.write(attributes.getQName(index));
        writer.write("=");
        writer.write(quote);
        writeEscapeAttributeEntities(attributes.getValue(index));
        writer.write(quote);
    }

    protected void indent() throws IOException {
        String indent = format.getIndent();

        if ((indent != null) && (indent.length() > 0)) {
            for (int i = 0; i < indentLevel; i++) {
                writer.write(indent);
            }
        }
    }

    /**
     * <p>
     * This will print a new line only if the newlines flag was set to true
     * </p>
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writePrintln() throws IOException {
        if (format.isNewlines()) {
            writer.write(format.getLineSeparator());
        }
    }

    /**
     * <p>
     * This will write the declaration to the given Writer. Assumes XML version
     * 1.0 since we don't directly know.
     * </p>
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeDeclaration() throws IOException {
        String encoding = format.getEncoding();

        // Only print of declaration is not suppressed
        if (!format.isSuppressDeclaration()) {
            // Assume 1.0 version
            if (encoding.equals("UTF8")) {
                writer.write("<?xml version=\"1.0\"");

                if (!format.isOmitEncoding()) {
                    writer.write(" encoding=\"UTF-8\"");
                }

                writer.write("?>");
            } else {
                writer.write("<?xml version=\"1.0\"");

                if (!format.isOmitEncoding()) {
                    writer.write(" encoding=\"" + encoding + "\"");
                }

                writer.write("?>");
            }

            if (format.isNewLineAfterDeclaration()) {
                println();
            }
        }
    }

    protected void writeClose(String qualifiedName) throws IOException {
        writer.write("</");
        writer.write(qualifiedName);
        writer.write(">");
    }

    protected void writeEmptyElementClose(String qualifiedName)
        throws IOException {
        // Simply close up
        if (!format.isExpandEmptyElements()) {
            writer.write("/>");
        } else {
            writer.write("></");
            writer.write(qualifiedName);
            writer.write(">");
        }
    }

    /**
     * This will take the pre-defined entities in XML 1.0 and convert their
     * character representation to the appropriate entity reference, suitable
     * for XML attributes.
     *
     * @param text DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected String escapeElementEntities(String text) {
        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;

            int c = text.codePointAt(i);
            switch (c) {
                case '<':
                    entity = "&lt;";
                    break;
                case '>':
                    entity = "&gt;";
                    break;
                case '&':
                    entity = "&amp;";
                    break;
                case '\t':
                case '\n':
                case '\r':
                    // don't encode standard whitespace characters
                    if (preserve) {
                        entity = String.valueOf((char) c);
                    }
                    break;

                default:

                    if ((c < 32) || shouldEncodeChar(c)) {
                        entity = "&#" + c + ";";
                    }

                    break;
            }


            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
                if (Character.isSupplementaryCodePoint(c)) {
                    last++;
                }
            }
            if (Character.isSupplementaryCodePoint(c)) {
                i++;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }

    protected void writeEscapeAttributeEntities(String txt) throws IOException {
        if (txt != null) {
            String escapedText = escapeAttributeEntities(txt);
            writer.write(escapedText);
        }
    }

    /**
     * This will take the pre-defined entities in XML 1.0 and convert their
     * character representation to the appropriate entity reference, suitable
     * for XML attributes.
     *
     * @param text DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected String escapeAttributeEntities(String text) {
        char quote = format.getAttributeQuoteCharacter();

        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            int c = text.codePointAt(i);

            switch (c) {
                case '<':
                    entity = "&lt;";
                    break;
                case '>':
                    entity = "&gt;";
                    break;
                case '\'':
                    if (quote == '\'') {
                        entity = "&apos;";
                    }
                    break;
                case '\"':
                    if (quote == '\"') {
                        entity = "&quot;";
                    }
                    break;
                case '&':
                    entity = "&amp;";
                    break;
                case '\t':
                case '\n':
                case '\r':
                    // don't encode standard whitespace characters
                    break;
                default:
                    if ((c < 32) || shouldEncodeChar(c)) {
                        entity = "&#" + c + ";";
                    }
                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
                if (Character.isSupplementaryCodePoint(c)) {
                    last++;
                }
            }
            if (Character.isSupplementaryCodePoint(c)) {
                i++;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }

    /**
     * Should the given character be escaped. This depends on the encoding of
     * the document.
     *
     * @param codepoint Unicode codepoint.
     *                  DOCUMENT ME!
     * @return boolean
     */
    protected boolean shouldEncodeChar(int codepoint) {
        int max = getMaximumAllowedCharacter();

        return (max > 0) && (codepoint > max);
    }

    /**
     * Returns the maximum allowed character code that should be allowed
     * unescaped which defaults to 127 in US-ASCII (7 bit) or 255 in ISO- (8
     * bit).
     *
     * @return DOCUMENT ME!
     */
    protected int defaultMaximumAllowedCharacter() {
        String encoding = format.getEncoding();

        if (encoding != null) {
            if (encoding.equals("US-ASCII")) {
                return 127;
            }
        }

        // no encoding for things like ISO-*, UTF-8 or UTF-16
        return -1;
    }

    protected boolean isNamespaceDeclaration(Namespace ns) {
        if ((ns != null) && (ns != Namespace.XML_NAMESPACE)) {
            String uri = ns.getURI();

            if (uri != null) {
                return !namespaceStack.contains(ns);
            }
        }

        return false;
    }

    protected void handleException(IOException e) throws SAXException {
        throw new SAXException(e);
    }

    // Laramie Crocker 4/8/2002 10:38AM

}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 *
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 *
 * 5. Due credit should be given to the DOM4J Project - http://www.dom4j.org
 *
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
