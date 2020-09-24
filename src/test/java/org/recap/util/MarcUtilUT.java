package org.recap.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertNotNull;
@RunWith(MockitoJUnitRunner.class)
public class MarcUtilUT {
    @Mock
    Record response;
    @Mock
    VariableField variableField;
    @Mock
    DataField dataField;

    String marcXml = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
            "    <record>\n" +
            "        <leader>01750cam a2200493 i 4500</leader>\n" +
            "        <controlfield tag=\"001\">9919400</controlfield>\n" +
            "        <controlfield tag=\"005\">20160912115017.0</controlfield>\n" +
            "        <controlfield tag=\"008\">160120t20172016enk b 000 0 eng</controlfield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "            <subfield code=\"a\">2016002744</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"a\">9780415710466</subfield>\n" +
            "            <subfield code=\"q\">hardcover</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"a\">0415710464</subfield>\n" +
            "            <subfield code=\"q\">hardcover</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"z\">9781315867618</subfield>\n" +
            "            <subfield code=\"q\">electronic book</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"z\">1315867613</subfield>\n" +
            "            <subfield code=\"q\">electronic book</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)909322578</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)ocn909322578</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "            <subfield code=\"a\">DLC</subfield>\n" +
            "            <subfield code=\"e\">rda</subfield>\n" +
            "            <subfield code=\"b\">eng</subfield>\n" +
            "            <subfield code=\"c\">DLC</subfield>\n" +
            "            <subfield code=\"d\">YDX</subfield>\n" +
            "            <subfield code=\"d\">BTCTA</subfield>\n" +
            "            <subfield code=\"d\">BDX</subfield>\n" +
            "            <subfield code=\"d\">OCLCF</subfield>\n" +
            "            <subfield code=\"d\">YDXCP</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"042\">\n" +
            "            <subfield code=\"a\">pcc</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "            <subfield code=\"a\">K236</subfield>\n" +
            "            <subfield code=\"b\">.F38 2017</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"082\">\n" +
            "            <subfield code=\"a\">342.08/5297</subfield>\n" +
            "            <subfield code=\"2\">23</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "            <subfield code=\"a\">Farrar, Salim,</subfield>\n" +
            "            <subfield code=\"e\">author.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\"0\" tag=\"245\">\n" +
            "            <subfield code=\"a\">Accommodating Muslims under common law :</subfield>\n" +
            "            <subfield code=\"b\">a comparative analysis /</subfield>\n" +
            "            <subfield code=\"c\">Salim Farrar and Ghena Krayem.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"1\" tag=\"264\">\n" +
            "            <subfield code=\"a\">Abingdon, Oxon ;</subfield>\n" +
            "            <subfield code=\"a\">New York, NY :</subfield>\n" +
            "            <subfield code=\"b\">Routledge,</subfield>\n" +
            "            <subfield code=\"c\">2017.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"4\" tag=\"264\">\n" +
            "            <subfield code=\"c\">©2016</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "            <subfield code=\"a\">viii, 206 pages ;</subfield>\n" +
            "            <subfield code=\"c\">25 cm</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"336\">\n" +
            "            <subfield code=\"a\">text</subfield>\n" +
            "            <subfield code=\"b\">txt</subfield>\n" +
            "            <subfield code=\"2\">rdacontent</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"337\">\n" +
            "            <subfield code=\"a\">unmediated</subfield>\n" +
            "            <subfield code=\"b\">n</subfield>\n" +
            "            <subfield code=\"2\">rdamedia</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"338\">\n" +
            "            <subfield code=\"a\">volume</subfield>\n" +
            "            <subfield code=\"b\">nc</subfield>\n" +
            "            <subfield code=\"2\">rdacarrier</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "            <subfield code=\"a\">Includes bibliographical references.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Legal polycentricity.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Legal status, laws, etc.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Civil rights.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Common law.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Islamic law.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Comparative law.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Common law.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00869795</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Comparative law.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00871350</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Islamic law.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00979949</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Legal polycentricity.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00995519</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Civil rights.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst01031035</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Legal status, laws, etc.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst01031055</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n" +
            "            <subfield code=\"a\">Krayem, Ghena,</subfield>\n" +
            "            <subfield code=\"e\">author.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"902\">\n" +
            "            <subfield code=\"a\">kl</subfield>\n" +
            "            <subfield code=\"b\">s</subfield>\n" +
            "            <subfield code=\"6\">a</subfield>\n" +
            "            <subfield code=\"7\">m</subfield>\n" +
            "            <subfield code=\"d\">v</subfield>\n" +
            "            <subfield code=\"f\">1</subfield>\n" +
            "            <subfield code=\"e\">20160912</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"904\">\n" +
            "            <subfield code=\"a\">kl</subfield>\n" +
            "            <subfield code=\"b\">a</subfield>\n" +
            "            <subfield code=\"h\">m</subfield>\n" +
            "            <subfield code=\"c\">b</subfield>\n" +
            "            <subfield code=\"e\">20160912</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\" \" tag=\"852\">\n" +
            "            <subfield code=\"0\">9734816</subfield>\n" +
            "            <subfield code=\"b\">rcppa</subfield>\n" +
            "            <subfield code=\"h\">K236 .F38 2017</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"876\">\n" +
            "            <subfield code=\"0\">9734816</subfield>\n" +
            "            <subfield code=\"a\">7453441</subfield>\n" +
            "            <subfield code=\"h\"/>\n" +
            "            <subfield code=\"j\">Not Charged</subfield>\n" +
            "            <subfield code=\"p\">32101095533293</subfield>\n" +
            "            <subfield code=\"t\">0</subfield>\n" +
            "            <subfield code=\"x\">Shared</subfield>\n" +
            "            <subfield code=\"z\">PA</subfield>\n" +
            "        </datafield>\n" +
            "    </record>\n" +
            "</collection>";
    @Test
    public void testgetSecondIndicatorForDataField(){
        MarcUtil marcUtil = new MarcUtil();
        variableField.setId((long) 1);
        variableField.setTag("test");
        dataField.setIndicator1('a');
        dataField.setIndicator2('b');
        dataField.setId((long)2);
        dataField.setTag("test");
        List<VariableField> variableFields = new ArrayList<>();
        variableFields.add(variableField);
        response.setType("test");
        Integer result = marcUtil.getSecondIndicatorForDataField(response,"test");
        assertNotNull(result);

    }

    @Test
    public void readMarcxml(){
        MarcUtil marcUtil =  new MarcUtil();
        List<Record> records= marcUtil.readMarcXml(marcXml);
        assertNotNull(records);
    }
}
