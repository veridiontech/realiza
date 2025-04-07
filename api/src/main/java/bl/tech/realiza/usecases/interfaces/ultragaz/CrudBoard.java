package bl.tech.realiza.usecases.interfaces.ultragaz;

import bl.tech.realiza.gateways.requests.ultragaz.BoardRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.gateways.responses.ultragaz.BoardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudBoard {
    BoardResponseDto save(BoardRequestDto request);
    BoardResponseDto findOne(String id);
    Page<BoardResponseDto> findAll(Pageable pageable);
    Page<BoardResponseDto> findAllByClient(String idClient, Pageable pageable);
    BoardResponseDto update(String id, BoardRequestDto request);
    void delete(String id);
}