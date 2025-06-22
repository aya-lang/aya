package aya.parser.tokens;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.SyntaxError;
import aya.parser.ParserString;
import aya.parser.SourceStringRef;
import aya.parser.StringParseUtils;

/**
 * A Token whose data needs to be unescaped ({@link StringParseUtils#unescape(ParserString)}) before being used.
 */
public abstract class EscapedToken extends StdToken {
    protected final String unescapedData;

    /**
     * @param hasExplicitTerminator true to indicate that the data was terminated by a special character (-> source index is off by one)
     */
    protected EscapedToken(String data, int type, SourceStringRef source, boolean hasExplicitTerminator)
            throws SyntaxError, EndOfInputError {
        super(data, type, source);

        unescapedData = StringParseUtils.unescape(new ParserString(source, data, hasExplicitTerminator));
    }
}
