package Petplan;

import lombok.Data;

@Data
public class Policy {

    private String policyScheme;
    private String deductible;
    private String copay;
    private PetType petType;
    private String breed;
    private int age;
    private String zipCode;
    private String state;

    public Policy() {

    }
}
