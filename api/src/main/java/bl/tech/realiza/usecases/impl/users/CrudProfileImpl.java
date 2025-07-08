package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.user.Profile;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.users.ProfileRepository;
import bl.tech.realiza.gateways.requests.users.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.ProfileResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudProfileImpl implements CrudProfile {
    private final ProfileRepository profileRepository;
    private final ClientRepository clientRepository;

    @Override
    public ProfileResponseDto save(ProfileRequestDto profileRequestDto) {
        Client client = clientRepository.findById(profileRequestDto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        return toDto(profileRepository.save(Profile.builder()
                .name(profileRequestDto.getName())
                .admin(profileRequestDto.getAdmin())
                .viewer(profileRequestDto.getViewer())
                .manager(profileRequestDto.getManager())
                .inspector(profileRequestDto.getInspector())
                .documentViewer(profileRequestDto.getDocumentViewer())
                .registration(profileRequestDto.getRegistration())
                .laboral(profileRequestDto.getLaboral())
                .workplaceSafety(profileRequestDto.getWorkplaceSafety())
                .registrationAndCertificates(profileRequestDto.getRegistrationAndCertificates())
                .general(profileRequestDto.getGeneral())
                .health(profileRequestDto.getHealth())
                .environment(profileRequestDto.getEnvironment())
                .concierge(profileRequestDto.getConcierge())
                .client(client)
                .build()));
    }

    @Override
    public ProfileResponseDto findOne(String id) {
        return toDto(profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found")));
    }

    @Override
    public List<ProfileResponseDto> findAll() {
        return toDto(profileRepository.findAll());
    }

    @Override
    public List<ProfileNameResponseDto> findAllByClientId(String clientId) {
        return profileRepository.findAllByClient_IdClient(clientId).stream().map(
                profile -> ProfileNameResponseDto.builder()
                        .id(profile.getId())
                        .profileName(profile.getName())
                        .build()
        ).toList();
    }

    @Override
    public ProfileResponseDto update(String id, ProfileRequestDto profileRequestDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        profile.setName(profileRequestDto.getName() != null
                ? profileRequestDto.getName()
                : profile.getName());
        profile.setAdmin(profileRequestDto.getAdmin() != null
                ? profileRequestDto.getAdmin()
                : profile.getAdmin());
        profile.setViewer(profileRequestDto.getViewer() != null
                ? profileRequestDto.getViewer()
                : profile.getViewer());
        profile.setManager(profileRequestDto.getManager() != null
                ? profileRequestDto.getManager()
                : profile.getManager());
        profile.setInspector(profileRequestDto.getInspector() != null
                ? profileRequestDto.getInspector()
                : profile.getInspector());
        profile.setDocumentViewer(profileRequestDto.getDocumentViewer() != null
                ? profileRequestDto.getDocumentViewer()
                : profile.getDocumentViewer());
        profile.setRegistration(profileRequestDto.getRegistration() != null
                ? profileRequestDto.getRegistration()
                : profile.getRegistration());
        profile.setLaboral(profileRequestDto.getLaboral() != null
                ? profileRequestDto.getLaboral()
                : profile.getLaboral());
        profile.setWorkplaceSafety(profileRequestDto.getWorkplaceSafety() != null
                ? profileRequestDto.getWorkplaceSafety()
                : profile.getWorkplaceSafety());
        profile.setRegistrationAndCertificates(profileRequestDto.getRegistrationAndCertificates() != null
                ? profileRequestDto.getRegistrationAndCertificates()
                : profile.getRegistrationAndCertificates());
        profile.setGeneral(profileRequestDto.getGeneral() != null
                ? profileRequestDto.getGeneral()
                : profile.getGeneral());
        profile.setHealth(profileRequestDto.getHealth() != null
                ? profileRequestDto.getHealth()
                : profile.getHealth());
        profile.setEnvironment(profileRequestDto.getEnvironment() != null
                ? profileRequestDto.getEnvironment()
                : profile.getEnvironment());
        profile.setConcierge(profileRequestDto.getConcierge() != null
                ? profileRequestDto.getConcierge()
                : profile.getConcierge());

        return null;
    }

    @Override
    public void delete(String id) {
        profileRepository.deleteById(id);
    }

    private ProfileResponseDto toDto(Profile profile) {
        return ProfileResponseDto.builder()
                .id(profile.getId())
                .name(profile.getName())
                .admin(profile.getAdmin())
                .viewer(profile.getViewer())
                .manager(profile.getManager())
                .inspector(profile.getInspector())
                .documentViewer(profile.getDocumentViewer())
                .registration(profile.getRegistration())
                .laboral(profile.getLaboral())
                .workplaceSafety(profile.getWorkplaceSafety())
                .registrationAndCertificates(profile.getRegistrationAndCertificates())
                .general(profile.getGeneral())
                .health(profile.getHealth())
                .environment(profile.getEnvironment())
                .concierge(profile.getConcierge())
                .clientId(profile.getClient() != null
                        ? profile.getClient().getIdClient()
                        : null)
                .build();
    }

    private List<ProfileResponseDto> toDto(List<Profile> profiles) {
        return profiles.stream().map(this::toDto).toList();
    }
}
