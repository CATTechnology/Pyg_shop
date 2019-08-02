package top.takefly.core.pojo.entity;

import top.takefly.core.pojo.specification.Specification;
import top.takefly.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddSpecification implements Serializable {

    private Specification specification;

    private List<SpecificationOption> specificationOptions = new ArrayList<SpecificationOption>();

    public AddSpecification(){

    }

    public AddSpecification(Specification specification, List<SpecificationOption> specificationOptions) {
        this.specification = specification;
        this.specificationOptions = specificationOptions;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptions() {
        return specificationOptions;
    }

    public void setSpecificationOptions(List<SpecificationOption> specificationOptions) {
        this.specificationOptions = specificationOptions;
    }
}
