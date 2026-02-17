package org.example.pojomanipulation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
    public class Service {
        private String id;
        private String name;
        private BigDecimal price;
        private String owningDepartmentId;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getOwningDepartmentId() { return owningDepartmentId; }
        public void setOwningDepartmentId(String owningDepartmentId) { this.owningDepartmentId = owningDepartmentId; }
    }