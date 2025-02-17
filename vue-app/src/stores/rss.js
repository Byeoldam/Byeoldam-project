import { defineStore } from "pinia";
import { ref } from 'vue';
import api from "@/utils/api";

//rss에 관한 함수 store
export const useRssStore = defineStore("rss", () => {
    //rss 구독 목록 조회 실제 response
    const rssList = ref({});
    //rss 구독 목록 조회
    const isLoading = ref(false);
    const getRss = async () => {
        isLoading.value = true;
        try {
            const response = await api.get(`/subscriptions`);
            rssList.value = response.data;
            console.log('rss 구독 목록 조회에 성공하였습니다'); 
            return response.data;
        } finally {
            isLoading.value = false;
        }
    };
    //rss 구독 목록 조회 예시 response
    // const exRssList = ref(
    //     {
    //         "status": true,
    //         "message": "string",
    //         "results": [
    //             {
    //             "rssId": 0,
    //             "name": "string",
    //             "read": true
    //             }
    //         ]
    //     }
    // );

    //구독 중인 rss 최신 글 목록 조회 실제 response
    const rssArticles = ref({});
    //특정 rss 최신 글 목록 조회
    const getRssArticles = async (rssId, page, size) => {
        isLoading.value = true;
        try {
            const response = await api.get(`/subscriptions/${rssId}/latest?page=${page}&size=${size}`);
            rssArticles.value = response.data;
            console.log('rss 최신 글 목록 조회에 성공하였습니다');
            return response.data;
        } finally {
            isLoading.value = false;
        }
    };
    //구독 중인 rss 최신 글 목록 조회 예시 response
    // const exRssArticles = ref(
    //     {
    //         "status": true,
    //         "message": "string",
    //         "results": {
    //           "rssId": 0,
    //           "name": "string",
    //           "latestPosts": {
    //                 "totalElements": 0,
    //                 "totalPages": 0,
    //                 "size": 0,
    //                 "content": [
    //                     {
    //                         "title": "string",
    //                         "url": "string",
    //                         "read": true
    //                     }
    //                 ],
    //                 "number": 0,
    //                 "sort": {
    //                     "empty": true,
    //                     "sorted": true,
    //                     "unsorted": true
    //                 },
    //                 "first": true,
    //                 "last": true,
    //                 "numberOfElements": 0,
    //                 "pageable": {
    //                     "offset": 0,
    //                     "sort": {
    //                         "empty": true,
    //                         "sorted": true,
    //                         "unsorted": true
    //                     },
    //                     "pageNumber": 0,
    //                     "paged": true,
    //                     "pageSize": 0,
    //                     "unpaged": true
    //                 },
    //                 "empty": true
    //             }
    //         }
    //     }
    // );



    //rss 구독 삭제
    const deleteRss = async (rssId) => {
        const response = await api.delete(`/subscriptions/${rssId}`);
        console.log('rss 구독 삭제에 성공하였습니다');
    };

    return {
        getRss,
        getRssArticles,
        deleteRss,
        rssList,
        rssArticles,
        isLoading,
    };

});