package com.n0dwis.Evernix.service;

import com.n0dwis.Evernix.model.NoteInfo;
import com.n0dwis.Evernix.model.NotebookInfo;
import com.n0dwis.Evernix.model.LocalStorage;
import com.n0dwis.Evernix.model.Synchronizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StoreConfig {

    final private static SimpleDateFormat df = new SimpleDateFormat("dd.MM.YYYY HH:mm:s");
    private static StoreConfig instance = null;

    public static StoreConfig getInstance() {
        if (instance == null) {
            instance = new StoreConfig();
        }

        return instance;
    }

    private File baseDirectory;
    private String token;
    private ArrayList<NotebookInfo> localNotebooks = new ArrayList<NotebookInfo>();

    public Synchronizer create(File baseDirectory, String token) {
        this.baseDirectory = baseDirectory;
        this.token = token;
        return new Synchronizer(new LocalStorage(baseDirectory, localNotebooks), token);
    }

    public Synchronizer load(File baseDirectory) {
        this.baseDirectory = baseDirectory;

        Document doc;
        try {
            doc = makeBuilder().parse(makeConfigFile());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error reading config", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading config", e);
        } catch (SAXException e) {
            throw new RuntimeException("Error reading config", e);
        }

        token = doc.getElementsByTagName("token").item(0).getTextContent();

        NodeList notebooksXml = doc.getElementsByTagName("notebook");
        for (int i = 0; i < notebooksXml.getLength(); i++) {
            Element notebookElement = (Element) notebooksXml.item(i);
            NotebookInfo notebook = new NotebookInfo(notebookElement.getAttribute("id"), notebookElement.getTextContent());
            localNotebooks.add(notebook);
            NodeList notes = notebookElement.getElementsByTagName("note");
            for (int j = 0; j < notes.getLength(); j++) {
                Element noteElement = (Element) notes.item(j);
                try {
                    NoteInfo ni = new NoteInfo(
                            noteElement.getAttribute("id"),
                            noteElement.getTextContent(),
                            df.parse(noteElement.getAttribute("lastSync")));
                    notebook.addNote(ni);

                } catch (ParseException e) {
                    throw new RuntimeException("Wrong date format " + noteElement.getAttribute("lastSync"), e);
                }
            }
        }

        return new Synchronizer(new LocalStorage(baseDirectory, localNotebooks), token);
    }

    public void save() {
        Document doc;
        try {
            doc = makeBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error reading config", e);
        }

        Element root = doc.createElement("evernix");
        doc.appendChild(root);

        Element tokenElem = doc.createElement("token");
        root.appendChild(tokenElem);
        tokenElem.setTextContent(token);

        for (NotebookInfo notebookInfo : localNotebooks) {
            Element notebookElem = doc.createElement("notebook");
            root.appendChild(notebookElem);
            notebookElem.setTextContent(notebookInfo.getName());
            notebookElem.setAttribute("id", notebookInfo.getId());

            for (NoteInfo noteInfo : notebookInfo.getNotes()) {
                Element noteElem = doc.createElement("note");
                notebookElem.appendChild(noteElem);
                noteElem.setTextContent(noteInfo.getName());
                noteElem.setAttribute("id", noteInfo.getId());
                noteElem.setAttribute("lastSync", df.format(noteInfo.getLastSync()));
            }
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(makeConfigFile());
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Error writing config", e);
        } catch (TransformerException e) {
            throw new RuntimeException("Error writing config", e);
        }
    }

    private File makeConfigFile() {
        return new File(baseDirectory.getAbsolutePath() + "/.db");
    }

    private DocumentBuilder makeBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        return docFactory.newDocumentBuilder();
    }

    public File getBaseDirectory() {
        return baseDirectory;
    }
}
