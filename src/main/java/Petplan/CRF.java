package Petplan;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static Petplan.PolicyPlan.*;

public class CRF {
    public Policy policy;
    public MyExcel excel;
    public static final String blueprint19 = "Blueprint - Canada (Rates) 20190703 1145 am.xlsx";
    public static final String blueprint18 = "Blueprint - Canada (2018 Rates)_20181106 0905 am.xlsx";

    public String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";

    public CRF() {
        ScanData scan = new ScanData();
        String blueprint = scan.scanForRateDoc();

        if(blueprint.equals("18"))
            filePath = filePath + blueprint18;
        else
            filePath = filePath + blueprint19;

        Thread thread = new Thread("New Thread") {
            public void run() {
                excel = new MyExcel(filePath);
            }
        };
        thread.start();
        policy = scan.scanAndGetPolicy();
    }

    private int getBaseRateColumn() {
        for (int i = 0; i < baseRates.length; i++) {
            if (baseRates[i].toLowerCase().equals(policy.getPolicyScheme()))
                if (policy.getPetType().equals(PetType.CAT))
                    return i + 2;
                else
                    return i + baseRates.length + 2;
        }
        return -1;
    }

    private int getBaseRateRow() {
        excel.openWorkSheet("Base Rate");
        //return excel.finedInColumn(0, policy.getState(), 4, 54);
        for (int i = 0; i < states.length; i++) {
            if (states[i].toLowerCase().equals(policy.getState().toLowerCase()))
                return i + 3;
        }
        return -1;
    }

    private void showBaseRate() {
        excel.openWorkSheet("Base Rate");
        int i = getBaseRateRow();
        if (i == -1) {
            System.out.println("Cannot fined state in sheet.");
            return;
        }
        int j = getBaseRateColumn();
        if (j == -1) {
            System.out.println("Cannot fined scheme for pet in sheet.");
            return;
        }

        System.out.println("Base Rate: " + excel.readCell(i, j));
    }

    private void showAgeFactor() {
        if(policy.getBlueprint().equals("19"))
            showAgeFactor_19();
        else
            showBreedAndAgeFactor_18();
    }

    private void showAgeFactor_19() {
        excel.openWorkSheet("Breed to Breed Grp Mapping 2019");
        String breedVal = this.policy.getPetType().equals(PetType.DOG) ? "ppdog001" : "ppcat001";
        int i = excel.finedInColumn(1, policy.getBreed(), 2, breedVal, 1, 628);
        if (i == -1) {
            System.out.println("Cannot fined breed in sheet.");
            return;
        }
        String groupId = excel.readCell(i, 7);
        System.out.println("Pet group Id is: " + groupId);

        excel.openWorkSheet("Combined Breed Grp-Age Factors ");

        int baseLine = excel.finedInColumn(0, policy.getPetType().toString(), 1, groupId, 8, 1067);
        if (baseLine == -1) {
            System.out.println("Cannot fined group Id for pet in sheet.");
            return;
        }

        System.out.println("Age Factor: " + excel.readCell(baseLine + policy.getAge(), 3));
    }

    private void showBreedAndAgeFactor_18() {
        excel.openWorkSheet("Breed");
        String breedVal = this.policy.getPetType().equals(PetType.DOG) ? "ppdog001" : "ppcat001";
        int i = excel.finedInColumn(1, policy.getBreed(), 2, breedVal, 1, 628);
        if (i == -1) {
            System.out.println("Cannot fined breed in sheet.");
            return;
        }
        String groupId = excel.readCell(i, 4);
        System.out.println("Pet group Id is: " + groupId);

        excel.openWorkSheet("Age");

        int baseLine = excel.finedInColumn(0, policy.getPetType().toString(), 1, groupId, 8, 1067);
        if (baseLine == -1) {
            System.out.println("Cannot fined group Id for pet in sheet.");
            return;
        }

        System.out.println("Age Factor: " + excel.readCell(baseLine + policy.getAge(), 3));
    }

    private void showAreaLookup() {
        Map<String, Integer> areaLookup = new HashMap();
        areaLookup.put("NL", 3);
        areaLookup.put("NS", 2);
        areaLookup.put("PE", 1);
        areaLookup.put("ON", 3);
        areaLookup.put("MB", 2);
        areaLookup.put("SK", 2);
        areaLookup.put("AB", 4);
        areaLookup.put("BC", 3);
        areaLookup.put("NU", 4);
        areaLookup.put("NT", 4);
        areaLookup.put("YT", 4);


        if (policy.getState().trim().toUpperCase().equals("ON")
                && policy.getZipCode().trim().toUpperCase().startsWith("M")) {
            areaLookup.put("ON", 4);
        }

        if (policy.getState().trim().toUpperCase().equals("AB")
                && policy.getZipCode().trim().toUpperCase().startsWith("T0")) {
            areaLookup.put("AB", 3);
        }

        if (policy.getState().trim().toUpperCase().equals("BC")
                && policy.getZipCode().trim().toUpperCase().startsWith("V0")) {
            areaLookup.put("BC", 2);
        }

        showLookupFactor(areaLookup);
    }

    private void showLookupFactor(Map<String, Integer> areaLookup) {
        int num = areaLookup.get(policy.getState().trim().toUpperCase());
        System.out.println("Pet area lookup is: " + num);

        excel.openWorkSheet("Area");
        int deep = policy.getPetType().equals(PetType.CAT) ? 0 : 4;
        System.out.println("Pet Area Factor is: " +
                excel.readCell(2 + num + deep, 2));
    }

    private void showAreaLookup_2018() {
        Map<String, Integer> areaLookup = new HashMap();
        areaLookup.put("NL", 2);
        areaLookup.put("NS", 3);
        areaLookup.put("PE", 4);
        areaLookup.put("ON", 2);
        areaLookup.put("MB", 3);
        areaLookup.put("SK", 3);
        areaLookup.put("AB", 1);
        areaLookup.put("BC", 2);
        areaLookup.put("NU", 1);
        areaLookup.put("NT", 1);
        areaLookup.put("YT", 1);


        if (policy.getState().trim().toUpperCase().equals("ON")
                && policy.getZipCode().trim().toUpperCase().startsWith("M")) {
            areaLookup.put("ON", 1);
        }

        if (policy.getState().trim().toUpperCase().equals("AB")
                && policy.getZipCode().trim().toUpperCase().startsWith("T0")) {
            areaLookup.put("AB", 2);
        }

        if (policy.getState().trim().toUpperCase().equals("BC")
                && policy.getZipCode().trim().toUpperCase().startsWith("V0")) {
            areaLookup.put("BC", 3);
        }

        showLookupFactor(areaLookup);
    }

    private void showDeductibleFactor() {
        excel.openWorkSheet("Deductible");
        int i = excel.finedInColumn(0, policy.getState().toUpperCase(), 2, 25);
        if (i == -1) {
            System.out.println("Cannot fined state in sheet.");
            return;
        }
        int k = -1;
        for (int j = 0; j < deductibles.length; j++) {
            if (deductibles[j].toLowerCase().equals(policy.getDeductible().trim().toLowerCase())) {
                k = j;
                break;
            }
        }
        if (k == -1) {
            System.out.println("Bad deductible.");
            return;
        }
        int j = (policy.getPetType().equals(PetType.DOG) ? deductibles.length : 0) + k + 1;
        String deductible = excel.readCell(i+1, j);
        System.out.println("Pet deductible factor is: " + deductible);
    }

    private void showCopayFactor() {
        excel.openWorkSheet("Copay");
        int i = excel.finedInColumn(0, policy.getState().toUpperCase(), 2, 25);
        if (i == -1) {
            System.out.println("Cannot fined state in sheet.");
            return;
        }
        int k = -1;
        for (int j = 0; j < copays.length; j++) {
            if (copays[j].toLowerCase().equals(policy.getCopay())) {
                k = j;
                break;
            }
        }
        if (k == -1) {
            System.out.println("Bad copay.");
            return;
        }
        int j = (policy.getPetType().equals(PetType.DOG) ? copays.length : 0) + k + 1;
        String deductible = excel.readCell(i+1, j);
        System.out.println("Pet copay factor is: " + deductible);
    }

    private void showAnnualDeductible() {
        excel.openWorkSheet("Annual Deductible");
        int i = excel.finedInColumn(0, policy.getState().toUpperCase(), 1, policy.getPetType().toString(), 2, 40);
        if (i == -1) {
            System.out.println("Cannot fined state or pet in sheet.");
            return;
        }
        String deductible = excel.readCell(i+1, 2);
        System.out.println("Pet annual deductible is: " + deductible);
    }

    private void showAgeAtInceptionFactor() {
        excel.openWorkSheet("Age-At-Inception");
        if (policy.getAge() > 20)
            throw new IllegalStateException("The selected pet Age should be less then 21 years.");

        int i = excel.finedInColumn(0, policy.getState(), 2, 700);
        if (i == -1) {
            System.out.println("Cannot fined state or pet in sheet.");
            return;
        }
        int petTypeIndex = (policy.getPetType().equals(PetType.DOG) ? 2 : 1);
        String inception = excel.readCell(i + 2 * policy.getAge() + petTypeIndex, 4);
        System.out.println("Pet Age At Inception is: " + inception);
    }

    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        disableWarning();
        CRF calculator = new CRF();
        System.out.println();
        calculator.showBaseRate();
        calculator.showAgeFactor();
        calculator.showAreaLookup();
        calculator.showDeductibleFactor();
        calculator.showCopayFactor();
        calculator.showAnnualDeductible();
        calculator.showAgeAtInceptionFactor();
    }
}
//mvn clean compile assembly:single