package edu.xiao.webservice.repository;

import edu.xiao.webservice.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PictureRepository extends JpaRepository<Picture, UUID> {
    Optional<Picture> findByUserId(String userId);
}
