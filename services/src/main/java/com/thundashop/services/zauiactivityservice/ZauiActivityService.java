package com.thundashop.services.zauiactivityservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.utils.ZauiStatusCodes;
import com.thundashop.repository.zauiactivityrepository.ZauiActivityConfigRepository;
import com.thundashop.repository.zauiactivityrepository.ZauiActivityRepository;
import com.thundashop.services.octoapiservice.OctoApiService;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class ZauiActivityService implements IZauiActivityService {
    private ZauiActivityConfigRepository zauiActivityConfigRepository;
    private ZauiActivityRepository zauiActivityRepository;
    private OctoApiService octoApiService;

    public ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo) {
        try{
            return zauiActivityConfigRepository.getZauiActivityConfig(sessionInfo).orElse(new ZauiActivityConfig());
        } catch(Exception ex){
            log.error("Failed to get zaui activity config. Reason: {}, Actual Exception: {}", ex.getMessage(), ex);
            return null;
        }
    }

    public ZauiActivityConfig setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo) {
        return zauiActivityConfigRepository.save(zauiActivityConfig, sessionInfo);
    }

    public void fetchZauiActivities(Integer supplierId, SessionInfo sessionInfo) throws ZauiException {
        List<OctoProduct> octoProducts = null;
        try {
            octoProducts = octoApiService.getOctoProducts(supplierId);
        } catch (Exception e) {
            throw new ZauiException(ZauiStatusCodes.OCTO_FAILED);
        }
        octoProducts
                .forEach(octoProduct -> zauiActivityRepository.save(mapOctoToZauiActivity(octoProduct), sessionInfo));
    }   

    private ZauiActivity mapOctoToZauiActivity(OctoProduct octoProduct) {
        ZauiActivity zauiActivity = new ZauiActivity();
        zauiActivity.name = octoProduct.getTitle();
        zauiActivity.shortDescription = octoProduct.getShortDescription();
        zauiActivity.description = octoProduct.getPrimaryDescription();
        zauiActivity.activityOptionList = octoProduct.getOptions();
        zauiActivity.mainImage = octoProduct.getCoverImage();
        zauiActivity.tag = "addon";
        return zauiActivity;
    }
}
