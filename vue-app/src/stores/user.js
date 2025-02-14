import { ref, computed } from "vue";
import { defineStore } from "pinia";
import api from "@/utils/api";
import axios from "axios";
import router from "@/router";
import { useCollectionStore } from "./collection";
const REST_API_URL = import.meta.env.VITE_API_BASE_URL;
//testtest
export const useUserStore = defineStore("user", () => {
  const collectionStore = useCollectionStore();
  const user = ref(null);

  // const username = ref(null)
  // const password1 = ref(null)
  // const password2 = ref(null)
  // const nickname = ref(null)
  // const age = ref(null)
  // const email = ref(null)
  // const profile_img = ref(null)

  // const payload = {
  //   username: username.value,
  //   password1: password1.value,
  //   password2: password2.value,
  //   nickname: nickname.value,
  //   age: age.value,
  //   email: email.value,
  //   profile_img: profile_img.value
  // }

  //api 경로 : `http:localhost:8080/api/users/login`
//   임시 로그인 함수

const userLogin = async (email, password) => {
  try {
    const formData = new URLSearchParams();
    formData.append("email", email);
    formData.append("password", password);

    // 로그인 요청
    const res = await axios.post(`${REST_API_URL}/users/login`, formData, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
    });
    
    if (res.data.success) {
      const { userId, email, nickname, accessToken, refreshToken } = res.data.results;

      // JWT 토큰 저장
      sessionStorage.setItem("accessToken", accessToken);
      sessionStorage.setItem("refreshToken", refreshToken);

      // 사용자 정보 업데이트
      user.value = { userId, email, nickname };

      // /me 엔드포인트로 테스트 요청
      try {
        const meResponse = await api.get('/users/me');
        console.log('내 정보 요청 성공:', meResponse.data);
      } catch (meError) {
        console.error('내 정보 요청 실패:', meError);
      }
      // api 인스턴스에 토큰 직접 설정
      api.defaults.headers.common['accessToken'] = accessToken;
      // 컬렉션 정보 가져오는 컨트롤러 아직 백엔드 준비 안됨
      try {
        // 컬렉션 데이터 가져오기
        await collectionStore.fetchAllCollections();
        
        // 라우팅 처리
        if (collectionStore.allCollections.length === 0) {
          router.push({ name: "collectionSelect" });
        } else {
          router.push({ name: "main" });
        }
      } catch (collectionError) {
        console.error("컬렉션 로드 중 오류:", collectionError);
        // 컬렉션 로드 실패해도 로그인은 성공으로 처리
        router.push({ name: "collectionSelect" });
      }
    } else {
      throw new Error(res.data.message || "로그인 실패");
    }
  } catch (err) {
    console.error("🚨 로그인 실패:", err);
    console.log(err.response?.data?.message || "ID/PW 정보가 맞지 않습니다.");
  }
};

const logout = async () => {
    try {
      await api.post(`${REST_API_URL}/users/logout`); // 백엔드에 로그아웃 요청
  
      // 익스텐션에 로그아웃 알림
      window.postMessage({ type: "LOGOUT" }, window.location.origin);
  
      // 세션 스토리지 및 유저 상태 초기화
      sessionStorage.removeItem("accessToken");
      sessionStorage.removeItem("refreshToken");
      user.value = null;
  
      alert("정상적으로 로그아웃 처리되었습니다.");
      router.push({ name: "intro" });
    } catch (error) {
      console.error("🚨 로그아웃 실패:", error);
    }
  };
  
  



  // 이메일 인증 요청 함수
  const emailVerification = async (email) => {
    try {
      const response = await axios.post(`${REST_API_URL}/users/email/send`, {
        email: email
      });
      // 성공 시 true 반환
      return response.data;
    } catch (error) {
      console.error("이메일 인증 요청 실패:", error);
      throw error;
    }
  };

  //이메일 응답코드 검증 함수
  // userStore.js
const checkCode = async (verifyCode) => {
    try {
      const response = await axios.post(`${REST_API_URL}/users/email/verify`, {
        verifyCode: verifyCode
      });
      // 성공 시 true 반환
      return response.data;
    } catch (error) {
      console.error("인증코드 확인 실패:", error);
      throw error;
    }
  };
  // 회원가입 함수
  const signup = async (form) => {
    try {
      const response = await axios.post(`${REST_API_URL}/users/register`, {
        email: form.email,
        password: form.password,
        nickname: form.nickname,
      });
  
      if (response.data.success) {
        alert("회원가입이 성공적으로 완료되었습니다.");
        router.push({ name: "login" });
      } else {
        alert(response.data.message || "회원가입에 실패했습니다.");
      }
    } catch (err) {
      console.error("회원가입 요청 실패:", err);
      alert(err.response?.data?.message || "회원가입에 실패했습니다. 다시 시도해주세요.");
    }
  };

  //마이페이지 조회 밑에 리턴 함수명 주석 해제해야 사용 가능능
const getMyPage = async () => {
  try {
    const res = await api.get("/users/mypage"); // `api` 인스턴스로 요청 보내기
    return res.data; // 필요하면 데이터 반환
  } catch (error) {
    console.error("🚨 마이페이지 조회 실패:", error);
  }
};


  //마이페이지 수정
  const putMyPage = async (params) => {
    try {
        const res = await axios.put(`${REST_API_URL}/users/mypage`)
    } catch (error) {
        
    }    
  }

  //회원 탈퇴
  const withdrawalOfMembership = async (params) => {
    try {
            const res = await axios.delete(`${REST_API_URL}/users/mypage`)
    } catch (error) {
        console.log('회원 탈퇴에 실패하였습니다:', error);
    }
  };

  return {
    user,
    // payload,
    
    // loginUser,
    // currentUser, // 사용자 정보 추가
    userLogin,
    emailVerification,
    logout,
    signup,
    // userId,
    checkCode,
//     getMyPage,
//     withdrawalOfMembership,
//     putMyPage,
//     getMyPage,
  };
});