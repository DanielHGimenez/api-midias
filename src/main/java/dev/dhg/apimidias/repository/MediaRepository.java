package dev.dhg.apimidias.repository;

import dev.dhg.apimidias.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Integer> {

    List<Media> findByDeleted(Boolean deleted);

}
