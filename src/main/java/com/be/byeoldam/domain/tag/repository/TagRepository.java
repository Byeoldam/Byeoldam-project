package com.be.byeoldam.domain.tag.repository;

import com.be.byeoldam.domain.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom {
    List<Tag> findTop10ByOrderByReferenceCountDesc();

    Optional<Tag> findByName(String name);

    List<Tag> findByNameIn(List<String> names);

    boolean existsByName(String name);

    @Query("SELECT t.name FROM Tag t")
    List<String> getAllNames();
}
