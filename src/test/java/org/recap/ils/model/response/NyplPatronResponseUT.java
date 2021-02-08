package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.NyplPatronData;
import org.recap.ils.model.nypl.response.NyplPatronResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class NyplPatronResponseUT extends BaseTestCaseUT {
    @Test
    public void testNyplPatronResponse() {
        NyplPatronResponse nyplPatronResponse = new NyplPatronResponse();
        DebugInfo debugInfo = new DebugInfo();
        List<DebugInfo> debugInfolist = new ArrayList<>();
        debugInfolist.add(debugInfo);
        nyplPatronResponse.setCount(1);
        nyplPatronResponse.setStatusCode(200);
        NyplPatronData nyplPatronData = new NyplPatronData();
        List<NyplPatronData> nyplPatronDatalist = new ArrayList<>();
        nyplPatronDatalist.add(nyplPatronData);
        nyplPatronResponse.setDebugInfo(debugInfolist);
        nyplPatronResponse.setData(nyplPatronDatalist);

        assertNotNull(nyplPatronResponse.getCount());
        assertNotNull(nyplPatronResponse.getStatusCode());
        assertNotNull(nyplPatronResponse.getDebugInfo());
        assertNotNull(nyplPatronResponse.getData());
    }
}
