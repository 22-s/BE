package dgu.sw.domain.admin.service;

import dgu.sw.domain.admin.dto.AdminDTO;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminMannerResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;

import java.util.List;

public interface AdminService {
    List<AdminUserResponse> getAllUsers();
    List<AdminMannerResponse> getAllManners();
    void deleteManner(Long mannerId);
    void saveManner(AdminDTO.AdminRequest.AdminMannerRequest request);
}
