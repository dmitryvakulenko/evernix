package com.n0dwis.Evernix.utils;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * Created by n0dwis on 15.09.15.
 */
public class NoteFormatConverter {

    private DocumentBuilder builder;
    private Logger logger;

    public NoteFormatConverter() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            builder = factory.newDocumentBuilder();
            logger = Logger.getLogger(getClass().getName());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Can't create parser", e);
        }
    }

    public String fromEvernoteToLocal(String evernoteNoteContent) throws UserErrorException {
//        URL dtdFile = getClass().getResource("/enml2.dtd");
//        logger.info("Validating note from " + dtdFile);
        String replaced = evernoteNoteContent;
        try {
            Document doc = builder.parse(new ByteArrayInputStream(replaced.getBytes()));
            return parseElement(doc.getDocumentElement());
        } catch (SAXException e) {
            throw new UserErrorException("Evernote note format error. Can't parse.", e);
        } catch (IOException e) {
            throw new UserErrorException("Can't read note.", e);
        }
    }


    private String parseElement(Node element) {
        String result = "";
        if (element.hasChildNodes()) {
            NodeList content = element.getChildNodes();
            for (int i = 0; i < content.getLength(); i++) {
                Node curNode = content.item(i);
                result += addMarkdown(curNode, parseElement(curNode));
            }
        } else {
            // only Text nodes
            result = element.getTextContent();
        }

        return result;
    }


    private String addMarkdown(Node node, String content) {
        String nodeName = node.getNodeName();
        if (nodeName.equals("strong")) {
            return "*" + content + "*";
        }

        if (nodeName.equals("h1")) {
            return "* " + content + "\n";
        }

        if (nodeName.equals("h2")) {
            return "** " + content + "\n";
        }

        if (nodeName.equals("h3")) {
            return "*** " + content + "\n";
        }

        if (nodeName.equals("a")) {
            return "[[" + node.getAttributes().getNamedItem("href").getTextContent() + "][" + content + "]]";
        }

        return content;
    }


    public String fromLocalToEvernote(String localNoteContent) throws SystemException {
        Document doc = builder.newDocument();
        doc.setXmlVersion("1.0");
        doc.setXmlStandalone(true);
        Element root = doc.createElement("en-note");
        doc.appendChild(root);

        for (String curLocalString : localNoteContent.split("\n")) {
            createEvernoteElement(curLocalString, root);
        }

        try {
            Transformer xformer = TransformerFactory.newInstance().newTransformer();

            DocumentType docType = builder.getDOMImplementation().createDocumentType("en-note", "SYSTEM", "http://xml.evernote.com/pub/enml2.dtd");
            xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());

            Source source = new DOMSource(doc);
            StringWriter result = new StringWriter();
            StreamResult streamResult = new StreamResult(result);
            xformer.transform(source, streamResult);
            return result.toString();
        } catch (TransformerConfigurationException e) {
            throw new SystemException("Can't create transformer", e);
        } catch (TransformerException e) {
            throw new SystemException("Can't transform note", e);
        }
    }

    private void createEvernoteElement(String localString, Element parent) {
        Document doc = parent.getOwnerDocument();
        if (localString.startsWith("* ")) {
            Element h1 = doc.createElement("h1");
            parent.appendChild(h1);
            createEvernoteElement(localString.substring(2), h1);
            return;
        }

        if (localString.startsWith("** ")) {
            Element h2 = doc.createElement("h2");
            parent.appendChild(h2);
            createEvernoteElement(localString.substring(3), h2);
            return;
        }

        if (localString.startsWith("*** ")) {
            Element h3 = doc.createElement("h3");
            parent.appendChild(h3);
            createEvernoteElement(localString.substring(4), h3);
            return;
        }

        Text text = doc.createTextNode(localString);
        parent.appendChild(text);
    }
}
