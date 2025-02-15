<template>
  <div class="bookmark-copy-modal">
    <h3 class="modal-title">북마크 공유 컬렉션으로 복사</h3>
    
    <div class="modal-content">
      <p class="description">
        선택한 북마크를 공유 컬렉션으로 복사합니다.
        복사된 북마크는 선택한 공유 컬렉션에서 확인하실 수 있습니다.
      </p>

      <div class="select-container">
        <label for="collection-select">공유 컬렉션 선택:</label>
        <select 
          id="collection-select" 
          v-model="selectedCollection"
          class="collection-select"
        >
          <option value="" disabled>컬렉션을 선택해주세요</option>
          <option 
            v-for="collection in sharedCollections.results" 
            :key="collection.collectionId"
            :value="collection.collectionId"
          >
            {{ collection.name }}
          </option>
        </select>
      </div>

      <div class="button-group">
        <button 
          class="copy-button"
          :disabled="!selectedCollection"
          @click="handleCopy"
        >
          복사하기
        </button>
        <button class="cancel-button" @click="handleClose">
          취소
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCollectionStore } from '@/stores/collection'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { useBookmarkStore } from '@/stores/bookmark'

const props = defineProps({
  bookmarkId: {
    type: Number,
    required: true
  },
  isPersonal: {
    type: Boolean,
    required: true
  },
})

const emit = defineEmits(['close'])
const collectionStore = useCollectionStore()
const { sharedCollections } = storeToRefs(collectionStore)
const selectedCollection = ref('')
const bookmarkStore = useBookmarkStore()

const handleCopy = async () => {
  try {
    if (!selectedCollection.value) {
      ElMessage.warning('컬렉션을 선택해주세요')
      return
    }
    
    await bookmarkStore.moveToOtherCollection(
      props.bookmarkId,
      false,
      selectedCollection.value
    )
    
    emit('close')
    window.location.reload()
  } catch (error) {
    console.error('북마크 복사 중 오류 발생:', error)
    ElMessage.error('북마크 복사에 실패했습니다')
  }
}

const handleClose = () => {
  emit('close')
}

onMounted(async () => {
  try {
    // 공유 컬렉션 목록 불러오기
    await collectionStore.fetchSharedCollection()
  } catch (error) {
    console.error('공유 컬렉션 로드 실패:', error)
    ElMessage.error('공유 컬렉션 목록을 불러오는데 실패했습니다')
  }
})
</script>

<style scoped>
.bookmark-copy-modal {
  padding: 20px;
}

.modal-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
}

.description {
  margin-bottom: 20px;
  color: #666;
}

.select-container {
  margin-bottom: 20px;
}

.collection-select {
  width: 100%;
  padding: 8px;
  margin-top: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.button-group {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

button {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.copy-button {
  background-color: #4CAF50;
  color: white;
  border: none;
}

.copy-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.cancel-button {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
}
</style>