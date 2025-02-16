<template>
  <div class="container">
    <nav>
      <div class="nav-links">
        <RouterLink :to="{ name: 'storage' }" class="nav-item">
          <span class="nav-text">저장</span>
        </RouterLink>
        <RouterLink :to="{ name: 'alarm' }" class="nav-item">
          <span class="nav-text">알림</span>
          <span class="notification-badge">5</span>
        </RouterLink>
        <RouterLink :to="{ name: 'feed' }" class="nav-item">
          <span class="nav-text">피드</span>
          <span class="notification-badge">N</span>
        </RouterLink>
      </div>
      <button class="link-button">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-5 h-5"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M13.5 6H5.25A2.25 2.25 0 0 0 3 8.25v10.5A2.25 2.25 0 0 0 5.25 21h10.5A2.25 2.25 0 0 0 18 18.75V10.5m-10.5 6L21 3m0 0h-5.25M21 3v5.25"
          />
        </svg>
      </button>
    </nav>
    <RouterView />
  </div>
</template>

<script setup>
import { RouterView } from "vue-router";
import { onMounted } from "vue";
import { useBookmarkStore } from "./stores/bookmarkStore";

const bookmarkStore = useBookmarkStore();

onMounted(() => {
  // 확장 아이콘 클릭 시 백그라운드로 사용자 정보 Extension Storage 저장 요청 보내기
  chrome.runtime.sendMessage({ action: "popupOpened" });
});
</script>

<style scoped>
.container {
  width: 450px;
  min-height: 200px;
  padding: 8px;
}

nav {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  position: relative;
  margin-bottom: 16px;
  align-items: center;
}

/* 하단 언더라인 */
nav::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background-color: #eee;
}

.nav-links {
  grid-column: 2;
  display: flex;
  gap: 32px;
}

.nav-item {
  position: relative;
  text-decoration: none;
  color: #666;
  font-weight: 300;
  font-size: 15px;
  padding: 8px 0;
  display: flex;
  align-items: center;
}

.nav-text {
  display: inline-block;
  position: relative;
  padding-right: 4px;
  text-align: center;
  min-width: 32px;
}

.nav-item.router-link-active {
  color: #4169e1;
  font-weight: bold;
}

/* 활성 탭의 하단 파란색 라인 */
.nav-item.router-link-active .nav-text::after {
  content: "";
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% + 10px);
  height: 2px;
  background-color: #4169e1;
}

.notification-badge {
  position: absolute;
  /* top: -1px; */
  right: -6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background-color: #ff4b4b;
  color: white;
  font-size: 9px;
  font-weight: 500;
  padding: 1px 3px;
  border-radius: 8px;
  min-width: 12px;
  height: 12px;
  line-height: 1;
}

.link-button {
  grid-column: 3;
  justify-self: end;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.link-button:hover {
  background-color: #f5f5f5;
}
</style>
