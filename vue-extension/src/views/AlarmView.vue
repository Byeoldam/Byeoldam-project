<template>
  <div class="p-2">
    <TransitionGroup name="list" tag="div" class="space-y-2">
      <div
        v-for="(alarm, index) in alarms"
        :key="alarm.id"
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
            <svg
              v-if="alarm.type === 'invitation'"
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
          <svg
            v-if="alarm.type === 'notification'"
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
          <p class="text-gray-600 flex-1 mr-4">{{ alarm.description }}</p>
          <div v-if="alarm.type === 'invitation'" class="flex gap-1 shrink-0">
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
        </div>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useBookmarkStore } from "@/stores/bookmarkStore";

const bookmarkStore = useBookmarkStore();
const access_token = bookmarkStore.accessToken;
onMounted(() => {
  console.log("access_token :", access_token);
});

const alarms = ref([
  {
    id: 1,
    title: "GOD생 사는 현직자의 경제이야기",
    description: "이 포스트를 추가한 지 7일이 지났습니다. 확인해보세요.",
    type: "notification",
    offset: 0,
  },
  {
    id: 2,
    title: "알고리즘 마스터하기",
    description: "컬렉션에 초대되었습니다! 지금 바로 확인하고 참여해보세요.",
    type: "invitation",
    offset: 0,
  },
  {
    id: 3,
    title: "Redis를 활용한 이메일 인증하기",
    description: "이 포스트를 추가한 지 7일이 지났습니다. 확인해보세요.",
    type: "notification",
    offset: 0,
  },
  {
    id: 4,
    title: "안드로이드 스튜디오 시작하기",
    description: "이 포스트를 추가한 지 7일이 지났습니다. 확인해보세요.",
    type: "notification",
    offset: 0,
  },
  {
    id: 5,
    title: "오사카 여행 계획",
    description: "컬렉션에 초대되었습니다! 지금 바로 확인하고 참여해보세요.",
    type: "invitation",
    offset: 0,
  },
]);

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
    alarms.value[currentIndex].offset = diff;
  }
};

const mouseUp = (index) => {
  if (currentIndex === -1) return;
  const alarm = alarms.value[currentIndex];
  if (Math.abs(alarm.offset) > 100) {
    // 100px 이상 드래그하면 삭제
    alarms.value.splice(currentIndex, 1);
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
