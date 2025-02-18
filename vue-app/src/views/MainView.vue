<template>
    <div class="layout">
        <Header class="header"/>
        <div class="content-wrapper">
            <SideBar class="sidebar"/>
            <div class="main-content">
                <div class="body">
                    <div class="page-header">
                        <div class="header-content">
                            <div class="title-section">
                                <i class="fas fa-home title-icon"></i>
                                <h2 class="title">모든 컬렉션</h2>
                            </div>
                            <p class="description">모든 개인 및 공유 컬렉션을 한눈에 확인할 수 있습니다</p>
                        </div>
                    </div>
                    <div class="collection-controls">
                        <button class="new-collection-btn" @click="createNewCollection">
                            <span class="plus-icon">+</span>
                            새 컬렉션
                        </button>
                    </div>
                    <div v-if="isLoading" class="loading-spinner">
                        <div class="spinner"></div>
                        <p>로딩 중...</p>
                    </div>
                    
                    <div v-else-if="!allCollections?.length" class="empty-state">
                        <i class="fas fa-folder-open empty-icon"></i>
                        <p class="empty-text">컬렉션이 존재하지 않습니다.</p>
                        <p class="empty-description">
                            상단의 '새 컬렉션' 버튼을 클릭하여<br>
                            새로운 컬렉션을 만들어보세요!
                        </p>
                    </div>
                    
                    <div v-else class="collections-grid">
                        <Collection 
                            v-for="collection in allCollections"
                            :key="collection.collectionId"
                            :collection="collection"
                            @action="handleCollectionAction"
                            @click="handleCollectionClick"
                        />
                    </div>
                </div>
            </div>
        </div>

        <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <CollectionEdit
                    v-if="modalType === 'edit'"
                    :collection-id="selectedCollection?.collectionId"
                    :current-name="selectedCollection?.name"
                    :is-personal="selectedCollection?.isPersonal"
                    @close="closeModal"
                    @confirm="handleEditConfirm"
                />
                <CollectionDel
                    v-if="modalType === 'delete'"
                    :collection-id="selectedCollection?.collectionId"
                    :is-personal="selectedCollection?.isPersonal"
                    @close="closeModal"
                    @confirm="handleDeleteConfirm"
                />
                <CreateCollection
                    v-if="modalType === 'create'"
                    @close="closeModal"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia';
import Header from '@/common/Header.vue';
import SideBar from '@/common/SideBar.vue';
import Collection from '@/common/Collection.vue';
import CreateCollection from '@/modal/CreateCollection.vue';
import CollectionEdit from '@/modal/CollectionEdit.vue';
import CollectionDel from '@/modal/CollectionDel.vue';
import { useCollectionStore } from '@/stores/collection';
import { ElMessage } from 'element-plus';

const router = useRouter();
const collectionStore = useCollectionStore();
const { allCollections } = storeToRefs(collectionStore);

// 모달 관련 상태
const showModal = ref(false);
const modalType = ref(null);
const selectedCollection = ref(null);
const isLoading = ref(true);

const createNewCollection = () => {
    modalType.value = 'create';
    showModal.value = true;
};

const closeModal = () => {
    showModal.value = false;
    modalType.value = null;
    selectedCollection.value = null;
};

const handleEditConfirm = async () => {
    await collectionStore.fetchAllCollections();
    closeModal();
};

const handleDeleteConfirm = async () => {
    await collectionStore.fetchAllCollections();
    closeModal();
};

const handleCollectionAction = (action, collection) => {
    selectedCollection.value = collection;
    modalType.value = action;
    showModal.value = true;
};

const handleCollectionClick = (collectionData) => {
    const { collectionId, isPersonal } = collectionData;
    const path = isPersonal 
        ? `/collections/personal/${collectionId}` 
        : `/collections/shared/${collectionId}`;
    router.push(path);
};

const handleLogout = async () => {
    try {
        await userStore.logout();
        ElMessage.success('로그아웃되었습니다.');
        setTimeout(() => {
            router.push({ name: 'intro' });
        }, 2000);
    } catch (error) {
        ElMessage.error('로그아웃 중 오류가 발생했습니다.');
    }
};

onMounted(async () => {
    try {
        isLoading.value = true;
        await collectionStore.fetchAllCollections();
    } catch (error) {
        console.error('컬렉션 로딩 실패:', error);
    } finally {
        isLoading.value = false;
    }
});
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
    margin-top: 60px;
    height: calc(100vh - 60px);
}

.sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    bottom: 0;
    width: 240px;
    /* background: white; */
    z-index: 99;
}

.main-content {
    flex: 1;
    margin-left: 240px;
    overflow-y: auto;
    height: 100%;
}

.body {
    padding: 20px;
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

.collection-controls {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    padding: 0 20px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    /* background-color: #f8f9fa; */
    border-radius: 12px;
    text-align: center;
    margin-top: 20px;
}

.empty-icon {
    font-size: 3rem;
    color: #ddd;
    margin-bottom: 16px;
}

.empty-text {
    font-size: 1.2rem;
    color: #666;
    margin-bottom: 8px;
}

.empty-description {
    font-size: 0.95rem;
    color: #888;
    line-height: 1.5;
}

.collections-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 24px;
    padding: 20px 20px;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    max-width: 90%;
    max-height: 90%;
    overflow-y: auto;
    position: relative;
    width: auto;
    margin: 1.75rem auto;
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
    gap: 10px;
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

.loading-spinner {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    padding-top: 200px;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #3730A3;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}
</style>