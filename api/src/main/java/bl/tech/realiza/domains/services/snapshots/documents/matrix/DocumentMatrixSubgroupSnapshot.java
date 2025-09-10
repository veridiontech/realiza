//package bl.tech.realiza.domains.services.snapshots.documents.matrix;
//
//import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
//import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class DocumentMatrixSubgroupSnapshot {
//    @EmbeddedId
//    private SnapshotId id;
//    private String name;
//    @Builder.Default
//    private LocalDateTime creationDate = LocalDateTime.now();
//
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "groupId", referencedColumnName = "id"),
//            @JoinColumn(name = "groupFrequency", referencedColumnName = "frequency"),
//            @JoinColumn(name = "groupSnapshotDate", referencedColumnName = "snapshotDate")
//    })
//    @JsonManagedReference
//    private DocumentMatrixGroupSnapshot group;
//
//    @OneToMany(mappedBy = "subgroup", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonBackReference
//    private List<DocumentMatrixSnapshot> documentsMatrix;
//}
