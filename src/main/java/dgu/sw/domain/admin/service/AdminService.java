package dgu.sw.domain.admin.service;

import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;

import java.util.List;

public interface AdminService {
    List<AdminUserResponse> getAllUsers();
}
