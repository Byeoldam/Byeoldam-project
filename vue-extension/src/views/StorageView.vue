<template>
  <div class="p-3">
    <!-- 가이드라인 -->
    <div class="guide-text-with-icon mb-5">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="w-4 h-4"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z"
        />
      </svg>
      <span class="guide-text"
        >북마크를 저장하려면 컬렉션과 태그를 선택하거나 직접 입력해주세요.</span
      >
    </div>

    <!-- 컬렉션 선택 -->
    <div class="mb-4">
      <label class="block text-sm font-medium mb-2 mt-5">컬렉션</label>
      <div class="flex gap-2">
        <select
          v-model="selectedCollection"
          class="flex-1 p-2 border rounded-md focus:outline-none focus:border-gray-400"
        >
        <!-- <option value="" disabled selected>컬렉션을 선택하세요</option> -->

          <!-- 개인 컬렉션 -->
          <optgroup v-if="bookmarkOptions.personalCollections.length > 0" label="개인 컬렉션">
            <option 
              v-for="collection in bookmarkOptions.personalCollections" 
              :key="collection.collectionId"
              :value="{ collectionId: collection.collectionId, isPersonal: collection.isPersonal }"
            >
              {{ collection.name }}
            </option>
          </optgroup>
          <!-- 공유 컬렉션 -->
          <optgroup v-if="bookmarkOptions.sharedCollections.length > 0" label="공유 컬렉션">
            <option 
              v-for="collection in bookmarkOptions.sharedCollections" 
              :key="collection.collectionId"
              :value="{ collectionId: collection.collectionId, isPersonal: collection.isPersonal }"
            >
              {{ collection.name }}
            </option>
          </optgroup>
        </select>
        <input
          v-model="newCollection"
          type="text"
          placeholder="새로운 컬렉션 만들기"
          class="flex-1 p-2 border rounded-md focus:outline-none focus:border-gray-400"
        />
      </div>
    </div>

    <!-- 태그 선택 -->
    <div class="mb-4">
      <label class="block text-sm font-medium mb-2">태그</label>
      <div class="flex flex-wrap gap-2 mb-2" id="tag-container">
        <!-- GPT 태그 표시 -->
        <span
          v-for="(tag, index) in gptTags"
          :key="'gpt-' + index"
          :style="{backgroundColor: tag.tagColor,borderColor: tag.tagBorderColor}"
          class="border border-black text-black px-3 py-1 rounded-full text-sm flex items-center"
        >
          {{ "# " + tag.tagName }}
          <button
            class="ml-2 text-gray-600 hover:text-gray-800"
            @click="removeTag(index, 'gpt')"
          >
            &times;
          </button>
        </span>

        <!-- 사용자 추가 태그 표시 -->
        <span
          v-for="(tag, index) in newTags"
          :key="'new-' + index"
          :style="{ backgroundColor: tag.tagColor,borderColor: tag.tagBorderColor}"
           class="border border-black text-black px-3 py-1 rounded-full text-sm flex items-center"
        >
          {{ "# " + tag.tagName }}
          <button
            class="ml-2 text-gray-600 hover:text-gray-800"
            @click="removeTag(index, 'custom')"
          >
            &times;
          </button>
        </span>
      </div>
      <div class="flex gap-2">
        <input
          type="text"
          v-model="newTag"
          @keyup.enter="addTag"
          placeholder="북마크를 설명할 태그를 입력해보세요."
          class="flex-1 p-2 border rounded-md focus:outline-none focus:border-gray-400"
        />
        <button
          class="flex items-center justify-center bg-blue-500 text-white w-10 h-10 rounded-md hover:bg-blue-600 transition-colors duration-200"
          @click="addTag"
        >
          <span class="text-lg font-bold">+</span>
        </button>
      </div>
    </div>

    <!-- RSS 피드 구독 선택 -->
    <label class="block text-sm font-medium mb-2">RSS 피드 구독</label>
    <div class="flex items-center justify-between mb-4">
      <div
        :class="
          isRssFeed
            ? 'flex items-center gap-2 bg-blue-50 text-blue-700 px-3 py-2 rounded-md'
            : 'flex items-center gap-2 bg-red-100 text-red-700 px-3 py-2 rounded-md'
        "
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-4 h-4"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12.75 19.5v-.75a7.5 7.5 0 00-7.5-7.5H4.5m0-6.75h.75c7.87 0 14.25 6.38 14.25 14.25v.75M6 18.75a.75.75 0 11-1.5 0 .75.75 0 011.5 0z"
          />
        </svg>
        <span class="text-sm">
          {{
            isRssFeed
              ? "이 포스트는 RSS 피드 구독이 가능합니다."
              : "이 포스트는 RSS 피드 구독이 불가능합니다."
          }}
        </span>
      </div>
      <label class="relative inline-flex items-center cursor-pointer">
        <input type="checkbox" class="sr-only peer" v-model="isRssFeed" />
        <div
          class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"
        ></div>
      </label>
    </div>

    <!-- 저장 버튼 -->
    <div class="flex justify-end">
      <button
        @click="saveBookmark"
        class="px-6 py-2 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-md hover:from-blue-600 hover:to-blue-700 transition-all duration-200 flex items-center gap-2"
      >
        <span>저장</span>
      </button>
    </div>
  </div>
  <div>User ID: {{ bookmarkStore.userId }}</div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/utils/api";
import { generatePastelColors } from "@/utils/colorUtils";
import { useBookmarkStore } from "@/stores/bookmarkStore";

const bookmarkStore = useBookmarkStore();

// 웹페이지 정보
const webpage = ref({
  siteUrl: null,
  title: null,
  readingTime: 0,
})

// 초기 로드 데이터
const bookmarkOptions = ref({
  personalCollections : [],
  sharedCollections: [],
  keywords: [],
  canSubscribeRss: false,
  notificationCnt: 0,
  hasNewFeed: false,
})

// 태그 정보
const gptTags = ref([ // GPT 생성 태그 배열
  { tagName: bookmarkOptions.value.keywords[0], ...generatePastelColors()  },
  { tagName: bookmarkOptions.value.keywords[1], ...generatePastelColors()  },
  { tagName: bookmarkOptions.value.keywords[2], ...generatePastelColors()  },
]); 
const newTag = ref(""); // 사용자 입력 태그
const newTags = ref([]); // 사용자 입력 태그 배열
const finalTags = computed(() => {
  return [...gptTags.value, ...newTags.value];
});


// 선택한 컬렉션 정보를 저장할 변수
const selectedCollection = ref({
  collectionId: null, 
  isPersonal: null    
});


onMounted(async () => {
  try {
    // 페이지정보와 토큰을 비동기적으로 가져오기
    const pageData = await new Promise((resolve, reject) => {
      chrome.runtime.sendMessage({ action: "GET_PAGE_INFO_FROM_BACK" }, (response) => {
        if (!response) {
              reject("페이지 정보 응답이 없습니다.");
              return;
          }

          if (response.pageInfo && response.pageInfo.siteUrl && response.pageInfo.title) {
            resolve(response);
        } else {
          reject("페이지 정보를 가져오는 데 실패했습니다.");
        }
      });
    });

    webpage.value = {
      siteUrl: pageData.pageInfo.siteUrl,
      title: pageData.pageInfo.title,
      readingTime: pageData.readingTime
    };

    // 초기 데이터 로드 API 요청
    const response = await api.post("/popup/load", { siteUrl: webpage.value.siteUrl, title: webpage.value.title });
    if (response.data.status) {
      bookmarkOptions.value = response.data.results;

      // bookmarkOptions가 업데이트된 후에 gptTags 업데이트
      gptTags.value = bookmarkOptions.value.keywords.slice(0, 3).map((keyword, index) => ({
        tagName: (index === 0 || index === 2) ? keyword.replace(/[\[\]]/g, '') : keyword,
        ...generatePastelColors(),
      }));



    } else {
      console.error("에러 발생:", response.data.message);
    }
  } catch (error) {
    console.error("데이터 로딩 실패:", error);
  }
});

const bookmarkSaveData = {
  bookmark_url: webpage.value.siteUrl, 
  collectionId: selectedCollection.value.collectionId, 
  isPersonal: selectedCollection.value.isPersonal, 
  tags: finalTags.value.map((finalTag) => ({
    tagName: finalTag.tagName,
    tagColor: finalTag.tagColor,
    tagBorderColor: finalTag.tagBorderColor
  })),
  readingTime: webpage.value.readingTime  
};



// 북마크 저장 API 요청
const saveBookmark = async () => {
  console.log(JSON.stringify(bookmarkOptions.value));
  if (url.value) {
    try {
      const response = await api.post(
        "/bookmarks/extension", bookmarkSaveData
      );
      if (response.status === 201) {
        // 크롬 익스텐션 창에 저장완료 표시 뜨도록!
        
      } else {
        console.log("북마크 저장 실패, 다시 시도해주세요.");
      }
    } catch (error) {
      console.error("API 요청 실패:", error);
    }
  } else {
    console.log("accessToken 또는 url이 없습니다.");
  }
};

// 사용자 태그 생성
const addTag = () => {
  if (newTag.value.trim()) {
    const color = generatePastelColors();
    newTags.value.push({ 
      tagName: newTag.value.trim(), 
      tagColor: color.tagColor,
      tagBorderColor: color.tagBorderColor
    });
    newTag.value = "";  
  }
};

// 태그 삭제
const removeTag = (index, type) => {
  if (type === "gpt") {
    gptTags.value.splice(index, 1);
  } else {
    newTags.value.splice(index, 1);
  }
};
</script>

<style scoped>
/* 스타일 1: 심플한 회색 텍스트 */
.guide-text-simple {
  color: #666;
  font-size: 13px;
  line-height: 1.4;
  margin: -4px 0 12px;
}

/* 스타일 2: 정보 아이콘이 있는 스타일 */
.guide-text-with-icon {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: -4px 0 12px;
}

.guide-text-with-icon svg {
  color: #1a73e8;
  flex-shrink: 0;
  transform: translateY(-1px);
  width: 16px;
  height: 16px;
}

.guide-text {
  color: #5f6368;
  font-size: 13px;
  line-height: 1.4;
}

/* 스타일 3: 연한 배경색이 있는 스타일 */
.guide-text-with-bg {
  background-color: #f8f9fa;
  border-radius: 4px;
  padding: 8px 12px;
  margin: -4px 0 12px;
}

.guide-text-with-bg p {
  color: #5f6368;
  font-size: 13px;
  line-height: 1.4;
  margin: 0;
}
</style>