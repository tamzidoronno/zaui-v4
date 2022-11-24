package com.thundashop.services.gotoservice;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.LongTermDeal;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.gotohubrepository.IGotoConfigRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.pmspricing.IPmsPricingService;

@Service
public class GotoService implements IGotoService {
    @Autowired
    private IPmsPricingService pmsPricingService;

    @Autowired
    private IGotoConfigRepository gotoConfigRepository;

    @Override
    public GoToConfiguration getGotoConfiguration(SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        // BasicDBObject regex = new BasicDBObject();
        // regex.put("$regex", "/GotoConfiguration/i");
        String regex = "/GotoConfiguration/i";
        query.put("className", regex.substring(1, regex.length() - 1));
        System.out.println(query.toString());
        return gotoConfigRepository.getFirst(query, sessionInfo).orElse(new GoToConfiguration());
    }

    @Override
    public List<LongTermDeal> getLongTermDeals(SessionInfo sessionInfo) {
        PmsPricing pricing = pmsPricingService.getByCodeOrDefaultCode("", sessionInfo);
        return pricing.longTermDeal.keySet()
                .stream()
                .filter(Objects::nonNull)
                .map(numOfDays -> new LongTermDeal(numOfDays, pricing.longTermDeal.get(numOfDays)))
                .sorted(Comparator.comparingInt(LongTermDeal::getNumbOfDays))
                .collect(Collectors.toList());
    }

}
