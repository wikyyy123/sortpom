package sortpom.wrapper.content;

import org.junit.jupiter.api.Test;
import sortpom.sort.XmlFragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author bjorn
 * @since 2016-07-30
 */
class SortedWrapperTest {

    @Test
    void toStringWithIndentShouldWork() {
        assertThat(new SortedWrapper(XmlFragment.createXmlFragment("Gurka").getDocumentElement(), 123).toString("  "), is("  SortedWrapper{element=[Element: <Gurka/>]}"));
        assertThat(new SortedWrapper(null, 123).toString("  "), is("  SortedWrapper{element=null}"));
    }
}
