package com.example.sourcebase.service.impl;

import com.example.sourcebase.domain.Criteria;
import com.example.sourcebase.domain.DepartmentCriterias;
import com.example.sourcebase.domain.Question;
import com.example.sourcebase.domain.dto.reqdto.CriteriaReqDTO;
import com.example.sourcebase.domain.dto.resdto.AnswerResDTO;
import com.example.sourcebase.domain.dto.resdto.CriteriaResDTO;
import com.example.sourcebase.domain.dto.resdto.QuestionResDTO;
import com.example.sourcebase.exception.AppException;
import com.example.sourcebase.mapper.CriteriaMapper;
import com.example.sourcebase.mapper.QuestionMapper;
import com.example.sourcebase.repository.ICriteriaRepository;
import com.example.sourcebase.repository.IDepartmentCriteriasRepository;
import com.example.sourcebase.repository.IQuestionRepository;
import com.example.sourcebase.service.ICriteriaService;
import com.example.sourcebase.util.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CriteriaServiceImpl implements ICriteriaService {

    ICriteriaRepository criteriaRepository;
    IQuestionRepository questionRepository;
    IDepartmentCriteriasRepository departmentCriteriasRepository;
    CriteriaMapper criteriaMapper = CriteriaMapper.INSTANCE;
    QuestionMapper questionMapper = QuestionMapper.INSTANCE;


    @Override
    public List<CriteriaResDTO> getAllCriterias() {
        List<Criteria> listCriteria = criteriaRepository.findAll();
        List<CriteriaResDTO> criteriaResDTOs = listCriteria.stream()
                .map(criteria -> {
                    CriteriaResDTO criteriaResDTO = criteriaMapper.toCriteriaResDTO(criteria);

                    // Mapping questions nếu có câu hỏi
                    if (criteria.getQuestions() != null && !criteria.getQuestions().isEmpty()) {
                        List<QuestionResDTO> questionResDTOs = criteria.getQuestions().stream()
                                .filter(question -> !question.isDeleted())
                                .map(question -> {
                                    QuestionResDTO questionResDTO = questionMapper.toQuestionResDTO(question);

                                    // Mapping answers nếu có câu trả lời
                                    if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
                                        List<AnswerResDTO> answerResDTOs = question.getAnswers().stream()
                                                .map(criteriaMapper::toAnswerResDTO)
                                                .collect(Collectors.toList());
                                        questionResDTO.setAnswers(answerResDTOs);
                                    } else {
                                        questionResDTO.setAnswers(null); // Không có câu trả lời
                                    }

                                    return questionResDTO;
                                })
                                .collect(Collectors.toList());
                        criteriaResDTO.setQuestions(questionResDTOs);
                    } else {
                        criteriaResDTO.setQuestions(null); // Không có câu hỏi
                    }

                    return criteriaResDTO;
                })
                .collect(Collectors.toList());
        return criteriaResDTOs;
    }

    @Override
    public Page<CriteriaResDTO> getAllCriteria(int page, int size, String sortBy, boolean asc) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return criteriaRepository.findAll(pageable).map(criteriaMapper::toCriteriaResDTO);
    }

    @Override
    public void validateUniqueTitle(CriteriaReqDTO criteriaReqDTO) {
        if (criteriaRepository.existsByTitle(criteriaReqDTO.getTitle())) {
            throw new IllegalArgumentException("Tiêu đề đã tồn tại!");
        }
    }

    @Override
    public CriteriaResDTO getCriteriaById(Long id, Long departmentId) {
        Criteria criteria = criteriaRepository.findById(id, departmentId)
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_NOT_FOUND));

        CriteriaResDTO criteriaResDTO = criteriaMapper.toCriteriaResDTO(criteria);

        if (criteria.getDepartmentCriterias() != null && !criteria.getDepartmentCriterias().isEmpty()) {
            List<QuestionResDTO> questionResDTOs = criteria.getDepartmentCriterias().stream()
                    .filter(dc -> dc.getDepartment().getId().equals(departmentId)) // Lọc theo departmentId
                    .map(dc -> {
                        Question question = dc.getQuestion();
                        if (question != null && !question.isDeleted()) {
                            QuestionResDTO questionResDTO = questionMapper.toQuestionResDTO(question);

                            List<AnswerResDTO> answerResDTOs = question.getAnswers() != null
                                    ? question.getAnswers().stream()
                                    .map(criteriaMapper::toAnswerResDTO)
                                    .collect(Collectors.toList())
                                    : null;
                            questionResDTO.setAnswers(answerResDTOs);

                            return questionResDTO;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            criteriaResDTO.setQuestions(questionResDTOs);
        } else {
            criteriaResDTO.setQuestions(null);
        }

        return criteriaResDTO;
    }

    @Override
    @Transactional
    public CriteriaResDTO addCriterion(CriteriaReqDTO criteriaReqDTO) {
        Criteria newCriteria = criteriaMapper.toEntity(criteriaReqDTO);
        if (criteriaRepository.existsByTitleIgnoreCase(newCriteria.getTitle())) {
            throw new AppException(ErrorCode.CRITERIA_EXISTED);
        }

        return criteriaMapper.toCriteriaResDTO(criteriaRepository.save(newCriteria));
    }

    @Override
    @Transactional
    public CriteriaResDTO updateCriterion(Long id, CriteriaReqDTO criteriaReqDTO) {
        return criteriaRepository.findById(id)
                .map(criteria -> {
                    if (criteriaReqDTO.getTitle() != null
                            && !criteriaReqDTO.getTitle().equalsIgnoreCase(criteria.getTitle())
                            && criteriaRepository.existsByTitleIgnoreCase(criteriaReqDTO.getTitle())
                    ) {
                        throw new AppException(ErrorCode.CRITERIA_EXISTED);
                    }
                    criteria = criteriaMapper.partialUpdate(criteriaReqDTO, criteria);
                    if (criteriaReqDTO.getTitle() != null
                            && !criteriaReqDTO.getTitle().equals(criteria.getTitle())
                            && criteriaRepository.existsByTitleIgnoreCase(criteriaReqDTO.getTitle())
                    ) {
                        throw new AppException(ErrorCode.CRITERIA_EXISTED);
                    }
                    return criteriaMapper.toCriteriaResDTO(criteriaRepository.save(criteria));
                })
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_NOT_FOUND));
    }

    @Override
    @Transactional
    public void deleteCriterion(Long id) {
        Criteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_NOT_FOUND));

        if (criteria.getQuestions() != null) {
            criteria.getQuestions().forEach(question -> {
                question.setDeleted(true);
                if (question.getAnswers() != null) {
                    question.getAnswers().forEach(answer -> answer.setDeleted(true));
                }
            });
        }
        criteria.setDeleted(true);
        criteriaRepository.save(criteria);
    }

    @Override
    @Transactional
    public void deleteCriterionByCriteriaIdAndDepartmentId(Long criteriaId, Long departmentId) {
        List<DepartmentCriterias> dcs = departmentCriteriasRepository.findByCriteria_IdAndDepartment_Id(criteriaId, departmentId);
        System.out.println(criteriaId + " " + departmentId);
        if (dcs != null && !dcs.isEmpty()) {
            dcs.forEach(dc -> {
                dc.getCriteria().getQuestions().forEach(question -> {
                    question.setDeleted(true);
                    question.getAnswers().forEach(answer -> answer.setDeleted(true));
                });
                dc.getCriteria().setDeleted(true);
            });
            departmentCriteriasRepository.deleteAll(dcs);
        }
    }

    @Override
    public Page<QuestionResDTO> findQuestionsByCriterionId(Long criteriaId, int page, int size, String sortBy, boolean asc) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return questionRepository.findAllByCriteria_Id(criteriaId, pageable).map(questionMapper::toQuestionResDTO);
    }

}
