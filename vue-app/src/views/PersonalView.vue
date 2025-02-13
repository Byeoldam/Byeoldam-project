<template>
    <div class="layout">
        <Header class="header" />
        <div class="content-wrapper">
            <SideBar class="sidebar" />
            <div class="main-content">
                <div class="body">
                    <div class="page-header">
                        <div class="header-content">
                            <div class="title-section">
                                <i class="fas fa-user title-icon"></i>
                                <h2 class="title">개인 컬렉션</h2>
                            </div>
                            <p class="description">나만의 북마크를 체계적으로 관리하고 정리할 수 있는 공간입니다</p>
                        </div>
                    </div>
                    <div class="top-section">
                        <div class="filter-buttons">
                            <button 
                                :class="['filter-btn', selectedCollection === 'all' ? 'active' : '']"
                                @click="selectedCollection = 'all'"
                            >
                                전체보기
                            </button>
                            <button 
                                v-for="collection in collections"
                                :key="collection.collection_id"
                                :class="['filter-btn', selectedCollection === collection.name ? 'active' : '']"
                                @click="selectedCollection = collection.name"
                            >
                                {{ collection.name }}
                            </button>
                        </div>
                        <button class="new-collection-btn" @click="createNewCollection">
                            <span class="plus-icon">+</span>
                            새 컬렉션
                        </button>
                    </div>
                    <div class="collections-wrapper">
                        <template v-if="selectedCollection === 'all'">
                            <PersonalCollectionList
                                v-for="collection in collections" 
                                :key="collection.collection_id" 
                                :collectionInfo="collection"
                                class="collection-item"
                            />
                        </template>
                        <template v-else>
                            <PersonalCollectionList
                                v-for="collection in filteredCollections" 
                                :key="collection.collection_id" 
                                :collectionInfo="collection"
                                class="collection-item"
                            />
                        </template>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="showCreateModal" class="modal-overlay">
            <div class="modal-content">
                <CreateCollection 
                    @close="showCreateModal = false"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import Header from '@/common/Header.vue'
import SideBar from '@/common/SideBar.vue'
import { computed, ref, onMounted } from 'vue';
import PersonalCollectionList from '@/component/PersonalCollectionList.vue';
import CreateCollection from '@/modal/CreateCollection.vue';
import { useCollectionStore } from '@/stores/collection';
import { storeToRefs } from 'pinia';

const collectionStore = useCollectionStore();
const { personalCollections } = storeToRefs(collectionStore);
const selectedCollection = ref('all');
const showCreateModal = ref(false);

const collections = computed(() => {
    return personalCollections.value || [];
});

const filteredCollections = computed(() => {
    return collections.value.filter(collection => 
        collection.name === selectedCollection.value
    );
});

const createNewCollection = () => {
    showCreateModal.value = true;
};

onMounted(async () => {
    try {
        await collectionStore.fetchAllCollection();
    } catch (error) {
        console.error('컬렉션 데이터 로딩 실패:', error);
    }
});
</script>

<style scoped>
.layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    background: white;
}

.content-wrapper {
    display: flex;
    margin-top: 60px; /* 헤더 높이만큼 여백 추가 */
    height: calc(100vh - 60px); /* 전체 높이에서 헤더 높이를 뺀 만큼 설정 */
}

.sidebar {
    position: fixed;
    left: 0;
    top: 60px; /* 헤더 높이만큼 떨어뜨림 */
    bottom: 0;
    width: 240px; /* 사이드바 너비 */
    background: white;
    z-index: 99;
}

.main-content {
    flex: 1;
    margin-left: 240px; /* 사이드바 너비만큼 여백 */
    overflow-y: auto; /* 본문 내용만 스크롤 가능하도록 */
    height: 100%;
}

.body {
    padding: 20px;
}

.collections-wrapper {
    display: flex;
    flex-direction: column;
    gap: 60px;
}

.collection-item {
    width: 100%;
}

.top-section {
    position: sticky;
    top: 0;
    background: white;
    padding: 10px 0;
    z-index: 98;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
}

.filter-buttons {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    flex: 1;
}

.filter-btn {
    padding: 8px 16px;
    border: 1px solid #ddd;
    border-radius: 20px;
    background: white;
    cursor: pointer;
    transition: all 0.3s ease;
}

.filter-btn:hover {
    background: #f5f5f5;
}

.filter-btn.active {
    background: #007bff;
    color: white;
    border-color: #007bff;
}

.new-collection-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border: none;
    border-radius: 20px;
    background: #007bff;
    color: white;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 500;
}

.new-collection-btn:hover {
    background: #0056b3;
}

.plus-icon {
    font-size: 1.2em;
    font-weight: bold;
}

/* 모달 관련 스타일 추가 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    /* padding: 20px; */
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
    max-height: 80vh;
    overflow-y: auto;
}

.page-header {
    background: linear-gradient(to right, #f8f9fa, #ffffff);
    padding: 16px 24px;
    border-radius: 12px;
    margin-bottom: 24px;
}

.header-content {
    max-width: 800px;
}

.title-section {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.title-icon {
    font-size: 1.5rem;
    color: #007bff;
}

.title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
}

.description {
    font-size: 0.95rem;
    color: #666;
    margin: 0;
    line-height: 1.4;
}

</style>