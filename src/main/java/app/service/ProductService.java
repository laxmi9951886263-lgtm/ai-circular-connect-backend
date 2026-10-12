
package app.service;

import app.entity.Product;
import app.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        validateProduct(product);

        if ("DONATE".equalsIgnoreCase(product.getListingType())) {
            product.setPrice(java.math.BigDecimal.ZERO);
        }

        product.setStatus("AVAILABLE");

        return productRepository.save(product);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStatus("AVAILABLE");
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found with id: " + id));
    }

    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category is required");
        }

        return productRepository.findByCategoryIgnoreCase(
                category.trim());
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product details are required");
        }

        if (product.getTitle() == null
                || product.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Product title is required");
        }

        if (product.getCategory() == null
                || product.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Product category is required");
        }

        if (product.getItemCondition() == null
                || product.getItemCondition().isBlank()) {
            throw new IllegalArgumentException(
                    "Product condition is required");
        }

        if (product.getListingType() == null
                || (!product.getListingType().equalsIgnoreCase("SELL")
                && !product.getListingType().equalsIgnoreCase("DONATE"))) {
            throw new IllegalArgumentException(
                    "Listing type must be SELL or DONATE");
        }

        if (product.getAgeInMonths() != null
                && product.getAgeInMonths() < 0) {
            throw new IllegalArgumentException(
                    "Product age cannot be negative");
        }

        if (product.getPrice() == null
                || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be negative");
        }
    }
}
