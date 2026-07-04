package com.example.refactor.feature.investor.service;

// 서비스 계층 — 입력 정규화·기본값 처리 후 저장소에 위임한다.
import com.example.refactor.common.NotFoundException;
import com.example.refactor.common.PageResult;
import com.example.refactor.feature.investor.dto.CreateInvestorRequest;
import com.example.refactor.feature.investor.model.Investor;
import com.example.refactor.feature.investor.model.InvestorListItem;
import com.example.refactor.feature.investor.repository.InvestorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestorService {
    private final InvestorRepository repository;

    public InvestorService(InvestorRepository repository) {
        this.repository = repository;
    }

    public List<Investor> list(String name) {
        return repository.findAll(name);
    }

    public PageResult<InvestorListItem> listPage(String name, int page, int size) {
        return repository.findPage(name, page, size);
    }

    public Investor detail(Long id) {
        // 조회 실패를 null로 흘려보내면 컨트롤러가 200과 함께 빈 본문을 내려줘 버그를 숨긴다.
        // 예외로 던져 GlobalExceptionHandler가 404로 변환하게 한다.
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("투자자를 찾을 수 없습니다. id=" + id));
    }

    public Investor create(CreateInvestorRequest request) {
        Investor investor = new Investor(
                null,
                safe(request.investorName()),
                safe(request.investorGrade()),
                request.totalAmount() == null ? 0L : request.totalAmount(),
                safe(request.lastProductName()),
                safe(request.screenMemo())
        );
        return repository.save(investor);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
