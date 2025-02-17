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

<script setup>
import { onMounted } from "vue";
import { useUserStore } from "@/stores/user"; // ✅ import 방식 수정

// const userStore = useUserStore(); // ✅ 변수명 수정

// onMounted(() => {
//   userStore.restoreSession(); // ✅ 새로고침 시 자동 실행
// });
</script>

<style scoped>

</style>
