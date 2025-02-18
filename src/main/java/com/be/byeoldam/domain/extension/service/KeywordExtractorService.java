package com.be.byeoldam.domain.extension.service;

import com.be.byeoldam.domain.extension.dto.ExtensionRequest;
import com.be.byeoldam.domain.tag.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class KeywordExtractorService {

    private final ChatClient chatClient;
    private final TagRepository tagRepository;
    private static final int EXPECTED_KEYWORD_COUNT = 3;
    private static final int MAX_KEYWORD_LENGTH = 20;
    private static final String DEFAULT_KEYWORD = "기타";

    @Autowired
    public KeywordExtractorService(ChatClient chatClient, TagRepository tagRepository) {
        this.chatClient = chatClient;
        this.tagRepository = tagRepository;
    }

    public List<String> extractKeywords(ExtensionRequest request) {
        List<String> tagList = tagRepository.getAllNames();
        log.info("태그목록 : " + tagList);

        SystemMessage systemMessage = new SystemMessage("""
                    당신은 웹 콘텐츠 태깅 및 키워드 추출 전문가입니다.
                
                    역할:
                    사용자가 북마크하는 웹페이지의 내용을 나중에 쉽게 찾을 수 있도록
                    가장 적절한 태그나 키워드를 추천합니다.
                
                    분석 대상:
                    1. URL 분석 (도메인 및 특정 패턴 참고)
                       - 개발 관련: stackoverflow.com, docs.spring.io
                       - 학습 관련: inflearn.com, nomadcoders.co, champ.hackers.com
                       - 취업 관련: jobkorea.co.kr, wanted.co.kr, saramin.co.kr
                
                
                    2. 제목 분석 (다음 도메인 또는 유사한 도메인일 경우 수행)
                       - 블로그 (velog.io, tistory.com, blog.naver.com 등)
                       - 동영상 (youtube.com, tv.naver.com 등)
                       - 커뮤니티 (stackoverflow.com 등)
                       - 뉴스 및 기타 콘텐츠
                
                    주어진 URL과 제목을 분석하여 가장 연관성 높은 태그를 아래 목록에서 선택하거나,
                    적절한 태그가 없는 경우 핵심 키워드를 추출해주세요.
                
                    현재 사용 가능한 태그 목록: %s
                
                    태그 선택 규칙:
                    1. 태그 유사도 판단 (URL + 제목 종합 분석)
                       - 강한 연관성: 제공된 태그 목록에서 선택
                       - 부분 연관성: 제공된 태그와 추출 키워드 조합
                       - 약한 연관성: 제목에서 직접 키워드 추출
                
                    2. 태그 선택 우선순위
                       1순위: 콘텐츠의 핵심을 가장 정확하게 표현하는 태그
                         - 구체적인 기술/도구
                         - 명확한 주제/활동
                         - 특정 분야/장소
                       2순위: 콘텐츠 이해나 분류에 도움이 되는 보조 태그
                         - 관련 분야
                         - 상위 카테고리
                         - 연관 주제
                
                    3. URL 분석 참고사항:
                       - 공식 문서/레퍼런스:
                         docs.spring.io, developer.mozilla.org 등 도메인에서 주제 파악
                       - URL 기반 분석의 구체화:
                         특정 URL 패턴을 참고하여 더 정교한 분석 수행
                         (예: stackoverflow.com/questions/ → 개발 질문)
                       - 제목 중심 분석:
                         • 블로그 (velog.io, tistory.com, blog.naver.com 등)
                         • 동영상 (youtube.com, tv.naver.com 등)
                         • 커뮤니티 (stackoverflow.com 등)
                
                    4. 태그 선택 예시:
                       블로그/개인 콘텐츠:
                       - URL: "velog.io/@user/post-123"
                         제목: "스프링 시큐리티 기초"
                         -> [SPRING, 시큐리티, 개발]
                
                       동영상:
                       - URL: "https://www.youtube.com/watch?v=V1PI7KTCcWo"
                         제목: "30분 전신 홈트레이닝"
                         -> [홈트레이닝, 전신, 운동]
                
                       기사:
                       - URL: "https://sgsg.hankyung.com/article/2025020700311"
                         제목: "시사이슈 찬반토론 상장 폐지 요건 완화해야 할"
                         -> [상장폐지, 시사이슈, 학습]
                
                    5. 추천 개수:
                       - 정확도 높은 태그/키워드 3개
                
                    6. 응답 형식:
                       - 응답은 키워드들이 쉼표로 구분되고, 각 키워드 뒤에는 공백 한 칸이 포함됩니다.
                       - 예시: [키워드1, 키워드2, 키워드3]
                """.formatted(tagList.toString()));

        UserMessage userMessage = new UserMessage("""
                다음 웹페이지의 URL과 제목을 분석하여 적절한 태그나 키워드를 추출해주세요:
                URL: %s
                제목: %s
                """.formatted(request.getSiteUrl(), request.getTitle()));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        try {
            String responseContent = chatClient.call(prompt).getResult().getOutput().getContent();

            // 대괄호 제거하고 키워드 분리
            String cleanedResponse = responseContent
                    .replaceAll("[\\[\\]]", "")  // 대괄호 제거
                    .replaceAll("->\\s*", "")    // 화살표와 그 뒤의 공백 제거
                    .trim();

            List<String> keywords = Arrays.asList(cleanedResponse.split(",\\s*"));

            // 응답 유효성 검사
            if (isValidKeywordResponse(keywords)) {
                return keywords;
            }
            log.warn("유효하지 않은 키워드 응답: {}", responseContent);
        } catch (Exception e) {
            log.error("키워드 추출 중 오류 발생: {}", e.getMessage());
        }

        return List.of(DEFAULT_KEYWORD);
    }

    private boolean isValidKeywordResponse(List<String> keywords) {
        // 키워드 개수 체크
        if (keywords.size() != EXPECTED_KEYWORD_COUNT) {
            log.debug("키워드 개수 불일치. 예상: {}, 실제: {}", EXPECTED_KEYWORD_COUNT, keywords.size());
            return false;
        }

        // 각 키워드 유효성 검사
        for (String keyword : keywords) {
            if (keyword == null || keyword.trim().isEmpty() ||
                    keyword.trim().length() > MAX_KEYWORD_LENGTH) {
                log.debug("유효하지 않은 키워드: {}", keyword);
                return false;
            }
        }

        return true;
    }
}
