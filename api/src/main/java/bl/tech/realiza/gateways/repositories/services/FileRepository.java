package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.services.FileDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<FileDocument, ObjectId> {
}
