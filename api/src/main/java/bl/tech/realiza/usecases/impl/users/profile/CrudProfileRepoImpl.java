package bl.tech.realiza.usecases.impl.users.profile;

import bl.tech.realiza.domains.user.profile.ProfileRepo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepoRepository;
import bl.tech.realiza.gateways.requests.users.profile.ProfileRepoRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileRepoResponseDto;
import bl.tech.realiza.usecases.interfaces.users.profile.CrudProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudProfileRepoImpl implements CrudProfileRepo {
    private final ProfileRepoRepository profileRepoRepository;

    @Override
    public ProfileRepoResponseDto save(ProfileRepoRequestDto profileRepoRequestDto) {
        return toDto(profileRepoRepository.save(ProfileRepo.builder()
                .name(profileRepoRequestDto.getName())
                .admin(profileRepoRequestDto.getAdmin())
                .viewer(profileRepoRequestDto.getViewer())
                .manager(profileRepoRequestDto.getManager())
                .inspector(profileRepoRequestDto.getInspector())
                .documentViewer(profileRepoRequestDto.getDocumentViewer())
                .registrationUser(profileRepoRequestDto.getRegistrationUser())
                .registrationContract(profileRepoRequestDto.getRegistrationContract())
                .laboral(profileRepoRequestDto.getLaboral())
                .workplaceSafety(profileRepoRequestDto.getWorkplaceSafety())
                .registrationAndCertificates(profileRepoRequestDto.getRegistrationAndCertificates())
                .general(profileRepoRequestDto.getGeneral())
                .health(profileRepoRequestDto.getHealth())
                .environment(profileRepoRequestDto.getEnvironment())
                .concierge(profileRepoRequestDto.getConcierge())
                .build()));
    }

    @Override
    public ProfileRepoResponseDto findOne(String id) {
        return toDto(profileRepoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProfileRepo not found")));
    }

    @Override
    public List<ProfileRepoResponseDto> findAll() {
        return toDto(profileRepoRepository.findAll());
    }

    @Override
    public ProfileRepoResponseDto update(String id, ProfileRepoRequestDto profileRepoRequestDto) {
        ProfileRepo profile = profileRepoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProfileRepo not found"));

        profile.setName(profileRepoRequestDto.getName() != null
                ? profileRepoRequestDto.getName()
                : profile.getName());
        profile.setAdmin(profileRepoRequestDto.getAdmin() != null
                ? profileRepoRequestDto.getAdmin()
                : profile.getAdmin());
        profile.setViewer(profileRepoRequestDto.getViewer() != null
                ? profileRepoRequestDto.getViewer()
                : profile.getViewer());
        profile.setManager(profileRepoRequestDto.getManager() != null
                ? profileRepoRequestDto.getManager()
                : profile.getManager());
        profile.setInspector(profileRepoRequestDto.getInspector() != null
                ? profileRepoRequestDto.getInspector()
                : profile.getInspector());
        profile.setDocumentViewer(profileRepoRequestDto.getDocumentViewer() != null
                ? profileRepoRequestDto.getDocumentViewer()
                : profile.getDocumentViewer());
        profile.setRegistrationUser(profileRepoRequestDto.getRegistrationUser() != null
                ? profileRepoRequestDto.getRegistrationUser()
                : profile.getRegistrationUser());
        profile.setRegistrationContract(profileRepoRequestDto.getRegistrationContract() != null
                ? profileRepoRequestDto.getRegistrationContract()
                : profile.getRegistrationContract());
        profile.setLaboral(profileRepoRequestDto.getLaboral() != null
                ? profileRepoRequestDto.getLaboral()
                : profile.getLaboral());
        profile.setWorkplaceSafety(profileRepoRequestDto.getWorkplaceSafety() != null
                ? profileRepoRequestDto.getWorkplaceSafety()
                : profile.getWorkplaceSafety());
        profile.setRegistrationAndCertificates(profileRepoRequestDto.getRegistrationAndCertificates() != null
                ? profileRepoRequestDto.getRegistrationAndCertificates()
                : profile.getRegistrationAndCertificates());
        profile.setGeneral(profileRepoRequestDto.getGeneral() != null
                ? profileRepoRequestDto.getGeneral()
                : profile.getGeneral());
        profile.setHealth(profileRepoRequestDto.getHealth() != null
                ? profileRepoRequestDto.getHealth()
                : profile.getHealth());
        profile.setEnvironment(profileRepoRequestDto.getEnvironment() != null
                ? profileRepoRequestDto.getEnvironment()
                : profile.getEnvironment());
        profile.setConcierge(profileRepoRequestDto.getConcierge() != null
                ? profileRepoRequestDto.getConcierge()
                : profile.getConcierge());

        return null;
    }

    @Override
    public void delete(String id) {
        profileRepoRepository.deleteById(id);
    }

    @Override
    public Boolean checkIfExistsByName(String name) {
        return profileRepoRepository.existsByName(name);
    }

    private ProfileRepoResponseDto toDto(ProfileRepo profile) {
        return ProfileRepoResponseDto.builder()
                .id(profile.getId())
                .name(profile.getName())
                .admin(profile.getAdmin())
                .viewer(profile.getViewer())
                .manager(profile.getManager())
                .inspector(profile.getInspector())
                .documentViewer(profile.getDocumentViewer())
                .registrationUser(profile.getRegistrationUser())
                .registrationContract(profile.getRegistrationContract())
                .laboral(profile.getLaboral())
                .workplaceSafety(profile.getWorkplaceSafety())
                .registrationAndCertificates(profile.getRegistrationAndCertificates())
                .general(profile.getGeneral())
                .health(profile.getHealth())
                .environment(profile.getEnvironment())
                .concierge(profile.getConcierge())
                .build();
    }

    private List<ProfileRepoResponseDto> toDto(List<ProfileRepo> profiles) {
        return profiles.stream().map(this::toDto).toList();
    }
}
