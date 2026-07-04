package com.pivotseoul.domain.simulation.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * F-003 결과 조회가 threshold_result마다 추가 SELECT(N+1)를 내지 않고
 * JOIN 1회로 끝나는지 회귀 방지.
 */
@ExtendWith(MockitoExtension.class)
class ScenarioResultQueryRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ScenarioResultQueryRepository repository;

    @Test
    void findBundleByIdIssuesSingleJoinQuery() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(List.of());

        repository.findBundleById(42L);

        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(42L));
    }

    @Test
    void findBundlesByRunIdIssuesSingleJoinQuery() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                .thenReturn(List.of());

        repository.findBundlesByRunId(7L);

        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(7L));
    }
}
