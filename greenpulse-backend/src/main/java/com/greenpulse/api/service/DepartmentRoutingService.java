package com.greenpulse.api.service;

import com.greenpulse.api.model.ComplaintCategory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DepartmentRoutingService {

    private static final Map<ComplaintCategory, String> ROUTING = Map.of(
            ComplaintCategory.WATER, "Water Works Department",
            ComplaintCategory.WASTE, "Sanitation Department",
            ComplaintCategory.AIR, "Pollution Control Board",
            ComplaintCategory.GREEN_COVER, "Parks & Forestry Department",
            ComplaintCategory.ENERGY, "Electricity Department",
            ComplaintCategory.OTHER, "General Grievance Cell"
    );

    public String routeFor(ComplaintCategory category) {
        return ROUTING.getOrDefault(category, "General Grievance Cell");
    }
}
