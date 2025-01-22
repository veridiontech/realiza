package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import bl.tech.realiza.domains.contract.Activity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUPPLIER")
public class ProviderSupplier extends Provider {
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "SUPPLIER_BRANCHS",
            joinColumns = @JoinColumn(name = "idProvider"),
            inverseJoinColumns = @JoinColumn(name = "idBranch")
    )
    private List<Branch> branches;
}
