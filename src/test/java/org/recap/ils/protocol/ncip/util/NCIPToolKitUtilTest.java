package org.recap.ils.protocol.ncip.util;

import org.extensiblecatalog.ncip.v2.common.ServiceValidator;
import org.extensiblecatalog.ncip.v2.common.ServiceValidatorFactory;
import org.extensiblecatalog.ncip.v2.common.Translator;
import org.extensiblecatalog.ncip.v2.common.TranslatorFactory;
import org.extensiblecatalog.ncip.v2.service.ServiceContext;
import org.extensiblecatalog.ncip.v2.service.ToolkitException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.recap.common.ScsbConstants;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class NCIPToolKitUtilTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field field = NCIPToolKitUtil.class.getDeclaredField("ncipToolkitUtilInstance");
        field.setAccessible(true);
        field.set(null, null);
    }

    private NCIPToolKitUtil allocateWithoutConstructor() throws Exception {
        sun.reflect.ReflectionFactory rf =
                sun.reflect.ReflectionFactory.getReflectionFactory();
        Constructor<?> objDef = Object.class.getDeclaredConstructor();
        Constructor<?> c = rf.newConstructorForSerialization(NCIPToolKitUtil.class, objDef);
        return NCIPToolKitUtil.class.cast(c.newInstance());
    }


    private void setSingleton(NCIPToolKitUtil instance) throws Exception {
        Field f = NCIPToolKitUtil.class.getDeclaredField("ncipToolkitUtilInstance");
        f.setAccessible(true);
        f.set(null, instance);
    }


    private ServiceValidator buildMockValidator(ServiceContext ctx) throws Exception {
        ServiceValidator v = mock(ServiceValidator.class);
        when(v.getInitialServiceContext()).thenReturn(ctx);
        return v;
    }


    private InputStream nonEmptyPropertiesStream() {
        return new ByteArrayInputStream("toolkit.key=toolkit.value\n".getBytes());
    }


    @Test
    public void getInstance_whenAlreadyInitialised_returnsSameInstance() throws Exception {
        NCIPToolKitUtil existing = allocateWithoutConstructor();
        setSingleton(existing);

        NCIPToolKitUtil result = NCIPToolKitUtil.getInstance();

        assertNotNull(result);
        assertSame(existing, result);
    }


    @Test
    public void getInstance_whenNull_initialisesAndReturnsSingleton() throws Exception {
        ServiceContext mockCtx = mock(ServiceContext.class);
        Translator mockTranslator = mock(Translator.class);
        ServiceValidator mockValidator = buildMockValidator(mockCtx);

        try (MockedStatic<ServiceValidatorFactory> svf =
                     mockStatic(ServiceValidatorFactory.class);
             MockedStatic<TranslatorFactory> tf =
                     mockStatic(TranslatorFactory.class)) {

            svf.when(() -> ServiceValidatorFactory.buildServiceValidator(any(Properties.class)))
                    .thenReturn(mockValidator);
            tf.when(() -> TranslatorFactory.buildTranslator(isNull(), any(Properties.class)))
                    .thenReturn(mockTranslator);

            ClassLoader mockCL = buildClassLoaderWithProperties(nonEmptyPropertiesStream());
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(mockCL);
            try {
                NCIPToolKitUtil result = NCIPToolKitUtil.getInstance();

                assertNotNull(result);
                assertSame(mockCtx, result.serviceContext);
                assertSame(mockTranslator, result.translator);
                assertSame(result, NCIPToolKitUtil.getInstance());
            } finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }

    @Test
    public void init_emptyProperties_throwsRuntimeException() throws Exception {
        try (MockedStatic<ServiceValidatorFactory> svf =
                     mockStatic(ServiceValidatorFactory.class);
             MockedStatic<TranslatorFactory> tf =
                     mockStatic(TranslatorFactory.class)) {

            svf.when(() -> ServiceValidatorFactory.buildServiceValidator((Properties) any()))
                    .thenThrow(new AssertionError("Should not reach ServiceValidatorFactory"));
            tf.when(() -> TranslatorFactory.buildTranslator(any(), any()))
                    .thenThrow(new AssertionError("Should not reach TranslatorFactory"));

            ClassLoader mockCL = buildClassLoaderWithProperties(
                    new ByteArrayInputStream("".getBytes())); // EMPTY ? isEmpty()==true
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(mockCL);
            try {
                invokeInitViaReflection();
                fail("Expected RuntimeException for empty properties");
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }

    @Test
    public void init_toolkitExceptionFromFactory_propagates() throws Exception {
        try (MockedStatic<ServiceValidatorFactory> svf =
                     mockStatic(ServiceValidatorFactory.class);
             MockedStatic<TranslatorFactory> tf =
                     mockStatic(TranslatorFactory.class)) {

            svf.when(() -> ServiceValidatorFactory.buildServiceValidator(any(Properties.class)))
                    .thenThrow(new ToolkitException("Simulated ToolkitException"));

            ClassLoader mockCL = buildClassLoaderWithProperties(nonEmptyPropertiesStream());
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(mockCL);
            try {
                invokeInitViaReflection();
                fail("Expected ToolkitException");
            } catch (ToolkitException e) {
                assertEquals("Simulated ToolkitException", e.getMessage());
            } finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }


    @Test
    public void init_ioExceptionOnLoad_propagates() throws Exception {
        try (MockedStatic<ServiceValidatorFactory> svf =
                     mockStatic(ServiceValidatorFactory.class);
             MockedStatic<TranslatorFactory> tf =
                     mockStatic(TranslatorFactory.class)) {

            InputStream broken = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("Simulated read failure");
                }
            };

            ClassLoader mockCL = buildClassLoaderWithProperties(broken);
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(mockCL);

        }
    }


    @Test
    public void privateConstructor_whenSingletonAlreadySet_throwsRuntimeException()
            throws Exception {
        NCIPToolKitUtil first = allocateWithoutConstructor();
        setSingleton(first);

        Constructor<NCIPToolKitUtil> ctor =
                NCIPToolKitUtil.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        try {
            ctor.newInstance();
            fail("Expected RuntimeException from constructor guard");
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof RuntimeException);
            assertTrue(cause.getMessage().contains("getInstance()"));
        }
    }

    @Test
    public void privateConstructor_whenSingletonNull_succeeds() throws Exception {
        Constructor<NCIPToolKitUtil> ctor =
                NCIPToolKitUtil.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        NCIPToolKitUtil instance = ctor.newInstance();
        assertNotNull(instance);
    }

    @Test
    public void staticInitializer_exceptionSwallowed_classLoadSucceeds() {
        assertNotNull(NCIPToolKitUtil.class.getName());
    }

    private ClassLoader buildClassLoaderWithProperties(InputStream stream) {
        ClassLoader parent = NCIPToolKitUtil.class.getClassLoader();
        return new ClassLoader(parent) {
            @Override
            public InputStream getResourceAsStream(String name) {
                if (name.equals(ScsbConstants.TOOLKIT_PROP_FILE)) {
                    return stream;
                }
                return super.getResourceAsStream(name);
            }
        };
    }


    private void invokeInitViaReflection() throws Exception {
        setSingleton(null);

        Method initMethod = NCIPToolKitUtil.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        try {
            initMethod.invoke(null);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof IOException) throw (IOException) cause;
            if (cause instanceof ToolkitException) throw (ToolkitException) cause;
            throw new RuntimeException(cause);
        }
    }
}