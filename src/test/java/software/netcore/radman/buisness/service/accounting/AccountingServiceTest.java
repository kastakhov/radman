package software.netcore.radman.buisness.service.accounting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.exception.NotFoundException;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.buisness.service.accounting.dto.AccountingFilter;
import software.netcore.radman.data.radius.entity.RadAcct;
import software.netcore.radman.data.radius.repo.RadAcctRepo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountingService
 */
@ExtendWith(MockitoExtension.class)
class AccountingServiceTest {

    @Mock
    private RadAcctRepo radAcctRepo;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private AccountingService accountingService;

    private RadAcct testRadAcct;
    private AccountingDto testAccountingDto;

    @BeforeEach
    void setUp() {
        testRadAcct = new RadAcct();
        testRadAcct.setRadAcctId(1L);
        testRadAcct.setUsername("testuser");
        testRadAcct.setNasIpAddress("192.168.1.1");
        testRadAcct.setAcctStartTime(new Date());

        testAccountingDto = new AccountingDto();
        testAccountingDto.setRadAcctId(1L);
        testAccountingDto.setUsername("testuser");
        testAccountingDto.setNasIpAddress("192.168.1.1");
    }

    @Test
    void pageAccountingRecords_ShouldReturnPagedResults() {
        // Arrange
        AccountingFilter filter = new AccountingFilter();
        Pageable pageable = PageRequest.of(0, 10);
        List<RadAcct> acctList = Arrays.asList(testRadAcct);
        Page<RadAcct> acctPage = new PageImpl<>(acctList, pageable, 1);

        when(radAcctRepo.findAll(any(), eq(pageable))).thenReturn(acctPage);
        when(conversionService.convert(testRadAcct, AccountingDto.class)).thenReturn(testAccountingDto);

        // Act
        Page<AccountingDto> result = accountingService.pageAccountingRecords(filter, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
        verify(radAcctRepo).findAll(any(), eq(pageable));
    }

    @Test
    void countAccountingRecords_ShouldReturnCount() {
        // Arrange
        AccountingFilter filter = new AccountingFilter();
        when(radAcctRepo.count(any())).thenReturn(10L);

        // Act
        long count = accountingService.countAccountingRecords(filter);

        // Assert
        assertThat(count).isEqualTo(10L);
        verify(radAcctRepo).count(any());
    }
}
