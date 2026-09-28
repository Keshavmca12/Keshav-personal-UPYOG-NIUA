package org.upyog.dpdp.adapter;

import org.upyog.dpdp.web.models.PersonalDataView;

import java.util.List;

public interface DpdpDataProvider {

    String applicationCode();

    List<PersonalDataView> findPersonalData(DataPrincipalContext context);

    CorrectionResult correctPersonalData(DataPrincipalContext context, String field, String value);

    DeletionResult deletePersonalData(DataPrincipalContext context);

    record DataPrincipalContext(String tenantId, String userUuid, String applicationCode) {
    }

    record CorrectionResult(boolean updated, String message) {
    }

    record DeletionResult(boolean deleted, String action, String message) {
    }
}
