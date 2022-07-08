package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.*;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleListResponseDto;
import com.sparta.stockhub.dto.responseDto.ArticleResponseDto;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.VoteDownRepository;
import com.sparta.stockhub.repository.VoteUpRepository;
import com.sparta.stockhub.repository.*;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Lists;

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

    // 게시글: 게시글 내용 조회
    @Transactional
    public ArticleResponseDto readArticle(UserDetailsImpl userDetails, Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        User user = userRepository.findById(article.getUserId()).orElseThrow(
                () -> new NullPointerException("유저가 존재하지 않습니다.")
        );
        article.setViewCount(article.getViewCount() + 1); // 게시글 내용 조회 시 조회수 1 증가
        int commentCount = countComment(article);
        int voteSign = 0;
        if(userDetails != null) voteSign = checkVoteSign(userDetails.getUser(), article);
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
                checkPopularList(article);
            }
            else {
                VoteUp myVote = new VoteUp(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteUpRepository.save(myVote);
                article.setVoteUpCount(article.getVoteUpCount() + 1);
                checkPopularList(article);
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
                checkPopularList(article);
            }
            else {
                VoteDown myVote = new VoteDown(articleId, loginId); // 해당 게시글에 투표를 처음 하는 경우
                voteDownRepository.save(myVote);
                article.setVoteDownCount(article.getVoteDownCount() + 1);
                checkPopularList(article);
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


    public boolean cleanArticle(ArticleRequestDto requestDto) {
        String articleTitle = requestDto.getArticleTitle();
        String stockName = requestDto.getStockName();
        String point1 = requestDto.getPoint1();
        String content1 = requestDto.getContent1();
        String point2 = requestDto.getPoint2();
        String content2 = requestDto.getContent2();
        String point3 = requestDto.getPoint3();
        String content3 = requestDto.getContent3();

        String contents = articleTitle + stockName + point1 + content1 + point2 + content2 + point3 + content3;

        String[] words = {"시발", "병신", "개같이", "멸망", "18년", "18놈", "18새끼", "ㄱㅐㅅㅐㄲl",
                "ㄱㅐㅈㅏ","가슴만져","가슴빨아","가슴빨어","가슴조물락","가슴주물럭",
                "가슴쪼물딱","가슴쪼물락","가슴핧아","가슴핧어","강간","개가튼년","개가튼뇬",
                "개같은년","개걸레","개고치", "개너미", "개넘", "개년", "개놈", "개늠", "개똥",
                "개떵", "개떡", "개라슥", "개보지", "개부달", "개부랄", "개불랄", "개붕알", "개새",
                "개세", "개쓰래기", "개쓰레기", "개씁년", "개씁블", "개씁자지",
                "개씨발", "개씨블", "개자식", "개자지", "개잡년", "개젓가튼넘", "개좆", "개지랄", "개후라년",
                "개후라들놈", "개후라새끼", "걔잡년", "거시기", "걸래년", "걸레같은년", "걸레년", "걸레핀년",
                "게부럴", "게세끼", "게이", "게새끼", "게늠", "게자식", "게지랄놈", "고환", "공지", "공지사항",
                "귀두", "깨쌔끼",

                "난자마셔", "난자먹어", "난자핧아", "내꺼빨아", "내꺼핧아", "내버지",
                "내자지", "내잠지", "내조지", "너거애비", "노옴", "누나강간", "니기미", "니뿡", "니뽕", "니씨브랄",
                "니아범", "니아비", "니애미", "니애뷔", "니애비", "니할애비", "닝기미", "닌기미", "니미", "느금",

                "닳은년", "덜은새끼", "돈새끼", "돌으년", "돌은넘", "돌은새끼", "동생강간", "동성애자", "딸딸이", "똥구녁",
                "똥꾸뇽", "똥구뇽", "똥", "띠발뇬", "띠팔", "띠펄", "띠풀", "띠벌", "띠벨", "띠빌",

                "마스터", "막간년", "막대쑤셔줘", "막대핧아줘", "맛간년", "맛없는년", "맛이간년", "멜리스", "미친구녕", "미친구멍",
                "미친넘", "미친년", "미친놈", "미친눔", "미친새끼", "미친쇄리", "미친쇠리", "미친쉐이", "미친씨부랄",
                "미튄", "미티넘", "미틴", "미틴넘", "미틴년", "미틴놈", "미틴것",

                "백보지", "버따리자지", "버지구녕", "버지구멍", "버지냄새", "버지따먹기", "버지뚫어", "버지뜨더", "버지물마셔", "버지벌려", "버지벌료",
                "버지빨아", "버지빨어", "버지썰어", "버지쑤셔", "버지털", "버지핧아", "버짓물", "버짓물마셔",
                "벌창같은년", "벵신", "병닥", "병딱", "병신", "보쥐", "보지", "보지핧어", "보짓물", "보짓물마셔",
                "봉알", "부랄", "불알", "붕알", "붜지", "뷩딱", "븅쉰", "븅신", "빙띤", "빙신", "빠가십새", "빠가씹새",
                "빠구리", "빠굴이", "뻑큐", "뽕알", "뽀지", "뼝신",

                "사까시", "상년", "새꺄", "새뀌", "새끼", "색갸",
                "색끼", "색스", "색키", "샤발", "서버", "써글", "써글년", "성교", "성폭행", "세꺄", "세끼", "섹스",
                "섹스하자", "섹스해", "섹쓰", "섹히", "수셔", "쑤셔", "쉐끼", "쉑갸", "쉑쓰", "쉬발", "쉬방", "쉬밸년",
                "쉬벌", "쉬불", "쉬붕", "쉬빨", "쉬이발", "쉬이방", "쉬이벌", "쉬이불", "쉬이붕", "쉬이빨", "쉬이팔",
                "쉬이펄", "쉬이풀", "쉬팔", "쉬펄", "쉬풀", "쉽쌔", "시댕이", "시발", "시발년", "시발놈", "시발새끼",
                "시방새", "시밸", "시벌", "시불", "시붕", "시이발", "시이벌", "시이불", "시이붕", "시이팔", "시이펄", "시이풀",
                "시팍새끼", "시팔", "시팔넘", "시팔년", "시팔놈", "시팔새끼", "시펄", "실프", "십8", "십때끼", "십떼끼",
                "십버지", "십부랄", "십부럴", "십새", "십세이", "십셰리", "십쉐", "십자석", "십자슥", "십지랄", "십창녀",
                "십창", "십탱", "십탱구리", "십탱굴이", "십팔새끼", "ㅆㅂ", "ㅆㅂㄹㅁ", "ㅆㅂㄻ", "ㅆㅣ", "쌍넘",
                "쌍년", "쌍놈", "쌍눔", "쌍보지", "쌔끼", "쌔리", "쌕스", "쌕쓰", "썅년", "썅놈", "썅뇬", "썅늠", "쓉새",
                "쓰바새끼", "쓰브랄쉽세", "씌발", "씌팔", "씨가랭넘", "씨가랭년", "씨가랭놈", "씨발", "씨발년",
                "씨발롬", "씨발병신", "씨방새", "씨방세", "씨밸", "씨뱅가리", "씨벌", "씨벌년", "씨벌쉐이", "씨부랄",
                "씨부럴", "씨불", "씨불알", "씨붕", "씨브럴", "씨블", "씨블년", "씨븡새끼", "씨빨", "씨이발", "씨이벌",
                "씨이불", "씨이붕", "씨이팔", "씨파넘", "씨팍새끼", "씨팍세끼", "씨팔", "씨펄", "씨퐁넘", "씨퐁뇬",
                "씨퐁보지", "씨퐁자지", "씹년", "씹물", "씹미랄", "씹버지", "씹보지", "씹부랄", "씹브랄", "씹빵구",
                "씹뽀지", "씹새", "씹새끼", "씹세", "씹쌔끼", "씹자석", "씹자슥", "씹자지", "씹지랄", "씹창", "씹창녀",
                "씹탱", "씹탱굴이", "씹탱이", "씹팔",

                "아가리", "애무", "애미", "애미랄", "애미보지", "애미씨뱅", "애미자지", "애미잡년", "애미좃물", "애비", "애자", "양아치", "어미강간", "어미따먹자", "어미쑤시자",
                "영자", "엄창", "에미", "에비", "엔플레버", "엠플레버", "염병", "염병할", "염뵹", "엿먹어라", "오랄",
                "오르가즘", "왕버지", "왕자지", "왕잠지", "왕털버지", "왕털보지", "왕털자지", "왕털잠지", "우미쑤셔",
                "운디네", "운영자", "유두", "유두빨어", "유두핧어", "유방", "유방만져", "유방빨아", "유방주물럭",
                "유방쪼물딱", "유방쪼물럭", "유방핧아", "유방핧어", "육갑", "이그니스", "이년", "이프리트",

                "자기핧아", "자지", "자지구녕", "자지구멍", "자지꽂아", "자지넣자", "자지뜨더", "자지뜯어", "자지박어", "자지빨아",
                "자지빨아줘", "자지빨어", "자지쑤셔", "자지쓰레기", "자지정개", "자지짤라", "자지털", "자지핧아",
                "자지핧아줘", "자지핧어", "작은보지", "잠지", "잠지뚫어", "잠지물마셔", "잠지털", "잠짓물마셔",
                "잡년", "잡놈", "저년", "점물", "젓가튼", "젓가튼쉐이", "젓같내", "젓같은", "젓까", "젓나", "젓냄새",
                "젓대가리", "젓떠", "젓마무리", "젓만이", "젓물", "젓물냄새", "젓밥", "정액마셔", "정액먹어",
                "정액발사", "정액짜", "정액핧아", "정자마셔", "정자먹어", "정자핧아", "젖같은",
                "젖까", "젖밥", "젖탱이", "조개넓은년", "조개따조", "조개마셔줘", "조개벌려조", "조개속물", "조개쑤셔줘",
                "조개핧아줘", "조까", "조또", "족같내", "족까", "족까내", "존나", "존나게", "존니", "졸라", "좀마니",
                "좀물", "좀쓰레기", "좁빠라라", "좃가튼뇬", "좃간년", "좃까", "좃까리", "좃깟네", "좃냄새", "좃넘",
                "좃대가리", "좃도", "좃또", "좃만아", "좃만이", "좃만한것", "좃만한쉐이", "좃물" , "좃물냄새",
                "좃보지", "좃부랄", "좃빠구리", "좃빠네", "좃빠라라", "좃털", "좆같은놈", "좆같은새끼", "좆까",
                "좆까라", "좆나", "좆년", "좆도", "좆만아", "좆만한년", "좆만한놈", "좆만한새끼", "좆먹어", "좆물",
                "좆밥", "좆빨아", "좆새끼", "좆털", "좋만한것", "주글년", "주길년", "쥐랄", "지랄", "지랼", "지럴", "지뢀",
                "쪼까튼", "쪼다", "쪼다새끼", "찌랄", "찌질이",

                "창남", "창녀", "창녀버지", "창년", "처먹고", "처먹을", "쳐먹고", "쳐쑤셔박어", "촌씨브라리", "촌씨브랑이", "촌씨브랭이",

                "크리토리스", "큰보지", "클리토리스",

                "트랜스젠더",

                "페니스",

                "항문수셔", "항문쑤셔", "허덥",
                "허버리년", "허벌년", "허벌보지", "허벌자식", "허벌자지", "허접", "허젚", "허졉", "허좁", "헐렁보지", "혀로보지핧기",
                "호냥년", "호로", "호로새끼", "호로자슥", "호로자식", "호로짜식", "호루자슥", "호모", "호졉", "호좁", "후라덜넘",
                "후장", "후장꽂아", "후장뚫어", "흐접", "흐젚", "흐졉",

                "bitch", "fuck", "fuckyou", "nflavor", "penis", "motherfuck"};
        //이 욕들이 지금은 여기에 있지만 멘토링 때는 Redis 에 넣기를 추천 하셨고 나중에 본격적으로 사용 할 데이터베이스에 넣을 것 같음
        for (String word : words) {
            if (contents.contains(word)) {
                return false;
            }
        }return true;
    }

    public List<Article> searchArticle(String keywords, Long searchtype) {
        /*
        0 + 전체검색
        1 = 제목 ArticleTitle
        2 = 종목이름 stockName
        3 = 투자 포인트 point
        4 = 내용 content
        */

        String[] keywordsSplitted = keywords.split(" ");
        Set<Article> containingAnyKeywordsArticleSet = new HashSet<>();

        if (searchtype == 0) {

            for (String keyword : keywordsSplitted) {
                System.out.println(keyword);

                List<Article> containingKeywordArticleList = articleRepository.findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3Containing(keyword, keyword, keyword, keyword, keyword, keyword, keyword, keyword);
                containingAnyKeywordsArticleSet.addAll(containingKeywordArticleList);


            }
            List<Article> resultList = Lists.newArrayList(containingAnyKeywordsArticleSet);
            return resultList;
        } else if (searchtype == 1) {

            for (String keyword : keywordsSplitted) {
                System.out.println(keyword);

                List<Article> containingKeywordArticleList = articleRepository.findAllByArticleTitleContaining(keyword);
                containingAnyKeywordsArticleSet.addAll(containingKeywordArticleList);


            }
            List<Article> resultList = Lists.newArrayList(containingAnyKeywordsArticleSet);
            return resultList;


        } else if (searchtype == 2) {

            for (String keyword : keywordsSplitted) {
                System.out.println(keyword);

                List<Article> containingKeywordArticleList = articleRepository.findAllByStockNameContaining(keyword);
                containingAnyKeywordsArticleSet.addAll(containingKeywordArticleList);


            }
            List<Article> resultList = Lists.newArrayList(containingAnyKeywordsArticleSet);
            return resultList;


        } else if (searchtype == 3) {

            for (String keyword : keywordsSplitted) {
                System.out.println(keyword);

                List<Article> containingKeywordArticleList = articleRepository.findAllByPoint1ContainingOrPoint2ContainingOrPoint3Containing(keyword, keyword, keyword);
                containingAnyKeywordsArticleSet.addAll(containingKeywordArticleList);


            }
            List<Article> resultList = Lists.newArrayList(containingAnyKeywordsArticleSet);
            return resultList;

        }
        for (String keyword : keywordsSplitted) {
            System.out.println(keyword);

            List<Article> containingKeywordArticleList = articleRepository.findAllByContent1ContainingOrContent2ContainingOrContent3Containing(keyword, keyword, keyword);
            containingAnyKeywordsArticleSet.addAll(containingKeywordArticleList);


        }
        List<Article> resultList = Lists.newArrayList(containingAnyKeywordsArticleSet);
        return resultList;
    }

    // 게시글 찬성/반대 투표 검사
    public int checkVoteSign(User user, Article article) {
        if (voteUpRepository.findByUserIdAndArticleId(user.getUserId(), article.getArticleId()).isPresent()) return 1;
        else if (voteDownRepository.findByUserIdAndArticleId(user.getUserId(), article.getArticleId()).isPresent()) return -1;
        else return 0;
    }

    // 인기글 등록/해제 검사
    @Transactional
    public void checkPopularList(Article article) {
        if (article.getVoteUpCount() >= 3 && article.getVoteDownCount() == 0) article.setPopularList(true);
        else if (article.getVoteUpCount() >= 3 && article.getVoteUpCount() / article.getVoteDownCount() >= 2) article.setPopularList(true);
        else article.setPopularList(false);
    }

    // 게시글 등록 종목 현재가 및 수익률 업데이트 //////////////////// 스케쥴러 적용 필요
    @Transactional
    public void updateArticle() {
        List<Article> articleList = articleRepository.findAll();
        for (int i = 0; i < articleList.size(); i++) {
            articleList.get(i).setStockPriceLast(stockService.getStockPrice(articleList.get(i).getStockName()));
            articleList.get(i).setStockReturn(stockService.getStockReturn(articleList.get(i).getStockPriceFirst(), articleList.get(i).getStockPriceLast()));
        }
    }

    // 수익왕 등록/해제 검사 //////////////////// 스케쥴러 적용 필요
    @Transactional
    public void checkRichList(Article article) {
        if (article.getStockReturn() >= 0.15) article.setRichList(true);
        else article.setRichList(false);

    }
}
