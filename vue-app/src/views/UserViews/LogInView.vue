<template>
  <div class="body-container">
    <div class="stars"></div>
    <div class="stars2"></div>
    <div class="stars3"></div>
    <div class="flex items-center justify-center min-h-screen">
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full max-w-md p-8 bg-white shadow-md rounded-lg">
        <h2 class="text-2xl font-bold text-center mb-6">로그인</h2>
  
        <form @submit.prevent="handleLogin">
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium text-gray-700">이메일</label>
            <input
              v-model="email"
              type="email"
              id="email"
              class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="이메일을 입력해주세요"
              required
            />
          </div>
  
          <div class="mb-4">
            <label for="password" class="block text-sm font-medium text-gray-700">비밀번호</label>
            <input
              v-model="password"
              type="password"
              id="password"
              class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="비밀번호를 입력해주세요"
              required
            />
          </div>
  
          <button
            type="submit"
            class="w-full text-gray-900 py-2 rounded-md hover:bg-[#e6c300] transition duration-300 cursor-pointer hover:shadow-md hover:scale-[1.02] active:scale-[0.98] text-lg font-medium"
            style="background-color: #FFD900F5"
          >
            로그인
          </button>
        </form>
  
        <div class="mt-4">
          <button
            @click="handleGoogleLogin"
            class="w-full flex items-center justify-center border border-[#FFD900F5] text-[#FFD900F5] py-2 rounded-md hover:bg-yellow-50 transition duration-300 cursor-pointer hover:shadow-md hover:scale-[1.02] active:scale-[0.98] text-lg font-medium"
          >
            <img src="@/assets/google-icon.png" alt="Google" class="w-5 h-5 mr-2" />
            Google로 로그인
          </button>
        </div>
  
        <div class="mt-4 text-center">
          <RouterLink 
            :to="{ name: 'register' }" 
            class="text-[#3730A3] hover:text-[#2d2682] font-normal"
          >
            회원가입하기
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { RouterLink } from "vue-router";
import { useRouter } from "vue-router";
import { ElMessage } from 'element-plus';

const router = useRouter()
const userstore = useUserStore()

const email = ref("");
const password = ref("");

// 별 그림자 상태를 저장할 ref 변수들 추가
const starsStyle = ref('');
const stars2Style = ref('');
const stars3Style = ref('');

// onMounted를 가져옵니다
onMounted(() => {
  starsStyle.value = generateStarsShadow(700, 1);
  stars2Style.value = generateStarsShadow(200, 2);
  stars3Style.value = generateStarsShadow(100, 3);
});

//로그인 핸들러
const handleLogin = async () => {
  try {
    await userstore.userLogin(email.value, password.value);
    ElMessage.success('로그인에 성공했습니다.');
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '로그인에 실패했습니다.');
  }
}

const handleGoogleLogin = () => {
  console.log("시도2");
  
  console.log("Google 로그인 시도");
  // Google 로그인 로직 추가
  const clientId = "437503882211-tmj75mbfqs56nmm01ddncp6og4hfghdg.apps.googleusercontent.com";
  const GOOGLE_AUTH_URL = `https://accounts.google.com/o/oauth2/auth?client_id=${clientId}&redirect_uri=http://localhost:8080/login/oauth2/code/google&response_type=code&scope=openid%20email%20profile`;
;

  window.location.href = GOOGLE_AUTH_URL;

};

// 별을 생성하는 함수
function generateStarsShadow(count, size) {
    let shadows = [];
    for(let i = 0; i < count; i++) {
        const x = Math.floor(Math.random() * 2000);
        const y = Math.floor(Math.random() * 2000);
        shadows.push(`${x}px ${y}px #FFF`);
    }
    return shadows.join(', ');
}
</script>

<style scoped>
.body-container {
  position: relative;
  overflow: hidden;
  background: radial-gradient(ellipse at bottom, #1B2735 0%, #1E1B4B 100%);
  /* background-image: linear-gradient(to bottom right, #1E1B4B, #3730A3); */
  min-height: 100vh;
  width: 100%;
}

input::placeholder {
  color: #9CA3AF;
}

input:focus::placeholder {
  color: #D1D5DB;
}

button {
  transition: all 0.3s ease;
}

button:hover {
  transform: translateY(-1px);
}

@keyframes animStar {
    from {
        transform: translateY(0);
    }
    to {
        transform: translateY(-2000px);
    }
}

@keyframes twinkle-1 {
    0%, 100% { opacity: 0.3; }
    50% { opacity: 1; }
}

@keyframes twinkle-2 {
    0%, 100% { opacity: 0.4; }
    50% { opacity: 1; }
}

@keyframes twinkle-3 {
    0%, 100% { opacity: 0.5; }
    50% { opacity: 1; }
}

.stars {
    width: 1px;
    height: 1px;
    background: transparent;
    box-shadow: v-bind('starsStyle');
    animation: 
        animStar 50s linear infinite,
        twinkle-1 3s ease-in-out infinite;
    position: absolute;
    top: 0;
    left: 0;
}

.stars2 {
    width: 2px;
    height: 2px;
    background: transparent;
    box-shadow: v-bind('stars2Style');
    animation: 
        animStar 100s linear infinite,
        twinkle-2 4s ease-in-out infinite;
    position: absolute;
    top: 0;
    left: 0;
}

.stars3 {
    width: 3px;
    height: 3px;
    background: transparent;
    box-shadow: v-bind('stars3Style');
    animation: 
        animStar 150s linear infinite,
        twinkle-3 5s ease-in-out infinite;
    position: absolute;
    top: 0;
    left: 0;
}

.stars:after, .stars2:after, .stars3:after {
    content: " ";
    position: fixed;
    top: 2000px;
    width: 1px;
    height: 1px;
    background: transparent;
    pointer-events: none;
    z-index: -1;
}
</style>
  