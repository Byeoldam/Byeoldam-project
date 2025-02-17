<template>
  <div class="bookmark-move-modal">
    <h2 class="modal-title">북마크 이동</h2>
    
    <div class="select-container">
      <label for="collection-select">개인 컬렉션 선택:</label>
      <select 
        id="collection-select" 
        v-model="selectedCollectionId"
        class="collection-select"
      >
        <option value="" selected>컬렉션을 선택해주세요</option>
        <option 
          v-for="collection in personalCollections.results" 
          :key="collection.collectionId"
          :value="collection.collectionId"
        >
          {{ collection.name }}
        </option>
      </select>
    </div>

    <div class="modal-actions">
      <button 
        @click="moveBookmark" 
        :disabled="!selectedCollectionId"
        class="btn-move"
      >
        이동
      </button>
      <button @click="$emit('close')" class="btn-cancel">취소</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCollectionStore } from '@/stores/collection'
import { useBookmarkStore } from '@/stores/bookmark'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'

const props = defineProps({
  bookmarkId: {
    type: Number,
    required: true
  },
  isPersonal: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['close', 'moveComplete'])
const collectionStore = useCollectionStore()
const bookmarkStore = useBookmarkStore()
const { personalCollections } = storeToRefs(collectionStore)

const selectedCollectionId = ref('')

onMounted(async () => {
  try {
    // 개인 컬렉션 목록 불러오기 (함수 이름 수정)
    await collectionStore.fetchPersonalCollection()
  } catch (error) {
    console.error('개인 컬렉션 로드 실패:', error)
    ElMessage.error('개인 컬렉션 목록을 불러오는데 실패했습니다')
  }
})

const moveBookmark = async () => {
  try {
    const response = await bookmarkStore.moveToOtherCollection(
      props.bookmarkId,
      props.isPersonal,
      selectedCollectionId.value
    )
    
    if (response.data.status) {
      ElMessage.success('북마크가 이동되었습니다')
      emit('moveComplete')  // 이동 완료 이벤트 emit
      emit('close')
    } else {
      ElMessage.error('북마크 이동에 실패했습니다')
    }
  } catch (error) {
    console.error('북마크 이동 중 오류 발생:', error)
    ElMessage.error('북마크 이동에 실패했습니다')
  }
}
</script>

<style scoped>
.bookmark-move-modal {
  padding: 20px;
}

.modal-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
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

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel, .btn-move {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
}

.btn-move {
  background-color: #6366F1;
  color: rgba(255, 255, 255, 0.901);
  border: none;
}

.btn-move:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>