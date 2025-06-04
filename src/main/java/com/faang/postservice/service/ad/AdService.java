package com.faang.postservice.service.ad;

import com.faang.postservice.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    @Async("removeExpiredAdsExecutorService")
    @Transactional
    public void removeExpiredAds() {
        log.info("Scheduled expired ads removing");
        List<Long> expiredAds = adRepository.findExpiredAds();
        adRepository.deleteAllById(expiredAds);
    }
}
