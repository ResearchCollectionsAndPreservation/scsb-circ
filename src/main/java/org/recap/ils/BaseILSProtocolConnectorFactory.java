package org.recap.ils;

public abstract class BaseILSProtocolConnectorFactory {
    public abstract AbstractProtocolConnector getIlsProtocolConnector(String instituteCode);
}
