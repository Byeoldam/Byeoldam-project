<script setup>
const props = defineProps({
    collectionInfo: {
        type: Object,
        required: true,
        validator: (value) => {
            return (
                'collectionId' in value &&
                'name' in value &&
                'isPersonal' in value
            );
        }
    }
});

// 북마크 데이터 로드
const loadBookmarks = async () => {
    try {
        await bookmarkStore.getPersonalCollectionBookmarks(props.collectionInfo.collectionId);
        console.log("학준 collection bookmarks:", bookmarkStore.personalCollectionBookmarks);
    } catch (error) {
        console.error('북마크 로딩 실패:', error);
    }
};
</script> 