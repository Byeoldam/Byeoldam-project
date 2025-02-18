package com.be.byeoldam.domain.memo;

import com.be.byeoldam.domain.bookmark.repository.BookmarkRepository;
import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.memo.dto.MemoRequest;
import com.be.byeoldam.domain.memo.dto.MemoResponse;
import com.be.byeoldam.domain.memo.model.Memo;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public MemoResponse createMemo(Long userId, Long bookmarkId, MemoRequest memoRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException("북마크를 찾을 수 없습니다."));

        Memo memo = memoRequest.toEntity(user, bookmark);

        memoRepository.save(memo);

        return MemoResponse.from(memo);
    }

    @Transactional
    public MemoResponse updateMemo(Long userId, Long memoId, Long bookmarkId, MemoRequest memoRequest) {

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new CustomException("메모를 찾을 수 없습니다."));

        if (!memo.getUser().getId().equals(userId)) {
            throw new CustomException("해당 메모에 대한 권한이 없습니다.");
        }

        if (!memo.getBookmark().getId().equals(bookmarkId)) {
            throw new CustomException("메모가 해당 북마크에 속해있지 않습니다.");
        }

        memo.updateContent(memoRequest.getContent());

        return MemoResponse.from(memo);
    }

    @Transactional
    public void deleteMemo(Long userId, Long memoId, Long bookmarkId) {

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new CustomException("메모를 찾을 수 없습니다."));

        if (!memo.getUser().getId().equals(userId)) {
            throw new CustomException("해당 메모에 대한 권한이 없습니다.");
        }

        if (!memo.getBookmark().getId().equals(bookmarkId)) {
            throw new CustomException("메모가 해당 북마크에 속해있지 않습니다.");
        }

        memoRepository.delete(memo);
    }

    @Transactional(readOnly = true)
    public List<MemoResponse> getMemoList(Long bookmarkId) {

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException("북마크를 찾을 수 없습니다."));

        if (!bookmark.isRead()) { // 읽지 않은 북마크인 경우 읽음 처리
            bookmark.updateRead();
        }

        List<Memo> memoList = memoRepository.findByBookmarkId(bookmarkId);

        return memoList.stream()
                .map(MemoResponse::from)
                .toList();
    }

}
