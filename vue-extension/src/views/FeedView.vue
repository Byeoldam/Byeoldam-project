<template>
  <div class="p-2">
    <div v-if="feedList.length">
      <div
        v-for="feed in feedList"
        :key="feed.rssId"
        class="flex items-center gap-3 p-2.5 hover:bg-gray-50 rounded-md cursor-pointer border-b last:border-0 border-gray-100"
        @click="openServicePage(feed.rssId)"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          :class="[
            'w-5 h-5',
            feed.isRead ? 'text-indigo-400' : 'text-gray-500',
          ]"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12.75 19.5v-.75a7.5 7.5 0 00-7.5-7.5H4.5m0-6.75h.75c7.87 0 14.25 6.38 14.25 14.25v.75M6 18.75a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
          />
        </svg>
        <h2 class="font-medium flex-1">{{ feed.name }}</h2>
        <div v-if="!feed.isRead" class="w-2 h-2 rounded-full bg-red-500"></div>
      </div>
    </div>
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
            d="M12.75 19.5v-.75a7.5 7.5 0 00-7.5-7.5H4.5m0-6.75h.75c7.87 0 14.25 6.38 14.25 14.25v.75M6 18.75a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
          />
        </svg>
      </div>
      <h3 class="text-indigo-900 font-medium mb-2">피드 목록이 비어있어요</h3>
      <p class="text-indigo-600 text-sm text-center">
        관심있는 사이트를 구독해보세요
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useBookmarkStore } from "@/stores/bookmarkStore";

const bookmarkStore = useBookmarkStore();
const feedList = ref([]);

onMounted(() => {
  feedList.value = bookmarkStore.feedList;
});

const openServicePage = (feedId) => {
  const serviceUrl = `https://example.com/feed/${feedId}`;
  chrome.tabs.create({ url: serviceUrl });
};
</script>

<style scoped></style>
