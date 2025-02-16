<template>
  <div class="p-2">
    <div v-if="feedList.length">
      <div
        v-for="feed in feedList"
        :key="feed.rssId"
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
        @click="openServicePage(feed.rssId)"
      >
        <h2 class="font-medium">{{ feed.name }}</h2>
        <span
          v-if="!feed.isRead"
          class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-500 ring-1 ring-red-500/20 animate-pulse"
        >
          New
        </span>
      </div>
    </div>
    <p v-else class="text-center text-gray-500">구독한 피드가 없습니다.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/utils/api";

const feedList = ref([]);

onMounted(async () => {
  try {
    const response = await api.get("/subscriptions");
    if (response.data.status) {
      feedList.value = response.data.results.map((feed) => ({
        rssId: feed.rssId,
        name: feed.name,
        isRead: feed.isRead,
      }));
    }
  } catch (error) {
    console.error("피드 데이터를 불러오는 중 오류 발생:", error);
  }
});

const openServicePage = (feedId) => {
  const serviceUrl = `https://example.com/feed/${feedId}`;
  chrome.tabs.create({ url: serviceUrl });
};
</script>

<style scoped></style>
