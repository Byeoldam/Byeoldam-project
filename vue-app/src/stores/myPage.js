import { defineStore } from "pinia";
import api from "@/utils/api";
import { ref } from "vue";

//마이페이지에 관한 함수 store
export const useMyPageStore = defineStore("myPage", () => {

    // 마이페이지 response 담을 변수
    const myPage = ref(null);

    //마이페이지 조회
    const getMyPage = async () => {
        // 데이터를 직접 전달
        const response = await api.get("/mypage");
        //getMyPage 호출 후 응답 response
        // {
        //     "status": true,
        //     "message": "string",
        //     "results": {
        //       "email": "string",
        //       "nickname": "string",
        //       "profileUrl": "string",
        //       "alrerDay": 0,
        //       "tagList": [
        //         {
        //           "tagName": "string",
        //           "tagColor": "string",
        //           "tagBolder": "string"
        //         }
        //       ]
        //     }
        //   }
        myPage.value = response.data;
        return response;
    };



    //마이페이지 정보변경
    const updateMyPage = async (myPageData) => {
        // myPageData 형식
        // {
        //     "nickname": "string",
        //     "alertDay": 0,
        //     "tagList": [
        //       {
        //         "tagName": "string",
        //         "tagColor": "string",
        //         "tagBolder": "string"
        //       }
        //     ]
        // }
        const response = await api.patch("/mypage", myPageData);
        myPage.value = response.data;  // 응답 데이터로 myPage 업데이트
        return response;
    };

    //마이페이지 프로필 이미지 변경
    const updateProfileImage = async (profileImage) => {
        const response = await api.post("/mypage/profile-image", profileImage, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        console.log('updateProfileImage');
        console.log(response.data.results.s3Url);
        await getMyPage();
        return response;
    };

    // 마이페이지 프로필 이미지 삭제
    const deleteProfileImage = async () => {
        const response = await api.delete("/mypage/profile-image");
        await getMyPage();  // 프로필 이미지 삭제 후 전체 데이터 새로고침
        return response;
    };

    
    // 마이페이지 비밀번호 변경
    const updatePassword = async (passwordData) => {
        // passwordData 형식
        // {
        //     "currentPassword": "string",
        //     "newPassword": "string"
        // }
        const response = await api.put("/mypage/change-password", passwordData);
        return response;
    };

    // 마이페이지 회원 탈퇴
    const deleteUser = async () => {
        const response = await api.delete("/mypage/deactivate");
        myPage.value = null;  // 회원 탈퇴 후 데이터 초기화
        return response;
    };

    return {
        myPage,
        getMyPage,
        updateMyPage,
        updateProfileImage,
        deleteProfileImage,
        updatePassword,
        deleteUser,
    };
});