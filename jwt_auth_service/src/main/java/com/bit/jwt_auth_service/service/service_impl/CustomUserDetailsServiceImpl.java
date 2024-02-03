package com.bit.jwt_auth_service.service.service_impl;

import com.bit.shared.entity.User;
import com.bit.shared.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {
    if (userRepository != null) {
      Optional<User> userDetail = userRepository.findByEmail(email);

      return userDetail.map(UserDetailsImpl::new)
          .orElseThrow(
              () -> new UsernameNotFoundException("User not found " + email));
    }

    return null;
  }
}
