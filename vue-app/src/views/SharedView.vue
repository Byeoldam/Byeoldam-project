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
                                <h2 class="title">공유 컬렉션</h2>
                            </div>
                            <p class="description">나만의 북마크를 체계적으로 관리하고 정리할 수 있는 공간입니다</p>
                        </div>
                    </div>
                    <div class="top-section">
                        <div class="filter-buttons">
                            <button 
                                v-for="collection in collections"
                                :key="collection.collectionId"
                                :class="['filter-btn', selectedCollectionId === collection.collectionId ? 'active' : '']"
                                @click="handleCollectionClick(collection.collectionId, collection.name)"
                            >
                                {{ collection.name }}
                            </button>
                        </div>
                        <button class="new-collection-btn" @click="createNewCollection">
                            <span class="plus-icon">+</span>
                            새 컬렉션
                        </button>
                    </div>
                    <div v-if="selectedCollectionId && selectedCollectionBookmarks.length === 0" class="empty-state">
                        <p class="empty-message">이 컬렉션에는 아직 북마크가 없습니다.</p>
                        <p class="empty-sub-message">새로운 북마크를 추가해보세요!</p>
                    </div>
                    <div v-else-if="selectedCollectionBookmarks.length > 0" class="cards-grid">
                        <Card
                            v-for="bookmark in selectedCollectionBookmarks"
                            :key="bookmark.bookmarkId"
                            :bookmarkId="bookmark.bookmarkId"
                            :url="bookmark.url"
                            :img="bookmark.img"
                            :title="bookmark.title"
                            :description="bookmark.description"
                            :tag="bookmark.tags"
                            :priority="bookmark.priority"
                            :createdAt="bookmark.createdAt"
                            :updatedAt="bookmark.updatedAt"
                            :isPersonal="false"
                        />
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
import CreateCollection from '@/modal/CreateCollection.vue';
import { useCollectionStore } from '@/stores/collection';
import { useBookmarkStore } from '@/stores/bookmark';
import { storeToRefs } from 'pinia';
import Card from '@/common/Card.vue';

const collectionStore = useCollectionStore();
const bookmarkStore = useBookmarkStore();
const { sharedCollectionBookmarks } = storeToRefs(bookmarkStore);
const selectedCollectionId = ref(null);
const selectedCollectionName = ref('');
const selectedCollectionBookmarks = computed(() => 
    sharedCollectionBookmarks.value?.results?.bookmarks || []
);
const showCreateModal = ref(false);

const collections = ref([]);


const handleCollectionClick = async (collectionId, collectionName) => {
    console.log('Selected Collection ID:', collectionId, 'hakjun0412');
    selectedCollectionId.value = collectionId;
    selectedCollectionName.value = collectionName;
    
    try {
        await bookmarkStore.getSharedCollectionBookmarks(collectionId);
    } catch (error) {
        console.error('북마크 로딩 실패:', error, 'hakjun0412');
    }
};

onMounted(async () => {
    try {
        const response = await collectionStore.fetchSharedCollection();
        console.log('Collections Response:', response, 'hakjun0412');
        collections.value = response.results || [];
        
        // 첫 번째 컬렉션 자동 선택 (옵션)
        if (collections.value.length > 0) {
            const firstCollection = collections.value[0];
            handleCollectionClick(firstCollection.collectionId, firstCollection.name);
        }
    } catch (error) {
        console.error('컬렉션 로딩 실패:', error, 'hakjun0412');
        collections.value = [];
    }
});

const createNewCollection = () => {
    showCreateModal.value = true;
};
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

.cards-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
    padding: 20px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
}

.empty-message {
    font-size: 1.2rem;
    color: #666;
    margin-bottom: 8px;
}

.empty-sub-message {
    font-size: 1rem;
    color: #888;
}
</style>