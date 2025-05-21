package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Cbo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.employees.CboRepository;
import bl.tech.realiza.gateways.requests.employees.CboRequestDto;
import bl.tech.realiza.gateways.responses.employees.CboResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudCbo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudCboImpl implements CrudCbo {
    private final CboRepository cboRepository;

    @Override
    public CboResponseDto save(CboRequestDto cboRequestDto) {
        Cbo cbo = Cbo.builder()
                .code(cboRequestDto.getCode())
                .title(cboRequestDto.getTitle())
                .build();

        return toResponse(cboRepository.save(cbo));
    }

    @Override
    public CboResponseDto findOne(String id) {
        return toResponse(cboRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cbo not found")));
    }

    @Override
    public CboResponseDto update(String id, CboRequestDto cboRequestDto) {
        Cbo cbo = cboRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cbo not found"));

        cbo.setCode(cboRequestDto.getCode() != null ? cboRequestDto.getCode() : cbo.getCode());
        cbo.setTitle(cboRequestDto.getTitle() != null ? cboRequestDto.getTitle() : cbo.getTitle());

        return toResponse(cboRepository.save(cbo));
    }

    @Override
    public List<CboResponseDto> findAll() {
        return toResponse(cboRepository.findAll());
    }

    @Override
    public void delete(String id) {
        cboRepository.deleteById(id);
    }

    private CboResponseDto toResponse(Cbo cbo) {
        return CboResponseDto.builder()
                .id(cbo.getId())
                .code(cbo.getCode())
                .title(cbo.getTitle())
                .build();
    }

    private List<CboResponseDto> toResponse(List<Cbo> cboList) {
        return cboList.stream()
                .sorted(Comparator.comparing(Cbo::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toResponse)
                .toList();
    }
}
