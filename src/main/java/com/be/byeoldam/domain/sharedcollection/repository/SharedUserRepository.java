package com.be.byeoldam.domain.sharedcollection.repository;

import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.sharedcollection.model.SharedUser;
import com.be.byeoldam.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedUserRepository extends JpaRepository<SharedUser, Long> {
    List<SharedUser> findBySharedCollection(SharedCollection sharedCollection);
    Optional<SharedUser> findByUserAndSharedCollection(User user, SharedCollection collection);
    void deleteBySharedCollection(SharedCollection sharedCollection);
    List<SharedUser> findByUser(User user);
    void deleteAllBySharedCollection(SharedCollection collection);

    boolean existsBySharedCollectionIdAndUserId(Long collectionId, Long inviterId);

    boolean existsByUserAndSharedCollection(User user, SharedCollection sharedCollection);
}