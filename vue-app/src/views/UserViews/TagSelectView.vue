<template>
    <div class="wrapper">
      <!-- max-w-lg를 max-w-xl로, height를 vh 단위로 설정 -->
      <div class="max-w-xl mx-auto p-8 bg-white shadow-lg rounded-lg h-[80vh] flex flex-col">
        <h2 class="text-2xl font-bold text-center mb-4">당신만의 태그로 더 나은 추천을 받아보세요</h2>
        <p class="text-gray-600 text-center mb-8">태그를 추가하면 관심사에 맞는 포스트를 추천받을 수 있어요.</p>
  
        <!-- 추천 태그 목록 -->
        <div class="flex flex-wrap gap-3 mb-8">
          <span v-for="tag in recommendedTags" :key="tag.tagName"
                :style="{
                  backgroundColor: tag.tagColor,
                  borderColor: tag.tagBolder
                }"
                class="tag"
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
          <div v-for="(tag, index) in selectedTags" :key="index" class="tag-item">
            <span class="tag"
                  :style="{
                    backgroundColor: tag.tagColor,
                    borderColor: tag.tagBolder
                  }">
              # {{ tag.tagName }}
            </span>
            <button @click="removeTag(tag)" class="delete-btn">×</button>
          </div>
        </div>
  
        <div class="flex justify-between mt-auto">
          <!-- 이전 버튼 -->
          <router-link :to="{ name: 'collectionSelect' }">
            <button class="px-6 py-2.5 bg-gray-300 rounded-lg hover:bg-gray-400">이전</button>
          </router-link>
  
          <!-- 완료 버튼 -->
          <router-link :to="{ name: 'main' }">
            <button @click="saveSelectedTags" class="px-6 py-2.5 bg-blue-500 text-white rounded-lg hover:bg-blue-600" style="background-color: #3730A3">완료</button>
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

// BookmarkTagSetting에서 가져온 색상 관련 함수들
const previousHues = ref([]);
const MIN_HUE_DIFFERENCE = 30;

const hslToHex = (h, s, l) => {
    s /= 100;
    l /= 100;
    const c = (1 - Math.abs(2 * l - 1)) * s;
    const x = c * (1 - Math.abs(((h / 60) % 2) - 1));
    const m = l - c / 2;
    let r = 0, g = 0, b = 0;

    if (0 <= h && h < 60) { r = c; g = x; b = 0; }
    else if (60 <= h && h < 120) { r = x; g = c; b = 0; }
    else if (120 <= h && h < 180) { r = 0; g = c; b = x; }
    else if (180 <= h && h < 240) { r = 0; g = x; b = c; }
    else if (240 <= h && h < 300) { r = x; g = 0; b = c; }
    else if (300 <= h && h < 360) { r = c; g = 0; b = x; }

    const toHex = (value) => {
        const hex = Math.round((value + m) * 255).toString(16);
        return hex.length === 1 ? "0" + hex : hex;
    };

    return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
};

const isDistinctHue = (hue) => {
    return previousHues.value.every((prevHue) => {
        const diff = Math.abs(hue - prevHue);
        return Math.min(diff, 360 - diff) >= MIN_HUE_DIFFERENCE;
    });
};

const getDistinctHue = () => {
    let attempts = 0;
    let hue;

    do {
        hue = Math.floor(Math.random() * 359);
        attempts++;
        if (attempts > 100) {
            previousHues.value = [];
            break;
        }
    } while (!isDistinctHue(hue));

    if (previousHues.value.length >= 10) {
        previousHues.value.shift();
    }
    previousHues.value.push(hue);

    return hue;
};

const generateRandomColor = () => {
    const hue = getDistinctHue();
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 50, lightness);
};

const generateDarkerColor = () => {
    const hue = getDistinctHue();
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 20, lightness - 25);
};

const addTag = (tagName) => {
    if (tagName && !selectedTags.value.some(tag => tag.tagName === tagName)) {
        const tagColor = generateRandomColor();
        const tagBolder = generateDarkerColor();
        selectedTags.value.push({
            tagName,
            tagColor,
            tagBolder
        });
        console.log('현재 선택된 태그들:', selectedTags.value);
        newTag.value = "";
    }
};

const removeTag = (tag) => {
    selectedTags.value = selectedTags.value.filter(t => t.tagName !== tag.tagName);
};


const saveSelectedTags = async () => {
  try {
    // 모든 태그를 한 번에 전송
    const tagsToSave = selectedTags.value.map(tag => ({
      tagName: tag.tagName,
      tagColor: tag.tagColor,
      tagBolder: tag.tagBolder
    }));

    const response = await bookmarkStore.saveUserDefineTags(tagsToSave);
    console.log('저장 응답:', response);
    // alert("태그가 성공적으로 저장되었습니다.");
  } catch (error) {
    console.error("태그 저장 중 오류 발생:", error);
    // alert("태그 저장에 실패했습니다.");
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

.tag-item {
    display: flex;
    align-items: center;
    gap: 2px;
}

.tag {
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 500;
    white-space: nowrap;
    border: none;
    color: rgb(95, 93, 93);
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    transition: all 0.2s ease;
    cursor: pointer;
}

.tag:hover {
    transform: translateY(-1.5px);
    box-shadow: 0 3px 6px rgba(0,0,0,0.15);
}

.delete-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 1.2rem;
    color: #666;
    padding: 0 4px;
}

.delete-btn:hover {
    color: #ce3e3ebc;
}
</style>