package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityDocumentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityDocumentRepository extends JpaRepository<ActivityDocuments, String> {
    List<ActivityDocuments> findAllByActivity_IdActivity(String idActivity);
    ActivityDocuments findByActivity_IdActivityAndDocumentBranch_IdDocumentation(String idActivity, String idDocumentBranch);
    List<ActivityDocuments> findAllByDocumentBranch_Branch_IdBranch(String idBranch);
    List<ActivityDocuments> findAllByActivity_Branch_Client_IdClientAndDocumentBranch_Branch_Client_IdClient(String activityIdClient, String documentIdClient);

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.contracts.activity.ActivityDocumentResponseDto(
        ad.id,
        db.idDocumentation,
        ad.activity.idActivity,
        db.title,
        CASE WHEN ad.documentBranch.idDocumentation IS NOT NULL THEN TRUE ELSE FALSE END
    )
    FROM ActivityDocuments ad
    JOIN ad.documentBranch db
    WHERE ad.activity.idActivity = :idActivity
""")
    List<ActivityDocumentResponseDto> findDocumentsByActivity(@Param("idActivity") String idActivity);

    List<ActivityDocuments> findAllByActivity_Branch_Client_IdClientAndActivity_TitleAndDocumentBranch_Branch_Client_IdClientAndDocumentBranch_Title(String idClient, String title, String idClient1, String title1);
}
