package bit.salesservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Entity class representing a product in the shopping bag.
 */
@Entity
@Table(name = "shopping_bag", schema = "sales")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id; // Unique identifier of the product

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checkout_id", referencedColumnName = "id")
    private Checkout checkout; // Checkout associated with the product

    @Column(name = "code", nullable = false)
    private String code; // Code or identifier of the product

    @Column(name = "name", nullable = false)
    private String name; // Name of the product

    @Column(name = "applied_campaign")
    private String appliedCampaign; // Name of the campaign applied to the product (if any)

    @Column(name = "quantity", nullable = false, columnDefinition = "integer default 1")
    private Integer quantity; // Quantity of the product

    @Column(name = "returned_quantity")
    private Integer returnedQuantity = 0; // Quantity of the product returned

    @Column(name = "is_returned", columnDefinition = "boolean default false")
    private boolean isReturned; // Indicates whether the product is returned

    @Column(name = "is_removed", columnDefinition = "boolean default false")
    private boolean isRemoved; // Indicates whether the product is removed

    @Column(name = "price", nullable = false)
    private double price; // Price of the product
}
