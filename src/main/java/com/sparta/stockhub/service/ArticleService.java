package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.*;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleListResponseDto;
import com.sparta.stockhub.dto.responseDto.ArticleResponseDto;
import com.sparta.stockhub.repository.*;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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

        if (articleTitle.equals("")) throw new IllegalArgumentException("제목 작성이 필요합니다.");
        if (stockName.equals(null)) throw new IllegalArgumentException("종목 선택이 필요합니다.");
        if (point1.equals("")) throw new IllegalArgumentException("투자포인트 작성이 필요합니다.");
        if (content1.equals("")) throw new IllegalArgumentException("세부 내용 작성이 필요합니다.");
        if (articleTitle.length() > 40) throw new IllegalArgumentException("제목은 40자 이내로 작성해주세요.");
        if (point1.length() > 40) throw new IllegalArgumentException("투자포인트는 40자 이내로 작성해주세요.");
        if (content1.length() > 800) throw new IllegalArgumentException("세부 내용은 800자 이내로 작성해주세요.");
        if (point2 != null && content2 != null) {
            if (point2.length() > 40) throw new IllegalArgumentException("투자포인트는 40자 이내로 작성해주세요.");
            if (content2.length() > 800) throw new IllegalArgumentException("세부 내용은 800자 이내로 작성해주세요.");
        }
        if (point3 != null && content3 != null) {
            if (point3.length() > 40) throw new IllegalArgumentException("투자포인트는 40자 이내로 작성해주세요.");
            if (content3.length() > 800) throw new IllegalArgumentException("세부 내용은 800자 이내로 작성해주세요.");
        }

        int stockPriceFirst = stockService.getStockPrice(stockName);
        int stockPriceLast = stockService.getStockPrice(stockName);
        double stockReturn = stockService.getStockReturn(stockPriceFirst, stockPriceLast);

        Article article = new Article(userId, articleTitle, stockName, stockPriceFirst, stockPriceLast, stockReturn,
        point1, content1, point2, content2, point3, content3);

        articleRepository.save(article);
    }

    // 메인: 전체 게시글 목록 조회
    public List<ArticleListResponseDto> readMainArticles() {
        List<Article> articleList = articleRepository.findAllOrderByCreatedAtDesc();
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
        List<Article> articleList = articleRepository.findAllOrderByCreatedAtDesc();
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

    // 게시글: 게시글 내용 조회
    public ArticleResponseDto readArticle(Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        User user = userRepository.findById(article.getUserId()).orElseThrow(
                () -> new NullPointerException("유저가 존재하지 않습니다.")
        );
        int commentCount = countComment(article);
        ArticleResponseDto responseDto = new ArticleResponseDto(article, user, commentCount);
        return responseDto;
    }

    // 게시글: 찬성 투표
    @Transactional
    public void voteUp(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        VoteUp oldVote = voteUpRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 찬성 투표를 했는지 여부 확인
        VoteDown oppositeVote = voteDownRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 반대 투표를 했는지 여부 확인
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        if (oldVote == null) {
            if (loginId == article.getUserId()) throw new IllegalArgumentException("본인 게시글에 투표할 수 없습니다."); // 본인 게시글에 투표하려는 경우
            else if (oppositeVote != null) {
                VoteUp myVote = new VoteUp(articleId, loginId); // 이미 반대를 한 경우
                voteUpRepository.save(myVote);
                voteDownRepository.delete(oppositeVote);
                article.setVoteUpCount(article.getVoteUpCount() + 1);
                article.setVoteDownCount(article.getVoteDownCount() - 1);
            }
            else {
                VoteUp myVote = new VoteUp(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteUpRepository.save(myVote);
                article.setVoteUpCount(article.getVoteUpCount() + 1);
            }
        } else {
            throw new IllegalArgumentException("이미 찬성 투표를 하였습니다."); // 이미 찬성을 한 경우
        }
    }

    // 게시글: 반대 투표
    @Transactional
    public void voteDown(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        VoteDown oldVote = voteDownRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 반대 투표를 했는지 여부 확인
        VoteUp oppositeVote = voteUpRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 찬성 투표를 했는지 여부 확인
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        if (oldVote == null) {
            if (loginId == article.getUserId()) throw new IllegalArgumentException("본인 게시글에 투표할 수 없습니다."); // 본인 게시글에 투표하려는 경우
            else if (oppositeVote != null) {
                VoteDown myVote = new VoteDown(articleId, loginId); // 이미 찬성을 한 경우
                voteDownRepository.save(myVote);
                voteUpRepository.delete(oppositeVote);
                article.setVoteDownCount(article.getVoteDownCount() + 1);
                article.setVoteUpCount(article.getVoteUpCount() - 1);
            }
            else {
                VoteDown myVote = new VoteDown(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteDownRepository.save(myVote);
                article.setVoteDownCount(article.getVoteDownCount() + 1);
            }
        } else {
            throw new IllegalArgumentException("이미 반대 푸툐를 하였습니다."); // 이미 반대를 한 경우
        }
    }

    // 게시글: 게시글 삭제
    @Transactional
    public void deleteArticle(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        if (loginId != article.getUserId()) throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");

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

    // 인기글 등록/해제 검사
    @Transactional
    public void checkPopularList(Article article) {
        if (article.getVoteUpCount() >= 5 && article.getVoteUpCount() / article.getVoteDownCount() >= 2) article.setPopularList(true);
        else article.setPopularList(false);
        articleRepository.save(article);
    }

    // 게시글 등록 종목 현재가 및 수익률 업데이트 //////////////////// 스케쥴러 적용 필요
    @Transactional
    public void updateArticle() {
        List<Article> articleList = articleRepository.findAll();
        for (int i = 0; i < articleList.size(); i++) {
            articleList.get(i).setStockPriceLast(stockService.getStockPrice(articleList.get(i).getStockName()));
            articleList.get(i).setStockReturn(stockService.getStockReturn(articleList.get(i).getStockPriceFirst(), articleList.get(i).getStockPriceLast()));
            articleRepository.save(articleList.get(i));
        }
    }

    // 수익왕 등록/해제 검사 //////////////////// 스케쥴러 적용 필요
    @Transactional
    public void checkRichList(Article article) {
        if (article.getStockReturn() >= 0.15) article.setRichList(true);
        else article.setRichList(false);
        articleRepository.save(article);
    }
}
