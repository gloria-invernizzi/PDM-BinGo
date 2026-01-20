package com.application.bingo.ui.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.application.bingo.model.WasteItem;
import com.application.bingo.repository.WasteRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.util.Log;

@RunWith(MockitoJUnitRunner.class)
public class WasteViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    @Mock
    WasteRepository repository;

    WasteViewModel viewModel;

    List<WasteItem> items;

    @Before
    public void setUp() {
        viewModel = new WasteViewModel(repository);
        items = List.of(
                new WasteItem("Bottiglia", "PLASTIC"),
                new WasteItem("Bucce", "ORGANIC"),
                new WasteItem("Giornale", "PAPER")
        );

    }

    @Test
    public void loadWasteData_setsItems() {
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");

        assertNotNull(viewModel.getFilteredWasteItems().getValue());
        assertEquals(3, viewModel.getFilteredWasteItems().getValue().size());

        verify(repository).loadWasteItems("it");
    }

    @Test
    public void filterWaste_byText() {
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("bottiglia", "ALL");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("PLASTIC", results.get(0).getCategory());
    }

    @Test
    public void filterWaste_byCategory() {
        when(repository.loadWasteItems("it")).thenReturn(items);
        when(repository.loadCategories("it")).thenReturn(List.of("PLASTIC", "ORGANIC"));

        viewModel.loadWasteData("it");
        viewModel.filterWaste("", "PAPER");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("PAPER", results.get(0).getCategory());
    }

    @Test
    public void filterWaste_matchTextAndCategory() {
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("gior", "PAPER");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Giornale", results.get(0).getTitle());
    }

    @Test
    public void filterWaste_noMatch() {
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("xyz", "ALL");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void filterWaste_caseInsensitive() {
        // Ricerca caseInsensitive
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("BoTtIgLiA", "ALL");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Bottiglia", results.get(0).getTitle());
    }

    @Test
    public void filterWaste_categoryMatch_textNoMatch() {
        // Verifica quando il testo non matcha
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("zzz", "PLASTIC");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void filterWaste_allCategoryIgnoresCategoryFilter() {
        when(repository.loadWasteItems("it")).thenReturn(items);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("gi", "ALL"); // match su Giornale

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Giornale", results.get(0).getTitle());
    }

    @Test
    public void filterWaste_itemWithNullTitle_doesNotCrash() {
        WasteItem nullTitleItem = new WasteItem(null, "PLASTIC");
        List<WasteItem> itemsWithNull = List.of(nullTitleItem);

        when(repository.loadWasteItems("it")).thenReturn(itemsWithNull);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("bottiglia", "ALL");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void filterWaste_itemWithNullCategory_doesNotCrash() {
        WasteItem nullCategoryItem = new WasteItem("Bottiglia", null);
        List<WasteItem> itemsWithNull = List.of(nullCategoryItem);

        when(repository.loadWasteItems("it")).thenReturn(itemsWithNull);

        viewModel.loadWasteData("it");
        viewModel.filterWaste("bottiglia", "PLASTIC");

        List<WasteItem> results = viewModel.getFilteredWasteItems().getValue();
        assertNotNull(results);
        assertEquals(0, results.size());
    }


}
