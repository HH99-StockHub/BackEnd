package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.*;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleResponseDto;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.VoteDownRepository;
import com.sparta.stockhub.repository.VoteUpRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final StockService stockService;
    private final VoteUpRepository voteUpRepository;
    private final VoteDownRepository voteDownRepository;

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

//        Stock stock = stockService.getStockInfo(stockName); // 주식 종목 정보 조회
//        String stockCode = stock.getStockCode();
//        int stockPrice = stock.getStockPrice();

        Article article = new Article(userId, articleTitle, stockName, 0, 0,
        point1, content1, point2, content2, point3, content3);

        articleRepository.save(article);
    }

    // 게시글 내용 조회
    public ArticleResponseDto readArticle(Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        int voteUpCount = countVoteUp(article);
        int voteDownCount = countVoteDown(article);
        ArticleResponseDto responseDto = new ArticleResponseDto(article, voteUpCount, voteDownCount);
        return responseDto;
    }

    // 게시글 찬성 투표
    public void voteUp(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        VoteUp oldVote = voteUpRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 찬성 투표를 했는지 여부 확인
        VoteDown oppositeVote = voteDownRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 반대 투표를 했는지 여부 확인
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (oldVote == null) {
            if (loginId == article.getUserId()) throw new CustomException(ErrorCode.FORBIDDEN_MYARTICLEVOTE); // 본인 게시글에 투표하려는 경우
            else if (oppositeVote != null) {
                VoteUp myVote = new VoteUp(articleId, loginId); // 이미 반대를 한 경우
                voteUpRepository.save(myVote);
                voteDownRepository.delete(oppositeVote);
            }
            else {
                VoteUp myVote = new VoteUp(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteUpRepository.save(myVote);
            }
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_OLDVOTEUP); // 이미 찬성을 한 경우
        }
    }

    // 게시글 반대 투표
    public void voteDown(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        VoteDown oldVote = voteDownRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 반대 투표를 했는지 여부 확인
        VoteUp oppositeVote = voteUpRepository.findByUserIdAndArticleId(loginId, articleId).orElse(null); // 해당 게시글에 찬성 투표를 했는지 여부 확인
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (oldVote == null) {
            if (loginId == article.getUserId()) throw new CustomException(ErrorCode.FORBIDDEN_MYARTICLEVOTE); // 본인 게시글에 투표하려는 경우
            else if (oppositeVote != null) {
                VoteDown myVote = new VoteDown(articleId, loginId); // 이미 찬성을 한 경우
                voteDownRepository.save(myVote);
                voteUpRepository.delete(oppositeVote);
            }
            else {
                VoteDown myVote = new VoteDown(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteDownRepository.save(myVote);
            }
        } else {
            throw new CustomException(ErrorCode.FORBIDDEN_OLDVOTEDOWN); // 이미 반대를 한 경우
        }

    }

    // 게시글 삭제
    public void deleteArticle(UserDetailsImpl userDetails, Long articleId) {
        Long loginId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (loginId != article.getUserId()) throw new CustomException(ErrorCode.UNAUTHORIZED_NOTUSER);

        articleRepository.deleteByArticleId(articleId);
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



}
