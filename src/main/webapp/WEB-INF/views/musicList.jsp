<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ page import="java.util.List" %>
<%@ page import="com.musichub.model.MusicVO" %>

<%
/*
 * [이서윤 담당]
 * <fmt:message key="music.list.title" /> 페이지 View
 *
 * 역할
 * - MusicListServlet에서 전달받은 <fmt:message key="music.list.title" /> 출력
 * - 검색어 / 장르 필터 / 정렬 조건 화면 유지
 * - 좋아요한 음악 필터 UI 제공
 * - <fmt:message key="music.list.title" />에서 좋아요 Ajax 처리
 *
 * 주의
 * - DB 조회는 JSP에서 직접 하지 않음
 * - 검색 / 필터 / 정렬 / 좋아요 여부 조회는
 *   Servlet → Service → DAO 구조에서 처리
 */

List<MusicVO> musicList =
    (List<MusicVO>)request.getAttribute("musicList");

String keyword =
    (String)request.getAttribute("keyword");

String filter =
    (String)request.getAttribute("filter");

String sort =
    (String)request.getAttribute("sort");

String[] selectedGenres =
    (String[])request.getAttribute("selectedGenres");

if(keyword == null) {
    keyword = "";
}

if(filter == null) {
    filter = "all";
}

if(sort == null) {
    sort = "latest";
}

if(selectedGenres == null) {
    selectedGenres = new String[0];
}
%>

<%!
/*
 * [이서윤 담당]
 * 장르 체크박스 선택 상태 유지용 함수
 *
 * selectedGenres 배열에 현재 genre가 포함되어 있으면 checked 처리한다.
 */
boolean isSelectedGenre(String[] selectedGenres, String genre) {
    if(selectedGenres == null) {
        return false;
    }

    for(String g : selectedGenres) {
        if(g.equals(genre)) {
            return true;
        }
    }

    return false;
}
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
<meta charset="UTF-8">
<title><fmt:message key="music.list.title" /></title>

<%-- [이서윤 담당] 음악 기능 전용 CSS --%>
<link rel="stylesheet"
      href="<%= request.getContextPath() %>/resources/css/recommend.css?v=4">

<%-- 검색 아이콘 사용 --%>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>

<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />


<%-- 검색 실행 시 짧게 표시되는 로딩 오버레이 --%>
<div id="searchOverlay">
    <div class="search-loading-text"><fmt:message key="music.list.searching" /></div>
</div>

<div class="recommend-wrapper recommend-page page-fade" id="pageFade">

    <h1 class="recommend-page-title"><fmt:message key="music.list.title" /></h1>

    <%-- =========================
         검색 영역
         - 제목 / 가수 / 장르 키워드 검색
         - 검색어 초기화
         - 사용자 추천 페이지 이동
         ========================= --%>
    <form action="<%= request.getContextPath() %>/music/list"
          method="get"
          class="recommend-search-area"
          id="searchForm">

        <div class="recommend-search-box">

            <span class="search-icon">
                <i class="fa-solid fa-magnifying-glass"></i>
            </span>

            <input type="text"
                   name="keyword"
                   value="<%= keyword %>"
                   placeholder="<fmt:message key="music.list.search.ph" />">

            <a href="<%= request.getContextPath() %>/music/list"
               class="recommend-clear-btn">
                ×
            </a>

        </div>

        <input type="submit"
               class="mh-btn-pink"
               value="<fmt:message key="music.search" />">

        <a href="<%= request.getContextPath() %>/recommend"
           class="mh-btn-pink">
            <fmt:message key="recommend.title" />
        </a>

    </form>

    <%-- =========================
         필터 / 정렬 영역
         - 전체 음악
         - 좋아요한 음악
         - 장르 필터
         - 정렬 조건
         ========================= --%>
    <div class="recommend-filter-tabs">

        <a href="<%= request.getContextPath() %>/music/list"
           class="mh-filter-tab <%= filter.equals("all") ? "active" : "" %>">
            <fmt:message key="music.filter.all" />
        </a>

        <a href="<%= request.getContextPath() %>/music/list?filter=liked"
           class="mh-filter-tab <%= filter.equals("liked") ? "active" : "" %>">
            <fmt:message key="music.filter.liked" />
        </a>

        <%-- 장르 필터 드롭다운 --%>
        <div class="mh-filter-dropdown">

            <button type="button"
                    class="mh-filter-tab"
                    id="genreBtn">
                <fmt:message key="music.filter.genre" />
            </button>

            <div class="mh-filter-menu"
                 id="genreMenu">

                <label>
                    <input type="checkbox"
                           name="genre"
                           value="KPOP"
                           form="searchForm"
                           <%= isSelectedGenre(selectedGenres, "KPOP") ? "checked" : "" %>>
                    KPOP
                </label>

                <label>
                    <input type="checkbox"
                           name="genre"
                           value="Ballad"
                           form="searchForm"
                           <%= isSelectedGenre(selectedGenres, "Ballad") ? "checked" : "" %>>
                    Ballad
                </label>

                <label>
                    <input type="checkbox"
                           name="genre"
                           value="Pop"
                           form="searchForm"
                           <%= isSelectedGenre(selectedGenres, "Pop") ? "checked" : "" %>>
                    Pop
                </label>

                <label>
                    <input type="checkbox"
                           name="genre"
                           value="HipHop"
                           form="searchForm"
                           <%= isSelectedGenre(selectedGenres, "HipHop") ? "checked" : "" %>>
                    HipHop
                </label>
                
                <label>
				    <input type="checkbox"
				           name="genre"
				           value="Jazz"
				           form="searchForm"
				           <%= isSelectedGenre(selectedGenres, "Jazz") ? "checked" : "" %>>
				    Jazz
				</label>
				
				<label>
				    <input type="checkbox"
				           name="genre"
				           value="Classic"
				           form="searchForm"
				           <%= isSelectedGenre(selectedGenres, "Classic") ? "checked" : "" %>>
				    Classic
				</label>
				
				<label>
				    <input type="checkbox"
				           name="genre"
				           value="Rock"
				           form="searchForm"
				           <%= isSelectedGenre(selectedGenres, "Rock") ? "checked" : "" %>>
				    Rock
				</label>
				
				<label>
				    <input type="checkbox"
				           name="genre"
				           value="Other"
				           form="searchForm"
				           <%= isSelectedGenre(selectedGenres, "Other") ? "checked" : "" %>>
				    Other
				</label>

                <button type="submit"
                        form="searchForm"
                        class="mh-filter-apply-btn">
                    <fmt:message key="music.filter.apply" />
                </button>

            </div>

        </div>

        <%-- 정렬 드롭다운 --%>
        <div class="mh-filter-dropdown">

            <button type="button"
                    class="mh-filter-tab"
                    id="sortBtn">
                <fmt:message key="music.sort.label" />
            </button>

            <div class="mh-filter-menu"
                 id="sortMenu">

                <label>
                    <input type="radio"
                           name="sort"
                           value="latest"
                           form="searchForm"
                           <%= sort.equals("latest") ? "checked" : "" %>>
                    <fmt:message key="music.sort.latest" />
                </label>

                <label>
                    <input type="radio"
                           name="sort"
                           value="view"
                           form="searchForm"
                           <%= sort.equals("view") ? "checked" : "" %>>
                    <fmt:message key="music.sort.view" />
                </label>

                <label>
                    <input type="radio"
                           name="sort"
                           value="like"
                           form="searchForm"
                           <%= sort.equals("like") ? "checked" : "" %>>
                    <fmt:message key="music.sort.like" />
                </label>

                <button type="submit"
                        form="searchForm"
                        class="mh-filter-apply-btn">
                    <fmt:message key="music.filter.apply" />
                </button>

            </div>

        </div>

    </div>

    <br>

    <%-- =========================
         <fmt:message key="music.list.title" /> 출력 영역
         - 음악 클릭 시 상세 페이지 이동
         - 목록에서도 좋아요 상태 표시
         ========================= --%>
    <div class="recommend-music-list">

<%
if(musicList != null && !musicList.isEmpty()) {
    for(MusicVO music : musicList) {
%>

        <div class="recommend-music-row">

            <a href="<%= request.getContextPath() %>/music/detail?music_id=<%= music.getMusicId() %>"
               class="recommend-music-link">

                <div class="recommend-thumb">

<%
String thumbnailPath = music.getThumbnailPath();

/*
 * 썸네일 경로가 있으면 이미지 출력.
 * 이미지가 없거나 로딩 실패 시 기본 음표 아이콘을 출력한다.
 */
if(thumbnailPath != null && !thumbnailPath.trim().equals("")) {
%>
                    <img src="<%= request.getContextPath() %>/<%= thumbnailPath %>"
                         class="recommend-thumbnail"
                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">

                    <span class="recommend-default-note" style="display:none;">
                        ♫
                    </span>
<%
} else {
%>
                    <span>
                        ♫
                    </span>
<%
}
%>

                </div>

                <div class="recommend-music-info">
                    <div class="recommend-title">
                        <%= music.getTitle() %>
                    </div>

                    <div class="recommend-meta">
                        <%= music.getArtist() %>
                        ·
                        <%= music.getGenre() %>
                    </div>
                </div>

            </a>

            <%-- [이서윤 담당] 목록 좋아요 토글 버튼 --%>
            <button type="button"
                    class="recommend-like-btn"
                    data-music-id="<%= music.getMusicId() %>">
                <%= music.isLiked() ? "♥" : "♡" %>
            </button>

        </div>

<%
    }
} else {
%>

        <%-- 검색 결과가 없을 때 표시 --%>
        <div class="empty-result">

            <div class="empty-icon">
                <i class="fa-solid fa-magnifying-glass"></i>
            </div>

            <div class="empty-title">
                <fmt:message key="music.no.result" />
            </div>

            <div class="empty-subtitle">
                <fmt:message key="music.no.result.sub" />
            </div>

        </div>

<%
}
%>

    </div>

</div>

<script>
/*
 * [이서윤 담당]
 * 검색 submit 처리
 *
 * 검색 버튼 클릭 시 바로 이동하지 않고,
 * 짧은 검색 오버레이를 보여준 뒤 form을 제출한다.
 */
const searchForm = document.getElementById("searchForm");
const overlay = document.getElementById("searchOverlay");

if(searchForm){
    searchForm.addEventListener("submit", function(e) {
        e.preventDefault();

        overlay.classList.add("show");

        setTimeout(function() {
            searchForm.submit();
        }, 250);
    });
}

/*
 * [이서윤 담당]
 * 페이지 진입 시 fade-in 효과 적용
 */
window.addEventListener("load", function() {
    document
        .getElementById("pageFade")
        .classList.add("loaded");
});
</script>

<script>
/*
 * [이서윤 담당]
 * 장르 필터 / 정렬 드롭다운 UI 제어
 */
const genreBtn = document.getElementById("genreBtn");
const genreMenu = document.getElementById("genreMenu");

if(genreBtn){
    genreBtn.addEventListener("click", function(e) {
        e.stopPropagation();
        genreMenu.classList.toggle("show");
    });

    document.addEventListener("click", function(e) {
        if(!genreMenu.contains(e.target) && e.target !== genreBtn){
            genreMenu.classList.remove("show");
        }
    });
}

const sortBtn = document.getElementById("sortBtn");
const sortMenu = document.getElementById("sortMenu");

if(sortBtn){
    sortBtn.addEventListener("click", function(e) {
        e.stopPropagation();
        sortMenu.classList.toggle("show");
    });

    document.addEventListener("click", function(e) {
        if(!sortMenu.contains(e.target) && e.target !== sortBtn){
            sortMenu.classList.remove("show");
        }
    });
}
</script>

<script>
/*
 * [이서윤 담당]
 * <fmt:message key="music.list.title" /> 좋아요 토글 Ajax
 *
 * 요청 URL:
 * - /like/toggle
 *
 * 처리 내용:
 * - 좋아요 등록 / 취소
 * - 하트 표시 변경
 * - 처리 결과 토스트 출력
 */
const likeButtons = document.querySelectorAll(".recommend-like-btn");

likeButtons.forEach(function(button) {

    button.addEventListener("click", function(e) {
        e.preventDefault();
        e.stopPropagation();

        const musicId = button.dataset.musicId;

        fetch("<%= request.getContextPath() %>/like/toggle?music_id=" + musicId)
            .then(response => response.text())
            .then(result => {

                result = result.trim();

                if(result === "LOGIN_REQUIRED"){
                    alert("<fmt:message key="js.login.required" />");
                    return;
                }

                const parts = result.split("|");
                const status = parts[0];

                if(status === "LIKED"){
                    button.innerHTML = "♥";
                    showToast("<fmt:message key="music.like.added" />");
                }
                else if(status === "UNLIKED"){
                    button.innerHTML = "♡";
                    showToast("<fmt:message key="music.like.removed" />");
                }
                else{
                    alert("<fmt:message key="music.like.error" />");
                }
            });
    });

});

/*
 * [이서윤 담당]
 * 공통 토스트 메시지
 *
 * 목록 좋아요 처리 결과를 화면 하단에 표시한다.
 */
function showToast(message){
    const toast = document.createElement("div");
    toast.className = "mh-toast-message";
    toast.textContent = message;

    document.body.appendChild(toast);

    setTimeout(function(){
        toast.classList.add("show");
    }, 10);

    setTimeout(function(){
        toast.classList.remove("show");

        setTimeout(function(){
            toast.remove();
        }, 300);
    }, 1600);
}
</script>

</body>
</html>