<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ page import="com.musichub.model.MusicVO" %>
<%@ page import="com.musichub.model.CommentVO" %>
<%@ page import="com.musichub.model.PlaylistVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%
/*
 * [이서윤 담당]
 * <fmt:message key="music.detail.title" /> 페이지 View
 *
 * 역할
 * - MusicDetailServlet에서 전달받은 <fmt:message key="music.detail.title" /> 정보 출력
 * - 좋아요 수 / 좋아요 여부 출력
 * - 플레이리스트 저장 여부 출력
 * - 댓글 목록 출력
 * - 좋아요, 플레이리스트 저장, 댓글 등록/삭제 Ajax 처리
 *
 * 주의
 * - DB 조회는 JSP에서 직접 하지 않음
 * - DB 처리는 Servlet → Service → DAO 구조에서 처리
 */

MusicVO music = (MusicVO)request.getAttribute("music");
Integer likeCount = (Integer)request.getAttribute("likeCount");
Boolean liked = (Boolean)request.getAttribute("liked");
Boolean playlistSaved = (Boolean)request.getAttribute("playlistSaved");

List<CommentVO> commentList =
    (List<CommentVO>)request.getAttribute("commentList");

/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * MusicDetailServlet에서 전달한 사용자 플레이리스트 목록
 *
 * 역할
 * - "+ 플레이리스트 저장" 버튼 클릭 시 표시되는
 *   플레이리스트 선택 모달에 목록을 출력하기 위해 사용
 * =========================================
 */
List<PlaylistVO> playlistList =
    (List<PlaylistVO>)request.getAttribute("playlistList");

Integer loginUserId =
    (Integer)session.getAttribute("loginUserId");
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
<meta charset="UTF-8">
<title><fmt:message key="music.detail.title" /></title>

<%-- [이서윤 담당] 음악 기능 전용 CSS --%>
<link rel="stylesheet"
      href="<%= request.getContextPath() %>/resources/css/recommend.css?v=4">

<%-- 하트 / 뒤로가기 / 휴지통 아이콘 사용 --%>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

<style>
/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * <fmt:message key="music.detail.title" /> 페이지 플레이리스트 선택 모달 스타일
 *
 * 역할
 * - "+ 플레이리스트 저장" 버튼 클릭 시
 *   Apple Music처럼 작은 선택 창을 띄우기 위한 스타일
 * =========================================
 */
.playlist-select-bg {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 999;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.65);
    display: flex;
    align-items: center;
    justify-content: center;
}

.playlist-select-modal {
    position: relative;
    width: 520px;
    max-height: 620px;
    padding: 72px 28px 28px;
    background: #252525;
    border-radius: 24px;
    box-shadow: 0 20px 70px rgba(0,0,0,0.45);
    color: white;
    overflow: hidden;
}

.playlist-select-title {
    position: absolute;
    top: 26px;
    left: 0;
    width: 100%;
    text-align: center;
    font-size: 21px;
    font-weight: bold;
}

.playlist-select-close,
.playlist-select-plus {
    position: absolute;
    top: 18px;
    width: 42px;
    height: 42px;
    border-radius: 50%;
    border: none;
    background: #3b3b3b;
    color: white;
    font-size: 24px;
    cursor: pointer;
}

.playlist-select-close {
    left: 20px;
}

.playlist-select-plus {
    right: 20px;
}

.playlist-select-close:hover,
.playlist-select-plus:hover {
    background: #e05c97;
}

.playlist-select-list {
    max-height: 500px;
    overflow-y: auto;
}

.playlist-select-item {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 14px 8px;
    border: none;
    border-bottom: 1px solid rgba(255,255,255,0.12);
    background: transparent;
    color: white;
    cursor: pointer;
    text-align: left;
}

.playlist-select-item:hover {
    background: rgba(255,255,255,0.08);
}

.playlist-select-thumb {
    width: 56px;
    height: 56px;
    border-radius: 8px;
    background: #2a2a2a;
    color: #e05c97;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
    flex-shrink: 0;
    overflow: hidden;
}

.playlist-select-name {
    font-size: 17px;
    font-weight: bold;
}

.playlist-select-empty {
    padding: 40px 0;
    color: #aaa;
    text-align: center;
}

/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * 플레이리스트 선택 모달 대표 썸네일 스타일
 *
 * 역할
 * - 플레이리스트에 음악이 4개 이상이면 2x2 썸네일 출력
 * - 음악이 1~3개이면 첫 번째 썸네일을 대표 이미지로 출력
 * - 썸네일이 없으면 기본 음표 출력
 * =========================================
 */
.playlist-select-grid {
    width: 56px;
    height: 56px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: 1fr 1fr;
    overflow: hidden;
    border-radius: 8px;
}

.playlist-select-grid-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.playlist-select-single-img {
    width: 56px;
    height: 56px;
    object-fit: cover;
    border-radius: 8px;
}

.playlist-select-note {
    font-size: 24px;
}

/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * 플레이리스트 화면 이동 버튼
 *
 * 역할
 * - 저장 버튼 왼쪽에 배치
 * - 좋아요 버튼과 같은 원형 아이콘 버튼 형태 유지
 * =========================================
 */
.playlist-move-btn i {
    color: white;
    font-size: 18px;
}
</style>
</head>

<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />


<div class="recommend-wrapper">

    <%-- =========================
         상세 페이지 상단 헤더
         ========================= --%>
    <div class="mh-detail-header">
        <a href="<%= request.getContextPath() %>/recommend"
           class="mh-detail-back-btn">
            <i class="fa-solid fa-chevron-left"></i>
        </a>

        <h1><fmt:message key="music.detail.title" /></h1>
    </div>

    <div class="mh-detail-panel">

        <%-- =========================
             음악 기본 정보 영역
             - 썸네일
             - 제목 / 가수 / 장르
             - 좋아요 수
             - 플레이리스트 저장 버튼
             - 좋아요 버튼
             ========================= --%>
        <div class="mh-detail-top">

            <div class="mh-detail-cover">
<%
String thumbnailPath = music.getThumbnailPath();

if(thumbnailPath != null && !thumbnailPath.trim().equals("")){
%>
                <img src="<%= request.getContextPath() %>/<%= thumbnailPath %>"
                     class="recommend-thumbnail"
                     onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">

                <span class="recommend-default-note" style="display:none;">♫</span>
<%
}else{
%>
                <span class="recommend-default-note">♫</span>
<%
}
%>
            </div>

            <div class="mh-detail-info">

                <h2><%= music.getTitle() %></h2>

                <div class="mh-detail-meta">
                    <span class="mh-detail-artist"><%= music.getArtist() %></span>
                    <span class="mh-detail-genre"><%= music.getGenre() %></span>
                </div>

                <div class="mh-detail-stats">
                    <span>
                        <i class="fa-solid fa-heart"></i>
                        <fmt:message key="music.detail.likes" />
                        <span id="detailLikeCount"><%= likeCount %></span>
                    </span>
                </div>

                <div class="mh-detail-actions">

                    <%--
                        =========================================
                        [이서윤 담당 - 플레이리스트 확장]
                        플레이리스트 화면 이동 버튼
                        =========================================
                    --%>
                    <button type="button"
                            class="mh-detail-circle-btn playlist-move-btn"
                            onclick="location.href='<%= request.getContextPath() %>/playlist';">
                        <i class="fa-solid fa-table-cells-large"></i>
                    </button>

                    <%--
                        [이서윤 담당 - 플레이리스트 확장]
                        플레이리스트 저장 버튼 수정
                    --%>
                    <button type="button"
                            id="playlistBtn"
                            class="mh-detail-playlist-btn playlist-save-btn <%= playlistSaved ? "saved" : "" %>"
                            data-music-id="<%= music.getMusicId() %>">
                        <% if (playlistSaved) { %><fmt:message key="playlist.saved" /><% } else { %><fmt:message key="playlist.save" /><% } %>
                    </button>

                    <%-- [이서윤 담당] 좋아요 토글 Ajax 버튼 --%>
                    <button type="button"
                            class="mh-detail-circle-btn detail-like-btn"
                            data-music-id="<%= music.getMusicId() %>">
                        <i class="<%= liked ? "fa-solid" : "fa-regular" %> fa-heart"></i>
                    </button>

                </div>

            </div>

        </div>

        <%-- =========================================
             [승민 담당 - 머지 추가] 30초 미리듣기 영역
             ========================================= --%>
        <% if (music.getFilePath() != null && !music.getFilePath().trim().isEmpty()) { %>
        <div class="mh-preview-area"
             style="background:#1a1a1a; border-radius:10px; padding:1.2rem; margin:1rem 0;
                    display:flex; flex-direction:column; gap:0.7rem;">
            <h3 style="margin:0; font-size:1rem; color:#fff;"><fmt:message key="preview.title" /></h3>

            <audio id="previewAudio"
                   src="<%= request.getContextPath() %>/<%= music.getFilePath() %>"
                   preload="metadata">
                <fmt:message key="preview.no.support" />
            </audio>

            <button type="button" id="playBtn" class="mh-btn-pink"
                    style="align-self:flex-start; padding:0.4rem 1.1rem; border:none;
                           border-radius:6px; background:#e05c97; color:#fff; cursor:pointer;">
                <fmt:message key="preview.play" />
            </button>

            <div style="background:#333; border-radius:4px; height:6px; overflow:hidden;">
                <div id="progressBar"
                     style="height:100%; width:0%; background:#e05c97; transition:width 0.2s linear;"></div>
            </div>

            <span id="timeDisplay" style="font-size:0.85rem; color:#888;">0 / 30<fmt:message key="preview.sec" /></span>

            <p id="previewEndMsg" style="display:none; font-size:0.85rem; color:#e05c97; margin:0;">
                <fmt:message key="preview.ended" />
            </p>
        </div>
        <% } %>

        <hr>

        <h3><fmt:message key="music.comment" /></h3>

        <form id="commentForm" class="mh-comment-form">
            <input type="hidden"
                   id="commentMusicId"
                   value="<%= music.getMusicId() %>">

            <div class="mh-comment-input-row">

                <div class="mh-comment-avatar">
                    <%= music.getArtist().substring(0, 1) %>
                </div>

                <div class="mh-comment-input-area">
                    <textarea
                        id="commentContent"
                        name="content"
                        placeholder="<fmt:message key="music.comment.ph" />"
                        required></textarea>

                    <div class="mh-comment-submit-row">
                        <button type="submit"
                                class="mh-comment-submit-btn">
                            <fmt:message key="comment.submit" />
                        </button>
                    </div>
                </div>

            </div>
        </form>

        <h3 id="commentListTitle">
            <fmt:message key="comment.list" /> (<%= commentList == null ? 0 : commentList.size() %>)
        </h3>

        <div id="commentList">

<%
if(commentList != null && !commentList.isEmpty()){
    for(CommentVO comment : commentList){
%>

            <div class="mh-comment-item">

                <div class="mh-comment-user-row">

                    <div class="mh-comment-user-avatar">
                        <%= comment.getWriter().substring(0, 1) %>
                    </div>

                    <div class="mh-comment-user-info">

                        <div class="mh-comment-header">
                            <span class="mh-comment-writer">
                                <%= comment.getWriter() %>
                            </span>

                            <span class="mh-comment-date">
                                <%= comment.getCreatedAt() %>
                            </span>
                        </div>

                        <div class="mh-comment-content">
                            <%= comment.getContent() %>
                        </div>

                    </div>

                </div>

<%
        if(loginUserId != null && loginUserId == comment.getUserId()){
%>
                <button type="button"
                        class="mh-comment-delete-btn"
                        data-comment-id="<%= comment.getCommentId() %>">
                    <i class="fa-solid fa-trash"></i>
                </button>
<%
        }
%>

            </div>

<%
    }
}else{
%>

            <div class="comment-empty">
                <fmt:message key="comment.empty" />
            </div>

<%
}
%>

        </div>

    </div>

</div>

<div id="playlistModal" class="mh-confirm-modal">
    <div class="mh-confirm-box">
        <h3><fmt:message key="music.detail.already.saved" /></h3>
        <p><fmt:message key="music.detail.goto.playlist" /></p>

        <div class="mh-confirm-buttons">
            <button id="playlistCancelBtn"
                    class="mh-confirm-cancel-btn">
                <fmt:message key="common.cancel" />
            </button>

            <button id="playlistMoveBtn"
                    class="mh-confirm-delete-btn">
                <fmt:message key="playlist.move" />
            </button>
        </div>
    </div>
</div>

<%--
    =========================================
    [이서윤 담당 - 플레이리스트 확장]
    플레이리스트 선택 모달
    =========================================
--%>
<div id="playlistSelectModal" class="playlist-select-bg" style="display:none;">

    <div class="playlist-select-modal">

        <button type="button"
                class="playlist-select-close"
                onclick="closePlaylistSelectModal();">
            ✕
        </button>

        <div class="playlist-select-title">
            <fmt:message key="playlist.add.title" />
        </div>

        <button type="button"
                class="playlist-select-plus"
                onclick="location.href='<%= request.getContextPath() %>/playlist';">
            +
        </button>

        <div class="playlist-select-list">

<%
if(playlistList != null && !playlistList.isEmpty()){
    for(PlaylistVO playlist : playlistList){
%>

            <button type="button"
                    class="playlist-select-item"
                    onclick="addMusicToSelectedPlaylist(<%= playlist.getPlaylistId() %>, <%= music.getMusicId() %>);">

                <%--
                    =========================================
                    [이서윤 담당 - 플레이리스트 확장]
                    플레이리스트 선택 모달 대표 썸네일 출력
                    =========================================
                --%>
                <div class="playlist-select-thumb">

<%
        List<String> selectThumbList = playlist.getThumbnailList();

        if(selectThumbList != null && !selectThumbList.isEmpty()){

            List<String> validSelectThumbs = new ArrayList<>();

            for(String thumb : selectThumbList){
                if(thumb != null && !thumb.trim().equals("")){
                    validSelectThumbs.add(thumb);
                }
            }

            if(validSelectThumbs.size() >= 4){
%>

                    <div class="playlist-select-grid">

<%
                for(int i = 0; i < 4; i++){
%>
                        <img src="<%= request.getContextPath() %>/<%= validSelectThumbs.get(i) %>"
                             class="playlist-select-grid-img">
<%
                }
%>

                    </div>

<%
            }else if(validSelectThumbs.size() > 0){

                String thumb = validSelectThumbs.get(0);
%>

                    <img src="<%= request.getContextPath() %>/<%= thumb %>"
                         class="playlist-select-single-img">

<%
            }else{
%>
                    <span class="playlist-select-note">♫</span>
<%
            }

        }else{
%>
                    <span class="playlist-select-note">♫</span>
<%
        }
%>

                </div>

                <div class="playlist-select-name">
                    <%= playlist.getPlaylistName() %>
                </div>

            </button>

<%
    }
}else{
%>

            <div class="playlist-select-empty">
                <fmt:message key="playlist.none" />
            </div>

<%
}
%>

        </div>

    </div>

</div>

<script>
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

function updateCommentCount(){
    const count =
        document.querySelectorAll("#commentList .mh-comment-item").length;

    const title =
        document.getElementById("commentListTitle");

    if(title){
        title.textContent = "<fmt:message key="comment.list" /> (" + count + ")";
    }
}

function setPlaylistSavedState(){
    const btn = document.getElementById("playlistBtn");

    if(!btn) return;

    btn.innerHTML = "<fmt:message key="playlist.saved" />";
    btn.classList.add("saved");
}

/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 * 플레이리스트 저장 버튼 클릭 처리 수정
 * =========================================
 */
const playlistSaveBtn = document.querySelector(".playlist-save-btn");

if(playlistSaveBtn){
    playlistSaveBtn.addEventListener("click", function(){
        openPlaylistSelectModal();
    });
}

function openPlaylistSelectModal(){
    document.getElementById("playlistSelectModal").style.display = "flex";
}

function closePlaylistSelectModal(){
    document.getElementById("playlistSelectModal").style.display = "none";
}

function addMusicToSelectedPlaylist(playlistId, musicId){

    fetch("<%= request.getContextPath() %>/playlist/select/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: "playlist_id=" + playlistId + "&music_id=" + musicId
    })
    .then(response => response.text())
    .then(result => {

        result = result.trim();

        if(result === "SAVED"){
            showToast("<fmt:message key="playlist.saved.toast" />");
            setPlaylistSavedState();
            closePlaylistSelectModal();
        }
        else if(result === "ALREADY"){
            showToast("<fmt:message key="playlist.already.saved" />");
        }
        else if(result === "LOGIN_REQUIRED"){
            alert("<fmt:message key="js.login.required" />");
            location.href = "<%= request.getContextPath() %>/login";
        }
        else{
            alert("<fmt:message key="playlist.save.fail" />");
        }
    });
}

const playlistCancelBtn =
    document.getElementById("playlistCancelBtn");

const playlistMoveBtn =
    document.getElementById("playlistMoveBtn");

if(playlistCancelBtn){
    playlistCancelBtn.addEventListener("click", function(){
        document.getElementById("playlistModal").classList.remove("show");
    });
}

if(playlistMoveBtn){
    playlistMoveBtn.addEventListener("click", function(){
        location.href = "<%= request.getContextPath() %>/playlist";
    });
}

const commentForm =
    document.getElementById("commentForm");

if(commentForm){
    commentForm.addEventListener("submit", function(e){
        e.preventDefault();

        const musicId =
            document.getElementById("commentMusicId").value;

        const contentInput =
            document.getElementById("commentContent");

        const content =
            contentInput.value;

        if(content.trim() === ""){
            alert("<fmt:message key="comment.input.required" />");
            return;
        }

        const bodyData =
            new URLSearchParams();

        bodyData.append("music_id", musicId);
        bodyData.append("content", content);

        fetch("<%= request.getContextPath() %>/comment/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
            },
            body: bodyData.toString()
        })
        .then(response => response.text())
        .then(result => {
            result = result.trim();

            const parts = result.split("|");
            const status = parts[0];

            if(status === "SUCCESS"){
                location.reload();
            }
            else if(status === "LOGIN_REQUIRED"){
                alert("<fmt:message key="js.login.required" />");
            }
            else{
                alert("<fmt:message key="comment.add.fail" />");
            }
        });
    });
}

document.querySelectorAll(".mh-comment-delete-btn").forEach(function(button){
    button.addEventListener("click", function(){

        if(!confirm("<fmt:message key="comment.delete.confirm" />")){
            return;
        }

        const commentId =
            button.dataset.commentId;

        fetch("<%= request.getContextPath() %>/comment/delete?comment_id=" + commentId)
            .then(response => response.text())
            .then(result => {
                result = result.trim();

                if(result === "DELETED"){
                    button.closest(".mh-comment-item").remove();
                    updateCommentCount();
                    showToast("<fmt:message key="comment.deleted" />");
                }
                else if(result === "LOGIN_REQUIRED"){
                    alert("<fmt:message key="js.login.required" />");
                }
                else{
                    alert("<fmt:message key="comment.delete.error" />");
                }
            });
    });
});

const likeBtn =
    document.querySelector(".detail-like-btn");

if(likeBtn){

    likeBtn.addEventListener("click", function(){

        const musicId =
            likeBtn.dataset.musicId;

        fetch(
            "<%= request.getContextPath() %>/like/toggle?music_id="
            + musicId
        )
        .then(response => response.text())
        .then(result => {

            result = result.trim();

            if(result === "LOGIN_REQUIRED"){
                alert("<fmt:message key="js.login.required" />");
                return;
            }

            const parts = result.split("|");

            const status = parts[0];
            const likeCount = parts[1];

            const icon =
                likeBtn.querySelector("i");

            document.getElementById(
                "detailLikeCount"
            ).textContent = likeCount;

            if(status === "LIKED"){
                icon.classList.remove("fa-regular");
                icon.classList.add("fa-solid");
            }else if(status === "UNLIKED"){
                icon.classList.remove("fa-solid");
                icon.classList.add("fa-regular");
            }

        });

    });

}
</script>

<script>
/*
 * [승민 담당 - 머지 추가] 30초 미리듣기 JavaScript
 * 재생 시간이 30초를 넘으면 강제로 멈추고 안내 메시지를 띄운다.
 */
(function() {
    const audio = document.getElementById("previewAudio");
    if (!audio) return;

    const PREVIEW_LIMIT = 30;
    const playBtn     = document.getElementById("playBtn");
    const progressBar = document.getElementById("progressBar");
    const timeDisplay = document.getElementById("timeDisplay");
    const endMsg      = document.getElementById("previewEndMsg");
    let isPlaying = false;

    playBtn.addEventListener("click", function() {
        if (isPlaying) {
            audio.pause();
            playBtn.textContent = "<fmt:message key="preview.play" />";
            isPlaying = false;
        } else {
            if (audio.currentTime >= PREVIEW_LIMIT) {
                audio.currentTime = 0;
                endMsg.style.display = "none";
            }
            audio.play();
            playBtn.textContent = "<fmt:message key="preview.pause" />";
            isPlaying = true;
        }
    });

    audio.addEventListener("timeupdate", function() {
        const current = audio.currentTime;
        if (current >= PREVIEW_LIMIT) {
            audio.pause();
            audio.currentTime    = PREVIEW_LIMIT;
            playBtn.textContent  = "<fmt:message key="preview.replay" />";
            isPlaying            = false;
            endMsg.style.display = "block";
        }
        progressBar.style.width = Math.min((current / PREVIEW_LIMIT) * 100, 100) + "%";
        timeDisplay.textContent = Math.min(Math.floor(current), PREVIEW_LIMIT) + " / 30<fmt:message key="preview.sec" />";
    });

    audio.addEventListener("ended", function() {
        playBtn.textContent  = "<fmt:message key="preview.replay" />";
        isPlaying            = false;
        endMsg.style.display = "block";
    });
})();
</script>

</body>
</html>