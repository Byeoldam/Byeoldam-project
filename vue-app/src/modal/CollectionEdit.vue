<template>
    <div class="collection-edit-modal">
      <h2 class="modal-title">컬렉션 이름 수정</h2>
      <el-input
        v-model="newName"
        placeholder="새로운 컬렉션 이름"
        :maxlength="50"
        show-word-limit
      />
      <div class="button-group">
        <el-button @click="$emit('close')">취소</el-button>
        <el-button type="primary" @click="handleUpdate" :disabled="!isValid">
          수정
        </el-button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue'
  import { useCollectionStore } from '@/stores/collection'
  import { ElMessage } from 'element-plus'
  
  const emit = defineEmits(['close', 'confirm'])
  const collectionStore = useCollectionStore()
  const props = defineProps({
    collectionId: {
      type: Number,
      required: true
    },
    currentName: {
      type: String,
      required: true
    },
    isPersonal: {
      type: Boolean,
      required: true
    }
  })
  
  const newName = ref(props.currentName)
  
  const isValid = computed(() => {
    return newName.value && newName.value.trim() !== '' && newName.value !== props.currentName
  })
  
  const handleUpdate = async () => {
    if (!isValid.value) return
  
    try {
      if (props.isPersonal) {
        await collectionStore.updatePersonalCollectionName(props.collectionId, newName.value)
      } else {
        await collectionStore.updateSharedCollectionName(props.collectionId, newName.value)
      }
      await collectionStore.fetchAllCollections()
      ElMessage.success('컬렉션 이름이 수정되었습니다')
      emit('confirm')
      emit('close')
    } catch (error) {
      console.error('컬렉션 이름 수정 중 오류 발생:', error)
      ElMessage.error('컬렉션 이름 수정에 실패했습니다')
    }
  }
  </script>
  
  <style scoped>
  .collection-edit-modal {
    padding: 20px;
  }
  
  .modal-title {
    font-size: 20px;
    margin-bottom: 20px;
    color: #303133;
  }
  
  .button-group {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 20px;
  }
  </style>