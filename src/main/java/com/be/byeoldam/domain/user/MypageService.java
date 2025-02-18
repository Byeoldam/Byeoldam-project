package com.be.byeoldam.domain.user;


import com.be.byeoldam.domain.bookmark.dto.TagDto;
import com.be.byeoldam.domain.tag.TagService;
import com.be.byeoldam.domain.user.dto.*;
import com.be.byeoldam.domain.user.model.S3Image;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.S3ImageRepository;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.domain.user.util.S3Util;
import com.be.byeoldam.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final S3Util s3Util;
    private final UserRepository userRepository;
    private final S3ImageRepository s3ImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagService tagService;

    // 마이페이지 조회
    @Transactional(readOnly = true)
    MypageResponse getMyPage(long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 userId와 매핑된 User가 없습니다."));

        List<TagDto> tagList = tagService.getUserTags(userId).getTagList();

        return MypageResponse.builder()
                .profileUrl(user.getProfileUrl())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .alrerDay(user.getAlertDay())
                .tagList(tagList)
                .build();
    }


    // 마이페이지 수정(이메일, 알람 주기, 사용자 태그)
    @Transactional
    void setMypage(long userId, MypageRequest mypageRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 userId와 매핑된 User가 없습니다."));

        user.updateMypage(mypageRequest.getNickname(), mypageRequest.getAlertDay());
        tagService.addUserTag(userId, mypageRequest.getTagList());
    }

    //프로필 이미지 삭제
    @Transactional
    void deleteMypageImageToS3(long userId){
        User user =userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 userId와 매핑된 User가 없습니다."));

        // 이미지 삭제하기
        s3ImageRepository.findByFileUrl(user.getProfileUrl())
                .ifPresent(s3Image -> {
                    String s3Key = s3Image.getFileKey();
                    s3ImageRepository.delete(s3Image);
                    s3Util.deleteImage(s3Key);
                });
        user.updateProfileImage(s3Util.getDefaultProfileImageUrl());
    }

    // S3에 이미지 업로드
    @Transactional
    MypageImageResponse uploadMypageImageToS3(long userId, MultipartFile multipartFile) throws IOException {
        User user =userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 userId와 매핑된 User가 없습니다."));

        // 기존에 이미지가 있다면 삭제하기.
        s3ImageRepository.findByFileUrl(user.getProfileUrl())
                .ifPresent(s3Image -> {
                    String s3Key = s3Image.getFileKey();
                    s3ImageRepository.delete(s3Image);
                    s3Util.deleteImage(s3Key);
                });

        // 새로운 이미지 추가하기 S3에 업로드
        S3UploadResponse s3UploadResponse = s3Util.imageUpload(multipartFile,"mypage");

        // 새로운 이미지 DB에 추가하기
        S3Image s3Image = s3UploadResponse.toEntity();
        user.updateProfileImage(s3Image.getFileUrl());
        s3ImageRepository.save(s3Image);

        return MypageImageResponse.of(s3UploadResponse.getS3Url());
    }


    // 비밀번호 변경
    /*
    * Transactional: 엔티티의 필드를 수정하면 자동으로 변경사항을 감지(dirty checking)해서 DB에 반영
    * userRepository.save(user)는 필요없음.
    * */
    @Transactional
    ChangePasswordResponse changePassword(long userId, ChangePasswordRequest changePasswordRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 userId와 매핑된 User가 없습니다."));

        if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())){
            throw new CustomException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.encodePassword(encodedPassword);

        return ChangePasswordResponse.of(true);
    }


    //회원 탈퇴(소프트 삭제)
    @Transactional
    void deactivateUser(long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("해당 userId와 매핑된 User가 없습니다."));

        user.deactivate();
    }

}
