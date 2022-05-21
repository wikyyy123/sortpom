package sortpom.wrapper;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import sortpom.parameter.PluginParameters;
import sortpom.util.FileUtil;
import sortpom.wrapper.operation.HierarchyRootWrapper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementToStringTest {
    @Test
    void testToString() throws Exception {
        String expected = IOUtils.toString(new FileInputStream("src/test/resources/Real1_expected_toString.txt"), StandardCharsets.UTF_8);
        assertEquals(expected, getToStringOnRootElementWrapper());
    }

    private String getToStringOnRootElementWrapper() throws Exception {
        PluginParameters pluginParameters = PluginParameters.builder()
                .setPomFile(null).setFileOutput(false, ".bak", null, false)
                .setEncoding("UTF-8")
                .setFormatting("\r\n", true, true, true)
                .setIndent(2, false, false)
                .setSortOrder("default_0_4_0.xml", null)
                .setSortEntities("scope,groupId,artifactId", "groupId,artifactId", "groupId,artifactId", true, true, true).build();

        FileUtil fileUtil = new FileUtil();
        fileUtil.setup(pluginParameters);

        String xml = IOUtils.toString(new FileInputStream("src/test/resources/" + "Real1_input.xml"), StandardCharsets.UTF_8);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        WrapperFactoryImpl wrapperFactory = new WrapperFactoryImpl(fileUtil);
        wrapperFactory.setup(pluginParameters);
        HierarchyRootWrapper rootWrapper = wrapperFactory.createFromRootElement(document.getDocumentElement());
        rootWrapper.createWrappedStructure(wrapperFactory);

        return rootWrapper.toString();
    }

}
