package org.upyog.dpdp.util;

public final class DpdpConstants {

    public static final String BASIS_CONSENT = "CONSENT";
    public static final String BASIS_LEGAL_OBLIGATION = "LEGAL_OBLIGATION";
    public static final String BASIS_STATUTORY_FUNCTION = "STATUTORY_FUNCTION";
    public static final String BASIS_OTHER_PERMITTED_USE = "OTHER_PERMITTED_USE";

    public static final String CONSENT_GRANTED = "GRANTED";
    public static final String CONSENT_WITHDRAWN = "WITHDRAWN";
    public static final String CONSENT_EXPIRED = "EXPIRED";
    public static final String CONSENT_REVOKED = "REVOKED";

    public static final String RIGHTS_ACCESS = "ACCESS";
    public static final String RIGHTS_CORRECTION = "CORRECTION";
    public static final String RIGHTS_ERASURE = "ERASURE";
    public static final String RIGHTS_GRIEVANCE = "GRIEVANCE";

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_SOFT_DELETED = "SOFT_DELETED";
    public static final String STATUS_ARCHIVED = "ARCHIVED";
    public static final String STATUS_HARD_DELETED = "HARD_DELETED";

    public static final String SCAN_PENDING = "PENDING";
    public static final String SCAN_RUNNING = "RUNNING";
    public static final String SCAN_COMPLETED = "COMPLETED";
    public static final String SCAN_FAILED = "FAILED";

    public static final String SCAN_DATABASE = "DATABASE";
    public static final String SCAN_API = "API";
    public static final String SCAN_LOG = "LOG";
    public static final String SCAN_ACCESS = "ACCESS";
    public static final String SCAN_ENCRYPTION = "ENCRYPTION";
    public static final String SCAN_FULL = "FULL";

    public static final String SEVERITY_CRITICAL = "CRITICAL";
    public static final String SEVERITY_HIGH = "HIGH";
    public static final String SEVERITY_MEDIUM = "MEDIUM";
    public static final String SEVERITY_LOW = "LOW";

    public static final String CLASS_PERSONAL = "PERSONAL";
    public static final String CLASS_HIGH_RISK = "HIGH_RISK_PERSONAL";
    public static final String CLASS_FINANCIAL = "FINANCIAL";
    public static final String CLASS_UNCLASSIFIED = "UNCLASSIFIED";

    public static final String USER_CITIZEN = "CITIZEN";

    public static final String ERR_TENANT_REQUIRED = "DPDP_TENANT_REQUIRED";
    public static final String ERR_USER_REQUIRED = "DPDP_USER_REQUIRED";
    public static final String ERR_CROSS_TENANT = "DPDP_CROSS_TENANT";
    public static final String ERR_CROSS_USER = "DPDP_CROSS_USER";
    public static final String ERR_NOT_FOUND = "DPDP_NOT_FOUND";
    public static final String ERR_VALIDATION = "DPDP_VALIDATION";
    public static final String ERR_CONSENT_REQUIRED = "DPDP_CONSENT_REQUIRED";

    private DpdpConstants() {
    }
}
