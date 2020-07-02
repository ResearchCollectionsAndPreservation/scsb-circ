package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.NyplPatronData;
import org.recap.ils.model.nypl.response.NyplPatronResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class NyplPatronResponseUT extends BaseTestCase {
    NyplPatronResponse nyplPatronResponse;
    @Test
    public void testNyplPatronResponse(){
        nyplPatronResponse = new NyplPatronResponse();
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
        nyplPatronResponse.getCount();
        nyplPatronResponse.getData();
        nyplPatronResponse.getDebugInfo();
        nyplPatronResponse.getStatusCode();
        assertTrue(true);
    }
}
