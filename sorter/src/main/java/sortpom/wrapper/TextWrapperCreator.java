package sortpom.wrapper;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import sortpom.jdomcontent.NewlineText;
import sortpom.parameter.PluginParameters;
import sortpom.wrapper.content.SingleNewlineInTextWrapper;
import sortpom.wrapper.content.UnsortedWrapper;
import sortpom.wrapper.content.Wrapper;

/**
 * @author bjorn
 * @since 2012-05-19
 */
public class TextWrapperCreator {
    private boolean keepBlankLines;

    public void setup(PluginParameters pluginParameters) {
        keepBlankLines = pluginParameters.keepBlankLines;
    }

    Wrapper<Node> createWrapper(Text text) {
        if (isSingleNewLine(text)) {
            return SingleNewlineInTextWrapper.INSTANCE;
        } else if (isBlankLineOrLines(text)) {
            return new UnsortedWrapper<>(new NewlineText(text.getOwnerDocument()));
        }
        return new UnsortedWrapper<>(text);
    }

    private boolean isSingleNewLine(Text content) {
        return content.getTextContent().matches("[\\t ]*[\\r]?[\\n]?[\\t ]*");
    }

    boolean isBlankLineOrLines(Text content) {
        if (!keepBlankLines) {
            return false;
        }
        return content.getTextContent().matches("^\\s*?([\\r\\n])\\s*$");
    }
}
