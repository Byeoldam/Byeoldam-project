<template>
    <div class="bookmark-tag-modal">
        <h1 class="modal-title">태그 관리</h1>
        
        <div class="tag-list">
            <div v-for="tag in tags" :key="tag.tagName" class="tag-item">
                <span 
                    class="tag"
                    :style="{
                        backgroundColor: tag.tagColor,
                        borderColor: tag.tagBolder
                    }"
                    @click="handleTagClick(tag)"
                >
                    #{{ tag.tagName }}
                </span>
                <button @click="removeTag(tag)" class="delete-btn">×</button>
            </div>
        </div>

        <div class="tag-input-section">
            <input 
                v-model="newTag" 
                @keyup.enter="handleInput"
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

const emit = defineEmits(['close', 'tagSelect'])
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

// 랜덤 RGB 색상 생성 함수
const generateRandomColor = () => {
    const r = Math.floor(Math.random() * 256);
    const g = Math.floor(Math.random() * 256);
    const b = Math.floor(Math.random() * 256);
    return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`;
};

// 더 진한 색상 생성 (border용)
const generateDarkerColor = (color) => {
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);
    
    const darkerR = Math.max(0, r - 30);
    const darkerG = Math.max(0, g - 30);
    const darkerB = Math.max(0, b - 30);
    
    return `#${darkerR.toString(16).padStart(2, '0')}${darkerG.toString(16).padStart(2, '0')}${darkerB.toString(16).padStart(2, '0')}`;
};

// 태그 추가 함수
const addTag = (tagName) => {
    const tagColor = generateRandomColor();
    const tagBolder = generateDarkerColor(tagColor);
    
    const newTag = {
        tagName,
        tagColor,
        tagBolder
    };
    
    tags.value.push(newTag);
};

// 태그 삭제 함수
const removeTag = (tag) => {
    const index = tags.value.findIndex(t => t.tagName === tag.tagName);
    if (index !== -1) {
        tags.value.splice(index, 1);
    }
};

// 태그 클릭 핸들러
const handleTagClick = (tag) => {
    emit('tagSelect', tag);
};

// 입력된 텍스트로 태그 생성
const handleInput = (event) => {
    const text = event.target.value.trim();
    if (text && event.key === 'Enter') {
        addTag(text);
        event.target.value = ''; // 입력 필드 초기화
    }
};

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
    gap: 8px;
    margin-top: 8px;
}

.tag-item {
    display: flex;
    align-items: center;
    gap: 4px;
}

.tag {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 0.85rem;
    color: #333;
    border: 1px solid;
    white-space: nowrap;
}

.delete-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 1.2rem;
    color: #666;
    padding: 0 4px;
}

.delete-btn:hover {
    color: #ff4444;
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