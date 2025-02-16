<template>
  <!--로딩 뷰-->
  <div
    v-if="isLoading"
    class="fixed inset-0 flex items-center justify-center z-50"
  >
    <div
      class="w-10 h-10 border-4 border-gray-200 border-t-4 border-t-blue-500 rounded-full animate-spin mt-[50px]"
    ></div>
  </div>
  <!-- 저장 완료 후 결과 뷰 -->
  <div v-else-if="isSaved" class="p-3">
    <!-- 결과메시지 -->
    <div class="guide-text-with-icon mb-5">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="w-4 h-4 text-blue-500"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <span class="guide-text text-blue-700">
        {{ savedResult.message }}
      </span>
    </div>

    <div class="bg-gray-50 p-4 rounded-lg mb-4">
      <h3 class="font-medium text-base mb-1">{{ webpage.title }}</h3>
      <a
        :href="webpage.siteUrl"
        class="text-sm text-blue-600 hover:underline break-all"
      >
        {{ webpage.siteUrl }}
      </a>
    </div>
  </div>

  <!-- 기본 뷰-->
  <div v-else class="p-3">
    <div class="space-y-6">
      <!-- RSS 피드 구독-->
      <div class="flex items-center justify-between">
        <div
          :class="
            bookmarkOptions.canSubscribeRss
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
              bookmarkOptions.canSubscribeRss
                ? "이 포스트는 RSS 피드 구독이 가능합니다."
                : "이 포스트는 RSS 피드 구독이 불가능합니다."
            }}
          </span>
        </div>
        <label class="relative inline-flex items-center cursor-pointer">
          <input
            type="checkbox"
            class="sr-only peer"
            v-model="subscribeRssResult"
          />
          <div
            class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"
          ></div>
        </label>
      </div>

      <!-- 가이드라인 -->
      <div
        class="guide-text-with-icon mb-5 p-3 bg-blue-50 rounded-lg flex items-center gap-2 text-gray-700 transition-all duration-300 ease-in-out hover:bg-blue-100 hover:shadow-sm"
        :class="{ 'scale-[1.02]': isHovered }"
        @mouseenter="isHovered = true"
        @mouseleave="isHovered = false"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-4 h-4 transition-transform duration-300"
          :class="{ 'rotate-12': isHovered }"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z"
          />
        </svg>
        <span class="guide-text text-sm font-medium">
          저장할
          <span
            class="text-blue-600 hover:text-blue-700 transition-colors duration-200 mx-1"
            >컬렉션</span
          >과
          <span
            class="text-blue-600 hover:text-blue-700 transition-colors duration-200 mx-1"
            >태그</span
          >를 설정해주세요
        </span>
      </div>

      <!-- 컬렉션 -->
      <div class="border-b border-gray-100">
        <div class="mb-4 space-y-2">
          <div class="flex items-center gap-2 mb-3">
            <svg
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
                d="M2.25 12.75V12A2.25 2.25 0 014.5 9.75h15A2.25 2.25 0 0121.75 12v.75m-8.69-6.44l-2.12-2.12a1.5 1.5 0 00-1.061-.44H4.5A2.25 2.25 0 002.25 6v12a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9a2.25 2.25 0 00-2.25-2.25h-5.379a1.5 1.5 0 01-1.06-.44z"
              />
            </svg>
            <label class="text-sm font-medium text-gray-700">컬렉션</label>
          </div>
          <div class="flex flex-col gap-2">
            <select
              v-model="selectedCollection"
              :class="[
                'p-2 border rounded-md focus:outline-none',
                isCollectionError
                  ? 'border-blue-400 bg-blue-50 ring-2 ring-blue-200'
                  : 'focus:border-gray-400',
              ]"
              @change="onCollectionChange"
            >
              <option value="" disabled>컬렉션을 선택하세요</option>

              <!-- 개인 컬렉션 -->
              <optgroup
                v-if="bookmarkOptions.personalCollections.length > 0"
                label="개인 컬렉션"
              >
                <option
                  v-for="collection in bookmarkOptions.personalCollections"
                  :key="collection.collectionId"
                  :value="{
                    collectionId: collection.collectionId,
                    isPersonal: collection.isPersonal,
                  }"
                >
                  {{ collection.name }}
                </option>
              </optgroup>

              <!-- 공유 컬렉션 -->
              <optgroup
                v-if="bookmarkOptions.sharedCollections.length > 0"
                label="공유 컬렉션"
              >
                <option
                  v-for="collection in bookmarkOptions.sharedCollections"
                  :key="collection.collectionId"
                  :value="{
                    collectionId: collection.collectionId,
                    isPersonal: collection.isPersonal,
                  }"
                >
                  {{ collection.name }}
                </option>
              </optgroup>
            </select>

            <!-- 에러 메시지 표시 -->
            <div v-if="errorMessage" class="guide-text-with-icon text-red-500">
              <span class="text-sm">{{ errorMessage }}</span>
            </div>

            <!--새로운 컬렉션 생성-->
            <div class="flex gap-2 items-center">
              <div class="inline-flex rounded-md shadow-sm" role="group">
                <button
                  type="button"
                  @click="setCollectionType(true)"
                  :class="[
                    'px-4 py-1 text-sm rounded-l-md border focus:outline-none',
                    newCollectionType
                      ? 'bg-blue-50 text-blue-600 border-blue-500'
                      : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50',
                  ]"
                >
                  개인
                </button>
                <button
                  type="button"
                  @click="setCollectionType(false)"
                  :class="[
                    'px-4 py-1 text-sm rounded-r-md border-t border-r border-b border-l-0 focus:outline-none',
                    !newCollectionType
                      ? 'bg-blue-50 text-blue-600 border-blue-500'
                      : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50',
                  ]"
                >
                  공유
                </button>
              </div>
              <input
                v-model="newInputCollection"
                type="text"
                maxlength="20"
                placeholder="새로 생성할 컬렉션명을 입력하세요"
                class="flex-1 p-2 border rounded-md focus:outline-none focus:border-gray-400"
                @focus="onNewCollectionNameFocus"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 태그 선택 -->
      <div class="mb-4 space-y-2">
        <div class="flex items-center gap-2 mb-3">
          <svg
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
              d="M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581c.699.699 1.78.872 2.607.33a18.095 18.095 0 005.223-5.223c.542-.827.369-1.908-.33-2.607L11.16 3.66A2.25 2.25 0 009.568 3z"
            />
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M6 6h.008v.008H6V6z"
            />
          </svg>
          <label class="text-sm font-medium text-gray-700">태그</label>
        </div>
        <div class="flex flex-wrap gap-2 mb-2" id="tag-container">
          <!-- GPT 태그 표시 -->
          <span
            v-for="(tag, index) in gptTags"
            :key="'gpt-' + index"
            :style="{
              backgroundColor: tag.tagColor,
              borderColor: tag.tagBorderColor,
            }"
            class="border px-3 py-1 rounded-full text-sm flex items-center text-gray-800"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              :style="{ color: tag.tagBorderColor }"
              class="w-3.5 h-3.5 mr-1.5"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 00-2.456 2.456z"
              />
            </svg>
            {{ "# " + tag.tagName }}
            <button
              class="ml-2 text-gray-500 hover:text-gray-700"
              @click="removeTag(index, 'gpt')"
            >
              &times;
            </button>
          </span>
          <!-- 사용자 추가 태그 표시 -->
          <span
            v-for="(tag, index) in newTags"
            :key="'new-' + index"
            :style="{
              backgroundColor: tag.tagColor,
              borderColor: tag.tagBorderColor,
            }"
            class="border px-3 py-1 rounded-full text-sm flex items-center"
          >
            {{ "# " + tag.tagName }}
            <button
              class="ml-2"
              :style="{ color: tag.tagTextColor }"
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
            placeholder="북마크를 설명할 태그를 입력해보세요"
            class="flex-1 p-2 border rounded-md focus:outline-none focus:border-gray-400"
          />
          <button
            class="flex items-center justify-center w-10 h-10 rounded-md bg-gradient-to-r from-indigo-500 to-indigo-600 text-white shadow-sm hover:shadow-md hover:from-indigo-600 hover:to-indigo-700 active:shadow-inner transition-all duration-200"
            @click="addTag"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="2.5"
              stroke="currentColor"
              class="w-5 h-5"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M12 4.5v15m7.5-7.5h-15"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- 저장 버튼 -->
      <div class="flex justify-end">
        <button
          @click="saveBookmark"
          class="group px-6 py-2.5 bg-gradient-to-r from-indigo-500 to-indigo-600 text-white rounded-md hover:from-indigo-600 hover:to-indigo-700 shadow-sm hover:shadow-md active:shadow-inner transition-all duration-200 flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            class="w-5 h-5 transition-transform duration-200 group-hover:-translate-y-0.5 group-hover:translate-x-0.5"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z"
            />
          </svg>
          <span class="font-medium">저장</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import api from "@/utils/api";
import { generatePastelColors } from "@/utils/colorUtils";

// 상태 변수수
const isLoading = ref(true);
const isHovered = ref(false); // 가이드라인 호버 상태
const isSaved = ref(false);
const savedResult = ref("");
const errorMessage = ref("");
const isCollectionError = ref(false); // 이미 저장된 url일때, select 강조 효과
const subscribeRssResult = ref(false);

// 웹페이지 정보
const webpage = ref({
  siteUrl: null,
  title: null,
  readingTime: 0,
});

// 초기 로드 데이터
const bookmarkOptions = ref({
  personalCollections: [],
  sharedCollections: [],
  keywords: [],
  canSubscribeRss: false,
  notificationCnt: 0,
  hasNewFeed: false,
});

// 컬렉션
const selectedCollection = ref("");
const newInputCollection = ref("");
const newCollectionType = ref(true); // 개인이면 true, 공유면 false

const onCollectionChange = () => {
  newInputCollection.value = "";
  newCollectionType.value = true;
  errorMessage.value = ""; // 에러 메시지 초기화
  isCollectionError.value = false; // 에러 상태 초기화
};

const onNewCollectionNameFocus = () => {
  selectedCollection.value = "";
  rrorMessage.value = ""; // 에러 메시지 초기화
  isCollectionError.value = false; // 에러 상태 초기화
};

const setCollectionType = (isPersonal) => {
  newCollectionType.value = isPersonal;
};

// 태그
const gptTags = ref([
  { tagName: bookmarkOptions.value.keywords[0], ...generatePastelColors() },
  { tagName: bookmarkOptions.value.keywords[1], ...generatePastelColors() },
  { tagName: bookmarkOptions.value.keywords[2], ...generatePastelColors() },
]);
const newTag = ref(""); // 사용자 입력 태그
const newTags = ref([]); // 사용자 입력 태그 배열
const finalTags = computed(() => {
  return [...gptTags.value, ...newTags.value];
});

const addTag = () => {
  // 사용자 태그 생성
  if (newTag.value.trim()) {
    const color = generatePastelColors();
    newTags.value.push({
      tagName: newTag.value.trim(),
      tagColor: color.tagColor,
      tagBorderColor: color.tagBorderColor,
    });
    newTag.value = "";
  }
};

const removeTag = (index, type) => {
  if (type === "gpt") {
    gptTags.value.splice(index, 1);
  } else {
    newTags.value.splice(index, 1);
  }
};

onMounted(async () => {
  try {
    const pageData = await new Promise((resolve, reject) => {
      chrome.runtime.sendMessage(
        { action: "GET_PAGE_INFO_FROM_BACK" },
        (response) => {
          if (!response) {
            reject("페이지 정보 응답이 없습니다.");
            return;
          }

          if (
            response.pageInfo &&
            response.pageInfo.siteUrl &&
            response.pageInfo.title
          ) {
            resolve(response);
          } else {
            reject("페이지 정보를 가져오는 데 실패했습니다.");
          }
        }
      );
    });

    webpage.value = {
      siteUrl: pageData.pageInfo.siteUrl,
      title: pageData.pageInfo.title,
      readingTime: pageData.readingTime,
    };

    // 초기 데이터 로드 API 요청
    const response = await api.post("/popup/load", {
      siteUrl: webpage.value.siteUrl,
      title: webpage.value.title,
    });
    if (response.data.status) {
      bookmarkOptions.value = response.data.results;
      console.log(
        "초기 로드 데이터 : ",
        JSON.stringify(bookmarkOptions, null, 2)
      );

      // bookmarkOptions가 업데이트된 후에 gptTags 업데이트
      gptTags.value = bookmarkOptions.value.keywords
        .slice(0, 3)
        .map((keyword, index) => ({
          tagName:
            index === 0 || index === 2
              ? keyword.replace(/[\[\]]/g, "")
              : keyword,
          ...generatePastelColors(),
        }));
    } else {
      console.error("에러 발생:", response.data.message);
    }
  } catch (error) {
    console.error("데이터 로딩 실패:", error);
  } finally {
    isLoading.value = false;
  }
});

// 북마크 저장 API 요청
const saveBookmark = async () => {
  errorMessage.value = "";
  isCollectionError.value = false;

  if (!selectedCollection.value && !newInputCollection.value) {
    errorMessage.value = "컬렉션을 선택하거나 새로운 컬렉션을 입력해주세요.";
    isCollectionError.value = true;
    return;
  }

  let bookmarkData;
  let apiUrl;

  if (selectedCollection.value) {
    bookmarkData = {
      url: webpage.value.siteUrl,
      collectionId: selectedCollection.value.collectionId,
      isPersonal: selectedCollection.value.isPersonal,
      tags: finalTags.value.map((finalTag) => ({
        tagName: finalTag.tagName,
        tagColor: finalTag.tagColor,
        tagBolder: finalTag.tagBorderColor,
      })),
      readingTime: webpage.value.readingTime,
    };
    apiUrl = "/bookmarks/extension";
  } else if (newInputCollection.value) {
    bookmarkData = {
      url: webpage.value.siteUrl,
      collectionName: newInputCollection.value,
      personal: newCollectionType.value,
      tags: finalTags.value.map((finalTag) => ({
        tagName: finalTag.tagName,
        tagColor: finalTag.tagColor,
        tagBolder: finalTag.tagBorderColor,
      })),
      readingTime: webpage.value.readingTime,
    };
    apiUrl = "/bookmarks/extension/new";
  } else {
    console.log("컬렉션을 선택하거나 새로운 컬렉션을 입력해주세요.");
    return;
  }

  // API 요청 전에 데이터 구조 확인
  console.log("전송될 데이터 : ", JSON.stringify(bookmarkData, null, 2));

  try {
    const response = await api.post(apiUrl, bookmarkData);
    if (response.data.status) {
      console.log("북마크 저장 성공!!!!");
      savedResult.value = response.data; // 전체 응답 데이터를 저장
      isSaved.value = true;
    } else {
      errorMessage.value = response.data.message;
      if (response.data.message.includes("이미 저장한 url입니다")) {
        isCollectionError.value = true; // select 강조 효과
      }
    }
  } catch (error) {
    console.error("API 요청 실패:", error);
    if (error.response?.data?.message?.includes("이미 저장한 url입니다")) {
      errorMessage.value = "이미 저장한 url입니다.";
      isCollectionError.value = true; // select 강조 효과
    } else {
      errorMessage.value = "저장 중 오류가 발생했습니다.";
    }
  }
};

// RSS 구독 API 요청
const scribeRSS = async () => {
  try {
    const response = await api.post("/subscriptions", webpage.value.siteUrl);
    if (response.data.status) {
      console.log("RSS구독 성공!!");
    }
  } catch (err) {}
};
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

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
  transform-origin: center;
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
