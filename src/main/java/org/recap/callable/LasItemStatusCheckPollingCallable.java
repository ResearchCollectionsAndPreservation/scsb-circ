package org.recap.callable;

import org.recap.ScsbConstants;
import org.recap.las.LASImsLocationConnectorFactory;
import org.recap.las.model.GFAItemStatus;
import org.recap.las.model.GFAItemStatusCheckRequest;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class LasItemStatusCheckPollingCallable implements Callable {

    private static final Logger logger = LoggerFactory.getLogger(LasItemStatusCheckPollingCallable.class);

    private static String barcode;
    private String imsLocationCode;
    private LASImsLocationConnectorFactory lasImsLocationConnectorFactory;
    private Integer pollingTimeInterval;


    public LasItemStatusCheckPollingCallable(Integer pollingTimeInterval, LASImsLocationConnectorFactory lasImsLocationConnectorFactory, String barcode, String imsLocationCode) {
        this.lasImsLocationConnectorFactory = lasImsLocationConnectorFactory;
        this.pollingTimeInterval = pollingTimeInterval;
        this.barcode = barcode;
        this.imsLocationCode = imsLocationCode;
    }

    @Override
    public GFAItemStatusCheckResponse call() throws Exception {
        return poll();
    }

    private GFAItemStatusCheckResponse poll() throws Exception {
        GFAItemStatusCheckResponse gfaItemStatusCheckResponse = null;
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        //Pol ItemRequest Rest Service
        GFAItemStatus gfaItemStatus001 = new GFAItemStatus();
        gfaItemStatus001.setItemBarCode(barcode);
        List<GFAItemStatus> gfaItemStatuses = new ArrayList<>();
        gfaItemStatuses.add(gfaItemStatus001);
        gfaItemStatusCheckRequest.setItemStatus(gfaItemStatuses);
        try {
            gfaItemStatusCheckResponse = lasImsLocationConnectorFactory.getLasImsLocationConnector(imsLocationCode).itemStatusCheck(gfaItemStatusCheckRequest);
            logger.info("Item Status Check Polling -> {}", gfaItemStatusCheckResponse);
            if (gfaItemStatusCheckResponse == null) {
                Thread.sleep(pollingTimeInterval);
                logger.info("LAS Item Status Check Polling");
                gfaItemStatusCheckResponse = poll();
            }
            ScsbConstants.LAS_ITEM_STATUS_REST_SERVICE_STATUS = 0;
        } catch (Exception e) {
            logger.error("", e);
        }
        return gfaItemStatusCheckResponse;
    }

    public static String getBarcode() {
        return barcode;
    }

    public static void setBarcode(String barcode) {
        LasItemStatusCheckPollingCallable.barcode = barcode;
    }
}
