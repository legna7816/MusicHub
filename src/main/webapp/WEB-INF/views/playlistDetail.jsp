<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="com.musichub.model.PlaylistVO" %>
<%@ page import="com.musichub.model.MusicVO" %>

<%
/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 * 플레이리스트 상세 페이지 View
 *
 * 역할
 * - PlaylistDetailServlet에서 전달받은 playlist 출력
 * - 상단: 플레이리스트 대표 썸네일, 제목, 사용자 닉네임
 * - 하단: 해당 플레이리스트에 저장된 음악 목록 출력
 *
 * 주의
 * - DB 조회는 JSP에서 직접 수행하지 않음
 * - Servlet → Service → DAO → DB 흐름으로 받은 데이터만 출력
 * =========================================
 */

PlaylistVO playlist =
    (PlaylistVO)request.getAttribute("playlist");

List<String> thumbnailList =
    playlist.getThumbnailList();

List<MusicVO> musicList =
    playlist.getMusicList();

String nickname =
    (String)session.getAttribute("loginNickname");

if(nickname == null){
    nickname = "사용자";
}
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
<meta charset="UTF-8">
<title><%= playlist.getPlaylistName() %></title>

<style>
body {
    margin: 0;
    background: #0d0d0d;
    color: #e0e0e0;
    font-family: 'Segoe UI', "Malgun Gothic", sans-serif;
}

.playlist-detail-wrapper {
    max-width: 760px;
    margin: 0 auto;
    padding: 40px 16px;
}

.back-btn {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    border: none;
    background: #1a1a1a;
    color: #e0e0e0;
    font-size: 30px;
    cursor: pointer;
}

.playlist-hero {
    display: flex;
    align-items: center;
    gap: 28px;
    margin-top: 36px;
    margin-bottom: 48px;

    /*
     * [이서윤 담당 - 플레이리스트 확장]
     * 긴 제목 때문에 상세 화면 전체에 가로 스크롤이 생기지 않도록 처리
     */
    min-width: 0;
}

.playlist-cover-large {
    width: 220px;
    height: 220px;
    border-radius: 12px;
    overflow: hidden;
    background: #2a2a2a;
    display: flex;
    align-items: center;
    justify-content: center;
}

.playlist-cover-grid {
    width: 100%;
    height: 100%;

    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, 1fr);

    overflow: hidden;
}

.playlist-cover-img,
.playlist-cover-default-small {
    width: 100%;
    height: 100%;
    aspect-ratio: 1 / 1;
    box-sizing: border-box;
}

.playlist-cover-img {
    object-fit: cover;
    display: block;
}

.playlist-cover-single {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

.playlist-default-note {
    font-size: 90px;
    color: #e05c97;
}

.playlist-cover-default-small {
    width: 100%;
    height: 100%;
    background: #2a2a2a;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #e05c97;
    font-size: 44px;
}

.playlist-info {
    flex: 1;
    min-width: 0;
}

.playlist-info h1 {
    font-size: 32px;
    color: #fff;
    margin: 0 0 12px;

    /*
     * [이서윤 담당 - 플레이리스트 확장]
     * 플레이리스트 제목이 너무 길 때 상세 화면 UI가 깨지지 않도록 처리
     *
     * - 가로 스크롤 방지
     * - 여러 줄로 자동 줄바꿈
     * - 대표 이미지 높이를 넘을 정도로 길면 말줄임표 처리
     */
    max-width: 850px;
    max-height: 260px;

    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 6;
    -webkit-box-orient: vertical;

    word-break: break-all;
    line-height: 1.25;
}

.playlist-info p {
    font-size: 24px;
    margin: 0;
    color: #aaa;
}

.music-row {
    display: grid;
    grid-template-columns: 72px 1.5fr 1fr 1fr 60px;
    align-items: center;
    gap: 28px;
    padding: 14px 0;
    border-top: 1px solid #333;
    color: inherit;
}

.music-row-link {
    display: contents;
    text-decoration: none;
    color: inherit;
}

.music-delete-btn {
    border: none;
    background: transparent;
    font-size: 22px;
    cursor: pointer;
    color: #777;
}

.music-delete-btn:hover {
    color: #e05c97;
}

.music-row:hover {
    background: #1a1a1a;
}

.music-thumb {
    width: 58px;
    height: 58px;
    border-radius: 6px;
    object-fit: cover;
    background: #2a2a2a;
}

.music-default-thumb {
    width: 58px;
    height: 58px;
    border-radius: 6px;
    background: #2a2a2a;
    color: #e05c97;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
}

.music-title {
    font-size: 18px;
    font-weight: bold;
}

.music-meta {
    font-size: 16px;
    color: #888;
}

.empty-text {
    padding: 40px 0;
    color: #888;
    font-size: 18px;
}
</style>
</head>

<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />


<div class="playlist-detail-wrapper">

    <button type="button" class="back-btn" onclick="history.back();">
        ‹
    </button>

    <div class="playlist-hero">

        <%--
            =========================================
            [이서윤 담당 - 플레이리스트 확장]
            상세 페이지 대표 썸네일 출력

            규칙
            - 썸네일 4개 이상: 4분할 출력
            - 썸네일 1~3개: 첫 번째 썸네일 크게 출력
            - 썸네일 없음: 기본 음표 출력
            =========================================
        --%>
        <div class="playlist-cover-large">

<%
if(thumbnailList != null && !thumbnailList.isEmpty()){

    if(thumbnailList.size() >= 4){
%>

            <div class="playlist-cover-grid">

<%
        for(int i = 0; i < 4; i++){
            String thumb = thumbnailList.get(i);
%>

                <% if(thumb != null && !thumb.trim().equals("")){ %>
                    <img src="<%= request.getContextPath() %>/<%= thumb %>"
                         class="playlist-cover-img"
                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                    <span class="playlist-cover-default-small" style="display:none;">♫</span>
                <% }else{ %>
                    <span class="playlist-cover-default-small">♫</span>
                <% } %>

<%
        }
%>

            </div>

<%
    }else{
        String thumb = thumbnailList.get(0);

        if(thumb != null && !thumb.trim().equals("")){
%>

            <img src="<%= request.getContextPath() %>/<%= thumb %>"
                 class="playlist-cover-single"
                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
            <span class="playlist-default-note" style="display:none;">♫</span>

<%
        }else{
%>

            <span class="playlist-default-note">♫</span>

<%
        }
    }

}else{
%>

            <span class="playlist-default-note">♫</span>

<%
}
%>

        </div>

        <div class="playlist-info">
            <h1><%= playlist.getPlaylistName() %></h1>
            <p><%= nickname %></p>
        </div>

    </div>

    <%--
        =========================================
        [이서윤 담당 - 플레이리스트 확장]
        플레이리스트에 저장된 음악 목록 출력

        - 음악 클릭 시 음악 상세 페이지로 이동
        - 왼쪽: 작은 썸네일
        - 가운데: 음악 제목
        - 오른쪽: 아티스트, 장르
        =========================================
    --%>

<%
if(musicList != null && !musicList.isEmpty()){
    for(MusicVO music : musicList){
%>

    <div class="music-row">

	    <a class="music-row-link"
	       href="<%= request.getContextPath() %>/music/detail?music_id=<%= music.getMusicId() %>">
	
	        <div>
	<%
	String thumb = music.getThumbnailPath();
	
	if(thumb != null && !thumb.trim().equals("")){
	%>
	            <img src="<%= request.getContextPath() %>/<%= thumb %>"
	                 class="music-thumb"
	                 onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
	            <span class="music-default-thumb" style="display:none;">♫</span>
	<%
	}else{
	%>
	            <span class="music-default-thumb">♫</span>
	<%
	}
	%>
	        </div>
	
	        <div class="music-title">
	            <%= music.getTitle() %>
	        </div>
	
	        <div class="music-meta">
	            <%= music.getArtist() %>
	        </div>
	
	        <div class="music-meta">
	            <%= music.getGenre() %>
	        </div>
	
	    </a>
	
	    <%--
	        [이서윤 담당 - 플레이리스트 확장]
	        플레이리스트 상세 페이지 음악 제거 버튼
	
	        - 음악 자체 삭제 아님
	        - 현재 플레이리스트에서만 제거
	    --%>
	    <button type="button"
	            class="music-delete-btn"
	            onclick="removeDetailMusic(<%= playlist.getPlaylistId() %>, <%= music.getMusicId() %>)">
	        🗑
	    </button>
	
	</div>

<%
    }
}else{
%>

    <div class="empty-text">
        <fmt:message key="playlist.detail.empty" />
    </div>

<%
}
%>

</div>

<script>
/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * 플레이리스트 상세 페이지 음악 제거 Ajax
 *
 * 요청 URL
 * - /playlist/detail/remove
 *
 * 처리
 * - 현재 플레이리스트에서 해당 음악만 제거
 * - 성공 시 상세 페이지 새로고침
 * =========================================
 */
function removeDetailMusic(playlistId, musicId){

    if(!confirm("<fmt:message key="playlist.remove.confirm" />")){
        return;
    }

    fetch("<%= request.getContextPath() %>/playlist/detail/remove", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: "playlist_id=" + playlistId + "&music_id=" + musicId
    })
    .then(response => response.text())
    .then(result => {

        result = result.trim();

        if(result === "SUCCESS"){
            location.reload();
        }else if(result === "LOGIN_REQUIRED"){
            alert("<fmt:message key="js.login.required" />");
            location.href = "<%= request.getContextPath() %>/login";
        }else{
            alert("<fmt:message key="playlist.delete.fail" />");
        }
    });
}
</script>

</body>
</html>