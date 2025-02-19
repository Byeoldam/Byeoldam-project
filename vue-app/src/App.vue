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
    <!-- 커밋을 위한 주석 11-->
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

    // if(!sessionStorage.getItem('isReloading')){
    //   console.log("isReloading 없음");
    //   clearUserData()
    // }

    onMounted(() => {
      console.log("새로 고침 시");
      
      // sessionStorage에 플래그 설정
      // sessionStorage.setItem('isReloading', 'true');
      
      // localStorage에서 데이터 불러오기
      const savedUser = localStorage.getItem('user')
      const savedAccessToken = localStorage.getItem('accessToken')
      const savedRefreshToken = localStorage.getItem('refreshToken')

      if (savedUser && savedAccessToken) {
        // store에 데이터 설정
        userStore.user = JSON.parse(savedUser)
        userStore.accessToken = savedAccessToken
        userStore.refreshToken = savedRefreshToken
        sessionStorage.setItem("isReloading", "true");

        // API 인스턴스에 토큰 설정
        api.defaults.headers.common['accessToken'] = savedAccessToken

        const initializeData = async () => {
          try {
            await userStore.initializeUserData()
          } catch (error) {
            console.error('데이터 초기화 실패:', error)
          }
        }

        initializeData()
      }

    })

   
  }
}
</script> 

<style scoped>

</style>
