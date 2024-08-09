package io.apicurio.registry.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SchemaUtils {

    public static Set<String> getAttributesFromString(String schemaContent) throws Exception {
        Set<String> attributes = new HashSet<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(schemaContent.getBytes()));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("xs".equals(prefix)) {
                    return "http://www.w3.org/2001/XMLSchema";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                    return "xs";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        });

        XPathExpression expr = xPath.compile("//xs:complexType");
        NodeList elements = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < elements.getLength(); i++) {
            Node complexType = elements.item(i);
            NodeList attributesList = ((Element) complexType).getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema",
                    "attribute");

            for (int j = 0; j < attributesList.getLength(); j++) {
                Node attribute = attributesList.item(j);
                String name = attribute.getAttributes().getNamedItem("name").getNodeValue();
                String use = attribute.getAttributes().getNamedItem("use") != null
                        ? attribute.getAttributes().getNamedItem("use").getNodeValue()
                        : "optional";
                attributes.add(name + ":" + use);
            }
        }
        return attributes;
    }

    public static Set<String> getElementsFromString(String schemaContent) throws Exception {
        Set<String> elements = new HashSet<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(schemaContent.getBytes()));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("xs".equals(prefix)) {
                    return "http://www.w3.org/2001/XMLSchema";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                    return "xs";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        });

        XPathExpression expr = xPath.compile("//xs:complexType/xs:sequence/xs:element");
        NodeList elementNodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < elementNodes.getLength(); i++) {
            Node elementNode = elementNodes.item(i);
            String name = elementNode.getAttributes().getNamedItem("name") != null
                    ? elementNode.getAttributes().getNamedItem("name").getNodeValue()
                    : elementNode.getAttributes().getNamedItem("ref").getNodeValue();
            String minOccurs = elementNode.getAttributes().getNamedItem("minOccurs") != null
                    ? elementNode.getAttributes().getNamedItem("minOccurs").getNodeValue()
                    : "1";
            elements.add(name + ":" + minOccurs);
        }

        return elements;
    }
}
