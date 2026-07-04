package com.allochub.domain.distribution;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DistributionRepository extends JpaRepository<Distribution, String> {

    // list() 화면은 분배 건마다 investment를 참조하는데, 지연 로딩 그대로면 건당 추가
    // 쿼리가 나가는 N+1이 된다. fetch join으로 한 번에 가져온다.
    @Query("SELECT DISTINCT d FROM Distribution d LEFT JOIN FETCH d.investment")
    List<Distribution> findAllWithInvestment();
}
