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
        <div class="info-section">
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
            <label>태그 목록</label>
            <div class="tag-list">
              <div v-for="(tag, index) in tags" 
                   :key="index" 
                   class="tag-item"
                   :style="{ backgroundColor: tag.tagColor }">
                {{ tag.tagName }}
                <button @click="removeTag(index)" class="tag-delete-btn">×</button>
              </div>
            </div>
            <div class="input-with-button">
              <input v-model="newTag.tagName" 
                     placeholder="태그를 작성 후 엔터를 눌러 추가해주세요" 
                     class="form-input"
                     @keydown.enter="addTag">
            </div>
          </div>

          <button @click="saveChanges" class="button-primary full-width">변경사항 저장</button>
        </div>
        <div class="button-container">
          <button @click="showPasswordModal = true" class="button-secondary">
              비밀번호 변경
          </button>
          <button @click="confirmDelete" class="button-danger">
            회원 탈퇴
          </button>
        </div>
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
  import { ElMessage } from 'element-plus';
  
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
  
  // 이전 색상들을 저장할 배열
  const previousHues = ref([]);
  const MIN_HUE_DIFFERENCE = 30;
  
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
        
          ElMessage({
            message: '프로필 이미지가 성공적으로 변경되었습니다.',
            type: 'success'
          });

        }
      } catch (error) {
        console.error('이미지 업로드 실패:', error);
        ElMessage({
          message: '이미지 업로드에 실패했습니다. 다시 시도해주세요.',
          type: 'error'
        });
      }
    }
  };
  
  // 이미지 삭제ff
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
        
        ElMessage({
          message: '변경사항이 저장되었습니다.',
          type: 'success'
        });
        router.push('/main');
      }
    } catch (error) {
      console.error('정보 업데이트 실패:', error);
      ElMessage({
        message: '정보 업데이트에 실패했습니다.',
        type: 'error'
      });
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
  const getRandomColor = () => {
    const hue = getDistinctHue();
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 50, lightness);
  };

  // 테두리 색상 생성 함수
  const generateDarkerColor = (baseColor) => {
    const hue = getDistinctHue();
    const saturation = 85 + Math.random() * 10;
    const lightness = 92 + Math.random() * 3;
    return hslToHex(hue, saturation - 20, lightness - 25);
  };

  // 태그 추가 함수 수정
  const addTag = () => {
    if (newTag.value.tagName.trim()) {
        const tagColor = getRandomColor();
        const tagBolder = generateDarkerColor(tagColor);
        
        tags.value.push({
            tagName: newTag.value.tagName,
            tagColor,
            tagBolder
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
    padding: 1rem;
    border-radius: 15px;
    width: 100%;
    max-width: 450px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  .header-section {
    position: relative;
    margin-bottom: 1rem;
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
    margin: 0 0 0.8rem 0;
    color: #1a1a1a;
    font-size: 1.3rem;
    font-weight: 600;
  }

  .form-group {
    margin-bottom: 0.6rem;
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
    gap: 0.3rem;
    margin-bottom: 0.6rem;
    padding: 0.2rem;
  }

  .tag-item {
    padding: 4px 8px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 500;
    white-space: nowrap;
    border: none;
    color: rgb(95, 93, 93);
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    gap: 0.3rem;
  }

  .tag-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 3px 6px rgba(0,0,0,0.15);
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

  .info-section {
    background: rgba(255, 255, 255, 0.7);
    padding: 0.8rem;
    border-radius: 10px;
    margin-bottom: 0.8rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(0, 0, 0, 0.1);
  }

  .button-container {
    display: flex;
    gap: 0.8rem;
    margin-top: 0.8rem;
  }

  .button-container button {
    flex: 1;
    margin: 0;
  }
  </style>
    