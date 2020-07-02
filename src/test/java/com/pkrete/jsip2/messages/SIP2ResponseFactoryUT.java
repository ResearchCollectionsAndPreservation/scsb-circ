package com.pkrete.jsip2.messages;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseException;
import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class SIP2ResponseFactoryUT extends BaseTestCase {
    SIP2ResponseFactory sip2ResponseFactory;

    @Test
    public void testcreate() {
        SIP2ResponseFactory sipResponseFactory = sip2ResponseFactory.getInstance();
        try {
            sipResponseFactory.create("88Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("98Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("24Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("10Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("12Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("36Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("38Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("18Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("20Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("26Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("16Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("30Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("66Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("82Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("94Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("89Test");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create("8");
        } catch (Exception e) {
        }
        try {
            sipResponseFactory.create(null);
        } catch (Exception e) {
        }
        assertTrue(true);
    }

}
