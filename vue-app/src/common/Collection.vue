<template>
  <div class="collection-card" @click="!showModal && $emit('click')">
    <div class="card-content">
      <div class="collection-header">
        <h3>{{ collection.name }}</h3>
        <div class="header-buttons">
          <button class="settings-button" @click.stop="$emit('action', 'edit', collection)">
            <i class="fas fa-cog"></i>
          </button>
          <button class="delete-button" @click.stop="$emit('action', 'delete', collection)">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
      <div class="collection-type">
        <i :class="['collection-icon', collection.isPersonal ? 'fas fa-user' : 'fas fa-users']"></i>
        {{ collection.isPersonal ? '개인' : '공유' }} 컬렉션
      </div>
    </div>

    <SharedCollectionSettings 
      v-if="showSharedSettings && !collection.isPersonal" 
      :current-name="collection.name"
      :collection-id="collection.collectionId
      "
      @close="showSharedSettings = false"
      @update="handleNameUpdate"
    />
    <PersonalCollectionSettings 
      v-if="showPersonalSettings && collection.isPersonal"
      :current-name="collection.name"
      :collection-id="collection.collectionId"
      @close="showPersonalSettings = false"
      @update="handleNameUpdate"
    />

    <!-- 삭제 확인 모달 추가 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="showDeleteConfirm = false">
      <div class="modal-content" @click.stop>
        <h3>컬렉션 삭제</h3>
        <p>정말로 이 컬렉션을 삭제하시겠습니까?</p>
        <div class="modal-buttons">
          <button class="cancel-btn" @click="showDeleteConfirm = false">취소</button>
          <button class="delete-btn" @click="confirmDelete">삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useCollectionStore } from '@/stores/collection';
import SharedCollectionSettings from '../component/SharedCollectionSettings.vue';
import PersonalCollectionSettings from '../component/PersonalCollectionSettings.vue';

const showSharedSettings = ref(false);
const showPersonalSettings = ref(false);
const showDeleteConfirm = ref(false);
const showModal = ref(false);

const props = defineProps({
  collection: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['click', 'action']);

const collectionStore = useCollectionStore();

const openSettings = () => {
  if (props.collection.isPersonal) {
    showPersonalSettings.value = true;
  } else {
    showSharedSettings.value = true;
  }
};

const handleNameUpdate = (newName) => {
  emit('update', {
    collectionId: props.collection.collectionId,
    newName: newName
  });
};

const handleDelete = () => {
  showDeleteConfirm.value = true;
};

const confirmDelete = async () => {
  try {
    if (props.collection.isPersonal) {
      await collectionStore.deletePersonalCollection(props.collection.collectionId);
    } else {
      await collectionStore.deleteSharedCollection(props.collection.collectionId);
    }
    
    await collectionStore.fetchAllCollection();
    emit('delete', props.collection.collectionId);
    showDeleteConfirm.value = false;
  } catch (error) {
    console.error('컬렉션 삭제 중 오류 발생:', error);
  }
};
</script>

<style scoped>
.collection-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(26, 35, 126, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #E9ECEF;
  min-height: 120px;
}

.card-content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  min-height: 50px;
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.collection-header h3 {
  margin: 0;
  font-size: 1.1rem;
}

.header-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.settings-button {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.settings-button:hover {
  background-color: #F8F9FA;
  color: #FFD700;
}

.delete-button {
  background: none;
  border: none;
  color: #ce3e3ebc;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.delete-button:hover {
  background-color: #ffebee;
}

.collection-type {
  font-size: 0.9rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 6px;
}

.collection-icon {
  color: #888;
}

.fa-user {
  color: #4F46E5;
}

.fa-users {
  color: #4F46E5;
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
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.delete-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background: #dc3545;
  color: white;
  cursor: pointer;
}
</style>
