<template>
    <div class="modal-overlay">
        <div class="bookmark-save-modal">
            <h3 class="modal-title">북마크 저장하기</h3>
            
            <div class="modal-content">
                <p class="description">
                    북마크를 저장할 개인 컬렉션을 선택하고 태그를 추가해주세요.
                </p>

                <div class="select-container">
                    <label for="collection-select">개인 컬렉션 선택:</label>
                    <select 
                        id="collection-select"
                        v-model="selectedCollection" 
                        class="collection-select"
                    >
                        <option value="" disabled>컬렉션을 선택하세요</option>
                        <option 
                            v-for="collection in collections" 
                            :key="collection.collection_id" 
                            :value="collection.collection_id"
                        >
                            {{ collection.collection_name }}
                        </option>
                    </select>
                </div>

                <div class="tags-section">
                    <label>태그 추가</label>
                    <div class="selected-tags">
                        <div v-for="tag in selectedTags" 
                             :key="tag.name"
                             class="tag-item">
                            <span 
                                class="tag"
                                :style="{
                                    backgroundColor: tag.color,
                                    borderColor: tag.bolder
                                }"
                            >
                                #{{ tag.name }}
                            </span>
                            <button @click="removeTag(tag.name)" class="delete-btn">×</button>
                        </div>
                    </div>
                    <div class="tag-input-container">
                        <input 
                            v-model="newTag"
                            type="text"
                            class="tag-input"
                            placeholder="태그를 입력후 엔터를 눌러주세요"
                            @keyup.enter="addTag"
                        >
                    </div>
                </div>

                <div class="button-group">
                    <button class="save-button" @click="handleSave">저장</button>
                    <button class="cancel-button" @click="closeModal">취소</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useCollectionStore } from '@/stores/collection';
import { useBookmarkStore } from '@/stores/bookmark';
import { ElMessage } from 'element-plus';

const props = defineProps({
    url: {
        type: String,
        required: true
    },
    title: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    img: {
        type: String,
        required: true
    },
    readingTime: {
        type: Number,
        required: true
    }
});

const emit = defineEmits(['close', 'save']);
const collectionStore = useCollectionStore();
const bookmarkStore = useBookmarkStore();

// 컴포넌트가 마운트될 때 컬렉션 데이터를 가져옵니다
await collectionStore.fetchAllCollections();

const selectedCollection = ref('');
const collections = computed(() => {
    console.log('collections raw:', collectionStore.allCollections);
    return collectionStore.allCollections.map(collection => ({
        collection_id: collection.collection_id || collection.collectionId,
        collection_name: collection.collection_name || collection.name,
        isPersonal: collection.isPersonal
    }));
});

const selectedTags = ref([]);
const newTag = ref('');

// HSL을 HEX로 변환하는 함수 추가
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

// 새로운 색상 생성 함수
const generateRandomColor = () => {
    const hue = Math.floor(Math.random() * 359);
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 50, lightness);
};

// addTag 함수 수정
const addTag = () => {
    if (newTag.value.trim() && !selectedTags.value.includes(newTag.value.trim())) {
        const tagColor = generateRandomColor();
        selectedTags.value.push({
            name: newTag.value.trim(),
            color: tagColor
        });
        newTag.value = '';
    }
};

const removeTag = (tag) => {
    selectedTags.value = selectedTags.value.filter(t => t.name !== tag);
};

const closeModal = () => {
    emit('close');
};

const handleSave = async () => {
    try {
        if (!selectedCollection.value) {
            alert('컬렉션을 선택해주세요.');
            return;
        }

        // 태그 색상 배열 추가
        const tagColors = [
            '#e3f2fd', // 파란색 계열
            '#fff3e0', // 주황색 계열
            '#fce4ec', // 분홍색 계열
            '#f3e5f5', // 보라색 계열
            '#e8f5e9', // 초록색 계열
            '#fff8e1'  // 노란색 계열
        ];

        // 선택된 컬렉션 찾기
        const selectedCollectionInfo = collections.value.find(
            c => c.collection_id === selectedCollection.value
        );

        // API 요청 데이터 구성
        const bookmarkData = {
            url: props.url,
            collectionId: Number(selectedCollection.value),
            tags: selectedTags.value.map(tag => ({
                tagName: tag.name,
                tagColor: tag.color,
                tagBolder: "normal"
            })),
            readingTime: props.readingTime,
            isPersonal: selectedCollectionInfo.isPersonal
        };

        console.log('서버로 보내는 데이터:', bookmarkData);
        
        const response = await bookmarkStore.saveBookmark(bookmarkData);
        console.log('서버 응답:', response);
        ElMessage.success('북마크가 저장장되었습니다')
        
        emit('save');
        closeModal();
    } catch (error) {
        console.error('북마크 저장 중 오류 발생:', error);
        if (error.response) {
            console.error('서버 응답 데이터:', error.response.data);
            console.error('요청 데이터:', error.config.data);
        }
        ElMessage.error(error.response.data.message);
    }
};

// 디버깅을 위한 watch
watch(() => collections.value, (newCollections) => {
    console.log('컬렉션 데이터 변경:', newCollections);
});

watch(selectedCollection, (newVal) => {
    console.log('선택된 컬렉션 값:', newVal, typeof newVal);
});
</script>

<style scoped>
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

.bookmark-save-modal {
    background-color: white;
    border-radius: 8px;
    padding: 20px;
    width: 90%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-title {
    text-align: center;
    margin-bottom: 20px;
    font-size: 24px;
    font-weight: bold;
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

.tags-section {
    margin-bottom: 20px;
}

.tags-section label {
    display: block;
    margin-bottom: 8px;
}

.selected-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 8px 0;
}

.tag-item {
    display: flex;
    align-items: center;
    gap: 2px;
}

.tag {
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 500;
    white-space: nowrap;
    border: none;
    color: rgb(95, 93, 93);
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    transition: all 0.2s ease;
}

.tag:hover {
    transform: translateY(-1.5px);
    box-shadow: 0 3px 6px rgba(0,0,0,0.15);
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
    color: #ce3e3ebc;
}

.tag-input-container {
    margin-top: 8px;
}

.tag-input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.button-group {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    margin-top: 20px;
}

button {
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}

.save-button {
    background-color: #6366F1;
    color: rgba(255, 255, 255, 0.901);
    border: none;
}

.save-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.cancel-button {
    background-color: #f5f5f5;
    border: 1px solid #ddd;
}
</style>
