package aya.ext.xml;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.util.Casting;
import aya.util.DictReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToXmlInstruction extends NamedOperator {
	private static final Symbol SYM_null = SymbolTable.getSymbol("null");

	public ToXmlInstruction() {
		super("xml.dumps");
		_doc = "data::dict opts::dict -> xml::str : Convert a dict to an xml string\n"
				+ "  opts::dict : {\n"
				+ "    pretty::bool : default=0 ; enable/disable pretty printing\n"
				+ "    xml_decl::bool : default=1 ; enable/disable printing the xml declaration\n"
				+ "    encoding::str : default=encoding of the JVM\n"
				+ "    attr_prefix::str : default=\"@\" ; the prefix used to identify which keys should become attributes instead of nested nodes\n"
				+ "    cdata_key::str : default=\"#text\" ; the key that identifies text-data\n"
				+ "  }\n";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj opts = blockEvaluator.pop();
		final Obj data = blockEvaluator.pop();

		if (!opts.isa(Obj.DICT)) {
			throw new TypeError(this, "opts::dict", opts);
		}
		if (!data.isa(Obj.DICT)) {
			throw new TypeError(this, "data::dict", opts);
		}

		DumpOpts dumpOpts = new DumpOpts(new DictReader(Casting.asDict(opts), "opts"));
		String xmlStr = dictToXmlStr(dumpOpts, Casting.asDict(data));
		blockEvaluator.push(List.fromString(xmlStr));
	}

	private static String dictToXmlStr(DumpOpts dumpOpts, Dict d) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, dumpOpts.xml_decl ? "no" : "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, dumpOpts.pretty ? "yes" : "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, dumpOpts.encoding.name());

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true); // https://stackoverflow.com/questions/1682796/omitting-the-standalone-attribute-in-xml-declaration-when-using-java-dom-trans
			Node rootNode = dictToXml(dumpOpts, d, doc, null);
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(rootNode), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerException | ParserConfigurationException e) {
			StringWriter traceWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(traceWriter));
			throw new ValueError("cannot write xml obj to string.\n\texception: " + e.getMessage() + "\n\ttrace: " + traceWriter);
		}
	}

	/**
	 * @return if parent is null: a suitable root node ; otherwise null.
	 */
	private static Node dictToXml(DumpOpts dumpOpts, Dict d, Document doc, Element parent) {
		Node result = null;
		for (Symbol key : d.keys()) {
			String keyStr = SymbolTable.getName(key);
			if (dumpOpts.cdata_key.equals(keyStr)) {
				if (parent == null) {
					throw new ValueError("text-data cannot exist at the root level of an xml document.");
				}
				// #text may be ::str or ::[str]list
				Stream.of(d.get(key))
						.flatMap(o -> o.isa(Obj.STR) ? Stream.of(o) : Casting.asList(o).stream())
						.forEach(textObj -> {
							parent.appendChild(doc.createTextNode(textObj.str()));
						});
			} else if (keyStr.startsWith(dumpOpts.attr_prefix)) {
				if (parent == null) {
					throw new ValueError("attributes cannot exist at the root level of an xml document.");
				}
				// @attribute is always a ::str
				String attrName = keyStr.substring(dumpOpts.attr_prefix.length());
				String attrValue = d.get(key).str();
				parent.setAttribute(attrName, attrValue);
			} else { // regular nested content
				java.util.List<Node> children = dictItemToNodes(dumpOpts, d.get(key), true, keyStr, doc).collect(Collectors.toList());
				if (parent == null) {
					if (children.size() > 1 || (!children.isEmpty() && result != null)) {
						throw new ValueError("the dictionary contains multiple root elements.");
					}
					result = children.get(0);
				} else {
					for (Node child : children) {
						parent.appendChild(child);
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param iterList {@code true} if list-values should be iterated
	 */
	private static Stream<Node> dictItemToNodes(DumpOpts dumpOpts, Obj item, boolean iterList, String tagName, Document doc) {
		// item may be "::null", ::str, ::dict, ::[["::null" str dict]union]list
		if (item.isa(Obj.SYMBOL) && Casting.asSymbol(item).equiv(SYM_null)) {
			return Stream.of(doc.createElement(tagName));
		} else if (item.isa(Obj.DICT)) {
			Element container = doc.createElement(tagName);
			dictToXml(dumpOpts, Casting.asDict(item), doc, container);
			return Stream.of(container);
		} else if (iterList && item.isa(Obj.LIST) && !item.isa(Obj.STR)) {
			return Casting.asList(item).stream().flatMap(x -> dictItemToNodes(dumpOpts, x, false, tagName, doc));
		} else {
			Element container = doc.createElement(tagName);
			container.appendChild(doc.createTextNode(item.str()));
			return Stream.of(container);
		}
	}

	private static class DumpOpts {
		private static final Symbol SYM_pretty = SymbolTable.getSymbol("pretty");
		private static final Symbol SYM_xml_decl = SymbolTable.getSymbol("xml_decl");
		private static final Symbol SYM_encoding = SymbolTable.getSymbol("encoding");
		private static final Symbol SYM_attr_prefix = SymbolTable.getSymbol("attr_prefix");
		private static final Symbol SYM_cdata_key = SymbolTable.getSymbol("cdata_key");

		public final boolean pretty;
		public final boolean xml_decl;
		public final Charset encoding;
		public final String attr_prefix;
		public final String cdata_key;

		public DumpOpts(DictReader d) {
			pretty = d.getBool(SYM_pretty, false);
			xml_decl = d.getBool(SYM_xml_decl, true);
			encoding = Optional.ofNullable(d.getString(SYM_encoding)).map(Charset::forName).orElse(Charset.defaultCharset());
			attr_prefix = d.getString(SYM_attr_prefix, "@");
			cdata_key = d.getString(SYM_cdata_key, "#text");
		}
	}
}
