package com.devonoff.domain.studyPost.controller;

import com.devonoff.domain.studyPost.dto.StudyPostCreateDto;
import com.devonoff.domain.studyPost.dto.StudyPostDto;
import com.devonoff.domain.studyPost.dto.StudyPostUpdateDto;
import com.devonoff.domain.studyPost.service.StudyPostService;
import com.devonoff.type.StudyDifficulty;
import com.devonoff.type.StudyMeetingType;
import com.devonoff.type.StudyStatus;
import com.devonoff.type.StudySubject;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-posts")
public class StudyPostController {

  private final StudyPostService studyPostService;

  // 스터디 모집글 상세 조회
  @GetMapping("/{studyPostId}")
  public ResponseEntity<StudyPostDto> getStudyPostDetail(@PathVariable Long studyPostId) {
    StudyPostDto studyPostDto = studyPostService.getStudyPostDetail(studyPostId);
    return ResponseEntity.ok(studyPostDto);
  }

  // 스터디 모집글 검색
  @GetMapping("/search")
  public ResponseEntity<Page<StudyPostDto>> searchStudyPosts(
      @RequestParam(required = false) StudyMeetingType meetingType,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) StudySubject subject,
      @RequestParam(required = false) StudyDifficulty difficulty,
      @RequestParam(required = false, defaultValue = "0") int dayType,
      @RequestParam(required = false) StudyStatus status,
      @RequestParam(required = false) Double latitude,
      @RequestParam(required = false) Double longitude,
      Pageable pageable) {

    Page<StudyPostDto> studyPosts = studyPostService.searchStudyPosts(
        meetingType, title, subject, difficulty, dayType, status, latitude, longitude, pageable);
    return ResponseEntity.ok(studyPosts);
  }

  // 스터디 모집글 생성
  @PostMapping
  public ResponseEntity<StudyPostCreateDto.Response> createStudyPost(
      @RequestBody StudyPostCreateDto.Request request) {
    StudyPostCreateDto.Response response = studyPostService.createStudyPost(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 스터디 모집글 수정
  @PatchMapping("/{studyPostId}")
  public ResponseEntity<StudyPostUpdateDto.Response> updateStudyPost(
      @PathVariable Long studyPostId, @RequestBody StudyPostUpdateDto.Request request) {
    StudyPostUpdateDto.Response response = studyPostService.updateStudyPost(studyPostId, request);
    return ResponseEntity.ok(response);
  }

  // 스터디 모집글 마감
  @PatchMapping("/{studyPostId}/close")
  public ResponseEntity<Void> closeStudyPost(@PathVariable Long studyPostId) {
    studyPostService.closeStudyPost(studyPostId);
    return ResponseEntity.noContent().build();
  }

  // 스터디 모집글 모집 취소 (삭제 스케줄링)
  @PatchMapping("/{studyPostId}/cancel")
  public ResponseEntity<Void> cancelStudyPost(@PathVariable Long studyPostId) {
    studyPostService.cancelStudyPost(studyPostId);
    return ResponseEntity.noContent().build();
  }

  // 모집 취소된 스터디 모집 기간 연장
  @PatchMapping("/{studyPostId}/extend-canceled")
  public ResponseEntity<Void> extendCanceledStudy(
      @PathVariable Long studyPostId, @RequestParam LocalDate recruitmentPeriod) {
    studyPostService.extendCanceledStudy(studyPostId, recruitmentPeriod);
    return ResponseEntity.noContent().build();
  }
}
