package com.thundashop.services.zauiactivityservice;

import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.utils.ZauiStatusCodes;
import com.thundashop.repository.zauiactivity.ZauiActivityConfigRepository;
import com.thundashop.repository.zauiactivity.ZauiActivityRepository;
import com.thundashop.services.octoapiservice.OctoApiService;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class ZauiActivityService implements IZauiActivityService {
    private final ZauiActivityConfigRepository zauiActivityConfigRepository;
    private final ZauiActivityRepository zauiActivityRepository;
    private final OctoApiService octoApiService;
    private final Map<String, Map<String, PmsPricing>> storeWiseConfiguration = new HashMap<>();
    public ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo) {
        return zauiActivityConfigRepository.getZauiActivityConfig(sessionInfo).orElse(null);
    }
    public void importZauiActivities(Integer supplierId, SessionInfo sessionInfo) throws ZauiException {
        List<OctoProduct> octoProducts = null;
        try {
            octoProducts = octoApiService.getProducts(supplierId);
        } catch (Exception e) {
            throw new ZauiException(ZauiStatusCodes.OCTO_FAILED);
        }
        octoProducts.forEach(octoProduct ->
                zauiActivityRepository.save(mapOctoToZauiActivity(octoProduct), sessionInfo));
    }

    @Override
    public void setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo) {
        zauiActivityConfigRepository.save(zauiActivityConfig,sessionInfo);
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
