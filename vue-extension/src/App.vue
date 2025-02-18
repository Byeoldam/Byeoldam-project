<template>
  <div class="container">
    <nav>
      <div class="nav-links">
        <RouterLink :to="{ name: 'storage' }" class="nav-item">
          <span class="nav-text">저장</span>
        </RouterLink>
        <RouterLink :to="{ name: 'alarm' }" class="nav-item">
          <span class="nav-text">알림</span>
          <span
            v-if="bookmarkStore.badges.notificationCnt > 0"
            class="notification-badge"
          >
            {{ bookmarkStore.badges.notificationCnt }}
          </span>
        </RouterLink>
        <RouterLink :to="{ name: 'feed' }" class="nav-item">
          <span class="nav-text">피드</span>
          <span
            v-if="bookmarkStore.badges.hasNewFeed"
            class="notification-badge"
            >N</span
          >
        </RouterLink>
      </div>
      <button class="link-button" @click="goToByeoldam">
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
import { RouterView, useRouter } from "vue-router";
import { onMounted, ref, watchEffect } from "vue";
import api from "@/utils/api";
import { useBookmarkStore } from "./stores/bookmarkStore";

const bookmarkStore = useBookmarkStore();
const isDataLoaded = ref(false);

onMounted(async () => {
  // 확장 아이콘 클릭 시 백그라운드로 사용자 정보 Extension Storage 저장 요청 보내기
  chrome.runtime.sendMessage({ action: "popupOpened" });

  // 알림/피드 데이터 로드 API 요청
  try {
    // 알림/피드 데이터 로드 API 요청
    const [feedResponse, alarmResponse] = await Promise.allSettled([
      api.get("/subscriptions"),
      api.get("/notifications"),
    ]);

    // 피드 데이터 처리
    if (feedResponse.status === "fulfilled" && feedResponse.value.data.status) {
      const feedData = feedResponse.value.data.results.map((feed) => ({
        rssId: feed.rssId,
        name: feed.name,
        isRead: feed.isRead,
      }));
      console.log("feedData : ", feedData); // isRead 안넘어옴 확인!!
      bookmarkStore.setFeedList(feedData);
    } else {
      console.error(
        "피드 데이터 가져오기 실패 : ",
        feedResponse.reason?.response?.data?.message || feedResponse.reason
      );
    }

    // 알림 데이터 처리
    if (
      alarmResponse.status === "fulfilled" &&
      alarmResponse.value.data.status
    ) {
      const alarmData = alarmResponse.value.data.results.map((alarm) => ({
        notificationId: alarm.notificationId,
        type: alarm.type,
        title: alarm.title,
        message: alarm.message,
        url: alarm.url,
        createdAt: alarm.createdAt,
      }));
      console.log("alarmData : ", alarmData);
      bookmarkStore.setAlarmList(alarmData);
    } else {
      console.error(
        "알림 데이터 가져오기 실패 : ",
        alarmResponse.reason?.response?.data?.message || alarmResponse.reason
      );
    }
    isDataLoaded.value = true; // 데이터 로드 완료 후 상태 변경
  } catch (error) {
    console.error("API 요청 중 예기치 않은 오류 발생: ", error.message);
  }
});

// 메인페이지 바로가기
const goToByeoldam = () => {
  chrome.tabs.create({ url: "http://byeoldam.store/main" });
};

// StorageView 렌더링 지연
const router = useRouter();
router.beforeEach((to, from, next) => {
  if (to.name === "storage") {
    if (isDataLoaded.value) {
      next();
    } else {
      const unwatch = watchEffect(() => {
        if (isDataLoaded.value) {
          unwatch();
          next();
        }
      });
    }
  } else {
    next();
  }
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
  margin-bottom: 8px; /* 12px에서 8px로 줄임 */
  align-items: center;
  padding-bottom: 1px; /* 언더라인을 위한 최소 패딩 추가 */
}

/* 하단 언더라인 */
nav::after {
  content: "";
  position: absolute;
  bottom: -1px; /* 0에서 -1px로 변경하여 nav 바로 아래에 붙도록 */
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
  color: #3730a3;
  font-weight: bold;
}

/* 활성 탭의 하단 파란색 라인 */
.nav-item.router-link-active .nav-text::after {
  content: "";
  position: absolute;
  bottom: -9px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% + 10px);
  height: 2px;
  background-color: #3730a3;
}

.notification-badge {
  position: absolute;
  /* top: -1px; */
  right: -12px; /* 배지 위치 조정*/
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
