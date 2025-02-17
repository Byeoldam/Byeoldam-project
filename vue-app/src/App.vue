<template>
  <div>
    <RouterView />
    <!-- 커밋을 위한 주석 -->
    <!-- 커밋을 위한 주석 2-->
    <!-- 커밋을 위한 주석 3-->
    <!-- 커밋을 위한 주석 4-->
    <!-- 커밋을 위한 주석 5-->
    <!-- 커밋을 위한 주석 6-->
    <!-- 커밋을 위한 주석 7-->
    <!-- 커밋을 위한 주석 8--> 
    <!-- 커밋을 위한 주석 9-->
    <!-- 커밋을 위한 주석 10-->
  </div>
</template>

<script>
import { onMounted, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'

export default {
  name: 'App',
  setup() {
    const userStore = useUserStore()

    const clearUserData = () => {
      // localStorage 데이터 삭제
      localStorage.removeItem('user')
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      
      // store 데이터 초기화
      userStore.user = null
      userStore.accessToken = null
      userStore.refreshToken = null
    }

    onMounted(() => {
      console.log("새로 고침 시");
      
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

      // 브라우저 창이 닫힐 때 이벤트 리스너 추가
      //window.addEventListener('unload', clearUserData)
      //window.addEventListener('beforeunload', clearUserData)
    })

    onBeforeUnmount(() => {
      // 이벤트 리스너 제거
      console.log("컴포넌트 언마운트 시");
      clearUserData();
      //window.removeEventListener('unload', clearUserData)
      //clearUserData()
    })


   
  }
}
</script> 

<style scoped>

</style>
