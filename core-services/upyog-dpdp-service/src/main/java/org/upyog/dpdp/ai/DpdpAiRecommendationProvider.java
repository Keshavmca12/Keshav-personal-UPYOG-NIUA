package org.upyog.dpdp.ai;

import org.upyog.dpdp.web.models.Finding;

import java.util.List;

public interface DpdpAiRecommendationProvider {
    List<String> recommend(List<Finding> findings);
}
