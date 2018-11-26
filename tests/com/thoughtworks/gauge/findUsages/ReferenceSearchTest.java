package com.thoughtworks.gauge.findUsages;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.thoughtworks.gauge.findUsages.helper.ReferenceSearchHelper;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ReferenceSearchTest extends LightCodeInsightFixtureTestCase {

    private Project project;
    private SpecStepImpl element;
    private ReferenceSearchHelper helper;
    private StepCollector collector;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        element = mock(SpecStepImpl.class);
        helper = mock(ReferenceSearchHelper.class);
        collector = mock(StepCollector.class);
        project = myFixture.getProject();
    }

    @Test
    public void testShouldNotFindReferencesOfNonGaugeElement() throws Exception {
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);
        when(helper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())).thenReturn(false);

        new ReferenceSearch(helper).processQuery(searchParameters, psiReference -> false);

        verify(helper, never()).getPsiElements(any(StepCollector.class), any(PsiElement.class));
    }

    @Test
    public void testShouldFindReferencesOfGaugeElement() throws Exception {
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);

        when(helper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())).thenReturn(true);
        when(helper.getStepCollector(element)).thenReturn(collector);

        new ReferenceSearch(helper).processQuery(searchParameters, psiReference -> false);

        verify(helper, times(1)).getPsiElements(collector, element);
    }
}