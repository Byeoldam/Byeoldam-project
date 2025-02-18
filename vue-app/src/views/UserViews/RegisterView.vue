<template>
  <div class="body-container">
      <div class="stars"></div>
      <div class="stars2"></div>
      <div class="stars3"></div>
      <div class="flex items-center justify-center min-h-screen">
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full max-w-md p-8 bg-white shadow-md rounded-lg">
        <h2 class="text-2xl font-bold text-center mb-6">회원가입</h2>
  
        <form @submit.prevent="handleRegister">
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">이메일</label>
            <div class="flex gap-2">
              <input
                v-model="email"
                type="email"
                :disabled="isEmailVerified"
                class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
                placeholder="이메일"
                required
              />
              <button
                type="button"
                @click="sendVerificationCode"
                :disabled="isEmailVerified"
                class="verify-button"
              >
                {{ isEmailSent ? '재발송' : '인증번호 발송' }}
              </button>
            </div>
          </div>

          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">인증코드</label>
            <div class="flex gap-2">
              <input
                v-model="verificationCode"
                type="text"
                :disabled="isEmailVerified"
                class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
                placeholder="6자리 숫자를 입력하세요"
                maxlength="6"
                pattern="[0-9]{6}"
                inputmode="numeric"
                required
              />
              <button
                type="button"
                @click="verifyCode"
                :disabled="isEmailVerified"
                class="verify-button"
              >
                {{ isEmailVerified ? '인증완료' : '인증하기' }}
              </button>
            </div>
          </div>

          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">닉네임</label>
            <input
              v-model="nickname"
              type="text"
              class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="닉네임"
              required
            />
          </div>

          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">비밀번호</label>
            <input
              v-model="password"
              type="password"
              class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="비밀번호"
              required
            />
          </div>

          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700">비밀번호 재확인</label>
            <input
              v-model="confirmPassword"
              type="password"
              class="w-full p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="비밀번호 재확인"
              required
            />
          </div>

          <button type="submit" class="submit-button">
            회원가입
          </button>

          <button type="button" @click="handleGoogleLogin" class="google-button">
            <img src="@/assets/google-icon.png" alt="Google" />
            Google로 로그인
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { useUserStore } from "@/stores/user";;
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
  
  const userStore = useUserStore();
  const router = useRouter();
  

  const email = ref("");
  const verificationCode = ref("");
  const nickname = ref("");
  const password = ref("");
  const confirmPassword = ref("");
  
  const modal = ref({
    visible: false,
    success: false,
    message: "",
  });

  const isEmailVerified = ref(false);
  const isEmailSent = ref(false);

  // 별 그림자 상태를 저장할 ref 변수들 추가
  const starsStyle = ref('');
  const stars2Style = ref('');
  const stars3Style = ref('');

  // 컴포넌트가 마운트될 때 한 번만 실행
  onMounted(() => {
    starsStyle.value = generateStarsShadow(700, 1);
    stars2Style.value = generateStarsShadow(200, 2);
    stars3Style.value = generateStarsShadow(100, 3);
  });

  // 폼 유효성 검사를 위한 computed 속성 추가
  const isFormValid = computed(() => {
    return email.value &&
      nickname.value &&
      password.value &&
      confirmPassword.value &&
      password.value === confirmPassword.value;
  });

  // 인증번호 발송
  const sendVerificationCode = async () => {
    try {
      const response = await userStore.emailVerification(email.value);
      console.log("sendVerificationCode");
      console.log(response.success);
      
      if (response.status) {
        console.log("인증번호 발송 완료");
       
        
        isEmailSent.value = true;
        console.log(isEmailSent.value);
        modal.value = { 
          visible: true, 
          success: true, 
          message: response.message || "인증번호 발송이" 
        };
      }
    } catch (error) {
      modal.value = { 
        visible: true, 
        success: false, 
        message: error.response?.data?.message || "이메일 발송이"
      };
    }
  };
  
  // 인증 코드 확인
  const verifyCode = async () => {
    console.log("인증 코드 확인 시작");
    if (!email.value) {
      modal.value = {
        visible: true,
        success: false,
        message: "먼저 인증번호를 발송해주세요"
      };
      return;
    }

    try {
      console.log("인증 코드 확인 시작 서버 전송");
      console.log(verificationCode.value);
      
      const response = await userStore.checkCode(email.value,verificationCode.value);
      if (response.status) {
        isEmailVerified.value = true;
        console.log("인증 코드 확인 완료");
        
        modal.value = {
          visible: true,
          success: true,
          message: response.message || "이메일 인증이"
        };
      }
    } catch (error) {
      isEmailVerified.value = false;
      modal.value = {
        visible: true,
        success: false,
        message: error.response?.data?.message || "이메일 인증이"
      };
    }
  };

  // 회원가입
  const handleRegister = async () => {
    if (!isEmailVerified.value) {
      alert("이메일 인증부터 해라~");
      return;
    }
    // 비밀번호 확인
    if (password.value !== confirmPassword.value) {
      modal.value = { 
        visible: true, 
        success: false, 
        message: "비밀번호가 일치하지 않습니다" 
      };
      return;
    }

    const payload = {
      email: email.value,
      password: password.value,
      nickname: nickname.value,
    };
    
    try {
      await userStore.signup(payload);
      router.push({ name: 'login' });
    } catch (error) {
      modal.value = { 
        visible: true, 
        success: false, 
        message: error.response?.data?.message || "회원가입에 실패했습니다" 
      };
    }
  };

  // Google 로그인 처리
  const handleGoogleLogin = () => {
    router.push({ name: 'login' });
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
  background-color: #1E1B4B;
  /* background-image: linear-gradient(to bottom right, #1E1B4B, #3730A3); */
  min-height: 100vh;
  width: 100%;
}

.verify-button {
    background-color: #3730A3;
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 0.375rem;
    white-space: nowrap;
    transition: all 0.3s;
}

.verify-button:hover:not(:disabled) {
    background-color: #3730A3;
}

.verify-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.submit-button {
    width: 100%;
    background-color: #FFD900F5;
    color: #111827;
    padding: 0.75rem;
    border-radius: 0.375rem;
    font-weight: 600;
    margin-top: 1.5rem;
    transition: all 0.3s;
}

.submit-button:hover {
    background-color: #e6c300;
}

.google-button {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.75rem;
    margin-top: 1rem;
    border: 1px solid #FFD900F5;
    color: #FFD900F5;
    border-radius: 0.375rem;
    font-weight: 600;
    transition: all 0.3s;
}

.google-button:hover {
    background-color: #FEF3C7;
}

.google-button img {
    width: 1.25rem;
    height: 1.25rem;
    margin-right: 0.5rem;
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

.stars:after {
    content: " ";
    position: absolute;
    top: 2000px;
    width: 1px;
    height: 1px;
    background: transparent;
    box-shadow: v-bind('starsStyle');
    animation: twinkle-1 3s ease-in-out infinite;
}

.stars2:after {
    content: " ";
    position: absolute;
    top: 2000px;
    width: 2px;
    height: 2px;
    background: transparent;
    box-shadow: v-bind('stars2Style');
    animation: twinkle-2 4s ease-in-out infinite;
}

.stars3:after {
    content: " ";
    position: absolute;
    top: 2000px;
    width: 3px;
    height: 3px;
    background: transparent;
    box-shadow: v-bind('stars3Style');
    animation: twinkle-3 5s ease-in-out infinite;
}

@keyframes animStar {
    from {
        transform: translateY(0);
    }
    to {
        transform: translateY(-2000px);
    }
}

@keyframes twinkle {
    0% { opacity: 0.2; }
    50% { opacity: 1; }
    100% { opacity: 0.2; }
}

.stars, .stars2, .stars3 {
    &::before {
        content: '';
        position: absolute;
        width: 100%;
        height: 100%;
        animation: twinkle 4s infinite;
    }
}
</style>
  