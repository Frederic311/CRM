package com.crm.authservice.auth_api1.Repository;


import com.crm.authservice.auth_api1.models.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Integer> {
    Optional<ProfilePhoto> findByUserId(Integer userId);

    Optional<ProfilePhoto> findByUserId(Long userId);
}