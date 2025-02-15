<template>
    <div class="bookmark-tag-modal">
        <h1 class="modal-title">태그 관리</h1>
        
        <div class="tag-list">
            <div v-for="tag in tags" :key="tag" class="tag-item">
                <span>{{ tag }}</span>
                <button @click="removeTag(tag)" class="delete-btn">×</button>
            </div>
        </div>

        <div class="tag-input-section">
            <input 
                v-model="newTag" 
                @keyup.enter="addTag"
                placeholder="새로운 태그를 입력하세요"
                class="tag-input"
            />
        </div>

        <button @click="saveChanges" class="save-btn">수정완료</button>
    </div>
</template> 

<script setup>
import { ref } from 'vue'
import { useBookmarkStore } from '@/stores/bookmark'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['close'])
const bookmarkStore = useBookmarkStore()

const props = defineProps({
  bookmarkId: {
    type: String,
    required: true
  },
  initialTags: {
    type: Array,
    required: true,
    default: () => []
  }
})

const tags = ref([...props.initialTags])
const newTag = ref('')

const addTag = () => {
    if (newTag.value.trim() && !tags.value.includes(newTag.value.trim())) {
        tags.value.push(newTag.value.trim())
        newTag.value = ''
    }
}

const removeTag = (tagToRemove) => {
    tags.value = tags.value.filter(tag => tag !== tagToRemove)
}

const saveChanges = async () => {
  try {
    await bookmarkStore.manageTags(props.bookmarkId, tags.value)
    ElMessage.success('태그가 성공적으로 수정되었습니다')
    emit('close')
  } catch (error) {
    console.error('태그 수정 중 오류 발생:', error)
    ElMessage.error('태그 수정에 실패했습니다')
  }
}
</script>

<style scoped>
.bookmark-tag-modal {
    padding: 20px;
    max-width: 500px;
    margin: 0 auto;
}

.modal-title {
    text-align: center;
    margin-bottom: 20px;
    font-size: 24px;
    font-weight: bold;
}

.tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 20px;
}

.tag-item {
    background-color: #f0f0f0;
    padding: 5px 10px;
    border-radius: 20px;
    display: flex;
    align-items: center;
}

.delete-btn {
    margin-left: 5px;
    border: none;
    background: none;
    cursor: pointer;
    color: #666;
}

.tag-input-section {
    margin-bottom: 20px;
}

.tag-input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.save-btn {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.save-btn:hover {
    background-color: #0056b3;
}
</style>