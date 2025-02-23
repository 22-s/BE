package dgu.sw.global.security;

import dgu.sw.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security의 UserDetails 구현체
 * - User 엔티티를 기반으로 인증 정보를 관리
 * - 인증된 사용자의 ID, 이메일, 비밀번호를 포함
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;

    public CustomUserDetails(User user) {
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    /**
     * 사용자의 권한을 반환하는 메서드 (현재는 빈 리스트 반환)
     * - 역할(Role)이 필요하면 여기에 추가 가능
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 역할이 추가되면 여기서 관리 가능
    }

    /**
     * Spring Security에서 사용자명을 반환하는 메서드
     * - 보통 email을 사용하지만, 여기서는 id를 사용
     */
    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
