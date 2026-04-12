package com.yemenptc.bss.migration.transformers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Transforms legacy data to TMF format.
 * Normalizes data from TITAN, Oracle BRM, WHM, Internal systems.
 */
@Slf4j
@Component
public class TmfDataTransformer {

    /**
     * Transforms TITAN customer to TMF632 Party format.
     */
    public Map<String, Object> transformToTmfParty(Map<String, Object> titanCustomer) {
        Map<String, Object> party = new HashMap<>();

        String name = (String) titanCustomer.get("name");
        String[] nameParts = name != null ? name.split("\\s+", 2) : new String[]{"", ""};

        party.put("firstName", nameParts[0]);
        party.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
        party.put("primaryPhone", titanCustomer.get("phone_number"));
        party.put("customerType", mapCustomerType((String) titanCustomer.get("service_type")));
        party.put("status", mapStatus((String) titanCustomer.get("status")));
        party.put("city", extractCity((String) titanCustomer.get("address")));
        party.put("governorate", extractGovernorate((String) titanCustomer.get("address")));

        Map<String, Object> characteristics = new HashMap<>();
        characteristics.put("legacyId", titanCustomer.get("customer_id"));
        characteristics.put("legacySystem", "TITAN");
        characteristics.put("migrationDate", new java.util.Date().toString());
        party.put("characteristics", characteristics);

        return party;
    }

    /**
     * Transforms Oracle BRM account to TMF647 BillingAccount.
     */
    public Map<String, Object> transformToTmfBillingAccount(Map<String, Object> brmAccount) {
        Map<String, Object> account = new HashMap<>();

        account.put("accountNumber", brmAccount.get("account_number"));
        account.put("accountType", "BILLING");
        account.put("serviceCategory", "MOBILE");
        account.put("status", mapBrmStatus((Integer) brmAccount.get("account_status")));

        Map<String, Object> characteristics = new HashMap<>();
        characteristics.put("legacyId", brmAccount.get("account_id"));
        characteristics.put("legacySystem", "ORACLE_BRM");
        characteristics.put("balanceGroupId", brmAccount.get("balance_group_id"));
        account.put("characteristics", characteristics);

        return account;
    }

    /**
     * Transforms WHM hosting client to TMF632 Party.
     */
    public Map<String, Object> transformWhmToTmfParty(Map<String, Object> whmClient) {
        Map<String, Object> party = new HashMap<>();

        party.put("firstName", whmClient.get("firstname"));
        party.put("lastName", whmClient.get("lastname"));
        party.put("email", whmClient.get("email"));
        party.put("primaryPhone", whmClient.get("phonenumber"));
        party.put("customerType", "BUSINESS");
        party.put("status", "ACTIVE");

        Map<String, Object> characteristics = new HashMap<>();
        characteristics.put("legacyId", whmClient.get("id"));
        characteristics.put("legacySystem", "WHM");
        characteristics.put("companyName", whmClient.get("companyname"));
        party.put("characteristics", characteristics);

        return party;
    }

    /**
     * Transforms TITAN line to TMF639 Service.
     */
    public Map<String, Object> transformToTmfService(Map<String, Object> titanLine) {
        Map<String, Object> service = new HashMap<>();

        service.put("serviceType", "PSTN");
        service.put("serviceIdentifier", titanLine.get("directory_number"));
        service.put("status", mapStatus((String) titanLine.get("status")));

        Map<String, Object> characteristics = new HashMap<>();
        characteristics.put("lineType", titanLine.get("line_type"));
        characteristics.put("features", titanLine.get("features"));
        characteristics.put("legacyId", titanLine.get("line_id"));
        characteristics.put("legacySystem", "TITAN");
        service.put("characteristics", characteristics);

        return service;
    }

    private String mapCustomerType(String legacyType) {
        if (legacyType == null) return "RESIDENTIAL";
        return switch (legacyType.toUpperCase()) {
            case "BUS", "BUSINESS" -> "BUSINESS";
            case "ENT", "ENTERPRISE" -> "ENTERPRISE";
            case "GOV", "GOVERNMENT" -> "GOVERNMENT";
            default -> "RESIDENTIAL";
        };
    }

    private String mapStatus(String legacyStatus) {
        if (legacyStatus == null) return "ACTIVE";
        return switch (legacyStatus.toUpperCase()) {
            case "A", "ACTIVE" -> "ACTIVE";
            case "S", "SUSPENDED" -> "SUSPENDED";
            case "T", "TERMINATED" -> "TERMINATED";
            default -> "INACTIVE";
        };
    }

    private String mapBrmStatus(Integer status) {
        if (status == null) return "ACTIVE";
        return switch (status) {
            case 1 -> "ACTIVE";
            case 2 -> "SUSPENDED";
            case 3 -> "CLOSED";
            default -> "ACTIVE";
        };
    }

    private String extractCity(String address) {
        if (address == null) return "";
        String[] parts = address.split(",");
        return parts.length > 1 ? parts[parts.length - 2].trim() : "";
    }

    private String extractGovernorate(String address) {
        if (address == null) return "";
        String[] parts = address.split(",");
        return parts.length > 0 ? parts[parts.length - 1].trim() : "";
    }
}
