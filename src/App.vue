<template>
  <router-view></router-view>
</template>

<script>
import { onMounted, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'

export default {
  name: 'App',
  setup() {
    const userStore = useUserStore()

    onMounted(() => {
      // localStorage에서 데이터 불러오기
      const savedUser = localStorage.getItem('user')
      const savedAccessToken = localStorage.getItem('accessToken')
      const savedRefreshToken = localStorage.getItem('refreshToken')

      if (savedUser && savedAccessToken) {
        // store에 데이터 설정
        userStore.user = JSON.parse(savedUser)
        userStore.accessToken = savedAccessToken
        userStore.refreshToken = savedRefreshToken

        // API 인스턴스에 토큰 설정
        api.defaults.headers.common['accessToken'] = savedAccessToken

        // 필요한 경우 컬렉션 데이터도 다시 불러오기
        const initializeData = async () => {
          try {
            await userStore.initializeUserData()
          } catch (error) {
            console.error('데이터 초기화 실패:', error)
          }
        }

        initializeData()
      }

      // 브라우저 종료 이벤트 리스너 추가
      window.addEventListener('beforeunload', clearLocalStorage)
    })

    // 브라우저 종료 시 localStorage 초기화 함수
    const clearLocalStorage = (event) => {
      // 탭 종료가 아닌 브라우저 종료인지 확인
      if (!event.persisted) {
        localStorage.clear()
      }
    }

    // 컴포넌트 언마운트 시 이벤트 리스너 제거
    onBeforeUnmount(() => {
      window.removeEventListener('beforeunload', clearLocalStorage)
    })
  }
}
</script> 