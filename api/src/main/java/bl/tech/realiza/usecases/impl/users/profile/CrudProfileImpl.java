package bl.tech.realiza.usecases.impl.users.profile;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.user.profile.Profile;
import bl.tech.realiza.domains.user.profile.ProfileRepo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepoRepository;
import bl.tech.realiza.gateways.repositories.users.profile.ProfileRepository;
import bl.tech.realiza.gateways.requests.users.profile.ProfileRequestDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileNameResponseDto;
import bl.tech.realiza.gateways.responses.users.profile.ProfileResponseDto;
import bl.tech.realiza.usecases.interfaces.users.profile.CrudProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrudProfileImpl implements CrudProfile {
    private final ProfileRepository profileRepository;
    private final ClientRepository clientRepository;
    private final ProfileRepoRepository profileRepoRepository;

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
                .registrationUser(profileRequestDto.getRegistrationUser())
                .registrationContract(profileRequestDto.getRegistrationContract())
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
        profile.setRegistrationUser(profileRequestDto.getRegistrationUser() != null
                ? profileRequestDto.getRegistrationUser()
                : profile.getRegistrationUser());
        profile.setRegistrationContract(profileRequestDto.getRegistrationContract() != null
                ? profileRequestDto.getRegistrationContract()
                : profile.getRegistrationContract());
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

    @Transactional
    @Override
    public void transferFromRepoToClient(String clientId) {
        log.info("Started setup client profiles ⌛ {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        List<ProfileRepo> profileRepos = profileRepoRepository.findAll();
        List<Profile> profiles = new ArrayList<>();
        for (ProfileRepo profileRepo : profileRepos) {
            profiles.add(
                    Profile.builder()
                            .name(profileRepo.getName())
                            .description(profileRepo.getDescription())
                            .admin(profileRepo.getAdmin())
                            .viewer(profileRepo.getViewer())
                            .manager(profileRepo.getManager())
                            .inspector(profileRepo.getInspector())
                            .documentViewer(profileRepo.getDocumentViewer())
                            .registrationUser(profileRepo.getRegistrationUser())
                            .registrationContract(profileRepo.getRegistrationContract())
                            .laboral(profileRepo.getLaboral())
                            .workplaceSafety(profileRepo.getWorkplaceSafety())
                            .registrationAndCertificates(profileRepo.getRegistrationAndCertificates())
                            .general(profileRepo.getGeneral())
                            .health(profileRepo.getHealth())
                            .environment(profileRepo.getEnvironment())
                            .concierge(profileRepo.getConcierge())
                            .client(client)
                            .build()
            );

            if (profiles.size() == 50) {
                profileRepository.saveAll(profiles);
                profiles.clear();
            }
        }

        if (!profiles.isEmpty()) {
            profileRepository.saveAll(profiles);
        }
        log.info("Finished setup client profiles ✔️ {}", clientId);
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
                .registrationUser(profile.getRegistrationUser())
                .registrationContract(profile.getRegistrationContract())
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
