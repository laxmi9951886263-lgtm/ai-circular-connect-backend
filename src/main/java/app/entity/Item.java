
package app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Item classification is required")
    @Column(nullable = false, length = 30)
    private String classification;

    @PositiveOrZero(message = "Estimated value cannot be negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedValueInr;

    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 30)
    private String status = "AVAILABLE";

    private LocalDateTime createdAt = LocalDateTime.now();

    public Item() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public BigDecimal getEstimatedValueInr() {
        return estimatedValueInr;
    }

    public void setEstimatedValueInr(BigDecimal estimatedValueInr) {
        this.estimatedValueInr = estimatedValueInr;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
