<template>
    <div class="callback-container">
      <h2>OAuth 로그인 처리 중...</h2>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  import { onMounted } from 'vue';
  
  // Vue Router 사용
  const router = useRouter();
  
  onMounted(async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    console.log("code",code);
    console.log("urlParams",urlParams);
    if (code) {
      try {
        console.log("OAuth2 로그인 성공! 서버에 코드 전송 중...");
  
        // 백엔드로 OAuth2 인증 코드 전송하여 JWT 발급 요청
        const response = await fetch("http://localhost:8080/api/oauth2/token", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            code: code,
            redirectUri: "http://localhost:5173/oauth/callback",
          }),
        });
        
        
        const data = await response.json();
        console.log(data);
        if(data.status){
            router.push({ name: "main" });
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
  });
  </script>
  
  <style scoped>
  .callback-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
    font-size: 18px;
  }
  </style>
