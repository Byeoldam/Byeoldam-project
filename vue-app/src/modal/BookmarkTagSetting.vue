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

// 이전 색상들을 저장할 배열
const previousHues = ref([]);
const MIN_HUE_DIFFERENCE = 30;

// HSL을 HEX로 변환하는 함수
const hslToHex = (h, s, l) => {
    s /= 100;
    l /= 100;
    const c = (1 - Math.abs(2 * l - 1)) * s;
    const x = c * (1 - Math.abs(((h / 60) % 2) - 1));
    const m = l - c / 2;
    let r = 0, g = 0, b = 0;

    if (0 <= h && h < 60) { r = c; g = x; b = 0; }
    else if (60 <= h && h < 120) { r = x; g = c; b = 0; }
    else if (120 <= h && h < 180) { r = 0; g = c; b = x; }
    else if (180 <= h && h < 240) { r = 0; g = x; b = c; }
    else if (240 <= h && h < 300) { r = x; g = 0; b = c; }
    else if (300 <= h && h < 360) { r = c; g = 0; b = x; }

    const toHex = (value) => {
        const hex = Math.round((value + m) * 255).toString(16);
        return hex.length === 1 ? "0" + hex : hex;
    };

    return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
};

// 새로운 색상이 이전 색상들과 충분히 다른지 확인
const isDistinctHue = (hue) => {
    return previousHues.value.every((prevHue) => {
        const diff = Math.abs(hue - prevHue);
        return Math.min(diff, 360 - diff) >= MIN_HUE_DIFFERENCE;
    });
};

// 구분되는 새로운 색상 생성
const getDistinctHue = () => {
    let attempts = 0;
    let hue;

    do {
        hue = Math.floor(Math.random() * 359);
        attempts++;
        if (attempts > 100) {
            previousHues.value = [];
            break;
        }
    } while (!isDistinctHue(hue));

    if (previousHues.value.length >= 10) {
        previousHues.value.shift();
    }
    previousHues.value.push(hue);

    return hue;
};

// 태그 색상 생성 함수
const generateRandomColor = () => {
    const hue = getDistinctHue();
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 50, lightness);
};

// 테두리 색상 생성 함수
const generateDarkerColor = (baseColor) => {
    // HSL 변환 로직을 사용하여 더 진한 색상 생성
    const hue = getDistinctHue();
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 20, lightness - 25);
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
const handleInput = () => {
    const tagText = newTag.value.trim();
    if (tagText) {
        addTag(tagText);
        newTag.value = ''; // input 내용 초기화
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