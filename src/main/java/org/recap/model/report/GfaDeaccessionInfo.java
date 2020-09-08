package org.recap.model.report;

import lombok.Data;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class GfaDeaccessionInfo {
    List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
    String username;
}
