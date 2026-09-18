package org.upyog.dpdp.scanner;

import org.springframework.stereotype.Component;
import org.upyog.dpdp.engine.ClassificationEngine;
import org.upyog.dpdp.engine.RuleEngine;
import org.upyog.dpdp.util.PiiMasker;
import org.upyog.dpdp.web.models.ClassificationResult;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.ScanJob;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseScanner {

    private final ClassificationEngine classificationEngine;
    private final RuleEngine ruleEngine;

    public DatabaseScanner(ClassificationEngine classificationEngine, RuleEngine ruleEngine) {
        this.classificationEngine = classificationEngine;
        this.ruleEngine = ruleEngine;
    }

    public List<Finding> scan(ScanJob job, List<ComplianceRule> rules) {
        List<Finding> findings = new ArrayList<>();
        if (job.getJdbcUrl() == null) {
            return findings;
        }
        try (Connection connection = DriverManager.getConnection(job.getJdbcUrl(), job.getJdbcUser(), job.getJdbcPassword())) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = job.getSchemaName();
            try (ResultSet columns = metaData.getColumns(null, schema, "%", "%")) {
                while (columns.next()) {
                    String table = columns.getString("TABLE_NAME");
                    String column = columns.getString("COLUMN_NAME");
                    ClassificationResult classified = classificationEngine.classify(column, null);
                    Map<String, Object> facts = new HashMap<>();
                    facts.put("personalData", classified.isPersonalData());
                    facts.put("highRiskPersonal", classified.isHighRiskPersonal());
                    facts.put("classification", classified.getClassification());
                    facts.put("retentionPolicy", null);
                    facts.put("encrypted", Boolean.FALSE);
                    facts.put("fieldName", column);
                    facts.put("assetName", table + "." + column);
                    facts.put("applicationCode", job.getApplicationCode());
                    if (job.isAllowSampleAnalysis()) {
                        facts.put("sample", PiiMasker.redact("sample"));
                    }
                    findings.addAll(ruleEngine.evaluate(job.getTenantId(), job.getId(), facts, rules));
                }
            }
        } catch (Exception ex) {
            findings.add(Finding.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .tenantId(job.getTenantId())
                    .scanId(job.getId())
                    .ruleCode("DPDP-DB-CONN")
                    .category("CLASSIFICATION")
                    .severity("HIGH")
                    .status("OPEN")
                    .title("Database scan could not complete")
                    .description(ex.getMessage())
                    .createdTime(System.currentTimeMillis())
                    .build());
        }
        return findings;
    }
}
