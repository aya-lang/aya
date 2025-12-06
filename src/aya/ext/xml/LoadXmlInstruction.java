package aya.ext.xml;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.exceptions.runtime.ValueError;
import aya.instruction.named.NamedOperator;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.ListCollector;
import aya.obj.list.Str;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.util.Casting;
import aya.util.DictReader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LoadXmlInstruction extends NamedOperator {
	public LoadXmlInstruction() {
		super("xml.loads");
		_doc = "xml::str opts::dict -> ::dict : Convert an xml string to a dict\n"
				+ "  opts::dict : {\n"
				+ "    ns_mode::num : default=1\n"
				+ "        if 0 : the prefix is removed from all element names\n"
				+ "        if 1 : the prefix is used as-is for element names\n"
				+ "        if 2 : the prefix is expanded for element names\n"
				+ "    ns_mapping::[str]dict : default=:{}\n"
				+ "        only relevant if ns_mode is 2.\n"
				+ "        key = namespace URI, value = prefix to replace the uri with.\n"
				+ "        URIs not in this mapping are expanded normally.\n"
				+ "        Example: :{\"p\":\"uri1\"; \"\":\"uri2\";} maps '<uri1:a/><uri2:b/>' to 'p:a' and 'b'\n"
				+ "    ns_separator::str : default=\":\" ; only relevant if ns_mode is 2.\n"
				+ "    attr_prefix::str : default=\"@\"\n"
				+ "    cdata_key::str : default=\"#text\" ; the key for text content in the dict.\n"
				+ "    trim_whitespace::bool : default=1 ; trim whitespace around text in text-nodes\n"
				+ "    force_list::[bool [str]list]union : default=0\n"
				+ "        if 0 : only elements that occur multiple times are collected to a list.\n"
				+ "        if 1 : all elements are collected to a list.\n"
				+ "        if list : only elements whose name are in the list are collected to a list.\n"
				+ "        <note: this option applies to #text as well, but not to attributes, as they are already distinct.>\n"
				+ "  }\n";
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj opts = blockEvaluator.pop();
		final Obj xml = blockEvaluator.pop();

		if (!xml.isa(Obj.STR)) {
			throw new TypeError(this, "xml::str", xml);
		}
		if (!opts.isa(Obj.DICT)) {
			throw new TypeError(this, "opts::dict", opts);
		}

		LoadOpts loadOpts = new LoadOpts(new DictReader(Casting.asDict(opts), "opts"));
		blockEvaluator.push(xmlToDict(loadOpts, parseXml(Casting.asStr(xml)), "xml"));
	}

	private static Document parseXml(Str xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xml.str())));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			StringWriter traceWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(traceWriter));

			throw new ValueError("invalid xml String.\n\texception: " + e.getMessage() + "\n\txml: " + xml.repr() + "\n\ttrace: " + traceWriter);
		}
	}

	private static Dict xmlToDict(LoadOpts loadOpts, Node xml, String path) {
		assert xml.getNodeType() == Node.ELEMENT_NODE || xml.getNodeType() == Node.DOCUMENT_NODE;

		Dict result = new Dict();

		NodeList children = xml.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String childName = null;
			Stream<Obj> values = null;
			if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
				childName = loadOpts.cdata_key;
				values = Optional.ofNullable(getTextContent(loadOpts, child)).map(Stream::of).orElse(null);
			} else if (child.getNodeType() == Node.ELEMENT_NODE) {
				childName = getNodeName(loadOpts, child);
				values = getChildValues(loadOpts, child, path + "." + childName);
			}
			if (childName == null || values == null)
				continue;

			Symbol childSym = SymbolTable.getSymbol(childName);
			Obj accumulator = result.getSafe(childSym);
			if (accumulator == null) { // first time this key is encountered
				if (loadOpts.isForceList(childName)) {
					result.set(childSym, values.collect(new ListCollector()));
				} else {
					List valueList = values.collect(new ListCollector());
					result.set(childSym, valueList.length() == 1 ? valueList.getExact(0) : valueList);
				}
			} else {
				if (accumulator.isa(Obj.LIST) && !accumulator.isa(Obj.STR)) {
					List accList = Casting.asList(accumulator);
					values.forEach(accList::mutAdd);
				} else {
					// the 'accumulator' is actually a single item
					result.set(childSym, Stream.concat(Stream.of(accumulator), values).collect(new ListCollector()));
				}
			}
		}

		// Check attributes after checking nested content
		// this way we detect name collisions (which should be impossible with the default prefix (@) because it's an illegal character in xml)
		NamedNodeMap attributes = xml.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				String attName = getNodeName(loadOpts, attribute);
				Symbol attSymbol = SymbolTable.getSymbol(attName);
				if (result.containsKey(attSymbol)) {
					throw new ValueError("duplicate attribute name at " + path + "." + attName);
				}
				result.set(attSymbol, List.fromString(attribute.getNodeValue()));
			}
		}

		return result;
	}

	private static Obj getTextContent(LoadOpts loadOpts, Node xml) {
		String textContent = xml.getNodeValue();
		if (loadOpts.trim_whitespace) {
			textContent = StringUtils.trimToNull(textContent);
		}
		return textContent == null ? null : List.fromString(textContent);
	}

	private static Stream<Obj> getChildValues(LoadOpts loadOpts, Node xml, String path) {
		Dict childD = xmlToDict(loadOpts, xml, path);
		ArrayList<Symbol> childKeys = childD.keys();
		if (childKeys.isEmpty()) {
			return Stream.of(SymbolTable.getSymbol("null"));
		} else if (childD.containsKey(loadOpts.cdata_sym) && childKeys.size() == 1) {
			// if this node only contains text, unwrap the dictionary.
			Obj textValues = childD.get(loadOpts.cdata_sym);
			if (textValues.isa(Obj.LIST) && !textValues.isa(Obj.STR)) {
				return Casting.asList(textValues).stream();
			} else {
				return Stream.of(textValues);
			}
		} else {
			return Stream.of(childD);
		}
	}

	private static String getNodeName(LoadOpts loadOpts, Node node) {
		StringBuilder nodeName = new StringBuilder();
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			nodeName.append(loadOpts.attr_prefix);
		}

		if (loadOpts.ns_mode == 2) { // 2 - the prefix is expanded for element names
			String nsUri = node.getNamespaceURI();
			if (nsUri != null) {
				String mappedUri = loadOpts.ns_mapping.getOrDefault(nsUri, nsUri);
				if (!mappedUri.isEmpty()) {
					nodeName.append(mappedUri);
					nodeName.append(loadOpts.ns_separator);
				}
			}
			nodeName.append(node.getLocalName());
		} else if (loadOpts.ns_mode == 1) { // 1 - the prefix is used as-is for element names
			nodeName.append(node.getNodeName());
		} else { // 0 - the prefix is removed from all element names
			nodeName.append(node.getLocalName());
		}
		return nodeName.toString();
	}

	private static class LoadOpts {
		private static final Symbol SYM_ns_mode = SymbolTable.getSymbol("ns_mode");
		private static final Symbol SYM_ns_mapping = SymbolTable.getSymbol("ns_mapping");
		private static final Symbol SYM_ns_separator = SymbolTable.getSymbol("ns_separator");
		private static final Symbol SYM_attr_prefix = SymbolTable.getSymbol("attr_prefix");
		private static final Symbol SYM_cdata_key = SymbolTable.getSymbol("cdata_key");
		private static final Symbol SYM_trim_whitespace = SymbolTable.getSymbol("trim_whitespace");
		private static final Symbol SYM_force_list = SymbolTable.getSymbol("force_list");

		public final int ns_mode;
		public final Map<String, String> ns_mapping;
		public final String ns_separator;
		public final String attr_prefix;
		public final String cdata_key;
		public final Symbol cdata_sym;
		public final boolean trim_whitespace;
		/** only relevant if {@link #force_list_keys} is null */
		public final boolean force_list;
		public final Set<String> force_list_keys;

		public LoadOpts(DictReader d) {
			ns_mode = d.getInt(SYM_ns_mode, 1);
			if (ns_mode < 0 || ns_mode > 2) {
				throw new ValueError("ns_mode must be one of [0 1 2]");
			}
			ns_mapping = Optional.ofNullable(d.getDict(SYM_ns_mapping))
					.map(nsMapping -> nsMapping.keys().stream().collect(Collectors.toMap(
							Symbol::name,
							k -> nsMapping.get(k).str()
					)))
					.orElse(Collections.emptyMap());
			ns_separator = d.getString(SYM_ns_separator, ":");
			attr_prefix = d.getString(SYM_attr_prefix, "@");
			cdata_key = d.getString(SYM_cdata_key, "#text");
			cdata_sym = SymbolTable.getSymbol(cdata_key);
			trim_whitespace = d.getBool(SYM_trim_whitespace, true);
			force_list = d.getBool(SYM_force_list, false);
			force_list_keys = Optional.ofNullable(d.getList(SYM_force_list))
					.map(fl -> IntStream.range(0, fl.length()).mapToObj(i -> fl.getExact(i).str()).collect(Collectors.toSet()))
					.orElse(null);
		}

		public boolean isForceList(String key) {
			if (force_list_keys == null)
				return force_list;
			return force_list_keys.contains(key);
		}
	}
}
