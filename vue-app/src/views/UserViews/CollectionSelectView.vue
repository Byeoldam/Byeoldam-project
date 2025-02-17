<template>
    <div class="wrapper">
      <div class="page-container">
        <h2 class="headline">나만의 컬렉션 만들기</h2>
        <p class="paragraph">
          추천 컬렉션을 선택하거나, 나만의 개성있는 컬렉션명을 입력해보세요. <br />
          설정한 컬렉션은 자동으로 생성됩니다.
        </p>
    
        <div class="folders">
          <div
            v-for="(folder, index) in folders"
            :key="index"
            class="folder"
            :class="{ selected: selectedFolders.includes(folder.name) }"
            @click="toggleFolder(folder.name)"
          >
            <img src="@/assets/폴더이미지.png" alt="폴더" />
            <span>{{ folder.name }}</span>
          </div>
        </div>
    
        <input
          type="text"
          v-model="customCollection"
          class="collection-input"
          placeholder="생성할 컬렉션명을 입력하고 엔터를 누르세요."
          @keyup.enter="addCustomCollection"
        />
    
        <div class="selected-collections">
          <span v-for="(collection, index) in selectedFolders" :key="index" class="tag">
            {{ collection }}
            <button @click="removeCollection(collection)">x</button>
          </span>
        </div>
        <RouterLink :to="{name: 'tagSelect'}">
          <button class="next-button" @click="createCollections">다음</button>
        </RouterLink>
      </div>
    </div>  
  </template>
  
  <script setup>
  import { ref } from "vue";
import { RouterLink } from "vue-router";
import { useCollectionStore } from "@/stores/collection";

const collectionStore = useCollectionStore();

// 폴더 리스트
const folders = ref([
  { name: "즐겨찾기" },      // 가장 자주 방문하는 사이트
  { name: "읽을거리" },      // 뉴스, 블로그, 아티클
  { name: "학습" },         // 강의, 튜토리얼, 교육자료
  { name: "업무" },         // 업무 관련 사이트, 도구
  { name: "쇼핑" },         // 쇼핑몰, 상품 정보
  { name: "엔터테인먼트" },  // 영화, 음악, 게임
  { name: "여행" },         // 여행 정보, 맛집, 숙소
  { name: "금융" },         // 은행, 투자, 금융 정보
  { name: "건강" },         // 운동, 의료, 건강 정보
  { name: "레시피" },       // 요리, 음식 관련
]);

// 선택된 폴더들
const selectedFolders = ref([]);

// 직접 입력한 컬렉션
const customCollection = ref("");

// 폴더 선택 토글
const toggleFolder = (name) => {
  if (selectedFolders.value.includes(name)) {
    selectedFolders.value = selectedFolders.value.filter((item) => item !== name);
  } else {
    selectedFolders.value.push(name);
  }
};

// 직접 입력한 컬렉션 추가
const addCustomCollection = () => {
  if (customCollection.value && !selectedFolders.value.includes(customCollection.value)) {
    selectedFolders.value.push(customCollection.value);
    customCollection.value = "";
  }
};

// 컬렉션 제거
const removeCollection = (name) => {
  selectedFolders.value = selectedFolders.value.filter((item) => item !== name);
};

// 선택된 컬렉션들 생성하는 함수
const createCollections = async () => {
  try {
    // 선택된 모든 컬렉션에 대해 순차적으로 생성
    for (const collectionName of selectedFolders.value) {
      await collectionStore.createPersonalCollection(collectionName);
    }
    console.log("모든 컬렉션이 성공적으로 생성되었습니다.");
  } catch (error) {
    console.error("컬렉션 생성 중 오류 발생:", error);
  }
};
  </script>
  
  <style scoped>
/* 전체 컨테이너 가운데 배치*/
.wrapper {
  display: grid;
  place-items: center;
  height: 100vh;
}

  .page-container {
    width: 680px;
    height: auto;
    background: #ffffff;
    border: 1px solid #dde1e6;
    padding: 20px;
    text-align: center;
  }
  
  .headline {
    font-size: 24px;
    font-weight: bold;
    color: #21272a;
  }
  
  .paragraph {
    font-size: 14px;
    color: #666;
    margin-bottom: 20px;
  }
  
  .folders {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: center;
  }
  
  .folder {
    width: 100px;
    height: 120px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border: 2px solid transparent;
    border-radius: 8px;
  }
  
  .folder img {
    width: 64px;
    height: 64px;
  }
  
  .folder span {
    margin-top: 8px;
    font-size: 14px;
  }
  
  .folder.selected {
    border-color: #818CF8;
    background: #EEF2FF;
  }
  
  .collection-input {
    width: 80%;
    padding: 10px;
    margin-top: 20px;
    border: 1px solid #ccc;
    border-radius: 6px;
    text-align: center;
  }
  
  .selected-collections {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
    margin-top: 20px;
  }
  
  .tag {
    background: #6366F1;
    color: rgba(255, 255, 255, 0.901);
    padding: 6px 12px;
    border-radius: 12px;
    display: flex;
    align-items: center;
  }
  
  .tag button {
    background: none;
    border: none;
    color: white;
    font-weight: bold;
    margin-left: 8px;
    cursor: pointer;
  }
  
  .next-button {
    width: 550px;
    height: 50px;
    margin-top: 20px;
    background: #3730A3;
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 18px;
    cursor: pointer;
  }
  </style>
  