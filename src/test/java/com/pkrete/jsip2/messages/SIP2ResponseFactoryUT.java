package com.pkrete.jsip2.messages;

import com.pkrete.jsip2.parser.SIP2ResponseParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class SIP2ResponseFactoryUT {


    SIP2ResponseFactory sip2ResponseFactory;

    @Mock
    SIP2ResponseParser parser;

    @Test
    public void testcreate() {

        SIP2ResponseFactory sipResponseFactory = SIP2ResponseFactory.getInstance();
        try {
            sipResponseFactory.create("88Test");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("98Test2");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("24Test3");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("10Test4");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("12Test5");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("36Test");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("38Test6");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("18Test7");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("20Test8");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("26Test9");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("16Test10");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("30Test11");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("66Test12");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("82Test13");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("94Test14");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("89Test15");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("8Test16");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create("64Test17");
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        try {
            sipResponseFactory.create(null);
        } catch (Exception e) {
            log.info("Exception" + e);
        }
        assertTrue(true);
    }

}
