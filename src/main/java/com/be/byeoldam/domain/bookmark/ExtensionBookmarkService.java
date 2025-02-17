package com.be.byeoldam.domain.bookmark;

import com.be.byeoldam.domain.bookmark.dto.CreateBookmarkAndCollectionRequest;
import com.be.byeoldam.domain.bookmark.dto.CreateBookmarkRequest;
import com.be.byeoldam.domain.bookmark.dto.ExtensionBookmarkResponse;
import com.be.byeoldam.domain.personalcollection.PersonalCollectionService;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionRequest;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionResponse;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionRequest;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionResponse;
import com.be.byeoldam.domain.sharedcollection.service.SharedCollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExtensionBookmarkService {

    private final PersonalCollectionService personalCollectionService;
    private final SharedCollectionService sharedCollectionService;
    private final BookmarkService bookmarkService;

    @Transactional
    public ExtensionBookmarkResponse createBookmarkAndCollection(@Valid CreateBookmarkAndCollectionRequest request, Long userId) {
        if (request.isPersonal()) {
            PersonalCollectionRequest personalRequest = request.makePersonalCollection();
            PersonalCollectionResponse response = personalCollectionService.createPersonalCollection(personalRequest, userId);
            CreateBookmarkRequest bookmarkRequest = request.makeBookmarkRequest(response.getCollectionId());
            bookmarkService.createBookmark(bookmarkRequest, userId);
            return new ExtensionBookmarkResponse(response.getName());
        } else {
            SharedCollectionRequest sharedRequest = request.makeSharedCollectionRequest();
            SharedCollectionResponse response = sharedCollectionService.createSharedCollection(sharedRequest, userId);
            CreateBookmarkRequest bookmarkRequest = request.makeBookmarkRequest(response.getCollectionId());
            bookmarkService.createBookmark(bookmarkRequest, userId);
            return new ExtensionBookmarkResponse(response.getName());
        }
    }
}
