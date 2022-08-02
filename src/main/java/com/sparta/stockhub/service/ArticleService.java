package com.sparta.stockhub.service;

import com.sparta.stockhub.chat.NotificationService;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final VoteUpRepository voteUpRepository;
    private final VoteDownRepository voteDownRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StockService stockService;
    private final UserService userService;

    private final NotificationService notificationService;

    // 게시글 작성
    @Transactional
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
        if (point1.length() > 60) throw new CustomException(ErrorCode.BAD_REQUEST_POINTLENGTH);
        if (content1.length() > 2000) throw new CustomException(ErrorCode.BAD_REQUEST_CONTENTLENGTH);
        if (point2 != null && content2 != null) {
            if (point2.length() > 60) throw new CustomException(ErrorCode.BAD_REQUEST_POINTLENGTH);
            if (content2.length() > 2000) throw new CustomException(ErrorCode.BAD_REQUEST_CONTENTLENGTH);
        }
        if (point3 != null && content3 != null) {
            if (point3.length() > 60) throw new CustomException(ErrorCode.BAD_REQUEST_POINTLENGTH);
            if (content3.length() > 2000) throw new CustomException(ErrorCode.BAD_REQUEST_CONTENTLENGTH);
        }

        int stockPriceFirst = stockService.getStockPrice(stockName);
        int stockPriceLast = stockService.getStockPrice(stockName);
        double stockReturn = stockService.getStockReturn(stockPriceFirst, stockPriceLast);

        LocalDateTime deadline = LocalDateTime.now(); // 수익률 추적 데드라인 세팅
        String timeLimit = requestDto.getTimeLimit();
        if (timeLimit.equals("2주")) deadline = deadline.plusWeeks(2);
        else if (timeLimit.equals("1개월")) deadline = deadline.plusMonths(1);
        else if (timeLimit.equals("3개월")) deadline = deadline.plusMonths(3);
        else if (timeLimit.equals("6개월")) deadline = deadline.plusMonths(6);
        else if (timeLimit.equals("1년")) deadline = deadline.plusYears(1);
        else if (timeLimit.equals("2년")) deadline = deadline.plusYears(2);
        else if (timeLimit.equals("3년")) deadline = deadline.plusYears(3);

        Article article = new Article(userId, requestDto, deadline, stockPriceFirst, stockPriceLast, stockReturn);

        stockService.registerStock(stockName);

        articleRepository.save(article);

        User user = userDetails.getUser(); // 경험치 30점 획득
        user.setExpPoint(user.getExpPoint() + 30);
        userRepository.save(user);
        userService.updateRank(user);
    }

    // 게시글 검색
//    public Page<ArticleListResponseDto> searchArticle(String keywords, int page, int size) {
//        String keywordsTrimmed = keywords.trim();
//        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
//
//        // A. 검색어에 공백이 없는 경우
//        if (!keywordsTrimmed.contains(" ")) {
//            List<Article> articleList = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed);
//
//            for (int i = 0; i < articleList.size(); i++) {
//                User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
//                        () -> new CustomException(ErrorCode.NOT_FOUND_USER)
//                );
//                int commentCount = countComment(articleList.get(i));
//
//                responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
//            }
//        }
//        // B. 검색어에 공백이 있는 경우
//        else {
//            String[] keywordsSplitted = keywordsTrimmed.split(" ");
//
//            // 1. 분리된 검색어로 게시글 검색
//            for (String keyword : keywordsSplitted) {
//                List<Article> articleList = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(keyword, keyword, keyword, keyword, keyword, keyword, keyword, keyword);
//
//                for (int i = 0; i < articleList.size(); i++) {
//                    if (keyword.equals("")) continue; // 공백이 2번 이상 연속된 경우 생기는 검색어 skip
//
//                    User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
//                            () -> new CustomException(ErrorCode.NOT_FOUND_USER)
//                    );
//                    int commentCount = countComment(articleList.get(i));
//
//                    responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
//                }
//            }
//
//            // 2. 중복 검색 결과 제거
//            for (int i = 0; i < responseDtoList.size(); i++) {
//                for (int j = i + 1; j < responseDtoList.size(); j++) {
//                    if (responseDtoList.get(i).getUserId().equals(responseDtoList.get(j).getUserId()) &&
//                            responseDtoList.get(i).getCreatedAt() == responseDtoList.get(j).getCreatedAt())
//                        responseDtoList.remove(i);
//                }
//            }
//
//            // 3. 게시글 제목이 분리 전 검색어를 통째로 포함하는 경우 해당 게시글을 검색 결과의 상단으로 이동
//            for (int i = 0; i < responseDtoList.size(); i++) {
//                if (responseDtoList.get(i).getArticleTitle().contains(keywordsTrimmed)) {
//                    responseDtoList.add(0, responseDtoList.get(i));
//                    responseDtoList.remove(i + 1);
//                }
//            }
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        final int start = (int) pageable.getOffset();
//        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
//        final Page<ArticleListResponseDto> resultpage = new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());
//
//        return resultpage;
//    }

    //--------------------------------//
    //게시글 검색 업그레이드

    public Page<ArticleListResponseDto> searchArticle(String keywords, int page, int size) {

        String keywordsTrimmed = keywords.trim();
        System.out.println(keywordsTrimmed);
        List<Article> responseList = new ArrayList<>();
        List<Article> keywordsSplitList = new ArrayList<>();
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();

        // 1순위 검색어가 그대로 제목이나 종목명에 포함된 게시글
        List<Article> articleList1 = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrderByCreatedAtDesc(keywordsTrimmed, keywordsTrimmed);
        responseList.addAll(articleList1);


        // 2순위 검색어가 그대로 투자포인트나 내용에 포함된 게시글
        List<Article> articleList2 = articleRepository.findAllByPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed, keywordsTrimmed);
        responseList.addAll(articleList2);

        if (keywordsTrimmed.contains(" ")) {
            String[] keywordsSplitted = keywordsTrimmed.split(" ");

            for (String keyword : keywordsSplitted) {

                System.out.println(keyword);

                if (keyword.equals("")) continue;

                List<Article> articleList3 = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(keyword, keyword, keyword, keyword, keyword, keyword, keyword, keyword);
                keywordsSplitList.addAll(articleList3);

            }

            List<Long> articleIdList = new ArrayList<>();
            Integer count=0;
            for (String keyword : keywordsSplitted){
                if(keyword!="") count+=1;
            }

            System.out.println(count);

            for(int i = 0; i < keywordsSplitList.size(); i ++){
                articleIdList.add(keywordsSplitList.get(i).getArticleId());
            }

            Stream<Long> longStream = articleIdList.stream();

            Map<Long, Integer> map = longStream.collect(
                    Collectors.toMap(Function.identity(), value -> 1, Integer::sum)
            );

            System.out.println("print map  " + map);

            List<Long> newList = articleIdList.stream().distinct().collect(Collectors.toList());

            System.out.println("print newList " + newList);

            for(int i = 0; i < newList.size(); i ++) {
                if (map.get(newList.get(i)) == count) {
                    List<Article> article = articleRepository.findAllByArticleId(newList.get(i));
                    responseList.addAll(article);
                }
            }

            for(int i = 0; i < newList.size(); i ++) {
                if (map.get(newList.get(i)) == count-1) {
                    List<Article> article = articleRepository.findAllByArticleId(newList.get(i));
                    responseList.addAll(article);
                }
            }

        }

        List<Article> resultList = responseList.stream().distinct().collect(Collectors.toList());

        for (int i = 0; i < resultList.size(); i++) {
                User user = userRepository.findById(resultList.get(i).getUserId()).orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_USER)
                );
                int commentCount = countComment(resultList.get(i));

                responseDtoList.add(new ArticleListResponseDto(resultList.get(i), user, commentCount));
            }

                Pageable pageable = PageRequest.of(page, size);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        final Page<ArticleListResponseDto> resultpage = new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());

        return resultpage;
    }

    // 메인: 전체 게시글 목록 조회
    public List<ArticleListResponseDto> readMainArticles() {
        List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 9) break; // 메인 페이지에 내릴 때는 최신 게시글 9개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 명예의 전당 인기글 목록 조회
    public List<ArticleListResponseDto> readMainFamePopularArticles() {
        List<Article> articleList = articleRepository.findAllByPopularListOrderByVoteUpCountDesc(1);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 3) break; // 명예의 전당은 인기도 상위 게시글 3개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 명예의 전당 수익왕 목록 조회
    public List<ArticleListResponseDto> readMainFameRichArticles() {
        List<Article> articleList = articleRepository.findAllByRichListOrderByStockReturnDesc(1);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 3) break; // 명예의 전당은 수익률 상위 게시글 3개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 인기글 목록 조회
    public List<ArticleListResponseDto> readMainPopularArticles() {
        List<Article> articleList = articleRepository.findAllByPopularListOrderByPopularRegTimeDesc(1);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 6) break; // 최신 인기글 6개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 메인: 수익왕 목록 조회
    public List<ArticleListResponseDto> readMainRichArticles() {
        List<Article> articleList = articleRepository.findAllByRichListOrderByRichRegTimeDesc(1);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (i == 6) break; // 최신 수익왕 6개만 반환
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        return responseDtoList;
    }

    // 전체 게시판: 게시글 목록 조회
    public Page<ArticleListResponseDto> readAllArticles(int page, int size) {
        List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        Pageable pageable = PageRequest.of(page, size);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        final Page<ArticleListResponseDto> resultpage = new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());
        return resultpage;
    }

    // 인기글 게시판: 게시글 목록 조회
    public Page<ArticleListResponseDto> readPopularArticles(int page, int size) {
        List<Article> articleList = articleRepository.findAllByPopularListOrderByPopularRegTimeDesc(1);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }

        Pageable pageable = PageRequest.of(page, size);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        final Page<ArticleListResponseDto> resultpage = new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());

        return resultpage;
    }

    // 수익왕 게시판: 게시글 목록 조회
    public Page<ArticleListResponseDto> readRichArticles(int page, int size) {
        List<Article> articleList = articleRepository.findAllByRichListOrderByRichRegTimeDesc(1);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        Pageable pageable = PageRequest.of(page, size);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        final Page<ArticleListResponseDto> resultpage = new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());

        return resultpage;
    }

    // 모아보기 게시판: 게시글 목록 조회
    public Page<ArticleListResponseDto> readUserArticles(Long userId, int page, int size) {
        List<Article> articleList = articleRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<ArticleListResponseDto> responseDtoList = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            User user = userRepository.findById(articleList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            int commentCount = countComment(articleList.get(i));
            responseDtoList.add(new ArticleListResponseDto(articleList.get(i), user, commentCount));
        }
        Pageable pageable = PageRequest.of(page, size);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        final Page<ArticleListResponseDto> resultpage = new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());

        return resultpage;
    }

    // 게시글: 게시글 내용 조회
    @Transactional
    public ArticleResponseDto readArticle(Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        User user = userRepository.findById(article.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        article.setViewCount(article.getViewCount() + 1); // 게시글 내용 조회 시 조회수 1 증가
        int commentCount = countComment(article);
        ArticleResponseDto responseDto = new ArticleResponseDto(article, user, commentCount);
        return responseDto;
    }

    // 게시글: 로그인 사용자 투표 검사
    public int checkVoteSign(User user, Long articleId) {
        if (voteUpRepository.findByUserIdAndArticleId(user.getUserId(), articleId).isPresent()) return 1;
        else if (voteDownRepository.findByUserIdAndArticleId(user.getUserId(), articleId).isPresent())
            return -1;
        else return 0;
    }

    // 게시글: 찬성 투표
    @Transactional
    public void voteUp(UserDetailsImpl userDetails, Long articleId) {

        Long loginId = userDetails.getUser().getUserId();
        String loginNickname = userDetails.getUser().getNickname();
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
                checkPopularList(article, loginNickname);
            } else {
                VoteUp myVote = new VoteUp(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteUpRepository.save(myVote);
                article.setVoteUpCount(article.getVoteUpCount() + 1);
                checkPopularList(article, loginNickname);
            }
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_OLDVOTEUP); // 이미 찬성을 한 경우
        }
    }

    // 게시글: 반대 투표
    @Transactional
    public void voteDown(UserDetailsImpl userDetails, Long articleId) {

        Long loginId = userDetails.getUser().getUserId();
        String loginNickname = userDetails.getUser().getNickname();
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
                checkPopularList(article, loginNickname);
            } else {
                VoteDown myVote = new VoteDown(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteDownRepository.save(myVote);
                article.setVoteDownCount(article.getVoteDownCount() + 1);
                checkPopularList(article, loginNickname);
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

        User user = userDetails.getUser(); // 경험치 30점 감소
        user.setExpPoint(user.getExpPoint() - 30);
        userRepository.save(user);
        userService.updateRank(user);
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
    public void checkPopularList(Article article, String loginNickname) {

        long userId = article.getUserId();
        int preCheck = article.getPopularList();

        if (article.getVoteUpCount() >= 3 && article.getVoteDownCount() == 0)
            article.setPopularList(1);
        else if (article.getVoteUpCount() >= 3 && article.getVoteUpCount() / article.getVoteDownCount() >= 2)
            article.setPopularList(1);
        else article.setPopularList(0);

        int postCheck = article.getPopularList();

        if (preCheck == 0 && postCheck == 1) { // 경험치 50점 획득
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            user.setExpPoint(user.getExpPoint() + 50);
            userService.updateRank(user);
            article.setPopularRegTime(LocalDateTime.now());

            Long articleUserId = article.getUserId();
            String userNickname = loginNickname;

            notificationService.sendPrivateNotificationLike(userNickname, articleUserId, article.getArticleId());
        }
        if (preCheck == 1 && postCheck == 0) { // 경험치 50점 감소
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            user.setExpPoint(user.getExpPoint() - 50);
            userService.updateRank(user);
        }
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
            int preCheck = articleList.get(i).getRichList();
            if (articleList.get(i).getStockReturn() >= 5) articleList.get(i).setRichList(1);
            else articleList.get(i).setRichList(0);
            int postCheck = articleList.get(i).getRichList();
            if (preCheck == 0 && postCheck == 1) {
                articleList.get(i).setRichRegTime(LocalDateTime.now());

                Long articleUserId = articleList.get(i).getUserId();
                Long articleId = articleList.get(i).getArticleId();
                notificationService.sendPrivateNotificationRich(articleUserId, articleId);
            }
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