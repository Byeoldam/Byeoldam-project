<template>
    <div class="layout shared-view">
        <Header class="header" />
        <div class="content-wrapper">
            <SideBar class="sidebar" />
            <div class="main-content">
                <div class="body">
                    <div class="page-header">
                        <div class="header-content">
                            <div class="title-section">
                                <i class="fas fa-users title-icon"></i>
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
                                @dblclick="handleCollectionNameEdit(collection)"
                            >
                                <span v-if="editingCollectionId !== collection.collectionId">
                                    {{ collection.name }}
                                </span>
                                <input
                                    v-else
                                    v-model="editingCollectionName"
                                    @blur="handleCollectionNameSave"
                                    @keyup.enter="handleCollectionNameSave"
                                    ref="collectionNameInput"
                                    class="collection-name-input"
                                />
                            </button>
                        </div>
                        <div class="right-section">
                            <CollectionMembers 
                                v-if="selectedCollectionId && collectionMembers.length > 0"
                                :members="collectionMembers"
                            />
                            <button 
                                v-if="selectedCollectionId"
                                class="settings-button"
                                @click="openSettings"
                            >
                                <i class="fas fa-cog"></i>
                            </button>
                            <button class="new-collection-btn" @click="createNewCollection">
                                <span class="plus-icon">+</span>
                                새 컬렉션
                            </button>
                        </div>
                    </div>
                    <div class="content-section">
                        <div class="cards-section">
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
                                    :readingTime="bookmark.readingTime"
                                />
                            </div>
                        </div>
                    </div>
                    <div v-if="!selectedCollectionId" class="initial-message">
                        <div class="message-box">
                            <i class="fas fa-share-alt search-icon"></i>
                            <h3>공유된 북마크가 없습니다.</h3>
                            <p>다른 사용자와 북마크를 공유해보세요</p>
                        </div>
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

        <!-- 멤버 관리 모달 -->
        <el-dialog
            v-model="showMemberModal"
            title="멤버 관리"
            width="50%"
        >
            <MemberManageModal 
                :members="collectionMembers"
                :collectionId="selectedCollectionId"
                @close="showMemberModal = false"
                @refresh="loadCollectionMembers(selectedCollectionId)"
            />
        </el-dialog>
    </div>
</template>

<script setup>
import Header from '@/common/Header.vue'
import SideBar from '@/common/SideBar.vue'
import { computed, ref, onMounted, nextTick } from 'vue';
import CreateCollection from '@/modal/CreateCollection.vue';
import { useCollectionStore } from '@/stores/collection';
import { useBookmarkStore } from '@/stores/bookmark';
import { storeToRefs } from 'pinia';
import Card from '@/common/Card.vue';
import CollectionMembers from '@/component/CollectionMembers.vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import MemberManageModal from '@/modal/MemberManageModal.vue';
import { ElMessage } from 'element-plus';

const collectionStore = useCollectionStore();
const bookmarkStore = useBookmarkStore();
const userStore = useUserStore();
const { getUserId } = storeToRefs(userStore);
const { sharedCollectionBookmarks } = storeToRefs(bookmarkStore);
const selectedCollectionId = ref(null);
const selectedCollectionName = ref('');
const selectedCollectionBookmarks = computed(() => 
    sharedCollectionBookmarks.value?.results?.bookmarks || []
);
const showCreateModal = ref(false);
const collectionMembers = ref([]);
const showMemberModal = ref(false);

const collections = ref([]);

const route = useRoute();

const editingCollectionId = ref(null);
const editingCollectionName = ref('');
const collectionNameInput = ref(null);

const handleCollectionClick = async (collectionId, collectionName) => {
    selectedCollectionId.value = collectionId;
    selectedCollectionName.value = collectionName;
    
    try {
        await Promise.all([
            bookmarkStore.getSharedCollectionBookmarks(collectionId),
            loadCollectionMembers(collectionId)
        ]);
    } catch (error) {
        console.error('데이터 로딩 실패:', error);
    }
};

const loadCollectionMembers = async (collectionId) => {
    try {
        const response = await collectionStore.getMembersByCollectionId(collectionId);
        console.log('Member Response:', response);
        collectionMembers.value = response.results || [];
    } catch (error) {
        console.error('멤버 정보 로딩 실패:', error);
        collectionMembers.value = [];
    }
};

onMounted(async () => {
    try {
        // 1. 컬렉션 목록 가져오기
        const response = await collectionStore.fetchSharedCollection();
        collections.value = response.results || [];
        
        // 2. URL에서 collectionId 확인
        const collectionId = parseInt(route.params.collectionId);
        
        if (collectionId) {
            // URL에 collectionId가 있는 경우 해당 컬렉션의 북마크 조회
            const targetCollection = collections.value.find(c => c.collectionId === collectionId);
            if (targetCollection) {
                selectedCollectionId.value = collectionId;
                selectedCollectionName.value = targetCollection.name;
                await Promise.all([
                    bookmarkStore.getSharedCollectionBookmarks(collectionId),
                    loadCollectionMembers(collectionId)
                ]);
            }
        } else if (collections.value.length > 0) {
            // URL에 collectionId가 없는 경우 첫 번째 컬렉션의 북마크 조회
            const firstCollection = collections.value[0];
            selectedCollectionId.value = firstCollection.collectionId;
            selectedCollectionName.value = firstCollection.name;
            await Promise.all([
                bookmarkStore.getSharedCollectionBookmarks(firstCollection.collectionId),
                loadCollectionMembers(firstCollection.collectionId)
            ]);
        }
    } catch (error) {
        console.error('데이터 로딩 실패:', error);
    }
});

const createNewCollection = () => {
    showCreateModal.value = true;
};

const openSettings = () => {
    showMemberModal.value = true;
};

const handleCollectionNameEdit = (collection) => {
    editingCollectionId.value = collection.collectionId;
    editingCollectionName.value = collection.name;
    // 다음 틱에 input에 포커스
    nextTick(() => {
        if (collectionNameInput.value) {
            collectionNameInput.value.focus();
        }
    });
};

const handleCollectionNameSave = async () => {
    if (editingCollectionId.value && editingCollectionName.value.trim()) {
        try {
            await collectionStore.updateSharedCollectionName(
                editingCollectionId.value,
                editingCollectionName.value.trim()
            );
            ElMessage({
                message: '컬렉션 이름이 수정되었습니다.',
                type: 'success',
                duration: 3000
            });
        } catch (error) {
            // 에러 메시지를 화면에 표시
            ElMessage({
                message: error.message,
                type: 'error',
                duration: 3000
            });
            // 수정 중이던 이름을 원래 이름으로 되돌림
            editingCollectionName.value = collections.value.find(
                c => c.collectionId === editingCollectionId.value
            )?.name || '';
        }
    }
    editingCollectionId.value = null;
};
</script>

<style scoped>
.layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #F5F5F5;
}

.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 100;
    /* background: white; */
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
    /* background: white; */
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
    background: #F5F5F5;
    padding: 10px 20px;
    z-index: 98;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.filter-buttons {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    flex: 1;
    /* padding: 0 20px; */
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
    background: #4338CA;
    color: rgba(255, 255, 255, 0.901);
    border-color: #4338CA;
}

.new-collection-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border: none;
    border-radius: 20px;
    background: #3730A3;
    color: rgba(255, 255, 255, 0.901);
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 500;
}

.new-collection-btn:hover {
    background: #3830a3c5;
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
    /* background: linear-gradient(to right, #f8f9fa, #ffffff); */
    padding: 16px 24px;
    border-radius: 12px;
    margin-bottom: 12px;
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
    color: #3730A3;
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

.initial-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    border-radius: 12px;
    text-align: center;
}

.message-box {
    text-align: center;
    padding: 20px;
    border-radius: 12px;
    max-width: 500px;
}

.search-icon {
    font-size: 48px;
    margin-bottom: 20px;
    color: #ddd;
    display: block;
}

.message-box h3 {
    color: #2d3748;
    font-size: 24px;
    margin-bottom: 12px;
}

.message-box p {
    color: #718096;
    font-size: 16px;
    line-height: 1.5;
}

.content-section {
    display: flex;
    margin-top: 20px;
}

.cards-section {
    flex: 1;
    min-width: 0;
}

.right-section {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-right: 20px;
}

.settings-button {
    padding: 8px;
    border: none;
    border-radius: 50%;
    background: #f8f9fa;
    color: #666;
    cursor: pointer;
    transition: all 0.3s ease;
}

.settings-button:hover {
    background: #e9ecef;
    color: #3730A3;
}

.settings-button i {
    font-size: 1.2rem;
}

.collection-name-input {
    background: transparent;
    border: none;
    border-bottom: 1px solid #3730A3;
    color: inherit;
    font-size: inherit;
    padding: 2px 4px;
    width: 100%;
    outline: none;
}

.collection-name-input:focus {
    border-bottom-color: #2d2682;
}
</style>