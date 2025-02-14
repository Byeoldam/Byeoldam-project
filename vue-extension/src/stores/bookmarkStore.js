import { defineStore } from "pinia";
import { ref } from "vue";

export const useBookmarkStore = defineStore("bookmark", () => {

  // 사용자 인증 정보
  const user = ref({
    userId: null,
    accessToken : null
  })

  const setUser = (id, token) => {
    userId.value = id;
    accessToken.value = token;
  };


  return {
    user,
    setUser,
  };
});
