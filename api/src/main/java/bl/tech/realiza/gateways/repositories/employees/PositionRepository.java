package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, String> {
}
