package org.recap.las;

import org.recap.las.model.*;
import org.recap.model.IMSConfigProperties;
import org.recap.model.gfa.GFAItemStatusCheckResponse;

/**
 * Created by rajeshbabuk on 20/Jan/2021
 */
public abstract class AbstractLASImsLocationConnector {

    protected IMSConfigProperties imsConfigProperties;

    protected String imsLocationCode;

    public abstract boolean supports(String imsLocationCode);

    public abstract void setImsLocationCode(String imsLocationCode);

    public abstract void setImsConfigProperties(IMSConfigProperties imsConfigProperties);

    public abstract GFALasStatusCheckResponse heartBeatCheck(GFALasStatusCheckRequest gfaLasStatusCheckRequest);

    public abstract GFAItemStatusCheckResponse itemStatusCheck(GFAItemStatusCheckRequest gfaItemStatusCheckRequest);

    public abstract GFARetrieveItemResponse itemRetrieval(GFARetrieveItemRequest gfaRetrieveItemRequest);

    public abstract GFAEddItemResponse itemEDDRetrieval(GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest);

    public abstract GFAPwdResponse gfaPermanentWithdrawalDirect(GFAPwdRequest gfaPwdRequest);

    public abstract GFAPwiResponse gfaPermanentWithdrawalInDirect(GFAPwiRequest gfaPwiRequest);
}
