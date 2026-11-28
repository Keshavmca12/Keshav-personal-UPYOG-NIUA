# DPDP API Specification

Base path: `/upyog-dpdp-service`  
Style: UPYOG POST `_verb` with `RequestInfo`.

```
POST /consent/v1/_create|_search|_withdraw|_verify
POST /purpose/v1/_search|_validate
POST /rights/v1/_create|_search
POST /retention/v1/_search|_evaluate
POST /scan/v1/_create|_search
POST /compliance/v1/_summary|_score|_search|_recommendations|_trends
POST /finding/v1/_search
POST /incident/v1/_create|_search|_update
```

Citizen APIs are scoped to `RequestInfo.userInfo.uuid`. Employee APIs require ACCESSCONTROL actions 91001–91020. Cross-tenant access is rejected.
