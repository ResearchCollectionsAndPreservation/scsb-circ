package org.recap.camel.requestinitialdataload;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import java.io.Serializable;

/**
 * Created by hemalathas on 3/5/17.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = false)
public class RequestDataLoadCSVRecord implements Serializable {

    @DataField(pos = 1, columnName = "Barcode")
    private String barcode;

    @DataField(pos = 2, columnName = "CustomerCode")
    private String customerCode;

    @DataField(pos = 3, columnName = "DeliveryMethod")
    private String deliveryMethod;

    @DataField(pos = 4, columnName = "CreateDate")
    private String createdDate;

    @DataField(pos = 5, columnName = "LastUpdatedDate")
    private String lastUpdatedDate;

    @DataField(pos = 6, columnName = "PatronId")
    private String patronId;

    @DataField(pos = 7, columnName = "StopCode")
    private String stopCode;

    @DataField(pos = 8, columnName = "Email")
    private String email;

}
