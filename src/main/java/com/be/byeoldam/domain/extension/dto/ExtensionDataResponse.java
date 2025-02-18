package com.be.byeoldam.domain.extension.dto;

import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionResponse;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExtensionDataResponse {
    private List<PersonalCollectionResponse> personalCollections;
    private List<SharedCollectionResponse> sharedCollections;
    private List<String> keywords;
    private boolean canSubscribeRss;
    private int notificationCnt;
    private boolean hasNewFeed;
}
