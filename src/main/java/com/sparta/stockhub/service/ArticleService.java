package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.*;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleListResponseDto;
import com.sparta.stockhub.dto.responseDto.ArticleResponseDto;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.VoteDownRepository;
import com.sparta.stockhub.repository.VoteUpRepository;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.*;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final VoteUpRepository voteUpRepository;
    private final VoteDownRepository voteDownRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StockService stockService;

    // 게시글 작성
    public void createArticle(UserDetailsImpl userDetails, ArticleRequestDto requestDto) {
        Long userId = userDetails.getUser().getUserId();
        String articleTitle = requestDto.getArticleTitle();
        String stockName = requestDto.getStockName();
        String point1 = requestDto.getPoint1();
        String content1 = requestDto.getContent1();
        String point2 = requestDto.getPoint2();
        String content2 = requestDto.getContent2();
        String point3 = requestDto.getPoint3();
        String content3 = requestDto.getContent3();

        if (articleTitle.equals("")) throw new CustomException(ErrorCode.BAD_REQUEST_NOTWRITE);
        if (stockName.equals(null)) throw new CustomException(ErrorCode.BAD_REQUEST_STOCKNAME);
        if (point1.equals("")) throw new CustomException(ErrorCode.BAD_REQUEST_NOTWRITE);
        if (content1.equals("")) throw new CustomException(ErrorCode.BAD_REQUEST_NOTWRITE);
        if (articleTitle.length() > 40) throw new CustomException(ErrorCode.BAD_REQUEST_TITLELENGTH);
        if (point1.length() > 40) throw new CustomException(ErrorCode.BAD_REQUEST_POINTLENGTH);
        if (content1.length() > 800) throw new CustomException(ErrorCode.BAD_REQUEST_CONTENTLENGTH);
        if (point2 != null && content2 != null) {
            if (point2.length() > 40) throw new CustomException(ErrorCode.BAD_REQUEST_POINTLENGTH);
            if (content2.length() > 800) throw new CustomException(ErrorCode.BAD_REQUEST_CONTENTLENGTH);
        }
        if (point3 != null && content3 != null) {
            if (point3.length() > 40) throw new CustomException(ErrorCode.BAD_REQUEST_POINTLENGTH);
            if (content3.length() > 800) throw new CustomException(ErrorCode.BAD_REQUEST_CONTENTLENGTH);
        }

        int stockPriceFirst = stockService.getStockPrice(stockName);
        int stockPriceLast = stockService.getStockPrice(stockName);
        double stockReturn = stockService.getStockReturn(stockPriceFirst, stockPriceLast);

        Article article = new Article(userId, articleTitle, stockName, stockPriceFirst, stockPriceLast, stockReturn,
                point1, content1, point2, content2, point3, content3);

        stockService.registerStock(stockName);

        articleRepository.save(article);
    }

    // 게시글 검색
    public List<ArticleListResponseDto> searchArticle(String keywords) {
        String keywordsTrimmed = keywords.trim();
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();

        // A. 검색어에 공백이 없는 경우
        if (!keywordsTrimmed.contains(" ")) {
            List<Article> articleList = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed);

            for (int i = 0; i < articleList.size(); i++) {
                User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                        () -> new NullPointerException("유저가 존재하지 않습니다.")
                );
                int commentCount = countComment(articleList.get(i));

                responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
            }
        }
        // B. 검색어에 공백이 있는 경우
        else {
            String[] keywordsSplitted = keywordsTrimmed.split(" ");

            // 1. 분리된 검색어로 게시글 검색
            for (String keyword : keywordsSplitted) {
                List<Article> articleList = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(keyword, keyword, keyword, keyword, keyword, keyword, keyword, keyword);

                for (int i = 0; i < articleList.size(); i++) {
                    if (keyword.equals("")) continue; // 공백이 2번 이상 연속된 경우 생기는 검색어 skip

                    User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                            () -> new NullPointerException("유저가 존재하지 않습니다.")
                    );
                    int commentCount = countComment(articleList.get(i));

                    responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
                }
            }

            // 2. 중복 검색 결과 제거
            for (int i = 0; i < responseDtoList.size(); i++) {
                for (int j = i + 1; j < responseDtoList.size(); j++) {
                    if (responseDtoList.get(i).getUserId().equals(responseDtoList.get(j).getUserId()) &&
                            responseDtoList.get(i).getCreatedAt() == responseDtoList.get(j).getCreatedAt())
                        responseDtoList.remove(i);
                }
            }

            // 3. 게시글 제목이 분리 전 검색어를 통째로 포함하는 경우 해당 게시글을 검색 결과의 상단으로 이동
            for (int i = 0; i < responseDtoList.size(); i++) {
                if (responseDtoList.get(i).getArticleTitle().contains(keywordsTrimmed)) {
                    responseDtoList.add(0, responseDtoList.get(i));
                    responseDtoList.remove(i + 1);
                }
            }
        }

        return responseDtoList;
    }

    // 메인: 전체 게시글 목록 조회
    public List<ArticleListResponseDto> readMainArticles() {
        List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 10) break; // 메인 페이지에 내릴 때는 최신 게시글 10개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 명예의 전당 인기글 목록 조회
    public List<ArticleListResponseDto> readMainFamePopularArticles() {
        List<Article> articleList = articleRepository.findAllByPopularListOrderByVoteUpCountDesc(true);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 3) break; // 명예의 전당은 인기도 상위 게시글 3개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 명예의 전당 수익왕 목록 조회
    public List<ArticleListResponseDto> readMainFameRichArticles() {
        List<Article> articleList = articleRepository.findAllByRichListOrderByStockReturnDesc(true);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 3) break; // 명예의 전당은 수익률 상위 게시글 3개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 인기글 목록 조회
    public List<ArticleListResponseDto> readMainPopularArticles() {
        List<Article> articleList = articleRepository.findAllByPopularListOrderByCreatedAtDesc(true);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 6) break; // 최신 인기글 6개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 수익왕 목록 조회
    public List<ArticleListResponseDto> readMainRichArticles() {
        List<Article> articleList = articleRepository.findAllByRichListOrderByCreatedAtDesc(true);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 6) break; // 최신 수익왕 6개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 전체 게시판: 게시글 목록 조회 //////////////////// 페이지네이션 적용 필요
    public List<ArticleListResponseDto> readAllArticles() {
        List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 인기글 게시판: 게시글 목록 조회 //////////////////// 페이지네이션 적용 필요
    public List<ArticleListResponseDto> readPopularArticles() {
        List<Article> articleList = articleRepository.findAllByPopularListOrderByCreatedAtDesc(true);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 수익왕 게시판: 게시글 목록 조회 //////////////////// 페이지네이션 적용 필요
    public List<ArticleListResponseDto> readRichArticles() {
        List<Article> articleList = articleRepository.findAllByRichListOrderByCreatedAtDesc(true);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 모아보기 게시판: 게시글 목록 조회 //////////////////// 페이지네이션 적용 필요
    public List<ArticleListResponseDto> readUserArticles(Long userId) {
        List<Article> articleList = articleRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new NullPointerException("유저가 존재하지 않습니다.")
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 게시글: 게시글 내용 조회 (로그인 사용자) //////////////////// 수정 필요
    @Transactional
    public ArticleResponseDto readArticleLoggedIn(User loginUser, Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        User user = userRepository.findById(article.getUserId()).orElseThrow(
                () -> new NullPointerException("유저가 존재하지 않습니다.")
        );
        article.setViewCount(article.getViewCount() + 1); // 게시글 내용 조회 시 조회수 1 증가
        int commentCount = countComment(article);
        int voteSign = checkVoteSign(loginUser, article);
        ArticleResponseDto responseDto = new ArticleResponseDto(article, user, commentCount, voteSign);
        return responseDto;
    }

    // 게시글: 게시글 내용 조회 (비로그인 사용자) //////////////////// 수정 필요
    @Transactional
    public ArticleResponseDto readArticle(Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        User user = userRepository.findById(article.getUserId()).orElseThrow(
                () -> new NullPointerException("유저가 존재하지 않습니다.")
        );
        article.setViewCount(article.getViewCount() + 1); // 게시글 내용 조회 시 조회수 1 증가
        int commentCount = countComment(article);
        int voteSign = 0;
        ArticleResponseDto responseDto = new ArticleResponseDto(article, user, commentCount, voteSign);
        return responseDto;
    }

    // 게시글: 찬성 투표
    @Transactional
    public void voteUp(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        VoteUp oldVote = voteUpRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 찬성 투표를 했는지 여부 확인
        VoteDown oppositeVote = voteDownRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 반대 투표를 했는지 여부 확인
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (oldVote == null) {
            if (loginId == article.getUserId())
                throw new CustomException(ErrorCode.FORBIDDEN_MYARTICLEVOTE); // 본인 게시글에 투표하려는 경우
            else if (oppositeVote != null) {
                VoteUp myVote = new VoteUp(articleId, loginId); // 이미 반대를 한 경우
                voteUpRepository.save(myVote);
                voteDownRepository.delete(oppositeVote);
                article.setVoteUpCount(article.getVoteUpCount() + 1);
                article.setVoteDownCount(article.getVoteDownCount() - 1);
                checkPopularList(article);
            } else {
                VoteUp myVote = new VoteUp(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteUpRepository.save(myVote);
                article.setVoteUpCount(article.getVoteUpCount() + 1);
                checkPopularList(article);
            }
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_OLDVOTEUP); // 이미 찬성을 한 경우
        }
    }

    // 게시글: 반대 투표
    @Transactional
    public void voteDown(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        VoteDown oldVote = voteDownRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 반대 투표를 했는지 여부 확인
        VoteUp oppositeVote = voteUpRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 찬성 투표를 했는지 여부 확인
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (oldVote == null) {
            if (loginId == article.getUserId())
                throw new CustomException(ErrorCode.FORBIDDEN_MYARTICLEVOTE); // 본인 게시글에 투표하려는 경우
            else if (oppositeVote != null) {
                VoteDown myVote = new VoteDown(articleId, loginId); // 이미 찬성을 한 경우
                voteDownRepository.save(myVote);
                voteUpRepository.delete(oppositeVote);
                article.setVoteDownCount(article.getVoteDownCount() + 1);
                article.setVoteUpCount(article.getVoteUpCount() - 1);
                checkPopularList(article);
            } else {
                VoteDown myVote = new VoteDown(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteDownRepository.save(myVote);
                article.setVoteDownCount(article.getVoteDownCount() + 1);
                checkPopularList(article);
            }
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_OLDVOTEDOWN); // 이미 반대를 한 경우
        }
    }

    // 게시글: 게시글 삭제
    @Transactional
    public void deleteArticle(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (loginId != article.getUserId()) throw new CustomException(ErrorCode.UNAUTHORIZED_NOTUSER);

        articleRepository.deleteById(articleId);

        List<VoteUp> voteUpList = voteUpRepository.findAllByArticleId(articleId); // 해당 게시글 찬성 표 삭제
        for (int i = 0; i < voteUpList.size(); i++) voteUpRepository.delete(voteUpList.get(i));

        List<VoteDown> voteDownList = voteDownRepository.findAllByArticleId(articleId); // 해당 게시글 반대 표 삭제
        for (int i = 0; i < voteDownList.size(); i++) voteDownRepository.delete(voteDownList.get(i));

        List<Comment> commentList = commentRepository.findAllByArticleId(articleId); // 해당 게시글 댓글 삭제
        for (int i = 0; i < commentList.size(); i++) commentRepository.delete(commentList.get(i));
    }

    // 게시글 찬성 표 집계
    public int countVoteUp(Article article) {
        List<VoteUp> voteUpList = voteUpRepository.findAllByArticleId(article.getArticleId());
        int voteUpCount = voteUpList.size();
        return voteUpCount;
    }

    // 게시글 반대 표 집계
    public int countVoteDown(Article article) {
        List<VoteDown> voteDownList = voteDownRepository.findAllByArticleId(article.getArticleId());
        int voteDownCount = voteDownList.size();
        return voteDownCount;
    }

    // 게시글 댓글 집계
    public int countComment(Article article) {
        List<Comment> commentList = commentRepository.findAllByArticleId(article.getArticleId());
        int commentCount = commentList.size();
        return commentCount;
    }

    // 게시글 찬성/반대 투표 검사
    public int checkVoteSign(User user, Article article) {
        if (voteUpRepository.findByUserIdAndArticleId(user.getUserId(), article.getArticleId()).isPresent()) return 1;
        else if (voteDownRepository.findByUserIdAndArticleId(user.getUserId(), article.getArticleId()).isPresent())
            return -1;
        else return 0;
    }

    // 인기글 등록/해제 검사
    @Transactional
    public void checkPopularList(Article article) {
        if (article.getVoteUpCount() >= 3 && article.getVoteDownCount() == 0) article.setPopularList(true);
        else if (article.getVoteUpCount() >= 3 && article.getVoteUpCount() / article.getVoteDownCount() >= 2)
            article.setPopularList(true);
        else article.setPopularList(false);
    }

    // 게시글 등록 종목 현재가 및 수익률 업데이트 //////////////////// 스케쥴러 연동 완료
    @Transactional
    public void updateArticle() {
        List<Article> articleList = articleRepository.findAll();
        for (int i = 0; i < articleList.size(); i++) {
            articleList.get(i).setStockPriceLast(stockService.getStockPrice(articleList.get(i).getStockName()));
            articleList.get(i).setStockReturn(stockService.getStockReturn(articleList.get(i).getStockPriceFirst(), articleList.get(i).getStockPriceLast()));
        }
    }

    // 수익왕 등록/해제 검사 //////////////////// 스케쥴러 연동 완료
    @Transactional
    public void checkRichList() {
        List<Article> articleList = articleRepository.findAll();
        for (int i = 0; i < articleList.size(); i++) {
            if (articleList.get(i).getStockReturn() >= 0.15) articleList.get(i).setRichList(true);
            else articleList.get(i).setRichList(false);
        }
    }

    // 게시글 욕설 필터링
    public boolean cleanArticle(ArticleRequestDto requestDto) {
        String articleTitle = requestDto.getArticleTitle();
        String point1 = requestDto.getPoint1();
        String content1 = requestDto.getContent1();
        String point2 = requestDto.getPoint2();
        String content2 = requestDto.getContent2();
        String point3 = requestDto.getPoint3();
        String content3 = requestDto.getContent3();

        // 필터링 대상 욕설
        String[] words = {
                "개걸레", "개보지", "개씨발", "개좆", "개지랄", "걸레년",
                "느검마", "느금", "니기미", "니애미", "니애비", "닝기미",
                "미친년", "미친놈", "미친새끼", "백보지", "보지털", "보짓물", "빠구리",
                "썅년", "썅놈", "씨발", "씹년", "씹보지", "씹새끼", "씹자지", "씹창",
                "잠지털", "잡년", "잡놈", "젓같은", "젖같은", "좆", "창녀", "창년"
        };

        for (String word : words)
            if (articleTitle.contains(word) || point1.contains(word) || content1.contains(word) || point2.contains(word) || content2.contains(word) || point3.contains(word) || content3.contains(word))
                return false;

        return true;
    }
}
