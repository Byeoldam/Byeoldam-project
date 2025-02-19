<template>
    <div class="body-container">
      <div class="stars"></div>
      <div class="stars2"></div>
      <div class="stars3"></div>
      
      <!-- 로딩 상태 추가 -->
      <div v-if="isLoading" class="loading">
        로딩중...
      </div>
      
      <!-- 마이페이지 컨텐츠 추가 -->
      <div class="mypage-content" v-if="!isLoading && myPageStore.myPage?.results">
        <div class="header-section">
          <button @click="goToMain" class="back-button">
            <span class="back-arrow">←</span> 메인으로
          </button>
          <h2>마이페이지</h2>
        </div>

        <div class="form-group">
          <label>닉네임</label>
          <div class="input-with-button">
            <input v-model="editedNickname" 
                   class="form-input"
                   :placeholder="myPageStore.myPage.results.nickname">
          </div>
        </div>

        <div class="form-group">
          <label>알림 날짜</label>
          <div class="input-with-button">
            <input v-model="editedAlertDay" 
                   type="number" 
                   class="form-input"
                   min="0">
          </div>
        </div>

        <div class="form-group">
          <label>프로필 이미지</label>
          <div class="profile-section">
            <div class="profile-content">
              <img :src="myPageStore.myPage.results.profileUrl || defaultProfileImage" 
                   alt="프로필 이미지" 
                   class="profile-image">
              <div class="profile-actions">
                <label class="button-primary">
                  변경
                  <input type="file" @change="handleImageUpload" accept="image/*" hidden>
                </label>
                <button @click="handleDeleteImage" class="button-secondary">삭제</button>
              </div>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>태그 목록</label>
          <div class="tag-list">
            <div v-for="(tag, index) in tags" 
                 :key="index" 
                 class="tag-item"
                 :style="{ backgroundColor: tag.tagColor, 
                          border: `1px solid ${tag.tagBolder}` }">
              {{ tag.tagName }}
              <button @click="removeTag(index)" class="tag-delete-btn">×</button>
            </div>
          </div>
          <div class="input-with-button">
            <input v-model="newTag.tagName" 
                   placeholder="태그 이름" 
                   class="form-input">
            <button @click="addTag" class="button-primary">추가</button>
          </div>
        </div>

        <button @click="showPasswordModal = true" class="button-secondary full-width">
            비밀번호 변경
        </button>
        <button @click="saveChanges" class="button-primary full-width">변경사항 저장</button>
        <button @click="confirmDelete" class="button-danger full-width">
          회원 탈퇴
        </button>
      </div>

      <!-- 비밀번호 변경 모달 -->
      <div v-if="showPasswordModal" class="modal">
        <div class="modal-content">
          <h3>비밀번호 변경</h3>
          <div class="form-group">
            <input v-model="currentPassword" 
                   type="password" 
                   placeholder="현재 비밀번호"
                   class="form-input">
          </div>
          <div class="form-group">
            <input v-model="newPassword" 
                   type="password" 
                   placeholder="새 비밀번호"
                   class="form-input">
          </div>
          <div class="modal-buttons">
            <button @click="changePassword" class="button-primary">변경</button>
            <button @click="showPasswordModal = false" class="button-secondary">취소</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import { useUserStore } from "@/stores/user";
  import { useMyPageStore } from "@/stores/myPage";
  import { useRouter } from "vue-router";
  
  const router = useRouter()
  const userstore = useUserStore()
  const myPageStore = useMyPageStore()
  
  const starsStyle = ref('');
  const stars2Style = ref('');
  const stars3Style = ref('');
  
  // 상태 관리
  const defaultProfileImage = '/path/to/default/image.png';
  const editedNickname = ref('');
  const editedAlertDay = ref(0);
  const tags = ref([]);
  const showPasswordModal = ref(false);
  const currentPassword = ref('');
  const newPassword = ref('');
  const isLoading = ref(true);  // 로딩 상태 추가
  
  const newTag = ref({
    tagName: ''
  });
  
  onMounted(() => {
    starsStyle.value = generateStarsShadow(700, 1);
    stars2Style.value = generateStarsShadow(200, 2);
    stars3Style.value = generateStarsShadow(100, 3);
  });
  
  function generateStarsShadow(count, size) {
      let shadows = [];
      for(let i = 0; i < count; i++) {
          const x = Math.floor(Math.random() * 2000);
          const y = Math.floor(Math.random() * 2000);
          shadows.push(`${x}px ${y}px #FFF`);
      }
      return shadows.join(', ');
  }
  
  // 초기 데이터 로드
  onMounted(async () => {
    try {
      isLoading.value = true;  // 로딩 시작
      await myPageStore.getMyPage();
      if (myPageStore.myPage?.results) {
        editedNickname.value = myPageStore.myPage.results.nickname;
        editedAlertDay.value = myPageStore.myPage.results.alrerDay;
        tags.value = myPageStore.myPage.results.tagList || [];
      }
    } catch (error) {
      console.error('마이페이지 로드 실패:', error);
    } finally {
      isLoading.value = false;  // 로딩 완료
    }
  });
  
  // 이미지 업로드 처리
  const handleImageUpload = async (event) => {
    const file = event.target.files[0];
    if (file) {
      // 파일 크기 체크 (예: 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('파일 크기는 5MB 이하여야 합니다.');
        return;
      }

      const formData = new FormData();
      // 'upload'로 키 값 변경
      formData.append('upload', file);

      try {
        const response = await myPageStore.updateProfileImage(formData);
        if (response.data.status) {
          console.log('성공');
          console.log(response.data.results.s3Url);
          
          const updatedUser = {
            ...userstore.user,
            profileUrl: response.data.results.s3Url
          };
        
          // userStore와 localStorage 모두 업데이트
          userstore.user = updatedUser;
          localStorage.setItem("user", JSON.stringify(updatedUser));
        
          alert('프로필 이미지가 성공적으로 변경되었습니다.');

        }
      } catch (error) {
        console.error('이미지 업로드 실패:', error);
        alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
      }
    }
  };
  
  // 이미지 삭제
  const handleDeleteImage = async () => {
    try {
      await myPageStore.deleteProfileImage();
    } catch (error) {
      console.error('이미지 삭제 실패:', error);
    }
  };
  
  // 변경사항 저장
  const saveChanges = async () => {
    try {
      const response = await myPageStore.updateMyPage({
        nickname: editedNickname.value,
        alertDay: editedAlertDay.value,
        tagList: tags.value
      });
      
      if (response.data.status) {
        // userStore의 user 정보 업데이트
        const updatedUser = {
          ...userstore.user,
          nickname: editedNickname.value
        };
        
        // userStore와 localStorage 모두 업데이트
        userstore.user = updatedUser;
        localStorage.setItem("user", JSON.stringify(updatedUser));
        
        alert('변경사항이 저장되었습니다.');
        router.push('/main');
      }
    } catch (error) {
      console.error('정보 업데이트 실패:', error);
      alert('정보 업데이트에 실패했습니다.');
    }
  };
  
  // 비밀번호 변경
  const changePassword = async () => {
    try {
      await myPageStore.updatePassword({
        currentPassword: currentPassword.value,
        newPassword: newPassword.value
      });
      showPasswordModal.value = false;
      currentPassword.value = '';
      newPassword.value = '';
    } catch (error) {
      console.error('비밀번호 변경 실패:', error);
    }
  };
  
  // 회원 탈퇴
  const confirmDelete = async () => {
    if (confirm('정말 탈퇴하시겠습니까?')) {
      try {
        await myPageStore.deleteUser();
        router.push('/login');
      } catch (error) {
        console.error('회원 탈퇴 실패:', error);
      }
    }
  };
  
  // 랜덤 색상 생성 함수
  const getRandomColor = () => {
    // 부드러운 파스텔톤 색상을 위해 채도와 명도 조정
    const hue = Math.floor(Math.random() * 360); // 0-359 색상
    const saturation = Math.floor(Math.random() * 30) + 70; // 70-100% 채도
    const lightness = Math.floor(Math.random() * 15) + 75; // 75-90% 명도
    return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
  };
  
  // 태그 추가 함수 수정
  const addTag = () => {
    if (newTag.value.tagName.trim()) {
      const backgroundColor = getRandomColor();
      // 테두리 색상은 배경색보다 약간 어둡게
      const borderColor = backgroundColor.replace(
        /lightness\)/, 
        'lightness) darken(15%)'
      );
      
      tags.value.push({
        tagName: newTag.value.tagName,
        tagColor: backgroundColor,
        tagBolder: borderColor
      });
      
      newTag.value.tagName = '';
    }
  };
  
  const removeTag = (index) => {
    tags.value.splice(index, 1);
  };

  const goToMain = () => {
    router.push('/main');
  };
  </script>
  
  <style scoped>
  .body-container {
    position: relative;
    overflow: hidden;
    background: radial-gradient(ellipse at bottom, #1B2735 0%, #1E1B4B 100%);
    min-height: 100vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 1rem;
  }
  
  @keyframes animStar {
      from { transform: translateY(0); }
      to { transform: translateY(-2000px); }
  }
  
  .stars, .stars2, .stars3 {
      position: absolute;
      top: 0;
      left: 0;
      background: transparent;
  }
  
  .stars {
      width: 1px;
      height: 1px;
      box-shadow: v-bind('starsStyle');
      animation: animStar 50s linear infinite;
  }
  
  .stars2 {
      width: 2px;
      height: 2px;
      box-shadow: v-bind('stars2Style');
      animation: animStar 100s linear infinite;
  }
  
  .stars3 {
      width: 3px;
      height: 3px;
      box-shadow: v-bind('stars3Style');
      animation: animStar 150s linear infinite;
  }

  .mypage-content {
    position: relative;
    z-index: 1;
    background: rgba(255, 255, 255, 0.95);
    padding: 2rem;
    border-radius: 15px;
    width: 100%;
    max-width: 450px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  .header-section {
    position: relative;
    margin-bottom: 1.5rem;
  }

  .back-button {
    position: absolute;
    left: 0;
    top: 0;
    background: none;
    border: none;
    color: #4B48EC;
    font-size: 0.9rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.3rem;
    padding: 0.3rem 0.6rem;
    border-radius: 5px;
    transition: background-color 0.2s;
  }

  .back-button:hover {
    background-color: rgba(75, 72, 236, 0.1);
  }

  .back-arrow {
    font-size: 1.2rem;
    line-height: 1;
  }

  h2 {
    text-align: center;
    margin: 0;
    color: #1a1a1a;
    font-size: 1.5rem;
    font-weight: 600;
  }

  .form-group {
    margin-bottom: 1rem;
  }

  label {
    display: block;
    margin-bottom: 0.3rem;
    color: #4a4a4a;
    font-size: 0.9rem;
    font-weight: 500;
  }

  .form-input {
    width: 100%;
    padding: 0.6rem;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    font-size: 0.9rem;
    transition: border-color 0.2s;
    background: white;
  }

  .form-input:focus {
    outline: none;
    border-color: #4B48EC;
    box-shadow: 0 0 0 2px rgba(75, 72, 236, 0.1);
  }

  .input-with-button {
    display: flex;
    gap: 0.5rem;
    align-items: center;
  }

  .input-with-button .form-input {
    flex: 1;
  }

  .input-with-button .button-primary {
    width: auto !important;
    white-space: nowrap !important;
    margin: 0 !important;
  }

  .button-primary, .button-secondary, .button-danger {
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.9rem;
    height: 40px;
    width: 100px;
  }

  .full-width {
    width: 100%;
    margin-bottom: 0.4rem;
  }

  .profile-section {
    margin: 0.3rem 0;
    padding: 0.3rem;
    height: auto;
    min-height: 60px;
  }

  .profile-content {
    display: flex;
    align-items: center;
    gap: 1rem;
    width: 100%;
  }

  .profile-image {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #4B48EC;
    padding: 2px;
    flex-shrink: 0;
  }

  .profile-actions {
    display: flex;
    gap: 0.3rem;
    flex-grow: 1;
    justify-content: flex-start;
  }

  .profile-actions .button-primary,
  .profile-actions .button-secondary {
    padding: 0.2rem 0.4rem !important;
    font-size: 0.8rem !important;
    min-width: 50px !important;
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
    text-align: center !important;
    height: 25px !important;
  }

  .profile-actions label.button-primary {
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
    margin: 0 !important;
    cursor: pointer !important;
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.4rem;
    margin-bottom: 0.8rem;
    padding: 0.3rem;
  }

  .tag-item {
    padding: 0.3rem 0.8rem;
    border-radius: 20px;
    display: flex;
    align-items: center;
    gap: 0.3rem;
    font-size: 0.8rem;
    transition: transform 0.2s;
  }

  .tag-item:hover {
    transform: translateY(-1px);
  }

  .tag-delete-btn {
    background: none;
    border: none;
    color: rgba(0, 0, 0, 0.5);
    font-size: 1rem;
    cursor: pointer;
    padding: 0;
    line-height: 1;
    transition: color 0.2s;
  }

  .tag-delete-btn:hover {
    color: rgba(0, 0, 0, 0.8);
  }

  .mypage-content .button-primary {
    background-color: #4B48EC !important;
    color: white !important;
    padding: 0.6rem 1rem !important;
    border: none !important;
    border-radius: 8px !important;
    cursor: pointer !important;
    font-size: 0.9rem !important;
    font-weight: 500 !important;
    width: 100% !important;
    margin-bottom: 0.5rem !important;
    transition: background-color 0.2s !important;
  }

  .mypage-content .button-secondary {
    background-color: #6c757d !important;
    color: white !important;
    padding: 0.6rem 1rem !important;
    border: none !important;
    border-radius: 8px !important;
    cursor: pointer !important;
    font-size: 0.9rem !important;
    font-weight: 500 !important;
    width: 100% !important;
    margin-bottom: 0.5rem !important;
    transition: background-color 0.2s !important;
  }

  .mypage-content .button-danger {
    background-color: #dc3545 !important;
    color: white !important;
    padding: 0.6rem 1rem !important;
    border: none !important;
    border-radius: 8px !important;
    cursor: pointer !important;
    font-size: 0.9rem !important;
    font-weight: 500 !important;
    width: 100% !important;
    margin-bottom: 0.5rem !important;
    transition: background-color 0.2s !important;
  }

  .mypage-content .button-primary:hover {
    background-color: #3f3cd9 !important;
  }

  .mypage-content .button-secondary:hover {
    background-color: #5a6268 !important;
  }

  .mypage-content .button-danger:hover {
    background-color: #c82333 !important;
  }

  .profile-actions .button-primary,
  .profile-actions .button-secondary,
  .input-with-button .button-primary {
    width: auto !important;
    margin-bottom: 0 !important;
    padding: 0.4rem 0.8rem !important;
    font-size: 0.85rem !important;
  }

  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    backdrop-filter: blur(5px);
  }

  .modal-content {
    background: white;
    padding: 1.5rem;
    border-radius: 15px;
    width: 90%;
    max-width: 350px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  .modal-buttons {
    display: flex;
    gap: 0.5rem;
    margin-top: 1rem;
  }

  @media (max-width: 768px) {
    .mypage-content {
      margin: 0.5rem;
      padding: 1.5rem;
    }
    
    .profile-actions {
      flex-direction: row;
    }
    
    .input-with-button {
      flex-direction: row;
      gap: 0.3rem;
    }
  }

  .loading {
    position: relative;
    z-index: 1;
    background: rgba(255, 255, 255, 0.95);
    padding: 2rem;
    border-radius: 15px;
    width: 100%;
    max-width: 450px;
    text-align: center;
    color: #4B48EC;
    font-weight: 500;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  </style>
    