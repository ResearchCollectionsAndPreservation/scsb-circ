package org.recap.ils.protocol.ncip.util;

  
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.extensiblecatalog.ncip.v2.common.ServiceValidatorFactory;
import org.extensiblecatalog.ncip.v2.common.Translator;
import org.extensiblecatalog.ncip.v2.common.TranslatorFactory;
import org.extensiblecatalog.ncip.v2.service.ServiceContext;
import org.extensiblecatalog.ncip.v2.service.ToolkitException;
import org.recap.ScsbConstants;

@Slf4j
public class NCIPToolKitUtil {

        private static NCIPToolKitUtil ncipToolkitUtilInstance = new NCIPToolKitUtil();
        public Translator translator;
        public ServiceContext serviceContext;

        private NCIPToolKitUtil() {
            if (ncipToolkitUtilInstance != null){
                throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
            }
   }

        public static NCIPToolKitUtil getInstance() throws IOException, ToolkitException {
            if (ncipToolkitUtilInstance == null) {
                    ncipToolkitUtilInstance = new NCIPToolKitUtil();
                    InputStream inputStream = NCIPToolKitUtil.class.getClassLoader().getResourceAsStream(ScsbConstants.TOOLKIT_PROP_FILE);
                    log.info("initializing the NCIP Toolkit Property File...");log.info("initializing the NCIP Toolkit Property File...");
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    if (properties.isEmpty()) {
                        log.error("Unable to initialize the default toolkit properties.");
                        throw new RuntimeException("Unable to initialize the NCIP Toolkit property file.");
                    }
                    ncipToolkitUtilInstance.serviceContext = ServiceValidatorFactory.buildServiceValidator(properties).getInitialServiceContext();
                    ncipToolkitUtilInstance.translator = TranslatorFactory.buildTranslator(null,properties);
                    return ncipToolkitUtilInstance;
            }
            return ncipToolkitUtilInstance;
        }
    }
