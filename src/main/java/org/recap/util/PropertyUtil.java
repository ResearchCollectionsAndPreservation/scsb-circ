package org.recap.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.recap.RecapConstants;
import org.recap.ils.model.ILSOAuthConfigProperties;
import org.recap.ils.model.ILSConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@RefreshScope
@Service
@Slf4j
public class PropertyUtil {

    @Autowired
    private Gson gson;

    @Value("${institution:No data available}")
    private String ilsConfigProperties;

    /**
     * Gets property value for the key and the institution
     * @param institution
     * @param propertyKey
     * @return String
     */
    public String getPropertyByInstitution(String institution, String propertyKey) {
        JSONObject jsonObject = getPropsByInstitution(institution);
        return jsonObject.get(propertyKey).toString();
    }

    /**
     * Gets Json object with all properties for the institution
     * @param institution
     * @return JSONObject
     */
    public JSONObject getPropsByInstitution(String institution) {
        JSONObject json = new JSONObject(ilsConfigProperties);
        JSONObject result = json.getJSONObject(institution);
        return result;
    }

    /**
     * Gets a DTO with all properties for the institution
     * @param institution
     * @return ILSConfigProperties
     */
    public ILSConfigProperties getILSConfigProperties(String institution) {
        ILSConfigProperties ilsConfigProperties = null;
        try {
            JSONObject institutionSpecificJson = getPropsByInstitution(institution);
            String protocol = institutionSpecificJson.get(RecapConstants.PROTOCOL).toString();
            log.info("Protocol: {}", protocol);
            if (RecapConstants.REST_PROTOCOL.equalsIgnoreCase(protocol)) {
                ilsConfigProperties = gson.fromJson(institutionSpecificJson.toString(), ILSOAuthConfigProperties.class);
            } else {
                ilsConfigProperties = gson.fromJson(institutionSpecificJson.toString(), ILSConfigProperties.class);
            }
            log.info(ilsConfigProperties.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ilsConfigProperties;
    }
}
