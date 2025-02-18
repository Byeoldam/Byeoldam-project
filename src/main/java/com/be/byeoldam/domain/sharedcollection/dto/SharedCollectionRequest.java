package com.be.byeoldam.domain.sharedcollection.dto;

import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SharedCollectionRequest {

    @NotBlank
    private String name;

    public SharedCollection toEntity() {
        return SharedCollection.create(name);
    }
}
