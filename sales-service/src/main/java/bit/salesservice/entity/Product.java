package bit.salesservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "shopping_bag", schema = "sales")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checkout_id", referencedColumnName = "id")
    private Checkout checkout;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "applied_campaign")
    private String appliedCampaign;

    @Column(name = "quantity", nullable = false, columnDefinition = "integer default 1")
    private Integer quantity;

    @Column(name = "returned_quantity")
    private Integer returnedQuantity = 0;

    @Column(name = "is_returned", columnDefinition = "boolean default false")
    private boolean isReturned;

    @Column(name = "is_removed", columnDefinition = "boolean default false")
    private boolean isRemoved;

    @Column(name = "price", nullable = false)
    private double price;
}
