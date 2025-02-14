import { ref, computed } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import router from "@/router";
import api from "@/utils/api";
const REST_API_URL_coll = `http://localhost:8080/api/collections`;


export const useCollectionStore = defineStore("collection", {
  state: () => ({
    personalCollections: null,
    sharedCollections: null,
    allCollections: null,
    membersByCollectionId: null,
  }),

  actions: {
    async fetchAllCollection() {
      try {
        const response = await api.get('/collections/personal');
        console.log('API Response:', response.data, 'hakjun0412');
        this.personalCollections = response.data;
        return response.data;
      } catch (error) {
        console.error('컬렉션 데이터 가져오기 실패:', error, 'hakjun0412');
        throw error;
      }
    },

    async fetchSharedCollection() {
      try {
        const response = await api.get('/collections/shared');
        console.log('API Response:', response.data, 'hakjun0412');
        this.sharedCollections = response.data;
        return response.data;
      } catch (error) {
        console.error('공유컬렉션 데이터 가져오기 실패:', error, 'hakjun0412');
        throw error;
      }
    },

    async fetchAllCollections() {
      try {
        const personalCollections = await this.fetchAllCollection();
        const sharedCollections = await this.fetchSharedCollection();
        this.allCollections = [...personalCollections.results, ...sharedCollections.results];
        return { personalCollections, sharedCollections, allCollections: this.allCollections };
      } catch (error) {
        console.error('전체 컬렉션 로드 중 오류 발생:', error);
        throw error;
      }
    },

    async getMembersByCollectionId(collectionId) {
      try {
        console.log('컬렉션 ID:', collectionId);
        const response = await api.get(`/collections/shared/${collectionId}/users`);
        console.log('API 응답:', response.data);
        this.membersByCollectionId = response.data;
        return response.data;
      } catch (error) {
        console.error('공유컬렉션 멤버 조회 중 오류 발생:', error);
        throw error;
      }
    },

    async createPersonalCollection(name) {
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
    },

    async createSharedCollection(name) {
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
    },

    async updatePersonalCollectionName(collectionId, newName) {
      try {
        const request = {
          "name": newName
        }
        const response = await api.put(`/collections/personal/${collectionId}`, request)
        console.log('컬렉션 이름 변경 성공:')

        await this.fetchAllCollection();
      } catch (error) {
        console.error('컬렉션 이름 변경 중 오류 발생:', error)
        throw error
      }
    },

    async updateSharedCollectionName(collectionId, newName) {
      try {
        const request = {
          "name": newName
        }
        const response = await api.put(`/collections/shared/${collectionId}`, request)
        console.log('컬렉션 이름 변경 성공:')

        await this.fetchAllCollection();
      } catch (error) {
        console.error('컬렉션 이름 변경 중 오류 발생:', error)
        throw error
      }
    },

    async deletePersonalCollection(collectionId) {
      try {
        console.log("삭제할 컬렉션 ID:", collectionId);
        if (!collectionId) {
          throw new Error("컬렉션 ID가 유효하지 않습니다.");
        }
        const response = await api.delete(`/collections/personal/${collectionId}`);
        console.log('개인컬렉션 삭제 성공:');
        
        await this.fetchAllCollection();
      } catch (error) {
        console.error('개인컬렉션 삭제 중 오류 발생:', error);
        throw error;
      }
    },

    async deleteSharedCollection(collectionId) {
      try { 
        if (!collectionId) {
          throw new Error("컬렉션 ID가 유효하지 않습니다.");
        }
        
        const response = await api.delete(`/collections/shared/${collectionId}`)
        console.log('공유컬렉션 삭제 성공:')

        await this.fetchAllCollection();
      } catch (error) {
        console.error('공유컬렉션 삭제 중 오류 발생:', error)
        throw error
      }
    },

    async addMemberToSharedCollection(collectionId, memberEmail) {
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
    },

    async removeMemberFromSharedCollection(collectionId, userId) {
      try {
        const response = await api.delete(`/collections/shared/${collectionId}/members/${userId}`)
        console.log('공유컬렉션 인원 강퇴 성공:')
      } catch (error) {
        console.error('공유컬렉션 인원 강퇴 중 오류 발생:', error)
        throw error
      }
    },
  },

  getters: {
    exampleAllCollections() {
      return [
        { "collection_id": 1, "name": "개발", "isPersonal": true },
        { "collection_id": 2, "name": "자바", "isPersonal": true },
        { "collection_id": 3, "name": "웹서핑", "isPersonal": true },
        { "collection_id": 4, "name": "일본여행", "isPersonal": false },
        { "collection_id": 5, "name": "알고리즘스터디", "isPersonal": false },
        { "collection_id": 6, "name": "영어공부", "isPersonal": false }
      ];
    },

    examplePersonalCollections() {
      return {
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
      };
    },

    exampleSharedCollections() {
      return {
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
      };
    },

    exampleMembersByCollectionId() {
      return {
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
      };
    },
  }
});



