<template>
    <div class="collection-delete-modal">
      <h2 class="modal-title">컬렉션 삭제</h2>
      <p class="warning-text">삭제된 컬렉션은 되돌릴 수 없습니다.</p>
      <div class="button-group">
        <el-button @click="$emit('close')">아니오</el-button>
        <el-button type="danger" @click="handleDelete">예, 삭제하겠습니다</el-button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useCollectionStore } from '@/stores/collection'
  import { ElMessage } from 'element-plus'
  
  const emit = defineEmits(['close', 'confirm'])
  const collectionStore = useCollectionStore()
  const props = defineProps({
    collectionId: {
      type: Number,
      required: true
    },
    isPersonal: {
      type: Boolean,
      required: true
    }
  })
  
  const handleDelete = async () => {
    try {
      if (props.isPersonal) {
        await collectionStore.deletePersonalCollection(props.collectionId)
      } else {
        await collectionStore.deleteSharedCollection(props.collectionId)
      }
      await collectionStore.fetchAllCollections()
      ElMessage.success('컬렉션이 삭제되었습니다')
      emit('confirm')
      emit('close')
    } catch (error) {
      console.error('컬렉션 삭제 중 오류 발생:', error)
      ElMessage.error('컬렉션 삭제에 실패했습니다')
    }
  }
  </script>
  
  <style scoped>
  .collection-delete-modal {
    padding: 20px;
    text-align: center;
  }
  
  .modal-title {
    font-size: 20px;
    margin-bottom: 16px;
    color: #303133;
  }
  
  .warning-text {
    color: #606266;
    margin-bottom: 24px;
  }
  
  .button-group {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
  </style>