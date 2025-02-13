<template>
    <div class="wrapper">
      <!-- max-w-lg를 max-w-xl로, height를 vh 단위로 설정 -->
      <div class="max-w-xl mx-auto p-8 bg-white shadow-lg rounded-lg h-[80vh] flex flex-col">
        <h2 class="text-2xl font-bold text-center mb-4">당신만의 태그로 더 나은 추천을 받아보세요</h2>
        <p class="text-gray-600 text-center mb-8">태그를 추가하면 관심사에 맞는 포스트를 추천받을 수 있어요.</p>
  
        <!-- 추천 태그 목록 -->
        <div class="flex flex-wrap gap-3 mb-8">
          <span v-for="tag in recommendedTags" :key="tag.tagName"
                class="px-3 py-1.5 text-sm rounded-lg bg-gray-200 cursor-pointer hover:bg-gray-300"
                @click="addTag(tag.tagName)">
            # {{ tag.tagName }}
          </span>
        </div>
  
        <!-- 태그 입력 -->
        <div class="flex items-center gap-3 mb-8">
          <input 
              v-model="newTag" 
              type="text" 
              placeholder="추천 받고 싶은 키워드를 입력하고 엔터를 누르세요"
              class="flex-1 px-4 py-2.5 border rounded-lg text-base"
              @keyup.enter="addTag(newTag)"
          >
        </div>
  
        <!-- 선택된 태그 목록 -->
        <div class="flex flex-wrap gap-3 mb-8">
          <span v-for="(tag, index) in selectedTags" 
                :key="index"
                class="px-3 py-1.5 text-sm rounded-lg bg-blue-500 text-white flex items-center gap-2">
            # {{ tag }}
            <button @click="removeTag(tag)" class="text-white text-xs font-bold">✕</button>
          </span>
        </div>
  
        <div class="flex justify-between mt-auto">
          <!-- 이전 버튼 -->
          <router-link :to="{ name: 'collectionSelect' }">
            <button class="px-6 py-2.5 bg-gray-300 rounded-lg hover:bg-gray-400">이전</button>
          </router-link>
  
          <!-- 완료 버튼 -->
          <router-link :to="{ name: 'main' }">
            <button @click="saveSelectedTags" class="px-6 py-2.5 bg-blue-500 text-white rounded-lg hover:bg-blue-600">완료</button>
          </router-link>
        </div>
      </div>
    </div>
  </template>
  
  

<script setup>
import { ref, onMounted } from "vue";
import { RouterLink } from "vue-router";
import { useErrorStore } from "@/stores/error";
import { useBookmarkStore } from "@/stores/bookmark";

const errorStore = useErrorStore();
const bookmarkStore = useBookmarkStore();

// 추천 태그를 저장할 ref 생성
const recommendedTags = ref([]);

// 컴포넌트가 마운트될 때 추천 태그 데이터 불러오기
onMounted(async () => {
    try {
        await bookmarkStore.getTopTags();
        // results.tagList에서 태그 데이터 가져오기
        recommendedTags.value = bookmarkStore.topTags.results?.tagList || [];
    } catch (error) {
        console.error("추천 태그 로딩 실패:", error);
        errorStore.setError("추천 태그를 불러오는데 실패했습니다.");
    }
});

const selectedTags = ref([]);
const newTag = ref("");

const addTag = (tagName) => {
    if (tagName && !selectedTags.value.includes(tagName)) {
        selectedTags.value.push(tagName);
        newTag.value = "";
    }
};

const removeTag = (tag) => {
    selectedTags.value = selectedTags.value.filter(t => t !== tag);
};

const saveSelectedTags = async () => {
  try {
    // tagList 형식에 맞춰 데이터 구조화
    const tagsData = {
      tagList: selectedTags.value.map(tag => ({
        tagName: tag,
        tagColor: "#3B82F6",  // 기본 색상 설정
        tagBolder: "normal"   // 기본 굵기 설정
      }))
    };
    
    await bookmarkStore.saveUserDefineTags(tagsData);
    errorStore.setSuccess("태그가 성공적으로 저장되었습니다.");
  } catch (error) {
    console.error("태그 저장 중 오류 발생:", error);
    errorStore.setError("태그 저장에 실패했습니다.");
  }
};
</script>

<style scoped>
.wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    padding: 2rem;
    background-color: #f9fafb;
}
</style>