package bl.tech.realiza.gateways.controllers.interfaces.ultragaz;

import bl.tech.realiza.gateways.requests.ultragaz.BoardRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.BoardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface BoardController {
    ResponseEntity<BoardResponseDto> createBoard(BoardRequestDto boardRequestDto);
    ResponseEntity<BoardResponseDto> getOneBoard(String id);
    ResponseEntity<Page<BoardResponseDto>> getAllBoards(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Page<BoardResponseDto>> getAllBoardsByClient(int page, int size, String sort, Sort.Direction direction, String idClient);
    ResponseEntity<BoardResponseDto> updateBoard(String id, BoardRequestDto boardRequestDto);
    ResponseEntity<Void> deleteBoard(String id);
}
