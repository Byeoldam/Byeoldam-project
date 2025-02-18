package com.be.byeoldam.domain.personalcollection.dto;

import com.be.byeoldam.domain.personalcollection.model.PersonalCollection;
import com.be.byeoldam.domain.user.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PersonalCollectionRequest {

    @NotBlank
    private String name;

    public PersonalCollection toEntity(User user) {
        return PersonalCollection.create(name, user);
    }
}