<template>
  <div class="p-4">
    <!-- 헤더 -->
    <div class="flex justify-between items-center mb-4">
      <div class="flex items-center gap-2">
        <!-- Bell 아이콘 -->
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-5 h-5 text-indigo-600"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"
          />
        </svg>
        <h2 class="text-lg font-semibold text-gray-800">알림</h2>
      </div>
      <button 
        class="p-2 rounded-full hover:bg-gray-100 transition-colors text-gray-500"
        @click="clearAllAlarms"
      >
        <!-- Trash 아이콘 -->
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
            d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"
          />
        </svg>
      </button>
    </div>

    <!-- 알림 리스트 -->
    <TransitionGroup
      v-if="alarmList.length"
      name="list"
      tag="div"
      class="space-y-3"
    >
      <div
        v-for="alarm in alarmList"
        :key="alarm.notificationId"
        class="bg-white rounded-xl p-4 shadow-sm hover:shadow-md transition-all duration-200 relative"
        @mousedown="mouseDown"
        @mousemove="mouseMove"
        @mouseup="mouseUp"
        @mouseleave="mouseUp"
        :style="{ transform: `translateX(${alarm.offset || 0}px)` }"
      >
        <div class="flex items-start gap-3">
          <!-- 알림 타입별 아이콘 -->
          <div class="bg-indigo-100 rounded-full p-2 shrink-0">
            <!-- INVITE 타입 아이콘 -->
            <svg
              v-if="alarm.type === 'INVITE'"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="w-5 h-5 text-indigo-600"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z"
              />
            </svg>
            <!-- BOOKMARK 타입 아이콘 -->
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="w-5 h-5 text-indigo-600"
            >
              <path
                fill-rule="evenodd"
                d="M6.32 2.577a49.255 49.255 0 0111.36 0c1.497.174 2.57 1.46 2.57 2.93V21a.75.75 0 01-1.085.67L12 18.089l-7.165 3.583A.75.75 0 013.75 21V5.507c0-1.47 1.073-2.756 2.57-2.93z"
                clip-rule="evenodd"
              />
            </svg>
          </div>

          <div class="flex-1 min-w-0">
            <div class="flex justify-between items-center">
              <div>
                <h3 class="font-medium text-gray-800">{{ alarm.title }}</h3>
                <span class="text-xs text-gray-500">{{ formatTime(alarm.createdAt) }}</span>
              </div>
              <!-- 화살표 아이콘 -->
              <svg
                v-if="alarm.type === 'BOOKMARK' && alarm.url"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-5 h-5 text-gray-400 group-hover:text-indigo-600 transition-colors shrink-0"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M8.25 4.5l7.5 7.5-7.5 7.5"
                />
              </svg>
            </div>
            
            <p class="text-sm text-gray-600 mt-1 truncate">
              {{ alarm.message }}
            </p>

            <!-- INVITE 타입일 때 버튼 -->
            <div v-if="alarm.type === 'INVITE'" class="flex gap-2 mt-3">
              <button 
                class="flex-1 py-2 text-sm rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 transition-colors"
                @click="rejectInvite(alarm.notificationId)"
              >
                거절
              </button>
              <button 
                class="flex-1 py-2 text-sm rounded-lg bg-indigo-600 text-white hover:bg-indigo-700 transition-colors"
                @click="acceptInvite(alarm.notificationId)"
              >
                수락
              </button>
            </div>
          </div>
        </div>
      </div>
    </TransitionGroup>

    <!-- 빈 상태 -->
    <div
      v-else
      class="flex flex-col items-center justify-center p-6 bg-gradient-to-b from-indigo-50 to-white rounded-lg border border-indigo-100"
    >
      <div class="bg-white shadow-sm rounded-full p-3 mb-4">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-6 h-6 text-indigo-800"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"
          />
        </svg>
      </div>
      <h3 class="text-indigo-800 font-medium mb-2">확인할 알림이 없습니다</h3>
      <p class="text-indigo-600 text-sm text-center">
        새로운 알림이 오면 여기서 확인할 수 있어요
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useBookmarkStore } from "@/stores/bookmarkStore";

const bookmarkStore = useBookmarkStore();
const alarmList = ref([]);

onMounted(() => {
  alarmList.value = bookmarkStore.alarmList;
});

// 시간 포맷팅 함수
const formatTime = (createdAt) => {
  const now = new Date();
  const created = new Date(createdAt);
  const diffInMinutes = Math.floor((now - created) / (1000 * 60));
  
  if (diffInMinutes < 1) return '방금 전';
  if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
  
  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) return `${diffInHours}시간 전`;
  
  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 7) return `${diffInDays}일 전`;
  
  return created.toLocaleDateString();
};

// 드래그 관련 변수와 함수들
let startX = 0;
let currentAlarm = null;

const mouseDown = (e) => {
  startX = e.clientX;
  currentAlarm = e.currentTarget;
};

const mouseMove = (e) => {
  if (!currentAlarm) return;
  const diff = e.clientX - startX;
  if (diff < 0) { // 왼쪽으로만 드래그 가능
    const alarmId = currentAlarm.getAttribute('data-alarm-id');
    const alarm = alarmList.value.find(a => a.notificationId === alarmId);
    if (alarm) {
      alarm.offset = diff;
    }
  }
};

const mouseUp = () => {
  if (!currentAlarm) return;
  const alarmId = currentAlarm.getAttribute('data-alarm-id');
  const alarm = alarmList.value.find(a => a.notificationId === alarmId);
  
  if (alarm && alarm.offset < -100) {
    // 삭제 처리
    removeAlarm(alarmId);
  } else if (alarm) {
    alarm.offset = 0;
  }
  
  currentAlarm = null;
};

// API 요청 함수들
const removeAlarm = async (notificationId) => {
  try {
    const response = await api.delete(`/notifications/${notificationId}/read`);
    if (response.data.status) {
      alarmList.value = alarmList.value.filter(a => a.notificationId !== notificationId);
    }
  } catch (error) {
    console.error("알림 삭제 실패:", error);
  }
};

const clearAllAlarms = async () => {
  try {
    const response = await api.delete('/notifications/all');
    if (response.data.status) {
      alarmList.value = [];
    }
  } catch (error) {
    console.error("전체 알림 삭제 실패:", error);
  }
};

// URL 이동 처리
const handleClick = (alarm) => {
  if (alarm.type === 'BOOKMARK' && alarm.url) {
    chrome.tabs.create({ url: alarm.url });
  }
};
</script>

<style scoped>
.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.list-leave-active {
  position: absolute;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>