<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.musichub.model.PlaylistVO" %>

<%
/*
 * =========================================
 * [이서윤 담당]
 * <fmt:message key="playlist.title" /> 페이지 View
 *
 * 역할
 * - PlaylistServlet에서 전달받은 <fmt:message key="playlist.title" /> 출력
 * - 저장한 음악 목록 조회 결과 표시
 * - <fmt:message key="playlist.title" /> 제거 Ajax 처리
 *
 * 구조
 * Servlet → Service → DAO → DB
 *
 * 주의
 * - DB 조회는 JSP에서 직접 수행하지 않음
 * =========================================
 *
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * 수정 내용
 * - 기존에는 저장한 음악 목록을 바로 카드로 출력했음
 * - 변경 후에는 사용자가 만든 <fmt:message key="playlist.title" /> 목록을 카드로 출력함
 *
 * 출력 구조
 * - 내 <fmt:message key="playlist.title" />
 * - 새 <fmt:message key="playlist.title" /> 만들기 버튼
 * - <fmt:message key="playlist.title" /> 카드 목록
 *
 * 다음 단계 예정
 * - 카드 클릭 시 playlist/detail?playlist_id=... 이동
 * - 상세 페이지에서 해당 <fmt:message key="playlist.title" /> 안의 음악 목록 출력
 * =========================================
 */

List<PlaylistVO> playlistList =
    (List<PlaylistVO>)request.getAttribute("playlistList");
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
<meta charset="UTF-8">
<title><fmt:message key="playlist.title" /></title>

<link rel="stylesheet"
      href="<%= request.getContextPath() %>/resources/css/recommend.css?v=5">

<style>
/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * playlist.jsp 전용 임시 스타일
 *
 * 역할
 * - 기존 recommend.css를 유지하면서
 * - <fmt:message key="playlist.title" /> 카드 목록 화면만 추가 스타일 적용
 *
 * 참고
 * - 추후 CSS 파일로 분리 가능
 * =========================================
 */

body {
    background: #0d0d0d;
    color: #e0e0e0;
    margin: 0;
    font-family: 'Segoe UI', "Malgun Gothic", sans-serif;
}

.playlist-page-header {
    display: flex;
    align-items: center;
    gap: 28px;
    margin-bottom: 32px;
}

.playlist-create-btn {
    padding: 12px 20px;
    background: #e05c97;
    color: white;
    border: none;
    border-radius: 999px;
    font-size: 15px;
    font-weight: bold;
    cursor: pointer;
}

.playlist-create-btn:hover {
    background: #c94b82;
}

/*
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 * <fmt:message key="playlist.title" /> 카드 가로 배치 영역
 */
.playlist-card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, 260px);
    gap: 24px;

    max-width: 1500px;

    justify-content: center;

    align-items: start;
}

/*
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 * <fmt:message key="playlist.title" /> 카드
 *
 * - 기존 음악 카드와 비슷한 크기
 * - 카드 클릭 시 상세 페이지 이동
 */
.playlist-card-link {
    text-decoration: none;
    color: inherit;
    display: block;
    width: 260px;
}

.playlist-card {
    position: relative;
    width: 260px;
    border-radius: 12px;
    overflow: hidden;
    background: #1a1a1a;
    border: 1px solid rgba(255,255,255,0.08);
    transition: transform 0.15s ease;
}

.playlist-card:hover {
    transform: translateY(-4px);
}

/*
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 * <fmt:message key="playlist.title" /> 대표 이미지 영역
 *
 * 현재 단계에서는 썸네일 목록 조회 전이므로 기본 음표 카드 출력
 * 다음 단계에서 thumbnailList를 이용해 1장 또는 4분할 이미지로 변경 예정
 */
.playlist-cover-box {
    width: 100%;
    aspect-ratio: 1 / 1;
    background: #2a2a2a;
    display: flex;
    align-items: center;
    justify-content: center;
}

.playlist-default-note {
    font-size: 72px;
    color: #e05c97;
}

.playlist-card-info {
    padding: 14px 16px 18px;
    min-width: 0;
}

.playlist-card-info b {
    display:block;

    font-size:18px;
    margin-bottom:6px;
    color:white;

    width:100%;

    white-space:nowrap;
    overflow:hidden;
    text-overflow:ellipsis;
}

.playlist-card-info span {
    font-size: 14px;
    color: #b8b8b8;
}

/*
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 * <fmt:message key="playlist.title" /> 삭제 버튼
 *
 * - 기존 음악 카드의 X 버튼 UI 유지
 * - 지금은 화면만 먼저 구성
 * - 다음 단계에서 /playlist/delete Ajax와 연결 예정
 */
.playlist-remove-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 5;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: none;
    background: rgba(0,0,0,0.65);
    color: white;
    cursor: pointer;
    font-size: 16px;
}

.playlist-remove-btn:hover {
    background: #e05c97;
}

/*
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 * <fmt:message key="playlist.title" />가 없을 때 안내 영역
 */
.playlist-empty-box {
    margin-top: 60px;
    color: #ccc;
    font-size: 18px;
}

/*
 * 화면이 좁아졌을 때 카드 개수 자동 조정
 */
@media (max-width: 1200px) {
    .playlist-card-grid {
        grid-template-columns: repeat(4, 1fr);
    }
}

@media (max-width: 900px) {
    .playlist-card-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}

@media (max-width: 600px) {
    .playlist-card-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * <fmt:message key="playlist.title" /> 대표 이미지 4분할 스타일
 *
 * - playlist-cover-grid : 2x2 앨범 커버 배치
 * - playlist-cover-single : 음악이 1~3개일 때 첫 번째 썸네일 크게 출력
 * - playlist-cover-default-small : 4분할 중 이미지 실패 시 기본 음표 출력
 * =========================================
 */
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

.playlist-cover-default-small {
    background: #2a2a2a;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #e05c97;
    font-size: 36px;
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * 새 <fmt:message key="playlist.title" /> 생성 모달 스타일
 *
 * 참고 디자인
 * - Apple Music의 작은 팝업 창 느낌
 * - 배경을 어둡게 처리하고 가운데 입력창 표시
 * =========================================
 */
.playlist-modal-bg {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 999;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.55);
    display: flex;
    align-items: center;
    justify-content: center;
}

.playlist-create-modal {
    position: relative;
    width: 420px;
    padding: 70px 28px 32px;
    background: #252525;
    border-radius: 22px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.4);
}

.modal-title {
    position: absolute;
    top: 26px;
    left: 0;
    width: 100%;
    text-align: center;
    font-size: 20px;
    font-weight: bold;
    color: white;

    /* [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
       제목 영역이 X 버튼 클릭을 가로막지 않도록 처리 */
    pointer-events: none;
    z-index: 1;
}

.modal-close-btn,
.modal-check-btn {
    position: absolute;
    top: 18px;
    width: 42px;
    height: 42px;
    border-radius: 50%;
    border: none;
    background: #3b3b3b;
    color: white;
    font-size: 22px;
    cursor: pointer;

    /* [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
       제목 영역보다 버튼이 위에 오도록 처리 */
    z-index: 10;
}

.modal-close-btn {
    left: 20px;
}

.modal-check-btn {
    right: 20px;
}

.modal-close-btn:hover,
.modal-check-btn:hover {
    background: #e05c97;
}

.playlist-name-input {
    width: 100%;
    box-sizing: border-box;
    padding: 18px 20px;
    border-radius: 14px;
    border: none;
    outline: none;
    font-size: 17px;
    background: #3a3a3a;
    color: white;
}

.playlist-name-input::placeholder {
    color: #aaa;
}

.playlist-edit-btn{
    position:absolute;
    top:10px;
    right:46px;

    z-index:5;

    width:28px;
    height:28px;

    border:none;
    border-radius:50%;

    background:rgba(0,0,0,0.65);
    color:white;

    cursor:pointer;
}

.playlist-edit-btn:hover{
    background:#4f8dff;
}

</style>
</head>

<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />


<div class="recommend-wrapper recommend-page">

    <div class="playlist-page-header">
        <h1 class="recommend-page-title">
            <fmt:message key="playlist.title" />
        </h1>

        <%--
            [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
            새 <fmt:message key="playlist.title" /> 만들기 버튼

            현재 단계
            - 버튼 UI만 먼저 배치

            다음 단계
            - 클릭 시 새 <fmt:message key="playlist.title" /> 생성 모달 또는 생성 페이지 연결
        --%>
        <button type="button"
		        class="playlist-create-btn"
		        onclick="openCreatePlaylistModal();">
		    + <fmt:message key="playlist.new" />
		</button>
    </div>

<%-- =========================================
    [이서윤 담당]
    <fmt:message key="playlist.title" /> 음악 목록 출력

    - 저장한 음악 카드 출력
    - 카드 클릭 시 음악 상세 페이지 이동
    - 제거 버튼 클릭 시 Ajax 삭제
========================================= --%>

<%-- =========================================
    [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
    <fmt:message key="playlist.title" /> 카드 목록 출력

    - 기존 음악 목록 카드 출력 영역을 <fmt:message key="playlist.title" /> 카드 출력 영역으로 변경
    - 카드 클릭 시 playlist/detail 페이지로 이동 예정
    - 삭제 버튼은 기존 X UI를 유지
========================================= --%>

<%
if(playlistList != null && !playlistList.isEmpty()){
%>

    <div class="playlist-card-grid">

<%
    for(PlaylistVO playlist : playlistList){
%>

        <a href="<%= request.getContextPath() %>/playlist/detail?playlist_id=<%= playlist.getPlaylistId() %>"
           class="playlist-card-link">

            <div class="playlist-card">

				<button type="button"
				        class="playlist-edit-btn"
				        onclick="openEditPlaylistModal(
				            event,
				            <%= playlist.getPlaylistId() %>,
				            '<%= playlist.getPlaylistName() %>'
				        )">
				    ✏
				</button>

                <%--
                    [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
                    <fmt:message key="playlist.title" /> 삭제 버튼

                    - 카드 클릭 이벤트와 충돌하지 않도록 event.stopPropagation 처리 예정
                    - 다음 단계에서 removePlaylist 함수와 Servlet 연결
                --%>
                <button type="button"
                        class="playlist-remove-btn"
                        onclick="removePlaylist(event, <%= playlist.getPlaylistId() %>)">
                    ✕
                </button>

                <%--
				    =========================================
				    [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
				
				    <fmt:message key="playlist.title" /> 대표 썸네일 출력
				
				    출력 규칙
				    - 음악이 4개 이상이면 처음 추가된 음악 4개의 썸네일을 4분할로 출력
				    - 음악이 1~3개이면 첫 번째 음악 썸네일을 대표 이미지처럼 크게 출력
				    - 음악이 없거나 썸네일 경로가 없으면 기본 음표 아이콘 출력
				
				    주의
				    - DB 조회는 JSP에서 하지 않음
				    - PlaylistDAO에서 playlist.thumbnailList에 담아온 값만 사용
				    =========================================
				--%>
				<div class="playlist-cover-box">
				
				<%
				    List<String> thumbnailList = playlist.getThumbnailList();
				
				    if(thumbnailList != null && !thumbnailList.isEmpty()){
				
				        /*
				         * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
				         * 음악이 4개 이상이면 썸네일 유무와 관계없이 무조건 4분할 출력
				         * - 썸네일이 있는 음악: 이미지 출력
				         * - 썸네일이 없는 음악: 해당 칸에 기본 음표 출력
				         */
				        if(thumbnailList.size() >= 4){
				%>
				
				    <div class="playlist-cover-grid">
				
				<%
				            for(int i = 0; i < 4; i++){
				                String thumb = thumbnailList.get(i);
				
				                if(thumb != null && !thumb.trim().equals("")){
				%>
				
				        <img src="<%= request.getContextPath() %>/<%= thumb %>"
				             class="playlist-cover-img"
				             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
				        <span class="playlist-cover-default-small" style="display:none;">♫</span>
				
				<%
				                }else{
				%>
				
				        <span class="playlist-cover-default-small">♫</span>
				
				<%
				                }
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

                <div class="playlist-card-info">

                    <b><%= playlist.getPlaylistName() %></b>

                    <span>
                        <fmt:message key="playlist.title" />
                    </span>

                </div>

            </div>

        </a>

<%
    }
%>

    </div>

<%
}else{
%>

    <%-- =========================================
         <fmt:message key="playlist.title" />가 비어 있을 때 표시
    ========================================= --%>

    <div class="playlist-empty-box">

        <div class="mh-playlist-empty-icon">
            🎵
        </div>

        <div class="mh-playlist-empty-title">
            <fmt:message key="playlist.empty.title" />
        </div>

        <div class="mh-playlist-empty-subtitle">
            <fmt:message key="playlist.empty.sub" />
        </div>

    </div>

<%
}
%>

</div>

<%--
    =========================================
    [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]

    새 <fmt:message key="playlist.title" /> 생성 모달

    역할
    - + <fmt:message key="playlist.new" /> 버튼 클릭 시 표시
    - <fmt:message key="playlist.title" /> 이름 입력
    - X 버튼: 모달 닫기
    - 체크 버튼: Ajax로 /playlist/create 요청

    참고
    - DB 테이블 구조 변경 없이 playlists.name 컬럼 사용
    =========================================
--%>
<div id="createPlaylistModal" class="playlist-modal-bg" style="display:none;">

    <div class="playlist-create-modal">

        <button type="button"
                class="modal-close-btn"
                onclick="closeCreatePlaylistModal();">
            ✕
        </button>

        <div class="modal-title">
            <fmt:message key="playlist.new" />
        </div>

        <button type="button"
                class="modal-check-btn"
                onclick="createPlaylist();">
            ✓
        </button>

        <input type="text"
               id="playlistNameInput"
               class="playlist-name-input"
               placeholder="<fmt:message key="playlist.create.ph" />"
               maxlength="50">

    </div>

</div>

<%--
    =========================================
    [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]

    <fmt:message key="playlist.title" /> 이름 수정 모달

    역할
    - 카드의 ✏ 버튼 클릭 시 표시
    - 기존 <fmt:message key="playlist.title" /> 이름을 입력창에 넣어 수정 가능
    - 체크 버튼 클릭 시 /playlist/update Ajax 요청
    =========================================
--%>
<div id="editPlaylistModal" class="playlist-modal-bg" style="display:none;">

    <div class="playlist-create-modal">

        <button type="button"
                class="modal-close-btn"
                onclick="closeEditPlaylistModal();">
            ✕
        </button>

        <div class="modal-title">
            <fmt:message key="playlist.name.edit" />
        </div>

        <button type="button"
                class="modal-check-btn"
                onclick="updatePlaylistName();">
            ✓
        </button>

        <input type="hidden"
               id="editPlaylistId">

        <input type="text"
               id="editPlaylistNameInput"
               class="playlist-name-input"
               placeholder="<fmt:message key="playlist.edit.ph" />"
               maxlength="50">

    </div>

</div>

<script>
/*
 * =========================================
 * [이서윤 담당]
 * <fmt:message key="playlist.title" /> 제거 Ajax
 *
 * 요청 URL
 * - /playlist/remove
 *
 * 반환값
 * - SUCCESS
 * - LOGIN_REQUIRED
 * - ERROR
 *
 * 처리 내용
 * - 저장 음악 제거
 * - 성공 시 목록 새로고침
 * =========================================
 */


/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * 새 <fmt:message key="playlist.title" /> 생성 모달 열기
 * =========================================
 */
function openCreatePlaylistModal(){

    const modal =
        document.getElementById("createPlaylistModal");

    const input =
        document.getElementById("playlistNameInput");

    modal.style.display = "flex";
    input.value = "";
    input.focus();
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * 새 <fmt:message key="playlist.title" /> 생성 모달 닫기
 * =========================================
 */
function closeCreatePlaylistModal(){

    document.getElementById("createPlaylistModal")
            .style.display = "none";
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * 새 <fmt:message key="playlist.title" /> 생성 Ajax
 *
 * 요청 URL
 * - /playlist/create
 *
 * 처리
 * - 입력한 <fmt:message key="playlist.title" /> 이름을 서버로 전송
 * - 성공 시 목록 새로고침
 * =========================================
 */
function createPlaylist(){

    const input =
        document.getElementById("playlistNameInput");

    const playlistName =
        input.value.trim();

    if(playlistName === ""){
        alert("<fmt:message key="playlist.title.required" />");
        input.focus();
        return;
    }

    fetch("<%= request.getContextPath() %>/playlist/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: "playlist_name=" + encodeURIComponent(playlistName)
    })
    .then(response => response.text())
    .then(result => {

        result = result.trim();

        if(result === "SUCCESS"){
            alert("<fmt:message key="playlist.created" />");
            location.reload();
        }else if(result === "LOGIN_REQUIRED"){
            alert("<fmt:message key="js.login.required" />");
            location.href = "<%= request.getContextPath() %>/login";
        }else if(result === "EMPTY_NAME"){
            alert("<fmt:message key="playlist.title.required" />");
        }else{
            alert("<fmt:message key="playlist.create.fail" />");
        }
    });
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * Enter 키로 <fmt:message key="playlist.title" /> 생성
 * =========================================
 */
document.addEventListener("DOMContentLoaded", function(){

    const input =
        document.getElementById("playlistNameInput");

    if(input != null){
        input.addEventListener("keydown", function(event){
            if(event.key === "Enter"){
                createPlaylist();
            }
        });
    }
});

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * <fmt:message key="playlist.title" /> 삭제
 *
 * 역할
 * - <fmt:message key="playlist.title" /> 카드 우측 상단 X 버튼
 * - 삭제 후 목록 새로고침
 * =========================================
 */
function removePlaylist(event, playlistId){

    event.preventDefault();
    event.stopPropagation();

    if(!confirm("<fmt:message key="playlist.delete.confirm" />")){
        return;
    }

    fetch(
        "<%=request.getContextPath()%>/playlist/delete?playlist_id="
        + playlistId
    )
    .then(response => response.text())
    .then(result => {

        result = result.trim();

        if(result === "SUCCESS"){

            alert("<fmt:message key="playlist.deleted" />");

            location.reload();

        }else if(result === "LOGIN_REQUIRED"){

            location.href =
                "<%=request.getContextPath()%>/login";

        }else{

            alert("<fmt:message key="playlist.delete.fail" />");

        }
    });
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * <fmt:message key="playlist.title" /> 수정 모달 열기
 *
 * 역할
 * - 카드의 ✏ 버튼 클릭 시 실행
 * - 클릭한 <fmt:message key="playlist.title" />의 id와 이름을 모달 입력창에 저장
 * =========================================
 */
function openEditPlaylistModal(event, playlistId, playlistName){

    event.preventDefault();
    event.stopPropagation();

    document.getElementById("editPlaylistId").value = playlistId;
    document.getElementById("editPlaylistNameInput").value = playlistName;

    document.getElementById("editPlaylistModal").style.display = "flex";
    document.getElementById("editPlaylistNameInput").focus();
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * <fmt:message key="playlist.title" /> 수정 모달 닫기
 * =========================================
 */
function closeEditPlaylistModal(){

    document.getElementById("editPlaylistModal").style.display = "none";
}

/*
 * =========================================
 * [이서윤 담당 - <fmt:message key="playlist.title" /> 확장]
 *
 * <fmt:message key="playlist.title" /> 이름 수정 Ajax
 *
 * 요청 URL
 * - /playlist/update
 *
 * 처리
 * - 수정 성공 시 목록 새로고침
 * =========================================
 */
function updatePlaylistName(){

    const playlistId =
        document.getElementById("editPlaylistId").value;

    const input =
        document.getElementById("editPlaylistNameInput");

    const playlistName =
        input.value.trim();

    if(playlistName === ""){
        alert("<fmt:message key="playlist.title.required" />");
        input.focus();
        return;
    }

    fetch("<%= request.getContextPath() %>/playlist/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body:
            "playlist_id=" + encodeURIComponent(playlistId)
            + "&playlist_name=" + encodeURIComponent(playlistName)
    })
    .then(response => response.text())
    .then(result => {

        result = result.trim();

        if(result === "SUCCESS"){
            alert("<fmt:message key="playlist.renamed" />");
            location.reload();
        }else if(result === "LOGIN_REQUIRED"){
            alert("<fmt:message key="js.login.required" />");
            location.href = "<%= request.getContextPath() %>/login";
        }else if(result === "EMPTY_NAME"){
            alert("<fmt:message key="playlist.title.required" />");
        }else{
            alert("<fmt:message key="playlist.rename.fail" />");
        }
    });
}
</script>

</body>
</html>