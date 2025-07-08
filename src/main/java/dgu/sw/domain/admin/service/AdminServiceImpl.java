package dgu.sw.domain.admin.service;

import dgu.sw.domain.admin.converter.AdminConverter;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminMannerResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;
import dgu.sw.domain.manner.entity.Manner;
import dgu.sw.domain.manner.repository.MannerRepository;
import dgu.sw.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final MannerRepository mannerRepository;

    @Override
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AdminConverter::toAdminUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminMannerResponse> getAllManners() {
        return mannerRepository.findAll().stream()
                .map(AdminConverter::toAdminMannerResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteManner(Long mannerId) {
        mannerRepository.deleteById(mannerId);
    }

    @Override
    @Transactional
    public void saveManner(AdminMannerRequest request) {
        Manner manner = AdminConverter.toManner(request);
        mannerRepository.save(manner);
    }
}
