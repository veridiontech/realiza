package bl.tech.realiza.domains.ultragaz;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrixSubgroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import java.util.List;

public class Center {

    @OneToMany(mappedBy = "center", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Market> markets;
}
