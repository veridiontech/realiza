package bl.tech.realiza.gateways.repositories.ultragaz;

import bl.tech.realiza.domains.ultragaz.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {
    Page<Board> findAllByClient_IdClient(String idClient, Pageable pageable);
}
