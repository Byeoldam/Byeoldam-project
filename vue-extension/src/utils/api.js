import axios from "axios";
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,  // axios에서는 이렇게 설정
  headers: {
    'Content-Type': 'application/json',
  },
  referrerPolicy: "no-referrer-when-downgrade"
});

// 요청 인터셉터
api.interceptors.request.use(
  async (config) => {  
    try {
      // Promise로 감싸서 비동기 처리
      const response = await new Promise((resolve, reject) => {
        chrome.storage.local.get(["access_token"], (result) => {
          if (chrome.runtime.lastError) {
            reject(chrome.runtime.lastError);
          } else {
            resolve(result);
          }
        });
      });

      const accessToken = response.access_token;
      if (accessToken) {
        config.headers['accessToken'] = accessToken;
        console.log('요청 URL:', config.baseURL + config.url);
        console.log('요청 헤더:', config.headers);
      } else {
        console.log('토큰이 없습니다!');
      }
      return config;
    } catch (error) {
      console.error('토큰 가져오기 실패:', error);
      return config;
    }
  },
  (error) => {
    console.error('요청 인터셉터 에러:', error);
    return Promise.reject(error);
  }
);

export default api;