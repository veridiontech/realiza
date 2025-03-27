package bl.tech.realiza.usecases.impl.ultragaz;

import bl.tech.realiza.domains.ultragaz.Board;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.ultragaz.BoardRepository;
import bl.tech.realiza.gateways.requests.ultragaz.BoardRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.BoardResponseDto;
import bl.tech.realiza.usecases.interfaces.ultragaz.CrudBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudBoardImpl implements CrudBoard {

    private final BoardRepository boardRepository;

    @Override
    public BoardResponseDto save(BoardRequestDto request) {
        Board board = Board.builder()
                .name(request.getName())
                .build();

        Board saved = boardRepository.save(board);
        return toResponse(saved);
    }

    @Override
    public BoardResponseDto findOne(String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Board não encontrado"));
        return toResponse(board);
    }

    @Override
    public Page<BoardResponseDto> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public BoardResponseDto update(String id, BoardRequestDto request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Board não encontrado"));

        board.setName(request.getName() != null ? request.getName() : board.getName());

        Board updated = boardRepository.save(board);
        return toResponse(updated);
    }

    @Override
    public void delete(String id) {
        boardRepository.deleteById(id);
    }

    public BoardResponseDto toResponse(Board board) {
        return BoardResponseDto.builder()
                .idBoard(board.getIdBoard())
                .name(board.getName())
                .build();
    }
}