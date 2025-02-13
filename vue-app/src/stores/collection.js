import { ref, computed } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import router from "@/router";
import api from "@/utils/api";
const REST_API_URL_coll = `http://localhost:8080/api/collections`;


export const useCollectionStore = defineStore("collection", () => {
  const personalCollections = ref([]); // 개인 컬렉션
  const sharedCollections = ref([]); // 공유 컬렉션
  const allCollections = ref([]); // 전체 컬렉션 저장 (필요할 경우)

  // membersByCollectionId를 ref로 초기화
  const membersByCollectionId = ref(null);  // 여기에 ref 추가

  // 로그인 후 개인 & 공유 컬렉션 모두 불러오기
  const fetchAllCollection = async () => {
    try {
      console.log("✅ 로그인 성공! 저장된 토큰:", sessionStorage.getItem("accessToken"));
      console.log("✅ 저장된 리프레시토큰:", sessionStorage.getItem("refreshToken"));
      
      const token = sessionStorage.getItem("accessToken");

      if (!token) {
        throw new Error("인증 정보가 없습니다. 로그인 후 다시 시도해주세요.");
      }

      const [personalRes, sharedRes] = await Promise.all([
        api.get('/collections/personal'),
        api.get('/collections/shared'),
      ]);

      // success가 true인 경우에만 데이터를 설정
      personalCollections.value = personalRes.data.results || [];
      sharedCollections.value = sharedRes.data.results || [];
      allCollections.value = [...personalCollections.value, ...sharedCollections.value];

      return { personalCollections, sharedCollections, allCollections };
      
    } catch (error) {
      console.error("컬렉션 로드 중 오류 발생:", error);
      throw error;
    }
  };

  const exampleAllCollections = ref([
    { "collection_id": 1, "name": "개발", "isPersonal": true },
    { "collection_id": 2, "name": "자바", "isPersonal": true },
    { "collection_id": 3, "name": "웹서핑", "isPersonal": true },
    { "collection_id": 4, "name": "일본여행", "isPersonal": false },
    { "collection_id": 5, "name": "알고리즘스터디", "isPersonal": false },
    { "collection_id": 6, "name": "영어공부", "isPersonal": false }
  ]);
  ///////////////////////////////////////////////////////////////////////////////////////////////
  //개인 컬렉션 예시 response
  const examplePersonalCollections = ref(
    {
      "success" : true,
      "message" : "some message",
      "results": [
        {
          "collection_id" : 1,
          "name" : "개발",
          "isPersonal" : true
        }, 
        {
          "collection_id" : 2,
          "name" : "자바",
          "isPersonal" : true
        },
        {
          "collection_id" : 3,
          "name" : "웹서핑",
          "isPersonal" : true
        }
      ] 
    }
  );
  ///////////////////////////////////////////////////////////////////////////////////////////////
  //공유 컬렉션 예시 response
  const exampleSharedCollections = ref(
    {
      "success":true,
      "message":"some message",

      "results": [
        {
          "collection_id" : 1,
          "name" : "일본여행",
          "isPersonal" : false
        }, 
        {
          "collection_id" : 2,
          "name" : "알고리즘스터디",
          "isPersonal" : false
        }, 
        {
          "collection_id" : 3,
          "name" : "영어공부",
          "isPersonal" : false
        }
      ]
    }
  )
  //공유 컬렉션 id로 멤버 조회
  const getMembersByCollectionId = async (collectionId) => {
    try {
      console.log('컬렉션 ID:', collectionId);
      const response = await api.get(`/collections/shared/${collectionId}/users`);
      console.log('API 응답:', response.data);
      membersByCollectionId.value = response.data;  // .value로 값 설정
      return response.data;
    } catch (error) {
      console.error('공유컬렉션 멤버 조회 중 오류 발생:', error);
      throw error;
    }
  };
  // 공유 컬렉션 id로 멤버 조회 예시 response
  const exampleMembersByCollectionId = ref(
    {
      "success" : true,
      "message" : "공유컬렉션의 사용자를 조회합니다. ",
      "results" : [
        {
          "user_id" : 1,
          "email" : "example11@naver.com",
          "nickname" : "사용자1"
        }, 
        {
          "user_id" : 2,
          "email" : "ex222@google.com",
          "nickname" : "사용자2"
        }, 
      ]
    }
  )
  ///////////////////////////////////////////////////////////////////////////////////////////////
  // 개인 컬렉션 생성
  const createPersonalCollection = async (name) => {
    try {
      const request = {
        "name": name
      } 
      const response = await api.post('/collections/personal', request)
      console.log('컬렉션 생성 성공:')
    } catch (error) {
      console.error('개인 컬렉션 생성 중 오류 발생:', error)
      throw error
    }
  }

  // 공유 컬렉션 생성
  const createSharedCollection = async (name) => {
    try {
      const request = {
        "name": name
      } 
      const response = await api.post('/collections/shared', request)
      console.log('컬렉션 생성 성공:')

    } catch (error) {
      console.error('컬렉션 생성 중 오류 발생:', error)
      throw error
    }
  }
  // 개인컬렉션 이름 변경
  const updatePersonalCollectionName = async (collectionId, newName) => {
    try {
      const request = {
        "name": newName
      }
      const response = await api.put(`/collections/personal/${collectionId}`, request)
      console.log('컬렉션 이름 변경 성공:')

      // 변경 후 컬렉션 목록 갱신
      await fetchAllCollection();

    } catch (error) {
      console.error('컬렉션 이름 변경 중 오류 발생:', error)
      throw error
    }
  }
  // 공유컬렉션 이름 변경
  const updateSharedCollectionName = async (collectionId, newName) => {
    try {
      const request = {
        "name": newName
      }
      const response = await api.put(`/collections/shared/${collectionId}`, request)
      console.log('컬렉션 이름 변경 성공:')

      // 변경 후 컬렉션 목록 갱신
      await fetchAllCollection();

    } catch (error) {
      console.error('컬렉션 이름 변경 중 오류 발생:', error)
      throw error
    }
  }
  //개인컬렉션 삭제
  const deletePersonalCollection = async (collectionId) => {
    try {
        console.log("삭제할 컬렉션 ID:", collectionId); // 디버깅용 로그 추가
        if (!collectionId) {
            throw new Error("컬렉션 ID가 유효하지 않습니다.");
        }
        const response = await api.delete(`/collections/personal/${collectionId}`);
        console.log('개인컬렉션 삭제 성공:');
        
        // 삭제 후 컬렉션 목록 갱신
        await fetchAllCollection();
    } catch (error) {
        console.error('개인컬렉션 삭제 중 오류 발생:', error);
        throw error;
    }
  };
  //공유컬렉션 삭제
  const deleteSharedCollection = async (collectionId) => {
    try { 
      if (!collectionId) {
        throw new Error("컬렉션 ID가 유효하지 않습니다.");
      }
      
      const response = await api.delete(`/collections/shared/${collectionId}`)
      console.log('공유컬렉션 삭제 성공:')

      // 삭제 후 컬렉션 목록 갱신
      await fetchAllCollection();
    } catch (error) {
      console.error('공유컬렉션 삭제 중 오류 발생:', error)
      throw error
    }

  } 
  // 공유 컬렉션 인원 초대 요청 
  const addMemberToSharedCollection = async (collectionId, memberEmail) => {
    try {
      const request = {
        "email": memberEmail
      }
      const response = await api.post(`/collections/shared/${collectionId}/invite`, request)
      console.log('공유컬렉션 인원 추가 성공:')

    } catch (error) {
      console.error('공유컬렉션 인원 추가 중 오류 발생:', error)
      throw error
    }
  }

  //공유 컬렉션 인원 강퇴
  const removeMemberFromSharedCollection = async (collectionId, userId) => {
    try {
      const response = await api.delete(`/collections/shared/${collectionId}/members/${userId}`)
      console.log('공유컬렉션 인원 강퇴 성공:')
    } catch (error) {
      console.error('공유컬렉션 인원 강퇴 중 오류 발생:', error)
      throw error
    }
  }



    return{
        personalCollections,
        sharedCollections,
        allCollections,
        fetchAllCollection,
        exampleAllCollections,
        examplePersonalCollections,
        exampleSharedCollections,
        createPersonalCollection,
        createSharedCollection,
        updatePersonalCollectionName,
        updateSharedCollectionName,
        deletePersonalCollection,
        deleteSharedCollection,
        addMemberToSharedCollection,
        removeMemberFromSharedCollection,
        getMembersByCollectionId,
        exampleMembersByCollectionId,
        membersByCollectionId
    }
});



