package sortpom.wrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import refutils.ReflectionHelper;
import sortpom.sort.XmlFragment;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author bjorn
 * @since 2016-06-22
 */
class ElementUtilTest {

    private Element parent;
    private Element child;

    @BeforeEach
    void setUp() {
        parent = XmlFragment.createXmlFragment("Parent").getDocumentElement();
        child = XmlFragment.createXmlFragment("Child").getDocumentElement();
        parent.appendChild(child);
    }

    @Test
    void testConstructor() {
        ElementUtil elementUtil = ReflectionHelper.instantiatePrivateConstructor(ElementUtil.class);
        assertThat(elementUtil, not(nullValue()));
    }

    @Test
    void parentElementNameShouldBeMatched() {
        assertThat(ElementUtil.isElementParentName(child, "Parent"), is(true));
        assertThat(ElementUtil.isElementParentName(child, "Gurka"), is(false));
        assertThat(ElementUtil.isElementParentName(parent, "Parent"), is(false));
    }
}
