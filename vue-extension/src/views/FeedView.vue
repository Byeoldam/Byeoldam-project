<template>
  <div class="p-2">
    <div>
      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">간헐적 고양이</h2>
        <span
          class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-500 ring-1 ring-red-500/20 animate-pulse"
        >
          New
        </span>
      </div>

      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">TED</h2>
      </div>

      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">Coding Horror</h2>
      </div>

      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">연합 뉴스</h2>
      </div>

      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">지식몰</h2>
      </div>

      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">채널십오야</h2>
        <span
          class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-500 ring-1 ring-red-500/20 animate-pulse"
        >
          New
        </span>
      </div>

      <div
        class="flex justify-between items-center p-2 hover:bg-gray-100 rounded-md cursor-pointer"
      >
        <h2 class="font-medium">Netflix Technology Blog</h2>
      </div>
    </div>
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

const feeds = ref([
  { title: "간헐적 고양이", isNew: true },
  { title: "TED", isNew: false },
  { title: "Coding Horror", isNew: false },
  { title: "연합 뉴스", isNew: false },
  { title: "지식몰", isNew: false },
  { title: "채널신문아", isNew: true },
  { title: "Netflix Technology Blog", isNew: false },
]);
</script>

<style scoped>
/* 필요한 경우 추가 스타일링 */
</style>
