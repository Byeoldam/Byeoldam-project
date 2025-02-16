<template>
  <div class="p-2">
    <TransitionGroup
      v-if="alarmList.length"
      name="list"
      tag="div"
      class="space-y-2"
    >
      <div
        v-for="(alarm, index) in alarmList"
        :key="alarm.notificationId"
        class="border rounded-md p-3 transform transition-all duration-300 relative"
        @mousedown="mouseDown"
        @mousemove="mouseMove"
        @mouseup="mouseUp(index)"
        @mouseleave="mouseUp(index)"
        :style="{ transform: `translateX(${alarm.offset}px)` }"
      >
        <div class="flex justify-between items-center mb-2">
          <div class="flex items-center">
            <h2 class="font-semibold">{{ alarm.title }}</h2>

            <!-- INVITE 알림 아이콘 -->
            <svg
              v-if="alarm.type === 'INVITE'"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="w-5 h-5 text-blue-500 ml-2"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z"
              />
            </svg>
          </div>

          <!-- BOOKMARK 알림 아이콘 -->
          <svg
            v-if="alarm.type === 'BOOKMARK'"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="w-5 h-5 text-gray-600"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"
            />
          </svg>
        </div>

        <div class="flex items-center justify-between">
          <!-- BOOKMARK 알림 메시지 -->
          <p class="text-gray-600 flex-1 mr-4" v-if="alarm.type === 'BOOKMARK'">
            {{ alarm.message }}
          </p>

          <!-- INVITE 알림 메시지 -->
          <p class="text-gray-600 flex-1 mr-4" v-if="alarm.type === 'INVITE'">
            {{ alarm.description }}
          </p>

          <!-- INVITE 알림의 버튼 (수락 / 거절) -->
          <div v-if="alarm.type === 'INVITE'" class="flex gap-1 shrink-0">
            <button
              class="px-2 py-0.5 text-sm rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50"
            >
              거절
            </button>
            <button
              class="px-2 py-0.5 text-sm rounded-md bg-blue-500 text-white hover:bg-blue-600"
            >
              수락
            </button>
          </div>

          <!-- 자세히 보기 링크 (모든 알림 타입에 적용 가능) -->
          <a
            v-if="alarm.url"
            :href="alarm.url"
            target="_blank"
            class="px-3 py-1 text-sm text-blue-500 hover:text-blue-600"
          >
            자세히 보기
          </a>
        </div>
      </div>
    </TransitionGroup>

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
          class="w-6 h-6 text-indigo-500"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"
          />
        </svg>
      </div>
      <h3 class="text-indigo-900 font-medium mb-2">확인할 알림이 없습니다</h3>
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

onMounted(async () => {
  alarmList.value = bookmarkStore.alarmList;
});

// 알림 삭제 API 요청
const removeAlarm = async () => {
  try {
    const response = await api.delete("/notifications/{notificationId}/read");
    if (response.data.status) {
      console.log("알림 삭제 성공");
      // 화면에서 제거
    }
  } catch (error) {
    console.log("알림 삭제 요청 실패:", error.response.data.message);
  }
};

// 초대 수락 API 요청

// 초대 거절 API 요청

// 알림 삭제 애니메이션
let startX = 0;
let currentIndex = -1;

const mouseDown = (e) => {
  startX = e.clientX;
  const element = e.target.closest(".border");
  currentIndex = Array.from(element.parentNode.children).indexOf(element);
};

const mouseMove = (e) => {
  if (currentIndex === -1) return;
  const diff = e.clientX - startX;
  if (diff > 0) {
    // 오른쪽으로만 드래그 가능하도록 변경
    alarmList.value[currentIndex].offset = diff;
  }
};

const mouseUp = (index) => {
  if (currentIndex === -1) return;
  const alarm = alarmList.value[currentIndex];
  if (Math.abs(alarm.offset) > 100) {
    // 100px 이상 드래그하면 삭제
    alarmList.value.splice(currentIndex, 1);
  } else {
    alarm.offset = 0; // 원위치로 복귀
  }
  currentIndex = -1;
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
</style>
