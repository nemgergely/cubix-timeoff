package hu.cubix.timeoff.security;

import hu.cubix.timeoff.model.Employee;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode(of = "username")
public class CustomUserDetails implements UserDetails {

    @Getter
    private Long id;
    @Getter
    private String name;
    private String username;
    private String password;
    private List<? extends GrantedAuthority> authorities;

    public static CustomUserDetails build(Employee employee) {
        List<GrantedAuthority> authorities = Arrays.stream(employee.getRoles().get(0)
            .split(", "))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return new CustomUserDetails(
            employee.getId(),
            employee.getName(),
            employee.getUsername(),
            employee.getPassword(),
            authorities
        );
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
