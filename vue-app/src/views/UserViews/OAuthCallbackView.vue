<template>
    <div class="callback-container">
      <div class="stars"></div>
      <div class="stars2"></div>
      <div class="stars3"></div>
      <div class="loading-message">
        <h2>로그인 진행 중</h2>
        <div class="loading-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  import { onMounted, ref } from 'vue';
  import api from "@/utils/api";
  import { useCollectionStore } from '@/stores/collection';
  import { useUserStore } from '@/stores/user';  // 상단에 import 추가

  // Vue Router 사용
  const router = useRouter();

  const starsStyle = ref('');
  const stars2Style = ref('');
  const stars3Style = ref('');

  function generateStarsShadow(count, size) {
    let shadows = [];
    for(let i = 0; i < count; i++) {
        const x = Math.floor(Math.random() * 2000);
        const y = Math.floor(Math.random() * 2000);
        shadows.push(`${x}px ${y}px #FFF`);
    }
    return shadows.join(', ');
  }

  onMounted(async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    console.log("code",code);
    console.log("urlParams",urlParams);
    if (code) {
      try {
        console.log("OAuth2 로그인 성공! 서버에 코드 전송 중...");
  
        // 백엔드로 OAuth2 인증 코드 전송하여 JWT 발급 요청
        const response = await fetch("https://byeoldam.store/api/oauth2/token", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            code: code,
            redirectUri: "https://byeoldam.store/oauth/callback",
          }),
        });
        const data = await response.json();
        console.log(data);
        
        if(data.status){
            const { userId, email, nickname, accessToken, refreshToken, profileUrl } = data.results;
            
            const user = {
                userId,
                email,
                nickname,
                profileUrl
            };
            
               // Pinia store에 저장
    const userStore = useUserStore();
    userStore.user = user;
    userStore.accessToken = accessToken;
    userStore.refreshToken = refreshToken;

            // user 저장장
            localStorage.setItem("user", JSON.stringify(user));
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", refreshToken);

            api.defaults.headers.common["accessToken"] = accessToken;
            
            const loginData = { access_token: accessToken, userId };
            window.postMessage({ type: "LOGIN", data: loginData }, window.location.origin);
        
            const collectionStore = useCollectionStore();
            await collectionStore.fetchAllCollections();

            if (collectionStore.allCollections.length === 0) {
                router.push({ name: "collectionSelect" });
            } else {
                router.push({ name: "main" });
            }

        }else{
            router.push("/");
        }
      } catch (error) {
        console.error("OAuth2 로그인 에러:", error);
        alert("OAuth 로그인 중 오류가 발생했습니다.");
        router.push("/login"); // 실패 시 로그인 페이지로 이동
      }
    } else {
      console.error("OAuth2 로그인 실패: 인증 코드 없음");
      alert("OAuth 로그인에 실패했습니다.");
      router.push("/login"); // 실패 시 로그인 페이지로 이동
    }

    starsStyle.value = generateStarsShadow(700, 1);
    stars2Style.value = generateStarsShadow(200, 2);
    stars3Style.value = generateStarsShadow(100, 3);
  });

  </script>
  
  <style scoped>
  .callback-container {
    position: relative;
    overflow: hidden;
    background: radial-gradient(ellipse at bottom, #1B2735 0%, #1E1B4B 100%);
    min-height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #ffd900f5;
  }

  @keyframes animStar {
    from { transform: translateY(0); }
    to { transform: translateY(-2000px); }
  }

  .stars, .stars2, .stars3 {
    position: absolute;
    top: 0;
    left: 0;
    background: transparent;
  }

  .stars {
    width: 1px;
    height: 1px;
    box-shadow: v-bind('starsStyle');
    animation: animStar 50s linear infinite;
  }

  .stars2 {
    width: 2px;
    height: 2px;
    box-shadow: v-bind('stars2Style');
    animation: animStar 100s linear infinite;
  }

  .stars3 {
    width: 3px;
    height: 3px;
    box-shadow: v-bind('stars3Style');
    animation: animStar 150s linear infinite;
  }

  .loading-message {
    position: relative;
    z-index: 1;
    text-align: center;
  }

  h2 {
    position: relative;
    z-index: 1;
    font-size: 24px;
    font-weight: 500;
    margin-bottom: 15px;
    color: #ffd900f5;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .loading-dots {
    display: flex;
    justify-content: center;
    gap: 8px;
  }

  .loading-dots span {
    width: 8px;
    height: 8px;
    background-color: #ffd900f5;
    border-radius: 50%;
    display: inline-block;
    animation: bounce 1.4s infinite ease-in-out both;
  }

  .loading-dots span:nth-child(1) {
    animation-delay: -0.32s;
  }

  .loading-dots span:nth-child(2) {
    animation-delay: -0.16s;
  }

  @keyframes bounce {
    0%, 80%, 100% { 
      transform: scale(0);
    } 
    40% { 
      transform: scale(1);
    }
  }
  </style>
