package org.recap.camel.requestinitialdataload;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import java.io.Serializable;

/**
 * Created by hemalathas on 5/5/17.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class RequestDataLoadErrorCSVRecord implements Serializable{
    @DataField(pos = 1, columnName = "BarcodesNotAvailable")
    private String barcodes;
}
