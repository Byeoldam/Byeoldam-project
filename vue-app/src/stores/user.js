import { ref, computed } from "vue";
import { defineStore } from "pinia";
import api from "@/utils/api";
import axios from "axios";
import router from "@/router";
import { useCollectionStore } from "./collection";

const REST_API_URL = import.meta.env.VITE_API_BASE_URL;

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null,
    accessToken: null,
    refreshToken: null,
  }),

  actions: {
    // ✅ 새로고침 시 `localStorage`에서 데이터 불러와 복원
    restoreSession() {
      const savedUser = localStorage.getItem("user");
      const savedAccessToken = localStorage.getItem("accessToken");
      const savedRefreshToken = localStorage.getItem("refreshToken");

      if (savedUser && savedAccessToken) {
        this.user = JSON.parse(savedUser);
        this.accessToken = savedAccessToken;
        this.refreshToken = savedRefreshToken;

        // ✅ API 요청 시 Authorization 헤더 추가
        api.defaults.headers.common["Authorization"] = `Bearer ${this.accessToken}`;
        console.log("🔄 세션 복원 완료:", this.user);
      }
    },

    // ✅ 로그인 함수 (새로고침 후에도 유지 가능)
    async userLogin(email, password) {
      try {
        const formData = new URLSearchParams();
        formData.append("email", email);
        formData.append("password", password);
    
        const res = await axios.post(`${REST_API_URL}/users/login`, formData, {
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
        });
    
        if (res.data.success) {
          const { userId, email, nickname, accessToken, refreshToken } = res.data.results;
    
          // 1. store 상태 업데이트
          this.user = { userId, email, nickname };
          this.accessToken = accessToken;
          this.refreshToken = refreshToken;
    
          // 2. localStorage에 저장
          localStorage.setItem("user", JSON.stringify(this.user));
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("refreshToken", refreshToken);
    
          // 3. API 인스턴스에 토큰 설정
          api.defaults.headers.common["accessToken"] = accessToken;
    
          // 4. 확장 프로그램에 로그인 정보 전달
          const loginData = { access_token: accessToken, userId };
          window.postMessage({ type: "LOGIN", data: loginData }, window.location.origin);
    
          // 5. 컬렉션 store 인스턴스 생성
          const collectionStore = useCollectionStore();
          
          // 6. 토큰이 설정된 후에 컬렉션 데이터 요청
          await collectionStore.fetchAllCollections();
    
          // 7. 페이지 이동
          if (collectionStore.allCollections.length === 0) {
            router.push({ name: "collectionSelect" });
          } else {
            router.push({ name: "main" });
          }
        } else {
          throw new Error(res.data.message || "로그인 실패");
        }
      } catch (err) {
        console.error("🚨 로그인 실패:", err);
        alert(err.response?.data?.message || "ID/PW 정보가 맞지 않습니다.");
      }
    },

    // ✅ 로그아웃 함수 (`localStorage`도 정리)
    async logout() {
      try {
        await api.post(`${REST_API_URL}/users/logout`);
        window.postMessage({ type: "LOGOUT" }, window.location.origin);

        // ✅ 상태 초기화
        this.user = null;
        this.accessToken = null;
        this.refreshToken = null;

        // ✅ `localStorage`에서 데이터 삭제
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

        alert("정상적으로 로그아웃 처리되었습니다.");
        router.push({ name: "intro" });
      } catch (error) {
        console.error("🚨 로그아웃 실패:", error);
      }
    },

    // ✅ 이메일 인증 요청
    async emailVerification(email) {
      console.log("이메일 인증 요청 시작");
      try {
        const response = await axios.post(`${REST_API_URL}/users/email/send`, { email });
        console.log("이메일 인증 요청 완료");
        console.log(response.data);
        return response.data;
      } catch (error) {
        console.error("이메일 인증 요청 실패:", error);
        throw error;
      }
    },

    // ✅ 인증코드 확인
    async checkCode(email, verificationCode) {
      console.log("인증코드 확인 시작 서버 전송");
      try {
        const response = await axios.post(`${REST_API_URL}/users/email/verify`, { email, verificationCode });
        console.log(response.data);
        return response.data;
      } catch (error) {
        console.error("인증코드 확인 실패:", error);
        throw error;
      }
    },

    // ✅ 회원가입 함수
    async signup(form) {
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
    },
  },

  persist: {
    enabled: true, // ✅ 자동 저장 활성화
    strategies: [
      {
        key: "user", // ✅ localStorage에 저장될 키
        storage: localStorage, // ✅ localStorage에 저장
        paths: ["user", "accessToken", "refreshToken"], // ✅ 유지할 상태 지정
      },
    ],
  },
});