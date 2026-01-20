package com.application.bingo.ui.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.application.bingo.model.Product;
import com.application.bingo.model.Result;
import com.application.bingo.model.relation.ProductWithPackagings;
import com.application.bingo.repository.product.ProductRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ProductViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    ProductRepository repository;

    ProductViewModel viewModel;
    MutableLiveData<Result> favLive;

    @Before
    public void setUp() {
        favLive = new MutableLiveData<>();
        when(repository.getFavoriteProducts()).thenReturn(favLive);

        viewModel = new ProductViewModel(repository);
        viewModel.getFavoriteProductsLiveData();
    }

    @Test
    public void searchQuery_filtersFavorites() {

        ProductWithPackagings item = new ProductWithPackagings();
        item.setProduct(new Product());
        item.getProduct().setName("Milk");
        item.getProduct().setBrand("BrandA");
        item.getProduct().setBarcode("123");

        // Setta i dati nella LiveData
        favLive.setValue(new Result.Success<>(Collections.singletonList(item)));

        // Associa un observer per attivare MediatorLiveData
        viewModel.getFilteredFavorites().observeForever(r -> {});

        // Fai partire il filtro
        viewModel.setSearchQuery("Milk");

        // Verifica il risultato filtrato
        Result filtered = viewModel.getFilteredFavorites().getValue();
        assertNotNull(filtered);
        assertTrue(filtered instanceof Result.Success);

        List<ProductWithPackagings> list =
                ((Result.Success<List<ProductWithPackagings>>) filtered).getData();
        assertEquals(1, list.size());
        assertEquals("Milk", list.get(0).getProduct().getName());
    }

    @Test
    public void emptyQuery_returnsFullList() {
        ProductWithPackagings item = new ProductWithPackagings();
        item.setProduct(new Product());
        item.getProduct().setName("Milk");
        item.getProduct().setBrand("BrandA");
        item.getProduct().setBarcode("123");

        favLive.setValue(new Result.Success<>(Collections.singletonList(item)));

        viewModel.getFilteredFavorites().observeForever(r -> {});
        viewModel.setSearchQuery("");

        Result result = viewModel.getFilteredFavorites().getValue();
        assertTrue(result instanceof Result.Success);

        List<ProductWithPackagings> list =
                ((Result.Success<List<ProductWithPackagings>>) result).getData();

        assertEquals(1, list.size());
    }

    @Test
    public void queryNoMatch_returnsEmptyList() {
        ProductWithPackagings item = new ProductWithPackagings();
        item.setProduct(new Product());
        item.getProduct().setName("Milk");
        item.getProduct().setBrand("BrandA");
        item.getProduct().setBarcode("123");

        favLive.setValue(new Result.Success<>(Collections.singletonList(item)));

        viewModel.getFilteredFavorites().observeForever(r -> {});
        viewModel.setSearchQuery("xyz");

        Result result = viewModel.getFilteredFavorites().getValue();
        assertTrue(result instanceof Result.Success);

        List<ProductWithPackagings> list =
                ((Result.Success<List<ProductWithPackagings>>) result).getData();

        assertEquals(0, list.size());
    }

    @Test
    public void queryCaseInsensitiveAndTrimmed() {
        ProductWithPackagings item = new ProductWithPackagings();
        item.setProduct(new Product());
        item.getProduct().setName("Milk");
        item.getProduct().setBrand("BrandA");
        item.getProduct().setBarcode("123");

        favLive.setValue(new Result.Success<>(Collections.singletonList(item)));

        viewModel.getFilteredFavorites().observeForever(r -> {});
        viewModel.setSearchQuery("   mILk   ");

        Result result = viewModel.getFilteredFavorites().getValue();
        assertTrue(result instanceof Result.Success);

        List<ProductWithPackagings> list =
                ((Result.Success<List<ProductWithPackagings>>) result).getData();

        assertEquals(1, list.size());
    }

    @Test
    public void queryMatchesBrand() {
        ProductWithPackagings item = new ProductWithPackagings();
        item.setProduct(new Product());
        item.getProduct().setName("Milk");
        item.getProduct().setBrand("BrandA");
        item.getProduct().setBarcode("123");

        favLive.setValue(new Result.Success<>(Collections.singletonList(item)));

        viewModel.getFilteredFavorites().observeForever(r -> {});
        viewModel.setSearchQuery("brand");

        Result result = viewModel.getFilteredFavorites().getValue();
        assertTrue(result instanceof Result.Success);

        List<ProductWithPackagings> list =
                ((Result.Success<List<ProductWithPackagings>>) result).getData();

        assertEquals(1, list.size());
    }

    @Test
    public void queryMatchesBarcode() {
        ProductWithPackagings item = new ProductWithPackagings();
        item.setProduct(new Product());
        item.getProduct().setName("Milk");
        item.getProduct().setBrand("BrandA");
        item.getProduct().setBarcode("123456");

        favLive.setValue(new Result.Success<>(Collections.singletonList(item)));

        viewModel.getFilteredFavorites().observeForever(r -> {});
        viewModel.setSearchQuery("345");

        Result result = viewModel.getFilteredFavorites().getValue();
        assertTrue(result instanceof Result.Success);

        List<ProductWithPackagings> list =
                ((Result.Success<List<ProductWithPackagings>>) result).getData();

        assertEquals(1, list.size());
    }


}
