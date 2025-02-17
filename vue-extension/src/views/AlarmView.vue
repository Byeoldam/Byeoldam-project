<template>
  <div class="p-4">
    <!-- 알림 리스트 -->
    <div v-if="alarmList.length" class="space-y-2">
      <!-- 헤더 -->
      <div
        class="flex justify-between items-center py-2 border-b border-gray-100 -mt-4"
      >
        <div class="flex items-center gap-2">
          <div class="w-1.5 h-1.5 bg-indigo-600 rounded-full"></div>
          <span class="text-xs text-gray-600 font-medium"
            >최신 소식을 확인해보세요</span
          >
        </div>

        <button
          class="p-1 rounded-full hover:bg-gray-50 text-gray-400 hover:text-gray-600 transition-colors"
          @click="clearAllAlarms"
          aria-label="전체 알림 삭제"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="w-3.5 h-3.5"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0"
            />
          </svg>
        </button>
      </div>

      <!-- 알림 아이템 -->
      <div
        v-for="alarm in alarmList"
        :key="alarm.notificationId"
        class="bg-white rounded-md shadow-sm p-2.5 border border-gray-200 border-l-2 border-l-gray-300"
      >
        <div class="flex items-center gap-2">
          <!-- 알림 타입별 아이콘 -->
          <div class="bg-indigo-50 rounded-full p-1.5 shrink-0">
            <svg
              v-if="alarm.type === 'INVITE'"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="w-4 h-4 text-indigo-600"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z"
              />
            </svg>
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="w-4 h-4 text-indigo-600"
            >
              <path
                fill-rule="evenodd"
                d="M6.32 2.577a49.255 49.255 0 0111.36 0c1.497.174 2.57 1.46 2.57 2.93V21a.75.75 0 01-1.085.67L12 18.089l-7.165 3.583A.75.75 0 013.75 21V5.507c0-1.47 1.073-2.756 2.57-2.93z"
                clip-rule="evenodd"
              />
            </svg>
          </div>

          <!-- 알림 내용 -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <h3 class="text-xs font-sm text-gray-900">{{ alarm.title }}</h3>
              <span
                v-if="alarm.type === 'BOOKMARK' && alarm.url"
                class="text-xs text-gray-400"
              >
                <a
                  :href="alarm.url"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="hover:text-gray-600 truncate max-w-[150px] inline-block align-bottom"
                >
                  {{ alarm.url }}
                </a>
              </span>
            </div>
            <p class="text-xs text-gray-500 line-clamp-1">
              {{ alarm.message }}
            </p>
          </div>
          <!-- 액션 버튼 -->
          <button
            v-if="alarm.type === 'BOOKMARK'"
            @click="removeAlarm(alarm.notificationId)"
            class="p-1 text-gray-400 hover:text-gray-600"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="w-4 h-4"
            >
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </button>

          <!-- INVITE 타입 버튼 -->
          <div v-if="alarm.type === 'INVITE'" class="flex gap-1.5 ml-2">
            <button
              class="px-2 py-1 text-xs rounded border border-gray-200 text-gray-600 hover:bg-gray-50"
              @click="removeAlarm(alarm.notificationId)"
            >
              거절
            </button>
            <button
              class="px-2 py-1 text-xs rounded bg-indigo-600 text-white hover:bg-indigo-700"
              @click="acceptInvite(alarm.notificationId)"
            >
              수락
            </button>
          </div>
        </div>
      </div>
    </div>

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
import api from "@/utils/api";

const bookmarkStore = useBookmarkStore();
const alarmList = ref([]);

onMounted(() => {
  // LocalDateTime 형식의 문자열을 Date로 변환하여 최신순으로 정렬
  alarmList.value = [...bookmarkStore.alarmList].sort((a, b) => {
    const dateA = new Date(a.createdAt.replace("T", " "));
    const dateB = new Date(b.createdAt.replace("T", " "));
    return dateB - dateA;
  });
});

// 알림 삭제 API 요청
const removeAlarm = async (notificationId) => {
  try {
    await api.delete(`/notifications/${notificationId}/read`);
    alarmList.value = alarmList.value.filter(
      (alarm) => alarm.notificationId !== notificationId
    );
  } catch (error) {
    console.error("알림 삭제 실패:", error.response?.data || error.message);
  }
};

// 알림 전체 삭제 API 요청
const clearAllAlarms = async () => {
  try {
    await api.delete("/notifications/read");
    alarmList.value = [];
  } catch (error) {
    console.error(
      "전체 알림 삭제 실패:",
      error.response?.data || error.message
    );
  }
};

// 초대 수락 API 요청
const acceptInvite = async (notificationId) => {
  try {
    await api.post("/collections/shared/invite", {
      notificationId: notificationId,
    });
    alarmList.value = alarmList.value.filter(
      (alarm) => alarm.notificationId !== notificationId
    );
  } catch (error) {
    console.error("초대 수락 실패:", error.response?.data || error.message);
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
