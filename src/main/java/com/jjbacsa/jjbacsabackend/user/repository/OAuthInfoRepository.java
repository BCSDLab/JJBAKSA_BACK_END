package com.jjbacsa.jjbacsabackend.user.repository;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthInfoRepository extends JpaRepository<OAuthInfoEntity, Long> {

    Optional<OAuthInfoEntity> findByApiKeyAndOauthType(String apiKey, OAuthType oauthType);

    boolean existsByApiKeyAndOauthType(String apiKey, OAuthType oauthType);
}