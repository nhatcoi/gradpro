package com.university.gradpro.evaluation.service;

import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.DuplicateResourceException;
import com.university.gradpro.common.exception.ForbiddenException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.council.entity.Council;
import com.university.gradpro.council.repository.CouncilMemberRepository;
import com.university.gradpro.council.repository.CouncilRepository;
import com.university.gradpro.evaluation.dto.*;
import com.university.gradpro.evaluation.entity.Evaluation;
import com.university.gradpro.evaluation.entity.FinalScore;
import com.university.gradpro.evaluation.repository.EvaluationRepository;
import com.university.gradpro.evaluation.repository.FinalScoreRepository;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.topic.repository.TopicRepository;
import com.university.gradpro.user.entity.User;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    
    private final EvaluationRepository evaluationRepository;
    private final FinalScoreRepository finalScoreRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CouncilRepository councilRepository;
    private final CouncilMemberRepository councilMemberRepository;
    
    /**
     * GVHD chấm điểm hướng dẫn
     */
    @Transactional
    public EvaluationDto submitSupervisorEvaluation(Long supervisorId, CreateEvaluationRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", request.getTopicId()));
        
        // Kiểm tra quyền
        if (topic.getSupervisor() == null || !topic.getSupervisor().getId().equals(supervisorId)) {
            throw new ForbiddenException("Bạn không phải GVHD của đề tài này");
        }
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", request.getStudentId()));
        
        User evaluator = userRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Người chấm", "id", supervisorId));
        
        // Kiểm tra đã chấm chưa
        if (evaluationRepository.findSupervisorEvaluation(topic.getId(), student.getId()).isPresent()) {
            throw new DuplicateResourceException("Bạn đã chấm điểm cho sinh viên này rồi");
        }
        
        Evaluation evaluation = Evaluation.builder()
                .topic(topic)
                .student(student)
                .evaluator(evaluator)
                .evaluationType("SUPERVISOR")
                .score(request.getScore())
                .comment(request.getComment())
                .strengths(request.getStrengths())
                .weaknesses(request.getWeaknesses())
                .build();
        
        evaluation = evaluationRepository.save(evaluation);
        
        // Cập nhật điểm tổng kết nếu có đủ điểm
        updateFinalScore(topic.getId(), student.getId());
        
        return toDto(evaluation);
    }
    
    /**
     * UC-4.7: Thành viên hội đồng chấm điểm
     */
    @Transactional
    public EvaluationDto submitCouncilEvaluation(Long lecturerId, CreateEvaluationRequest request) {
        if (request.getCouncilId() == null) {
            throw new BadRequestException("Vui lòng chọn hội đồng");
        }
        
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", request.getTopicId()));
        
        Council council = councilRepository.findById(request.getCouncilId())
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", request.getCouncilId()));
        
        // Kiểm tra quyền - phải là thành viên hội đồng
        if (!councilMemberRepository.existsByCouncilIdAndLecturerId(council.getId(), lecturerId)) {
            throw new ForbiddenException("Bạn không phải thành viên của hội đồng này");
        }
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", request.getStudentId()));
        
        User evaluator = userRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Người chấm", "id", lecturerId));
        
        // Kiểm tra đã chấm chưa
        if (evaluationRepository.findByTopicIdAndStudentIdAndEvaluatorId(
                topic.getId(), student.getId(), lecturerId).isPresent()) {
            throw new DuplicateResourceException("Bạn đã chấm điểm cho sinh viên này rồi");
        }
        
        Evaluation evaluation = Evaluation.builder()
                .topic(topic)
                .student(student)
                .evaluator(evaluator)
                .council(council)
                .evaluationType("COUNCIL")
                .score(request.getScore())
                .comment(request.getComment())
                .strengths(request.getStrengths())
                .weaknesses(request.getWeaknesses())
                .build();
        
        evaluation = evaluationRepository.save(evaluation);
        
        // Cập nhật điểm tổng kết
        updateFinalScore(topic.getId(), student.getId());
        
        return toDto(evaluation);
    }
    
    /**
     * Tính và cập nhật điểm tổng kết
     */
    private void updateFinalScore(Long topicId, Long studentId) {
        Topic topic = topicRepository.findById(topicId).orElse(null);
        User student = userRepository.findById(studentId).orElse(null);
        
        if (topic == null || student == null) return;
        
        // Lấy điểm GVHD
        var supervisorEval = evaluationRepository.findSupervisorEvaluation(topicId, studentId);
        if (supervisorEval.isEmpty()) return;
        
        Double supervisorScore = supervisorEval.get().getScore();
        
        // Lấy điểm trung bình hội đồng
        Double councilAvg = evaluationRepository.calculateAverageCouncilScore(topicId, studentId);
        if (councilAvg == null) return;
        
        // Tính điểm tổng kết: GVHD 40%, HĐ 60%
        double finalScore = supervisorScore * 0.4 + councilAvg * 0.6;
        
        // Xác định letter grade
        String letterGrade = calculateLetterGrade(finalScore);
        boolean passed = finalScore >= 5.0;
        
        FinalScore fs = finalScoreRepository.findByTopicIdAndStudentId(topicId, studentId)
                .orElse(FinalScore.builder()
                        .topic(topic)
                        .student(student)
                        .build());
        
        fs.setSupervisorScore(supervisorScore);
        fs.setCouncilScore(councilAvg);
        fs.setFinalScore(Math.round(finalScore * 100.0) / 100.0);
        fs.setLetterGrade(letterGrade);
        fs.setPassed(passed);
        
        finalScoreRepository.save(fs);
    }
    
    private String calculateLetterGrade(double score) {
        if (score >= 9.0) return "A+";
        if (score >= 8.5) return "A";
        if (score >= 8.0) return "B+";
        if (score >= 7.0) return "B";
        if (score >= 6.5) return "C+";
        if (score >= 5.5) return "C";
        if (score >= 5.0) return "D+";
        if (score >= 4.0) return "D";
        return "F";
    }
    
    /**
     * UC-5.7: Sinh viên xem điểm
     */
    public List<EvaluationDto> getEvaluationsByStudent(Long studentId) {
        return evaluationRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy điểm tổng kết của sinh viên
     */
    public List<FinalScoreDto> getFinalScoresByStudent(Long studentId) {
        return finalScoreRepository.findByStudentId(studentId)
                .stream()
                .map(this::toFinalScoreDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy các đánh giá của đề tài
     */
    public List<EvaluationDto> getEvaluationsByTopic(Long topicId, Long studentId) {
        return evaluationRepository.findByTopicIdAndStudentId(topicId, studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy điểm tổng kết theo học kỳ
     */
    public List<FinalScoreDto> getFinalScoresBySemester(String semester) {
        return finalScoreRepository.findBySemester(semester)
                .stream()
                .map(this::toFinalScoreDto)
                .collect(Collectors.toList());
    }
    
    private EvaluationDto toDto(Evaluation eval) {
        EvaluationDto.EvaluationDtoBuilder builder = EvaluationDto.builder()
                .id(eval.getId())
                .evaluationType(eval.getEvaluationType())
                .score(eval.getScore())
                .comment(eval.getComment())
                .strengths(eval.getStrengths())
                .weaknesses(eval.getWeaknesses())
                .createdAt(eval.getCreatedAt())
                .updatedAt(eval.getUpdatedAt());
        
        builder.topic(EvaluationDto.TopicInfo.builder()
                .id(eval.getTopic().getId())
                .code(eval.getTopic().getCode())
                .title(eval.getTopic().getTitle())
                .build());
        
        builder.student(EvaluationDto.StudentInfo.builder()
                .id(eval.getStudent().getId())
                .code(eval.getStudent().getCode())
                .fullName(eval.getStudent().getFullName())
                .build());
        
        builder.evaluator(EvaluationDto.EvaluatorInfo.builder()
                .id(eval.getEvaluator().getId())
                .code(eval.getEvaluator().getCode())
                .fullName(eval.getEvaluator().getFullName())
                .academicTitle(eval.getEvaluator().getAcademicTitle())
                .build());
        
        if (eval.getCouncil() != null) {
            builder.council(EvaluationDto.CouncilInfo.builder()
                    .id(eval.getCouncil().getId())
                    .code(eval.getCouncil().getCode())
                    .name(eval.getCouncil().getName())
                    .build());
        }
        
        return builder.build();
    }
    
    private FinalScoreDto toFinalScoreDto(FinalScore fs) {
        String supervisorName = fs.getTopic().getSupervisor() != null 
                ? fs.getTopic().getSupervisor().getFullName() : "";
        
        return FinalScoreDto.builder()
                .id(fs.getId())
                .supervisorScore(fs.getSupervisorScore())
                .councilScore(fs.getCouncilScore())
                .finalScore(fs.getFinalScore())
                .letterGrade(fs.getLetterGrade())
                .passed(fs.getPassed())
                .notes(fs.getNotes())
                .createdAt(fs.getCreatedAt())
                .updatedAt(fs.getUpdatedAt())
                .topic(FinalScoreDto.TopicInfo.builder()
                        .id(fs.getTopic().getId())
                        .code(fs.getTopic().getCode())
                        .title(fs.getTopic().getTitle())
                        .supervisorName(supervisorName)
                        .build())
                .student(FinalScoreDto.StudentInfo.builder()
                        .id(fs.getStudent().getId())
                        .code(fs.getStudent().getCode())
                        .fullName(fs.getStudent().getFullName())
                        .major(fs.getStudent().getMajor())
                        .build())
                .build();
    }
}
