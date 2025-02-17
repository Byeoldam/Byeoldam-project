export const useUserStore = defineStore("user", {
  state: () => ({
    user: null,
    accessToken: null,
    refreshToken: null,
  }),

  actions: {
    // 기존 액션들...

    // 새로고침 시 데이터 초기화를 위한 메서드
    async initializeUserData() {
      try {
        const savedUser = localStorage.getItem('user')
        const savedAccessToken = localStorage.getItem('accessToken')
        const savedRefreshToken = localStorage.getItem('refreshToken')

        if (savedUser && savedAccessToken) {
          // store 상태 업데이트
          this.user = JSON.parse(savedUser)
          this.accessToken = savedAccessToken
          this.refreshToken = savedRefreshToken

          // API 인스턴스에 토큰 설정
          api.defaults.headers.common['accessToken'] = savedAccessToken

          // 컬렉션 store 인스턴스 생성 및 데이터 로드
          const collectionStore = useCollectionStore()
          await collectionStore.fetchAllCollections()
        }
      } catch (error) {
        console.error('사용자 데이터 초기화 실패:', error)
        // 에러 발생 시 로그아웃 처리
        this.logout()
      }
    },

    // 기존 액션들...
  },

  persist: {
    enabled: true,
    strategies: [
      {
        key: "user",
        storage: localStorage,
        paths: ["user", "accessToken", "refreshToken"],
      },
    ],
  },
}) 