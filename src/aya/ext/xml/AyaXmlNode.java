package aya.ext.xml;

import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.ListCollector;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.util.Casting;
import aya.util.DictReader;
import aya.util.Sym;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AyaXmlNode {
    private static final Symbol SYM_META = Sym.sym("meta");
    private static final Symbol SYM_META_TYPE = Sym.sym("meta_type");
    private static final Symbol SYM_NAMESPACE = Sym.sym("ns");
    private static final Symbol SYM_NAME = Sym.sym("name");
    private static final Symbol SYM_ATTRIBUTES = Sym.sym("attributes");
    private static final Symbol SYM_CONTENT = Sym.sym("content");
    private static final Symbol SYM_VALUE = Sym.sym("value");

    public static String getDocString(String padLeft) {
        return ("dict\n"
                + padLeft + "meta::bool : (optional) true if this is a meta node\n"
                + padLeft + "meta_type::num : (optional) only if meta=1 (cdata=4, entityReference=5, processingInstruction=7, comment=8)\n"
                + padLeft + "ns::str : (optional) the namespace prefix\n"
                + padLeft + "name::str : the tag name (without namespace)\n"
                + padLeft + "attributes::list (item::dict\n"
                + padLeft + "  ns::str\n"
                + padLeft + "  name::str\n"
                + padLeft + "  value::str\n"
                + padLeft + ") : attributes of this node\n"
                + padLeft + "content::list (item::dict or str) : child nodes and or plaintext\n"
        );
    }

    public static AyaXmlNode fromString(String xmlStr) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlStr)));
            return new AyaXmlNode(document.getDocumentElement());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            StringWriter traceWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(traceWriter));
            throw new ValueError("invalid xml String.\n\texception: " + e.getMessage() + "\n\txmlStr: " + xmlStr + "\n\ttrace: " + traceWriter);
        }
    }

    public final boolean isMetaNode;
    public final Short metaType;
    public final AyaNsName name;
    public final List<AyaXmlAttribute> attributes = new ArrayList<>();
    public final List<Union<String, AyaXmlNode>> content = new ArrayList<>();

    public AyaXmlNode(DictReader d) {
        isMetaNode = d.getBool(SYM_META, false);
        metaType = d.hasKey(SYM_META_TYPE) ? (short) d.getInt(SYM_META_TYPE, 0) : null;
        name = new AyaNsName(d);

        aya.obj.list.List attrList = d.getList(SYM_ATTRIBUTES);
        if (attrList != null) {
            for (int i = 0; i < attrList.length(); i++) {
                Obj attribute = attrList.getExact(i);
                String errName = d.get_err_name() + "." + SYM_ATTRIBUTES.name() + ".[" + i + "]";
                if (attribute == null) {
                    throw new ValueError(errName + " is null");
                } else if (!attribute.isa(Obj.DICT)) {
                    throw new ValueError(errName + " is not a Dict");
                }

                attributes.add(new AyaXmlAttribute(new DictReader(Casting.asDict(attribute), errName)));
            }
        }

        aya.obj.list.List contentList = d.getList(SYM_CONTENT);
        if (contentList != null) {
            for (int i = 0; i < contentList.length(); i++) {
                Obj content = contentList.getExact(i);
                String errName = d.get_err_name() + "." + SYM_CONTENT.name() + ".[" + i + "]";
                if (content == null) {
                    throw new ValueError(errName + " is null");
                }

                if (content.isa(Obj.STR)) {
                    this.content.add(Union.ofT1(content.str()));
                } else if (content.isa(Obj.DICT)) {
                    this.content.add(Union.ofT2(new AyaXmlNode(new DictReader(Casting.asDict(content), errName))));
                } else {
                    throw new ValueError(errName + " is neither a String nor a Dict");
                }
            }
        }
    }

    public AyaXmlNode(Node xmlNode) {
        if (xmlNode.getNodeType() == Node.ELEMENT_NODE) {
            isMetaNode = false;
            metaType = null;
            name = new AyaNsName(xmlNode);

            NamedNodeMap attributeMap = xmlNode.getAttributes();
            if (attributeMap != null) {
                for (int i = 0; i < attributeMap.getLength(); i++) {
                    Node attribute = attributeMap.item(i);
                    attributes.add(new AyaXmlAttribute(attribute));
                }
            }

            NodeList children = xmlNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    String textContent = child.getNodeValue();
                    if (textContent != null) {
                        content.add(Union.ofT1(textContent));
                    }
                } else {
                    content.add(Union.ofT2(new AyaXmlNode(child)));
                }
            }
        } else {
            isMetaNode = true;
            metaType = xmlNode.getNodeType();
            name = new AyaNsName(xmlNode);
            String value = xmlNode.getNodeValue();
            if (value != null) {
                content.add(Union.ofT1(value));
            }
        }
    }

    public Dict toDict() {
        Dict d = new Dict();
        d.set(SYM_META, isMetaNode ? Num.ONE : Num.ZERO);
        if (metaType != null) {
            d.set(SYM_META_TYPE, Num.fromInt(metaType));
        }
        name.writeToDict(d);
        d.set(SYM_ATTRIBUTES, attributes.stream().map(AyaXmlAttribute::toDict).collect(new ListCollector()));
        d.set(SYM_CONTENT, content.stream()
                .map(union -> union.t1 != null ? aya.obj.list.List.fromString(union.t1) : union.t2.toDict())
                .collect(new ListCollector()));
        return d;
    }

    public Node toXmlNode(Document doc) {
        if (isMetaNode) {
            switch (metaType) {
                case Node.CDATA_SECTION_NODE:
                    return doc.createCDATASection(content.stream().map(x -> x.t1).findFirst().orElse(null));
                case Node.ENTITY_REFERENCE_NODE:
                    return doc.createEntityReference(name.name);
                case Node.PROCESSING_INSTRUCTION_NODE:
                    return doc.createProcessingInstruction(name.name, content.stream().map(x -> x.t1).findFirst().orElse(null));
                case Node.COMMENT_NODE:
                    return doc.createComment(content.stream().map(x -> x.t1).findFirst().orElse(null));
                default:
                    System.err.println("dropping unsupported meta type: " + metaType);
                    return null;
            }
        } else {
            Element node = doc.createElement(name.getQualifiedName());
            for (AyaXmlAttribute attribute : attributes) {
                node.setAttribute(attribute.name.getQualifiedName(), attribute.value);
            }
            for (Union<String, AyaXmlNode> child : content) {
                if (child.t1 != null) {
                    node.appendChild(doc.createTextNode(child.t1));
                } else {
                    Node childNode = child.t2.toXmlNode(doc);
                    if (childNode != null) {
                        node.appendChild(childNode);
                    }
                }
            }
            return node;
        }
    }

    public String toXmlString() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, Charset.defaultCharset().name());
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Node xmlNode = toXmlNode(doc);

            // HACK to disable namespace validation
            Set<String> namespacesPrefixes = new HashSet<>();
            collectNamespacePrefixes(namespacesPrefixes);
            Element nsRoot = doc.createElement("nsRoot");
            for (String nsPrefix : namespacesPrefixes) {
                nsRoot.setAttribute("xmlns:" + nsPrefix, nsPrefix);
            }
            nsRoot.appendChild(xmlNode);

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(nsRoot), new StreamResult(writer));
            String xmlString = writer.toString();
            Pattern nsRootPattern = Pattern.compile("^<nsRoot[^>]*?>(.*?)</nsRoot>$", Pattern.DOTALL);
            Matcher nsRootMatcher = nsRootPattern.matcher(xmlString);
            if (!nsRootMatcher.matches())
                throw new RuntimeException("assertion error. nsRootPattern does not match string: " + xmlString);
            xmlString = nsRootMatcher.replaceFirst("$1");
            return xmlString;
        } catch (TransformerException | ParserConfigurationException e) {
            StringWriter traceWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(traceWriter));
            throw new ValueError("cannot write Xml Obj to String.\n\texception: " + e.getMessage() + "\n\ttrace: " + traceWriter);
        }
    }

    private void collectNamespacePrefixes(Set<String> namespacePrefixes) {
        namespacePrefixes.add(this.name.namespace);
        for (AyaXmlAttribute attribute : this.attributes) {
            namespacePrefixes.add(attribute.name.namespace);
        }
        for (Union<String, AyaXmlNode> contentNode : this.content) {
            if (contentNode.t2 != null) {
                contentNode.t2.collectNamespacePrefixes(namespacePrefixes);
            }
        }
    }

    public static class AyaNsName {
        public final String namespace;
        public final String name;

        public AyaNsName(DictReader d) {
            this.namespace = d.getString(SYM_NAMESPACE);
            this.name = d.getString(SYM_NAME);
        }

        public AyaNsName(Node node) {
            String qualifiedName = node.getNodeName();
            int nsSepIdx = qualifiedName.indexOf(':');
            if (nsSepIdx < 0) {
                this.namespace = null;
                this.name = qualifiedName;
            } else {
                this.namespace = qualifiedName.substring(0, nsSepIdx);
                this.name = qualifiedName.substring(nsSepIdx + 1);
            }
        }

        public String getQualifiedName() {
            return namespace == null ? name : (namespace + ":" + name);
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AyaNsName)) return false;

            AyaNsName ayaNsName = (AyaNsName) o;
            return Objects.equals(namespace, ayaNsName.namespace) && Objects.equals(name, ayaNsName.name);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(namespace);
            result = 31 * result + Objects.hashCode(name);
            return result;
        }

        private void writeToDict(Dict d) {
            if (namespace != null) {
                d.set(SYM_NAMESPACE, aya.obj.list.List.fromString(namespace));
            }
            if (name != null) {
                d.set(SYM_NAME, aya.obj.list.List.fromString(name));
            }
        }
    }

    public static class AyaXmlAttribute {
        public final AyaNsName name;
        public final String value;

        public AyaXmlAttribute(Node node) {
            this.name = new AyaNsName(node);
            this.value = node.getNodeValue();
        }

        public AyaXmlAttribute(DictReader d) {
            this.name = new AyaNsName(d);
            this.value = d.getString(SYM_VALUE);
        }

        public Dict toDict() {
            Dict d = new Dict();
            name.writeToDict(d);
            d.set(SYM_VALUE, aya.obj.list.List.fromString(value));
            return d;
        }
    }

    /**
     * A Union type that contains either {@link T1} or {@link T2}
     * @param <T1>
     * @param <T2>
     */
    public static class Union<T1, T2> {
        public final T1 t1;
        public final T2 t2;

        public static <T1, T2> Union<T1, T2> ofT1(T1 t1) {
            return new Union<>(t1, null);
        }

        public static <T1, T2> Union<T1, T2> ofT2(T2 t2) {
            return new Union<>(null, t2);
        }

        private Union(T1 t1, T2 t2) {
            this.t1 = t1;
            this.t2 = t2;
        }
    }
}
