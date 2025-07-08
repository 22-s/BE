package dgu.sw.domain.admin.converter;

import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminVocaRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminVocaResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminMannerRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminRequest.AdminQuizRequest;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminMannerResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminQuizResponse;
import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;
import dgu.sw.domain.manner.entity.Manner;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.voca.entity.Voca;

public class AdminConverter {
    public static AdminUserResponse toAdminUserResponse(User user) {
        return AdminUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static AdminMannerResponse toAdminMannerResponse(Manner manner) {
        return AdminMannerResponse.builder()
                .mannerId(manner.getMannerId())
                .category(manner.getCategory())
                .title(manner.getTitle())
                .imageUrl(manner.getImageUrl())
                .content(manner.getContent())
                .build();
    }

    public static Manner toManner(AdminMannerRequest request) {
        return Manner.builder()
                .category(request.getCategory())
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .content(request.getContent())
                .build();
    }

    public static AdminQuizResponse toAdminQuizResponse(Quiz quiz) {
        return AdminQuizResponse.builder()
                .quizId(quiz.getQuizId())
                .category(quiz.getCategory())
                .question(quiz.getQuestion())
                .answer(quiz.getAnswer())
                .description(quiz.getDescription())
                .questionDetail(quiz.getQuestionDetail())
                .build();
    }

    public static Quiz toQuiz(AdminQuizRequest request) {
        return Quiz.builder()
                .category(request.getCategory())
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .description(request.getDescription())
                .questionDetail(request.getQuestionDetail())
                .build();
    }

    public static AdminVocaResponse toAdminVocaResponse(Voca voca) {
        return AdminVocaResponse.builder()
                .vocaId(voca.getVocaId())
                .category(voca.getCategory())
                .term(voca.getTerm())
                .description(voca.getDescription())
                .example(voca.getExample())
                .build();
    }

    public static Voca toVoca(AdminVocaRequest request) {
        return Voca.builder()
                .category(request.getCategory())
                .term(request.getTerm())
                .description(request.getDescription())
                .example(request.getExample())
                .build();
    }
}
