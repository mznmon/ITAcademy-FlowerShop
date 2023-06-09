package org.flowershop.service;

import org.flowershop.domain.products.Product;
import org.flowershop.exceptions.NegativeValueException;
import org.flowershop.repository.IFlowerShopRepository;
import org.flowershop.repository.IProductRepository;

import java.util.List;


public class ProductService {
    private static ProductService instance;
    private final IProductRepository repository;


    // This method gets the repository by injection dependencies.
    private ProductService(IProductRepository repository) {
        this.repository = repository;
    }

    public static ProductService getInstance(IProductRepository repository) {
        if (instance == null) {
            instance = new ProductService(repository);
        }
        return instance;
    }


    // Methods
    public void addProduct(Product product) {
        repository.addProduct(product);
    }

    public List<Product> getProducts() {
        repository.loadProducts();
        return repository.getProducts();
    }

    public Product getProductById(long id) {
        return repository.getProductById(id);
    }

    public Product getProductByRef(String ref) {
         return repository.getProductByRef(ref);
    }

    public void updateStockbyRef(String ref, int quantity) {
        Product product = getProductByRef(ref);
        try {
            product.updateStock(quantity);
        } catch (NegativeValueException e) {
            throw new RuntimeException(e);
        }
        repository.updateProduct(product);
    }

    public void updateProductById(long id, Product product) {
        repository.updateProductById(id, product);
    }
    public void updateProduct(Product product) {
        repository.updateProduct(product);
    }

    public void removeProductById(long id) {
        repository.removeProductById(id);
    }

    public void removeProductByRef(String ref) {
        repository.removeProductByRef(ref);
    }

}
