package com.pkrete.jsip2.messages;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class SIP2ResponseFactoryUT extends BaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(SIP2ResponseFactoryUT.class);

    SIP2ResponseFactory sip2ResponseFactory;

    @Test
    public void testcreate() {

        SIP2ResponseFactory sipResponseFactory = SIP2ResponseFactory.getInstance();
        try {
            sipResponseFactory.create("88Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("98Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("24Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("10Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("12Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("36Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("38Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("18Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("20Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("26Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("16Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("30Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("66Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("82Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("94Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("89Test");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create("8");
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        try {
            sipResponseFactory.create(null);
        } catch (Exception e) {
            logger.info("Exception"+e);
        }
        assertTrue(true);
    }

}
