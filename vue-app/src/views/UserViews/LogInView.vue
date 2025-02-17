<template>
    <div class="flex items-center justify-center min-h-screen bg-gray-100">
      <div class="w-full max-w-md p-8 bg-white shadow-md rounded-lg">
        <h2 class="text-2xl font-bold text-center mb-6">로그인</h2>
  
        <form @submit.prevent="handleLogin">
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium text-gray-700">이메일</label>
            <input
              v-model="email"
              type="email"
              id="email"
              class="w-full mt-1 p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="이메일"
              required
            />
          </div>
  
          <div class="mb-4">
            <label for="password" class="block text-sm font-medium text-gray-700">비밀번호</label>
            <input
              v-model="password"
              type="password"
              id="password"
              class="w-full mt-1 p-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-200"
              placeholder="비밀번호"
              required
            />
          </div>
  
          <button
            type="submit"
            class="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition duration-300"
          >
            로그인
          </button>
        </form>
  
        <div class="mt-4">
          <button
            @click="handleGoogleLogin"
            class="w-full flex items-center justify-center border border-blue-600 text-blue-600 py-2 rounded-md hover:bg-blue-50 transition duration-300"
          >
            <span class="mr-2">🔵</span> Google로 시작하기
          </button>
        </div>
  
        <div class="mt-4 text-center">
          <RouterLink :to="{ name: 'register' }" class="text-blue-600 hover:underline">
            회원가입
          </RouterLink>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import { useUserStore } from "@/stores/user";
  import { RouterLink } from "vue-router";
  import { useRouter } from "vue-router";
  const router = useRouter()
  const userstore = useUserStore()

  const email = ref("");
  const password = ref("");

  //로그인 핸들러
  const handleLogin = async () => {
    try {
      await userstore.userLogin(email.value, password.value)
      sessionStorage.setItem("isReloading", "true");
    } catch (error) {
      alert('로그인 실패: ' + error.message)
    }
  }

  const handleGoogleLogin = () => {
    console.log("Google 로그인 시도");
    // Google 로그인 로직 추가
  };
  </script>
  
  <style scoped>
  </style>
  