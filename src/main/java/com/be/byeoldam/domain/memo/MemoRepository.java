package com.be.byeoldam.domain.memo;

import com.be.byeoldam.domain.memo.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByBookmarkId(Long bookmarkId);

    void deleteAllByBookmarkId(Long bookmarkId);
}
