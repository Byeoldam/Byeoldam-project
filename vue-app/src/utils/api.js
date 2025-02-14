// 로그인 후 모든 요청이 access-token을 요청하므로 반복해서 쓰지 않기 위해
//만듬 axios 인터셉터라고 보면 된다

import axios from "axios";
import { useErrorStore } from "@/stores/error";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const accessToken = sessionStorage.getItem("accessToken");
    if (accessToken) {
      config.headers['accessToken'] = accessToken;
      // console.log('요청 URL:', config.baseURL + config.url);
      // console.log('요청 헤더:', config.headers);
    } else {
      console.log('토큰이 없습니다!');
    }
    return config;
  },
  (error) => {
    console.error('요청 인터셉터 에러:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터에서 헤더 이름을 수정해야 합니다
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      console.log("🔄 Access Token 만료됨! Refresh Token 시도");
      
      const errorStore = useErrorStore();
      const newAccessToken = await errorStore.throwRefreshToken();
      
      if (newAccessToken) {
        // 여기를 수정: ac -> accessToken
        error.config.headers.accessToken = newAccessToken;  // Bearer prefix 없이
        return api.request(error.config);
      } else {
        console.log("🚨 재로그인 필요!");
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
