import { ref } from "vue";
import { defineStore } from "pinia";
import api from "@/utils/api";

export const useCollectionStore = defineStore("collection", () => {
    // 상태 정의
    const personalCollections = ref(null);
    const sharedCollections = ref(null);
    const allCollections = ref(null);
    const membersByCollectionId = ref(null);

    // 액션 정의
    const fetchAllCollection = async () => {
        try {
            const response = await api.get('/collections/personal');
            console.log('API Response:', response.data, 'hakjun0412');
            personalCollections.value = response.data;
            return response.data;
        } catch (error) {
            console.error('컬렉션 데이터 가져오기 실패:', error, 'hakjun0412');
            throw error;
        }
    };

    const fetchSharedCollection = async () => {
        try {
            const response = await api.get('/collections/shared');
            console.log('API Response:', response.data, 'hakjun0412');
            sharedCollections.value = response.data;
            return response.data;
        } catch (error) {
            console.error('공유컬렉션 데이터 가져오기 실패:', error, 'hakjun0412');
            throw error;
        }
    };

    const fetchAllCollections = async () => {
        try {
            const personalCollectionsData = await fetchAllCollection();
            const sharedCollectionsData = await fetchSharedCollection();
            allCollections.value = [...personalCollectionsData.results, ...sharedCollectionsData.results];
            return { 
                personalCollections: personalCollectionsData, 
                sharedCollections: sharedCollectionsData, 
                allCollections: allCollections.value 
            };
        } catch (error) {
            console.error('전체 컬렉션 로드 중 오류 발생:', error);
            throw error;
        }
    };

    const getMembersByCollectionId = async (collectionId) => {
        try {
            console.log('컬렉션 ID:', collectionId);
            const response = await api.get(`/collections/shared/${collectionId}/users`);
            console.log('API 응답:', response.data);
            membersByCollectionId.value = response.data;
            return response.data;
        } catch (error) {
            console.error('공유컬렉션 멤버 조회 중 오류 발생:', error);
            throw error;
        }
    };

    const createPersonalCollection = async (name) => {
        try {
            const request = { "name": name };
            const response = await api.post('/collections/personal', request);
            console.log('컬렉션 생성 성공:');
        } catch (error) {
            console.error('개인 컬렉션 생성 중 오류 발생:', error);
            throw error;
        }
    };

    const createSharedCollection = async (name) => {
        try {
            const request = { "name": name };
            const response = await api.post('/collections/shared', request);
            console.log('컬렉션 생성 성공:');
        } catch (error) {
            console.error('컬렉션 생성 중 오류 발생:', error);
            throw error;
        }
    };

    const updatePersonalCollectionName = async (collectionId, newName) => {
        try {
            const request = { "name": newName };
            const response = await api.put(`/collections/personal/${collectionId}`, request);
            console.log('컬렉션 이름 변경 성공:');
            await fetchAllCollection();
        } catch (error) {
            console.error('컬렉션 이름 변경 중 오류 발생:', error);
            throw error;
        }
    };

    const updateSharedCollectionName = async (collectionId, newName) => {
        try {
            const request = { "name": newName };
            const response = await api.put(`/collections/shared/${collectionId}`, request);
            console.log('컬렉션 이름 변경 성공:');
            await fetchAllCollection();
        } catch (error) {
            console.error('컬렉션 이름 변경 중 오류 발생:', error);
            throw error;
        }
    };

    const deletePersonalCollection = async (collectionId) => {
        try {
            if (!collectionId) {
                throw new Error("컬렉션 ID가 유효하지 않습니다.");
            }
            const response = await api.delete(`/collections/personal/${collectionId}`);
            console.log('개인컬렉션 삭제 성공:');
            await fetchAllCollection();
        } catch (error) {
            console.error('개인컬렉션 삭제 중 오류 발생:', error);
            throw error;
        }
    };

    const deleteSharedCollection = async (collectionId) => {
        try {
            if (!collectionId) {
                throw new Error("컬렉션 ID가 유효하지 않습니다.");
            }
            const response = await api.delete(`/collections/shared/${collectionId}`);
            console.log('공유컬렉션 삭제 성공:');
            await fetchAllCollection();
        } catch (error) {
            console.error('공유컬렉션 삭제 중 오류 발생:', error);
            throw error;
        }
    };

    const addMemberToSharedCollection = async (collectionId, memberEmail) => {
        try {
            const request = { "email": memberEmail };
            const response = await api.post(`/collections/shared/${collectionId}/invite`, request);
            console.log('공유컬렉션 인원 추가 성공:');
        } catch (error) {
            console.error('공유컬렉션 인원 추가 중 오류 발생:', error);
            throw error;
        }
    };

    const removeMemberFromSharedCollection = async (collectionId, userId) => {
        try {
            const response = await api.delete(`/collections/shared/${collectionId}/members/${userId}`);
            console.log('공유컬렉션 인원 강퇴 성공:');
        } catch (error) {
            console.error('공유컬렉션 인원 강퇴 중 오류 발생:', error);
            throw error;
        }
    };

    // 예시 데이터를 위한 getter들
    const exampleAllCollections = [
        { "collection_id": 1, "name": "개발", "isPersonal": true },
        { "collection_id": 2, "name": "자바", "isPersonal": true },
        { "collection_id": 3, "name": "웹서핑", "isPersonal": true },
        { "collection_id": 4, "name": "일본여행", "isPersonal": false },
        { "collection_id": 5, "name": "알고리즘스터디", "isPersonal": false },
        { "collection_id": 6, "name": "영어공부", "isPersonal": false }
    ];

    const examplePersonalCollections = {
        "success": true,
        "message": "some message",
        "results": [
            {
                "collection_id": 1,
                "name": "개발",
                "isPersonal": true
            },
            {
                "collection_id": 2,
                "name": "자바",
                "isPersonal": true
            },
            {
                "collection_id": 3,
                "name": "웹서핑",
                "isPersonal": true
            }
        ]
    };

    const exampleSharedCollections = {
        "success": true,
        "message": "some message",
        "results": [
            {
                "collection_id": 1,
                "name": "일본여행",
                "isPersonal": false
            },
            {
                "collection_id": 2,
                "name": "알고리즘스터디",
                "isPersonal": false
            },
            {
                "collection_id": 3,
                "name": "영어공부",
                "isPersonal": false
            }
        ]
    };

    const exampleMembersByCollectionId = {
        "success": true,
        "message": "공유컬렉션의 사용자를 조회합니다. ",
        "results": [
            {
                "user_id": 1,
                "email": "example11@naver.com",
                "nickname": "사용자1"
            },
            {
                "user_id": 2,
                "email": "ex222@google.com",
                "nickname": "사용자2"
            },
        ]
    };

    return {
        // 상태
        personalCollections,
        sharedCollections,
        allCollections,
        membersByCollectionId,

        // 액션
        fetchAllCollection,
        fetchSharedCollection,
        fetchAllCollections,
        getMembersByCollectionId,
        createPersonalCollection,
        createSharedCollection,
        updatePersonalCollectionName,
        updateSharedCollectionName,
        deletePersonalCollection,
        deleteSharedCollection,
        addMemberToSharedCollection,
        removeMemberFromSharedCollection,

        // 예시 데이터
        exampleAllCollections,
        examplePersonalCollections,
        exampleSharedCollections,
        exampleMembersByCollectionId
    };
});



