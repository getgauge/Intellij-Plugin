package com.thoughtworks.gauge.findUsages;

import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.ArrayList;
import java.util.List;

public class StepsCollection {
    private List<SpecStepImpl> specSteps = new ArrayList<SpecStepImpl>();
    private List<ConceptStepImpl> conceptSteps = new ArrayList<ConceptStepImpl>();

    public StepsCollection(List<SpecStepImpl> specSteps, List<ConceptStepImpl> conceptSteps) {
        this.specSteps = specSteps;
        this.conceptSteps = conceptSteps;
    }

    public List<SpecStepImpl> getSpecSteps() {
        return specSteps;
    }

    public List<ConceptStepImpl> getConceptSteps() {
        return conceptSteps;
    }
}
