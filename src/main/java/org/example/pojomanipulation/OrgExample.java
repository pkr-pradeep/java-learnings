package org.example.pojomanipulation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * POJO mapping + manipulation example for:
 * Organization[employees, departments, services, addresses, holidays, revenue, name, headquarter]
 */
public class OrgExample {


    public static void main(String[] args) throws IOException {
        String sampleJson = "{\n" +
                "  \"id\": \"org-1\",\n" +
                "  \"name\": \"Acme Corp\",\n" +
                "  \"headquarter\": \"addr-1\",\n" +
                "  \"revenue\":  \"1234567.89\",\n" +
                "  \"addresses\": " +
                "    {\"id\":\"addr-1\",\"line1\":\"1 Infinite Way\",\"city\":\"Bengaluru\",\"country\":\"IN\",\"type\":\"HQ\"}\n" +
                "  ,\n" +
                "  \"departments\": [\n" +
                "    {\"id\":\"dep-ENG\",\"name\":\"Engineering\",\"addressId\":\"addr-1\"},\n" +
                "    {\"id\":\"dep-SLS\",\"name\":\"Sales\",\"addressId\":\"addr-2\"}\n" +
                "  ],\n" +
                "  \"employees\": [\n" +
                "    {\"id\":\"e1\",\"name\":\"Kiran\",\"email\":\"kiran@acme.com\",\"departmentId\":\"dep-ENG\",\"title\":\"SDE\",\"salary\":900000},\n" +
                "    {\"id\":\"e2\",\"name\":\"Anita\",\"email\":\"anita@acme.com\",\"departmentId\":\"dep-SLS\",\"title\":\"AE\",\"salary\":700000}\n" +
                "  ],\n" +
                "  \"services\": [\n" +
                "    {\"id\":\"svc-1\",\"name\":\"Premium Support\",\"price\":15000,\"owningDepartmentId\":\"dep-SLS\"},\n" +
                "    {\"id\":\"svc-2\",\"name\":\"Platform License\",\"price\":500000,\"owningDepartmentId\":\"dep-ENG\"}\n" +
                "  ],\n" +
                "  \"holidays\": [\n" +
                "    {\"name\":\"Diwali\",\"date\":\"2025-10-20\",\"regional\":true},\n" +
                "    {\"name\":\"New Year\",\"date\":\"2026-01-01\",\"regional\":false}\n" +
                "  ]\n" +
                "}";

        // JSON -> POJO
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        Organization org = objectMapper.readValue(sampleJson, Organization.class);

        // === Manipulations on POJOs ===
        Employee newEmp = new Employee();
        newEmp.setId("e3");
        newEmp.setName("Rohit");
        newEmp.setEmail("rohit@acme.com");
        newEmp.setDepartmentId("dep-ENG");
        newEmp.setTitle("SDE2");
        newEmp.setSalary(new BigDecimal("1100000"));
        Organization org2 = new Organization();
        Address address = new Address();
        address.setPostalCode("1234");
        State state = new State();
        state.setName("Odisha");
        address.setState(state);
        org2.setAddresses(address);
        org2.setEmployees(Collections.singletonList(newEmp));

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setEmployees(Collections.singletonList(newEmp));
        organizationDTO.setRevenue("1234");
        organizationDTO.setAddresses(address);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonNode updates = mapper.valueToTree(org2);
        //OrganizationMerger.INSTANCE.merge(org, org2);
        objectMapper.readerForUpdating(org).readValue(mapper.writeValueAsString(updates));

        //Organization organization = objectMapper.readValue(mapper.writeValueAsString(organizationDTO), Organization.class);
        state.setName(null);
        state.setCode("OD");
        address.setState(state);
        org2.setAddresses(address);
        updates = mapper.valueToTree(org2);
        objectMapper.readerForUpdating(org).readValue(mapper.writeValueAsString(updates));

        state.setCode(null);
        address.setState(state);
        org2.setAddresses(address);
        updates = mapper.valueToTree(org2);
        objectMapper.readerForUpdating(org).readValue(mapper.writeValueAsString(updates));

        System.out.println(org);
    }


    // Utility to avoid null lists
    public  static <T> List<T> nvl(List<T> list) {
        return (list == null) ? new ArrayList<>() : list;
    }
}
